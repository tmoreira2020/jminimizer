package net.java.dev.jminimizer.beans;


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

}