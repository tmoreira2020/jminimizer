package net.java.dev.jminimizer;
import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;
import net.java.dev.jminimizer.util.DisplayVisitor;
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
//		Analyser an = this.getAnalyser();
//		Map classes= an.getClasses();
	}
	public void testGetInspecter() {
	}
	public void testGetLimitsMethods() throws Exception{
		Analyser an = this.getAnalyser();
		System.out.println(an.getLimitsMethods());
		an.visit(new DisplayVisitor());
	}
	public void testGetRepository() {
	}
	public void testVisit() throws Exception {
/*		Analyser an = this.getAnalyser();
		File d = new File("temp");
		if (d.exists()) {
			d.delete();
		}
		d.mkdirs();
		an.visit(new Transformer(an.getRepository(), d));
	*/}
	private Analyser getAnalyser() throws Exception {
		URL[] program = new URL[]{new URL("file:target/test-classes/")};
		URL[] runtime = new URL[]{};
		Repository repo = new URLRepository(program, runtime);
		return new Analyser(new XMLMethodInspector(AllTests.TEST_PATH + "xml"
				+ File.separator + "AnalyserTest.xml"), repo);
	}
}