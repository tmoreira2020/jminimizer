package net.java.dev.jminimizer;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 16, 2004
 *  
 */
public class Transformer implements Visitor {
	private static final Log log = LogFactory.getLog(Transformer.class);
	private net.java.dev.jminimizer.util.Repository repo;
	private File directory;
	private Set classes;
	/**
	 *  
	 */
	public Transformer(Repository repo, File directory) {
		super();
		this.repo= repo;
		this.directory= directory;
		this.classes= repo.getProgramClasses();
	}
	/**
	 * @see net.java.dev.jminimizer.util.Visitor#visit(net.java.dev.jminimizer.beans.Class)
	 */
	public void visit(Class clazz) {
        String className = clazz.getName();
        if (classes.contains(className)) {
            JavaClass jc = repo.findClass(className);
            org.apache.bcel.classfile.Method[] ms = jc.getMethods();
            log.debug("Cleaning class: " + className);
            ClassGen cg = new ClassGen(jc);
            for (int i = 0; i < ms.length; i++) {
                if (!clazz.containsMethod(ms[i].getName(), ms[i]
                        .getSignature())) {
                    log.debug("Removing method: " + ms[i]);
                    cg.removeMethod(ms[i]);
                }
            }
            org.apache.bcel.classfile.Field[] fs = jc.getFields();
            for (int i = 0; i < fs.length; i++) {
                if (!clazz.containsField(fs[i].getName(), fs[i]
                        .getSignature())) {
                    log.debug("Removing field: " + fs[i]);
                    cg.removeField(fs[i]);
                }
            }
            cg.update();
            File file = new File(directory, className.replace('.',
                    File.separatorChar).concat(".class"));
            log.debug("Dumping class: " + className + " to file: " + file);
            try {
				cg.getJavaClass().dump(file);
			} catch (IOException e) {
	            log.error("Error on dumping class: " + className + " to file: " + file, e);
			}
        }
	}
}