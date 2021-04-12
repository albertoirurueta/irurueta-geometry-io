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

import com.irurueta.statistics.UniformRandomizer;
import org.junit.Test;

import java.io.File;
import java.util.Random;

import static org.junit.Assert.*;

public class TextureTest {

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    @Test
    public void testConstructorAndGetFileName() {

        final String fileName = "fake.png";
        final int id = 1;
        Texture tex = new Texture(fileName, id);

        // check correctness
        assertNotNull(tex);
        assertEquals(tex.getFileName(), fileName);
        assertEquals(tex.getId(), id);
        assertTrue(tex.isFileNameAvailable());
        assertNotNull(tex.getFile());
        assertEquals(tex.getFile().getName(), fileName);
        assertTrue(tex.isFileAvailable());
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());
        assertFalse(tex.isValid());

        tex = new Texture(id);

        // check correctness
        assertTrue(tex.getFileName().isEmpty());
        assertEquals(tex.getId(), id);
        assertTrue(tex.isFileNameAvailable());
        assertNull(tex.getFile());
        assertFalse(tex.isFileAvailable());
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());
        assertFalse(tex.isValid());
    }

    @Test
    public void testGetSetFile() {
        final String fileName = "fake.png";
        final int id = 1;
        final Texture tex = new Texture(fileName, id);

        // check correctness
        assertNotNull(tex);
        assertEquals(tex.getFileName(), fileName);
        assertEquals(tex.getId(), id);
        assertNotNull(tex.getFile());
        assertEquals(tex.getFile().getName(), fileName);
        assertTrue(tex.isFileAvailable());

        final File f1 = tex.getFile();

        final String fileName2 = "fake2.png";
        final File f2 = new File(fileName2);

        tex.setFile(f2);
        assertNotSame(tex.getFile(), f1);
        assertSame(tex.getFile(), f2);
        assertEquals(tex.getFile().getName(), fileName2);
        assertTrue(tex.isFileAvailable());

        tex.setFile(null);
        assertNull(tex.getFile());
        assertFalse(tex.isFileAvailable());
    }

    @Test
    public void testGetSetWidth() {
        final int id = 1;
        final Texture tex = new Texture("fake.png", id);

        // check default value
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());

        // set new value
        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int width = randomizer.nextInt(MIN_SIZE, MAX_SIZE);

        tex.setWidth(width);
        // check correctness
        assertEquals(tex.getWidth(), width);
        assertTrue(tex.isWidthAvailable());
        assertEquals(tex.getId(), id);
    }

    @Test
    public void testGetSetHeight() {
        final int id = 1;
        final Texture tex = new Texture("fake.png", id);

        // check default value
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());

        // set new value
        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int height = randomizer.nextInt(MIN_SIZE, MAX_SIZE);

        tex.setHeight(height);
        // check correctness
        assertEquals(tex.getHeight(), height);
        assertTrue(tex.isHeightAvailable());
        assertEquals(tex.getId(), id);
    }

    @Test
    public void testIsSetValid() {
        final int id = 1;
        final Texture tex = new Texture("fake.png", id);

        // check default value
        assertFalse(tex.isValid());

        // set valid
        tex.setValid(true);
        // check correctness
        assertTrue(tex.isValid());

        // unset valid
        tex.setValid(false);
        // check correctness
        assertFalse(tex.isValid());
        assertEquals(tex.getId(), id);
    }
}
