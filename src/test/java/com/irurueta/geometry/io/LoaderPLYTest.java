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

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LoaderPLYTest implements LoaderListener {

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @Test
    void testConstants() {
        assertEquals(50000000, Loader.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        assertEquals(8, LoaderPLY.BUFFER_SIZE);
        assertEquals(0xffff, LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(1, LoaderPLY.MIN_MAX_VERTICES_IN_CHUNK);
        assertFalse(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(1000000, LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertEquals(1, LoaderPLY.MIN_STREAM_POSITIONS);
        assertEquals(0.01f, LoaderPLY.PROGRESS_DELTA, 0.0f);
    }

    @Test
    void testConstructors() throws IOException {
        var loader = new LoaderPLY();
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with maxVerticesInChunk
        final var maxVerticesInChunk = 21423;
        loader = new LoaderPLY(maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(0));

        // test constructor with maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(0, true));

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final var maxStreamPositions = 500L;
        loader = new LoaderPLY(maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(maxVerticesInChunk, true, 0));

        // constructor with file
        final var f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        final var badF = new File("./non-existing");
        assertFalse(badF.exists());

        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF));

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, 0));

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, 0, true));

        // test constructor with file, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(IOException.class,
                () -> new LoaderPLY(badF, maxVerticesInChunk, true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, 0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, maxVerticesInChunk, true, 0));

        // Test constructor with loader listener
        loader = new LoaderPLY(this);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderPLY(this, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(this, 0));

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(this, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(this, 0, true));

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(this, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(this, 0,
                true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(this, maxVerticesInChunk,
                true, 0));

        // test constructor with file and listener
        loader = new LoaderPLY(f, this);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF, this));

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderPLY(f, this, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF, this, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, this, 0));

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, this, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class,
                () -> new LoaderPLY(badF, this, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, this, 0, true));

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, this, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(this, loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderPLY(badF, this, maxVerticesInChunk,
                true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, this, 0,
                true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, this, maxVerticesInChunk,
                true, 0));
    }

    @Test
    void testHasSetFileAndIsReady() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        try (var loader = new LoaderPLY()) {
            assertFalse(loader.hasFile());
            assertFalse(loader.isReady());

            // set file
            loader.setFile(f);
            assertTrue(loader.hasFile());
            assertTrue(loader.isReady());
        }
    }

    @Test
    void testGetSetListener() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {
            assertNull(loader.getListener());

            loader.setListener(this);
            assertEquals(this, loader.getListener());
        }
    }

    @Test
    void testGetSetMaxVerticesInChunk() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {
            final var maxVerticesInChunk = 521351;

            assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());

            // set new value
            loader.setMaxVerticesInChunk(maxVerticesInChunk);
            // check correctness
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());

            // Force IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> loader.setMaxVerticesInChunk(0));
        }
    }

    @Test
    void testGetSetAllowDuplicateVerticesInChunk() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {

            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                    loader.areDuplicateVerticesInChunkAllowed());

            // set new value
            loader.setAllowDuplicateVerticesInChunk(true);
            // check correctness
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        }
    }

    @Test
    void testGetSetMaxStreamPositions() throws LockedException, IOException {
        final var maxStreamPositions = 400L;
        try (final var loader = new LoaderPLY()) {

            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());

            // set new value
            loader.setMaxStreamPositions(maxStreamPositions);
            // check correctness
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());

            // Force IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> loader.setMaxStreamPositions(0));
        }
    }

    @Test
    void testIsValidFile() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final var loader = new LoaderPLY(f);
        assertTrue(loader.isValidFile());
        // to free file resources
        loader.close();
    }

    @Test
    void testLoadAndIterate() throws IOException, LockedException, LoaderException, NotReadyException,
            NotAvailableException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        final var maxNumberOfVerticesInChunk = 500;
        try (final var loader = new LoaderPLY(f, maxNumberOfVerticesInChunk, false)) {
            loader.setListener(this);

            final var it = loader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (it.hasNext()) {
                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var chunk = it.next();

                final var vertices = chunk.getVerticesCoordinatesData();
                final var texture = chunk.getTextureCoordinatesData();
                final var colors = chunk.getColorData();
                final var normals = chunk.getNormalsData();
                final var indices = chunk.getIndicesData();

                assertEquals(vertices.length, normals.length);
                assertNull(texture);
                assertNotNull(colors);

                final var nVerticesInChunk = vertices.length / 3;

                for (var index : indices) {
                    assertTrue(index < nVerticesInChunk);
                }

                // check bounding box values
                assertTrue(chunk.getMinX() <= chunk.getMaxX());
                assertTrue(chunk.getMinY() <= chunk.getMaxY());
                assertTrue(chunk.getMinZ() <= chunk.getMaxZ());
            }

            if (it.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Override
    public void onLoadStart(Loader loader) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        checkLocked((LoaderPLY) loader);
    }

    @Override
    public void onLoadEnd(Loader loader) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        checkLocked((LoaderPLY) loader);
    }

    @Override
    public void onLoadProgressChange(Loader loader, float progress) {
        if ((progress < 0.0) || (progress > 1.0)) {
            progressValid = false;
        }
        if (progress < previousProgress) {
            progressValid = false;
        }
        previousProgress = progress;

        checkLocked((LoaderPLY) loader);
    }

    private static void checkLocked(final LoaderPLY loader) {
        assertTrue(loader.isLocked());
        assertThrows(LockedException.class, () -> loader.setMaxVerticesInChunk(0));
        assertThrows(LockedException.class, () -> loader.setAllowDuplicateVerticesInChunk(false));
        assertThrows(LockedException.class, () -> loader.setMaxStreamPositions(1));
        assertThrows(LockedException.class, loader::isValidFile);
        assertThrows(LockedException.class, loader::load);
    }

    private void resetListener() {
        startValid = endValid = progressValid = lockedValid = true;
        startCounter = endCounter = 0;
        previousProgress = 0.0f;
    }

    private boolean isStartValid() {
        return startValid;
    }

    private boolean isEndValid() {
        return endValid;
    }

    private boolean isProgressValid() {
        return progressValid;
    }

    private boolean isLockedValid() {
        return lockedValid;
    }
}
