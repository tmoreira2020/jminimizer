package net.java.dev.jminimizer.util;



/**
 * @author Thiago Le�o Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public interface Visitor {
	
	public void visit(String className) throws Exception;
	
	public void finish() throws Exception;
	
}
