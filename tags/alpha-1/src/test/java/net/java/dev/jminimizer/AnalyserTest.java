package net.java.dev.jminimizer;
import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.URLRepository;
import net.java.dev.jminimizer.util.XMLConfigurator;

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
	public void testGetInspecter() {
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
//		an.visit(new Transformer(an.getRepository(), d));
	}
	private Analyser getAnalyser() throws Exception {
		URL[] program = new URL[]{new URL("file:target/test-classes/")};
		URL[] runtime = new URL[]{};
		Repository repo = new URLRepository(program, runtime);
		return new Analyser(new XMLConfigurator(new File(AllTests.TEST_PATH + "xml",
				"AnalyserTest.xml"), repo), repo);
	}
}