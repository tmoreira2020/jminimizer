package net.java.dev.jminimizer.beans;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.bcel.generic.MethodGen;

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
    public Class(String name)
            {
        super();
        this.name = name;
        this.fields = new HashSet();
        this.methods = new HashSet();
    }

    /**
     *  
     */
    public void add(Field field) {
        if (field.getClassName().equals(name)) {
            fields.add(field);
        } else {
            throw new IllegalArgumentException(
                    "The field must be a member of this class!");
        }
    }

    /**
     *  
     */
    public void add(Method method) {
        if (method.getClassName().equals(name)) {
            if (method.getName().equals("<init>")) {
                method = new Constructor(method.getClassName(), method
                        .getSignature());
            }
            methods.add(method);
        } else {
            throw new IllegalArgumentException(
                    "The method must be a member of this class!");
        }
    }

    /**
     * @return
     */
    public boolean containsField(String name, String signature) {
        return fields.contains(new Field(this.name, name, signature));
    }

    /**
     * @return
     */
    public boolean containsMethod(String name, String signature) {
        return methods.contains(new Method(this.name, name, signature, false));
    }

    /**
     * @return
     */
    public boolean containsMethod(MethodGen method) {
        return methods.contains(new Method(this.name, method.getName(), method
                .getSignature(), false));
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

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return name.hashCode() + fields.hashCode() + methods.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Class: ");
        buffer.append(name);
        buffer.append("\n  Fields:\n    ");
        Iterator i = fields.iterator();
        while (i.hasNext()) {
            Field field = (Field) i.next();
            buffer.append(field.toString());
            buffer.append("\n    ");
        }
        buffer.delete(buffer.length() - 2, buffer.length());
        buffer.append("Methods:\n    ");
        i = methods.iterator();
        while (i.hasNext()) {
            Method method = (Method) i.next();
            buffer.append(method.toString());
            buffer.append("\n    ");
        }
        return buffer.toString();
    }
}