package net.java.dev.jminimizer.beans;

import org.apache.bcel.generic.Type;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Field extends FieldOrMethod {

	/**
	 * 
	 */
	public Field(String className, String name, String signature) {
		super();
		this.setClassName(className);
		this.setName(name);
		this.setSignature(signature);
	}
	
	public Field(java.lang.reflect.Field field) {
	    this(field.getDeclaringClass().getName(), field.getName(), Type.getType(field.getType()).getSignature());
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
