/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.InvalidTextureException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 19, 2012
 */
package com.irurueta.geometry.io;

/**
 *
 * Raised if a material loader fails to validate a texture. This is usually
 * because image file cannot be found or is corrupted.
 */
public class InvalidTextureException extends LoaderException{
    
    /**
     * Constructor
     */        
    public InvalidTextureException(){
        super();
    }
    
    /**
     * Constructor with String containing message
     * @param message Message indicating the cause of the exception
     */        
    public InvalidTextureException(String message){
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message Message describing the cause of the exception
     * @param cause Instance containing the cause of the exception
     */        
    public InvalidTextureException(String message, Throwable cause){
        super(message, cause);
    }
    
    /**
     * Constructor with cause
     * @param cause Instance containing the cause of the exception
     */        
    public InvalidTextureException(Throwable cause){
        super(cause);
    }    
}
