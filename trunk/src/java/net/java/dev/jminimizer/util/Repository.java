package net.java.dev.jminimizer.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 15, 2004
 *  
 */
public class Repository implements org.apache.bcel.util.Repository {
	private static final Log log = LogFactory.getLog(Repository.class);
	private Map programClasses;
	private Map runtimeClasses;
	private URL[] pc;
	private URLClassLoader rc;
	/**
	 * @param parent
	 */
	public Repository(URL[] program, URL[] runtime) {
		this.rc = new URLClassLoader(runtime);
		this.pc = program;
		programClasses = new HashMap();
		runtimeClasses = new HashMap();
	}
	/**
	 * @see org.apache.bcel.util.Repository#storeClass(org.apache.bcel.classfile.JavaClass)
	 */
	public void storeClass(JavaClass clazz) {
//		programClasses.put(clazz.getClassName(), clazz);
	}
	/**
	 * @see org.apache.bcel.util.Repository#removeClass(org.apache.bcel.classfile.JavaClass)
	 */
	public void removeClass(JavaClass clazz) {
		programClasses.remove(clazz.getClassName());
		runtimeClasses.remove(clazz.getClassName());
	}
	/**
	 * @see org.apache.bcel.util.Repository#findClass(java.lang.String)
	 */
	public JavaClass findClass(String className) {
		return (JavaClass) programClasses.get(className);
	}
	/**
	 * @see org.apache.bcel.util.Repository#loadClass(java.lang.String)
	 */
	public JavaClass loadClass(String className) throws ClassNotFoundException {
		JavaClass jc = (JavaClass) programClasses.get(className);
		if (jc == null) {
			String path = className.replace('.', File.separatorChar).concat(
					".class");
			for (int i = 0; i < pc.length; i++) {
				URL url = null;
				try {
					url = new URL(pc[i], path);
				} catch (MalformedURLException e) {
					log.debug("Error on creating the URL", e);
					continue;
				}
				byte[] data;
				try {
					ClassParser parser = new ClassParser(url.openStream(),
							className);
					jc = parser.parse();
					programClasses.put(className, jc);
				} catch (IOException e) {
					log.debug("Error on reading the URL", e);
					continue;
				}
			}
			//runtime classes
			if (jc == null) {
				InputStream in= rc.getResourceAsStream(className.replace('.', File.separatorChar).concat(".class"));
				try {
					ClassParser parser = new ClassParser(in, className);
					jc = parser.parse();
					runtimeClasses.put(className, jc);
				} catch (IOException e) {
					log.debug("Error on reading the URL", e);
				}
			}
		}
		return jc;
	}
	/**
	 * @see org.apache.bcel.util.Repository#loadClass(java.lang.Class)
	 */
	public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
		return this.loadClass(clazz.getName());
	}
	/**
	 * @see org.apache.bcel.util.Repository#clear()
	 */
	public void clear() {
		programClasses.clear();
	}
	/**
	 * @see org.apache.bcel.util.Repository#getClassPath()
	 */
	public ClassPath getClassPath() {
		return null;
	}
	/**
	 *  
	 */
	private List findClassFromDirectory(File directory) {
		List list = new LinkedList();
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					list.addAll(this.findClassFromDirectory(files[i]));
				} else {
					if (files[i].getName().endsWith(".class")) {
						list.add(files[i]);
					}
				}
			}
		}
		return list;
	}
	/**
	 *  
	 */
	private List findClassFromJar(URL jar) throws IOException {
		List list = new LinkedList();
		JarURLConnection con = (JarURLConnection) jar.openConnection();
		JarFile file = con.getJarFile();
		Enumeration e = file.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = (JarEntry) e.nextElement();
			if (entry.getName().endsWith(".class")) {
				list.add(entry);
			}
		}
		return list;
	}
	/**
	 *  
	 */
	private void loadClassFromDirectory(File directory) {
		List classes = this.findClassFromDirectory(directory);
		Iterator i = classes.iterator();
		int start = directory.getAbsolutePath().length();
		while (i.hasNext()) {
			File file = (File) i.next();
			//extract the directory
			String clazz = file.getAbsolutePath().substring(start + 1);
			clazz = this.normalizeClass(clazz);
			try {
				programClasses.put(clazz, new ClassParser(new FileInputStream(
						file), clazz).parse());
			} catch (IOException e) {
				//never here
			}
		}
	}
	public Set getProgramClasses() {
		for (int i = 0; i < pc.length; i++) {
			String file = pc[i].getFile();
			if (!file.endsWith(File.separator)) {
				throw new IllegalArgumentException(pc[i]
						+ " must point to classpath and end with "
						+ File.separator);
			}
			String protocol = pc[i].getProtocol();
			if (protocol.equals("file")) {
				loadClassFromDirectory(new File(file));
			} else if (protocol.equals("jar")) {
				loadClassFromJar(pc[i]);
			}
		}
		return programClasses.keySet();
	}
	/**
	 *  
	 */
	private String normalizeClass(String clazz) {
		//replace file separator per dot
		clazz = clazz.replace(File.separatorChar, '.');
		//remove from end the termination ".class"
		return clazz.substring(0, clazz.length() - 6);
	}
	/**
	 *  
	 */
	private void loadClassFromJar(URL jar) {
		try {
			List list = this.findClassFromJar(jar);
			Iterator i = list.iterator();
			while (i.hasNext()) {
				JarEntry entry = (JarEntry) i.next();
				String clazz = this.normalizeClass(entry.getName());
				programClasses.put(clazz, new ClassParser(new URL(jar, entry.getName()).openStream(), clazz).parse());
			}
		} catch (IOException e) {
			//never here
		}
	}

}