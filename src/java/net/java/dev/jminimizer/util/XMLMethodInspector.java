package net.java.dev.jminimizer.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.java.dev.jminimizer.beans.Constructor;
import net.java.dev.jminimizer.beans.Method;

import org.apache.bcel.generic.MethodGen;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
;
;

/**
 * @author Thiago Leão Moreira
 *         <thiago.leao.moreira@terra.com.br>
 *  
 */
public class XMLMethodInspector implements MethodInspector {

	private static final Log log = LogFactory.getLog(XMLMethodInspector.class);
	protected RE reNotInspect;
	protected RE reNotRemove;
	protected Set methodsNoRemove;
	protected Map primitives;
	protected Method[] methods;

	/**
	 *  
	 */
	public XMLMethodInspector(String file) throws Exception {
		super();
		this.initPrimitives();
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(file);
		this.normalize(document);
		Element root = document.getDocumentElement();
		this.buildMethodsToInspect(root);
		reNotInspect = this.buildPattern(root, "notInspect");
		methodsNoRemove= new HashSet();
		reNotRemove = this.buildPattern(root, "notRemove");
		
	}

	private void initPrimitives() {
		primitives = new HashMap();
		primitives.put("byte", Byte.TYPE);
		primitives.put("short", Short.TYPE);
		primitives.put("char", Character.TYPE);
		primitives.put("int", Integer.TYPE);
		primitives.put("float", Float.TYPE);
		primitives.put("long", Long.TYPE);
		primitives.put("double", Double.TYPE);
	}

	private RE buildPattern(Element root, String elementName)
		throws ClassNotFoundException {
		Node node = root.getElementsByTagName(elementName).item(0);
		//any pattern to no match with methods
		String pattern = "\\b9876546321\\b";
		if (node != null) {
			pattern = this.buildPattern((Element) node);
		}

		log.debug("Pattern compiled: " + pattern);
		return new RE(pattern);
	}

