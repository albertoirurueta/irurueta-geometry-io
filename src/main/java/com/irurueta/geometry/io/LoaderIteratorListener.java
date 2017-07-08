/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderIteratorListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Interface that contains method to notify when iterator is finished and there
 * are no more chunks to read.
 */
public interface LoaderIteratorListener {
    /**
     * Method called when a loader iterator has no more data to be read.
     * @param iterator Iterator loading a file.
     */
    public void onIteratorFinished(LoaderIterator iterator);
}
