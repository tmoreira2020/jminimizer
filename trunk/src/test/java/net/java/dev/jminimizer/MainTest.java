package net.java.dev.jminimizer;

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
			System.out.println(loader.loadClass("net.java.dev.jminimizer."+order+".Main"));
		} catch (Throwable e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

}

