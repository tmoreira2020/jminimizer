package net.java.dev.jminimizer.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Class {
	
	private String name;
	private Set methods;
	private Set fields;

	/**
	 * 
	 */
	public Class(String name) {
		super();
		this.name= name;
		this.fields= new HashSet();
		this.methods= new HashSet();
	}

	/**
	 * @return
	 */
	public Field[] getFields() {
		return (Field[]) fields.toArray();
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

	/**
	 * @param fields
	 */
	public void setFields(Field[] fields) {
		for (int i = 0; i < fields.length; i++) {
			this.add(fields[i]);
		}
	}

	public void add(Field field) {
		if (field.getClassName().equals(name)) {
			fields.add(field);
		} else {
			throw new IllegalArgumentException("The field must be a member of this class!");
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

	public void add(Method method) {
		if (method.getClassName().equals(name)) {
			methods.add(method);
		} else {
			throw new IllegalArgumentException("The method must be a member of this class!");
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
