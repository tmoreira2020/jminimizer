package net.java.dev.jminimizer.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public class Class {

    private Set fields;

    private Set methods;

    private String name;

    /**
     *  
     */
    public Class(String name) throws ClassNotFoundException {
        this(name, false);
    }

    /**
     *  
     */
    public Class(String name, boolean loadMembers)
            throws ClassNotFoundException {
        super();
        this.name = name;
        this.fields = new HashSet();
        this.methods = new HashSet();
        if (loadMembers) {
            this.loadMembers();
        }
    }

    public void add(Field field) {
        if (field.getClassName().equals(name)) {
            fields.add(field);
        } else {
            throw new IllegalArgumentException(
                    "The field must be a member of this class!");
        }
    }

    public void add(Method method) {
        if (method.getClassName().equals(name)) {
            methods.add(method);
        } else {
            throw new IllegalArgumentException(
                    "The method must be a member of this class!");
        }
    }

    public boolean containsField(String name, String signature) {
        return fields.contains(new Field(this.name, name, signature));
    }

    public boolean containsMethod(String name, String signature) {
        return methods.contains(new Method(this.name, name, signature, false));
    }

    /**
     * @return
     */
    public Field[] getFields() {
        return (Field[]) fields.toArray(new Field[0]);
    }

    /**
     * @return
     */
    public Method[] getMethods() {
        return (Method[]) methods.toArray(new Method[0]);
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    public Set getOverridedMethods(Method[] ms) {
        Set set = new HashSet();
        for (int i = 0; i < ms.length; i++) {
            if (this.containsMethod(ms[i].getName(), ms[i].getSignature())) {
                set.add(ms[i]);
            }
        }
        return set;
    }

    private void loadMembers() throws ClassNotFoundException {
        java.lang.Class clazz = java.lang.Class.forName(name);
        java.lang.reflect.Method[] ms = clazz.getDeclaredMethods();
        for (int i = 0; i < ms.length; i++) {
            this.add(new Method(ms[i]));
        }
        java.lang.reflect.Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            this.add(new Field(fs[i]));
        }
    }

    /**
     * @param fields
     */
    public void setFields(Field[] fields) {
        for (int i = 0; i < fields.length; i++) {
            this.add(fields[i]);
        }
    }

    /**
     * @param methods
     */
    public void setMethods(Method[] methods) {
        for (int i = 0; i < methods.length; i++) {
            this.add(methods[i]);
        }
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    public java.lang.Class toClass() throws ClassNotFoundException {
        return java.lang.Class.forName(name);
    }

}