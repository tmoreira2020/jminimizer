package net.java.dev.jminimizer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.URLRepository;
import net.java.dev.jminimizer.util.XMLMethodInspector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira
 * @since Apr 18, 2004
 *  
 */
public class JMinimizer {
	private static final Log log = LogFactory.getLog(JMinimizer.class);
	public static void main(String[] args) throws Exception{
		if (args.length != 3) {
			System.out.println("Erro na quantidade argumentos");
			System.exit(0);
		}
		File output= new File("output");
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(1)) {
				case 'o' : {
					output= new File(args[i].substring(2));
					break;
				}
				case 'p' :
					
					break;
				case 'c' :
					
					break;
				case 'r' :
					
					break;
				default :
					break;
			}
		}
		URL[] program= new URL[]{convertToURL(args[0])};
		Repository repo= new URLRepository(program, new URL[0]);
		MethodInspector mi= new XMLMethodInspector("test.xml", repo);
		Analyser an= new Analyser(mi, repo);
		an.visit(new Transformer(repo, output));
		
	}
	
	private static URL convertToURL(String path) throws MalformedURLException {
		File file= new File(path);
		URL url= null;
		if (file.isDirectory()) {
			url= file.toURL();
		} else if (path.endsWith("jar") || path.endsWith("zip")) {
			url= new URL("jar:"+file.toURL()+"!/");
		}
		return url;
		
	}
}