package net.java.dev.jminimizer.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 14, 2004
 *  
 */
public class ClassLoader extends java.lang.ClassLoader {
	private static final Log log = LogFactory.getLog(ClassLoader.class);
	private Map programClasses;
	private URL[] pc;
	private URLClassLoader rc;
	/**
	 * @param parent
	 */
	public ClassLoader(URL[] program, URL[] runtime) {
		super(null);
		this.rc = new URLClassLoader(runtime);
		this.pc = program;
		programClasses = new HashMap();
		this.init();
	}
	/**
	 *  
	 */
	private Class defineClass(URL url, String className) throws IOException {
		Class clazz= (Class)programClasses.get(className);
		if(clazz != null) {
		byte[] data = this.readClass(url);
		clazz = this.defineClass(className, data, 0, data.length);
		}
		return clazz;
	}
	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");
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
				Class clazz = this.defineClass(url, name);
				programClasses.put(name, clazz);
				return clazz;
			} catch (IOException e) {
				log.debug("Error on reading the URL", e);
				continue;
			}
		}
		//the runtime classloader will load the classes (java.lang.Object,
		// java.lang.String, ...)
		return rc.loadClass(name);
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
	private void init() {
		for (int i = 0; i < pc.length; i++) {
			final String file = pc[i].getFile();
			if (!file.endsWith(File.separator)) {
				throw new IllegalArgumentException(pc[i]
						+ " must point to classpath and end with "
						+ File.separator);
			}
			String protocol = pc[i].getProtocol();
			if (protocol.equals("file")) {
				loadClassFromDirectory(new File(file));
			} else if (protocol.equals("jar")) {
				final int x = i;
				loadClassFromJar(pc[x]);
			}
		}
	}
	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public synchronized Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		//try to find in the collection of loaded classes
		Class clazz = (Class) programClasses.get(name);
		if (clazz == null) {
			clazz= this.findClass(name);
		}
		if (resolve) {
		    resolveClass(clazz);
		}
		return clazz;
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
				programClasses
						.put(clazz, this.defineClass(file.toURL(), clazz));
			} catch (IOException e) {
				//never here
			}
		}
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
				programClasses.put(clazz, this.defineClass(new URL(jar, entry
						.getName()), clazz));
			}
		} catch (IOException e) {
			//never here
		}
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
	private byte[] readClass(URL url) throws IOException {
		log.debug("Reading url: " + url.toString());
		InputStream in = url.openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int x;
		while ((x = in.read(data)) != -1) {
			out.write(data, 0, x);
		}
		return out.toByteArray();
	}
	/**
	 * @return Returns the programClasses.
	 */
	public Map getProgramClasses() {
		return this.programClasses;
	}
}