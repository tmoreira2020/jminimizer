package net.java.dev.jminimizer.test6;

import java.util.Collection;
import java.util.Locale;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {
	
	private static Locale locale= Locale.getDefault();

	public static void main(String[] args) {
		System.out.println(locale);
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Hello World with JMinimizer!!");
	}
	
	/**
	 * @see java.util.AbstractCollection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		return false;
	}
	
	/**
	 * @see java.util.ArrayList#clear()
	 */
	public void clear() {
		System.out.println("Main.clear");
	}

	private void nothing() {
		System.out.println("nothing");
	}
	
}