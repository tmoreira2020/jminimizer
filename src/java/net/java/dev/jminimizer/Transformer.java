package net.java.dev.jminimizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.Configurator;
import net.java.dev.jminimizer.util.Repository;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Thiago Leão Moreira
 * @since Apr 16, 2004
 *  
 */
public class Transformer implements Visitor {

    private static final Log log = LogFactory.getLog(Transformer.class);

    private Set classes;

    private Configurator configurator;

    private ZipOutputStream out;

    private Repository repo;

    private Set usedMethods;

    /**
     *  
     */
    public Transformer(Set usedMethods, Configurator configurator,
            Repository repo) throws IOException {
        super();
        this.usedMethods = usedMethods;
        this.configurator = configurator;
        this.repo = repo;
        this.classes = repo.getProgramClasses();
        File output = configurator.getTransformationOutput();
        if (output.isFile()) {
            out = new ZipOutputStream(new FileOutputStream(output, false)) {

                /**
                 * @see java.util.zip.ZipOutputStream#close()
                 */
                public void close() throws IOException {
                    this.closeEntry();
                }
            };
        }
    }

    private void dump(JavaClass jc) throws IOException {
        String classFile = jc.getClassName().replace('.', File.separatorChar)
                .concat(".class");
        OutputStream stream;
        if (out == null) {
            File file = new File(configurator.getTransformationOutput(),
                    classFile);
            file.mkdirs();
            stream = new FileOutputStream(file);
        } else {
            ZipEntry entry = new ZipEntry(classFile);
            out.putNextEntry(entry);
            stream = out;
        }
        log.debug("Dumping class: " + classFile);
        jc.dump(stream);
    }

    public void finish() throws IOException {
        if (out != null) {
            out.finish();
        }
    }

    private Element reportMethod(Document document,
            org.apache.bcel.classfile.Method method) {
        Element eMethod = document.createElement("method");
        eMethod.setAttribute("name", method.getName());
        Element eArguments = document.createElement("arguments");
        Type[] args = Type.getArgumentTypes(method.getSignature());
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
        Type type = Type.getReturnType(method.getSignature());
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
        return eMethod;
    }

    /**
     * @see net.java.dev.jminimizer.util.Visitor#visit(net.java.dev.jminimizer.beans.Class)
     */
    public void visit(String className) throws Exception {
        Document document = null;
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument();
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
                    Method m = new Method(className, ms[i].getName(), ms[i]
                            .getSignature());
                    if (!usedMethods.contains(m)) {
                        cg.removeMethod(ms[i]);
                        if (true) {
                            eClazz.appendChild(this.reportMethod(document,
                                    ms[i]));
                        }
                    } else {
                        cg.removeAttribute(ms[i].getLineNumberTable());
                        cg.removeAttribute(ms[i].getLocalVariableTable());
                    }
                }
            } else {
                //Classes com nomes com o caracter $
                //System.out.println(className);
            }
            Attribute[] attrs = cg.getAttributes();
            for (int i = 0; i < attrs.length; i++) {
                if (attrs[i] instanceof SourceFile) {
                    //TODO
                    //cg.removeAttribute(attrs[i]);
                } else {
                    //System.out.println(attrs[i]);
                }
            }
            cg.update();
            this.dump(cg.getJavaClass());
            if (eClazz.hasChildNodes()) {
                File directory = configurator.getReportDirectory();
                directory.mkdirs();
                File report = new File(directory, className + ".xml");
                javax.xml.transform.Transformer trans = TransformerFactory
                        .newInstance().newTransformer();
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                trans.transform(new DOMSource(document), new StreamResult(
                        report));
            }
        }
        log.debug("\n\n");
    }
}