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

import org.junit.Test;

import static org.junit.Assert.*;

public class DataChunkTest {

    @Test
    public void testConstructor() {
        final DataChunk chunk = new DataChunk();

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

        assertEquals(DataChunk.DEFAULT_COLOR_COMPONENTS,
                chunk.getColorComponents());

        assertEquals(Float.MAX_VALUE, chunk.getMinX(), 0.0);
        assertEquals(Float.MAX_VALUE, chunk.getMinY(), 0.0);
        assertEquals(Float.MAX_VALUE, chunk.getMinZ(), 0.0);

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);
        assertEquals(-Float.MAX_VALUE, chunk.getMaxY(), 0.0);
        assertEquals(-Float.MAX_VALUE, chunk.getMaxZ(), 0.0);
    }

    @Test
    public void testGetSetVerticesCoordinatesData() {
        final DataChunk chunk = new DataChunk();

        assertNull(chunk.getVerticesCoordinatesData());
        assertFalse(chunk.isVerticesCoordinatesDataAvailable());

        // set data
        final float[] data = new float[1024];
        chunk.setVerticesCoordinatesData(data);

        // check correctness
        assertEquals(data, chunk.getVerticesCoordinatesData());
        assertTrue(chunk.isVerticesCoordinatesDataAvailable());
    }

    @Test
    public void testGetSetColorData() {
        final DataChunk chunk = new DataChunk();

        assertNull(chunk.getColorData());
        assertFalse(chunk.isColorDataAvailable());

        // set data
        final short[] data = new short[1024];
        chunk.setColorData(data);

        // check correctness
        assertEquals(data, chunk.getColorData());
        assertTrue(chunk.isColorDataAvailable());
    }

    @Test
    public void testGetSetIndicesData() {
        final DataChunk chunk = new DataChunk();

        assertNull(chunk.getIndicesData());
        assertFalse(chunk.isIndicesDataAvailable());

        // set data
        final int[] data = new int[1024];
        chunk.setIndicesData(data);

        // check correctness
        assertEquals(data, chunk.getIndicesData());
        assertTrue(chunk.isIndicesDataAvailable());
    }

    @Test
    public void testGetSetTextureCoordinatesData() {
        final DataChunk chunk = new DataChunk();

        assertNull(chunk.getTextureCoordinatesData());
        assertFalse(chunk.isTextureCoordinatesDataAvailable());

        // set data
        final float[] data = new float[1024];
        chunk.setTextureCoordinatesData(data);

        // check correctness
        assertEquals(data, chunk.getTextureCoordinatesData());
        assertTrue(chunk.isTextureCoordinatesDataAvailable());
    }

    @Test
    public void testGetSetNormalsData() {
        final DataChunk chunk = new DataChunk();

        assertNull(chunk.getNormalsData());
        assertFalse(chunk.isNormalsDataAvailable());

        // set data
        final float[] data = new float[1024];
        chunk.setNormalsData(data);

        // check correctness
        assertEquals(data, chunk.getNormalsData());
        assertTrue(chunk.isNormalsDataAvailable());
    }

    @Test
    public void testGetSetColorComponents() {
        final DataChunk chunk = new DataChunk();

        assertEquals(DataChunk.DEFAULT_COLOR_COMPONENTS, chunk.getColorComponents());

        final int components = 4;

        // set new value
        chunk.setColorComponents(components);
        // check correctness
        assertEquals(components, chunk.getColorComponents());

        // Force IllegalArgumentException
        try {
            chunk.setColorComponents(0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetSetMinX() {
        final DataChunk chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinX(), 0.0);

        // set value
        final float value = 54213.23f;
        chunk.setMinX(value);

        // check correctness
        assertEquals(value, chunk.getMinX(), 0.0);
    }

    @Test
    public void testGetSetMinY() {
        final DataChunk chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinY(), 0.0);

        // set value
        final float value = 533.423f;
        chunk.setMinY(value);

        // check correctness
        assertEquals(value, chunk.getMinY(), 0.0);
    }

    @Test
    public void testGetSetMinZ() {
        final DataChunk chunk = new DataChunk();

        assertEquals(Float.MAX_VALUE, chunk.getMinZ(), 0.0);

        // set value
        final float value = 21542314.2523f;
        chunk.setMinZ(value);

        // check correctness
        assertEquals(value, chunk.getMinZ(), 0.0);
    }

    @Test
    public void testGetSetMaxX() {
        final DataChunk chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);

        // set value
        final float value = 3255.52f;
        chunk.setMaxX(value);

        // check correctness
        assertEquals(value, chunk.getMaxX(), 0.0);
    }

    @Test
    public void testGetSetMaxY() {
        final DataChunk chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxX(), 0.0);

        // set value
        final float value = 43215324.32231f;
        chunk.setMaxY(value);

        // check correctness
        assertEquals(value, chunk.getMaxY(), 0.0);
    }

    @Test
    public void testGetSetMaxZ() {
        final DataChunk chunk = new DataChunk();

        assertEquals(-Float.MAX_VALUE, chunk.getMaxZ(), 0.0);

        // set value
        final float value = 4514235.3245f;
        chunk.setMaxZ(value);

        // check correctness
        assertEquals(value, chunk.getMaxZ(), 0.0);
    }

    @Test
    public void testGetSetMaterial() {
        final DataChunk chunk = new DataChunk();

        // check default values
        assertNull(chunk.getMaterial());
        assertFalse(chunk.isMaterialAvailable());

        final Material material = new Material();

        // set new material
        chunk.setMaterial(material);
        // check correctness
        assertSame(material, chunk.getMaterial());
        assertTrue(chunk.isMaterialAvailable());
    }
}
