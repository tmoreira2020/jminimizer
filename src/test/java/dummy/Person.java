package dummy;

import java.util.Date;

/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public abstract class Person {

//	private static final Log log = LogFactory.getLog(Person.class);
	private String name;

	/**
	 *  
	 */
	public Person() {
		super();
	}
	
	public void setName(String name) {
		this.name= name;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDeadDate() {
		return new Date();
	}
	
	public abstract String getJob();
	
	
}
