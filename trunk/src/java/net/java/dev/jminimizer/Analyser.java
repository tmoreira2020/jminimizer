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

    protected Map classes;

    protected MethodInspector inspecter;

    protected SortedSet limitsMethods;

    protected net.java.dev.jminimizer.util.Repository repository;

    /**
     *  
     */
    public Analyser(MethodInspector inspecter,
            net.java.dev.jminimizer.util.Repository repo)
            throws ClassNotFoundException {
        super();
        limitsMethods = new TreeSet(FieldOrMethod.COMPARATOR);
        abstractClasses = new HashMap();
        classes = new HashMap();
        this.inspecter = inspecter;
        this.repository = repo;
        Repository.setRepository(this.repository);
        this.analyse(inspecter.getMethodsToInspect());
    }

    /**
     *  
     */
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
        //interfaces
        JavaClass jc = repository.loadClass(className);
        String[] interfaces = jc.getInterfaceNames();
        for (int i = 0; i < interfaces.length; i++) {
            if (!abstractClasses.containsKey(interfaces[i])) {
                clazz= repository.loadClass(interfaces[i], true);
                abstractClasses.put(interfaces[i], clazz);
                classes.put(interfaces[i], clazz);
            }
        }
    }

    /**
     *  
     */
    private Set analiseAbstractMethods() throws ClassNotFoundException {
        Iterator i = repository.getProgramClasses().iterator();
        Set overridedMethods = new HashSet();
        while (i.hasNext()) {
            Class baseClass = repository.loadClass((String) i.next(), true);
            JavaClass[] classes = repository.findClass(baseClass.getName())
                    .getSuperClasses();
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

    /**
     *  
     */
    public void analyse(Method method) throws ClassNotFoundException {
        this.analyse(method.toMethodGen());
        Iterator i = this.analiseAbstractMethods().iterator();
        while (i.hasNext()) {
            Method m = (Method) i.next();
            this.analyse(m);
        }
    }

    /**
     *  
     */
    public void analyse(Method[] ms) throws ClassNotFoundException {
        for (int i = 0; i < ms.length; i++) {
            this.analyse(ms[i]);
        }
    }

    /**
     *  
     */
    private void analyse(MethodGen method) throws ClassNotFoundException {
        Class clazz = this.getClass(method.getClassName());
        if (clazz.containsMethod(method)) { return; }
        this.addToClasses(method);
        if (!method.isAbstract()) {
            JavaClass javaClass = Repository.lookupClass(method.getClassName());
            ConstantPoolGen pool = new ConstantPoolGen(javaClass
                    .getConstantPool());
            Set mgs = this.analyseInvokeInstruction(this.findInstructions(
                    method, "InvokeInstruction"), pool);
            if (mgs.size() != 0) {
                Iterator i = mgs.iterator();
                while (i.hasNext()) {
                    MethodGen m = (MethodGen) i.next();
//                    System.out.println(Method.toString(m));
                    this.analyse(m);
                }
            } else {
                limitsMethods.add(new Method(method));
            }
            this.analyseFieldInstruction(this.findInstructions(method,
                    "FieldInstruction"), pool);
        }
    }

    /**
     *  
     */
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

    /**
     *  
     */
    private Set analyseInvokeInstruction(Set instructions, ConstantPoolGen pool)
            throws ClassNotFoundException {
        Iterator i = instructions.iterator();
        Set methods = new HashSet();
        while (i.hasNext()) {
            InvokeInstruction element = (InvokeInstruction) i.next();
            MethodGen m = this.findMethod(element, pool);
            Method method = new Method(m);
            System.out.println(method);
            if ("void com.datasul.crm.sfa.gui.UserHome.shortCutSelected(com.datasul.crm.sfa.lcdui.home.ShortCut)".equals(method.toString())) {
                System.out.println(inspecter.inspect(m));
            }
            if (inspecter.inspect(m)) {
                methods.add(m);
            } else {
                limitsMethods.add(method);
            }
        }
        return methods;
    }

    /**
     *  
     */
    private FieldGen findField(FieldInstruction fi, ConstantPoolGen pool)
            throws ClassNotFoundException {
        return ClassUtils.findField(fi.getClassName(pool), fi.getName(pool));
    }

    /**
     *  
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
     */
    private MethodGen findMethod(InvokeInstruction ii, ConstantPoolGen pool)
            throws ClassNotFoundException {
        return ClassUtils.findMethod(ii.getClassName(pool), ii.getName(pool),
                ii.getSignature(pool));
    }

    /**
     *  
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
     */
    private Class getClass(String className) {
        return this.getClass(classes, className);
    }

    /**
     * @return Returns the classes.
     */
    public Map getClasses() {
        return this.classes;
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
     * @return Returns the repository.
     */
    public net.java.dev.jminimizer.util.Repository getRepository() {
        return this.repository;
    }

    /**
     *  
     */
    public void visit(Visitor visitor) {
        Collection col = classes.values();
        for (Iterator iter = col.iterator(); iter.hasNext();) {
            visitor.visit((Class) iter.next());
        }
    }
}