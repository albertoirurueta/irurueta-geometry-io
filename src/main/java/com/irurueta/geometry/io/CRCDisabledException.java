/**
 * @file
 * This file contains implementation of
 * com.irurueta.geoemtry.io.CRCDisabledException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 20, 2012
 */
package com.irurueta.geometry.io;

/**
 * Raised if attempting to retrieve a CRC value when such an option is not 
 * enabled
 */
public class CRCDisabledException extends GeometryIOException{
    
    /**
     * Constructor
     */        
    public CRCDisabledException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */        
    public CRCDisabledException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */        
    public CRCDisabledException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */        
    public CRCDisabledException(Throwable cause){
        super(cause);
    }        
}
