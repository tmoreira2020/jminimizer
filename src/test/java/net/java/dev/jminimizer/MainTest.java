package net.java.dev.jminimizer;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Thiago Leão Moreira
 * @since Jul 30, 2004
 *
 */
public class MainTest extends TestCase {
	
	public void testMain1() {
		this.performTest("test1");
	}
	
	public void testMain2() {
		this.performTest("test2");
	}
	
	public void testMain3() {
		this.performTest("test3");
	}
	
	public void testMain4() {
		this.performTest("test4");
	}
	
	public void testMain5() {
		this.performTest("test5");
	}
	
	public void testMain6() {
		this.performTest("test6");
	}
	
	public void testMain7() {
		this.performTest("test7");
	}
	
	public void testMain8() {
		this.performTest("test8");
	}
	
	public void testMain9() {
		this.performTest("test9");
	}
	
	public void testMain10() {
		this.performTest("test10");
	}
	
	private void performTest(String order) {
		String[] args= new String[]{"-c", "src/test/xml/"+order+".xml"}; 
		try {
			JMinimizer.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(e.getMessage(), false);
		}
		try {
			ClassLoader loader= new URLClassLoader(new URL[]{new URL("file:./target/test-jminimizer/"+order+"/classes/")}, null);
			Class clazz= loader.loadClass("net.java.dev.jminimizer."+order+".Main");
			Method method= clazz.getMethod("main", new Class[]{String[].class});
			method.invoke(null, new Object[]{new String[0]});
		} catch (Throwable e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

}

