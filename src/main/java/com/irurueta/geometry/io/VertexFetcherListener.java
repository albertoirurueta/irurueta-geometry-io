/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.VertexFetcherListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 23, 2011
 */
package com.irurueta.geometry.io;

import java.io.IOException;

/**
 * Interface defining method to be implemented for vertex fetchers.
 * Vertex fetchers must be able to move the stream position to the location 
 * where requested so that a LoaderIterator can read vertex values.
 */
public interface VertexFetcherListener {
    
    /**
     * Moves current file position so that on next read requested vertex data
     * can be read.
     * @param index index of vertex to be requested
     * @throws LoaderException raised if file is corrupted or unexpected data is
     * found.
     * @throws IOException if an I/O exception occurs
     * @throws NotAvailableException raised if requested vertex can't be found
     */
    public void fetch(long index) 
            throws LoaderException, IOException, NotAvailableException;
}