	private String buildPattern(Element element)
		throws ClassNotFoundException {
		NodeList list = element.getElementsByTagName("class");
		StringBuffer buffer = new StringBuffer();
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element classElement = (Element) list.item(i);
			net.java.dev.jminimizer.beans.Class clazz =
				this.buildClass(classElement);
			Method[] ms = clazz.getMethods();
			int m = ms.length;
			if (m == 0) {
				buffer.append("\\b");
				buffer.append(ClassUtils.normalize(clazz.getName()));
				buffer.append("*");
			} else {
				for (int j = 0; j < m; j++) {
					buffer.append("\\b");
					buffer.append(ClassUtils.normalize(ms[j].toPattern()));
					buffer.append("\\b");
					if (j != (m - 1)) {
						buffer.append('|');
					}
				}
			}
			if (i != (n - 1)) {
				buffer.append('|');
			}
		}
		list = element.getElementsByTagName("pattern");
		n = list.getLength();
		if (n != 0 && buffer.length() != 0) {
			buffer.append('|');
		}
		for (int i = 0; i < n; i++) {
			buffer.append(
				ClassUtils.normalize(list.item(i).getFirstChild().getNodeValue()));
			if (i != (n - 1)) {
				buffer.append('|');
			}
		}
		return buffer.toString();
	}

	private Method buildConstructor(Class clazz, Element constructorElement)
		throws ClassNotFoundException {
		Class[] args =
			this.getArgumentClasses(
				constructorElement.getFirstChild().getChildNodes());
		Constructor m = new Constructor(clazz, args);
		log.debug("Constructor builded: " + m);
		return m;
	}

	private Method buidMethod(Class clazz, Element methodElement)
		throws ClassNotFoundException {
		Node argumentsElement = methodElement.getFirstChild();
		Element returnElement =
			(Element) argumentsElement.getNextSibling().getFirstChild();
		Class[] args =
			this.getArgumentClasses(argumentsElement.getChildNodes());
		Class ret = this.getReturnClass(returnElement);
		Method m =
			new Method(
				clazz,
				methodElement.getAttribute("name"),
				args,
				ret,
				Boolean.valueOf(methodElement.getAttribute("inSubClasses")).booleanValue());
		if (m.isInSubClasses()) {
			methodsNoRemove.add(m.getNameAndSignature());
		}
		log.debug("Method builded: " + m);
		return m;
	}

	private Class[] getArgumentClasses(NodeList list)
		throws ClassNotFoundException {
		return this.getClasses(list);
	}

	private Class[] getClasses(NodeList list) throws ClassNotFoundException {
		Class[] classes = new Class[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			String name = element.getNodeName();
			if (name.equals("primitiveType")) {
				classes[i] =
					(java.lang.Class) primitives.get(
						element.getAttribute("name"));
			} else {
				classes[i] = Class.forName(element.getAttribute("name"));
			}
		}
		return classes;
	}

	private java.lang.Class getReturnClass(Element element)
		throws ClassNotFoundException {
		String name = element.getNodeName();
		if (name.equals("void")) {
			return Void.TYPE;
		} else if (name.equals("primitiveType")) {
			return (java.lang.Class) primitives.get(
				element.getAttribute("name"));
		} else {
			return Class.forName(element.getAttribute("name"));
		}
	}

	/**
	 * @see net.java.dev.jminimizer.util.MethodInspecter#inspect(org.apache.bcel.generic.MethodGen)
	 */
	public boolean inspect(MethodGen method) {
		String s= Method.toPattern(method);
		boolean b= !reNotInspect.match(s); 
		log.debug("Inspect " + s + " ? = " + b);
		return b;
	}

	private void normalize(Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength();) {
			Node item = list.item(i);
			if (item.getNodeType() == Node.TEXT_NODE
				&& item.getNodeValue().trim().length() == 0) {
				node.removeChild(item);
			} else {
				i++;
				this.normalize(item);
			}
		}
	}

	/**
	 * @see net.java.dev.jminimizer.util.MethodInspector#remove(org.apache.bcel.generic.MethodGen)
	 */
	public boolean remove(MethodGen method) throws ClassNotFoundException {
		//test if this is method that was overrided from a super class
		Method m= new Method(method);
		if (methodsNoRemove.contains(m.getNameAndSignature())) {
			if (ClassUtils.isMethodOverridFromSuperClass(method)) {
				log.debug(
						"Method overrided from super class: "
						+ Method.toString(method));
				return false;
			}
		}
		//test if this method is in a pattern
		return !reNotRemove.match(Method.toPattern(method));
	}


	/**
	 * @see net.java.dev.jminimizer.util.MethodInspector#getMethodsToInspect()
	 */
	public Method[] getMethodsToInspect() {
		return methods;
	}

	private net.java.dev.jminimizer.beans.Class buildClass(Element element)
		throws ClassNotFoundException {
		net.java.dev.jminimizer.beans.Class clazz =
			new net.java.dev.jminimizer.beans.Class(element.getAttribute("name"));
		NodeList constructorList = element.getElementsByTagName("constructor");
		int n = constructorList.getLength();
		for (int j = 0; j < n; j++) {
			clazz.add(
				this.buildConstructor(
					clazz.toClass(),
					(Element) constructorList.item(j)));
		}
		NodeList methodList = element.getElementsByTagName("method");
		n = methodList.getLength();
		for (int j = 0; j < n; j++) {
			clazz.add(
				this.buidMethod(clazz.toClass(), (Element) methodList.item(j)));
		}
		return clazz;
	}

	private void buildMethodsToInspect(Element root)
		throws ClassNotFoundException {
		Element inspect =
			(Element) root.getElementsByTagName("inspect").item(0);
		if (inspect != null) {
			NodeList list = inspect.getChildNodes();
			List clazzs = new LinkedList();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					clazzs.add(this.buildClass((Element) node));
				}
			}
			Iterator i = clazzs.iterator();
			List ms = new LinkedList();
			while (i.hasNext()) {
				net.java.dev.jminimizer.beans.Class clazz =
					(net.java.dev.jminimizer.beans.Class) i.next();
				ms.addAll(Arrays.asList(clazz.getMethods()));
			}
			methods = (Method[]) ms.toArray(new Method[0]);
		} else {
			methods = new Method[0];
		}

	}

}
