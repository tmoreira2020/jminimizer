package net.java.dev.jminimizer.beans;

import org.apache.bcel.generic.Type;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Field extends FieldOrMethod {

	
	public Field(java.lang.reflect.Field field) {
	    super(field.getDeclaringClass().getName(), field.getName(), Type.getType(field.getType()).getSignature());
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
	/**
	 * @param className
	 * @param name
	 * @param signature
	 */
	public Field(String className, String name, String signature) {
		super(className, name, signature);
	}
}
