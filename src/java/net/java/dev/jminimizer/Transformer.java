package net.java.dev.jminimizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CodeExceptionGen;
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
import org.apache.bcel.generic.LocalVariableGen;
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
            File file= new File(configurator.getTransformationOutput(), classFile);
            File directory= file.getParentFile();
            if (directory != null) {
                directory.mkdirs();
            }
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
//            if (className.indexOf("$") == -1) {
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
                    }
                }
                Field[] fields= jc.getFields();
                for (int i = 0; i < fields.length; i++) {
                    net.java.dev.jminimizer.beans.Field field= new net.java.dev.jminimizer.beans.Field(className, fields[i].getName(), fields[i].getSignature());
                    if(!usedMethods.contains(field)) {
                        if (className.indexOf("TextScanner") != -1) {
                            System.out.println("DELETAR field"+field);
                        }
                        cg.removeField(fields[i]);
                    }
                }
      
  //          } else {
                //Classes com nomes com o caracter $
                //System.out.println(className);
    //        }
            Attribute[] attrs = cg.getAttributes();
            for (int i = 0; i < attrs.length; i++) {
                if (attrs[i] instanceof SourceFile) {
                    //TODO
                    //cg.removeAttribute(attrs[i]);
                } else {
                    //System.out.println(attrs[i]);
                }
            }
            this.updateClassGen(cg);
            cg.update();
            cg.getJavaClass().accept(new ConstantPoolCleanerVisitor());
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
        //method.stripAttributes(true);
        Attribute[] attrs= method.getAttributes();
        InstructionList code= method.getInstructionList();
        InstructionHandle[] ins= code.getInstructionHandles();
        InstructionHandle start= null, end= null;
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof Synthetic && !method.getName().equals(METHOD_SYNTHETIC_NAME)) {
                this.updateConstantPool(pool, method.getClassName());
                InstructionHandle temp= code.getStart();
                //method.removeExceptionHandlers();
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
  */              //System.out.println("SINTETICO");
                InstructionFactory factory= new InstructionFactory(pool);
                start= code.getStart();
                temp= start.getNext();
                code.insert(temp, this.getin(pool, classname));
                InstructionHandle newEnd= code.insert(temp, new ARETURN());
                try {
                    code.delete(temp, end= code.getEnd());
                } catch (TargetLostException e) {
                    //System.out.println("ERRRRRO");
                    InstructionHandle[] targets = e.getTargets();
               	 	for(int d=0; d < targets.length; d++) {
               	 	    InstructionTargeter[] targeters = targets[d].getTargeters();
               	 	    for(int j=0; j < targeters.length; j++) {
               	 	        if (targeters[j] instanceof CodeExceptionGen) {
               	 	            method.removeExceptionHandler((CodeExceptionGen)targeters[j]);
               	 	          //System.out.println("è CODEEXCPTIONGEN");  
               	 	        }
               	 	        if (targeters[j] instanceof LocalVariableGen) {
               	 	            LocalVariableGen lvg= (LocalVariableGen) targeters[j];
               	 	            //TODO
           	 	                method.removeLocalVariable(lvg);
               	 	            LocalVariable lv= lvg.getLocalVariable(pool);
               	 	            if (lv.getStartPC() == 0) {
               	 	                lvg.setEnd(newEnd);
               	 	                //lvg= method.addLocalVariable(lvg.getName(), lvg.getType(), lv.getNameIndex(), lvg.getStart(), newEnd);
               	 	            } else {
               	 	                method.removeLocalVariable(lvg);
               	 	            }
               	 	        }
               	 	        //targeters[j].updateTarget(targets[d], code.getEnd());
               	 	    }
                    }                
                    //e.printStackTrace();
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
                this.updateMethod(start, end, code, pool, method);
                this.updateConstantPool(pool, method.getClassName());

                //TODO transformar este trecho de codigo em um método synthetic
            }
        }
