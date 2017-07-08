/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MeshWriterJsonListener
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 21, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;

/**
 * Specific implementation of a mesh writer listener prepared for JSON writer.
 * This listener provides additional methods to handle textures
 */
public interface MeshWriterJsonListener extends MeshWriterListener{
    
    /**
     * Handles texture to write URL where it will be located
     * @param writer reference to writer
     * @param texture reference to texture being processed
     * @param textureFile reference to file containing texture
     * @return URL where texture can be located
     */
    public String onRemoteTextureUrlRequested(MeshWriterJson writer, 
            Texture texture, File textureFile);
    
    /**
     * Handles texture to write ID to fetch it
     * @param writer reference to writer
     * @param texture reference to texture being processed
     * @param textureFile reference to file containing texture
     * @return ID to locate texture
     */
    public String onRemoteTextureIdRequested(MeshWriterJson writer,
            Texture texture, File textureFile);
}
