package net.java.dev.jminimizer;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import net.java.dev.jminimizer.beans.Constructor;
import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.URLRepository;
import net.java.dev.jminimizer.util.XMLMethodInspector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 16, 2004
 *  
 */
public class AnalyserTest extends TestCase {
	private static final Log log = LogFactory.getLog(AnalyserTest.class);
	/*
	 * Class to test for void analyse(Method)
	 */
	public void testAnalyseMethod() {
	}
	/*
	 * Class to test for void analyse(Method[])
	 */
	public void testAnalyseMethodArray() {
	}
	public void testGetClasses() throws Exception{
		Analyser an = this.getAnalyser();
		Map classes= an.getClasses();
		assertTrue("Must contains: dummy.Teacher class", classes.containsKey("dummy.Teacher"));
		assertTrue("Must contains: dummy.Worker class", classes.containsKey("dummy.Worker"));
		assertTrue("Must contains: dummy.Person class", classes.containsKey("dummy.Person"));
		assertTrue("Must contains: java.lang.Object class", classes.containsKey("java.lang.Object"));
		assertFalse("Must not contains java.jang.Integer class", classes.containsKey("java.lang.Integer"));
		assertFalse("Must not contains java.jang.Number class", classes.containsKey("java.lang.Number"));
		assertFalse("Must not contains java.jang.StringBuffer class", classes.containsKey("java.lang.StringBuffer"));
	}
	public void testGetInspecter() {
	}
	public void testGetLimitsMethods() throws Exception{
		Analyser an = this.getAnalyser();
		Set lm= an.getLimitsMethods();
		assertEquals(3, lm.size());
		Method m= new Method("dummy.Person", "getName", "()Ljava/lang/String;", false);
		assertTrue("Must contains: " + m + " method", lm.contains(m));
		m= new Method("dummy.Person", "setName", "(Ljava/lang/String;)V", false);
		assertTrue("Must contains: " + m + " method", lm.contains(m));
		m= new Constructor("java.lang.Object", "()V");
		assertTrue("Must contains: " + m + " constructor", lm.contains(m));
		m= new Constructor("java.lang.String", "()V");
		assertFalse("Must not contains: " + m + " constructor", lm.contains(m));
	}
	public void testGetRepository() {
	}
	public void testVisit() throws Exception {
		Analyser an = this.getAnalyser();
		File d = new File("temp");
		if (d.exists()) {
			d.delete();
		}
		d.mkdirs();
		an.visit(new Transformer(an.getRepository(), d));
	}
	private Analyser getAnalyser() throws Exception {
		URL[] program = new URL[]{new URL("file:target/test-classes/")};
		URL[] runtime = new URL[]{};
		Repository repo = new URLRepository(program, runtime);
		return new Analyser(new XMLMethodInspector(AllTests.TEST_PATH + "xml"
				+ File.separator + "AnalyserTest.xml", repo), repo);
	}
}