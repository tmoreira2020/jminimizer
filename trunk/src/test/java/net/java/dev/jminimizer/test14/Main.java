package net.java.dev.jminimizer.test14;

import java.io.Serializable;


/**
 * @author Thiago Leão Moreira
 * @since Jul 29, 2004
 *  
 */
public class Main {

	public static void main(String[] args) {
	    Object pessoa= getUsuário();
	    if (pessoa instanceof Pessoa) {
            System.out.println("É pessoa");
        }else if (pessoa instanceof Peão) {
            Peão peão= (Peão) pessoa;
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