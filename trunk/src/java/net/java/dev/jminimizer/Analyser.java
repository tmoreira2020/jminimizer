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

import org.apache.bcel.Constants;
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
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class Analyser {

    private static final Log log = LogFactory.getLog(Analyser.class);

    protected Configurator inspecter;

    protected net.java.dev.jminimizer.util.Repository repository;

    protected Set notProcessedMethods;

    protected Set classes;

    protected Set methodsThatUseClassForName;

    /**
     * @param inspecter
     * @param repo
     * @throws ClassNotFoundException
     */
    public Analyser(Configurator inspecter,
            net.java.dev.jminimizer.util.Repository repo)
            throws ClassNotFoundException {
        super();
        this.inspecter = inspecter;
        this.repository = repo;
        this.notProcessedMethods = new HashSet();
        this.classes = new HashSet();
        this.methodsThatUseClassForName = new HashSet();
        Repository.setRepository(this.repository);
    }

    /**
     * @param methods
     * @return @throws
     *         ClassNotFoundException
     */
    public void analyse(Method[] methods, Set usedMethods)
            throws ClassNotFoundException {
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
        log.debug("Quantidade de métodos que utilizam java.lang.Class.forName(java.lang.String className): " + methodsThatUseClassForName.size());
    }
    
    private void proc(Method method) {
        try {
            MethodGen mg= method.toMethodGen();
            Attribute[] a= mg.getAttributes();
            for (int i = 0; i < a.length; i++) {
                if (a[i] instanceof Synthetic && method.getName().equals(Transformer.METHOD_SYNTHETIC_NAME)) {
                    System.out.println("É O CARA");
                    return;
                }
            }
            log.warn(method);
            methodsThatUseClassForName.add(method);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param methods
     * @throws ClassNotFoundException
     */
    private void analyseOverridedMethods(Set methods)
            throws ClassNotFoundException {
        String[] cls = (String[]) classes.toArray(new String[0]);
        //System.out.println("Free: "+ Runtime.getRuntime().freeMemory());
        System.gc();
        //System.out.println("Free: "+ Runtime.getRuntime().freeMemory());
        for (int i = 0; i < cls.length; i++) {
            this.analyseOverridedMethods(cls[i], methods);
        }
    }

    /**
     * @param method
     * @param methods
     * @return @throws
     *         ClassNotFoundException
     */
    public void analyse(Method method, String tab, Set methods)
            throws ClassNotFoundException {
        if (!methods.contains(method)) {
            classes.add(method.getClassName());
            methods.add(method);
            MethodGen mg = method.toMethodGen();
            if (mg.isNative()) {
                System.out.println("nativo");
                return;
            }
            if (mg.isAbstract()) {
            //System.out.println("abstrato");
            return; }
            Instruction[] instructions = this.findInvokeInstructions(mg);
            ConstantPoolGen pool = mg.getConstantPool();
            Method[] ms = this.instructionToMethod(
                    (InvokeInstruction[]) instructions, pool);
            for (int i = 0; i < ms.length; i++) {
                if (ms[i].getName().equals("forName")
                        && ms[i].getClassName().equals("java.lang.Class")) {
                    this.proc(method);
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
                methods.add(new Field(className, fi.getName(pool), fi
                        .getSignature(pool)));
                if (fi.getOpcode() == Constants.GETSTATIC) {
                    //System.out.println(fi.getName(pool));
                    //System.out.println(fi.getSignature(pool));
                    //System.out.println();
                }
                if (fi.getSignature(pool).indexOf("PopupWindow") != -1) {
                    //System.out.println(className);
                    //System.out.println(fi.getSignature(pool));
                }
                classes.add(className);
            }
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
    private InvokeInstruction[] findInvokeInstructions(MethodGen method) {
        return (InvokeInstruction[]) this.findInstructions(method,
                "InvokeInstruction").toArray(new InvokeInstruction[0]);
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
                System.out.println("NÃO ACHOU O METODO");
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
     * @param visitor
     */
    public void receiveVisitor(Visitor visitor) throws Exception {
        Iterator iter = classes.iterator();
        while (iter.hasNext()) {
            String className = (String) iter.next();
            visitor.visit(className);
        }
    }

    /**
     * @param className
     * @throws ClassNotFoundException
     */
    private void analyseOverridedMethods(String className, Set methods)
            throws ClassNotFoundException {
        JavaClass jc = repository.loadClass(className);
        List ms = new ArrayList(Arrays.asList(jc.getMethods()));
        int size = ms.size();
        JavaClass sjc = jc.getSuperClass();
        while (sjc != null && ms.size() != 0 && inspecter.inspect(className)) {
            //System.out.println(sjc.getClassName());
            an(ms, sjc, className, methods);
            sjc = sjc.getSuperClass();
        }
        JavaClass[] is = jc.getAllInterfaces();
        for (int i = 0; i < is.length && ms.size() != 0 && inspecter.inspect(className); i++) {
            if (is[i].getClassName().indexOf("Stack") != -1) {
                //		        System.out.println(className);
            }
            this.an(ms, is[i], className, methods);
        }

    }

    /**
     * @param ms
     * @param sjc
     * @param className
     * @param methods
     */
    private void an(List ms, JavaClass sjc, String className, Set methods) {
        classes.add(sjc.getClassName());
        for (int i = 0; i < ms.size();) {
            //System.out.println(ms.size());
            //System.out.println(sjc.getClassName());
            org.apache.bcel.classfile.Method mcf = (org.apache.bcel.classfile.Method) ms
                    .get(i);
            Method m = new Method(className, mcf.getName(), mcf.getSignature());
            ClassGen scg = new ClassGen(sjc);
            if (mcf.getName().equals("<cinit>")
                    && mcf.getSignature().equals("()V")) {
                notProcessedMethods.add(m);
                ms.remove(mcf);
                continue;
            }
            if (mcf.getName().equals("<init>")) {
                ms.remove(mcf);
                continue;
            }
            if (scg.containsMethod(mcf.getName(), mcf.getSignature()) != null
                    && !methods.contains(m)) {
                //TODO
                //System.out.println("ACHOU: " + m);
                notProcessedMethods.add(m);
                ms.remove(mcf);
            } else {
                i++;
            }
        }
    }
    /**
     * @return Returns the methodsThatUseClassForName.
     */
    public Set getMethodsThatUseClassForName() {
        return methodsThatUseClassForName;
    }
}