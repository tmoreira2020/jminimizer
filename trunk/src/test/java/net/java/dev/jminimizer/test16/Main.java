package net.java.dev.jminimizer.test16;



/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    Implemetation implemetation= getImpl();
	    if (implemetation != null) {
	        implemetation.setIndex(52, "");
	    }
	}
	
	static Implemetation getImpl() {
	    return null;
	}
}

interface Interface {
    void setIndex(int index, String name);
}

abstract class Implemetation implements Interface {
    abstract int getIndex();
}