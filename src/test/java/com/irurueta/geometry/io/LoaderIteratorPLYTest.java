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
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LoaderIteratorPLYTest implements LoaderListener {

    private static final double ERROR = 1e-4;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @Test
    void testConstructors() throws IOException {

        // test empty constructor
        try (final var loader = new LoaderPLY()) {
            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // test constructor with maxVerticesInChunk
        final var maxVerticesInChunk = 21423;
        try (final var loader = new LoaderPLY(maxVerticesInChunk)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(0));

        // test constructor with maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        try (final var loader = new LoaderPLY(maxVerticesInChunk, true)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(0, true));

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final var maxStreamPositions = 500L;
        try (final var loader = new LoaderPLY(maxVerticesInChunk, true, maxStreamPositions)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(maxVerticesInChunk, true, 0));

        // constructor with file
        final var f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        try (final var loader = new LoaderPLY(f)) {
            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
        }

        // Force FileNotFoundException
        final var badF = new File("./non-existing");
        assertFalse(badF.exists());

        //noinspection resource
        assertThrows(FileNotFoundException.class, () -> new LoaderPLY(badF));

        // test constructor with file and maxVerticesInChunk
        try (final var loader = new LoaderPLY(f, maxVerticesInChunk)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class, () -> new LoaderPLY(badF, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, 0));

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        try (final var loader = new LoaderPLY(f, maxVerticesInChunk, true)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class,
                () -> new LoaderPLY(badF, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, 0, true));

        // test constructor with file, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        try (final var loader = new LoaderPLY(f, maxVerticesInChunk, true,
                maxStreamPositions)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class,
                () -> new LoaderPLY(badF, maxVerticesInChunk, true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, 0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, maxVerticesInChunk, true, 0));

        // Test constructor with loader listener
        final var listener = new LoaderListener() {
            @Override
            public void onLoadStart(final Loader loader) {
                // no action needed
            }

            @Override
            public void onLoadEnd(final Loader loader) {
                // no action needed
            }

            @Override
            public void onLoadProgressChange(final Loader loader, final float progress) {
                // no action needed
            }
        };

        try (final var loader = new LoaderPLY(listener)) {
            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // test constructor with loader listener and maxVerticesInChunk
        try (final var loader = new LoaderPLY(listener, maxVerticesInChunk)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(listener, 0));

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        try (final var loader = new LoaderPLY(listener, maxVerticesInChunk, true)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(listener, 0, true));

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        try (final var loader = new LoaderPLY(listener, maxVerticesInChunk, true,
                maxStreamPositions)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(listener, 0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(listener, maxVerticesInChunk, true, 0));

        // test constructor with file and listener
        try (final var loader = new LoaderPLY(f, listener)) {
            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                    loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class, () -> new LoaderPLY(badF, listener));

        // test constructor with file, listener and maxVerticesInChunk
        try (final var loader = new LoaderPLY(f, listener, maxVerticesInChunk)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                    loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class, () -> new LoaderPLY(badF, listener, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, listener, 0));

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        try (final var loader = new LoaderPLY(f, listener, maxVerticesInChunk, true)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class,
                () -> new LoaderPLY(badF, listener, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderPLY(f, listener, 0, true));

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        try (final var loader = new LoaderPLY(f, listener, maxVerticesInChunk, true,
                maxStreamPositions)) {
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
        }

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(FileNotFoundException.class, () -> new LoaderPLY(badF, listener, maxVerticesInChunk,
                true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, listener, 0,
                true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderPLY(f, listener, maxVerticesInChunk,
                true, 0));
    }

    @Test
    void testHasSetFileAndIsReady() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        try (final var loader = new LoaderPLY()) {
            assertFalse(loader.hasFile());
            assertFalse(loader.isReady());

            // set file
            loader.setFile(f);
            assertTrue(loader.hasFile());
            assertTrue(loader.isReady());
        }
    }

    @Test
    void testGetSetFileSizeLimitToKeepInMemory() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {
            assertEquals(LoaderPLY.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY, loader.getFileSizeLimitToKeepInMemory());

            // set new value
            final var value = 1000000L;
            loader.setFileSizeLimitToKeepInMemory(value);
            // and check correctness
            assertEquals(value, loader.getFileSizeLimitToKeepInMemory());
        }
    }

    @Test
    void testGetSetListener() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {
            assertNull(loader.getListener());

            final var listener = new LoaderListener() {
                @Override
                public void onLoadStart(final Loader loader) {
                    // no action needed
                }

                @Override
                public void onLoadEnd(final Loader loader) {
                    // no action needed
                }

                @Override
                public void onLoadProgressChange(final Loader loader, float progress) {
                    // no action needed
                }
            };

            loader.setListener(listener);
            assertEquals(listener, loader.getListener());
        }
    }

    @Test
    void testGetSetMaxVerticesInChunk() throws LockedException, IOException {
        try (final var loader = new LoaderPLY()) {
            final var maxVerticesInChunk = 521351;

            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());

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
        // Test for BIG ENDIAN
        var f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        try (final var loader = new LoaderPLY(f)) {
            assertTrue(loader.isValidFile());

            // Test for LITTLE ENDIAN
            f = new File("./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
            // check that file exists
            assertTrue(f.exists());
            assertTrue(f.isFile());
        }

        try (final var loader = new LoaderPLY(f)){
            assertTrue(loader.isValidFile());

            // Test for ASCII
            f = new File("./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
            // check that file exists
            assertTrue(f.exists());
            assertTrue(f.isFile());
        }

        try (final var loader = new LoaderPLY(f)) {
            assertTrue(loader.isValidFile());
        }
    }

    @Test
    void testLoad() throws LockedException, NotReadyException, IOException, LoaderException {
        // Test for BIG ENDIAN
        var f = new File("./src/test/java/com/irurueta/geometry/io/randomBig.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        var loader = new LoaderPLY(f);
        loader.setListener(this);
        assertNotNull(loader.load());
        // releases file resources
        loader.close();

        // Test for LITTLE_ENDIAN
        f = new File("./src/test/java/com/irurueta/geometry/io/randomLittle.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertNotNull(loader.load());
        // releases file resources
        loader.close();

        // Test for ASCII
        f = new File("./src/test/java/com/irurueta/geometry/io/randomAscii.ply");
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertNotNull(loader.load());
        // releases file resources
        loader.close();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
    }

    @Test
    void testLoadAndIterate() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {
        final var maxNumberOfVerticesInChunk = 9;

        final var fileAscii = new File("./src/test/java/com/irurueta/geometry/io/randomAscii.ply");
        final var fileLittle = new File("./src/test/java/com/irurueta/geometry/io/randomLittle.ply");
        final var fileBig = new File("./src/test/java/com/irurueta/geometry/io/randomBig.ply");

        final var asciiLoader = new LoaderPLY(fileAscii, maxNumberOfVerticesInChunk);
        asciiLoader.setListener(this);
        final var littleLoader = new LoaderPLY(fileLittle, maxNumberOfVerticesInChunk);
        littleLoader.setListener(this);
        final var bigLoader = new LoaderPLY(fileBig, maxNumberOfVerticesInChunk);
        bigLoader.setListener(this);

        final var asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final var littleIt = littleLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final var bigIt = bigLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            final var asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var littleChunk = littleIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var bigChunk = bigIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final var asciiColors = asciiChunk.getColorData();
            final var asciiIndices = asciiChunk.getIndicesData();

            final var littleVertices = littleChunk.getVerticesCoordinatesData();
            final var littleColors = littleChunk.getColorData();
            final var littleIndices = littleChunk.getIndicesData();

            final var bigVertices = bigChunk.getVerticesCoordinatesData();
            final var bigColors = bigChunk.getColorData();
            final var bigIndices = bigChunk.getIndicesData();

            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);

            final var nVerticesInChunk = asciiVertices.length / 3;

            if (asciiIt.hasNext()) {
                assertEquals((maxNumberOfVerticesInChunk / 3) * 3, nVerticesInChunk);
            }

            assertEquals(nVerticesInChunk, littleVertices.length / 3);
            assertEquals(nVerticesInChunk, bigVertices.length / 3);

            var minX = Float.MAX_VALUE;
            var minY = Float.MAX_VALUE;
            var minZ = Float.MAX_VALUE;
            var maxX = -Float.MAX_VALUE;
            var maxY = -Float.MAX_VALUE;
            var maxZ = -Float.MAX_VALUE;
            float x;
            float y;
            float z;
            for (var i = 0; i < nVerticesInChunk; i++) {
                // check x coordinate
                x = littleVertices[3 * i];
                assertEquals(asciiVertices[3 * i], littleVertices[3 * i], ERROR);
                assertEquals(littleVertices[3 * i], bigVertices[3 * i], 0.0);
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }

                // check that values are integers in floating point format
                // (the files contains vertices as integer values)
                assertEquals(Math.round(asciiVertices[3 * i]), asciiVertices[3 * i], ERROR);
                assertEquals(Math.round(littleVertices[3 * i]), littleVertices[3 * i], ERROR);
                assertEquals(Math.round(bigVertices[3 * i]), bigVertices[3 * i], ERROR);

                // check y coordinate
                y = littleVertices[3 * i + 1];
                assertEquals(asciiVertices[3 * i + 1], littleVertices[3 * i + 1], ERROR);
                assertEquals(littleVertices[3 * i + 1], bigVertices[3 * i + 1], 0.0);
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }

                // check that values are integers in floating point format
                // (the files contains vertices as integer values)
                assertEquals(Math.round(asciiVertices[3 * i + 1]), asciiVertices[3 * i + 1], ERROR);
                assertEquals(Math.round(littleVertices[3 * i + 1]), littleVertices[3 * i + 1], ERROR);
                assertEquals(Math.round(bigVertices[3 * i + 1]), bigVertices[3 * i + 1], ERROR);

                // check z coordinate
                z = littleVertices[3 * i + 2];
                assertEquals(asciiVertices[3 * i + 2], littleVertices[3 * i + 2], ERROR);
                assertEquals(littleVertices[3 * i + 2], bigVertices[3 * i + 2], 0.0);
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }

                // check that values are integers in floating point format
                // (the files contains vertices as integer values)
                assertEquals(Math.round(asciiVertices[3 * i + 2]), asciiVertices[3 * i + 2], ERROR);
                assertEquals(Math.round(littleVertices[3 * i + 2]), littleVertices[3 * i + 2], ERROR);
                assertEquals(Math.round(bigVertices[3 * i + 2]), bigVertices[3 * i + 2], ERROR);

                // check red color
                assertEquals(asciiColors[3 * i], littleColors[3 * i]);
                assertEquals(littleColors[3 * i], bigColors[3 * i]);
                assertTrue(asciiColors[3 * i] >= 0 && asciiColors[3 * i] <= 255);

                // check green color
                assertEquals(asciiColors[3 * i + 1], littleColors[3 * i + 1]);
                assertEquals(littleColors[3 * i + 1], bigColors[3 * i + 1]);
                assertTrue(asciiColors[3 * i + 1] >= 0 && asciiColors[3 * i + 1] <= 255);

                // check blue color
                assertEquals(asciiColors[3 * i + 2], littleColors[3 * i + 2]);
                assertEquals(littleColors[3 * i + 2], bigColors[3 * i + 2]);
                assertTrue(asciiColors[3 * i + 2] >= 0 && asciiColors[3 * i + 2] <= 255);
            }

            final var nIndicesInChunk = asciiIndices.length;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);

            for (var i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(littleIndices[i] < nVerticesInChunk);
                assertTrue(bigIndices[i] < nVerticesInChunk);

                assertEquals(asciiIndices[i], littleIndices[i]);
                assertEquals(littleIndices[i], bigIndices[i]);
            }

            // check bounding box values
            assertEquals(minX, asciiChunk.getMinX(), ERROR);
            assertEquals(asciiChunk.getMinX(), littleChunk.getMinX(), ERROR);
            assertEquals(minX, littleChunk.getMinX(), 0.0);
            assertEquals(littleChunk.getMinX(), bigChunk.getMinX(), 0.0);

            assertEquals(minY, asciiChunk.getMinY(), ERROR);
            assertEquals(asciiChunk.getMinY(), littleChunk.getMinY(), ERROR);
            assertEquals(minY, littleChunk.getMinY(), 0.0);
            assertEquals(littleChunk.getMinY(), bigChunk.getMinY(), 0.0);

            assertEquals(minZ, asciiChunk.getMinZ(), ERROR);
            assertEquals(asciiChunk.getMinZ(), littleChunk.getMinZ(), ERROR);
            assertEquals(minZ, littleChunk.getMinZ(), 0.0);
            assertEquals(littleChunk.getMinZ(), bigChunk.getMinZ(), 0.0);

            assertEquals(maxX, asciiChunk.getMaxX(), ERROR);
            assertEquals(asciiChunk.getMaxX(), littleChunk.getMaxX(), ERROR);
            assertEquals(maxX, littleChunk.getMaxX(), 0.0);
            assertEquals(littleChunk.getMaxX(), bigChunk.getMaxX(), 0.0);

            assertEquals(maxY, asciiChunk.getMaxY(), ERROR);
            assertEquals(asciiChunk.getMaxY(), littleChunk.getMaxY(), ERROR);
            assertEquals(maxY, littleChunk.getMaxY(), 0.0);
            assertEquals(littleChunk.getMaxY(), bigChunk.getMaxY(), 0.0);

            assertEquals(maxZ, asciiChunk.getMaxZ(), ERROR);
            assertEquals(asciiChunk.getMaxZ(), littleChunk.getMaxZ(), ERROR);
            assertEquals(maxZ, littleChunk.getMaxZ(), 0.0);
            assertEquals(littleChunk.getMaxZ(), bigChunk.getMaxZ(), 0.0);

            assertTrue(minX <= maxX);
            assertTrue(minY <= maxY);
            assertTrue(minZ <= maxZ);
            assertTrue(asciiChunk.getMinX() <= asciiChunk.getMaxX());
            assertTrue(asciiChunk.getMinY() <= asciiChunk.getMaxY());
            assertTrue(asciiChunk.getMinZ() <= asciiChunk.getMaxZ());
            assertTrue(littleChunk.getMinX() <= littleChunk.getMaxX());
            assertTrue(littleChunk.getMinY() <= littleChunk.getMaxY());
            assertTrue(littleChunk.getMinZ() <= littleChunk.getMaxZ());
            assertTrue(bigChunk.getMinX() <= bigChunk.getMaxX());
            assertTrue(bigChunk.getMinY() <= bigChunk.getMaxY());
            assertTrue(bigChunk.getMinZ() <= bigChunk.getMaxZ());
        }

        if (asciiIt.hasNext()) {
            fail("Wrong number of chunks in ASCII mode");
        }
        if (littleIt.hasNext()) {
            fail("Wrong number of chunks in Little endian mode");
        }
        if (bigIt.hasNext()) {
            fail("Wrong number of chunks in big endian mode");
        }

        // releases file resources
        asciiLoader.close();
        littleLoader.close();
        bigLoader.close();
    }

    @Test
    void testLoadAndIterateRealFile() throws LockedException, NotReadyException, IOException, LoaderException,
            NotAvailableException {

        final var fileAscii = new File("./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
        final var fileLittle = new File("./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
        final var fileBig = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");

        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileLittle.exists());
        assertTrue(fileLittle.isFile());
        assertTrue(fileBig.exists());
        assertTrue(fileBig.isFile());

        final var asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        final var littleLoader = new LoaderPLY(fileLittle);
        littleLoader.setListener(this);
        final var bigLoader = new LoaderPLY(fileBig);
        bigLoader.setListener(this);

        final var asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final var littleIt = littleLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final var bigIt = bigLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // read and compare chunks of data
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            final var asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var littleChunk = littleIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var bigChunk = bigIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final var asciiColors = asciiChunk.getColorData();
            final var asciiIndices = asciiChunk.getIndicesData();

            final var littleVertices = littleChunk.getVerticesCoordinatesData();
            final var littleColors = littleChunk.getColorData();
            final var littleIndices = littleChunk.getIndicesData();

            final var bigVertices = bigChunk.getVerticesCoordinatesData();
            final var bigColors = bigChunk.getColorData();
            final var bigIndices = bigChunk.getIndicesData();

            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);

            // compare ascii, little and big endian chunks
            assertEquals(asciiVertices.length, littleVertices.length);
            assertEquals(asciiVertices.length, bigVertices.length);

            final var arraySize = asciiVertices.length;
            for (var i = 0; i < arraySize; i++) {
                assertEquals(littleVertices[i], bigVertices[i], 0.0);
                assertEquals(asciiVertices[i], littleVertices[i], ERROR);

                assertEquals(littleColors[i], bigColors[i]);
                assertEquals(asciiColors[i], littleColors[i]);

                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(littleColors[i] >= 0 && littleColors[i] <= 255);
                assertTrue(bigColors[i] >= 0 && bigColors[i] <= 255);
            }

            final var nIndicesInChunk = asciiIndices.length;
            final var nVerticesInChunk = arraySize / 3;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);

            for (var i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(littleIndices[i] < nVerticesInChunk);
                assertTrue(bigIndices[i] < nVerticesInChunk);

                assertEquals(asciiIndices[i], littleIndices[i]);
                assertEquals(littleIndices[i], bigIndices[i]);
            }

            // check bounding box values
            var minX = Float.MAX_VALUE;
            var minY = Float.MAX_VALUE;
            var minZ = Float.MAX_VALUE;
            var maxX = -Float.MAX_VALUE;
            var maxY = -Float.MAX_VALUE;
            var maxZ = -Float.MAX_VALUE;
            float x;
            float y;
            float z;
            for (var i = 0; i < arraySize; i += 3) {
                x = littleVertices[i];
                y = littleVertices[i + 1];
                z = littleVertices[i + 2];
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
            }

            assertEquals(minX, asciiChunk.getMinX(), ERROR);
            assertEquals(asciiChunk.getMinX(), littleChunk.getMinX(), ERROR);
            assertEquals(minX, littleChunk.getMinX(), 0.0);
            assertEquals(littleChunk.getMinX(), bigChunk.getMinX(), 0.0);

            assertEquals(minY, asciiChunk.getMinY(), ERROR);
            assertEquals(asciiChunk.getMinY(), littleChunk.getMinY(), ERROR);
            assertEquals(minY, littleChunk.getMinY(), 0.0);
            assertEquals(littleChunk.getMinY(), bigChunk.getMinY(), 0.0);

            assertEquals(minZ, asciiChunk.getMinZ(), ERROR);
            assertEquals(asciiChunk.getMinZ(), littleChunk.getMinZ(), ERROR);
            assertEquals(minZ, littleChunk.getMinZ(), 0.0);
            assertEquals(littleChunk.getMinZ(), bigChunk.getMinZ(), 0.0);

            assertEquals(maxX, asciiChunk.getMaxX(), ERROR);
            assertEquals(asciiChunk.getMaxX(), littleChunk.getMaxX(), ERROR);
            assertEquals(maxX, littleChunk.getMaxX(), 0.0);
            assertEquals(littleChunk.getMaxX(), bigChunk.getMaxX(), 0.0);

            assertEquals(maxY, asciiChunk.getMaxY(), ERROR);
            assertEquals(asciiChunk.getMaxY(), littleChunk.getMaxY(), ERROR);
            assertEquals(maxY, littleChunk.getMaxY(), 0.0);
            assertEquals(littleChunk.getMaxY(), bigChunk.getMaxY(), 0.0);

            assertEquals(maxZ, asciiChunk.getMaxZ(), ERROR);
            assertEquals(asciiChunk.getMaxZ(), littleChunk.getMaxZ(), ERROR);
            assertEquals(maxZ, littleChunk.getMaxZ(), 0.0);
            assertEquals(littleChunk.getMaxZ(), bigChunk.getMaxZ(), 0.0);

            assertTrue(minX <= maxX);
            assertTrue(minY <= maxY);
            assertTrue(minZ <= maxZ);
            assertTrue(asciiChunk.getMinX() <= asciiChunk.getMaxX());
            assertTrue(asciiChunk.getMinY() <= asciiChunk.getMaxY());
            assertTrue(asciiChunk.getMinZ() <= asciiChunk.getMaxZ());
            assertTrue(littleChunk.getMinX() <= littleChunk.getMaxX());
            assertTrue(littleChunk.getMinY() <= littleChunk.getMaxY());
            assertTrue(littleChunk.getMinZ() <= littleChunk.getMaxZ());
            assertTrue(bigChunk.getMinX() <= bigChunk.getMaxX());
            assertTrue(bigChunk.getMinY() <= bigChunk.getMaxY());
            assertTrue(bigChunk.getMinZ() <= bigChunk.getMaxZ());
        }

        if (asciiIt.hasNext()) {
            fail("Wrong number of chunks in ASCII mode");
        }
        if (littleIt.hasNext()) {
            fail("Wrong number of chunks in Little endian mode");
        }
        if (bigIt.hasNext()) {
            fail("Wrong number of chunks in big endian mode");
        }

        // releases file resources
        asciiLoader.close();
        littleLoader.close();
        bigLoader.close();
    }

    @Test
    void testLoadAndIterateRealFileWithNormals() throws LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var fileAscii = new File("./src/test/java/com/irurueta/geometry/io/booksAscii.ply");
        final var fileBinary = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileBinary.exists());
        assertTrue(fileBinary.isFile());

        final var asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        final var binaryLoader = new LoaderPLY(fileBinary);
        binaryLoader.setListener(this);

        final var asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final var binaryIt = binaryLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // read and compare chunks of data
        while (asciiIt.hasNext() && binaryIt.hasNext()) {
            final var asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var binaryChunk = binaryIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final var asciiColors = asciiChunk.getColorData();
            final var asciiIndices = asciiChunk.getIndicesData();
            final var asciiTextureCoords = asciiChunk.getTextureCoordinatesData();
            final var asciiNormals = asciiChunk.getNormalsData();

            final var binaryVertices = binaryChunk.getVerticesCoordinatesData();
            final var binaryColors = binaryChunk.getColorData();
            final var binaryIndices = binaryChunk.getIndicesData();
            final var binaryTextureCoords = binaryChunk.getTextureCoordinatesData();
            final var binaryNormals = binaryChunk.getNormalsData();

            final var arraySize = asciiVertices.length;
            final var nVerticesInChunk = arraySize / 3;

            assertEquals(asciiVertices.length, binaryVertices.length);
            assertEquals(asciiColors.length, binaryColors.length);
            assertEquals(asciiIndices.length, binaryIndices.length);
            assertNull(asciiTextureCoords);
            assertNull(binaryTextureCoords);
            assertEquals(asciiNormals.length, binaryNormals.length);

            assertEquals(asciiVertices.length, nVerticesInChunk * 3);
            assertEquals(asciiColors.length, nVerticesInChunk * 4);
            assertEquals(asciiNormals.length, nVerticesInChunk * 3);

            assertEquals(binaryVertices.length, nVerticesInChunk * 3);
            assertEquals(binaryColors.length, nVerticesInChunk * 4);
            assertEquals(binaryNormals.length, nVerticesInChunk * 3);

            // compare ascii and binary chunks
            for (var i = 0; i < asciiVertices.length; i++) {
                assertEquals(asciiVertices[i], binaryVertices[i], ERROR);
                assertEquals(asciiNormals[i], binaryNormals[i], ERROR);
            }

            for (var i = 0; i < asciiColors.length; i++) {
                assertEquals(asciiColors[i], binaryColors[i]);
                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(binaryColors[i] >= 0 && binaryColors[i] <= 255);
            }

            final var nIndicesInChunk = asciiIndices.length;
            for (var i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(binaryIndices[i] < nVerticesInChunk);

                assertEquals(asciiIndices[i], binaryIndices[i]);
            }

            // check bounding box values
            var minX = Float.MAX_VALUE;
            var minY = Float.MAX_VALUE;
            var minZ = Float.MAX_VALUE;
            var maxX = -Float.MAX_VALUE;
            var maxY = -Float.MAX_VALUE;
            var maxZ = -Float.MAX_VALUE;
            float x;
            float y;
            float z;
            for (var i = 0; i < arraySize; i += 3) {
                x = binaryVertices[i];
                y = binaryVertices[i + 1];
                z = binaryVertices[i + 2];
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
            }

            assertEquals(minX, asciiChunk.getMinX(), ERROR);
            assertEquals(minX, binaryChunk.getMinX(), 0.0);
            assertEquals(asciiChunk.getMinX(), binaryChunk.getMinX(), ERROR);

            assertEquals(minY, asciiChunk.getMinY(), ERROR);
            assertEquals(minY, binaryChunk.getMinY(), 0.0);
            assertEquals(asciiChunk.getMinY(), binaryChunk.getMinY(), ERROR);

            assertEquals(minZ, asciiChunk.getMinZ(), ERROR);
            assertEquals(minZ, binaryChunk.getMinZ(), 0.0);
            assertEquals(asciiChunk.getMinZ(), binaryChunk.getMinZ(), ERROR);

            assertEquals(maxX, asciiChunk.getMaxX(), ERROR);
            assertEquals(maxX, binaryChunk.getMaxX(), 0.0);
            assertEquals(asciiChunk.getMaxX(), binaryChunk.getMaxX(), ERROR);

            assertEquals(maxY, asciiChunk.getMaxY(), ERROR);
            assertEquals(maxY, binaryChunk.getMaxY(), 0.0);
            assertEquals(asciiChunk.getMaxY(), binaryChunk.getMaxY(), ERROR);

            assertEquals(maxZ, asciiChunk.getMaxZ(), ERROR);
            assertEquals(maxZ,  binaryChunk.getMaxZ(),0.0);
            assertEquals(asciiChunk.getMaxZ(), binaryChunk.getMaxZ(), ERROR);

            assertTrue(minX <= maxX);
            assertTrue(minY <= maxY);
            assertTrue(minZ <= maxZ);
            assertTrue(asciiChunk.getMinX() <= asciiChunk.getMaxX());
            assertTrue(asciiChunk.getMinY() <= asciiChunk.getMaxY());
            assertTrue(asciiChunk.getMinZ() <= asciiChunk.getMaxZ());
            assertTrue(binaryChunk.getMinX() <= binaryChunk.getMaxX());
            assertTrue(binaryChunk.getMinY() <= binaryChunk.getMaxY());
            assertTrue(binaryChunk.getMinZ() <= binaryChunk.getMaxZ());
        }

        if (asciiIt.hasNext()) {
            fail("Wrong number of chunks in ASCII mode");
        }
        if (binaryIt.hasNext()) {
            fail("Wrong number of chunks in Little endian mode");
        }

        // releases file resources
        asciiLoader.close();
        binaryLoader.close();
    }

    @Override
    public void onLoadStart(final Loader loader) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        checkLocked((LoaderPLY) loader);
    }

    @Override
    public void onLoadEnd(final Loader loader) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        checkLocked((LoaderPLY) loader);
    }

    @Override
    public void onLoadProgressChange(final Loader loader, final float progress) {
        if ((progress < 0.0) || (progress > 1.0)) {
            progressValid = false;
        }
        if (progress < previousProgress) {
            progressValid = false;
        }
        previousProgress = progress;

        checkLocked((LoaderPLY) loader);
    }

    private void checkLocked(final LoaderPLY loader) {
        if (!loader.isLocked()) {
            lockedValid = false;
        }

        try {
            loader.setListener(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.load();
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setMaxVerticesInChunk(1);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setAllowDuplicateVerticesInChunk(true);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setMaxStreamPositions(1);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }
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
