package net.java.dev.jminimizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.TargetLostException;
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
    
    public static final String METHOD_SYNTHETIC_NAME= "SYNTHETIC$JMINIMIZER";

    private Set classes;

    private Configurator configurator;

    private ZipOutputStream out;

    private Repository repo;

    private Set usedMethods;
    
    private String classname;
    
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
                            ConstantPoolGen pool= cg.getConstantPool();
                            if (classname == null) {
                                classname= m.getClassName();
                                cg.addMethod(this.createMethod(m.getClassName(), pool).getMethod());
                            }
                            cg.replaceMethod(mc ,this.tran(m.toMethodGen(), pool));
                            cg.setConstantPool(pool);
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
    
    private org.apache.bcel.classfile.Method tran(MethodGen method, ConstantPoolGen pool) throws Exception {
        Attribute[] attrs= method.getAttributes();
        InstructionList code= method.getInstructionList();
        InstructionHandle[] ins= code.getInstructionHandles();
        InstructionHandle start= null, end= null;
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof Synthetic && !method.getName().equals(METHOD_SYNTHETIC_NAME)) {
                this.updateConstantPool(pool, method.getClassName());
                InstructionHandle temp= code.getStart();
                method.removeExceptionHandlers();
/*                while (temp != null) {
                    if (temp.hasTargeters()) {
                        InstructionTargeter[] tar=temp.getTargeters();
                        for (int k = 0; k < tar.length; k++) {
                            System.out.println(temp);
                            temp.removeAllTargeters();
        				}
                    }
                    temp= temp.getNext();
                }
  */              System.out.println("SINTETICO");
                InstructionFactory factory= new InstructionFactory(pool);
                start= code.getStart();
                temp= start.getNext();
                code.insert(temp, this.getin(pool, classname));
                code.insert(temp, new ARETURN());
                try {
                    code.delete(temp, end= code.getEnd());
                } catch (TargetLostException e) {
                    InstructionHandle[] targets = e.getTargets();
               	 	for(int d=0; d < targets.length; d++) {
               	 	    InstructionTargeter[] targeters = targets[d].getTargeters();
               	 	    for(int j=0; j < targeters.length; j++) {
                   	 	    System.out.println(targeters[j]);
               	 	        targeters[j].updateTarget(targets[d], code.getEnd());
               	 	    }
                    }                
                    e.printStackTrace();
               	}
                System.out.println(code);
                break;
            }
        }
        int j;
        for (j= 0; j < ins.length; j++) {
            if ((j < (ins.length-4)) && ins[j].getInstruction() instanceof GETSTATIC 
                    && ins[j+1].getInstruction() instanceof DUP
                    && ins[j+2].getInstruction() instanceof IFNONNULL
                    && ins[j+3].getInstruction() instanceof POP
                	&& ins[j+4].getInstruction() instanceof LDC) {
                start= ins[j+5];
                end= ins[j+14];
                System.out.println(start.toString());
                System.out.println(end);
                this.updateMethod(start, end, code, pool, method);
                this.updateConstantPool(pool, method.getClassName());

                //TODO transformar este trecho de codigo em um método synthetic
            }
        }
        LocalVariableTable lct= method.getLocalVariableTable(pool);
        if (lct.getLength() != 0) {
            LocalVariable[] lvs= lct.getLocalVariableTable();
            method.removeLocalVariables();
            for (int i = 0; i < lvs.length; i++) {
	            LocalVariable lc= lct.getLocalVariable(i);
	            System.out.println(lc);
	            if (lc == null) {
	                continue;
	            }
	            lc.setStartPC(0);
	            method.addLocalVariable(lc.getName(), Type.getType(lc.getSignature()), code.getStart(), code.getEnd());
            }
        }
        return method.getMethod();
    }
    
    
    private MethodGen createMethod(String className, ConstantPoolGen pool) {
        int accessFlag= Constants.ACC_STATIC | Constants.ACC_PUBLIC;
        Type returnType= Type.getType("Ljava/lang/Class;");
        InstructionList code= new InstructionList();
        InstructionFactory factory= new InstructionFactory(pool);
        Instruction i= InstructionFactory.createLoad(Type.STRING, 0);
        code.append(i);
        int out = pool.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
        int println = pool.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
        code.append(new GETSTATIC(out));
        code.append(new ALOAD(0));
        code.append(new INVOKEVIRTUAL(println));
        i= factory.createInvoke("java.lang.Class", "forName", returnType, new Type[]{Type.STRING}, Constants.INVOKESTATIC);
        code.append(i);
        code.append(factory.createPrintln("FOIIIIII"));
        i= InstructionFactory.createReturn(returnType);
        code.append(i);
        i=  InstructionFactory.createStore(Type.OBJECT, 1);
        InstructionHandle errorHandler= code.append(i);
        i= factory.createNew(new ObjectType("java.lang.NoClassDefFoundError"));
        code.append(i);
        code.append(new DUP());
        i= InstructionFactory.createLoad(Type.OBJECT, 1);
        code.append(i);
        i= factory.createInvoke("java.lang.Throwable", "getMessage", Type.STRING, new Type[0], Constants.INVOKEVIRTUAL);
        code.append(i);
        i= factory.createInvoke("java.lang.NoClassDefFoundError", "<init>", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESPECIAL);
        code.append(i);
        code.append(new ATHROW());
        MethodGen mg= new MethodGen(accessFlag, returnType, new Type[]{Type.STRING}, new String[]{"className"}, METHOD_SYNTHETIC_NAME, className, code, pool);
        mg.addExceptionHandler(code.getStart(), code.getEnd(), errorHandler, new ObjectType("java.lang.ClassNotFoundException"));
       mg.setMaxStack();
        mg.setMaxLocals();
        int x= pool.addConstant(new ConstantUtf8("Synthetic"), pool);
        System.out.println(x);
        mg.addAttribute(new Synthetic(x, 0, null, pool.getConstantPool()));
        return mg;
    }
    
    private InstructionList getin(ConstantPoolGen pool, String className) {
        InstructionList list= new InstructionList();
        InstructionFactory i= new InstructionFactory(pool);
        list.append(i.createInvoke(className, METHOD_SYNTHETIC_NAME, new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, Constants.INVOKESTATIC));
        return list;
        
    }
    
    private void updateMethod(InstructionHandle start, InstructionHandle end, InstructionList code, ConstantPoolGen pool, MethodGen method) throws TargetLostException {
        InstructionHandle temp= start;
        while (temp != end) {
            if (temp.hasTargeters()) {
                InstructionTargeter[] tar=temp.getTargeters();
                for (int k = 0; k < tar.length; k++) {
                    method.removeExceptionHandlers();
                    temp.removeAllTargeters();
				}
            }
            temp= temp.getNext();
        }
        code.insert(start, this.getin(pool, classname));
        temp= start.getNext().getNext().getNext();
        System.out.println(temp);
        code.delete(temp, end);
        code.delete(start);
        method.setConstantPool(pool);
        method.setInstructionList(code);
    }
    
    private void updateConstantPool(ConstantPoolGen pool, String className) {
        if (!classname.equals(className)) {
            System.out.println(pool.getSize());
            for (int j = 0; j < pool.getSize(); j++) {
                Constant cons= pool.getConstant(j);
                if (cons instanceof ConstantMethodref) {
                    ConstantMethodref cm= (ConstantMethodref) cons;
                    String cn= cm.getClass(pool.getConstantPool());
                    ConstantNameAndType cnt= (ConstantNameAndType)pool.getConstant(cm.getNameAndTypeIndex());
                    String name= cnt.getName(pool.getConstantPool());
                    String signature= cnt.getSignature(pool.getConstantPool());
                    if (name.equals("forName") && cn.equals("java.lang.Class") && signature.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                        System.out.println(className);
                        System.out.println("REIRANDO: " +cm);
                        int f= pool.addNameAndType("toString", "()Ljava/lang/String;");
                        System.out.println("INDEX: " + f);
                        cm.setNameAndTypeIndex(f);
                        break;
                    }
                }
            }
        }

    }
    
}