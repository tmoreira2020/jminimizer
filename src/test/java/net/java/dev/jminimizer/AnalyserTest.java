package net.java.dev.jminimizer;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.XMLMethodInspector;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * @since Apr 15, 2004
 *
 */
public class AnalyserTest extends TestCase {

    /*
     * Class to test for void analyse(Method)
     */
    public void testAnalyseMethod() throws Exception {
        Analyser an= this.getAnalyser();
//        an.analyse(new Method());
    }

    /*
     * Class to test for void analyse(Method[])
     */
    public void testAnalyseMethodArray() {
    }

    public void testDump() {
    }

    public void testGetLimitsMethods() {
    }

    public void testVisit() {
    }
    
    private Analyser getAnalyser() throws Exception {
        URL[] program= new URL[]{};
        URL[] runtime= new URL[]{};
        Repository repo= new Repository(program, runtime);
        return new Analyser(new XMLMethodInspector(AllTests.TEST_PATH + "xml" + File.separator + "AnalyserTest.xml"), repo);
    }

}
