package net.java.dev.jminimizer.beans;

import net.java.dev.jminimizer.util.*;

import org.apache.bcel.generic.Type;


/**
 * @author Thiago Leão Moreira
 * @since Apr 13, 2004
 *  
 */
public class Constructor extends Method {

	/**
	 * @param className
	 * @param signature
	 */
	public Constructor(String className, String signature) {
		super(className, "<init>", signature, false);
	}

	/**
	 * @param className
	 * @param argumentClasses
	 */
	public Constructor(java.lang.Class className, java.lang.Class[] argumentClasses) {
		super(className, "<init>", argumentClasses, void.class, false);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("new ");
		buffer.append(this.getClassName());
		buffer.append('(');
		Type[] args = Type.getArgumentTypes(this.getSignature());
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			buffer.append(", ");
		}
		if (args.length != 0) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}
		buffer.append(')');
		return buffer.toString();
	}
}