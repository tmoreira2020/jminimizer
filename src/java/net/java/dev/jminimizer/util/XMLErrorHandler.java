package net.java.dev.jminimizer.util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Thiago Leão Moreira
 * @since Apr 21, 2004
 *  
 */
public class XMLErrorHandler implements ErrorHandler {
	private static final Log log = LogFactory.getLog(XMLErrorHandler.class);
	/**
	 *  
	 */
	public XMLErrorHandler() {
		super();
	}
	/**
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException exception) throws SAXException {
		System.out.println();
		String id= exception.getPublicId();
		if (id == null) {
			id= exception.getSystemId();
		}
		System.out.println("Erro in xml: " +id);
		System.out.print("In line: " + exception.getLineNumber());
		System.out.println(" column: " +exception.getColumnNumber());
		System.out.println(exception.getMessage());
		System.exit(0);
	}
	/**
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
	}
	/**
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
	}
}