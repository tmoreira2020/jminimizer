package net.java.dev.jminimizer.test10;

import java.io.FileNotFoundException;

/**
 * @author Thiago Le�o Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
		Pai pai= Pai.getInstance();
		try {
			pai.metodo();
		} catch (FileNotFoundException e) {
		}
	}

}

abstract class Pai {
	abstract void metodo() throws FileNotFoundException;
	
	static Pai getInstance() {
		return new Filho();
	}
}



/**
 * @author Thiago Le�o Moreira
 * @since Aug 4, 2004
 * @deprecated
 */
class Filho extends Pai {
	/**
	 * @see net.java.dev.jminimizer.test10.Pai#metodo()
	 */
	void metodo() throws FileNotFoundException {
		throw new FileNotFoundException("test");
	}
}