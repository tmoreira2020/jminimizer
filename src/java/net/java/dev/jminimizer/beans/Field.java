package net.java.dev.jminimizer.beans;

/**
 * @author Thiago Le�o Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class Field extends FieldOrMethod {

	/**
	 * 
	 */
	public Field(String className, String name, String signature) {
		super();
		this.setClassName(className);
		this.setName(name);
		this.setSignature(signature);
	}

}
