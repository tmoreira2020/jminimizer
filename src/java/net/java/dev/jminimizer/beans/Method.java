package net.java.dev.jminimizer.beans;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class Method extends FieldOrMethod {
	
	private MethodGen method;
	/**
	 *  
	 */
	public static String toPattern(MethodGen method) {
		return Method.toPattern(method.getClassName(), method.getName(), method
				.getSignature());
	}
	/**
	 *  
	 */
	public static String toPattern(String className, String name,
			String signature) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(className);
		buffer.append('.');
		buffer.append(name);
		buffer.append(signature);
		return buffer.toString();
	}
	/**
	 *  
	 */
	public static String toString(MethodGen method) {
		return new Method(method).toString();
	}
	/**
	 *  
	 */
	private boolean inSubClasses;
	/**
	 *  
	 */
	public Method(String className, String name,
			String[] argumentClasses, String returnClass,
			boolean inSubClasses) {
	    this(className, name, Utility.methodTypeToSignature(returnClass, argumentClasses), inSubClasses);
	}
	/**
	 *  
	 */
	public Method(MethodGen method) {
		this(method.getClassName(), method.getName(), method.getSignature(),
				false);
		this.method= method;
	}
	/**
	 *  
	 */
	public Method(String className, String name, String signature,
			boolean inSubClasses) {
		super();
		this.setClassName(className);
		this.setName(name);
		this.setSignature(signature);
		this.inSubClasses = inSubClasses;
	}
	/**
	 *  
	 */
	public String getNameAndSignature() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getName());
		buffer.append(this.getSignature());
		return buffer.toString();
	}
	/**
	 * @return Returns the inSubClasses.
	 */
	public boolean isInSubClasses() {
		return this.inSubClasses;
	}
	/**
	 *  
	 */
	public org.apache.bcel.classfile.Method toClassFileMethod()
			throws ClassNotFoundException {
		ClassGen clazz = new ClassGen(Repository.lookupClass(className));
		return clazz.containsMethod(name, signature);
	}
	/**
	 *  
	 */
	public MethodGen toMethodGen() throws ClassNotFoundException {
		if (method == null) {
			ClassGen clazz = new ClassGen(Repository.lookupClass(className));
			method= new MethodGen(this.toClassFileMethod(), className, clazz
					.getConstantPool());
		}
		return method;
	}
	/**
	 *  
	 */
	public String toPattern() {
		return Method.toPattern(this.getClassName(), this.getName(), this
				.getSignature());
	}
	/**
	 *  
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Type ret = Type.getReturnType(this.getSignature());
		buffer.append(ret);
		buffer.append(' ');
		buffer.append(className);
		buffer.append('.');
		buffer.append(name);
		buffer.append('(');
		Type[] args = Type.getArgumentTypes(this.getSignature());
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			if (i != args.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(')');
		return buffer.toString();
	}
}