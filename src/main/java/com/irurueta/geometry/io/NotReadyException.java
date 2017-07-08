/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.NotReadyException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Raised when not enough parameters have been provided an an instance is not
 * ready to start a given process.
 */
public class NotReadyException extends Exception{
    
    /**
     * Constructor
     */            
    public NotReadyException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */            
    public NotReadyException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */            
    public NotReadyException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */            
    public NotReadyException(Throwable cause){
        super(cause);
    }
}
