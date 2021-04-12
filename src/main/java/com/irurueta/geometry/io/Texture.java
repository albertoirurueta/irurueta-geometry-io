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
 * Contains data related to a texture.
 */
public class Texture {

    /**
     * Path to file containing texture image in any of the supported formats.
     */
    private final String fileName;

    /**
     * Reference to file actually containing the texture image.
     */
    private File file;

    /**
     * With of texture image expressed in pixels.
     */
    private int width;

    /**
     * Height of texture image expressed in pixels.
     */
    private int height;

    /**
     * Indicates if texture is valid or not. A texture will be considered to be
     * invalid when its image cannot be loaded because the file cannot be found,
     * it is corrupted or its format is not supported.
     */
    private boolean valid;

    /**
     * Id assigned to this texture. Id must be unique so that texture can be
     * uniquely identified.
     */
    private final int id;

    /**
     * Constructor.
     *
     * @param id id to be set for this texture.
     */
    public Texture(final int id) {
        this.fileName = "";
        this.id = id;
        file = null;
        width = height = -1;
        valid = false;
    }

    /**
     * COnstructor.
     *
     * @param fileName path to file containing texture image in any of the
     *                 supported formats.
     * @param id       id to be set for this texture.
     */
    public Texture(final String fileName, final int id) {
        this.fileName = fileName;
        this.id = id;
        file = new File(fileName);
        width = height = -1;
        valid = false;
    }

    /**
     * Returns path to file containing texture image in any of the supported
     * formats.
     *
     * @return path to file containing texture image in any of the supported
     * formats.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Indicates if file name has been provided or not.
     *
     * @return true if file names has been provided, false otherwise.
     */
    public boolean isFileNameAvailable() {
        return fileName != null;
    }

    /**
     * Returns id assigned to this texture. Id must be unique so that texture
     * can be uniquely identified.
     *
     * @return id assigned to this texture.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns reference to file actually containing the texture image.
     *
     * @return reference to file actually containing the texture image.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets file containing the texture image.
     *
     * @param file file containing the texture image.
     */
    public void setFile(final File file) {
        this.file = file;
    }

    /**
     * Indicates if file containing the texture image has already been provided.
     *
     * @return true if file containing the texture image is available, false
     * otherwise.
     */
    public boolean isFileAvailable() {
        return file != null;
    }

    /**
     * Returns texture image width expressed in pixels.
     *
     * @return texture image width expressed in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets texture image width expressed in pixels.
     *
     * @param width texture image width to be set and expressed in pixels.
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Indicates whether width has been provided (when its value is positive).
     *
     * @return true if width is available, false otherwise.
     */
    public boolean isWidthAvailable() {
        return width >= 0;
    }

    /**
     * Returns texture image height expressed in pixels.
     *
     * @return texture image height expressed in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets texture image height expressed in pixels.
     *
     * @param height texture image height to be set and expressed in pixels.
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Indicates whether height has been provided (when its value is positive).
     *
     * @return true if height is available, false otherwise.
     */
    public boolean isHeightAvailable() {
        return height >= 0;
    }

    /**
     * Indicates if texture is valid or not. A texture will be considered to be
     * invalid when its image cannot be loaded because the file cannot be found,
     * it is corrupted or its format is not supported.
     *
     * @return true if texture is valid, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Specifies whether a texture is valid or not. A texture will be considered
     * to be invalid when its image cannot be loaded because the file cannot be
     * found, it is corrupted or its format is not supported.
     *
     * @param valid whether texture is to be set as valid or not.
     */
    public void setValid(final boolean valid) {
        this.valid = valid;
    }
}
