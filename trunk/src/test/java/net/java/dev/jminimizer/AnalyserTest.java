package net.java.dev.jminimizer;

import java.net.URL;
import java.util.SortedSet;

import junit.framework.TestCase;
import net.java.dev.jminimizer.util.ClassLoader;
import net.java.dev.jminimizer.util.DisplayVisitor;
import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.XMLMethodInspector;
import net.java.dev.jminimizer.util.XMLMethodInspectorTest;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Thiago Leão Moreira
 * @since Apr 13, 2004
 *  
 */
public class AnalyserTest extends TestCase {
	
	private Analyser analyser;
	
	public void testVisit()throws Exception{
	}

	public void testGetLimitsMethods() throws Exception{
		MethodInspector mi= new XMLMethodInspector(XMLMethodInspectorTest.XML_TEST_PATH + "AnalyserTest.xml");
		analyser= new Analyser(mi, new ClassLoader(new URL[]{new URL("file:target/test-classes/")}, new URL[0]));
//		analyser= new Analyser(mi, new ClassLoader(new URL[0], new URL[0]));
		JavaClass clazz= Repository.lookupClass("dummy.Main");
		Class temp= Class.forName("dummy.Main");
		org.apache.bcel.classfile.Method m= clazz.getMethod(temp.getMethod("main", new Class[]{String[].class}));
		analyser.analyse(clazz, m);
		analyser.visit(new DisplayVisitor());
		SortedSet set= analyser.getLimitsMethods();
		System.out.println(set);
	}
}