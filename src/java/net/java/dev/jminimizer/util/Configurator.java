package net.java.dev.jminimizer.util;

import java.io.File;
import java.net.URL;

import javax.xml.transform.Source;

import net.java.dev.jminimizer.beans.Method;

/**
 * @author Thiago Le�o Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public interface Configurator {

    /**
     * Returns a list of methods that must be inspect.
     * 
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     * @return a array with methods that must be inspect.
     */
    public Method[] getMethodsToInspect();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public URL[] getProgramClasspath();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public File getReportDirectory();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public Source getReportStyleSheet();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public URL[] getRuntimeClasspath();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public File getTransformationOutput();

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     * @return
     */
    public boolean inspect(Method method);

    /**
     * @author Thiago Le�o Moreira
     * @since May 11, 2004
     *  
     */
    public boolean inspect(String className);

}