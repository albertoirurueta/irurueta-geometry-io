/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.PLYReadValueFromStreamListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 25, 2012
 */
package com.irurueta.geometry.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Listener to read the appropriate amount of bytes from a PLY file 
 * corresponding to this data type. The amount of bytes read are stored within 
 * the read buffer.
 */
public interface PLYReadValueFromStreamListener {
    /**
     * Reads needed data from stream
     * @param buffer Buffer where data is stored
     * @throws IOException if an I/O error occurs
     */
    public void readFromStream(ByteBuffer buffer) throws IOException;
}
