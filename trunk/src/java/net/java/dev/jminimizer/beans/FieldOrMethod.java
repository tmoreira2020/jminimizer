package net.java.dev.jminimizer.beans;

import java.util.Comparator;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public abstract class FieldOrMethod {
	
	public static final Comparator COMPARATOR= new Comparator() {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			FieldOrMethod fm1= (FieldOrMethod) o1;
			FieldOrMethod fm2= (FieldOrMethod) o2;
			return fm1.toCompare().compareTo(fm2.toCompare());
		}

	};
	
	protected String className;
	protected String name;
	protected String signature;

	/**
	 * 
	 */
	protected FieldOrMethod() {
		super();
	}

	/**
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param string
	 */
	public void setClassName(String string) {
		className = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setSignature(String string) {
		signature = string;
	}

	private String toCompare() {
		return className +" "+ name + signature;
	}

	/**
	* Returns <code>true</code> if this <code>FieldOrMethod</code> is the same as the o argument.
	*
	* @return <code>true</code> if this <code>FieldOrMethod</code> is the same as the o argument.
	*/
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof FieldOrMethod)) {
			return false;
		}
		
		FieldOrMethod castedObj = (FieldOrMethod) o;
		return (
			(this.className == null ? castedObj.className == null : this.className.equals(castedObj.className))
				&& (this.name == null ? castedObj.name == null : this.name.equals(castedObj.name))
				&& (this.signature == null ? castedObj.signature == null : this.signature.equals(castedObj.signature)));
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[FieldOrMethod:");
		buffer.append(" COMPARATOR: ");
		buffer.append(COMPARATOR);
		buffer.append(" className: ");
		buffer.append(className);
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" signature: ");
		buffer.append(signature);
		buffer.append("]");
		return buffer.toString();
	}

}
