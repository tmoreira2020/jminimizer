package net.java.dev.jminimizer.util;

import net.java.dev.jminimizer.beans.Field;
import net.java.dev.jminimizer.beans.Method;

import org.apache.bcel.Repository;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;


/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 * 
 */
public class DisplayVisitor implements Visitor {

	protected String className;
	protected ClassGen clazz;

	/**
	 * 
	 */
	public DisplayVisitor() {
		super();
	}

	/**
	 * @see br.ufsc.inf.tlm.util.Visitor#visit(br.ufsc.inf.tlm.Method)
	 */
	public void visit(Method method) {
		MethodGen m = null;
		try {
			this.init(method.getClassName());
			m = ClassUtils.findMethod(className, method.getName(), method.getSignature());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		System.out.print('\t');
		System.out.println(m);
	}

	/**
	 * @see br.ufsc.inf.tlm.util.Visitor#visit(br.ufsc.inf.tlm.Field)
	 */
	public void visit(Field field) {
		FieldGen f = null;
		try {
			this.init(field.getClassName());
			f = ClassUtils.findField(className, field.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		System.out.print('\t');
		System.out.println(f);
	}

	private void init(String className) throws ClassNotFoundException {
		if (!className.equals(this.className)) {
			System.out.println();
			System.out.println(className);
			this.className = className;
			clazz = new ClassGen(Repository.lookupClass(className));
		}
	}

}
