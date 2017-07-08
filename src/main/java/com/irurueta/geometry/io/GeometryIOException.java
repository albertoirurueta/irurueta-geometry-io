/**
 * @file 
 * This file contains implementation of
 * com.irurueta.geometry.io.GeometryIOException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Base class for exceptions in this package.
 */
public class GeometryIOException extends Exception{
    
    /**
     * Constructor
     */    
    public GeometryIOException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */    
    public GeometryIOException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */    
    public GeometryIOException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */    
    public GeometryIOException(Throwable cause){
        super(cause);
    }
    
}
