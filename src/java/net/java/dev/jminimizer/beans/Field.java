package net.java.dev.jminimizer.beans;

import org.apache.bcel.generic.Type;

/**
 * @author Thiago Leão Moreira <thiagolm@dev.java.net>
 * 
 */
public class Field extends FieldOrMethod {
	/**
	 * @param className
	 * @param name
	 * @param signature
	 */
	public Field(String className, String name, String signature) {
		super(className, name, signature);
	}

	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Type ret = Type.getReturnType(this.getSignature());
		buffer.append(ret);
		buffer.append(' ');
		buffer.append(this.getClassName());
		buffer.append('.');
		buffer.append(this.getName());
		return buffer.toString();
	}
}
