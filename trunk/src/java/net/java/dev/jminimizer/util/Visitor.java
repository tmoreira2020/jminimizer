package net.java.dev.jminimizer.util;

import net.java.dev.jminimizer.beans.Class;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public interface Visitor {
	
	public void visit(Class clazz);
	
}
