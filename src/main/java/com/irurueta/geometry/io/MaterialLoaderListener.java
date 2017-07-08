/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MaterialLoaderListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 12, 2012
 */
package com.irurueta.geometry.io;

/**
 * Interface of listener in charge of notifying when material loading starts, 
 * ends or to notify loading progress.
 */
public interface MaterialLoaderListener {
    /**
     * This method is called when a MaterialLoader starts decoding a file
     * @param loader Loader decoding a material file.
     */
    public void onLoadStart(MaterialLoader loader);
    
    /**
     * This method is called when a MaterialLoader ends decoding a file
     * @param loader Loader decoding a material file.
     */
    public void onLoadEnd(MaterialLoader loader);
    
    /**
     * This method is called when texture validation is needed, to ensure that
     * a texture file is in a recognized image format
     * @param loader Loader decoding a material file
     * @param texture A texture
     * @return true if texture is valid, false otherwise
     */
    public boolean onValidateTexture(MaterialLoader loader, Texture texture);
}
