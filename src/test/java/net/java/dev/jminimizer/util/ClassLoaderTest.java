package net.java.dev.jminimizer.util;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import net.java.dev.jminimizer.AllTests;
import net.java.dev.jminimizer.util.ClassLoader;
import junit.framework.TestCase;
/**
 * @author Thiago Leão Moreira
 * @since Apr 14, 2004
 *  
 */
public class ClassLoaderTest extends TestCase {
	public static final String LIB_TEST_PATH = AllTests.TEST_PATH + "lib"
			+ File.separator;
	public static final String CLASS_TEST_PATH = AllTests.TEST_PATH + "class"
			+ File.separator;
	/**
	 * Class to test for void ClassLoader(URL[], URL[])
	 */
	public void testClassLoaderURLArrayURLArray() {
		URL[] urls = new URL[2];
		try {
			urls[0] = new URL("file://" + CLASS_TEST_PATH);
			urls[1] = new URL("jar:file://" + LIB_TEST_PATH + "util.jar!/");
		} catch (MalformedURLException e) {
			assertFalse(e.getMessage(), true);
		}
		try {
			new ClassLoader(urls, new URL[0]);
		} catch (IllegalArgumentException e) {
			assertFalse(e.getMessage(), true);
		}
		try {
			urls[0] = new URL("file://c");
			urls[1] = new URL("jar:file://c!/class");
		} catch (MalformedURLException e) {
			assertFalse(e.getMessage(), true);
		}
		try {
			new ClassLoader(urls, new URL[0]);
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	/**
	 * Class to test for Class loadClass(String)
	 */
	public void testLoadClassString() throws Exception {
		URL[] urls = new URL[2];
		urls[0] = new URL("file://" + CLASS_TEST_PATH);
		urls[1] = new URL("jar:file://" + LIB_TEST_PATH + "util.jar!/");
		ClassLoader cl = new ClassLoader(urls, new URL[0]);
		assertEquals("util.Data", cl.loadClass("util.Data").getName());
		assertEquals("util.StringUtils", cl.loadClass("util.StringUtils").getName());
		assertEquals("util.CheckBox", cl.loadClass("util.CheckBox").getName());
		assertEquals("util.Base64", cl.loadClass("util.Base64").getName());
	}
}