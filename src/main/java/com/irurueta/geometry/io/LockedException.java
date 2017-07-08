/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LockedException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Raised when a Loader is locked because it is already loading a file.
 */
public class LockedException extends GeometryIOException{
    
    /**
     * Constructor
     */        
    public LockedException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */        
    public LockedException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */        
    public LockedException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */        
    public LockedException(Throwable cause){
        super(cause);
    }
}
