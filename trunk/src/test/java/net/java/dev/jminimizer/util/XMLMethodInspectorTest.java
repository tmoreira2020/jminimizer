package net.java.dev.jminimizer.util;


import java.io.File;

import junit.framework.TestCase;
import net.java.dev.jminimizer.AllTests;
import net.java.dev.jminimizer.beans.Constructor;
import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.XMLMethodInspector;

/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public class XMLMethodInspectorTest extends TestCase {
	
	public static final String XML_TEST_PATH = AllTests.TEST_PATH + "xml" + File.separator;

	public void testGetMethodsToInspect() throws Exception{
		MethodInspector mi = new XMLMethodInspector(XML_TEST_PATH + "XMLMethodInspectorTest-inspect.xml");
		Method[] ms = mi.getMethodsToInspect();
		//check if amount of method/constructor is three
		assertEquals(3, ms.length);
		//check if the method/constructor exist
		for (int i = 0; i < ms.length; i++) {
			if (ms[i].equals(new Method("dummy.Person", "getName", "()Ljava/lang/String;", false))) {
				continue;
			}
			if (ms[i].equals(new Method("dummy.Person", "setName", "(Ljava/lang/String;)V", false))) {
				continue;
			}
			if (ms[i].equals(new Constructor("dummy.Teacher", "(Ldummy/Teacher;)V"))) {
				continue;
			}
			assertTrue("Method not find: " + ms[i].toString(), false);
		}
	}
	
	public void testInspect() throws Exception{
		MethodInspector mi = new XMLMethodInspector(XML_TEST_PATH + "XMLMethodInspectorTest-notInspect.xml");
		
		//test if "org.w3c.dom.Document.getDoctype()Lorg/w3c/dom/DocumentType;" method is for inspect ?
		Method m= new Method("org.w3c.dom.Document", "getDoctype", "()Lorg/w3c/dom/DocumentType;", false);
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m.toMethodGen()));
		
		//test if "java.lang.Object.<init>()V" method is for inspect ?
		m= new Constructor("java.lang.Object", "()V");
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m.toMethodGen()));
		
		//test if "java.lang.Integer.<init>(I)V" method is for inspect ?
		m= new Method("java.lang.Integer", "intValue", "()I", false);
		assertFalse("Not to inspect: " + m.toString(), mi.inspect(m.toMethodGen()));
		
		//test if "java.lang.Object.toString()V" method is for inspect ?
		m= new Method("java.lang.Object", "toString", "()Ljava/lang/String;", false);
		assertTrue("To inspect: " + m.toString(), mi.inspect(m.toMethodGen()));
	}

	public void testRemove() throws Exception {
		MethodInspector mi = new XMLMethodInspector(XML_TEST_PATH + "XMLMethodInspectorTest-notRemove.xml");
		
		//test if "dummy.Teacher.getJob()Ljava/lang/String;" method is for remove ?
		Method m= new Method("dummy.Teacher", "getJob", "()Ljava/lang/String;", false);
		assertFalse("Not to remove: " + m.toString(), mi.remove(m.toMethodGen()));
		
		//test if "dummy.Teacher.getSalary()I" method is for remove ?
		m= new Method("dummy.Teacher", "getSalary", "()I", false);
		assertFalse("Not to remove: " + m.toString(), mi.remove(m.toMethodGen()));
		
		//test if "java.lang.Object.<init>()V" method is for remove ?
		m= new Constructor("java.lang.Object","()V");
		assertFalse("Not to remove: " + m.toString(), mi.remove(m.toMethodGen()));
		
		//test if "java.lang.Integer.<init>(I)V" method is for remove ?
		m= new Method("java.lang.Integer", "intValue", "()I", false);
		assertTrue("To remove: " + m.toString(), mi.remove(m.toMethodGen()));
		
		//test if "dummy.Worker.setJob(Ljava/lang/String;)V" method is for remove ?
		m= new Method("dummy.Worker", "setJob", "(Ljava/lang/String;)V", false);
		assertTrue("To remove: " + m.toString(), mi.remove(m.toMethodGen()));
		
		//test if "org.w3c.dom.Document.getDoctype()Lorg/w3c/dom/DocumentType;" method is for remove ?
		m= new Method("org.w3c.dom.Document", "getDoctype", "()Lorg/w3c/dom/DocumentType;", false);
		assertFalse("Not to remove: " + m.toString(), mi.remove(m.toMethodGen()));
	}

}