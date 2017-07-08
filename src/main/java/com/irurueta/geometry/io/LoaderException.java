/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Raised if a Loader fails to load a file. This is usually because the file is
 * corrupted or cannot be interpreted.
 */
public class LoaderException extends GeometryIOException{
    
    /**
     * Constructor
     */        
    public LoaderException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */        
    public LoaderException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */        
    public LoaderException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */        
    public LoaderException(Throwable cause){
        super(cause);
    }
}
