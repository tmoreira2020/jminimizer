package net.java.dev.jminimizer;

import java.io.File;
import java.io.IOException;
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
import org.apache.bcel.generic.ClassGen;
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

    protected Map classes;

    protected MethodInspector inspecter;

    protected SortedSet limitsMethods;

    protected net.java.dev.jminimizer.util.Repository repo;

    /**
     *  
     */
    public Analyser(MethodInspector inspecter,
            net.java.dev.jminimizer.util.Repository repo) {
        super();
        limitsMethods = new TreeSet(FieldOrMethod.COMPARATOR);
        abstractClasses = new HashMap();
        classes = new HashMap();
        this.inspecter = inspecter;
        this.repo = repo;
        Repository.setRepository(this.repo);
    }

    private void addToClasses(MethodGen method) throws ClassNotFoundException {
        String className = method.getClassName();
        Class clazz;
        Method m = new Method(method);
        if (method.isAbstract()) {
            clazz = this.getClass(abstractClasses, className);
            clazz.add(m);
        }
        clazz = this.getClass(classes, className);
        clazz.add(m);
    }

    private Set analiseAbstractMethods() throws ClassNotFoundException {
        Iterator i = repo.getProgramClasses().iterator();
        Set overridedMethods = new HashSet();
        while (i.hasNext()) {
            Class baseClass = new Class((String) i.next(), true);
            JavaClass[] classes = Repository.getSuperClasses(baseClass
                    .getName());
            for (int j = 0; j < classes.length; j++) {
                Class superClass = (Class) abstractClasses.get(classes[j]
                        .getClassName());
                if (superClass != null) {
                    overridedMethods.addAll(superClass
                            .getOverridedMethods(baseClass.getMethods()));
                }
            }
        }
        abstractClasses.clear();
        return overridedMethods;
    }

    public void analyse(Method method) throws ClassNotFoundException {
        this.analyse(method.toMethodGen());
        Iterator i = this.analiseAbstractMethods().iterator();
        while (i.hasNext()) {
            Method m = (Method) i.next();
            this.analyse(m);
        }
    }

    public void analyse(Method[] ms) throws ClassNotFoundException {
        for (int i = 0; i < ms.length; i++) {
            this.analyse(ms[i]);
        }
    }

    private void analyse(MethodGen method) throws ClassNotFoundException {
        this.addToClasses(method);
        if (!method.isAbstract()) {
            JavaClass javaClass = Repository.lookupClass(method.getClassName());
            ConstantPoolGen pool = new ConstantPoolGen(javaClass
                    .getConstantPool());
            Set mgs = this.analyseInvokeInstruction(this.findInstructions(
                    method, "InvokeInstruction"), pool);
            Iterator i = mgs.iterator();
            while (i.hasNext()) {
                MethodGen m = (MethodGen) i.next();
                this.analyse(m);
            }
            this.analyseFieldInstruction(this.findInstructions(method,
                    "FieldInstruction"), pool);
        }
    }

    private void analyseFieldInstruction(Set instructions, ConstantPoolGen pool)
            throws ClassNotFoundException {
        Iterator i = instructions.iterator();
        while (i.hasNext()) {
            FieldInstruction element = (FieldInstruction) i.next();
            FieldGen f = this.findField(element, pool);
            String className = element.getClassName(pool);
            Class clazz = this.getClass(classes, className);
            clazz.add(new Field(className, f.getName(), f.getSignature()));
        }
    }

    private Set analyseInvokeInstruction(Set instructions, ConstantPoolGen pool)
            throws ClassNotFoundException {
        Iterator i = instructions.iterator();
        Set methods = new HashSet();
        while (i.hasNext()) {
            InvokeInstruction element = (InvokeInstruction) i.next();
            MethodGen m = this.findMethod(element, pool);
            Method method = new Method(m);
            if (inspecter.inspect(m)) {
                methods.add(m);
            } else {
                limitsMethods.add(method);
            }
        }
        return methods;
    }

    public void dump(File directory) throws IOException {
        Set programClasses = repo.getProgramClasses();
        Collection col = classes.values();
        for (Iterator iter = col.iterator(); iter.hasNext();) {
            Class clazz = (Class) iter.next();
            String className = clazz.getName();
            if (programClasses.contains(className)) {
                JavaClass jc = repo.findClass(className);
                org.apache.bcel.classfile.Method[] ms = jc.getMethods();
                log.debug("Cleaning class: " + className);
                ClassGen cg = new ClassGen(jc);
                for (int i = 0; i < ms.length; i++) {
                    if (!clazz.containsMethod(ms[i].getName(), ms[i]
                            .getSignature())) {
                        log.debug("Removing method: " + ms[i]);
                        cg.removeMethod(ms[i]);
                    }
                }
                org.apache.bcel.classfile.Field[] fs = jc.getFields();
                for (int i = 0; i < fs.length; i++) {
                    if (!clazz.containsField(fs[i].getName(), fs[i]
                            .getSignature())) {
                        log.debug("Removing field: " + fs[i]);
                        cg.removeField(fs[i]);
                    }
                }
                cg.update();
                File file = new File(directory, className.replace('.',
                        File.separatorChar).concat(".class"));
                log.debug("Dumping class: " + className + " to file: " + file);
                cg.getJavaClass().dump(file);
            }
        }

    }

    private FieldGen findField(FieldInstruction fi, ConstantPoolGen pool)
            throws ClassNotFoundException {
        return ClassUtils.findField(fi.getClassName(pool), fi.getName(pool));
    }

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

    private MethodGen findMethod(InvokeInstruction ii, ConstantPoolGen pool)
            throws ClassNotFoundException {
        return ClassUtils.findMethod(ii.getClassName(pool), ii.getName(pool),
                ii.getSignature(pool));
    }

    private Class getClass(Map classes, String className)
            throws ClassNotFoundException {
        Class clazz = (Class) classes.get(className);
        if (clazz == null) {
            clazz = new Class(className);
            classes.put(className, clazz);
        }
        return clazz;
    }

    /**
     * @return
     */
    public SortedSet getLimitsMethods() {
        return limitsMethods;
    }

    public void visit(Visitor visitor) {
        Collection col = classes.values();
        for (Iterator iter = col.iterator(); iter.hasNext();) {
            visitor.visit((Class) iter.next());
        }
    }

}