/*        LocalVariableTable lct= method.getLocalVariableTable(pool);
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
  */      return method.getMethod();
    }
    
    
    private MethodGen createMethod(String className, ConstantPoolGen pool) {
        int accessFlag= Constants.ACC_STATIC | Constants.ACC_PUBLIC;
        Type returnType= Type.getType("Ljava/lang/Class;");
        InstructionList code= new InstructionList();
        InstructionFactory factory= new InstructionFactory(pool);
        Instruction i= InstructionFactory.createLoad(Type.STRING, 0);
        InstructionHandle startPC;
        startPC= code.append(i);
        //startPC= code.append(new LDC(pool.addString("java.lang.Net")));
        i= factory.createInvoke("java.lang.Class", "forName", returnType, new Type[]{Type.STRING}, Constants.INVOKESTATIC);
        InstructionHandle endPC =code.append(i);
        int out = pool.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
        int println = pool.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
//        code.append(new DUP());
        code.append(new GETSTATIC(out));
        code.append(new ALOAD(0));
        code.append(new INVOKEVIRTUAL(println));
        code.append(factory.createPrintln("FOIIIIII"));
        i= InstructionFactory.createReturn(returnType);
        code.append(i);
        i=  InstructionFactory.createStore(Type.OBJECT, 1);
        InstructionHandle handlerPC=code.append(i);
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
        CodeExceptionGen ceg= mg.addExceptionHandler(startPC, endPC, handlerPC, new ObjectType("java.lang.ClassNotFoundException"));
       mg.setMaxStack();
        mg.setMaxLocals();
        int x= pool.addConstant(new ConstantUtf8("Synthetic"), pool);
        mg.removeLineNumbers();
//        System.out.println(x);
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
/*        while (temp != end) {
            if (temp.hasTargeters()) {
                InstructionTargeter[] tar=temp.getTargeters();
                for (int k = 0; k < tar.length; k++) {
                    method.removeExceptionHandlers();
                    temp.removeAllTargeters();
				}
            }
            temp= temp.getNext();
        }
*/
        code.insert(start, this.getin(pool, classname));
        temp= start.getNext().getNext().getNext();
//        System.out.println(temp);
        try {
            code.delete(temp, end);
        } catch (TargetLostException e) {
            InstructionHandle[] handles= e.getTargets();
            for (int i = 0; i < handles.length; i++) {
                InstructionTargeter[] targets= handles[i].getTargeters();
                for (int j = 0; j < targets.length; j++) {
                    if (targets[j] instanceof CodeExceptionGen) {
                        CodeExceptionGen ceg= (CodeExceptionGen) targets[j];
                        method.removeExceptionHandler(ceg);
                    }
                }
            }
        }
        
        try {
            code.delete(start);
        } catch (TargetLostException e) {
            InstructionHandle[] handles= e.getTargets();
            for (int i = 0; i < handles.length; i++) {
                InstructionTargeter[] targets= handles[i].getTargeters();
                for (int j = 0; j < targets.length; j++) {
                    if (targets[j] instanceof CodeExceptionGen) {
                        CodeExceptionGen ceg= (CodeExceptionGen) targets[j];
                        method.removeExceptionHandler(ceg);
                    }
                }
            }
        }
        method.setConstantPool(pool);
        method.setInstructionList(code);
    }
    
    private void updateConstantPool(ConstantPoolGen pool, String className) {
/*        if (!classname.equals(className)) {
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
*/
    }
    
    private Constant[] lookupClass(String className, ConstantPoolGen pool) {
        Constant[] ret= new Constant[2];
        int index= pool.lookupClass(className);
        ret[1]= pool.getConstant(index);
        ret[0]= pool.getConstant(((ConstantClass)pool.getConstant(index)).getNameIndex());
        return ret;
    }
    
    private void updateClassGen(ClassGen cg) {
        Set general= new HashSet();
        ConstantPoolGen pool= cg.getConstantPool();
        Constant[] intArray= this.lookupClass(cg.getClassName(), pool);
        for (int i = 0; i < intArray.length; i++) {
            general.add(intArray[i]);
        }
        intArray= this.lookupClass(cg.getSuperclassName(), pool);
        for (int i = 0; i < intArray.length; i++) {
            general.add(intArray[i]);
        }
        String[] interfaces= cg.getInterfaceNames();
        for (int i = 0; i < interfaces.length; i++) {
            intArray= this.lookupClass(interfaces[i], pool);
            for (int j = 0; j < intArray.length; j++) {
                general.add(intArray[j]);
            }
        }
        int index= pool.lookupUtf8("L"+cg.getClassName()+";");
        if (index != -1) {
            general.add(pool.getConstant(index));
        }
        index= pool.lookupUtf8(cg.getFileName());
        if (index != -1) {
            general.add(pool.getConstant(index));
        }
        
        org.apache.bcel.classfile.Method[] methods= cg.getMethods();
        for (int i = 0; i < methods.length; i++) {
    	    Set temp= new HashSet();
    	    map.put(methods[i], temp);
           	temp.add(pool.getConstant(methods[i].getNameIndex()));
        	temp.add(pool.getConstant(methods[i].getSignatureIndex()));
        	LocalVariableTable lvt= methods[i].getLocalVariableTable();
        	if (lvt != null) {
	        	LocalVariable[] lvs= lvt.getLocalVariableTable();
	        	for (int j = 0; j < lvs.length; j++) {
	                temp.add(pool.getConstant(lvs[j].getNameIndex()));
	                temp.add(pool.getConstant(lvs[j].getSignatureIndex()));
	            }
        	}
        	ExceptionTable et= methods[i].getExceptionTable();
        	if (et != null) {
        	    int[] eit= et.getExceptionIndexTable();
        	    for (int j = 0; j < eit.length; j++) {
        	        ConstantClass cc= (ConstantClass) pool.getConstant(eit[j]);
        	        temp.add(cc);
        	        temp.add(pool.getConstant(cc.getNameIndex()));
                }
        	}
        	MethodGen mg= new MethodGen(methods[i],cg.getClassName(),  pool);
        	CodeExceptionGen[] cges= mg.getExceptionHandlers();
        	for (int j = 0; j < cges.length; j++) {
                ConstantClass cc= (ConstantClass) pool.getConstant(cges[j].getCodeException(pool).getCatchType());
                if (cc != null) {
                    temp.add(cc);
                    temp.add(pool.getConstant(cc.getNameIndex()));
                }
            }
            if (!(methods[i].isAbstract() || methods[i].isNative())) {
                //System.out.println(methods[i]);
                //System.out.println(methods[i].getCode().getAttributes().length);
	            InstructionList list= new InstructionList(methods[i].getCode().getCode());
	            Instruction[] instructions= list.getInstructions();
	            for (int j = 0; j < instructions.length; j++) {
	                index= -1;
//                    System.out.println(instructions[j].getClass());
	                if (instructions[j] instanceof CPInstruction) {
	                    CPInstruction cpi= (CPInstruction) instructions[j];
	                    Constant constant= pool.getConstant(cpi.getIndex());
	                    temp.add(constant);
	                    //System.out.println(constant);
	                    switch (constant.getTag()) {
	                    case Constants.CONSTANT_InterfaceMethodref:
	                    case Constants.CONSTANT_Methodref:
	                    case Constants.CONSTANT_Fieldref:{
	                        ConstantCP ccp= (ConstantCP) constant;
	                        ConstantClass cc= (ConstantClass) pool.getConstant(ccp.getClassIndex());
	                        temp.add(cc);
	                        temp.add(pool.getConstant(cc.getNameIndex()));
	                    	ConstantNameAndType cnt= (ConstantNameAndType) pool.getConstant(ccp.getNameAndTypeIndex());
	                    	temp.add(cnt);
	                    	temp.add(pool.getConstant(cnt.getNameIndex()));
	                    	temp.add(pool.getConstant(cnt.getSignatureIndex()));
	                    }
                        break;
                        case Constants.CONSTANT_Class: {
    	                    ConstantClass cclass= (ConstantClass)constant;
	                    	temp.add(pool.getConstant(cclass.getNameIndex()));
                        }
                        break;
                        case Constants.CONSTANT_String:
	                        ConstantString cs= (ConstantString) constant;
                    		temp.add(pool.getConstant(cs.getStringIndex()));
	                    default:
	                        
	                        break;
	                    }
	                }
	            }
            }
        }
        Field[] fields= cg.getFields();
        for (int i = 0; i < fields.length; i++) {
            //fields[i].getNameIndex()
        }
        for (int i = 0; i < CONSTANTS_IN_POOL.length; i++) {
            index= pool.lookupUtf8(CONSTANTS_IN_POOL[i]) ; 
            if (index != -1) {
                general.add(pool.getConstant(index));
            }
        }
        int x= pool.getSize();
        int t= 0;
        if (cg.getClassName().endsWith(".TextScanner")) {
            //System.out.println(cg.getClassName());
            //System.out.println(methods.length);
        }
        
        for (int i = 0; i < x; i++) {
            boolean used= false;
            Constant constant= pool.getConstant(i);
            if (constant == null) {
//                System.out.println("ENTRUPOUOUOUPOUPOIUOIU");
                continue;
            }
            for (int j = 0; j < methods.length; j++) {
//        	    System.out.println("H2"+new Method(cg.getClassName(), methods[j]).hashCode());
                Set constants= (Set)map.get(methods[j]);
                if (constants.contains(constant)) {
                    used= true;
                    break;
                }
            }
            if (i == 533) {
                //System.out.println(pool.getConstant(532));
                //System.out.println(constant);
                //System.out.println(pool.getConstant(534));
            }
            if (!used && !general.contains(constant)) {
                t++;
                pool.setConstant(i, EMPTY);
                switch (constant.getTag()) {
            	case Constants.CONSTANT_Double:
            	case Constants.CONSTANT_Long:
            	case Constants.CONSTANT_Float: {
            	    //System.out.println("DOOOOOOOOOOOOOOOOOOOOOOBLU");
            	    pool.setConstant(i+1, EMPTY);
            	    
            	}
                }
                if (cg.getClassName().endsWith(".TextScanner")) {
                    System.out.println("NÂO UTILIZADA:" + i+ ")" + constant);
                }
            }
        }
        if (cg.getClassName().endsWith(".TextScanner")) {
          System.out.println("QUANTIDADE not util: " +t);
          System.out.println(classname);
        }
    }
    
    Map map= new HashMap();
    
    static Constant EMPTY= new ConstantUtf8("");
    
    private static final String[] CONSTANTS_IN_POOL= {"Synthetic", "SourceFile", "Code", "LineNumberTable", "LocalVariableTable", "Exceptions", "this", "LocalVariables", "ConstantValue"};
    
}