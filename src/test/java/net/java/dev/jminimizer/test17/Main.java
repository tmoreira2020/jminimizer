package net.java.dev.jminimizer.test17;

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
	    return new Pe�oCh�oDeFabrica() {
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
             * @see net.java.dev.jminimizer.test17.Pe�o#getProfiss�o()
             */
            public String getProfiss�o() {
                System.out.println(".getProfiss�o");
                return "profiss�o";
            }
            
            /**
             * @see net.java.dev.jminimizer.test17.Pe�o#getSalario()
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


interface Pe�o extends Serializable, Pessoa  {
    double getSalario();
    String getProfiss�o();
}
interface Pe�oAnalfabeto extends Pe�o {
    
}

interface Pe�oCh�oDeFabrica extends Pe�oAnalfabeto {
    
}