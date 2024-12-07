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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataChunkTest {

    @Test
    void testConstructor() {
        final var chunk = new DataChunk();

        assertNull(chunk.getVerticesCoordinatesData());
        assertFalse(chunk.isVerticesCoordinatesDataAvailable());
        assertNull(chunk.getColorData());
        assertFalse(chunk.isColorDataAvailable());
        assertNull(chunk.getIndicesData());
        assertFalse(chunk.isIndicesDataAvailable());
        assertNull(chunk.getTextureCoordinatesData());
        assertFalse(chunk.isTextureCoordinatesDataAvailable());
        assertNull(chunk.getNormalsData());
        assertFalse(chunk.isNormalsDataAvailable());

        assertEquals(DataChunk.DEFAULT_COLOR_COMPONENTS, chunk.getColorComponents());

        assertEquals(Float.MAX_VALUE, chunk.getMinX(), 0.0);
        assertEquals(Float.MAX_VALUE, chunk.getMinY(), 0.0);
        assertEquals(Float.MAX_VALUE, chunk.getMinZ(), 0.0);

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);
        assertEquals(-Float.MAX_VALUE, chunk.getMaxY(), 0.0);
        assertEquals(-Float.MAX_VALUE, chunk.getMaxZ(), 0.0);
    }

    @Test
    void testGetSetVerticesCoordinatesData() {
        final var chunk = new DataChunk();

        assertNull(chunk.getVerticesCoordinatesData());
        assertFalse(chunk.isVerticesCoordinatesDataAvailable());

        // set data
        final var data = new float[1024];
        chunk.setVerticesCoordinatesData(data);

        // check correctness
        assertEquals(data, chunk.getVerticesCoordinatesData());
        assertTrue(chunk.isVerticesCoordinatesDataAvailable());
    }

    @Test
    void testGetSetColorData() {
        final var chunk = new DataChunk();

        assertNull(chunk.getColorData());
        assertFalse(chunk.isColorDataAvailable());

        // set data
        final var data = new short[1024];
        chunk.setColorData(data);

        // check correctness
        assertEquals(data, chunk.getColorData());
        assertTrue(chunk.isColorDataAvailable());
    }

    @Test
    void testGetSetIndicesData() {
        final var chunk = new DataChunk();

        assertNull(chunk.getIndicesData());
        assertFalse(chunk.isIndicesDataAvailable());

        // set data
        final var data = new int[1024];
        chunk.setIndicesData(data);

        // check correctness
        assertEquals(data, chunk.getIndicesData());
        assertTrue(chunk.isIndicesDataAvailable());
    }

    @Test
    void testGetSetTextureCoordinatesData() {
        final var chunk = new DataChunk();

        assertNull(chunk.getTextureCoordinatesData());
        assertFalse(chunk.isTextureCoordinatesDataAvailable());

        // set data
        final var data = new float[1024];
        chunk.setTextureCoordinatesData(data);

        // check correctness
        assertEquals(data, chunk.getTextureCoordinatesData());
        assertTrue(chunk.isTextureCoordinatesDataAvailable());
    }

    @Test
    void testGetSetNormalsData() {
        final var chunk = new DataChunk();

        assertNull(chunk.getNormalsData());
        assertFalse(chunk.isNormalsDataAvailable());

        // set data
        final var data = new float[1024];
        chunk.setNormalsData(data);

        // check correctness
        assertEquals(data, chunk.getNormalsData());
        assertTrue(chunk.isNormalsDataAvailable());
    }

    @Test
    void testGetSetColorComponents() {
        final var chunk = new DataChunk();

        assertEquals(DataChunk.DEFAULT_COLOR_COMPONENTS, chunk.getColorComponents());

        final var components = 4;

        // set new value
        chunk.setColorComponents(components);
        // check correctness
        assertEquals(components, chunk.getColorComponents());

        // Force IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> chunk.setColorComponents(0));
    }

    @Test
    void testGetSetMinX() {
        final var chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinX(), 0.0);

        // set value
        final var value = 54213.23f;
        chunk.setMinX(value);

        // check correctness
        assertEquals(value, chunk.getMinX(), 0.0);
    }

    @Test
    void testGetSetMinY() {
        final var chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinY(), 0.0);

        // set value
        final var value = 533.423f;
        chunk.setMinY(value);

        // check correctness
        assertEquals(value, chunk.getMinY(), 0.0);
    }

    @Test
    void testGetSetMinZ() {
        final var chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinZ(), 0.0);

        // set value
        final var value = 21542314.2523f;
        chunk.setMinZ(value);

        // check correctness
        assertEquals(value, chunk.getMinZ(), 0.0);
    }

    @Test
    void testGetSetMaxX() {
        final var chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);

        // set value
        final var value = 3255.52f;
        chunk.setMaxX(value);

        // check correctness
        assertEquals(value, chunk.getMaxX(), 0.0);
    }

    @Test
    void testGetSetMaxY() {
        final var chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);

        // set value
        final var value = 43215324.32231f;
        chunk.setMaxY(value);

        // check correctness
        assertEquals(value, chunk.getMaxY(), 0.0);
    }

    @Test
    void testGetSetMaxZ() {
        final var chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxZ(), 0.0);

        // set value
        final var value = 4514235.3245f;
        chunk.setMaxZ(value);

        // check correctness
        assertEquals(value, chunk.getMaxZ(), 0.0);
    }

    @Test
    void testGetSetMaterial() {
        final var chunk = new DataChunk();

        // check default values
        assertNull(chunk.getMaterial());
        assertFalse(chunk.isMaterialAvailable());

        final var material = new Material();

        // set new material
        chunk.setMaterial(material);
        // check correctness
        assertSame(material, chunk.getMaterial());
        assertTrue(chunk.isMaterialAvailable());
    }
}
