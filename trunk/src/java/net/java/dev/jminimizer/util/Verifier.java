package net.java.dev.jminimizer.util;
import java.io.File;
import java.net.URL;

import org.apache.bcel.Repository;

/**
 * @author Thiago Leão Moreira
 * @since Aug 3, 2004
 *  
 */
public class Verifier {

	public static void main(String[] args) throws Exception {
	    URL url;
	    File file= new File(args[0]);
	    if (file.isFile()) {
	        url= new URL("jar:file:"+args[0]+"!/");
	    } else {
	        url= file.getAbsoluteFile().toURL();
	    }
		Repository.setRepository(new URLRepository(new URL[]{url}, new URL[0]));
		String[] argsTemp=new String[args.length-1];
		for (int i = 0; i < argsTemp.length; i++) {
			argsTemp[i]= args[i+1];
		}
		org.apache.bcel.verifier.Verifier.main(argsTemp);
	}

}