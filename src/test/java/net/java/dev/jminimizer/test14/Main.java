package net.java.dev.jminimizer.test14;

import java.io.Serializable;


/**
 * @author Thiago Le�o Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    Object pessoa= getUsu�rio();
	    if (pessoa instanceof Pessoa) {
            System.out.println("� pessoa");
        }else if (pessoa instanceof Pe�o) {
            Pe�o pe�o= (Pe�o) pessoa;
        }
	}
	
	static Object getUsu�rio() {
	    return null;
	}
	
}

interface Pessoa {
    String getNome();
    
    int getIdade();
}


interface Pe�o extends Serializable, Pessoa  {
    double getSalario();
    String getProfiss�o();
}

interface Pe�oCh�oDeFabrica extends Pe�o, Serializable {
    
}