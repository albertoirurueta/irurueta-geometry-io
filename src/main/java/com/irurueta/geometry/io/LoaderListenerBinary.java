/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderListenerBinary
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 20, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;

public interface LoaderListenerBinary extends LoaderListener{
    
    /**
     * Callee should create a file where texture data will be stored.
     * Caller of this method will be in charge of writing texture data in JPG
     * format at the file returned by this method.
     * @param loader Caller of this method
     * @param textureId internal identifier of texture
     * @param textureImageWidth Texture image width in pixels
     * @param textureImageHeight Texture image height in pixels
     * @return A file where texture data will be stored
     */
    public File onTextureReceived(LoaderBinary loader, int textureId,
            int textureImageWidth, int textureImageHeight);
    
    /**
     * Indicates that texture data has been stored in provided texture file,
     * which is the one returned by onTextureReceived(LoaderBinary).
     * Texture data is always provided in JPG format
     * @param loader Caller of this method
     * @param textureFile File where texture data has been stored
     * @param textureId id assigned to texture
     * @param textureImageWidth Texture image width in pixels
     * @param textureImageHeight Texture image height in pixels
     * @return True to indicate that texture data is a valid image, 
     * false otherwise. Returning false will halt the loading process for a
     * binary loader by raising a LoaderException
     * @see #onTextureReceived(LoaderBinary, int, int, int)
     */
    public boolean onTextureDataAvailable(LoaderBinary loader, 
            File textureFile, int textureId, int textureImageWidth, 
            int textureImageHeight);
}
