package dummy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Le�o Moreira
 * @since Apr 12, 2004
 *  
 */
public class Main {

	private static final Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) {
		Worker w= new EnglishTeacher(null);
		w.getName();
		w.getSalary();
	}

}