package net.java.dev.jminimizer.util;


import org.apache.bcel.Repository;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Leão Moreira
 * @since Apr 13, 2004
 *  
 */
public class ClassUtils {

	private static final Log log = LogFactory.getLog(ClassUtils.class);

	public static boolean isMethodOverridFromSuperClass(MethodGen method)
		throws ClassNotFoundException {
		String className= method.getClassName();
		Class clazz = Class.forName(className);
		//TODO verificar o relacionamento com interfaces e superinterfaces
		if (className.equals("java.lang.Object") || clazz.isInterface()) {
			return true;
		} else {
			clazz= clazz.getSuperclass();
			MethodGen m= findMethod(clazz.getName(), method.getName(), method.getSignature());
			return m != null;
		}
	}

	public static MethodGen findMethod(String className, String name, String signature) throws ClassNotFoundException {
		ClassGen clazz = new ClassGen(Repository.lookupClass(className));
		org.apache.bcel.classfile.Method method;
		do {
			method = clazz.containsMethod(name, signature);
			if (method != null) {
				log.debug("Method find: " + clazz.getClassName() + "." +method);
				return new MethodGen(method, clazz.getClassName(), clazz.getConstantPool());
			}
			clazz = new ClassGen(Repository.lookupClass(clazz.getSuperclassName()));
		} while (!clazz.getClassName().equals("java.lang.Object"));
		return null;
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
