package net.java.dev.jminimizer.util;
import java.io.File;
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
import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Constructor;
import net.java.dev.jminimizer.beans.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class XMLMethodInspector implements MethodInspector {
	private static final Log log = LogFactory.getLog(XMLMethodInspector.class);
	protected Method[] methods;
	protected Set methodsNoRemove;
	protected Map primitives;
	protected RE reNotInspect;
	protected RE reNotRemove;
	protected Repository repository;
	protected Set runtimeLoadedClass;
	/**
	 *  
	 */
	public XMLMethodInspector(File file, Repository repository)
			throws Exception {
		super();
		this.initPrimitives();
		this.repository = repository;
		runtimeLoadedClass = new HashSet();
		methodsNoRemove = new HashSet();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new XMLErrorHandler());
		Document document = builder.parse(file);
		this.normalize(document);
		Element root = document.getDocumentElement();
		this.buildMethodsToInspect(root);
		reNotInspect = this.buildPattern(root, "notInspect");
		reNotRemove = this.buildPattern(root, "notRemove");
	}
	/**
	 * @param className
	 * @param methodElement
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private Method buidMethod(String className, Element methodElement)
			throws ClassNotFoundException {
		Node argumentsElement = methodElement.getFirstChild();
		Element returnElement = (Element) argumentsElement.getNextSibling()
				.getFirstChild();
		String[] args = this.getArgumentClasses(argumentsElement
				.getChildNodes());
		String ret = this.getReturnClass(returnElement);
		Method m = new Method(className, methodElement.getAttribute("name"),
				args, ret);
		log.debug("Method builded: " + m);
		return m;
	}
	/**
	 * @param element
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private Class buildClass(Element element) throws ClassNotFoundException {
		Class clazz = new Class(element.getAttribute("name"));
		String knowTime = element.getAttribute("knowTime");
		if (knowTime != null && knowTime.equals("runtime")) {
			log.debug("Building runtime loading class: " + clazz.getName());
			runtimeLoadedClass.add(clazz);
		}
		NodeList constructorList = element.getElementsByTagName("constructor");
		int n = constructorList.getLength();
		for (int j = 0; j < n; j++) {
			clazz.add(this.buildConstructor(clazz.getName(),
					(Element) constructorList.item(j)));
		}
		NodeList methodList = element.getElementsByTagName("method");
		n = methodList.getLength();
		for (int j = 0; j < n; j++) {
			clazz.add(this.buidMethod(clazz.getName(), (Element) methodList
					.item(j)));
		}
		return clazz;
	}
	/**
	 * @param className
	 * @param constructorElement
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private Method buildConstructor(String className, Element constructorElement)
			throws ClassNotFoundException {
		String[] args = this.getArgumentClasses(constructorElement
				.getFirstChild().getChildNodes());
		Constructor m = new Constructor(className, args);
		log.debug("Constructor builded: " + m);
		return m;
	}
	/**
	 * @param root
	 * @throws ClassNotFoundException
	 */
	private void buildMethodsToInspect(Element root)
			throws ClassNotFoundException {
		Element inspect = (Element) root.getElementsByTagName("inspect")
				.item(0);
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
				Class clazz = (Class) i.next();
				ms.addAll(Arrays.asList(clazz.getMethods()));
			}
			methods = (Method[]) ms.toArray(new Method[0]);
		} else {
			methods = new Method[0];
		}
	}
	/**
	 * @param element
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private String buildPattern(Element element) throws ClassNotFoundException {
		NodeList list = element.getElementsByTagName("class");
		StringBuffer buffer = new StringBuffer();
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element classElement = (Element) list.item(i);
			Class clazz = this.buildClass(classElement);
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
			String temp = list.item(i).getFirstChild().getNodeValue().trim();
			if (temp.startsWith("*") && temp.endsWith("*")) {
				buffer.append(ClassUtils.normalize(temp));
			} else if (temp.startsWith("*")) {
				buffer.append(ClassUtils.normalize(temp + "$"));
			} else if (temp.endsWith("*")) {
				buffer.append(ClassUtils.normalize("^" + temp));
			}
			if (i != (n - 1)) {
				buffer.append('|');
			}
		}
		return buffer.toString();
	}
	/**
	 * @param root
	 * @param elementName
	 * @return @throws
	 *         ClassNotFoundException
	 */
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
	/**
	 * @param list
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private String[] getArgumentClasses(NodeList list)
			throws ClassNotFoundException {
		return this.getClasses(list);
	}
	/**
	 * @param list
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private String[] getClasses(NodeList list) throws ClassNotFoundException {
		String[] classes = new String[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			String name = element.getNodeName();
			if (name.equals("primitiveType")) {
				classes[i] = (String) primitives.get(element
						.getAttribute("name"));
			} else {
				classes[i] = element.getAttribute("name");
			}
		}
		return classes;
	}
	/**
	 * @see net.java.dev.jminimizer.util.MethodInspector#getMethodsToInspect()
	 */
	public Method[] getMethodsToInspect() {
		return methods;
	}
	/**
	 * @param element
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private String getReturnClass(Element element)
			throws ClassNotFoundException {
		String name = element.getNodeName();
		if (name.equals("void")) {
			return Void.TYPE.getName();
		} else if (name.equals("primitiveType")) {
			return (String) primitives.get(element.getAttribute("name"));
		} else {
			return element.getAttribute("name");
		}
	}
	/**
	 * @see net.java.dev.jminimizer.util.MethodInspector#getRuntimeLoadedClass()
	 */
	public Class[] getRuntimeLoadedClass() {
		return (Class[]) runtimeLoadedClass.toArray(new Class[0]);
	}
	/**
	 * 
	 *  
	 */
	private void initPrimitives() {
		primitives = new HashMap();
		primitives.put("byte", Byte.TYPE.getName());
		primitives.put("short", Short.TYPE.getName());
		primitives.put("char", Character.TYPE.getName());
		primitives.put("int", Integer.TYPE.getName());
		primitives.put("float", Float.TYPE.getName());
		primitives.put("long", Long.TYPE.getName());
		primitives.put("double", Double.TYPE.getName());
	}
	/**
	 * @see net.java.dev.jminimizer.util.MethodInspecter#inspect(net.java.dev.jminimizer.beans.Method)
	 */
	public boolean inspect(Method method) {
		String s = method.toPattern();
		boolean b = !reNotInspect.match(s);
		log.debug("Inspect " + s + " ? = " + b);
		return b;
	}
	/**
	 * @param node
	 */
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
	 * @see net.java.dev.jminimizer.util.MethodInspector#remove(net.java.dev.jminimizer.beans.Method)
	 */
	public boolean remove(Method method) throws ClassNotFoundException {
		//test if this method was overrided from a super class
		System.out.println("foi");
		if (methodsNoRemove.contains(method.getNameAndSignature())) {
			if (ClassUtils.isMethodOverridFromSuperClass(method)) {
				log.debug("Method overrided from super class: " + method);
				return false;
			}
		}
		//test if this method is in a pattern
		return !reNotRemove.match(method.toPattern());
	}
}