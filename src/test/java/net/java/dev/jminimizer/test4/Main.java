package net.java.dev.jminimizer.test4;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main extends ArrayList implements Runnable{

	public static void main(String[] args) {
		Main o= new Main(){
			/**
			 * @see java.util.ArrayList#clone()
			 */
			public Object clone() {
				System.out.println(".clone");
				add("clone");
				return this;
			}
		};
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Hello World with JMinimizer!!");
		this.add("temp");
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