package net.java.dev.jminimizer.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

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
 * @since May 11, 2004
 */
public class XMLConfigurator implements Configurator {

    public static final String ELEMENT_DIRECTORY = "directory";

    public static final String ELEMENT_FILE = "file";

    public static final String ELEMENT_FILESET = "fileset";

    public static final String ELEMENT_PATTERN = "pattern";

    public static final String ELEMENT_PROGRAM_CLASSPATH = "programClasspath";

    public static final String ELEMENT_RUNTIME_CLASSPATH = "runtimeClasspath";

    public static final String ELEMENT_URL = "url";

    private static final Log log = LogFactory.getLog(XMLConfigurator.class);

    private Set methods;

    private Map primitives;

    private Set programClasspath;

    private RE reNotInspect;

    private File reportDiretory;

    private Source resportStyleSheet;

    private Set runtimeClasspath;

    private File transformationDiretory;

    /**
     *  
     */
    public XMLConfigurator(File file) throws Exception {
        super();
        methods = new HashSet();
        reportDiretory = new File("report");
        this.initPrimitives();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new XMLErrorHandler());
        Document document = builder.parse(file);
        this.normalize(document);
        Element root = document.getDocumentElement();
        programClasspath = this.buildClasspath(root,
                XMLConfigurator.ELEMENT_PROGRAM_CLASSPATH);
        runtimeClasspath = this.buildClasspath(root,
                XMLConfigurator.ELEMENT_RUNTIME_CLASSPATH);
        methods = this.buildMethodsToInspect(root
                .getElementsByTagName("inspect"));
        reNotInspect= this.buildNotInspectPattern(root.getElementsByTagName("notInspect"));
        transformationDiretory= this.buildTransformationOutput((Element)root.getElementsByTagName("output").item(0));
    }
    /**
     * 
     * @param eOutput
     * @return
     */
    private File buildTransformationOutput(Element eOutput) throws IOException {
        File output;
        if (eOutput == null) {
            output= new File("out.jar");
            if (!output.exists()) {
                output.createNewFile();
            }
        } else {
            Element element= (Element) eOutput.getFirstChild();
            if (element.getNodeName().equals("file")) {
                output= new File(element.getAttribute("name"));
                if (!output.exists()) {
                    output.createNewFile();
                }
            } else {
                output= new File(element.getAttribute("path"));
                if (!output.exists()) {
                    output= new File("output");
                    output.mkdir();
                    log.warn("The output file don't exist using default: " +output.getAbsolutePath());
                }
            }
        }
        return output;
    }
    /**
     * 
     * @param root
     * @param elementName
     * @return
     */
    private Set buildClasspath(Element root, String elementName) {
        Set classpath = new HashSet();
        NodeList list = root.getElementsByTagName(elementName);
        for (int i = 0; i < list.getLength(); i++) {
            Element eClasspath = (Element) list.item(i);
            classpath.addAll(this.buildURLs(eClasspath
                    .getElementsByTagName(XMLConfigurator.ELEMENT_URL)));
            classpath
                    .addAll(this
                            .buildDirectorys(eClasspath
                                    .getElementsByTagName(XMLConfigurator.ELEMENT_DIRECTORY)));
            classpath.addAll(this.buildFileset(eClasspath
                    .getElementsByTagName(XMLConfigurator.ELEMENT_FILESET)));
        }
        return classpath;
    }

    /**
     * @param className
     * @param constructorElement
     */
    private Method buildConstructor(String className, Element constructorElement) {
        String[] args = this.getArgumentClasses(constructorElement
                .getFirstChild().getChildNodes());
        Constructor m = new Constructor(className, args);
        log.debug("Constructor builded: " + m);
        return m;
    }

    /**
     * @param lDirectory
     * @return
     */
    private Set buildDirectorys(NodeList lDirectory) {
        Set directories = new HashSet();
        for (int i = 0; i < lDirectory.getLength(); i++) {
            Element eDirectory = (Element) lDirectory.item(i);
            File directory = new File(eDirectory.getAttribute("path"));
            if (directory.exists()) {
                try {
                    directories.add(directory.toURL());
                } catch (MalformedURLException e) {
                    log.error("Error creating url: " + directory, e);
                }
            } else {
                log.warn("The directory not exist: " + directory
                        + " and don't was add to classpath!");
            }
        }
        return directories;
    }

    private Set buildFileset(NodeList lFileset) {
        Set files = new HashSet();
        for (int i = 0; i < lFileset.getLength(); i++) {
            Element eFileset = (Element) lFileset.item(i);
            File directory = new File(eFileset.getAttribute("directory"));
            if (directory.exists()) {
                NodeList lFile = eFileset.getChildNodes();
                for (int j = 0; j < lFile.getLength(); j++) {
                    Element eFile = (Element) lFile.item(j);
                    File file = new File(directory, eFile.getAttribute("name"));
                    if (file.exists()) {
                        try {
                            files.add(new URL("jar:" + file.toURL() + "!/"));
                        } catch (MalformedURLException e) {
                            log.error("Error creating url: " + file, e);
                        }
                    } else {
                        log.warn("The file don't exist: " + file
                                + " and don't was add to classpath!");
                    }
                }
            } else {
                log.warn("The directory not exist: " + directory
                        + " and don't was add to classpath!");
            }
        }
        return files;
    }

    /**
     * @param className
     * @param methodElement
     * @return @throws
     *         ClassNotFoundException
     */
    private Method buildMethod(String className, Element methodElement) {
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
     * @param root
     */
    private Set buildMethodsToInspect(NodeList lInspect) {
        Set methods = new HashSet();
        for (int i = 0; i < lInspect.getLength(); i++) {
            Element eInspect = (Element) lInspect.item(i);
            NodeList lClass = eInspect.getChildNodes();
            for (int j = 0; j < lClass.getLength(); j++) {
                Element eClass = (Element) lClass.item(j);
                String className = eClass.getAttribute("name");
                NodeList lConstructors = eClass
                        .getElementsByTagName("constructor");
                for (int k = 0; k < lConstructors.getLength(); k++) {
                    Element eConstructor = (Element) lConstructors.item(k);
                    methods.add(this.buildConstructor(className, eConstructor));
                }
                NodeList lMethods = eClass.getElementsByTagName("method");
                for (int k = 0; k < lMethods.getLength(); k++) {
                    Element eMethod = (Element) lMethods.item(k);
                    methods.add(this.buildMethod(className, eMethod));
                }
            }
        }
        return methods;
    }

    /**
     * @param element
     */
    private RE buildNotInspectPattern(NodeList lNotInspect) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < lNotInspect.getLength(); i++) {
            Element eNotInspect= (Element) lNotInspect.item(i);
            NodeList lPattern= eNotInspect.getChildNodes();
            for (int j = 0; j < lPattern.getLength(); j++) {
                String temp = lPattern.item(j).getFirstChild().getNodeValue().trim();
                if (temp.startsWith("*") && temp.endsWith("*")) {
                    buffer.append(ClassUtils.normalize(temp));
                } else if (temp.startsWith("*")) {
                    buffer.append(ClassUtils.normalize(temp + "$"));
                } else if (temp.endsWith("*")) {
                    buffer.append(ClassUtils.normalize("^" + temp));
                }
                buffer.append('|');
            }
        }
        if (buffer.length() != 0) {
            buffer.deleteCharAt(buffer.length()-1);
        } else {
            buffer.append("\\b9876546321\\b");
        }
        log.debug("Pattern compiled: " + buffer.toString());
        return new RE(buffer.toString());
    }

    /**
     * @param lURL
     */
    private Set buildURLs(NodeList lURL) {
        Set urls = new HashSet();
        for (int i = 0; i < lURL.getLength(); i++) {
            Node nURL = lURL.item(i);
            String url = nURL.getFirstChild().getNodeValue();
            try {
                urls.add(new URL(url));
            } catch (MalformedURLException e) {
                log.error("Error creating url: " + url, e);
            }
        }
        return urls;
    }

    /**
     * @param list
     * @return @throws
     *         ClassNotFoundException
     */
    private String[] getArgumentClasses(NodeList list) {
        return this.getClasses(list);
    }

    /**
     * @param list
     */
    private String[] getClasses(NodeList list) {
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
     * @see net.java.dev.jminimizer.util.Configurator#getMethodsToInspect()
     */
    public Method[] getMethodsToInspect() {
        return (Method[]) methods.toArray(new Method[0]);
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#getProgramClasspath()
     */
    public URL[] getProgramClasspath() {
        return (URL[]) programClasspath.toArray(new URL[0]);
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#getReportDirectory()
     */
    public File getReportDirectory() {
        return reportDiretory;
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#getReportStyleSheet()
     */
    public Source getReportStyleSheet() {
        return resportStyleSheet;
    }

    /**
     * @param element
     */
    private String getReturnClass(Element element) {
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
     * @see net.java.dev.jminimizer.util.Configurator#getRuntimeClasspath()
     */
    public URL[] getRuntimeClasspath() {
        return (URL[]) runtimeClasspath.toArray(new URL[0]);
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#getTransformationOutput()
     */
    public File getTransformationOutput() {
        return transformationDiretory;
    }

    /**
     * 
     *  
     */
    private void initPrimitives() {
        primitives = new HashMap();
        primitives.put("boolean", Boolean.TYPE.getName());
        primitives.put("byte", Byte.TYPE.getName());
        primitives.put("short", Short.TYPE.getName());
        primitives.put("char", Character.TYPE.getName());
        primitives.put("int", Integer.TYPE.getName());
        primitives.put("float", Float.TYPE.getName());
        primitives.put("long", Long.TYPE.getName());
        primitives.put("double", Double.TYPE.getName());
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#inspect(net.java.dev.jminimizer.beans.Method)
     *  
     */
    public boolean inspect(Method method) {
        return this.inspect(method.toPattern());
    }

    /**
     * @see net.java.dev.jminimizer.util.Configurator#inspect(java.lang.String)
     */
    public boolean inspect(String pattern) {
        boolean b = !reNotInspect.match(pattern);
        log.debug("Inspect " + pattern + " ? = " + pattern);
        return b;
    }

    /**
     * @param node
     */
    private void normalize(Node node) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength();) {
            Node item = list.item(i);
            if ((item.getNodeType() == Node.TEXT_NODE && item.getNodeValue()
                    .trim().length() == 0)
                    || item.getNodeType() == Node.COMMENT_NODE) {
                node.removeChild(item);
            } else {
                i++;
                this.normalize(item);
            }
        }
    }

}