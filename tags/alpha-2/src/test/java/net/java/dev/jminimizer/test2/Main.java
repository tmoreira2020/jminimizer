package net.java.dev.jminimizer.test2;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main implements Runnable{

	public static void main(String[] args) {
		new Thread(new Main()).start();
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Hello World with JMinimizer!!");
	}
	
	private void nothing() {
		System.out.println("nothing");
	}

}