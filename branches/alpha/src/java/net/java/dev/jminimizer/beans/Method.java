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
	public Method(String className, String name, String[] argumentClasses,
			String returnClass) {
		this(className, name, Utility.methodTypeToSignature(returnClass,
				argumentClasses));
	}
	/**
	 *  
	 */
	public org.apache.bcel.classfile.Method toClassFileMethod()
			throws ClassNotFoundException {
		ClassGen clazz = new ClassGen(Repository.lookupClass(className));
		//TODO lancar execeção que não achou o metodo
		return clazz.containsMethod(name, signature);
	}
	/**
	 *  
	 */
	public MethodGen toMethodGen() throws ClassNotFoundException {
		if (method == null) {
			ClassGen clazz = new ClassGen(Repository.lookupClass(className));
				method = new MethodGen(this.toClassFileMethod(), className, clazz
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
	/**
	 * @param className
	 * @param name
	 * @param signature
	 */
	public Method(String className, String name, String signature) {
		super(className, name, signature);
		// TODO Auto-generated constructor stub
	}
}