package net.java.dev.jminimizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.java.dev.jminimizer.beans.Class;
import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.FieldOrMethod;
import net.java.dev.jminimizer.beans.Method;
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
import org.apache.bcel.util.InstructionFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class Analyser {
	private static final Log log = LogFactory.getLog(Analyser.class);
	protected Map abstractClasses;
	protected MethodInspector inspecter;
	protected SortedSet limitsMethods;
	protected Set othersMethods;
	protected net.java.dev.jminimizer.util.Repository repository;
	protected Map sourceClasses;
	protected Map targetClasses;
	/**
	 * @param inspecter
	 * @param repo
	 * @throws ClassNotFoundException
	 */
	public Analyser(MethodInspector inspecter,
			net.java.dev.jminimizer.util.Repository repo)
			throws ClassNotFoundException {
		super();
		limitsMethods = new TreeSet(FieldOrMethod.COMPARATOR);
		abstractClasses = new HashMap();
		targetClasses = new HashMap();
		sourceClasses = new HashMap();
		othersMethods = new HashSet();
		this.inspecter = inspecter;
		this.repository = repo;
		Repository.setRepository(this.repository);
		Class[] ac = inspecter.getRuntimeLoadedClass();
		for (int i = 0; i < ac.length; i++) {
			abstractClasses.put(ac[i].getName(), ac[i]);
			this.buildSourceHierachyClassTree(repo.loadClass(ac[i].getName()));
		}
		this.analyse(inspecter.getMethodsToInspect());
	}
	/**
	 * @return @throws
	 *         ClassNotFoundException
	 */
	private Set analiseAbstractMethods() throws ClassNotFoundException {
		Iterator ii = abstractClasses.keySet().iterator();
		Set overridedMethods = new HashSet();
		while (ii.hasNext()) {
			Class baseClass = (Class) abstractClasses.get(ii.next());
			Class[] sub = ((Class) sourceClasses.get(baseClass.getName()))
					.getSubClasses();
			for (int i = 0; i < sub.length; i++) {
				Set ms = this.getOverridedMethods(baseClass, sub[i]);
				overridedMethods.addAll(ms);
				log.debug("Find " + ms.size() + " overrided methods in sub class: " + sub[i].getName() + " from super class: " + baseClass.getName());
			}
		}
		abstractClasses.clear();
		return overridedMethods;
	}
	/**
	 * @param method
	 * @throws ClassNotFoundException
	 */
	public void analyse(Method method) throws ClassNotFoundException {
		this.analyse(method.toMethodGen());
		Set forgotten = new HashSet();
		forgotten.addAll(this.analiseAbstractMethods());
		abstractClasses.clear();
		forgotten.addAll(this.othersMethods);
		this.othersMethods.clear();
		Iterator i = forgotten.iterator();
		while (i.hasNext()) {
			Method m = (Method) i.next();
			this.analyse(m);
		}
	}
	/**
	 * @param ms
	 * @throws ClassNotFoundException
	 */
	public void analyse(Method[] ms) throws ClassNotFoundException {
		for (int i = 0; i < ms.length; i++) {
			this.analyse(ms[i]);
		}
	}
	/**
	 * @param method
	 * @throws ClassNotFoundException
	 */
	private void analyse(MethodGen method) throws ClassNotFoundException {
		String className = method.getClassName();
		Class clazz = (Class) targetClasses.get(className);
		if (clazz == null) {
			//TODO talvez possa ser melhorado
			if (className.indexOf("$") != -1) {
				clazz = repository.loadClass(className, true);
				Method[] ms = clazz.getMethods();
				for (int i = 0; i < ms.length; i++) {
					othersMethods.add(ms[i]);
				}
			}
			log.debug("Loading new class: " + className);
			clazz = repository.loadClass(className, false);
			targetClasses.put(className, clazz);
			JavaClass jc = repository.loadClass(className);
			this.buildSourceHierachyClassTree(jc);
		}
		if (clazz.containsMethod(method)) {
			return;
		} else {
			clazz.add(new Method(method));
			String[] ex = method.getExceptions();
			for (int i = 0; i < ex.length; i++) {
				targetClasses.put(ex[i], repository.loadClass(ex[i], false));
			}
		}
		if (!method.isAbstract()) {
			JavaClass javaClass = repository.loadClass(method.getClassName());
			ConstantPoolGen pool = new ConstantPoolGen(javaClass
					.getConstantPool());
			Set mgs = this.analyseInvokeInstruction(this.findInstructions(
					method, "InvokeInstruction"), pool);
			if (mgs.size() != 0) {
				Iterator i = mgs.iterator();
				while (i.hasNext()) {
					Method m = (Method) i.next();
					this.analyse(m.toMethodGen());
				}
			} else {
				limitsMethods.add(new Method(method));
			}
			this.analyseFieldInstruction(this.findInstructions(method,
					"FieldInstruction"), pool);
		} else {
			Class ac = (Class) abstractClasses.get(className);
			if (ac == null) {
				ac = new Class(className);
				abstractClasses.put(className, ac);
			}
			Method m= new Method(method); 
			ac.add(m);
			log.debug("Find a abtract method: " + m);
		}
	}
	/**
	 * 
	 * @param instructions
	 * @param pool
	 * @throws ClassNotFoundException
	 */
	private void analyseFieldInstruction(Set instructions, ConstantPoolGen pool)
			throws ClassNotFoundException {
		Iterator i = instructions.iterator();
		while (i.hasNext()) {
			FieldInstruction element = (FieldInstruction) i.next();
			FieldGen f = this.findField(element, pool);
			String className = element.getClassName(pool);
			JavaClass jc = repository.loadClass(className);
			//used to adds <clinit> to list of methods to analyse
			this.buildSourceHierachyClassTree(jc);
			Class clazz = this.getClass(targetClasses, className);
			clazz.add(new Field(className, f.getName(), f.getSignature()));
		}
	}
	/**
	 * 
	 * @param instructions
	 * @param pool
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Set analyseInvokeInstruction(Set instructions, ConstantPoolGen pool)
			throws ClassNotFoundException {
		Iterator i = instructions.iterator();
		Set methods = new HashSet();
		while (i.hasNext()) {
			InvokeInstruction element = (InvokeInstruction) i.next();
			Method method = this.findMethod(element, pool);
			if (method != null) {
				if (inspecter.inspect(method)) {
					methods.add(method);
				} else {
					limitsMethods.add(method);
				}
			}
		}
		return methods;
	}
	/**
	 * @param clazz
	 * @throws ClassNotFoundException
	 */
	private void buildSourceHierachyClassTree(JavaClass clazz)
			throws ClassNotFoundException {
		Class baseClass = (Class) sourceClasses.get(clazz.getClassName());
		if (baseClass == null) {
			baseClass = repository.loadClass(clazz.getClassName(), true);
			if (baseClass.containsMethod("<clinit>", "()V")) {
				othersMethods.add(new Method(baseClass.getName(), "<clinit>",
						"()V"));
			}
			sourceClasses.put(clazz.getClassName(), baseClass);
			JavaClass sjc = clazz.getSuperClass();
			if (sjc != null) {
				Class superClass = (Class) sourceClasses
						.get(sjc.getClassName());
				if (superClass == null) {
					superClass = repository.loadClass(sjc.getClassName(), true);
					this.buildSourceHierachyClassTree(sjc);
					sourceClasses.put(sjc.getClassName(), superClass);
				}
				superClass.addSubClass(baseClass);
				baseClass.setSuperClass(superClass);
			}
			JavaClass[] is = clazz.getAllInterfaces();
			for (int i = 0; i < is.length; i++) {
				Class interClass = (Class) sourceClasses.get(is[i]
						.getClassName());
				if (interClass == null) {
					interClass = repository.loadClass(is[i].getClassName(),
							true);
					if (interClass.containsMethod("<clinit>", "()V")) {
						othersMethods.add(new Method(interClass.getName(),
								"<clinit>", "()V"));
					}
					this.buildSourceHierachyClassTree(is[i]);
					sourceClasses.put(is[i].getClassName(), interClass);
					targetClasses.put(is[i].getClassName(), new Class(is[i]
							.getClassName()));
				}
				interClass.addSubClass(baseClass);
			}
		}
	}
	/**
	 * 
	 * @param fi
	 * @param pool
	 * @return
	 * @throws ClassNotFoundException
	 */
	private FieldGen findField(FieldInstruction fi, ConstantPoolGen pool)
			throws ClassNotFoundException {
		return ClassUtils.findField(fi.getClassName(pool), fi.getName(pool));
	}
	/**
	 * 
	 * @param method
	 * @param instructionPattern
	 * @return
	 */
	private Set findInstructions(MethodGen method, String instructionPattern) {
		Set instructions = new InstructionSet();
		//check if exist code
		if (!method.isNative()) {
			InstructionFinder finder = new InstructionFinder(method
					.getInstructionList());
			Iterator i = finder.search(instructionPattern);
			while (i.hasNext()) {
				InstructionHandle[] ih = (InstructionHandle[]) i.next();
				if (ih.length != 1) {
					throw new RuntimeException(
							"Just one instruction must return!");
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
	/**
	 * 
	 * @param ii
	 * @param pool
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Method findMethod(InvokeInstruction ii, ConstantPoolGen pool)
			throws ClassNotFoundException {
		return ClassUtils.findMethod(ii.getClassName(pool), ii.getName(pool),
				ii.getSignature(pool));
	}
	/**
	 * 
	 * @param classes
	 * @param className
	 * @return
	 */
	private Class getClass(Map classes, String className) {
		Class clazz = (Class) classes.get(className);
		if (clazz == null) {
			clazz = new Class(className);
			classes.put(className, clazz);
		}
		return clazz;
	}
	/**
	 * 
	 * @param className
	 * @return
	 */
	private Class getClass(String className) {
		return this.getClass(targetClasses, className);
	}
	/**
	 * @return Returns the classes.
	 */
	public Map getClasses() {
		return this.targetClasses;
	}
	/**
	 * @return Returns the inspecter.
	 */
	public MethodInspector getInspecter() {
		return this.inspecter;
	}
	/**
	 * @return
	 */
	public SortedSet getLimitsMethods() {
		return limitsMethods;
	}
	/**
	 * 
	 * @param baseClass
	 * @param subClass
	 * @return
	 */
	private Set getOverridedMethods(Class baseClass, Class subClass) {
		Set ms = baseClass.getOverridedMethods(subClass.getMethods());
		if (ms.size() == 0) {
			subClass = subClass.getSuperClass();
			if (subClass != null) {
				ms = this.getOverridedMethods(baseClass, subClass);
			}
		}
		return ms;
	}
	/**
	 * @return Returns the repository.
	 */
	public net.java.dev.jminimizer.util.Repository getRepository() {
		return this.repository;
	}
	/**
	 * 
	 * @param visitor
	 */
	public void visit(Visitor visitor) {
		Collection col = targetClasses.values();
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			visitor.visit((Class) iter.next());
		}
	}
}