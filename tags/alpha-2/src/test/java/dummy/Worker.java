package dummy;


/**
 * @author Thiago Leão Moreira
 * @since Apr 12, 2004
 *  
 */
public abstract class Worker extends Person {

	private String job;
	
	/**
	 *  
	 */
	public Worker(Worker w) {
		super();
	}
	
	protected void setJob(String job) {
		this.job= job;
	}
	
	public abstract int getSalary();
	
}
