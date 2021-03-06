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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Le�o Moreira
 * @since Apr 15, 2004
 *  
 */
public class URLRepository implements Repository {
	private static final Log log = LogFactory.getLog(Repository.class);
	private URL[] pc;
	private Map programClasses;
	private URLClassLoader rc;
	private Map runtimeClasses;
	private Set programResources;
	/**
	 * @param parent
	 */
	public URLRepository(URL[] program, URL[] runtime) {
		this.rc = new URLClassLoader(runtime);
		this.pc = program;
		programClasses = new HashMap();
		programResources= new HashSet();
		runtimeClasses = new HashMap();
	}
	/**
	 * @see org.apache.bcel.util.Repository#clear()
	 */
	public void clear() {
		programClasses.clear();
		programResources.clear();
		runtimeClasses.clear();
	}
	/**
	 * @see org.apache.bcel.util.Repository#findClass(java.lang.String)
	 */
	public JavaClass findClass(String className) {
		JavaClass jc = (JavaClass) programClasses.get(className);
		if (jc == null) {
			jc = (JavaClass) runtimeClasses.get(className);
		}
		return jc;
	}
	/**
	 *  
	 */
	private List findClassFromDirectory(File directory) throws MalformedURLException {
		List list = new LinkedList();
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					list.addAll(this.findClassFromDirectory(files[i]));
				} else {
					if (files[i].getName().endsWith(".class")) {
					    //this is used to get classes that is compiled to be a STUB
					    if (files[i].getName().endsWith("_Stub.class")) {
					        //this is a resource and don't have to be processed
					        log.warn("Stubs: "+files[i]);
					        programResources.add(files[i].toURL());
		                } else {
							list.add(files[i]);
		                }
					} else {
				        programResources.add(files[i].toURL());
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
			    //this is used to get classes that is compiled to be a STUB
			    if (entry.getName().endsWith("_Stub.class")) {
			        //this is a resource and don't have to be processed
			        log.warn("Stubs: "+entry);
			        programResources.add(new URL(jar, entry.getName()));
                } else {
    				list.add(entry);
                }
			} else {
			    if (!entry.isDirectory()) {
			        programResources.add(new URL(jar, entry.getName()));
			    }
			}
		}
		return list;
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
	public Set getProgramClasses() {
		for (int i = 0; i < pc.length; i++) {
			String file = pc[i].getFile();
			String protocol = pc[i].getProtocol();
			if (protocol.equals("file")) {
				loadClassFromDirectory(new File(file));
			} else if (protocol.equals("jar")) {
				if (!file.endsWith("!/")) {
					throw new IllegalArgumentException(pc[i]
							+ " must point to classpath and end with !/");
				}
				loadClassFromJar(pc[i]);
			}
		}
		Set classes = new TreeSet(String.CASE_INSENSITIVE_ORDER);
		classes.addAll(programClasses.keySet());
		return classes;
	}
	/**
	 * @see org.apache.bcel.util.Repository#loadClass(java.lang.Class)
	 */
	public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
		return this.loadClass(clazz.getName());
	}
	/**
	 * @see org.apache.bcel.util.Repository#loadClass(java.lang.String)
	 */
	public JavaClass loadClass(String className) throws ClassNotFoundException {
		JavaClass jc = (JavaClass) this.findClass(className);
		if (jc == null) {
			jc = this.loadProgramClass(className);
			if (jc == null) {
				jc = this.loadRuntimeClass(className);
			}
		}
		return jc;
	}
	/**
     * 
     * @param directory
     */
    private void loadClassFromDirectory(File directory) {
        try {
            List classes = this.findClassFromDirectory(directory);
            Iterator i = classes.iterator();
            int start = directory.getAbsolutePath().length();
            while (i.hasNext()) {
                File file = (File) i.next();
                //extract the directory
                String clazz = file.getAbsolutePath().substring(start + 1);
                clazz = this.normalizeClass(clazz);
                if (!programClasses.containsKey(clazz)) {
                    programClasses.put(clazz, this.parseClass(
                            new FileInputStream(file), clazz));
                }
            }
        } catch (IOException e) {
            log.error("Never should be here!!!", e);
        }
    }
	/**
	 * 
	 * @param jar
	 */
	private void loadClassFromJar(URL jar) {
		try {
			List list = this.findClassFromJar(jar);
			Iterator i = list.iterator();
			while (i.hasNext()) {
				JarEntry entry = (JarEntry) i.next();
				String clazz = this.normalizeClass(entry.getName());
				if (!programClasses.containsKey(clazz)) {
					programClasses.put(clazz, this.parseClass(new URL(jar,
							entry.getName()).openStream(), clazz));
				}
			}
		} catch (IOException e) {
            log.error("Never should be here!!!", e);
		}
	}
	/**
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	private JavaClass loadProgramClass(String className)
			throws ClassNotFoundException {
		//program classes
		JavaClass jc = null;
		for (int i = 0; i < pc.length; i++) {
			URL url = null;
			String path = className;
			try {
			    if (pc[i].getProtocol().equals("jar")) {
                    path= path.replace('.', '/').concat(".class");
                } else {
                    path= path.replace('.', File.separatorChar).concat(".class");
                }
				url = new URL(pc[i], path);
			} catch (MalformedURLException e) {
				log.debug("Error on creating the URL", e);
				continue;
			}
			byte[] data;
			try {
				jc = this.parseClass(url.openStream(), className);
				programClasses.put(className, jc);
				break;
			} catch (IOException e) {
				log.debug("Error on reading the URL", e);
				continue;
			}
		}
		return jc;
	}
	/**
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	private JavaClass loadRuntimeClass(String className)
			throws ClassNotFoundException {
		//program classes
		JavaClass jc = null;
		//URL url= rc.getResource(className.replace('.', '/').concat(".class"));
		InputStream in = rc.getResourceAsStream(className.replace('.','/').concat(".class"));
		if (in == null) {
			throw new ClassNotFoundException(className);
		}
		try {
			jc = this.parseClass(in, className);
			runtimeClasses.put(className, jc);
		} catch (IOException e) {
			log.debug("Error on reading the URL", e);
		}
		return jc;
	}
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	private String normalizeClass(String clazz) {
		//replace file separator per dot
		clazz = clazz.replace(File.separatorChar, '.');
		//remove from end the termination ".class"
		return clazz.substring(0, clazz.length() - 6);
	}
	/**
	 * 
	 * @param in
	 * @param className
	 * @return
	 * @throws IOException
	 */
	private JavaClass parseClass(InputStream in, String className)
			throws IOException {
		ClassParser parser = new ClassParser(in, className);
		JavaClass jc = parser.parse();
		jc.setRepository(this);
		return jc;
	}
	/**
	 * @see org.apache.bcel.util.Repository#removeClass(org.apache.bcel.classfile.JavaClass)
	 */
	public void removeClass(JavaClass clazz) {
		programClasses.remove(clazz.getClassName());
		runtimeClasses.remove(clazz.getClassName());
	}
	/**
	 * @see org.apache.bcel.util.Repository#storeClass(org.apache.bcel.classfile.JavaClass)
	 */
	public void storeClass(JavaClass clazz) {
		throw new RuntimeException("No sense in this class!!");
	}
    /**
     * @see net.java.dev.jminimizer.util.Repository#getProgramResources()
     */
    public Set getProgramResources() {
        return programResources;
    }
}