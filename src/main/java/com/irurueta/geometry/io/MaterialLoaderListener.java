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

/**
 * Interface of listener in charge of notifying when material loading starts,
 * ends or to notify loading progress.
 */
public interface MaterialLoaderListener {
    /**
     * This method is called when a MaterialLoader starts decoding a file
     *
     * @param loader Loader decoding a material file.
     */
    void onLoadStart(final MaterialLoader loader);

    /**
     * This method is called when a MaterialLoader ends decoding a file
     *
     * @param loader Loader decoding a material file.
     */
    void onLoadEnd(final MaterialLoader loader);

    /**
     * This method is called when texture validation is needed, to ensure that
     * a texture file is in a recognized image format.
     *
     * @param loader  Loader decoding a material file.
     * @param texture A texture.
     * @return true if texture is valid, false otherwise.
     */
    boolean onValidateTexture(final MaterialLoader loader, final Texture texture);
}
