package net.java.dev.jminimizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.FieldOrMethod;
import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.ClassLoader;
import net.java.dev.jminimizer.util.ClassUtils;
import net.java.dev.jminimizer.util.InstructionSet;
import net.java.dev.jminimizer.util.MethodInspector;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.InstructionFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Analyser {

	private static final Log log = LogFactory.getLog(Analyser.class);

	protected SortedSet fields;
	protected SortedSet methods;
	protected SortedSet limitsMethods;
	protected MethodInspector inspecter;
	protected Collection methodsToInspect;
	protected Set abstractMethods;
	protected Map clazzs;
	protected ClassLoader loader;

	/**
	 * 
	 */
	public Analyser(MethodInspector inspecter, ClassLoader loader) {
		super();
		methods = new TreeSet(FieldOrMethod.COMPARATOR);
		fields = new TreeSet(FieldOrMethod.COMPARATOR);
		limitsMethods = new TreeSet(FieldOrMethod.COMPARATOR);
		methodsToInspect= new LinkedList();
		abstractMethods= new HashSet();
		clazzs= new HashMap();
		this.inspecter = inspecter;
		this.loader= loader;
		Repository.setRepository(new ClassLoaderRepository(this.loader));
	}

	public void analyse(JavaClass clazz, org.apache.bcel.classfile.Method method) throws ClassNotFoundException{
		ConstantPoolGen pool = new ConstantPoolGen(clazz.getConstantPool());
		methodsToInspect.add(new Method(new MethodGen(method, clazz.getClassName(), pool)));
		Iterator i= methodsToInspect.iterator();
		while (i.hasNext()) {
			Method m= (Method) i.next();
			methods.add(m);
			this.analyse(m.toMethodGen());
		}
		System.out.println("Methods" + methods);
		//now will find classes that implements this abstract methods
		//TODO
		System.out.println("AbstractMethods" + abstractMethods);
		i = loader.getProgramClasses().keySet().iterator();
		while (i.hasNext()) {
			Method m= (Method) i.next();
			System.out.println(m);
		}
	}

	private void analyse(MethodGen method) throws ClassNotFoundException {
		String className= method.getClassName();
		JavaClass javaClass = Repository.lookupClass(className);
		ConstantPoolGen pool = new ConstantPoolGen(javaClass.getConstantPool());
		Class c= (Class)clazzs.get(className);
		if (c == null) {
			c= new Class(className);
			clazzs.put(className, c);
		}
		c.add(new Method(method));
		if (method.isAbstract()) {
			abstractMethods.add(new Method(method));
		} else {
			this.analyseInvokeInstruction(this.findInstructions(method, "InvokeInstruction"), pool);
			this.analyseFieldInstruction(this.findInstructions(method, "FieldInstruction"), pool);
		}
	}
	
	private void analyseFieldInstruction(Set instructions, ConstantPoolGen pool) throws ClassNotFoundException {
		Iterator i = instructions.iterator();
		while (i.hasNext()) {
			FieldInstruction element = (FieldInstruction) i.next();
			FieldGen f = this.findField(element, pool);
			if (fields.add(new Field(element.getClassName(pool), f.getName(), f.getSignature()))) {
				//adicionou as instruções de acesso a atributos de classe
			}
		}
	}

	private void analyseInvokeInstruction(Set instructions, ConstantPoolGen pool) throws ClassNotFoundException {
		Iterator i = instructions.iterator();
		while (i.hasNext()) {
			InvokeInstruction element = (InvokeInstruction) i.next();
			MethodGen m = this.findMethod(element, pool);
			Method method = new Method(m);
			if (methods.add(method)) {
				if (inspecter.inspect(m)) {
					this.analyse(m);
				} else {
					limitsMethods.add(method);
				}
			}
		}
	}

	private FieldGen findField(FieldInstruction fi, ConstantPoolGen pool) throws ClassNotFoundException {
		return ClassUtils.findField(fi.getClassName(pool), fi.getName(pool));
	}

	private Set findInstructions(MethodGen method, String instructionPattern) {
		Set instructions = new InstructionSet();
		//verifica se existe código
		if (!method.isNative()) {
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
		} else {
			limitsMethods.add(new Method(method));
		}
		return instructions;
	}

	private MethodGen findMethod(InvokeInstruction ii, ConstantPoolGen pool) throws ClassNotFoundException {
		return ClassUtils.findMethod(ii.getClassName(pool), ii.getName(pool), ii.getSignature(pool));
	}

	public void visit(Visitor visitor) {
		SortedSet set = new TreeSet(methods);
		set.addAll(fields);
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof Field) {
				visitor.visit((Field) element);
			} else {
				visitor.visit((Method) element);
			}
		}
	}

	/**
	 * @return
	 */
	public SortedSet getLimitsMethods() {
		return limitsMethods;
	}

	public void addMethodsToInspect(Method[] ms) {
		this.methodsToInspect.addAll(Arrays.asList(ms));
	}
	
}
