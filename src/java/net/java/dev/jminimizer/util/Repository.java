package net.java.dev.jminimizer.util;

import java.util.Set;

import net.java.dev.jminimizer.beans.Class;

/**
 * @author Thiago Leão Moreira
 * @since Apr 16, 2004
 *
 */
public interface Repository extends org.apache.bcel.util.Repository {

	public Set getProgramClasses();
	
	public Class loadClass(String className, boolean loadMembers) throws ClassNotFoundException;
}
