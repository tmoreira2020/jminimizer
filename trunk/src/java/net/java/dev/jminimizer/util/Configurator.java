package net.java.dev.jminimizer.util;

import java.io.File;
import java.net.URL;

import javax.xml.transform.Source;

import net.java.dev.jminimizer.beans.Method;

/**
 * @author Thiago Leão Moreira <thiago.leao.moreira@terra.com.br>
 *  
 */
public interface Configurator {

    /**
     * Returns a list of methods that must be inspect.
     * 
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     * @return a array with methods that must be inspect.
     */
    public Method[] getMethodsToInspect();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public URL[] getProgramClasspath();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public File getReportDirectory();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public Source getReportStyleSheet();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public URL[] getRuntimeClasspath();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public File getTransformationOutput();

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     * @return
     */
    public boolean inspect(Method method);

    /**
     * @author Thiago Leão Moreira
     * @since May 11, 2004
     *  
     */
    public boolean inspect(String className);

}