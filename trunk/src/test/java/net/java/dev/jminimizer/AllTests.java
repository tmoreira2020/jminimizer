package net.java.dev.jminimizer;
import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.java.dev.jminimizer.util.RepositoryTest;
import net.java.dev.jminimizer.util.XMLMethodInspectorTest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 14, 2004
 *  
 */
public class AllTests {

	private static final Log log = LogFactory.getLog(AllTests.class);
	public static final String TEST_PATH;
	static {
		StringBuffer buf= new StringBuffer();
		buf.append(System.getProperty("user.dir"));
		buf.append(File.separator);
		buf.append("src");
		buf.append(File.separator);
		buf.append("test");
		buf.append(File.separator);
		TEST_PATH= buf.toString();
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.java.dev.jminimizer");
		//$JUnit-BEGIN$
		suite.addTestSuite(RepositoryTest.class);
		suite.addTestSuite(XMLMethodInspectorTest.class);
		suite.addTestSuite(AnalyserTest.class);
		suite.addTestSuite(MainTest.class);
		//$JUnit-END$
		return suite;
	}
}