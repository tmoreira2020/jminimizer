package net.java.dev.jminimizer.util;



import java.util.Set;

import net.java.dev.jminimizer.beans.Method;

import org.apache.bcel.Repository;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Leão Moreira
 * @since Apr 13, 2004
 *  
 */
public class ClassUtils {

	private static final Log log = LogFactory.getLog(ClassUtils.class);

	public static Method findMethod(Configurator configurator, Set classesUsedByProgram, String className, String name, String signature) throws ClassNotFoundException {
		ClassGen clazz = new ClassGen(Repository.lookupClass(className));
		org.apache.bcel.classfile.Method method;
		Method m= null;
		do {
		    className= clazz.getClassName();
            if (configurator.inspect(className) && classesUsedByProgram.add(className)) {
            	//used just to relax de user
            	log.info("Analysing class: "+className);
            }
			method = clazz.containsMethod(name, signature);
			if (method != null) {
				m= new Method(clazz.getClassName(), method.getName(), method.getSignature());
				log.debug("Method find: " + m);
				return m;
			} else {
			    String[] interfaces= clazz.getInterfaceNames();
			    for (int i = 0; i < interfaces.length; i++) {
                    m= ClassUtils.findMethod(configurator, classesUsedByProgram, interfaces[i], name, signature);
                    if (m != null) {
        				log.debug("Method find: " + m);
                        return m;
                    }
                }
			}
			clazz = new ClassGen(Repository.lookupClass(clazz.getSuperclassName()));
		} while (!clazz.getClassName().equals("java.lang.Object"));
		return m;
	}

	public static FieldGen findField(String className, String name) throws ClassNotFoundException {
		ClassGen clazz = new ClassGen(Repository.lookupClass(className));
		org.apache.bcel.classfile.Field field;
		do {
			field = clazz.containsField(name);
			if (field != null) {
				log.debug("Field find: " + clazz.getClassName() + "." +field);
				return new FieldGen(field, clazz.getConstantPool());
			}
			clazz = new ClassGen(Repository.lookupClass(clazz.getSuperclassName()));
		} while (clazz != null);
		return null;
	}

	public static String normalize(String string) {
		StringBuffer buffer = new StringBuffer(string);
		char[] chs = {'.', '(', ')', '$'};
		for (int i = 0; i < chs.length; i++) {
			ClassUtils.normalize(chs[i], buffer);
		}
		return buffer.toString().replace('*', '.');
	}

	private static void normalize(char c, StringBuffer buffer) {
		String ch = "" + c;
		int i = buffer.indexOf(ch);
		while (i != -1) {
			buffer.insert(i, '\\');
			i = buffer.indexOf(ch, i + 2);
		}
	}

}
