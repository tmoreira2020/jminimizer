package net.java.dev.jminimizer.util;

import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.Method;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public interface Visitor {
	
	public void visit(Method method);
	
	public void visit(Field field);

}
