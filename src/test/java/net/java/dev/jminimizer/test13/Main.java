package net.java.dev.jminimizer.test13;

import java.io.Serializable;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    PeãoChãoDeFabrica pessoa= (PeãoChãoDeFabrica)getUsuário();
	    if (pessoa != null) {
            System.out.println(pessoa.getNome());
            System.out.println(pessoa.getProfissão());
        } else {
            System.out.println("PeãoChãoDeFabrica é null");
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

interface PeãoChãoDeFabrica extends Peão, Serializable {
    
}