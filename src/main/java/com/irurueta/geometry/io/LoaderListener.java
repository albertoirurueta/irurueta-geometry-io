/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Interface of listener in charge of notifying when the loading starts, ends
 * or to notify loading progress.
 */
public interface LoaderListener {
    /**
     * This method is called when a Loader starts decoding a file
     * @param loader Loader decoding a file.
     */
    public void onLoadStart(Loader loader);
    
    /**
     * This method is called when a Loader ends decoding a file
     * @param loader Loader decoding a file.
     */
    public void onLoadEnd(Loader loader);
    
    /**
     * This method is called each time a substantial amount of the file is 
     * loaded.
     * @param loader Loader decoding a file.
     * @param progress Amount of progress that has been decoded. Progress is
     * within the range 0.0 and 1.0.
     */
    public void onLoadProgressChange(Loader loader, float progress);
}
