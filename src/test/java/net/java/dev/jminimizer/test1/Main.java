package net.java.dev.jminimizer.test1;

/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {
	
	static final int x= 90;

	public static int test(String[] args) {
		String string= "Hello World with JMinimizer!!";
		if (string.length() > 90) {
			return string.length();
		}
		return x;
	}

	public static void main(String[] args) {
		System.out.println(test(args));
	}

}