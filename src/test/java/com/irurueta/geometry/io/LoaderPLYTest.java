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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

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

    /**
     * Checks that generated files with header comments are valid and loadable in every storage mode.
     *
     * @param format storage format name.
     * @param dir    temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(strings = {PLYTestFileBuilder.ASCII, PLYTestFileBuilder.LITTLE, PLYTestFileBuilder.BIG})
    void isValidFile_whenGeneratedFile_returnsTrue(final String format, @TempDir final Path dir) throws Exception {
        final var file = new PLYTestFileBuilder(format).comment("first comment").comment("second").write(dir,
                "valid.ply");

        try (final var loader = new LoaderPLY(file)) {
            assertTrue(loader.isValidFile());
            assertFalse(loader.isLocked());
            assertNotNull(loader.load());
        }
    }

    /**
     * Checks that every invalid header is reported as an invalid file.
     *
     * @param name   test case name.
     * @param header invalid header contents.
     * @param dir    temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidHeaders")
    void isValidFile_whenHeaderIsInvalid_returnsFalse(final String name, final String header,
                                                      @TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "invalid.ply", header);

        try (final var loader = new LoaderPLY(file)) {
            assertFalse(loader.isValidFile());
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that loading a file with invalid header fails.
     *
     * @param name   test case name.
     * @param header invalid header contents.
     * @param dir    temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidHeaders")
    void load_whenHeaderIsInvalid_throwsLoaderException(final String name, final String header,
                                                        @TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "invalid.ply", header);

        try (final var loader = new LoaderPLY(file)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a non numeric number of element instances raises an I/O error.
     *
     * @param dir temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void isValidFile_whenElementCountIsNotANumber_throwsIOException(@TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "count.ply",
                "ply\nformat ascii 1.0\nelement vertex many\nend_header\n");

        try (final var loader = new LoaderPLY(file)) {
            assertThrows(IOException.class, loader::isValidFile);
        }
    }

    /**
     * Checks that a header can have extra blank spaces and that data type aliases are accepted.
     *
     * @param dir temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void isValidFile_whenHeaderHasExtraSpaces_returnsTrue(@TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "spaces.ply",
                "ply\nformat   ascii   1.0\ncomment   hello\nelement  vertex  1\nproperty  float  x\n"
                        + "property list  uchar  int8  l\nend_header\n");

        try (final var loader = new LoaderPLY(file)) {
            assertTrue(loader.isValidFile());
        }
    }

    /**
     * Checks that headers truncated at the end of the stream are rejected with a loader exception.
     *
     * @param header truncated header.
     * @param dir    temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "ply\nformat ascii 1.0\ncomment hello\n",
            "ply\nformat ascii 1.0\nelement",
            "ply\nformat ascii 1.0\nelement vertex 1\nproperty",
            "ply\nformat ascii 1.0\nelement vertex 1\nproperty float x\n",
            "ply\nformat ascii 1.0\n",
            "ply\nformat ascii 1.0\nobj_info x\n"})
    void load_whenHeaderIsTruncated_throwsLoaderException(final String header, @TempDir final Path dir)
            throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "truncated.ply", header);

        try (final var loader = new LoaderPLY(file)) {
            assertFalse(loader.isValidFile());
        }
        try (final var loader = new LoaderPLY(file)) {
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that negative element counts are rejected.
     *
     * @param dir temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenElementCountIsNegative_throwsLoaderException(@TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "negative.ply",
                "ply\nformat ascii 1.0\nelement vertex -5\nproperty float x\nproperty float y\n"
                        + "property float z\nend_header\n");

        try (final var loader = new LoaderPLY(file)) {
            assertFalse(loader.isValidFile());
        }
        try (final var loader = new LoaderPLY(file)) {
            assertThrows(LoaderException.class, () -> consume(loader));
        }
    }

    /**
     * Checks that crafted files only fail with loader exceptions when being loaded.
     *
     * @param name  test case name.
     * @param bytes file content.
     * @param dir   temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("hostileFiles")
    void load_whenFileIsHostile_throwsLoaderException(final String name, final byte[] bytes,
                                                      @TempDir final Path dir) throws Exception {
        final var file = PLYTestFileBuilder.writeBytes(dir, "hostile.ply", bytes);

        try (final var loader = new LoaderPLY(file)) {
            // only checked exceptions are allowed to be thrown (never runtime ones)
            final var ex = assertThrows(Exception.class, () -> consume(loader));
            assertTrue(ex instanceof LoaderException || ex instanceof IOException, ex.toString());
        }
    }

    /**
     * Loads a file and iterates over all its chunks.
     *
     * @param loader loader.
     * @throws Exception if any error occurs.
     */
    private static void consume(final LoaderPLY loader) throws Exception {
        final var iterator = loader.load();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    /**
     * Provides crafted files with hostile counts and list lengths.
     *
     * @return test case names and file contents.
     */
    private static Stream<Arguments> hostileFiles() {
        final var asciiVertex = "ply\nformat ascii 1.0\nelement vertex ";
        final var props = "\nproperty float x\nproperty float y\nproperty float z\n";
        final var face = "element face 1\nproperty list uchar int vertex_indices\nend_header\n";
        final var binLe = "ply\nformat binary_little_endian 1.0\nelement vertex ";

        return Stream.of(
                Arguments.of("ascii huge vertex count",
                        ascii(asciiVertex + "2147483647" + props + face + "0 0 0\n3 0 0 0\n")),
                Arguments.of("ascii huge long vertex count",
                        ascii(asciiVertex + "9223372036854775807" + props + face + "0 0 0\n3 0 0 0\n")),
                Arguments.of("ascii huge face count",
                        ascii(asciiVertex + "3" + props + "element face 2147483647\n"
                                + "property list uchar int vertex_indices\nend_header\n"
                                + "0 0 0\n1 0 0\n0 1 0\n3 0 1 2\n")),
                Arguments.of("binary huge vertex count truncated body",
                        concat(ascii(binLe + "2147483647" + props + face), new byte[12])),
                Arguments.of("binary huge face count truncated body",
                        concat(ascii(binLe + "3" + props + "element face 2147483647\n"
                                + "property list uchar int vertex_indices\nend_header\n"), new byte[36],
                                new byte[]{3, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0})),
                Arguments.of("binary list of 255 entries with truncated body",
                        concat(ascii(binLe + "3" + props + face), new byte[36], new byte[]{(byte) 255, 0, 0, 0, 0})),
                Arguments.of("binary list of 255 entries without body",
                        concat(ascii(binLe + "3" + props + face), new byte[36], new byte[]{(byte) 255})),
                Arguments.of("binary list with negative int length",
                        concat(ascii(binLe + "3" + props
                                + "element face 1\nproperty list int int vertex_indices\nend_header\n"),
                                new byte[36], new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                                        0, 0, 0, 0})),
                Arguments.of("binary list with huge int length",
                        concat(ascii(binLe + "3" + props
                                + "element face 1\nproperty list int int vertex_indices\nend_header\n"),
                                new byte[36], new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, 0x7f,
                                        0, 0, 0, 0})),
                Arguments.of("binary list with huge uint length",
                        concat(ascii(binLe + "3" + props
                                + "element face 1\nproperty list uint int vertex_indices\nend_header\n"),
                                new byte[36], new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                                        0, 0, 0, 0})),
                Arguments.of("ascii list with huge length",
                        ascii(asciiVertex + "3" + props + "element face 1\n"
                                + "property list int int vertex_indices\nend_header\n"
                                + "0 0 0\n1 0 0\n0 1 0\n2147483647 0 1 2\n")),
                Arguments.of("ascii list with negative length",
                        ascii(asciiVertex + "3" + props + "element face 1\n"
                                + "property list int int vertex_indices\nend_header\n"
                                + "0 0 0\n1 0 0\n0 1 0\n-1 0 1 2\n")),
                Arguments.of("ascii list with truncated body",
                        ascii(asciiVertex + "3" + props + "element face 1\n"
                                + "property list uchar int vertex_indices\nend_header\n"
                                + "0 0 0\n1 0 0\n0 1 0\n255 0 1 2\n")),
                Arguments.of("face index out of range",
                        ascii(asciiVertex + "3" + props + face + "0 0 0\n1 0 0\n0 1 0\n3 0 1 99999\n")),
                Arguments.of("face negative index",
                        ascii(asciiVertex + "3" + props + face + "0 0 0\n1 0 0\n0 1 0\n3 0 1 -5\n")));
    }

    private static byte[] ascii(final String text) {
        return text.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    }

    private static byte[] concat(final byte[]... arrays) {
        final var out = new java.io.ByteArrayOutputStream();
        for (final var array : arrays) {
            out.writeBytes(array);
        }
        return out.toByteArray();
    }

    /**
     * Provides headers that are not valid.
     *
     * @return test case names and headers.
     */
    private static Stream<Arguments> invalidHeaders() {
        final var start = "ply\nformat ascii 1.0\n";
        return Stream.of(
                Arguments.of("empty file", ""),
                Arguments.of("wrong magic", "plz\nformat ascii 1.0\nend_header\n"),
                Arguments.of("only magic", "ply\n"),
                Arguments.of("missing format keyword", "ply\nfmt ascii 1.0\nend_header\n"),
                Arguments.of("format without mode", "ply\nformat"),
                Arguments.of("unsupported storage mode", "ply\nformat binary_weird_endian 1.0\nend_header\n"),
                Arguments.of("unsupported version", "ply\nformat ascii 2.0\nend_header\n"),
                Arguments.of("missing version", "ply\nformat ascii"),
                Arguments.of("missing end_header", start + "element vertex 1\nproperty float x\n"),
                Arguments.of("unknown keyword", start + "unknown thing\nend_header\n"),
                Arguments.of("property before element", start + "property float x\nend_header\n"),
                Arguments.of("element without count", start + "element vertex"),
                Arguments.of("unknown scalar type", start + "element vertex 1\nproperty foo x\nend_header\n"),
                Arguments.of("scalar without name", start + "element vertex 1\nproperty float"),
                Arguments.of("list without length type", start + "element face 1\nproperty list"),
                Arguments.of("list with unknown length type",
                        start + "element face 1\nproperty list foo int idx\nend_header\n"),
                Arguments.of("list without value type", start + "element face 1\nproperty list uchar"),
                Arguments.of("list with unknown value type",
                        start + "element face 1\nproperty list uchar foo idx\nend_header\n"),
                Arguments.of("list without name", start + "element face 1\nproperty list uchar int"));
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

    /**
     * Checks that an ASCII file whose body ends before all declared values are
     * read is reported as a corrupted file (LoaderException) and that the
     * loader is unlocked afterwards.
     *
     * @param dir temporary directory.
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenAsciiBodyIsTruncated_throwsLoaderExceptionAndUnlocks(@TempDir final Path dir)
            throws Exception {
        final var file = PLYTestFileBuilder.writeText(dir, "truncatedbody.ply",
                "ply\nformat ascii 1.0\nelement vertex 3\nproperty float x\nproperty float y\n"
                        + "property float z\nelement face 1\nproperty list uchar int vertex_indices\n"
                        + "end_header\n0 0 0\n1 0 0\n0 1 0\n3 0 1 x\n");

        try (final var loader = new LoaderPLY(file)) {
            assertThrows(LoaderException.class, () -> {
                final var it = loader.load();
                while (it.hasNext()) {
                    it.next();
                }
            });
            assertFalse(loader.isLocked());
        }
    }
}
