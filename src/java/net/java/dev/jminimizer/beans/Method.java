package net.java.dev.jminimizer.beans;

import org.apache.bcel.Repository;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Method extends FieldOrMethod {
	
	private boolean inSubClasses;

	/**
	 * 
	 */
	public Method(String className, String name, String signature, boolean inSubClasses) {
		super();
		this.setClassName(className);
		this.setName(name);
		this.setSignature(signature);
		this.inSubClasses= inSubClasses;
	}
	
	public Method(MethodGen method) {
		this(method.getClassName(), method.getName(), method.getSignature(), false); 
	}
	
	public Method(java.lang.reflect.Method method) {
	    this(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), method.getReturnType(), false);
	}

	public Method(java.lang.Class className, String name, java.lang.Class[] argumentClasses, java.lang.Class returnClass, boolean inSubClasses) {
		Type[] argumentTypes = new Type[argumentClasses.length];
		for (int i = 0; i < argumentTypes.length; i++) {
			argumentTypes[i] = Type.getType(argumentClasses[i]);
		}
		this.setClassName(className.getName());
		this.setName(name);
		this.setSignature(Type.getMethodSignature(Type.getType(returnClass), argumentTypes));
		this.inSubClasses= inSubClasses;
	}

	public Type getReturnType() {
		return Type.getReturnType(signature);
	}

	public Type[] getArgumentsType() {
		return Type.getArgumentTypes(signature);
	}

	public String toPattern() {
		return Method.normalize(this.toString());
	}

	public static String toString(MethodGen method) {
		return new Method(method).toString();
	}
	
	public static String normalize(String string) {
		StringBuffer buffer= new StringBuffer(string);
		Method.normalize(buffer);
		return buffer.toString();
	}
	

	private static void normalize(StringBuffer buffer) {
		char[] chs= {'.', '(', ')', '$'};
		for (int i = 0; i < chs.length; i++) {
			normalize(chs[i], buffer);
		}
	}

	private static void normalize(char c, StringBuffer buffer) {
		String ch = "" + c;
		int i = buffer.indexOf(ch);
		while (i != -1) {
			buffer.insert(i, '\\');
			i = buffer.indexOf(ch, i + 2);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClassName());
		buffer.append('.');
		buffer.append(this.getName());
		buffer.append(this.getSignature());
		return buffer.toString();
	}
	
	public org.apache.bcel.classfile.Method toClassFileMethod() throws ClassNotFoundException{
		ClassGen clazz= new ClassGen(Repository.lookupClass(className));
		return clazz.containsMethod(name, signature);
	}

	public MethodGen toMethodGen() throws ClassNotFoundException{
		ClassGen clazz= new ClassGen(Repository.lookupClass(className));
		return new MethodGen(this.toClassFileMethod(), className, clazz.getConstantPool());
	}

	/**
	 * @return Returns the inSubClasses.
	 */
	public boolean isInSubClasses() {
		return this.inSubClasses;
	}
	
	public String getNameAndSignature() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getName());
		buffer.append(this.getSignature());
		return buffer.toString();
	}

}
