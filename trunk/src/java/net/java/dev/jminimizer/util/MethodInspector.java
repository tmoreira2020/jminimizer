package net.java.dev.jminimizer.util;


import org.apache.bcel.generic.MethodGen;

/**
 * @author Thiago Leão Moreira
 *         <thiago.leao.moreira@terra.com.br>
 *  
 */
public interface MethodInspector {

	public boolean inspect(MethodGen method);

	public boolean remove(MethodGen method) throws ClassNotFoundException;

	public Method[] getMethodsToInspect();
}
