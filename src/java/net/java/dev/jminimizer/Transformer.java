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
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IFNONNULL;
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
 * @author Thiago Leão Moreira <thiagolm@dev.java.net>
 * @since Apr 16, 2004
 *  
 */
public class Transformer implements Visitor {
	
	private static final Log log = LogFactory.getLog(Transformer.class);
	
	public static final String SYNTHETIC_METHOD_NAME= "SYNTHETIC$JMINIMIZER";
	
	private Set classes;
	
	private String classThatContainsTheNewMethod;
	
	private Configurator configurator;
	
	private Set methodsThatUseClassForName;
	
	private ZipOutputStream out;
	
	private Repository repository;
	
	private Set usedMethods;
	
	/**
	 * @param configurator
	 * @param repository
	 * @param usedMethods
	 * @param methodsThatUseClassForName
	 * @throws IOException
	 */
	public Transformer(Configurator configurator, Repository repository,
			Set usedMethods, Set methodsThatUseClassForName) throws IOException {
		super();
		this.configurator = configurator;
		this.repository = repository;
		this.usedMethods = usedMethods;
		this.methodsThatUseClassForName= methodsThatUseClassForName;
		this.classes = repository.getProgramClasses();
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
	
	
	/**
	 * @param className
	 * @param pool
	 * @return
	 */
	private MethodGen createMethod(String className, ConstantPoolGen pool) {
		int accessFlag= Constants.ACC_STATIC | Constants.ACC_PUBLIC;
		Type returnType= Type.getType("Ljava/lang/Class;");
		InstructionList code= new InstructionList();
		InstructionFactory factory= new InstructionFactory(pool);
		Instruction i= InstructionFactory.createLoad(Type.STRING, 0);
		InstructionHandle startPC;
		startPC= code.append(i);
		i= factory.createInvoke("java.lang.Class", "forName", returnType, new Type[]{Type.STRING}, Constants.INVOKESTATIC);
		InstructionHandle endPC =code.append(i);
		/*int out = pool.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
		 int println = pool.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
		 code.append(new GETSTATIC(out));
		 code.append(new ALOAD(0));
		 code.append(new INVOKEVIRTUAL(println));
		 code.append(factory.createPrintln("FOIIIIII"));
		 */
		i= InstructionFactory.createReturn(returnType);
		code.append(i);
		i= InstructionFactory.createStore(Type.OBJECT, 1);
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
		MethodGen mg= new MethodGen(accessFlag, returnType, new Type[]{Type.STRING}, new String[]{"className"}, SYNTHETIC_METHOD_NAME, className, code, pool);
		CodeExceptionGen ceg= mg.addExceptionHandler(startPC, endPC, handlerPC, new ObjectType("java.lang.ClassNotFoundException"));
		mg.setMaxStack();
		mg.setMaxLocals();
		int x= pool.addConstant(new ConstantUtf8("Synthetic"), pool);
		mg.removeLineNumbers();
		mg.addAttribute(new Synthetic(x, 0, null, pool.getConstantPool()));
		return mg;
	}
	
	/**
	 * @param jc
	 * @throws IOException
	 */
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
	
	/**
	 * @param pool
	 * @param className
	 * @return
	 */
	private InstructionList buildInstructionListWithCallToNewMethod(ConstantPoolGen pool, String className) {
		InstructionList list= new InstructionList();
		InstructionFactory i= new InstructionFactory(pool);
		list.append(i.createInvoke(className, SYNTHETIC_METHOD_NAME, new ObjectType("java.lang.Class"), new Type[]{Type.STRING}, Constants.INVOKESTATIC));
		return list;
		
	}
	
	/**
	 * @param document
	 * @param method
	 * @return
	 */
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
	
	private void transformBySynthetic(MethodGen method, ConstantPoolGen pool, InstructionList code){
		InstructionHandle start= code.getStart();
		InstructionHandle end= code.getEnd();
		InstructionHandle temp= start.getNext();
		InstructionFactory factory= new InstructionFactory(pool);
		code.insert(temp, this.buildInstructionListWithCallToNewMethod(pool, classThatContainsTheNewMethod));
		InstructionHandle newEnd= code.insert(temp, new ARETURN());
		try {
			code.delete(temp, end);
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
		//System.out.println(code);
	}
	
	/**
	 * @param method
	 * @param pool
	 * @return
	 * @throws Exception
	 */
	private org.apache.bcel.classfile.Method transform(MethodGen method, ConstantPoolGen pool) throws Exception {
		Attribute[] attrs= method.getAttributes();
		InstructionList code= method.getInstructionList();
		InstructionHandle[] ins= code.getInstructionHandles();
		//test if this method is Synthetic
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i] instanceof Synthetic && !method.getName().equals(SYNTHETIC_METHOD_NAME)) {
				this.transformBySynthetic(method, pool, code);
			}
		}
		//test if this method has a sequence os instructions write by compiler
		for (int j= 0; j < ins.length; j++) {
			if ((j < (ins.length-4)) && ins[j].getInstruction() instanceof GETSTATIC 
					&& ins[j+1].getInstruction() instanceof DUP
					&& ins[j+2].getInstruction() instanceof IFNONNULL
					&& ins[j+3].getInstruction() instanceof POP
					&& ins[j+4].getInstruction() instanceof LDC) {
				this.transformByInstructionSequence(ins[j+5], ins[j+14], code, pool, method);
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
	
	private void transformByInstructionSequence(InstructionHandle start, InstructionHandle end, InstructionList code, ConstantPoolGen pool, MethodGen method) throws TargetLostException {
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
		code.insert(start, this.buildInstructionListWithCallToNewMethod(pool, classThatContainsTheNewMethod));
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
	
	/**
	 * @see net.java.dev.jminimizer.util.Visitor#visit(net.java.dev.jminimizer.beans.Class)
	 */
	public void visit(String className) throws Exception {
		if (classes.contains(className)) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			.newDocument();
			Element eClazz = document.createElement("class");
			eClazz.setAttribute("name", className);
			document.appendChild(eClazz);
			JavaClass javaClass = repository.findClass(className);
			ClassGen cg = new ClassGen(javaClass);
			org.apache.bcel.classfile.Method[] ms = javaClass.getMethods();
			log.debug("Cleaning class: " + className);
			for (int i = 0; i < ms.length; i++) {
				Method m = new Method(className, ms[i].getName(), ms[i]
																	 .getSignature());
				//Deleting methods
				if (!usedMethods.contains(m)) {
					log.debug("Removing method: "+ m);
					cg.removeMethod(ms[i]);
					if (true) {
						eClazz.appendChild(this.reportMethod(document,
								ms[i]));
					}
				} else {
					if (methodsThatUseClassForName.contains(m)) {
						org.apache.bcel.classfile.Method mc= m.toClassFileMethod();
						ConstantPoolGen pool= cg.getConstantPool();
						if (classThatContainsTheNewMethod == null) {
							classThatContainsTheNewMethod= m.getClassName();
							cg.addMethod(this.createMethod(m.getClassName(), pool).getMethod());
						}
						cg.replaceMethod(mc ,this.transform(m.toMethodGen(), pool));
					}
				}
			}
			//Deleting fields
			Field[] fields= javaClass.getFields();
			for (int i = 0; i < fields.length; i++) {
				net.java.dev.jminimizer.beans.Field field= new net.java.dev.jminimizer.beans.Field(className, fields[i].getName(), fields[i].getSignature());
				if(!usedMethods.contains(field)) {
					log.debug("Removing field: "+ field);
					cg.removeField(fields[i]);
				}
			}
			log.debug("Cleaning up the constant pool of class: "+ cg.getClassName());
			javaClass= new ConstantPoolCleanerVisitor(configurator.isDeepStripment()).cleanUpClassGen(cg);
			this.dump(javaClass);
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
	}
	
}