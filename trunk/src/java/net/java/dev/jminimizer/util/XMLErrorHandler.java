package net.java.dev.jminimizer.util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Thiago Leão Moreira
 * @since Apr 21, 2004
 *  
 */
public class XMLErrorHandler extends DefaultHandler {
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
    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {
        System.out.println("systemId: "+ systemId);
        
        try {
            return super.resolveEntity(publicId, systemId);
		} catch (Exception e) {
			//TODO problema com o MAVEN na hora de compilar reclama q o super lança java.io.IOException
			e.printStackTrace();
		}
		return null;
    }
}