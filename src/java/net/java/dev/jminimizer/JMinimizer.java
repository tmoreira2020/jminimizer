package net.java.dev.jminimizer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.URLRepository;
import net.java.dev.jminimizer.util.XMLMethodInspector;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * @author Thiago Leão Moreira
 * @since Apr 18, 2004
 *  
 */
public class JMinimizer {
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */	
	public static void main(String[] args) throws Exception{
	    CommandLineParser parser= new BasicParser();
	    CommandLine cl= null;;
	    try {
            cl= parser.parse(getOptions(), args);
        } catch (ParseException e) {
            HelpFormatter hf= new HelpFormatter();
            hf.printHelp("net.java.dev.jminimizer.JMinimizer", getOptions());
            System.exit(0);
        }
        String[] paths= cl.getOptionValues('p');
		URL[] program= new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            program[i]= convertToURL(paths[i]);
        }
        URL[] runtime= new URL[0];
        paths= cl.getOptionValues('r');
        if (paths != null) {
            runtime= new URL[paths.length];
            for (int i = 0; i < paths.length; i++) {
                runtime[i]= convertToURL(paths[i]);
            }
        }
		Repository repo= new URLRepository(program, runtime);
//		JavaClass jc= repo.loadClass(JMinimizer.class);
//		System.out.println(jc.getMethods()[1].getCode());
		MethodInspector mi= new XMLMethodInspector((File)cl.getOptionObject('c'), repo);
		System.out.println("Analysing...");
		Analyser an= new Analyser(mi, repo);
		Set methods= new HashSet();
		an.analyse(mi.getMethodsToInspect(), methods);
		System.out.println();
//		System.out.println(methods);
		System.out.println();

	    File output= new File("output");
		if (cl.hasOption('o')) {
			output= (File)cl.getOptionObject('o');
		}
		System.out.println("Transforming...");
		an.receiveVisitor(new Transformer(methods, mi, repo, output));
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL convertToURL(String path) throws MalformedURLException {
	    File file= new File(path);
		URL url= null;
		if (file.isDirectory()) {
			url = file.toURL();
		} else if (path.endsWith("jar") || path.endsWith("zip")) {
			url = new URL("jar:" + file.toURL() + "!/");
		}
		return url;
	}

	/**
	 * 
	 * @return
	 */	
	private static Options getOptions() {
	    Options opts= new Options();
	    //output option
	    Option op= new Option("o", "output", true, "Path to output the generated classes.");
	    op.setType(File.class);
	    opts.addOption(op);
	    //configFile option
	    op= new Option("c", "config", true, "Configuration file.");
	    op.setType(File.class);
	    op.setRequired(true);
	    opts.addOption(op);
	    //program classes option
	    op= new Option("p", "program", true, "Program classes or lib to analyse.");
	    op.setValueSeparator(File.pathSeparatorChar);
	    op.setRequired(true);
	    op.setArgs(Option.UNLIMITED_VALUES);
	    opts.addOption(op);
	    //runtime classes option
	    op= new Option("r", "runtime", true, "Runtime classes or lib to help wiht analysis.");
	    op.setValueSeparator(File.pathSeparatorChar);
	    op.setRequired(false);
	    op.setArgs(Option.UNLIMITED_VALUES);
	    opts.addOption(op);
	    return opts;
	}
}