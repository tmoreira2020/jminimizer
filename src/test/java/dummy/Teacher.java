package dummy;

import java.io.File;
import java.lang.reflect.Array;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public class Teacher extends Worker {

	private static final Log log = LogFactory.getLog(Teacher.class);
	
	/**
	 *  
	 */
	public Teacher(Teacher t) {
		super(t);
		new Integer(3);
		new String();
		new File("");
		Array.newInstance(int.class, 23);
	}

	/**
	 *
	 * 
	 * @see dummy.Worker#getSalary()
	 */
	public int getSalary() {
		int salary= Math.abs(new Integer(-67).intValue());
		return salary;
	}
	
	/**
	 * @see dummy.Person#getJob()
	 */
	public String getJob() {
		return "Teacher";
	}

}
