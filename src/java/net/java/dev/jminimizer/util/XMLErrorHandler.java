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
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException {
        String id = exception.getPublicId();
        if (id == null) {
            id = exception.getSystemId();
        }
        String location= "The xml file: " + id + " has a error at line: " +
        exception.getLineNumber() + " and column: " + exception.getColumnNumber() + " the error message is: \n";
        location+= exception.getMessage();
        log.error(location);
        System.exit(0);
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        String id = exception.getPublicId();
        if (id == null) {
            id = exception.getSystemId();
        }
        String location= "The xml file: " + id + " has a fatal error at line: " +
        exception.getLineNumber() + " and column: " + exception.getColumnNumber() + " the error message is: \n";
        location+= exception.getMessage();
        log.fatal(location, exception);
        System.exit(0);
    }

    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException {
        String id = exception.getPublicId();
        if (id == null) {
            id = exception.getSystemId();
        }
        String location= "The xml file: " + id + " has a warning at line: " +
        exception.getLineNumber() + " and column: " + exception.getColumnNumber() + " the error message is: \n";
        location+= exception.getMessage();
        log.warn(location);
    }

    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {
        return new InputSource(this.getClass().getResourceAsStream(
        "/resources/configuration.dtd"));
    }
}