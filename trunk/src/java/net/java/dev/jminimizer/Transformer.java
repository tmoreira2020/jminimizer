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
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.MethodObserver;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.PUTSTATIC;
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
    
    private ConstantPoolGen po;

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
                        if (Analyser.runtimeClassLoaderMethods.contains(m)) {
                            org.apache.bcel.classfile.Method mc= m.toClassFileMethod();
                            cg.replaceMethod(mc ,this.tran(m.toMethodGen()));
                            if (po != null) {
                                cg.setConstantPool(po);
                            }
                        }
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
    
    private org.apache.bcel.classfile.Method tran(MethodGen mg) throws Exception {
        mg.addObserver(new MethodObserver() {
           
        /**
         * @see org.apache.bcel.generic.MethodObserver#notify(org.apache.bcel.generic.MethodGen)
         */
        public void notify(MethodGen method) {
            System.out.println(method);

        } 
        });
        InstructionList list= mg.getInstructionList();
        InstructionHandle[] ins= list.getInstructionHandles();
//        System.out.println(""+ins.length +" " +(ins.length == list.getLength()));
        int j;
        InstructionHandle start= null, end= null;
        for (j= 0; j < ins.length; j++) {
            if (ins[j].getInstruction() instanceof GETSTATIC 
                    && ins[j+1].getInstruction() instanceof DUP
                    && ins[j+2].getInstruction() instanceof IFNONNULL
                    && ins[j+3].getInstruction() instanceof POP) {
                start= ins[j];
                end= ins[j+14];
                System.out.println(start);
                System.out.println(end);
                //TODO transformar este trecho de codigo em um método synthetic
                break;
            }
        }
        System.out.println(list.size());
        if (j != ins.length) {
            System.out.println("CODIGO");
            for (int i=j; i < j+15; i++) {
//                System.out.println(ins[i].toString());
                if (ins[i].hasTargeters()) {
                    InstructionTargeter[] tar=ins[i].getTargeters();
                    for (int k = 0; k < tar.length; k++) {
//                        System.out.println(tar[k]);
						if (tar[k] instanceof CodeExceptionGen) {
					        mg.removeExceptionHandler((CodeExceptionGen)ins[i].getTargeters()[k]);
						}
				        ins[i].removeAllTargeters();
					}
                }
            }
            System.out.println("CODIGO");
//            System.out.println(j);
            System.out.println(mg);
            System.out.println("ANTES\n"+ list.toString(true));
            ConstantPoolGen pool= mg.getConstantPool();
            int x= pool.addString("ljfm");
            System.out.println();
            System.out.println(x);
            LDC ldc= new LDC(x);
            x= pool.addFieldref("Teste1", "x", "Ljava/lang/Class;");
            System.out.println(x);
            ACONST_NULL ac= new ACONST_NULL();
            list.insert(start, ac);
//            list.insert(start, new PUTSTATIC(x));
            list.delete(start, end);
            list.setPositions(true);
            System.out.println("DEPOIS\n"+ list.toString(true));
            po= pool= new ConstantPoolGen(pool.getFinalConstantPool());
            //System.out.println(pool);
            mg.setConstantPool(pool);
            LocalVariableTable lct= mg.getLocalVariableTable(pool);
            if (lct.getLength() != 0) {
                System.out.println(lct.getLength());
                LocalVariable[] lvs= lct.getLocalVariableTable();
	            mg.removeLocalVariables();
                for (int i = 0; i < lvs.length; i++) {
    	            LocalVariable lc= lct.getLocalVariable(i);
    	            System.out.println(lc);
    	            lc.setStartPC(0);
    	            System.out.println(lc);
    	            mg.addLocalVariable(lc.getName(), Type.getType(lc.getSignature()), list.getStart(), list.getEnd());
                }
            }
            System.out.println(lct);
            mg.setInstructionList(list);
            System.out.println();
            ins= list.getInstructionHandles();
            mg.removeLineNumbers();
            for (int i = 0; i < ins.length; i++) {
                mg.addLineNumber(ins[i], 3);
}
            po= pool= new ConstantPoolGen(pool.getFinalConstantPool());
            mg.setConstantPool(po);
            }
        mg.setMaxStack();
        mg.setMaxLocals();
        return mg.getMethod();
    }
}