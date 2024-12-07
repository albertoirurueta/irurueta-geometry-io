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

class LoaderSTLTest implements LoaderListener {

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

        // test constants are equal to LoaderPLY
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK, LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderSTL.MIN_MAX_VERTICES_IN_CHUNK, LoaderPLY.MIN_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderSTL.PROGRESS_DELTA, LoaderPLY.PROGRESS_DELTA, 0.0);

        // test empty constructor
        var loader = new LoaderSTL();
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with maxVerticesInChunk
        final var maxVerticesInChunk = 21423;
        loader = new LoaderSTL(maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderSTL(0));

        // test constructor with file
        final var f = new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderSTL(f);
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());
        // to free file resources
        loader.close();

        // Force IOException
        final var badF = new File("./non-existing");
        assertFalse(badF.exists());

        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderSTL(badF));

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderSTL(f, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderSTL(badF, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderSTL(f, 0));

        // test constructor with loader listener
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

        loader = new LoaderSTL(listener);
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderSTL(listener, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderSTL(listener, 0));

        // test constructor with file and listener
        loader = new LoaderSTL(f, listener);
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderSTL(badF, listener));

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderSTL(f, listener, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_STL, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertNull(loader.getSolidName());
        assertNull(loader.getNumberOfVertices());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderSTL(badF, listener, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderSTL(f, listener, 0));
    }

    @Test
    void testHasSetFileAndIsReady() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        try (final var loader = new LoaderSTL()) {
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
        try (final var loader = new LoaderSTL()) {
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
                public void onLoadProgressChange(final Loader loader, final float progress) {
                    // no action needed
                }
            };

            loader.setListener(listener);
            assertEquals(loader.getListener(), listener);
        }
    }

    @Test
    void testGetSetMaxVerticesInChunk() throws LockedException, IOException {
        try (final var loader = new LoaderSTL()) {
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
    void testIsValidFile() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final var loader = new LoaderSTL(f);
        assertTrue(loader.isValidFile());
        // to free file resources
        loader.close();
    }

    @Test
    void testLoad() throws LockedException, NotReadyException, IOException, LoaderException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final var loader = new LoaderSTL(f);
        assertNotNull(loader.load());

        assertNull(loader.getSolidName());
        assertNotNull(loader.getNumberOfVertices());

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // to free file resources
        loader.close();
    }

    @Test
    void testLoadAndIterate() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 500;
        final var fileStlBin = new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        final var fileStlAscii = new File("./src/test/java/com/irurueta/geometry/io/pitcher-ascii.stl");

        try (final var binLoader = new LoaderSTL(fileStlBin, maxNumberOfVerticesInChunk);
             final var asciiLoader = new LoaderSTL(fileStlAscii, maxNumberOfVerticesInChunk)) {
            binLoader.setListener(this);
            asciiLoader.setListener(this);

            final var binIt = binLoader.load();

            assertNull(binLoader.getSolidName());
            assertNotNull(binLoader.getNumberOfVertices());

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final var asciiIt = asciiLoader.load();

            assertNotNull(asciiLoader.getSolidName());
            assertNotNull(asciiLoader.getNumberOfVertices());
            assertEquals(0L, asciiLoader.getNumberOfVertices().longValue());

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (binIt.hasNext() && asciiIt.hasNext()) {
                final var binChunk = binIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var asciiChunk = asciiIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var binVertices = binChunk.getVerticesCoordinatesData();
                final var binTexture = binChunk.getTextureCoordinatesData();
                final var binColors = binChunk.getColorData();
                final var binNormals = binChunk.getNormalsData();
                final var binIndices = binChunk.getIndicesData();

                final var asciiVertices = asciiChunk.getVerticesCoordinatesData();
                final var asciiTexture = asciiChunk.getTextureCoordinatesData();
                final var asciiColors = asciiChunk.getColorData();
                final var asciiNormals = asciiChunk.getNormalsData();
                final var asciiIndices = asciiChunk.getIndicesData();

                assertEquals(binVertices.length, binNormals.length);
                assertNull(binTexture);
                assertNull(binColors);
                assertEquals(asciiVertices.length, asciiNormals.length);
                assertNull(asciiTexture);
                assertNull(asciiColors);

                final var nVerticesInChunk = binVertices.length / 3;

                assertEquals(nVerticesInChunk, binVertices.length / 3);
                assertEquals(nVerticesInChunk, asciiVertices.length / 3);

                for (var i = 0; i < nVerticesInChunk; i++) {
                    // check x coordinate
                    assertEquals(binVertices[3 * i], asciiVertices[3 * i], ERROR);

                    // check y coordinate
                    assertEquals(binVertices[3 * i + 1], asciiVertices[3 * i + 1], ERROR);

                    // check z coordinate
                    assertEquals(binVertices[3 * i + 2], asciiVertices[3 * i + 2], ERROR);

                    // check x normal
                    assertEquals(binNormals[3 * i], asciiNormals[3 * i], ERROR);

                    // check y normal
                    assertEquals(binNormals[3 * i + 1], asciiNormals[3 * i + 1], ERROR);

                    // check z normal
                    assertEquals(binNormals[3 * i + 2], asciiNormals[3 * i + 2], ERROR);
                }

                final var nIndicesInChunk = binIndices.length;
                assertEquals(nIndicesInChunk, asciiIndices.length);

                for (var i = 0; i < nIndicesInChunk; i++) {
                    assertTrue(binIndices[i] < nVerticesInChunk);
                    assertTrue(asciiIndices[i] < nVerticesInChunk);

                    assertEquals(binIndices[i], asciiIndices[i]);
                }

                // check bounding box values
                assertEquals(binChunk.getMinX(), asciiChunk.getMinX(), ERROR);
                assertEquals(binChunk.getMinY(), asciiChunk.getMinY(), ERROR);
                assertEquals(binChunk.getMinZ(), asciiChunk.getMinZ(), ERROR);
                assertEquals(binChunk.getMaxX(), asciiChunk.getMaxX(), ERROR);
                assertEquals(binChunk.getMaxY(), asciiChunk.getMaxY(), ERROR);
                assertEquals(binChunk.getMaxZ(), asciiChunk.getMaxZ(), ERROR);

                assertTrue(binChunk.getMinX() <= binChunk.getMaxX());
                assertTrue(binChunk.getMinY() <= binChunk.getMaxY());
                assertTrue(binChunk.getMinZ() <= binChunk.getMaxZ());
                assertTrue(asciiChunk.getMinX() <= asciiChunk.getMaxX());
                assertTrue(asciiChunk.getMinY() <= asciiChunk.getMaxY());
                assertTrue(asciiChunk.getMinZ() <= asciiChunk.getMaxZ());
            }

            if (binIt.hasNext()) {
                fail("Wrong number of chunks in STL");
            }
            if (asciiIt.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Override
    public void onLoadStart(final Loader loader) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        checkLocked((LoaderSTL) loader);
    }

    @Override
    public void onLoadEnd(final Loader loader) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        checkLocked((LoaderSTL) loader);
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

        checkLocked((LoaderSTL) loader);
    }

    private void checkLocked(final LoaderSTL loader) {
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
