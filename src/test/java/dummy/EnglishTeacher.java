package dummy;


/**
 * @author Thiago Leão Moreira
 * @since Apr 13, 2004
 *  
 */
public class EnglishTeacher extends Teacher {

	/**
	 * @param t
	 */
	public EnglishTeacher(Teacher t) {
		super(t);
	}
	
	/**
	 * @see dummy.Person#getJob()
	 */
	public String getJob() {
		return "English Teacher";
	}
	
}

