package net.java.dev.jminimizer.util;


import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import net.java.dev.jminimizer.AllTests;
import net.java.dev.jminimizer.beans.Constructor;
import net.java.dev.jminimizer.beans.Method;

/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public class XMLMethodInspectorTest extends TestCase {
	
	public static final String XML_TEST_PATH = AllTests.TEST_PATH + "xml" + File.separator;

	public void testGetMethodsToInspect() throws Exception{
		MethodInspector mi = new XMLMethodInspector(new File(XML_TEST_PATH,"XMLMethodInspectorTest-inspect.xml"), this.getRepository());
		Method[] ms = mi.getMethodsToInspect();
		//check if amount of method/constructor is three
		assertEquals(3, ms.length);
		//check if the method/constructor exist
		for (int i = 0; i < ms.length; i++) {
			if (ms[i].equals(new Method("dummy.Person", "getName", "()Ljava/lang/String;"))) {
				continue;
			}
			if (ms[i].equals(new Method("dummy.Person", "setName", "(Ljava/lang/String;)V"))) {
				continue;
			}
			if (ms[i].equals(new Constructor("dummy.Teacher", "(Ldummy/Teacher;)V"))) {
				continue;
			}
			assertTrue("Method not find: " + ms[i].toString(), false);
		}
	}
	
	public void testInspect() throws Exception{
		MethodInspector mi = new XMLMethodInspector(new File(XML_TEST_PATH, "XMLMethodInspectorTest-notInspect.xml"), this.getRepository());
		
		//test if "org.w3c.dom.Document.getDoctype()Lorg/w3c/dom/DocumentType;" method is for inspect ?
		Method m= new Method("org.w3c.dom.Document", "getDoctype", "()Lorg/w3c/dom/DocumentType;");
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m));
		
		//test if "java.lang.Object.<init>()V" method is for inspect ?
		m= new Constructor("java.lang.Object", "()V");
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m));
		
		//test if "java.lang.Integer.<init>(I)V" method is for inspect ?
		m= new Method("java.lang.Integer", "intValue", "()I");
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m));
		
	}

	public void testRemove() throws Exception {
		MethodInspector mi = new XMLMethodInspector(new File(XML_TEST_PATH, "XMLMethodInspectorTest-notRemove.xml"), this.getRepository());
		
		//test if "dummy.Teacher.getJob()Ljava/lang/String;" method is for remove ?
		Method m= new Method("dummy.Teacher", "getJob", "()Ljava/lang/String;");
//		assertFalse("Not to remove: " + m.toString(), mi.remove(m));
		
		//test if "dummy.Teacher.getSalary()I" method is for remove ?
		m= new Method("dummy.Teacher", "getSalary", "()I");
//		assertFalse("Not to remove: " + m.toString(), mi.remove(m));
		
		//test if "java.lang.Object.<init>()V" method is for remove ?
		m= new Constructor("java.lang.Object","()V");
//		assertFalse("Not to remove: " + m.toString(), mi.remove(m));
		
		//test if "java.lang.Integer.<init>(I)V" method is for remove ?
		m= new Method("java.lang.Integer", "intValue", "()I");
		//assertTrue("To remove: " + m.toString(), mi.remove(m));
		
		//test if "dummy.Worker.setJob(Ljava/lang/String;)V" method is for remove ?
		m= new Method("dummy.Worker", "setJob", "(Ljava/lang/String;)V");
		//assertTrue("To remove: " + m.toString(), mi.remove(m));
		
		//test if "org.w3c.dom.Document.getDoctype()Lorg/w3c/dom/DocumentType;" method is for remove ?
		m= new Method("org.w3c.dom.Document", "getDoctype", "()Lorg/w3c/dom/DocumentType;");
		//assertFalse("Not to remove: " + m.toString(), mi.remove(m));
	}
	
	private Repository getRepository() throws Exception {
		URL[] program = new URL[]{new URL("file:target/test-classes/")};
		URL[] runtime = new URL[]{};
		return new URLRepository(program, runtime);
	}

}