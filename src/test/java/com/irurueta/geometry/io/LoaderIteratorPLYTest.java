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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class LoaderIteratorPLYTest implements LoaderListener {

    private static final double ERROR = 1e-4;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @Test
    public void testConstructors() throws LockedException, IOException,
            LoaderException {

        // test empty constructor
        LoaderPLY loader = new LoaderPLY();
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK,
                loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS,
                loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // test constructor with maxVerticesInChunk
        final int maxVerticesInChunk = 21423;
        loader = new LoaderPLY(maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

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
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(0, true);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final long maxStreamPositions = 500;
        loader = new LoaderPLY(maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(0, true, maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        try {
            loader = new LoaderPLY(maxVerticesInChunk, true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // constructor with file
        final File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Force FileNotFoundException
        final File badF = new File("./non-existing");
        assertFalse(badF.exists());

        loader = null;
        try {
            loader = new LoaderPLY(badF);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }
        assertNull(loader);

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk,
                true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

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

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        try {
            loader = new LoaderPLY(f, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // Test constructor with loader listener
        final LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(final Loader loader) {
            }

            @Override
            public void onLoadEnd(final Loader loader) {
            }

            @Override
            public void onLoadProgressChange(final Loader loader, final float progress) {
            }
        };

        loader = new LoaderPLY(listener);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS,
                loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderPLY(listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS,
                loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(listener, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0, true);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        try {
            loader = new LoaderPLY(listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file and listener
        loader = new LoaderPLY(f, listener);
        assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderPLY(f, listener, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, listener, maxVerticesInChunk, true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk, true);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0, true);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_PLY, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("FileNotFoundException not thrown");
        } catch (final FileNotFoundException ignore) {
        }

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0,
                    true, maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        try {
            loader = new LoaderPLY(f, listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
    }

    @Test
    public void testHasSetFileAndIsReady()
            throws LockedException, IOException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        try (final LoaderPLY loader = new LoaderPLY()) {
            assertFalse(loader.hasFile());
            assertFalse(loader.isReady());

            // set file
            loader.setFile(f);
            assertTrue(loader.hasFile());
            assertTrue(loader.isReady());
        }
    }

    @Test
    public void testGetSetFileSizeLimitToKeepInMemory() throws LockedException, IOException {
        try (final LoaderPLY loader = new LoaderPLY()) {
            assertEquals(LoaderPLY.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                    loader.getFileSizeLimitToKeepInMemory());

            // set new value
            final long value = 1000000;
            loader.setFileSizeLimitToKeepInMemory(value);
            // and check correctness
            assertEquals(value, loader.getFileSizeLimitToKeepInMemory());
        }
    }

    @Test
    public void testGetSetListener() throws LockedException, IOException {
        try (final LoaderPLY loader = new LoaderPLY()) {
            assertNull(loader.getListener());

            final LoaderListener listener = new LoaderListener() {
                @Override
                public void onLoadStart(final Loader loader) {
                }

                @Override
                public void onLoadEnd(final Loader loader) {
                }

                @Override
                public void onLoadProgressChange(final Loader loader, float progress) {
                }
            };

            loader.setListener(listener);
            assertEquals(listener, loader.getListener());
        }
    }

    @Test
    public void testGetSetMaxVerticesInChunk() throws LockedException, IOException {
        try (final LoaderPLY loader = new LoaderPLY()) {
            final int maxVerticesInChunk = 521351;

            assertEquals(LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());

            // set new value
            loader.setMaxVerticesInChunk(maxVerticesInChunk);
            // check correctness
            assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());

            // Force IllegalArgumentException
            loader.setMaxVerticesInChunk(0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetSetAllowDuplicateVerticesInChunk() throws LockedException, IOException {
        try (final LoaderPLY loader = new LoaderPLY()) {

            assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                    loader.areDuplicateVerticesInChunkAllowed());

            // set new value
            loader.setAllowDuplicateVerticesInChunk(true);
            // check correctness
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        }
    }

    @Test
    public void testGetSetMaxStreamPositions() throws LockedException, IOException {
        final long maxStreamPositions = 400;
        try (final LoaderPLY loader = new LoaderPLY()) {
            assertEquals(LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());

            // set new value
            loader.setMaxStreamPositions(maxStreamPositions);
            // check correctness
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());

            // Force IllegalArgumentException
            loader.setMaxStreamPositions(0);
            fail("IllegalArgumentException not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testIsValidFile() throws LockedException, IOException {
        // Test for BIG ENDIAN
        File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        try (final LoaderPLY loader = new LoaderPLY(f)) {
            assertTrue(loader.isValidFile());

            // Test for LITTLE ENDIAN
            f = new File("./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
            // check that file exists
            assertTrue(f.exists());
            assertTrue(f.isFile());
        }

        try (final LoaderPLY loader = new LoaderPLY(f)){
            assertTrue(loader.isValidFile());

            // Test for ASCII
            f = new File("./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
            // check that file exists
            assertTrue(f.exists());
            assertTrue(f.isFile());
        }

        try (final LoaderPLY loader = new LoaderPLY(f)) {
            assertTrue(loader.isValidFile());
        }
    }

    @Test
    public void testLoad() throws LockedException, NotReadyException,
            IOException, LoaderException {
        // Test for BIG ENDIAN
        File f = new File("./src/test/java/com/irurueta/geometry/io/randomBig.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        LoaderPLY loader = new LoaderPLY(f);
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
    public void testLoadAndIterate()
            throws IllegalArgumentException, LockedException, NotReadyException,
            IOException, LoaderException, NotAvailableException {
        final int maxNumberOfVerticesInChunk = 9;

        final File fileAscii = new File(
                "./src/test/java/com/irurueta/geometry/io/randomAscii.ply");
        final File fileLittle = new File(
                "./src/test/java/com/irurueta/geometry/io/randomLittle.ply");
        final File fileBig = new File(
                "./src/test/java/com/irurueta/geometry/io/randomBig.ply");

        final LoaderPLY asciiLoader = new LoaderPLY(fileAscii,
                maxNumberOfVerticesInChunk);
        asciiLoader.setListener(this);
        final LoaderPLY littleLoader = new LoaderPLY(fileLittle,
                maxNumberOfVerticesInChunk);
        littleLoader.setListener(this);
        final LoaderPLY bigLoader = new LoaderPLY(fileBig,
                maxNumberOfVerticesInChunk);
        bigLoader.setListener(this);

        final LoaderIterator asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final LoaderIterator littleIt = littleLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final LoaderIterator bigIt = bigLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            final DataChunk asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk littleChunk = littleIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk bigChunk = bigIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final short[] asciiColors = asciiChunk.getColorData();
            final int[] asciiIndices = asciiChunk.getIndicesData();

            final float[] littleVertices = littleChunk.getVerticesCoordinatesData();
            final short[] littleColors = littleChunk.getColorData();
            final int[] littleIndices = littleChunk.getIndicesData();

            final float[] bigVertices = bigChunk.getVerticesCoordinatesData();
            final short[] bigColors = bigChunk.getColorData();
            final int[] bigIndices = bigChunk.getIndicesData();

            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);

            final int nVerticesInChunk = asciiVertices.length / 3;

            if (asciiIt.hasNext()) {
                assertEquals(nVerticesInChunk,
                        (maxNumberOfVerticesInChunk / 3) * 3);
            }

            assertEquals(nVerticesInChunk, littleVertices.length / 3);
            assertEquals(nVerticesInChunk, bigVertices.length / 3);

            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE,
                    minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE,
                    maxZ = -Float.MAX_VALUE;
            float x, y, z;
            for (int i = 0; i < nVerticesInChunk; i++) {
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

            final int nIndicesInChunk = asciiIndices.length;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
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
    public void testLoadAndIterateRealFile() throws LockedException, NotReadyException,
            IOException, LoaderException, NotAvailableException {

        final File fileAscii = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
        final File fileLittle = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
        final File fileBig = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");

        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileLittle.exists());
        assertTrue(fileLittle.isFile());
        assertTrue(fileBig.exists());
        assertTrue(fileBig.isFile());

        final LoaderPLY asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        final LoaderPLY littleLoader = new LoaderPLY(fileLittle);
        littleLoader.setListener(this);
        final LoaderPLY bigLoader = new LoaderPLY(fileBig);
        bigLoader.setListener(this);

        final LoaderIterator asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final LoaderIterator littleIt = littleLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final LoaderIterator bigIt = bigLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // read and compare chunks of data
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            final DataChunk asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk littleChunk = littleIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk bigChunk = bigIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final short[] asciiColors = asciiChunk.getColorData();
            final int[] asciiIndices = asciiChunk.getIndicesData();

            final float[] littleVertices = littleChunk.getVerticesCoordinatesData();
            final short[] littleColors = littleChunk.getColorData();
            final int[] littleIndices = littleChunk.getIndicesData();

            final float[] bigVertices = bigChunk.getVerticesCoordinatesData();
            final short[] bigColors = bigChunk.getColorData();
            final int[] bigIndices = bigChunk.getIndicesData();

            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);

            // compare ascii, little and big endian chunks
            assertEquals(asciiVertices.length, littleVertices.length);
            assertEquals(asciiVertices.length, bigVertices.length);

            final int arraySize = asciiVertices.length;
            for (int i = 0; i < arraySize; i++) {
                assertEquals(littleVertices[i], bigVertices[i], 0.0);
                assertEquals(asciiVertices[i], littleVertices[i], ERROR);

                assertEquals(littleColors[i], bigColors[i]);
                assertEquals(asciiColors[i], littleColors[i]);

                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(littleColors[i] >= 0 && littleColors[i] <= 255);
                assertTrue(bigColors[i] >= 0 && bigColors[i] <= 255);
            }

            final int nIndicesInChunk = asciiIndices.length;
            final int nVerticesInChunk = arraySize / 3;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(littleIndices[i] < nVerticesInChunk);
                assertTrue(bigIndices[i] < nVerticesInChunk);

                assertEquals(asciiIndices[i], littleIndices[i]);
                assertEquals(littleIndices[i], bigIndices[i]);
            }

            // check bounding box values
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;
            float x;
            float y;
            float z;
            for (int i = 0; i < arraySize; i += 3) {
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
    public void testLoadAndIterateRealFileWithNormals() throws LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final File fileAscii =
                new File("./src/test/java/com/irurueta/geometry/io/booksAscii.ply");
        final File fileBinary =
                new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileBinary.exists());
        assertTrue(fileBinary.isFile());

        final LoaderPLY asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        final LoaderPLY binaryLoader = new LoaderPLY(fileBinary);
        binaryLoader.setListener(this);

        final LoaderIterator asciiIt = asciiLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        final LoaderIterator binaryIt = binaryLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // read and compare chunks of data
        while (asciiIt.hasNext() && binaryIt.hasNext()) {
            final DataChunk asciiChunk = asciiIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk binaryChunk = binaryIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            final short[] asciiColors = asciiChunk.getColorData();
            final int[] asciiIndices = asciiChunk.getIndicesData();
            final float[] asciiTextureCoords = asciiChunk.getTextureCoordinatesData();
            final float[] asciiNormals = asciiChunk.getNormalsData();

            final float[] binaryVertices = binaryChunk.getVerticesCoordinatesData();
            final short[] binaryColors = binaryChunk.getColorData();
            final int[] binaryIndices = binaryChunk.getIndicesData();
            final float[] binaryTextureCoords = binaryChunk.getTextureCoordinatesData();
            final float[] binaryNormals = binaryChunk.getNormalsData();

            final int arraySize = asciiVertices.length;
            final int nVerticesInChunk = arraySize / 3;

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
            for (int i = 0; i < asciiVertices.length; i++) {
                assertEquals(asciiVertices[i], binaryVertices[i], ERROR);
                assertEquals(asciiNormals[i], binaryNormals[i], ERROR);
            }

            for (int i = 0; i < asciiColors.length; i++) {
                assertEquals(asciiColors[i], binaryColors[i]);
                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(binaryColors[i] >= 0 && binaryColors[i] <= 255);
            }

            final int nIndicesInChunk = asciiIndices.length;
            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(binaryIndices[i] < nVerticesInChunk);

                assertEquals(asciiIndices[i], binaryIndices[i]);
            }

            // check bounding box values
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;
            float x;
            float y;
            float z;
            for (int i = 0; i < arraySize; i += 3) {
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
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.load();
            lockedValid = false;
        } catch (final LockedException ignore) {
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setMaxVerticesInChunk(1);
            lockedValid = false;
        } catch (final LockedException ignore) {
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setAllowDuplicateVerticesInChunk(true);
            lockedValid = false;
        } catch (final LockedException ignore) {
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.setMaxStreamPositions(1);
            lockedValid = false;
        } catch (final LockedException ignore) {
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
