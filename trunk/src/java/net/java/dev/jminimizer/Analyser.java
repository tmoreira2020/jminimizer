package net.java.dev.jminimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.bcel.generic.MethodGen;
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

    protected Configurator inspecter;

    protected Set methodsThatUseClassForName;

    protected Set notProcessedMethods;

    protected net.java.dev.jminimizer.util.Repository repository;

    /**
     * @param inspecter
     * @param repository
     * @throws ClassNotFoundException
     */
    public Analyser(Configurator inspecter,
            net.java.dev.jminimizer.util.Repository repository)
            throws ClassNotFoundException {
        super();
        this.inspecter = inspecter;
        this.repository = repository;
        this.notProcessedMethods = new HashSet();
        this.classes = new HashSet();
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
            if (classes.add(method.getClassName())) {
            	//used just to relax de user
            	log.info("Analysing class: "+method.getClassName());
            }
            usedMethods.add(method);
            MethodGen mg = method.toMethodGen();
            if (mg.isNative()) {
            	log.trace("Native method: "+ method.toString());
                return;
            }
            if (mg.isAbstract()) {
            	log.trace("Abstract method: "+ method.toString());
            	return;
            }
            Instruction[] instructions = this.findInvokeInstructions(mg);
            ConstantPoolGen pool = mg.getConstantPool();
            Method[] ms = this.instructionToMethod(
                    (InvokeInstruction[]) instructions, pool);
            for (int i = 0; i < ms.length; i++) {
                if (ms[i].getName().equals("forName")
                        && ms[i].getClassName().equals("java.lang.Class")) {
                    this.analiseMethodThatUseClassForName(method);
                }
                // test with this method should be analyse
                if (inspecter.inspect(ms[i])) {
                    this.analyse(ms[i], usedMethods);
                } else {
                    usedMethods.add(ms[i]);
                }
            }
            instructions = this.findFieldInstructions(mg);
            // Look up for use of fields. 
            for (int i = 0; i < instructions.length; i++) {
                FieldInstruction fi = (FieldInstruction) instructions[i];
                String className = fi.getClassName(pool);
                usedMethods.add(new Field(className, fi.getName(pool), fi
                        .getSignature(pool)));
                classes.add(className);
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
        while (superClass != null && methods.size() != 0 && inspecter.inspect(className)) {
            analiseMethodFromSuperClass(methods, superClass, className, usedMethods);
            superClass = superClass.getSuperClass();
        }
        JavaClass[] is = jc.getAllInterfaces();
        for (int i = 0; i < is.length && methods.size() != 0 && inspecter.inspect(className); i++) {
            this.analiseMethodFromSuperClass(methods, is[i], className, usedMethods);
        }

    }

    /**
     * @param method
     * @return
     */
    private FieldInstruction[] findFieldInstructions(MethodGen method) {
        return (FieldInstruction[]) this.findInstructions(method,
                "FieldInstruction").toArray(new FieldInstruction[0]);
    }

    /**
     * @param method
     * @param instructionPattern
     * @return
     */
    private Set findInstructions(MethodGen method, String instructionPattern) {
        Set instructions = new InstructionSet();
        InstructionFinder finder = new InstructionFinder(method
                .getInstructionList());
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
     * @param method
     * @param instructionPattern
     * @return
     */
    private InvokeInstruction[] findInvokeInstructions(MethodGen method) {
        return (InvokeInstruction[]) this.findInstructions(method,
                "InvokeInstruction").toArray(new InvokeInstruction[0]);
    }
    /**
     * @return Returns the methodsThatUseClassForName.
     */
    public Set getMethodsThatUseClassForName() {
        return methodsThatUseClassForName;
    }

    /**
     * @param instructions
     * @param pool
     * @return @throws
     *         ClassNotFoundException
     */
    private Method[] instructionToMethod(InvokeInstruction[] instructions,
            ConstantPoolGen pool) throws ClassNotFoundException {
        Set methods = new HashSet();
        for (int i = 0; i < instructions.length; i++) {
            Method method = ClassUtils.findMethod(instructions[i]
                    .getClassName(pool), instructions[i].getName(pool),
                    instructions[i].getSignature(pool));
            if (method == null) {
            	method= new Method(instructions[i].getClassName(pool), 
            			instructions[i].getName(pool),
						instructions[i].getSignature(pool));
            	throw new RuntimeException("Method not find: " + method);
            } else {
                methods.add(method);
            }
        }
        return (Method[]) methods.toArray(new Method[0]);
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