package net.java.dev.jminimizer.test17;

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
	    return new PeãoChãoDeFabrica() {
	        /**
             * @see net.java.dev.jminimizer.test17.Pessoa#getIdade()
             */
            public int getIdade() {
                System.out.println(".getIdade");
                return 0;
            }
            
            /**
             * @see net.java.dev.jminimizer.test17.Pessoa#getNome()
             */
            public String getNome() {
                System.out.println(".getNome");
                return "nome";
            }
            
            /**
             * @see net.java.dev.jminimizer.test17.Peão#getProfissão()
             */
            public String getProfissão() {
                System.out.println(".getProfissão");
                return "profissão";
            }
            
            /**
             * @see net.java.dev.jminimizer.test17.Peão#getSalario()
             */
            public double getSalario() {
                System.out.println(".getSalario");
                return 34.90;
            }
	    };
	}
	
}

interface SerVivo {
    
}

interface Pessoa extends SerVivo{
    String getNome();
    
    int getIdade();
}


interface Peão extends Serializable, Pessoa  {
    double getSalario();
    String getProfissão();
}
interface PeãoAnalfabeto extends Peão {
    
}

interface PeãoChãoDeFabrica extends PeãoAnalfabeto {
    
}