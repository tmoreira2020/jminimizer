package net.java.dev.jminimizer.util;
import net.java.dev.jminimizer.beans.Method;
/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public interface MethodInspector {
	/**
	 * 
	 * @return
	 */
	public Method[] getMethodsToInspect();
	/**
	 * 
	 * @return
	 */
	public boolean inspect(Method method);
}