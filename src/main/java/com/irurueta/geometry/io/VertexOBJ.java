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

import com.irurueta.geometry.Point3D;

/**
 * This class defines the structure that contains data for a single vertex in
 * an OBJ file.
 */
public class VertexOBJ {

    /**
     * Contains coordinates of 3D point.
     */
    private Point3D vertex;

    /**
     * Index of vertex in OBJ file.
     */
    private int vertexIndex;

    /**
     * Index of normal corresponding to this vertex in OBJ file.
     */
    private int normalIndex;

    /**
     * Index of texture assigned to this vertex in OBJ file.
     */
    private int textureIndex;


    /**
     * Constructor.
     */
    public VertexOBJ() {
        vertex = null;
        vertexIndex = normalIndex = textureIndex = -1;
    }

    /**
     * Returns coordinates of 3D point.
     *
     * @return coordinates of 3D point.
     */
    public Point3D getVertex() {
        return vertex;
    }

    /**
     * Sets coordinates of 3D point.
     *
     * @param vertex coordinates of 3D point.
     */
    public void setVertex(final Point3D vertex) {
        this.vertex = vertex;
    }

    /**
     * Indicates whether 3D point has been provided or not.
     *
     * @return true if 3D point is available, false otherwise.
     */
    public boolean isVertexAvailable() {
        return vertex != null;
    }

    /**
     * Returns index of vertex in OBJ file.
     *
     * @return index of vertex in OBJ file.
     */
    public int getVertexIndex() {
        return vertexIndex;
    }

    /**
     * Sets index of vertex in OBJ file.
     *
     * @param vertexIndex index of vertex in OBJ file.
     */
    public void setVertexIndex(final int vertexIndex) {
        this.vertexIndex = vertexIndex;
    }

    /**
     * Indicates whether vertex index has been provided or not.
     *
     * @return true if vertex index is available, false otherwise.
     */
    public boolean isVertexIndexAvailable() {
        return vertexIndex >= 0;
    }

    /**
     * Returns index of normal corresponding to this vertex in OBJ file.
     *
     * @return index of normal corresponding to this vertex in OBJ file.
     */
    public int getNormalIndex() {
        return normalIndex;
    }

    /**
     * Sets index of normal corresponding to this vertex in OBJ file.
     *
     * @param normalIndex index of normal corresponding to this vertex in OBJ
     *                    file.
     */
    public void setNormalIndex(final int normalIndex) {
        this.normalIndex = normalIndex;
    }

    /**
     * Indicates whether normal index has been provided or not.
     *
     * @return true if normal index is available, false otherwise.
     */
    public boolean isNormalIndexAvailable() {
        return normalIndex >= 0;
    }

    /**
     * Returns index of texture assigned to this vertex in OBJ file.
     *
     * @return index of texture assigned to this vertex in OBJ file.
     */
    public int getTextureIndex() {
        return textureIndex;
    }

    /**
     * Sets index of texture assigned to this vertex in OBJ file.
     *
     * @param textureIndex index of texture assigned to this vertex in OBJ file.
     */
    public void setTextureIndex(final int textureIndex) {
        this.textureIndex = textureIndex;
    }

    /**
     * Indicates if texture assigned to this vertex is available or not
     *
     * @return true if available, false otherwise.
     */
    public boolean isTextureIndexAvailable() {
        return textureIndex >= 0;
    }
}
