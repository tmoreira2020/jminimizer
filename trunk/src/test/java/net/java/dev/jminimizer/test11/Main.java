package net.java.dev.jminimizer.test11;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
        double x= Math.E;
        x++;
        Math.max(x, 7865.45689);
	}
	
	class Inner extends Thread {
	    
	    /**
         * @see java.lang.Thread#run()
         */
        public void run() {
            double x= Math.E;
            x++;
            x=0;
        }
	}
	
	public void test() {
	    new Inner().start();
	}

}