package net.java.dev.jminimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.Method;
import net.java.dev.jminimizer.util.ClassUtils;
import net.java.dev.jminimizer.util.Configurator;
import net.java.dev.jminimizer.util.InstructionSet;
import net.java.dev.jminimizer.util.Visitor;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.util.InstructionFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Leão Moreira <thiagolm@dev.java.net>
 *
 *
 * 
 * 
 */
public class Analyser {

    private static final Log log = LogFactory.getLog(Analyser.class);

    protected Set classes;

    protected Configurator configurator;

    protected Set methodsThatUseClassForName;

    protected Set notProcessedMethods;

    protected net.java.dev.jminimizer.util.Repository repository;

    /**
     * @param configurator
     * @param repository
     * @throws ClassNotFoundException
     */
    public Analyser(Configurator configurator,
            net.java.dev.jminimizer.util.Repository repository)
            throws ClassNotFoundException {
        super();
        this.configurator = configurator;
        this.repository = repository;
        this.notProcessedMethods = new HashSet();
        this.classes = new TreeSet(Collections.reverseOrder());
        this.methodsThatUseClassForName = new HashSet();
        Repository.setRepository(this.repository);
    }

    /**
     * @param methods
     * @param superClass
     * @param className
     * @param usedMethods
     */
    private void analiseMethodFromSuperClass(List methods, JavaClass superClass, String className, Set usedMethods) {
        classes.add(superClass.getClassName());
        for (int i = 0; i < methods.size();) {
            org.apache.bcel.classfile.Method mcf = (org.apache.bcel.classfile.Method) methods
                    .get(i);
            Method m = new Method(className, mcf.getName(), mcf.getSignature());
            ClassGen scg = new ClassGen(superClass);
            if (mcf.getName().equals("<cinit>")
                    && mcf.getSignature().equals("()V")) {
                notProcessedMethods.add(m);
                methods.remove(mcf);
                log.trace("Find a class initializer: "+ m);
                continue;
            }
            if (mcf.getName().equals("<init>")) {
                methods.remove(mcf);
                log.trace("Find a default constructor: "+ m);
                continue;
            }
            if (scg.containsMethod(mcf.getName(), mcf.getSignature()) != null
                    && !usedMethods.contains(m)) {
                notProcessedMethods.add(m);
                methods.remove(mcf);
            } else {
                i++;
            }
        }
    }
    
    /**
     * @param method
     * @throws ClassNotFoundException
     */
    private void analiseMethodThatUseClassForName(Method method) throws ClassNotFoundException{
        MethodGen mg= method.toMethodGen();
        if (method.getName().equals(Transformer.SYNTHETIC_METHOD_NAME)) {
            Attribute[] a= mg.getAttributes();
            for (int i = 0; i < a.length; i++) {
            	//if method was the method that centralize calls to java.lang.Class.forName(java.lang.String className) do nothing.
                if (a[i] instanceof Synthetic) {
                    return;
                }
            }
        }
        methodsThatUseClassForName.add(method);
        log.debug("Method that contains a call to java.lang.Class.forName(java.lang.String className): " +method);
    }

    /**
     * @param method
     * @param usedMethods
     * @return @throws
     *         ClassNotFoundException
     */
    public void analyse(Method method, Set usedMethods)
            throws ClassNotFoundException {
        if (!usedMethods.contains(method)) {
            String className= method.getClassName(); 
            if (configurator.inspect(className) && classes.add(className)) {
            	//used just to relax de user
            	log.info("Analysing class: "+method.getClassName());
            }
            usedMethods.add(method);
            MethodGen methodGen = method.toMethodGen();
            if (methodGen.isNative()) {
            	log.trace("Native method: "+ method.toString());
                return;
            }
            if (methodGen.isAbstract()) {
            	log.trace("Abstract method: "+ method.toString());
            	return;
            }
            LoadClass[] loadClasses= this.findLoadClassInstructions(methodGen);  
            ConstantPoolGen pool = methodGen.getConstantPool();
            for (int i = 0; i < loadClasses.length; i++) {
                if (loadClasses[i] instanceof InvokeInstruction) {
                    InvokeInstruction instruction= (InvokeInstruction) loadClasses[i];
                    Method methodTemp= new Method(instruction.getClassName(pool),
                            instruction.getName(pool), instruction.getSignature(pool));
                    if (usedMethods.contains(methodTemp)) {
                        continue;
                    }
                    methodTemp= ClassUtils.findMethod(configurator, classes, instruction.getClassName(pool),
                            instruction.getName(pool), instruction.getSignature(pool));
                    if (methodTemp.getName().equals("forName")
                            && methodTemp.getClassName().equals("java.lang.Class")) {
                        this.analiseMethodThatUseClassForName(method);
                    }
                    // test if this method should be analyse
                    if (configurator.inspect(methodTemp)) {
                        this.analyse(methodTemp, usedMethods);
                    } else {
                        usedMethods.add(methodTemp);
                    }
                } else if (loadClasses[i] instanceof FieldInstruction) {
                    FieldInstruction fieldInstruction = (FieldInstruction) loadClasses[i];
                    className = fieldInstruction.getClassName(pool);
                    usedMethods.add(new Field(className, fieldInstruction.getName(pool), fieldInstruction.getSignature(pool)));
                    this.buildHierachyTree(className);
                } else {
                    ObjectType objectType= loadClasses[i].getLoadClassType(pool);
                    //used to cacth casts from object to array of primitive types (int[], char[] long[])
                    if (objectType != null) {
                        className= objectType.getClassName();
                        //used to cacth new, multinewarray, checkcast and instanceof
                        this.buildHierachyTree(className);
                    }
                }
            }
        }
    }
    
