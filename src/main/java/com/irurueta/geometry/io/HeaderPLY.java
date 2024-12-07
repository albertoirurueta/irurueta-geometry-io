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

import java.util.LinkedList;
import java.util.List;

/**
 * This class contains elements of the header of a PLY file.
 * The header of a ply file is stored in text form at the beginning of the file.
 */
public class HeaderPLY {
    /**
     * Indicates storage mode of the file. This can be either binary big-endian,
     * binary little-endian or ascii text.
     */
    private PLYStorageMode storageMode;

    /**
     * List containing all the elements forming the data of the file.
     */
    private final List<ElementPLY> elements;

    /**
     * List containing all the string comments that the author might have stored
     * in the file.
     */
    private final List<String> comments;

    /**
     * List of strings containing addition object information.
     */
    private final List<String> objInfos;

    /**
     * Constructor
     */
    public HeaderPLY() {
        storageMode = PLYStorageMode.PLY_ASCII;
        elements = new LinkedList<>();
        comments = new LinkedList<>();
        objInfos = new LinkedList<>();
    }

    /**
     * Returns storage mode of this file.
     *
     * @return Storage mode of this file.
     */
    public PLYStorageMode getStorageMode() {
        return storageMode;
    }

    /**
     * Sets storage mode of this file.
     *
     * @param storageMode Storage mode to be set.
     */
    public void setStorageMode(final PLYStorageMode storageMode) {
        this.storageMode = storageMode;
    }

    /**
     * Returns the structure of all the elements forming the data of this file.
     *
     * @return All the elements forming the data of this file.
     */
    public List<ElementPLY> getElements() {
        return elements;
    }

    /**
     * Returns a list of strings containing all the comments set by the author
     * of this file.
     *
     * @return Comments of this file.
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * Returns list of strings containing additional object information.
     *
     * @return Additional object information.
     */
    public List<String> getObjInfos() {
        return objInfos;
    }

    /**
     * Converts header data into string format ready to be saved on a PLY file.
     *
     * @return Header data into string format.
     */
    @Override
    public String toString() {
        final var builder = new StringBuilder("ply\n");

        if (storageMode != null) {
            builder.append("format ");
            switch (storageMode) {
                case PLY_ASCII:
                    builder.append("ascii ");
                    break;
                case PLY_LITTLE_ENDIAN:
                    builder.append("binary_little_endian ");
                    break;
                case PLY_BIG_ENDIAN:
                    builder.append("binary_big_endian ");
                    break;
                default:
                    break;
            }
        }

        // add version
        builder.append("1.0\n");

        // add comments
        for (final var comment : comments) {
            builder.append("comment ").append(comment).append("\n");
        }

        // add obj_infos
        for (final var objInfo : objInfos) {
            builder.append("obj_info ").append(objInfo).append("\n");
        }

        // dd elements
        for (final var element : elements) {
            // NOTE: elements already contain carrier return on their textual
            // representation
            builder.append(element.toString());
        }

        builder.append("end_header\n");

        return builder.toString();
    }
}
