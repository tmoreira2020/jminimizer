package net.java.dev.jminimizer.beans;
import org.apache.bcel.generic.Type;
/**
 * @author Thiago Le�o Moreira <thiagolm@dev.java.net>
 * @since Apr 13, 2004
 *  
 */
public class Constructor extends Method {
	/**
	 * @param className
	 * @param signature
	 */
	public Constructor(String className, String signature) {
		super(className, "<init>", signature);
	}
	
	/**
	 * @param className
	 * @param argumentClasses
	 */
	public Constructor(String className,
			String[] argumentClasses) {
		super(className, "<init>", argumentClasses, "void");
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("new ");
		buffer.append(this.getClassName());
		buffer.append('(');
		Type[] args = Type.getArgumentTypes(this.getSignature());
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i]);
			buffer.append(", ");
		}
		if (args.length != 0) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}
		buffer.append(')');
		return buffer.toString();
	}
}