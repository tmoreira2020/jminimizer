package net.java.dev.jminimizer;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.OutputProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * @author Thiago Le�o Moreira
 * @since Apr 16, 2004
 *  
 */
public class Transformer implements Visitor {
	private static final Log log = LogFactory.getLog(Transformer.class);
	private MethodInspector mi;
	private Repository repo;
	private File directory;
	private Set classes;
	private Set usedMethods;
	/**
	 *  
	 */
	public Transformer(Set usedMethods, MethodInspector mi, Repository repo, File directory) {
		super();
		this.usedMethods = usedMethods;
		this.mi = mi;
		this.repo = repo;
		this.directory = directory;
		this.classes = repo.getProgramClasses();
	}
	/**
	 * @see net.java.dev.jminimizer.util.Visitor#visit(net.java.dev.jminimizer.beans.Class)
	 */
	public void visit(Class clazz) {
		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String className = clazz.getName();
		Element eClazz = document.createElement("class");
		eClazz.setAttribute("name", className);
		document.appendChild(eClazz);
		if (classes.contains(className)) {
			JavaClass jc = repo.findClass(className);
			ClassGen cg = new ClassGen(jc);
			if (className.indexOf("$") == -1) {
				org.apache.bcel.classfile.Method[] ms = jc.getMethods();
				log.debug("Cleaning class: " + className);
				for (int i = 0; i < ms.length; i++) {
					Method m = new Method(className, ms[i].getName(), ms[i].getSignature());
					if (!usedMethods.contains(m)) {
						//System.out.println("REMOVENDO: " + className + "__"
						// +ms[i]);
						cg.removeMethod(ms[i]);
						if (true) {
							Element eMethod = document.createElement("method");
							eMethod.setAttribute("name", ms[i].getName());
							Element eArguments = document.createElement("arguments");
							Type[] args = Type.getArgumentTypes(ms[i].getSignature());
							for (int j = 0; j < args.length; j++) {
								Element eArg;
								if (args[j] instanceof BasicType) {
									eArg = document.createElement("primitiveType");
								} else {
									eArg = document.createElement("classType");
								}
								eArg.setAttribute("name", args[j].toString());
								eArguments.appendChild(eArg);
							}
							Element eReturn = document.createElement("return");
							Type type = Type.getReturnType(ms[i].getSignature());
							if (type.getType() == Constants.T_VOID) {
								eReturn.appendChild(document.createElement("void"));
							} else {
								Element temp = null;
								if (type instanceof BasicType) {
									temp = document.createElement("primitiveType");
								} else {
									temp = document.createElement("classType");
								}
								temp.setAttribute("name", type.toString());
								eReturn.appendChild(temp);
							}
							eMethod.appendChild(eArguments);
							eMethod.appendChild(eReturn);
							eClazz.appendChild(eMethod);
						}
					}
					/*
					 * try { if (!clazz.containsMethod(ms[i].getName(),
					 * ms[i].getSignature()) && mi.remove(new
					 * net.java.dev.jminimizer.beans.Method(clazz.getName(),
					 * ms[i].getName(), ms[i].getSignature()))) {
					 * log.debug("Removing method: " + ms[i]);
					 * System.out.println("Removendo: " + ms[i]);
					 * cg.removeMethod(ms[i]); } } catch (ClassNotFoundException
					 * e1) { // TODO Auto-generated catch block
					 * e1.printStackTrace(); }
					 */
				}
				org.apache.bcel.classfile.Field[] fs = jc.getFields();
				for (int i = 0; i < fs.length; i++) {
					if (!clazz.containsField(fs[i].getName(), fs[i].getSignature())) {
						log.debug("Removing field: " + fs[i]);
						cg.removeField(fs[i]);
					}
				}
				cg.update();
			}
			File file = new File(directory, className.replace('.', File.separatorChar).concat(".class"));
			log.debug("Dumping class: " + className + " to file: " + file);
			try {
				cg.getJavaClass().dump(file);
			} catch (IOException e) {
				log.error("Error on dumping class: " + className + " to file: " + file, e);
			}
			try {
				javax.xml.transform.Transformer trans = TransformerFactory.newInstance().newTransformer();
				trans.setOutputProperty(OutputKeys.INDENT, "yes");
				trans.transform(new DOMSource(document), new StreamResult(new File("report", className + ".xml")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.debug("\n\n");
	}
}