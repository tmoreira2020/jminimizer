package net.java.dev.jminimizer.util;

import java.util.Set;

import net.java.dev.jminimizer.beans.Class;

/**
 * @author Thiago Leão Moreira
 * @since Apr 16, 2004
 *
 */
public interface Repository extends org.apache.bcel.util.Repository {

	/**
	 * Build a set with all classes that composite the program. 
	 * @return
	 */
	public Set getProgramClasses();
	
	/**
	 * 
	 * @param className
	 * @param loadMembers
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String className, boolean loadMembers) throws ClassNotFoundException;
}
