package net.java.dev.jminimizer.util;
import net.java.dev.jminimizer.beans.Method;
import org.apache.bcel.generic.MethodGen;
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
	public boolean inspect(MethodGen method);
	/**
	 * 
	 * @return
	 */
	public boolean remove(MethodGen method) throws ClassNotFoundException;
}