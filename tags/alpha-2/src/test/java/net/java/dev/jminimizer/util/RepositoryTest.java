package net.java.dev.jminimizer.util;

import java.net.URL;
import java.util.Set;

import junit.framework.TestCase;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * @since Apr 15, 2004
 *
 */
public class RepositoryTest extends TestCase {

    public void testStoreClass() throws Exception {
        Repository repo= this.getRepository();
        boolean b;
        try {
            repo.storeClass(repo.loadClass("util.Data"));
            b= false;
        } catch (RuntimeException e) {
            b= true;
        }
        assertTrue(b);
    }

    public void testRemoveClass() throws Exception {
        Repository repo= this.getRepository();
        repo.removeClass(repo.loadClass("util.Data"));
        assertNull(repo.findClass("util.Data"));
    }

    public void testFindClass() throws Exception {
        Repository repo= this.getRepository();
        repo.getProgramClasses();
        assertEquals("util.Data", repo.findClass("util.Data").getClassName());
    }

    /*
     * Class to test for JavaClass loadClass(String)
     */
    public void testLoadClassString() throws Exception {
        Repository repo= this.getRepository();
        assertEquals("util.Data", repo.loadClass("util.Data").getClassName());
    }

    /*
     * Class to test for JavaClass loadClass(Class)
     */
    public void testLoadClassClass() throws Exception{
        Repository repo= this.getRepository();
        assertEquals("java.lang.Object", repo.loadClass("java.lang.Object").getClassName());
    }

    public void testClear() throws Exception {
        Repository repo= this.getRepository();
        repo.getProgramClasses();
        repo.clear();
        assertNull(repo.findClass("util.Data"));
    }

    public void testGetProgramClasses() throws Exception {
        Repository repo= this.getRepository();
        Set classes= repo.getProgramClasses();
        assertTrue(classes.contains("util.Data"));
        assertTrue(classes.contains("util.StringUtils"));
        assertFalse(classes.contains("java.lang.Object"));
    }
    
    private Repository getRepository() throws Exception {
        return new URLRepository(new URL[]{new URL("file:src/test/class/")}, new URL[0]);
    }

}
