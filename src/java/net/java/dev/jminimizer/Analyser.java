package net.java.dev.jminimizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.ClassUtils;
import net.java.dev.jminimizer.util.InstructionSet;
import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.InstructionFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Le�o Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class Analyser {
	private static final Log log = LogFactory.getLog(Analyser.class);
	protected MethodInspector inspecter;
	protected net.java.dev.jminimizer.util.Repository repository;
	protected Set notProcessedMethods;
	protected Map classes;
	/**
	 * @param inspecter
	 * @param repo
	 * @throws ClassNotFoundException
	 */
	public Analyser(MethodInspector inspecter, net.java.dev.jminimizer.util.Repository repo) throws ClassNotFoundException {
		super();
		this.inspecter = inspecter;
		this.repository = repo;
		this.notProcessedMethods = new HashSet();
		this.classes = new HashMap();
		Repository.setRepository(this.repository);
	}
	/**
	 * @param methods
	 * @return @throws
	 *         ClassNotFoundException
	 */
	public void analyse(Method[] methods, Set usedMethods) throws ClassNotFoundException {
		for (int i = 0; i < methods.length; i++) {
			this.analyse(methods[i], "", usedMethods);
		}
		this.analyseOverridedMethods(usedMethods);
		int size = notProcessedMethods.size();
		if (size != 0) {
			methods = new Method[size];
			notProcessedMethods.toArray(methods);
			notProcessedMethods.clear();
			this.analyse(methods, usedMethods);
		}
	}
	/**
	 * 
	 * @param methods
	 * @throws ClassNotFoundException
	 */
	private void analyseOverridedMethods(final Set methods) throws ClassNotFoundException {
		this.receiveVisitor(new Visitor() {
			public void visit(Class clazz) {
				if (clazz.getName().indexOf("Listener") != -1) {
					//System.out.println(clazz.getName());
					Class[] tem = clazz.getSubClasses();
					for (int i = 0; i < tem.length; i++) {
						//						System.out.println("    "+tem[i].getName());
					}
					//				System.out.println();
				}
				Class[] subs = clazz.getSubClasses();
				for (int i = 0; i < subs.length; i++) {
					Iterator iterator = clazz.getOverridedMethods(subs[i].getMethods()).iterator();
					while (iterator.hasNext()) {
						Method method = (Method) iterator.next();
						Method methodInSuperClass = new Method(clazz.getName(), method.getName(), method.getSignature());
						if (subs[i].getName().indexOf("$") != -1) {
							methods.add(methodInSuperClass);
						}
						if (methods.contains(methodInSuperClass) && !methods.contains(method)) {
							notProcessedMethods.add(method);
							//if (subs[i].getName().indexOf("InstructionSet") != -1) {
							//System.out.println(methodInSuperClass);
							//System.out.println(clazz.getSuperClass().size());
							//}
						} else {
							if (!methods.contains(method) && clazz.getName().indexOf("java.awt.") != -1) {
								if (subs[i].getName().indexOf("KComponent") != -1) {
									System.out.println(methodInSuperClass);
									System.out.println(method);
									System.out.println();
								}
							}
						}
					}
				}
			}
		});
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}

	/**
	 * @param method
	 * @param methods
	 * @return @throws
	 *         ClassNotFoundException
	 */
	public void analyse(Method method, String tab, Set methods) throws ClassNotFoundException {
		if (method.getName().equals("getContextClassLoader")) {
			//		System.out.println("ACHOU");
			//	System.out.println(methods.contains(method));
		}
		if (method.getClassName().indexOf("Factory$") != -1) {
			/// System.out.println(method);
		}
		if (!methods.contains(method)) {
			methods.add(method);
			MethodGen mg = method.toMethodGen();
			JavaClass jc = repository.loadClass(mg.getClassName());
			this.buildHierachyTree(jc);
			if (mg.isNative()) {
				System.out.println("nativo");
				return;
			}
			if (mg.isAbstract()) {
				//System.out.println("abstrato");
				return;
			}
			Instruction[] instructions = this.findInvokeInstructions(mg);
			ConstantPoolGen pool = mg.getConstantPool();
			Method[] ms = this.instructionToMethod((InvokeInstruction[]) instructions, pool);
			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getName().equals("forName") && ms[i].getClassName().equals("java.lang.Class")) {
					System.out.println("WARNING: " + method);
				}
				if (inspecter.inspect(ms[i])) {
					//System.out.println(tab+ms[i]);
					this.analyse(ms[i], tab + "  ", methods);
				} else {
					//					System.out.println(ms[i]);
					methods.add(ms[i]);
				}
			}
			instructions = this.findFieldInstructions(mg);
			for (int i = 0; i < instructions.length; i++) {
				FieldInstruction fi = (FieldInstruction) instructions[i];
				String className = fi.getClassName(pool);
				methods.add(new Field(className, fi.getName(pool), fi.getSignature(pool)));
				if (fi.getOpcode() == Constants.GETSTATIC) {
					System.out.println(fi.getName(pool));
					System.out.println(fi.getSignature(pool));
					System.out.println();
				}
				if (fi.getSignature(pool).indexOf("PopupWindow") != -1) {
					//System.out.println(className);
					//System.out.println(fi.getSignature(pool));
				}
				jc = repository.loadClass(className);
				this.buildHierachyTree(jc);
			}
		}
	}
	/**
	 * @param method
	 * @return
	 */
	private FieldInstruction[] findFieldInstructions(MethodGen method) {
		return (FieldInstruction[]) this.findInstructions(method, "FieldInstruction").toArray(new FieldInstruction[0]);
	}
	/**
	 * @param method
	 * @param instructionPattern
	 * @return
	 */
	private InvokeInstruction[] findInvokeInstructions(MethodGen method) {
		return (InvokeInstruction[]) this.findInstructions(method, "InvokeInstruction").toArray(new InvokeInstruction[0]);
	}
	/**
	 * 
	 * @param method
	 * @param instructionPattern
	 * @return
	 */
	private Set findInstructions(MethodGen method, String instructionPattern) {
		Set instructions = new InstructionSet();
		InstructionFinder finder = new InstructionFinder(method.getInstructionList());
		Iterator i = finder.search(instructionPattern);
		while (i.hasNext()) {
			InstructionHandle[] ih = (InstructionHandle[]) i.next();
			if (ih.length != 1) {
				throw new RuntimeException("Just one instruction must return!");
			} else {
				Instruction instruction = ih[0].getInstruction();
				instructions.add(instruction);
			}
		}
		return instructions;
	}
	/**
	 * @param instructions
	 * @param pool
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private Method[] instructionToMethod(InvokeInstruction[] instructions, ConstantPoolGen pool) throws ClassNotFoundException {
		Set methods = new HashSet();
		for (int i = 0; i < instructions.length; i++) {
			Method method = ClassUtils.findMethod(instructions[i].getClassName(pool), instructions[i].getName(pool), instructions[i].getSignature(pool));
			if (method == null) {
				System.out.println(instructions[i].getClassName(pool));
				System.out.println(instructions[i].getName(pool));
				System.out.println(instructions[i].getSignature(pool));
				System.out.println();
			} else {
				methods.add(method);
			}
		}
		return (Method[]) methods.toArray(new Method[0]);
	}
	/**
	 * @param clazz
	 */
	private void buildHierachyTree(JavaClass clazz) throws ClassNotFoundException {
		Class baseClass = (Class) classes.get(clazz.getClassName());
		if (baseClass == null) {
			baseClass = repository.loadClass(clazz.getClassName(), true);
			//System.out.println(baseClass.getName());
			if (baseClass.containsMethod("<clinit>", "()V")) {
				notProcessedMethods.add(new Method(baseClass.getName(), "<clinit>", "()V"));
			}
			classes.put(clazz.getClassName(), baseClass);
			JavaClass sjc = clazz.getSuperClass();
			if (sjc != null) {
				Class superClass = (Class) classes.get(sjc.getClassName());
				if (superClass == null) {
					superClass = repository.loadClass(sjc.getClassName(), true);
					this.buildHierachyTree(sjc);
					classes.put(sjc.getClassName(), superClass);
				}
				superClass.addSubClass(baseClass);
				baseClass.addSuperClass(superClass);
			}
			JavaClass[] is = clazz.getAllInterfaces();
			if (clazz.getClassName().indexOf("TextController") != -1) {
				//System.out.println(is.length);
			}
			for (int i = 0; i < is.length; i++) {
				Class interClass = (Class) classes.get(is[i].getClassName());
				if (interClass == null) {
					interClass = repository.loadClass(is[i].getClassName(), true);

					//TODO construir um plugin para tirar isso daqui
					if (interClass.getName().indexOf("java.awt.event") != -1) {
						org.apache.bcel.classfile.Method[] ms = clazz.getMethods();
						for (int j = 0; j < ms.length; j++) {
							if (interClass.containsMethod(ms[j].getName(), ms[j].getSignature())) {
								Method method = new Method(interClass.getName(), ms[j].getName(), ms[j].getSignature());
								System.out.println(method);
								notProcessedMethods.add(method);
							}
						}
					}
					//TODO construir um plugin para tirar isso daqui

					this.buildHierachyTree(is[i]);
					classes.put(is[i].getClassName(), interClass);
				}
				if (interClass.getName().indexOf("java.awt.event") != -1) {
					//					System.out.println(baseClass.getName() + " --> "+interClass.getName());
				}
				interClass.addSubClass(baseClass);
				baseClass.addSuperClass(interClass);
			}
		} else {
			if (baseClass.getName().indexOf("Instruction") != -1) {
				//				baseClass.debug();
			}
		}
	}
	/**
	 * @param visitor
	 */
	public void receiveVisitor(Visitor visitor) {
		Collection col = classes.values();
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			Class clazz = (Class) iter.next();
			visitor.visit(clazz);
		}
	}

	private void analyseOverridedMethods(String className) throws ClassNotFoundException {
		JavaClass jc = Repository.lookupClass(className);
		List ms = new ArrayList(Arrays.asList(jc.getMethods()));
		int size = ms.size();
		JavaClass sjc = jc.getSuperClass();
		while (sjc != null && ms.size() != 0) {
			an(ms, sjc, className);
			sjc = sjc.getSuperClass();
		}
		JavaClass[] is= jc.getAllInterfaces();
		for (int i = 0; i < is.length && ms.size() != 0; i++) {
			this.an(ms, is[i], className);
		}

	}

	private void an(List ms, JavaClass sjc, String className) {
		for (int i = 0; i < ms.size();) {
			//System.out.println(ms.size());
			//System.out.println(sjc.getClassName());
			org.apache.bcel.classfile.Method mcf = (org.apache.bcel.classfile.Method) ms.get(i);
			Method m = new Method(className, mcf.getName(), mcf.getSignature());
			ClassGen scg = new ClassGen(sjc);
			if (scg.containsMethod(mcf.getName(), mcf.getSignature()) != null) {
				//TODO
				System.out.println("ACHOU: " + m);
				notProcessedMethods.add(m);
				ms.remove(mcf);
			} else {
				i++;
			}
		}
	}
}