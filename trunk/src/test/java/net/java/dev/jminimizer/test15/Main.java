package net.java.dev.jminimizer.test15;



/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    int[] is= (int[])testInt();
	    long[] ls= (long[]) testLong();
	    byte[] bs = (byte[]) testByte();
	    double[] ds= (double[]) testDouble();
	}
	
	static Object testInt() {
	    return new int[0];
	}
	
	static Object testLong() {
	    return new long[0];
	}
	
	static Object testByte() {
	    return new byte[0];
	}
	
	static Object testDouble() {
	    return new double[0];
	}
	
}