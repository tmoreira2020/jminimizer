package net.java.dev.jminimizer.util;
import net.java.dev.jminimizer.beans.Class;
/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * @since Apr 15, 2004
 *  
 */
public class DisplayVisitor implements Visitor {
	/**
	 *  
	 */
	public DisplayVisitor() {
		super();
	}
	/**
	 * @see net.java.dev.jminimizer.util.Visitor#visit(net.java.dev.jminimizer.beans.Class)
	 */
	public void visit(Class clazz) {
	    System.out.println(clazz.toString());
		System.out.println();
		System.out.println();
	}
}