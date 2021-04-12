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
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class VertexOBJTest {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 1000;

    @Test
    public void testConstructor() {
        final VertexOBJ vertex = new VertexOBJ();

        // test default values
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());
        assertEquals(vertex.getVertexIndex(), -1);
        assertFalse(vertex.isVertexIndexAvailable());
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isNormalIndexAvailable());
        assertEquals(vertex.getTextureIndex(), -1);
        assertFalse(vertex.isTextureIndexAvailable());
    }

    @Test
    public void testGetSetVertex() {
        final VertexOBJ vertex = new VertexOBJ();

        final Point3D point = Point3D.create();

        // check default value
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());

        // set vertex
        vertex.setVertex(point);
        // check correctness
        assertSame(vertex.getVertex(), point);
        assertTrue(vertex.isVertexAvailable());
    }

    @Test
    public void testGetSetVertexIndex() {
        final VertexOBJ vertex = new VertexOBJ();

        // check default value
        assertEquals(vertex.getVertexIndex(), -1);
        assertFalse(vertex.isVertexIndexAvailable());

        // set new value
        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int vertexIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setVertexIndex(vertexIndex);
        // check correctness
        assertEquals(vertex.getVertexIndex(), vertexIndex);
        assertTrue(vertex.isVertexIndexAvailable());
    }

    @Test
    public void testGetSetNormalIndex() {
        final VertexOBJ vertex = new VertexOBJ();

        // check default value
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isNormalIndexAvailable());

        // set new value
        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int normalIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setNormalIndex(normalIndex);
        // check correctness
        assertEquals(vertex.getNormalIndex(), normalIndex);
        assertTrue(vertex.isNormalIndexAvailable());
    }

    @Test
    public void testGetSetTextureIndex() {
        final VertexOBJ vertex = new VertexOBJ();

        // check default value
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isTextureIndexAvailable());

        // set new value
        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int textureIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);

        // set value
        vertex.setTextureIndex(textureIndex);
        // check correctness
        assertEquals(vertex.getTextureIndex(), textureIndex);
        assertTrue(vertex.isTextureIndexAvailable());
    }
}
