package net.java.dev.jminimizer.test13;

import java.io.Serializable;


/**
 * @author Thiago Le�o Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    Pe�oCh�oDeFabrica pessoa= (Pe�oCh�oDeFabrica)getUsu�rio();
	    if (pessoa != null) {
            System.out.println(pessoa.getNome());
            System.out.println(pessoa.getProfiss�o());
        } else {
            System.out.println("Pe�oCh�oDeFabrica � null");
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