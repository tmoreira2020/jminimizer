package net.java.dev.jminimizer;
import java.io.File;

import junit.framework.TestCase;
import net.java.dev.jminimizer.util.Configurator;
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
		Configurator configurator= new XMLConfigurator(new File(AllTests.TEST_PATH + "xml",	"AnalyserTest.xml"));
		Repository repo= new URLRepository(configurator.getProgramClasspath(), configurator.getRuntimeClasspath());
		return new Analyser(configurator, repo);
	}
}