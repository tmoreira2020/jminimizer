package net.java.dev.jminimizer.util;
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
	public void visit(String className) {
	    System.out.println(className.toString());
		System.out.println();
		System.out.println();
	}
	
	/**
     * @see net.java.dev.jminimizer.util.Visitor#finish()
     */
    public void finish() throws Exception {
        System.out.println("DisplayVisitor.finish");
    }
}