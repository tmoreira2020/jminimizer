package net.java.dev.jminimizer;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.java.dev.jminimizer.util.Configurator;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.URLRepository;
import net.java.dev.jminimizer.util.Visitor;
import net.java.dev.jminimizer.util.XMLConfigurator;

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
        File file= (File)cl.getOptionObject('c');
        if (!file.exists()) {
        	System.out.println("Configuration file not found: " +file);
        	System.exit(0);
		}
		Configurator configurator= new XMLConfigurator(file);
		Repository repo= new URLRepository(configurator.getProgramClasspath(), configurator.getRuntimeClasspath());
		System.out.println("Analysing...");
		Analyser an= new Analyser(configurator, repo);
		Set usedMethods= new HashSet();
		an.analyse(configurator.getMethodsToInspect(), usedMethods);
		System.out.println("Transforming...");
		Set methodsThatUseClassForName= an.getMethodsThatUseClassForName();
		Visitor visitor= new Transformer(configurator, repo, usedMethods, methodsThatUseClassForName);
		an.receiveVisitor(visitor);
		visitor.finish();
	}
	
	/**
	 * 
	 * @return
	 */	
	private static Options getOptions() {
	    Options opts= new Options();
	    //configFile option
	    Option op= new Option("c", "config", true, "Configuration file.");
	    op.setType(File.class);
	    op.setRequired(true);
	    opts.addOption(op);
	    return opts;
	}
}