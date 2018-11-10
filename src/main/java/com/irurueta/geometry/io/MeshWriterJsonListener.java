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
 * Specific implementation of a mesh writer listener prepared for JSON writer.
 * This listener provides additional methods to handle textures.
 */
public interface MeshWriterJsonListener extends MeshWriterListener {
    
    /**
     * Handles texture to write URL where it will be located.
     * @param writer reference to writer.
     * @param texture reference to texture being processed.
     * @param textureFile reference to file containing texture.
     * @return URL where texture can be located.
     */
    String onRemoteTextureUrlRequested(MeshWriterJson writer,
            Texture texture, File textureFile);
    
    /**
     * Handles texture to write ID to fetch it.
     * @param writer reference to writer.
     * @param texture reference to texture being processed.
     * @param textureFile reference to file containing texture
     * @return ID to locate texture.
     */
    String onRemoteTextureIdRequested(MeshWriterJson writer,
            Texture texture, File textureFile);
}
