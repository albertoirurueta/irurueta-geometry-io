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
import com.irurueta.statistics.UniformRandomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VertexOBJTest {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 1000;

    @Test
    void testConstructor() {
        final var vertex = new VertexOBJ();

        // test default values
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());
        assertEquals(-1, vertex.getVertexIndex());
        assertFalse(vertex.isVertexIndexAvailable());
        assertEquals(-1, vertex.getNormalIndex());
        assertFalse(vertex.isNormalIndexAvailable());
        assertEquals(-1, vertex.getTextureIndex());
        assertFalse(vertex.isTextureIndexAvailable());
    }

    @Test
    void testGetSetVertex() {
        final var vertex = new VertexOBJ();

        final var point = Point3D.create();

        // check default value
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());

        // set vertex
        vertex.setVertex(point);
        // check correctness
        assertSame(point, vertex.getVertex());
        assertTrue(vertex.isVertexAvailable());
    }

    @Test
    void testGetSetVertexIndex() {
        final var vertex = new VertexOBJ();

        // check default value
        assertEquals(-1, vertex.getVertexIndex());
        assertFalse(vertex.isVertexIndexAvailable());

        // set new value
        final var randomizer = new UniformRandomizer();
        final var vertexIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setVertexIndex(vertexIndex);
        // check correctness
        assertEquals(vertexIndex, vertex.getVertexIndex());
        assertTrue(vertex.isVertexIndexAvailable());
    }

    @Test
    void testGetSetNormalIndex() {
        final var vertex = new VertexOBJ();

        // check default value
        assertEquals(-1, vertex.getNormalIndex());
        assertFalse(vertex.isNormalIndexAvailable());

        // set new value
        final var randomizer = new UniformRandomizer();
        final var normalIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setNormalIndex(normalIndex);
        // check correctness
        assertEquals(normalIndex, vertex.getNormalIndex());
        assertTrue(vertex.isNormalIndexAvailable());
    }

    @Test
    void testGetSetTextureIndex() {
        final var vertex = new VertexOBJ();

        // check default value
        assertEquals(-1, vertex.getNormalIndex());
        assertFalse(vertex.isTextureIndexAvailable());

        // set new value
        final var randomizer = new UniformRandomizer();
        final var textureIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setTextureIndex(textureIndex);
        // check correctness
        assertEquals(textureIndex, vertex.getTextureIndex());
        assertTrue(vertex.isTextureIndexAvailable());
    }
}