    private void buildHierachyTree(String className) throws ClassNotFoundException  {
        if (configurator.inspect(className) && classes.add(className)) {
        	//used just to relax de user
        	log.info("Analysing class: "+className);
            JavaClass javaClass= repository.loadClass(className);
            JavaClass superJavaClass= javaClass.getSuperClass();
            while (superJavaClass != null) {
                className= superJavaClass.getClassName();
                superJavaClass= superJavaClass.getSuperClass();
                this.buildHierachyTree(className);
            }
            String[] javaClasses= javaClass.getInterfaceNames();
            for (int i = 0; i < javaClasses.length; i++) {
                this.buildHierachyTree(javaClasses[i]);
            }
        }
    }

    /**
     * @param methods
     * @param usedMethods
     * @throws ClassNotFoundException
     */
    public void analyse(Method[] methods, Set usedMethods)
            throws ClassNotFoundException {
        for (int i = 0; i < methods.length; i++) {
            this.analyse(methods[i], usedMethods);
        }
        this.analyseOverridedMethods(usedMethods);
        int size = notProcessedMethods.size();
        if (size != 0) {
            methods = new Method[size];
            notProcessedMethods.toArray(methods);
            notProcessedMethods.clear();
            this.analyse(methods, usedMethods);
        }
        log.debug("Quantity of methods that call java.lang.Class.forName(java.lang.String className): " + methodsThatUseClassForName.size());
    }

    /**
     * @param usedMethods
     * @throws ClassNotFoundException
     */
    private void analyseOverridedMethods(Set usedMethods)
            throws ClassNotFoundException {
        String[] cls = (String[]) classes.toArray(new String[0]);
        for (int i = 0; i < cls.length; i++) {
            this.analyseOverridedMethods(cls[i], usedMethods);
        }
    }

    /**
     * @param className
     * @param usedMethods
     * @throws ClassNotFoundException
     */
    private void analyseOverridedMethods(String className, Set usedMethods)
            throws ClassNotFoundException {
        JavaClass jc = repository.loadClass(className);
        List methods = new ArrayList(Arrays.asList(jc.getMethods()));
        int size = methods.size();
        JavaClass superClass = jc.getSuperClass();
        while (superClass != null && methods.size() != 0 && configurator.inspect(className)) {
            this.analiseMethodFromSuperClass(methods, superClass, className, usedMethods);
            superClass = superClass.getSuperClass();
        }
        JavaClass[] is = jc.getAllInterfaces();
        for (int i = 0; i < is.length && methods.size() != 0 && configurator.inspect(className); i++) {
            this.analiseMethodFromSuperClass(methods, is[i], className, usedMethods);
        }

    }

    /**
     * @param method
     * @return
     */
    private LoadClass[] findLoadClassInstructions(MethodGen method) {
        Set instructions = new InstructionSet();
        InstructionFinder finder = new InstructionFinder(method
                .getInstructionList());
        Iterator i = finder.search("LoadClass");
        while (i.hasNext()) {
            InstructionHandle[] ih = (InstructionHandle[]) i.next();
            if (ih.length != 1) {
                throw new RuntimeException("Just one instruction must return!");
            } else {
                Instruction instruction = ih[0].getInstruction();
                instructions.add(instruction);
            }
        }
        return (LoadClass[]) instructions.toArray(new LoadClass[0]);
    }

    /**
     * @return Returns the methodsThatUseClassForName.
     */
    public Set getMethodsThatUseClassForName() {
        return methodsThatUseClassForName;
    }

    /**
     * @param visitor
     */
    public void receiveVisitor(Visitor visitor) throws Exception {
        Iterator iter = classes.iterator();
        while (iter.hasNext()) {
            String className = (String) iter.next();
            visitor.visit(className);
        }
    }
}