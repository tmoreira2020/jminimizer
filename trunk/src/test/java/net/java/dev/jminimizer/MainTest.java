package net.java.dev.jminimizer;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.bcel.classfile.ClassParser;

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
	
	public void testMain11() {
		this.performTest("test11");
	}
	
	public void testMain12() {
		this.performTest("test12");
	}
	
	public void testMain13() {
		this.performTest("test13");
	}
	
	private void performTest(String order) {
		String[] args= new String[]{"-c", "src/test/xml/"+order+".xml"}; 
		try {
			JMinimizer.main(args);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
		Method method= null;
		try {
			ClassLoader loader= new URLClassLoader(new URL[]{new URL("file:./target/test-jminimizer/"+order+"/classes/")}, null);
			Class clazz= loader.loadClass("net.java.dev.jminimizer."+order+".Main");
			method= clazz.getMethod("main", new Class[]{String[].class});
		} catch (Throwable e) {
			Assert.assertTrue(e.getMessage(), false);
		}
		try {
			method.invoke(null, new Object[]{new String[0]});
		} catch (Throwable e) {
			Assert.assertTrue(e.getMessage(), false);
		}
		try {
		    ClassParser classParser= new ClassParser("./target/test-jminimizer/"+order+"/classes/net/java/dev/jminimizer/"+order+"/Main.class");
		    classParser.parse();
		} catch (Throwable e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

}

