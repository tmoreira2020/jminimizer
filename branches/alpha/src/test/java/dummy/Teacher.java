package dummy;


/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public class Teacher extends Worker {
	
	static int x=0;

//	private static final Log log = LogFactory.getLog(Teacher.class);
	
	/**
	 *  
	 */
	public Teacher(Teacher t) {
		super(t);
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
