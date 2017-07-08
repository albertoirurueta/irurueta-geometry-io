/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.PLYReadValueFromBufferListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 25, 2012
 */
package com.irurueta.geometry.io;

import java.nio.ByteBuffer;

/**
 * Listener to read a value contained within the byte read buffer and transform 
 * it into the appropriate data type
 */
public interface PLYReadValueFromBufferListener {
    
    /**
     * Reads a value from the byte read buffer
     * @param buffer Byte buffer
     */
    public void readValueFromBuffer(ByteBuffer buffer);
}
