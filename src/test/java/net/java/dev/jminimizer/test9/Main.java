package net.java.dev.jminimizer.test9;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main extends ArrayList implements Comparable{
	
	final double ds= 934563456.234859843;
	final float fs= 2345234.2345F;
	final long ls= 2783465289375L;

	public static void main(String[] args) {
		System.out.println(new Main().clone());
	}
	
	
	/**
	 * @deprecated
	 */
	public int compareTo(Object o) {
		System.out.println("Main.compareTo");
		System.out.println(fs);
		double x= ds+0234475;
		return (int)ls;
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