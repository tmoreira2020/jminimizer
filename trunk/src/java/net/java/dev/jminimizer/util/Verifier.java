package net.java.dev.jminimizer.util;
import java.net.URL;

import org.apache.bcel.Repository;

/**
 * @author Thiago Leão Moreira
 * @since Aug 3, 2004
 *  
 */
public class Verifier {

	public static void main(String[] args) throws Exception {
		Repository.setRepository(new URLRepository(new URL[]{new URL("file:"+args[0])}, new URL[0]));
		String[] argsTemp=new String[args.length-1];
		for (int i = 0; i < argsTemp.length; i++) {
			argsTemp[i]= args[i+1];
		}
		org.apache.bcel.verifier.Verifier.main(argsTemp);
	}

}