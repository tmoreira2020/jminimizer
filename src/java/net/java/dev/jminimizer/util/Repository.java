package net.java.dev.jminimizer.util;

import java.util.Set;

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
	 * Build a set with all resources that composite the program. 
	 * @return
	 */
	public Set getProgramResources();
	
}
