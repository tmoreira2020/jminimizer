package net.java.dev.jminimizer.util;
import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.Method;
/**
 * @author Thiago Leão Moreira <thiago.moreira@datasul.com.br>
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
		System.out.println(clazz.getName());
		System.out.println("Fields: ");
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			System.out.println("\t" + fields[i]);
		}
		System.out.println("Methods: ");
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			System.out.println("\t" + methods[i]);
		}
		System.out.println();
		System.out.println();
	}
}