package net.java.dev.jminimizer.test8;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main extends ArrayList implements Comparable{

	public static void main(String[] args) {
		System.out.println(new Main().clone());
	}
	
	
	/**
	 * @deprecated
	 */
	public int compareTo(Object o) {
		System.out.println("Main.compareTo");
		return 0;
	}
	/**
	 * @deprecated
	 */
	public boolean containsAll(Collection c) {
		return false;
	}
	
	/**
	 * @deprecated
	 */
	public void clear() {
		System.out.println("Main.clear");
	}

	/**
	 * @deprecated
	 */
	private void nothing() {
		System.out.println("nothing");
	}
	
}