package net.java.dev.jminimizer.test12;

import java.io.Serializable;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    Peão pessoa= (Peão)getUsuário();
	    if (pessoa != null) {
            System.out.println(pessoa.getNome());
            System.out.println(pessoa.getProfissão());
        } else {
            System.out.println("Peão é null");
        }
	}
	
	static Object getUsuário() {
	    return null;
	}
	
}

interface Pessoa {
    String getNome();
    
    int getIdade();
}


interface Peão extends Serializable, Pessoa  {
    double getSalario();
    String getProfissão();
}