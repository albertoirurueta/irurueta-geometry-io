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

public interface LoaderListenerBinary extends LoaderListener {
    
    /**
     * Callee should create a file where texture data will be stored.
     * Caller of this method will be in charge of writing texture data in JPG
     * format at the file returned by this method.
     * @param loader Caller of this method.
     * @param textureId internal identifier of texture.
     * @param textureImageWidth Texture image width in pixels.
     * @param textureImageHeight Texture image height in pixels.
     * @return A file where texture data will be stored.
     */
    File onTextureReceived(LoaderBinary loader, int textureId,
            int textureImageWidth, int textureImageHeight);
    
    /**
     * Indicates that texture data has been stored in provided texture file,
     * which is the one returned by onTextureReceived(LoaderBinary).
     * Texture data is always provided in JPG format.
     * @param loader Caller of this method.
     * @param textureFile File where texture data has been stored.
     * @param textureId id assigned to texture.
     * @param textureImageWidth Texture image width in pixels.
     * @param textureImageHeight Texture image height in pixels.
     * @return True to indicate that texture data is a valid image, 
     * false otherwise. Returning false will halt the loading process for a
     * binary loader by raising a LoaderException.
     * @see #onTextureReceived(LoaderBinary, int, int, int)
     */
    boolean onTextureDataAvailable(LoaderBinary loader,
            File textureFile, int textureId, int textureImageWidth, 
            int textureImageHeight);
}
