/*
 * Copyright (C) 2012 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irurueta.geometry.io;

import java.io.File;

/**
 * Listener for a mesh writer. This listener will notify when writing starts,
 * finishes, notifies progress changes, when textures need to be validated, when
 * a chunk of data has been loaded, etc.
 */
public interface MeshWriterListener {
    
    /**
     * Notifies when writing starts.
     * @param writer reference to writer.
     */
    void onWriteStart(MeshWriter writer);
    
    /**
     * Notifies when writing ends.
     * @param writer reference to writer.
     */
    void onWriteEnd(MeshWriter writer);
    
    /**
     * Notifies progress changes during writing.
     * @param writer reference to writer.
     * @param progress progress amount as a value between 0.0 and 1.0.
     */
    void onWriteProgressChange(MeshWriter writer, float progress);
    
    /**
     * Called when a chunk of data is available.
     * @param writer reference to writer.
     * @param chunk chunk of data that has been loaded.
     */
    void onChunkAvailable(MeshWriter writer, DataChunk chunk);
    
    /**
     * Called when a material file is needed. A path used as a hint to locate
     * such file will be provided, and using that path the File must be 
     * retrieved (by any means, such as downloading from a remote location or
     * uncompressing from a file, etc).
     * Returned file will be handled by the callee of this method. If a 
     * temporary file is created copying the contents of the file from a remote
     * location, then it will be the responsibility of the callee to delete the
     * temporary file.
     * @param writer reference to writer.
     * @param path path where material file is supposed to be found.
     * @return File containing the material file.
     */
    File onMaterialFileRequested(MeshWriter writer, String path);
    
    /**
     * Called when a texture needs to be validated.
     * Provided texture data will contain the path that can be used as a hint to
     * locate the texture image. The texture image file can be retrieved by
     * any means, such as downloading form a remote location or uncompressing
     * from a file, etc.
     * Returned file will be handled by the callee of this method. If a 
     * temporary file is created copying the contents of the original texture
     * (or modifying such texture by transcoding it or resizing it), then it
     * will be the responsibility of the callee to delete the temporary file.
     * @param writer reference to writer.
     * @param texture reference to texture.
     * @return File containing texture to be validated.
     */
    File onValidateTexture(MeshWriter writer, Texture texture);
    
    /**
     * Called when texture has been validated.
     * This method is called so that the callee has a chance to remove the
     * texture file if needed.
     * @param writer reference to writer.
     * @param f reference to texture file that has been validated. If this file
     * was meant to be temporary, this method gives a chance to remove it 
     * because it will no longer be needed.
     */
    void onDidValidateTexture(MeshWriter writer, File f);
    
    /**
     * Called when texture data is found in a binary file.
     * This method should create a temporary file where such data will be 
     * copied to.
     * @param writer reference to writer.
     * @param textureWidth texture image width in pixels.
     * @param textureHeight texture image height in pixels.
     * @return File where texture data will be copied to.
     */
    File onTextureReceived(MeshWriter writer, int textureWidth,
            int textureHeight);
    
    /**
     * Called when texture data of a binary file has been copied to temporal 
     * file and is available for processing.
     * This method gives the callee a chance to transcode, transform or resize
     * the texture image as needed.
     * @param writer reference to writer.
     * @param textureFile file that contains texture image data read from stream.
     * @param textureWidth texture image width in pixels.
     * @param textureHeight texture image height in pixels.
     * @return converted image transcoded into JPG format or resized if
     * needed, otherwise textureFile can be returned if no changes are needed.
     */
    File onTextureDataAvailable(MeshWriter writer, File textureFile,
            int textureWidth, int textureHeight);
    
    /**
     * Called when texture data has already been processed for a binary file.
     * This method gives a chance to remove any temporal texture image as it
     * will no longer be needed.
     * @param writer reference to writer.
     * @param textureFile file that contains texture image data that can be
     * removed.
     * @param textureWidth texture image width in pixels.
     * @param textureHeight texture image height in pixels.
     */
    void onTextureDataProcessed(MeshWriter writer, File textureFile,
            int textureWidth, int textureHeight);
}
