/**
 * @file
 * This file contains definition of
 * com.irurueta.geoemtry.io.LoaderIterator
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

import java.io.IOException;

/**
 * Defines the interface to iterate on the loading process of a file.
 */
public interface LoaderIterator {

    /**
     * Returns boolean indicating if there is more data to be read.
     * @return True if there are more chunks to be read, false otherwise.
     */
    public boolean hasNext();
    
    /**
     * Reads next chunk of data on the file.
     * @return Chunk of data containing vertices, indices, colors, textures, 
     * etc.
     * @throws NotAvailableException Raised if no more chunks are available.
     * @throws LoaderException Raised if file cannot be read because it is 
     * either corrupted or cannot be interpreted.
     * @throws IOException if an I/O error occurs.
     */
    public DataChunk next() 
            throws NotAvailableException, LoaderException, IOException;
}
