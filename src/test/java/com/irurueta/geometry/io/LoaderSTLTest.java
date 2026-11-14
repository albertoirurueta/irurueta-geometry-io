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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LoaderSTLTest implements LoaderListener {

    private static final double ERROR = 1e-4;

    private static final int BINARY_HEADER_SIZE = 80;

    @TempDir
    private Path tempDir;

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

    /**
     * Checks that an ASCII STL file containing a single solid with two facets is loaded into a single chunk.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenAsciiSingleSolid_returnsSingleChunkWithAllVertices() throws Exception {
        final var f = writeAscii("single.stl", "solid my solid\n" + facet(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0)
                + facet(0, 1, 0, 1, 1, 1, 2, 1, 1, 2, 2, 2) + "endsolid my solid\n");

        try (var loader = new LoaderSTL(f)) {
            loader.setListener(this);
            final var it = loader.load();
            assertTrue(loader.isLocked());
            assertEquals("my solid", loader.getSolidName().trim());
            assertEquals(0L, loader.getNumberOfVertices());
            assertTrue(it.hasNext());

            final var chunk = it.next();
            assertFalse(it.hasNext());
            assertThrows(NotAvailableException.class, it::next);

            assertEquals(6, chunk.getVerticesCoordinatesData().length / 3);
            assertEquals(6, chunk.getIndicesData().length);
            assertEquals(18, chunk.getNormalsData().length);
            assertEquals(0.0f, chunk.getMinX());
            assertEquals(2.0f, chunk.getMaxX());
            assertEquals(0.0f, chunk.getMinZ());
            assertEquals(2.0f, chunk.getMaxZ());
            assertEquals(1.0f, chunk.getNormalsData()[2]);
            assertFalse(loader.isLocked());
            assertEquals(1, startCounter);
            assertEquals(1, endCounter);
        }
    }

    /**
     * Checks that a small chunk size splits an ASCII solid into several chunks.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenAsciiAndSmallChunkSize_returnsSeveralChunks() throws Exception {
        final var sb = new StringBuilder("solid s\n");
        for (var i = 0; i < 5; i++) {
            sb.append(facet(0, 0, 1, i, 0, 0, i, 1, 0, i, 0, 1));
        }
        sb.append("endsolid s\n");
        final var f = writeAscii("chunks.stl", sb.toString());

        try (var loader = new LoaderSTL(f, 3)) {
            final var it = loader.load();
            var count = 0;
            var vertices = 0;
            while (it.hasNext()) {
                final var chunk = it.next();
                if (chunk.getVerticesCoordinatesData() != null) {
                    vertices += chunk.getVerticesCoordinatesData().length / 3;
                }
                count++;
            }
            assertTrue(count > 1);
            assertEquals(15, vertices);
        }
    }

    /**
     * Checks that an ASCII solid without facets yields an empty chunk.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenAsciiEmptySolid_returnsEmptyChunk() throws Exception {
        final var f = writeAscii("empty.stl", "solid empty\nendsolid empty\n");

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            final var chunk = it.next();
            assertNull(chunk.getVerticesCoordinatesData());
            assertNull(chunk.getIndicesData());
            assertNull(chunk.getNormalsData());
            assertFalse(it.hasNext());
        }
    }

    /**
     * Checks that after the first solid ends, a second solid in the same file is not loaded.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenAsciiMultipleSolids_loadsOnlyFirstSolid() throws Exception {
        final var f = writeAscii("multi.stl", "solid a\n" + facet(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0)
                + "endsolid a\nsolid b\n" + facet(0, 0, 1, 5, 5, 5, 6, 5, 5, 5, 6, 5) + "endsolid b\n");

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            final var chunk = it.next();
            assertEquals(3, chunk.getVerticesCoordinatesData().length / 3);
            assertFalse(it.hasNext());
        }
    }

    /**
     * Checks that malformed ASCII files raise a loader exception when read.
     *
     * @param content malformed file content.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "solid a\nfoo\n",
            "solid a\nfacet foo 0 0 1\n",
            "solid a\nfacet\n",
            "solid a\nouter foo\n",
            "solid a\nouter\n",
            "solid a\nfacet normal 0 0\n",
            "solid a\nfacet normal 0 0 1\nouter loop\nvertex 0 0 0\nvertex 1 0 0\n",
            "solid a\nfacet normal 0 0 1\nouter loop\n"
    })
    void next_whenAsciiMalformed_throwsLoaderException(final String content) throws Exception {
        final var f = writeAscii("bad.stl", content);

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that malformed numbers in ASCII files do not escape as unchecked exceptions.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenAsciiVertexNotANumber_throwsLoaderException() throws Exception {
        final var f = writeAscii("nan.stl", "solid a\nfacet normal 0 0 1\nouter loop\nvertex a b c\n");

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that a file containing only the "solid" keyword is rejected.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenAsciiOnlyMagic_throwsLoaderException() throws Exception {
        final var f = writeAscii("magic.stl", "solid");

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a file shorter than the "solid" keyword is rejected.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileTooShort_throwsLoaderException() throws Exception {
        final var f = writeAscii("short.stl", "sol");

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a binary STL with several triangles is loaded in one chunk.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenBinary_returnsAllTriangles() throws Exception {
        final var f = writeBinary("bin.stl", 3, 3);

        try (var loader = new LoaderSTL(f)) {
            loader.setListener(this);
            final var it = loader.load();
            assertEquals(9L, loader.getNumberOfVertices());
            assertNull(loader.getSolidName());
            final var chunk = it.next();
            assertFalse(it.hasNext());
            assertEquals(9, chunk.getVerticesCoordinatesData().length / 3);
            assertEquals(9, chunk.getIndicesData().length);
            assertEquals(1.0f, chunk.getNormalsData()[1]);
            assertEquals(0.0f, chunk.getMinX());
            assertEquals(2.0f, chunk.getMaxX());
            assertEquals(1, endCounter);
        }
    }

    /**
     * Checks that a binary STL is split in several chunks when chunk size is small.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenBinaryAndSmallChunkSize_returnsSeveralChunks() throws Exception {
        final var f = writeBinary("binchunks.stl", 7, 7);

        try (var loader = new LoaderSTL(f, 3)) {
            final var it = loader.load();
            var count = 0;
            var vertices = 0;
            while (it.hasNext()) {
                vertices += it.next().getVerticesCoordinatesData().length / 3;
                count++;
            }
            assertEquals(7, count);
            assertEquals(21, vertices);
        }
    }

    /**
     * Checks that a binary STL with zero triangles has no chunks.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void hasNext_whenBinaryWithoutTriangles_returnsFalse() throws Exception {
        final var f = writeBinary("binempty.stl", 0, 0);

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            assertEquals(0L, loader.getNumberOfVertices());
            assertFalse(it.hasNext());
            assertThrows(NotAvailableException.class, it::next);
        }
    }

    /**
     * Checks that a binary STL declaring more triangles than stored fails.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenBinaryTruncatedTriangles_throwsException() throws Exception {
        final var f = writeBinary("bintrunc.stl", 5, 2);

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that a binary STL truncated in the middle of a triangle fails.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenBinaryTruncatedMidTriangle_throwsException() throws Exception {
        final var f = writeBinary("bintrunc2.stl", 2, 2);
        final var bytes = Files.readAllBytes(f.toPath());
        Files.write(f.toPath(), Arrays.copyOf(bytes, bytes.length - 20));

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a binary STL with a huge triangle count and no data fails with an exception.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenBinaryHugeTriangleCount_throwsException() throws Exception {
        final var buffer = ByteBuffer.allocate(BINARY_HEADER_SIZE + 4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(BINARY_HEADER_SIZE);
        buffer.putInt(-1);
        final var f = tempDir.resolve("binhuge.stl").toFile();
        Files.write(f.toPath(), buffer.array());

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that crafted binary STL files with a tiny body are rejected at load time.
     *
     * @param declared number of declared triangles (unsigned 32 bit).
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 0x7FFFFFFFL, 0x80000000L, 0xFFFFFFFEL, 0xFFFFFFFFL})
    void load_whenBinaryDeclaresMoreTrianglesThanFits_throwsLoaderException(final long declared) throws Exception {
        final var buffer = ByteBuffer.allocate(BINARY_HEADER_SIZE + 4 + 10).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(BINARY_HEADER_SIZE);
        buffer.putInt((int) declared);
        final var f = tempDir.resolve("crafted" + declared + ".stl").toFile();
        Files.write(f.toPath(), buffer.array());

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that a binary STL declaring zero triangles with a tiny body loads without triangles.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenBinaryDeclaresZeroTrianglesWithTinyBody_hasNoData() throws Exception {
        final var buffer = ByteBuffer.allocate(BINARY_HEADER_SIZE + 4 + 3).order(ByteOrder.LITTLE_ENDIAN);
        final var f = tempDir.resolve("zero.stl").toFile();
        Files.write(f.toPath(), buffer.array());

        try (var loader = new LoaderSTL(f)) {
            final var it = loader.load();
            assertFalse(it.hasNext());
        }
    }

    /**
     * Checks that binary files shorter than the header plus triangle count are rejected with a checked exception.
     *
     * @param length file length in bytes.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(ints = {81, 82, 83})
    void load_whenBinaryShorterThanHeader_throwsLoaderException(final int length) throws Exception {
        final var bytes = new byte[length];
        final var f = tempDir.resolve("short" + length + ".stl").toFile();
        Files.write(f.toPath(), bytes);

        try (var loader = new LoaderSTL(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Gets a single ASCII facet.
     *
     * @param values 3 normal values followed by 9 vertex coordinates.
     * @return facet text.
     */
    private static String facet(final float... values) {
        final var sb = new StringBuilder("facet normal ").append(values[0]).append(' ').append(values[1])
                .append(' ').append(values[2]).append("\nouter loop\n");
        for (var i = 0; i < 3; i++) {
            sb.append("vertex ").append(values[3 + 3 * i]).append(' ').append(values[4 + 3 * i]).append(' ')
                    .append(values[5 + 3 * i]).append('\n');
        }
        return sb.append("endloop\nendfacet\n").toString();
    }

    /**
     * Writes an ASCII STL file.
     *
     * @param name    file name.
     * @param content file content.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    private File writeAscii(final String name, final String content) throws IOException {
        final var f = tempDir.resolve(name).toFile();
        Files.write(f.toPath(), content.getBytes(StandardCharsets.US_ASCII));
        return f;
    }

    /**
     * Writes a binary STL file.
     *
     * @param name      file name.
     * @param declared  number of triangles declared in the header.
     * @param available number of triangles actually written.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    private File writeBinary(final String name, final int declared, final int available) throws IOException {
        final var buffer = ByteBuffer.allocate(BINARY_HEADER_SIZE + 4 + available * 50)
                .order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(BINARY_HEADER_SIZE);
        buffer.putInt(declared);
        for (var i = 0; i < available; i++) {
            // normal
            buffer.putFloat(0.0f).putFloat(1.0f).putFloat(0.0f);
            // vertices
            buffer.putFloat(i % 3).putFloat(0.0f).putFloat(0.0f);
            buffer.putFloat(0.0f).putFloat(i % 3).putFloat(0.0f);
            buffer.putFloat(0.0f).putFloat(0.0f).putFloat(i % 3);
            buffer.putShort((short) 0);
        }
        final var f = tempDir.resolve(name).toFile();
        Files.write(f.toPath(), buffer.array());
        return f;
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
