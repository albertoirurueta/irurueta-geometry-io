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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LoaderOBJTest implements LoaderListenerOBJ {

    private static final double ERROR = 1e-4;

    @TempDir
    private Path tempDir;

    private int counter;

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
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                !LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertEquals(LoaderOBJ.MIN_MAX_VERTICES_IN_CHUNK, LoaderPLY.MIN_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderOBJ.MIN_STREAM_POSITIONS, LoaderPLY.MIN_STREAM_POSITIONS);
        assertEquals(LoaderOBJ.PROGRESS_DELTA, LoaderPLY.PROGRESS_DELTA, 0.0);

        // test empty constructor
        var loader = new LoaderOBJ();
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with maxVerticesInChunk
        final var maxVerticesInChunk = 21423;
        loader = new LoaderOBJ(maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(0));

        // test constructor with maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(0, true));

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final var maxStreamPositions = 500L;
        loader = new LoaderOBJ(maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(maxVerticesInChunk, true, 0));

        // constructor with file
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderOBJ(f);
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        final var badF = new File("./non-existing");
        assertFalse(badF.exists());

        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF));

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderOBJ(f, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(f, 0));

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(f, 0, true));

        // test constructor with file, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        //noinspection resource
        assertThrows(IOException.class,
                () -> new LoaderOBJ(badF, maxVerticesInChunk, true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(f, 0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(f, maxVerticesInChunk, true, 0));

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

        loader = new LoaderOBJ(listener);
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(listener, 0));

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(listener, 0, true));

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(listener, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertFalse(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());

        assertThrows(IOException.class, loader::isValidFile);
        assertThrows(NotReadyException.class, loader::load);

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(listener, 0, true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(listener, maxVerticesInChunk, true, 0));

        // test constructor with file and listener
        loader = new LoaderOBJ(f, listener);
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF, listener));

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK, loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF, listener, maxVerticesInChunk));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(f, listener, 0));

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk, true);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class,
                () -> new LoaderOBJ(badF, listener, maxVerticesInChunk, true));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class,
                () -> new LoaderOBJ(f, listener, 0, true));

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk, true, maxStreamPositions);
        assertEquals(maxVerticesInChunk, loader.getMaxVerticesInChunk());
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(maxStreamPositions, loader.getMaxStreamPositions());
        assertTrue(loader.isReady());
        assertEquals(MeshFormat.MESH_FORMAT_OBJ, loader.getMeshFormat());
        assertFalse(loader.isLocked());
        assertEquals(listener, loader.getListener());
        assertEquals(LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR, loader.isContinueIfTriangulationError());
        assertTrue(loader.getComments().isEmpty());
        assertNull(loader.getMaterials());
        // to free file resources
        loader.close();

        // Force IOException
        //noinspection resource
        assertThrows(IOException.class, () -> new LoaderOBJ(badF, listener, maxVerticesInChunk,
                true, maxStreamPositions));

        // Force IllegalArgumentException
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(f, listener, 0,
                true, maxStreamPositions));
        //noinspection resource
        assertThrows(IllegalArgumentException.class, () -> new LoaderOBJ(f, listener, maxVerticesInChunk,
                true, 0));
    }

    @Test
    void testHasSetFileAndIsReady() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        try (final var loader = new LoaderOBJ()) {
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
        try (final var loader = new LoaderOBJ()) {
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
            assertEquals(listener, loader.getListener());
        }
    }

    @Test
    void testGetSetMaxVerticesInChunk() throws LockedException, IOException {
        try (final var loader = new LoaderOBJ()) {
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
        try (final var loader = new LoaderOBJ()) {

            assertEquals(LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                    loader.areDuplicateVerticesInChunkAllowed());

            // set new value
            loader.setAllowDuplicateVerticesInChunk(true);
            // check correctness
            assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        }
    }

    @Test
    void testGetSetMaxStreamPositions() throws LockedException, IOException {
        final var maxStreamPositions = 400;
        try (final var loader = new LoaderOBJ()) {

            assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS, loader.getMaxStreamPositions());

            // set new value
            loader.setMaxStreamPositions(maxStreamPositions);
            // check correctness
            assertEquals(maxStreamPositions, loader.getMaxStreamPositions());

            // Force IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> loader.setMaxStreamPositions(0));
        }
    }

    @Test
    void testIsSetContinueIfTriangulationError() throws IOException {
        try (final var loader = new LoaderOBJ()) {

            assertTrue(loader.isContinueIfTriangulationError());

            loader.setContinueIfTriangulationError(false);

            assertFalse(loader.isContinueIfTriangulationError());
        }
    }

    @Test
    void testIsValidFile() throws LockedException, IOException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final var loader = new LoaderOBJ(f);
        assertTrue(loader.isValidFile());
        // to free file resources
        loader.close();
    }

    @Test
    void testLoad() throws LockedException, NotReadyException, IOException, LoaderException {
        final var f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final var loader = new LoaderOBJ(f);
        assertNotNull(loader.load());

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
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        final var filePly = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        try (final var plyLoader = new LoaderPLY(filePly, maxNumberOfVerticesInChunk, false);
             //disable vertex duplicates
             final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, false)) {

            objLoader.setListener(this);


            final var plyIt = plyLoader.load();
            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext() && plyIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var plyChunk = plyIt.next();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                final var plyVertices = plyChunk.getVerticesCoordinatesData();
                final var plyTexture = plyChunk.getTextureCoordinatesData();
                final var plyColors = plyChunk.getColorData();
                final var plyNormals = plyChunk.getNormalsData();
                final var plyIndices = plyChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNull(objTexture);
                assertNull(objColors);
                assertEquals(plyVertices.length, plyNormals.length);
                assertNull(plyTexture);
                assertNotNull(plyColors);

                final var nVerticesInChunk = objVertices.length / 3;

                assertEquals(nVerticesInChunk, objVertices.length / 3);
                assertEquals(nVerticesInChunk, plyVertices.length / 3);

                for (var i = 0; i < nVerticesInChunk; i++) {
                    // check x coordinate
                    assertEquals(objVertices[3 * i], plyVertices[3 * i], ERROR);

                    // check y coordinate
                    assertEquals(objVertices[3 * i + 1], plyVertices[3 * i + 1], ERROR);

                    // check z coordinate
                    assertEquals(objVertices[3 * i + 2], plyVertices[3 * i + 2], ERROR);

                    // check x normal
                    assertEquals(objNormals[3 * i], plyNormals[3 * i], ERROR);

                    // check y normal
                    assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1], ERROR);

                    // check z normal
                    assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2], ERROR);
                }

                final var nIndicesInChunk = objIndices.length;
                assertEquals(nIndicesInChunk, plyIndices.length);

                for (var i = 0; i < nIndicesInChunk; i++) {
                    assertTrue(objIndices[i] < nVerticesInChunk);
                    assertTrue(plyIndices[i] < nVerticesInChunk);

                    assertEquals(objIndices[i], plyIndices[i]);
                }

                // check bounding box values
                assertEquals(objChunk.getMinX(), plyChunk.getMinX(), ERROR);
                assertEquals(objChunk.getMinY(), plyChunk.getMinY(), ERROR);
                assertEquals(objChunk.getMinZ(), plyChunk.getMinZ(), ERROR);
                assertEquals(objChunk.getMaxX(), plyChunk.getMaxX(), ERROR);
                assertEquals(objChunk.getMaxY(), plyChunk.getMaxY(), ERROR);
                assertEquals(objChunk.getMaxZ(), plyChunk.getMaxZ(), ERROR);

                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
                assertTrue(plyChunk.getMinX() <= plyChunk.getMaxX());
                assertTrue(plyChunk.getMinY() <= plyChunk.getMaxY());
                assertTrue(plyChunk.getMinZ() <= plyChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
            if (plyIt.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Test
    void testLoadAndIterateMacbook() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 100000;
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook.obj");
        final var filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");

        try (final var plyLoader = new LoaderPLY(filePly, maxNumberOfVerticesInChunk, true); //enable vertex duplicates
             //enable vertex duplicates
             final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, true)) {

            objLoader.setListener(this);

            final var plyIt = plyLoader.load();
            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext() && plyIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var plyChunk = plyIt.next();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                final var plyVertices = plyChunk.getVerticesCoordinatesData();
                final var plyTexture = plyChunk.getTextureCoordinatesData();
                final var plyColors = plyChunk.getColorData();
                final var plyNormals = plyChunk.getNormalsData();
                final var plyIndices = plyChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNull(objTexture);
                assertNull(objColors);
                assertEquals(plyVertices.length, plyNormals.length);
                assertNull(plyTexture);
                assertNotNull(plyColors);

                final var nVerticesInChunk = objVertices.length / 3;

                assertEquals(nVerticesInChunk, objVertices.length / 3);
                assertEquals(nVerticesInChunk, plyVertices.length / 3);

                for (var i = 0; i < nVerticesInChunk; i++) {
                    // check x coordinate
                    assertEquals(objVertices[3 * i], plyVertices[3 * i], ERROR);

                    // check y coordinate
                    assertEquals(objVertices[3 * i + 1], plyVertices[3 * i + 1], ERROR);

                    // check z coordinate
                    assertEquals(objVertices[3 * i + 2], plyVertices[3 * i + 2], ERROR);
                }

                final var nIndicesInChunk = objIndices.length;
                assertEquals(nIndicesInChunk, plyIndices.length);

                for (var i = 0; i < nIndicesInChunk; i++) {
                    assertTrue(objIndices[i] < nVerticesInChunk);
                    assertTrue(plyIndices[i] < nVerticesInChunk);

                    assertEquals(objIndices[i], plyIndices[i]);
                }

                // check bounding box values
                assertEquals(objChunk.getMinX(), plyChunk.getMinX(), ERROR);
                assertEquals(objChunk.getMinY(), plyChunk.getMinY(), ERROR);
                assertEquals(objChunk.getMinZ(), plyChunk.getMinZ(), ERROR);
                assertEquals(objChunk.getMaxX(), plyChunk.getMaxX(), ERROR);
                assertEquals(objChunk.getMaxY(), plyChunk.getMaxY(), ERROR);
                assertEquals(objChunk.getMaxZ(), plyChunk.getMaxZ(), ERROR);

                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
                assertTrue(plyChunk.getMinX() <= plyChunk.getMaxX());
                assertTrue(plyChunk.getMinY() <= plyChunk.getMaxY());
                assertTrue(plyChunk.getMinZ() <= plyChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
            if (plyIt.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Test
    void testLoadAndIterateMacbook2() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 100000;
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook2.obj");
        final var filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");

        try (final var plyLoader = new LoaderPLY(filePly, maxNumberOfVerticesInChunk, false);
             //disable vertex duplicates
             final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, false)) {

            objLoader.setListener(this);

            final var plyIt = plyLoader.load();
            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext() && plyIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var plyChunk = plyIt.next();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                final var plyVertices = plyChunk.getVerticesCoordinatesData();
                final var plyTexture = plyChunk.getTextureCoordinatesData();
                final var plyColors = plyChunk.getColorData();
                final var plyNormals = plyChunk.getNormalsData();
                final var plyIndices = plyChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNull(objTexture);
                assertNull(objColors);
                assertEquals(plyVertices.length, plyNormals.length);
                assertNull(plyTexture);
                assertNotNull(plyColors);

                final var nVerticesInChunk = objVertices.length / 3;

                assertEquals(nVerticesInChunk, objVertices.length / 3);
                assertEquals(nVerticesInChunk, plyVertices.length / 3);

                for (var i = 0; i < nVerticesInChunk; i++) {
                    // check x coordinate
                    assertEquals(objVertices[3 * i], plyVertices[3 * i], ERROR);

                    // check y coordinate
                    assertEquals(objVertices[3 * i + 1], plyVertices[3 * i + 1], ERROR);

                    // check z coordinate
                    assertEquals(objVertices[3 * i + 2], plyVertices[3 * i + 2], ERROR);

                    // check x normal
                    assertEquals(objNormals[3 * i], plyNormals[3 * i], ERROR);

                    // check y normal
                    assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1], ERROR);

                    // check z normal
                    assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2], ERROR);
                }

                final var nIndicesInChunk = objIndices.length;
                assertEquals(nIndicesInChunk, plyIndices.length);

                for (var i = 0; i < nIndicesInChunk; i++) {
                    assertTrue(objIndices[i] < nVerticesInChunk);
                    assertTrue(plyIndices[i] < nVerticesInChunk);

                    assertEquals(objIndices[i], plyIndices[i]);
                }

                // check bounding box values
                assertEquals(objChunk.getMinX(), plyChunk.getMinX(), ERROR);
                assertEquals(objChunk.getMinY(), plyChunk.getMinY(), ERROR);
                assertEquals(objChunk.getMinZ(), plyChunk.getMinZ(), ERROR);
                assertEquals(objChunk.getMaxX(), plyChunk.getMaxX(), ERROR);
                assertEquals(objChunk.getMaxY(), plyChunk.getMaxY(), ERROR);
                assertEquals(objChunk.getMaxZ(), plyChunk.getMaxZ(), ERROR);

                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
                assertTrue(plyChunk.getMinX() <= plyChunk.getMaxX());
                assertTrue(plyChunk.getMinY() <= plyChunk.getMaxY());
                assertTrue(plyChunk.getMinZ() <= plyChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
            if (plyIt.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Test
    void testLoadAndIteratePitcher() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 100000;
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/pitcher.obj");
        final var filePly = new File("./src/test/java/com/irurueta/geometry/io/pitcher.ply");

        try (final var plyLoader = new LoaderPLY(filePly, maxNumberOfVerticesInChunk, true);
             //enable vertex duplicates
             final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, true)) {

            objLoader.setListener(this);

            final var plyIt = plyLoader.load();
            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext() && plyIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var plyChunk = plyIt.next();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                final var plyVertices = plyChunk.getVerticesCoordinatesData();
                final var plyTexture = plyChunk.getTextureCoordinatesData();
                final var plyColors = plyChunk.getColorData();
                final var plyNormals = plyChunk.getNormalsData();
                final var plyIndices = plyChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNotNull(objTexture);
                assertNull(objColors);
                assertEquals(plyVertices.length, plyNormals.length);
                assertNull(plyTexture);
                assertNotNull(plyColors);

                final var nVerticesInChunk = objVertices.length / 3;

                assertEquals(nVerticesInChunk, objVertices.length / 3);
                assertEquals(nVerticesInChunk, plyVertices.length / 3);

                for (var i = 0; i < nVerticesInChunk; i++) {
                    // check x coordinate
                    assertEquals(objVertices[3 * i], plyVertices[3 * i], ERROR);

                    // check y coordinate
                    assertEquals(objVertices[3 * i + 1], plyVertices[3 * i + 1], ERROR);

                    // check z coordinate
                    assertEquals(objVertices[3 * i + 2], plyVertices[3 * i + 2], ERROR);
                }

                final var nIndicesInChunk = objIndices.length;
                assertEquals(nIndicesInChunk, plyIndices.length);

                for (var i = 0; i < nIndicesInChunk; i++) {
                    assertTrue(objIndices[i] < nVerticesInChunk);
                    assertTrue(plyIndices[i] < nVerticesInChunk);

                    assertEquals(objIndices[i], plyIndices[i]);
                }

                // check bounding box values
                assertEquals(objChunk.getMinX(), plyChunk.getMinX(), ERROR);
                assertEquals(objChunk.getMinY(), plyChunk.getMinY(), ERROR);
                assertEquals(objChunk.getMinZ(), plyChunk.getMinZ(), ERROR);
                assertEquals(objChunk.getMaxX(), plyChunk.getMaxX(), ERROR);
                assertEquals(objChunk.getMaxY(), plyChunk.getMaxY(), ERROR);
                assertEquals(objChunk.getMaxZ(), plyChunk.getMaxZ(), ERROR);

                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
                assertTrue(plyChunk.getMinX() <= plyChunk.getMaxX());
                assertTrue(plyChunk.getMinY() <= plyChunk.getMaxY());
                assertTrue(plyChunk.getMinZ() <= plyChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
            if (plyIt.hasNext()) {
                fail("Wrong number of chunks in PLY");
            }
        }
    }

    @Test
    void testLoadAndIterateCamera() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 100000;
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/camera.obj");
        //enable vertex duplicates
        try (final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, true)) {
            objLoader.setListener(this);

            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNotNull(objTexture);
                assertNull(objColors);

                final var nVerticesInChunk = objVertices.length / 3;

                for (final var objIndex : objIndices) {
                    assertTrue(objIndex < nVerticesInChunk);
                }

                // check bounding box values
                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
        }
    }

    @Test
    void testLoadAndIteratePotro() throws IllegalArgumentException, LockedException, NotReadyException, IOException,
            LoaderException, NotAvailableException {

        final var maxNumberOfVerticesInChunk = 100000;
        final var fileObj = new File("./src/test/java/com/irurueta/geometry/io/potro.obj");
        //enable vertex duplicates
        try (final var objLoader = new LoaderOBJ(fileObj, maxNumberOfVerticesInChunk, true)) {
            objLoader.setListener(this);

            final var objIt = objLoader.load();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            // check correctness of chunks
            while (objIt.hasNext()) {
                final var objChunk = objIt.next();

                assertTrue(isEndValid());
                assertTrue(isLockedValid());
                assertTrue(isProgressValid());
                assertTrue(isStartValid());
                resetListener();

                final var objVertices = objChunk.getVerticesCoordinatesData();
                final var objTexture = objChunk.getTextureCoordinatesData();
                final var objColors = objChunk.getColorData();
                final var objNormals = objChunk.getNormalsData();
                final var objIndices = objChunk.getIndicesData();

                assertEquals(objVertices.length, objNormals.length);
                assertNotNull(objTexture);
                assertNull(objColors);

                final var nVerticesInChunk = objVertices.length / 3;

                for (final var objIndex : objIndices) {
                    assertTrue(objIndex < nVerticesInChunk);
                }

                // check bounding box values
                assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
                assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
                assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());
            }

            if (objIt.hasNext()) {
                fail("Wrong number of chunks in OBJ");
            }
        }
    }

    /**
     * Checks vertex only face indices (form "v").
     */
    @Test
    void next_whenVertexOnlyIndices_returnsVerticesAndIndicesOnly() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3");

        final var chunks = loadChunks(file, 1000, true);

        assertEquals(1, chunks.size());
        final var chunk = chunks.get(0);
        assertArrayEquals(new float[]{0, 0, 0, 1, 0, 0, 0, 1, 0}, chunk.getVerticesCoordinatesData(), 0.0f);
        assertArrayEquals(new int[]{0, 1, 2}, chunk.getIndicesData());
        assertNull(chunk.getTextureCoordinatesData());
        assertNull(chunk.getNormalsData());
        assertNull(chunk.getMaterial());
        assertEquals(1.0f, chunk.getMaxX(), 0.0f);
        assertEquals(1.0f, chunk.getMaxY(), 0.0f);
        assertEquals(0.0f, chunk.getMinZ(), 0.0f);
    }

    /**
     * Checks vertex and texture face indices (form "v/vt").
     */
    @Test
    void next_whenVertexTextureIndices_returnsTextureCoordinates() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0.25 0.5", "vt 0.75 1.0", "vt 0.5 0.5",
                "f 1/1 2/2 3/3");

        final var chunk = loadChunks(file, 1000, true).get(0);

        final var texture = chunk.getTextureCoordinatesData();
        assertNotNull(texture);
        assertEquals(0.25f, texture[0], 0.0f);
        assertEquals(0.5f, texture[1], 0.0f);
        assertEquals(0.75f, texture[2], 0.0f);
        assertEquals(1.0f, texture[3], 0.0f);
        assertNull(chunk.getNormalsData());
        assertArrayEquals(new int[]{0, 1, 2}, chunk.getIndicesData());
    }

    /**
     * Checks vertex and normal face indices (form "v//vn").
     */
    @Test
    void next_whenVertexNormalIndices_returnsNormals() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 0 0 1", "vn 0 1 0", "vn 1 0 0",
                "f 1//1 2//2 3//3");

        final var chunk = loadChunks(file, 1000, true).get(0);

        final var normals = chunk.getNormalsData();
        assertNotNull(normals);
        assertArrayEquals(new float[]{0, 0, 1, 0, 1, 0, 1, 0, 0}, normals, 0.0f);
        assertNull(chunk.getTextureCoordinatesData());
    }

    /**
     * Checks full face indices (form "v/vt/vn") including homogeneous vertex and texture coordinates.
     */
    @Test
    void next_whenVertexTextureNormalIndices_returnsAllData() throws Exception {
        final var file = writeObj("v 0 0 0 1", "v 2 0 0 2", "v 0 1 0", "vt 0.1 0.2 1.0", "vt 0.3 0.4 0.0",
                "vt 0.5 0.6", "vn 0 0 1", "vn 0 0 1", "vn 0 0 1", "f 1/1/1 2/2/2 3/3/3");

        final var chunk = loadChunks(file, 1000, true).get(0);

        final var vertices = chunk.getVerticesCoordinatesData();
        assertEquals(1.0f, vertices[3], 0.0f);
        assertNotNull(chunk.getNormalsData());
        final var texture = chunk.getTextureCoordinatesData();
        assertEquals(0.1f, texture[0], 1e-6f);
        // w = 0 falls back to raw u, v
        assertEquals(0.3f, texture[2], 1e-6f);
        assertEquals(0.5f, texture[4], 1e-6f);
    }

    /**
     * Checks that quads and pentagons are triangulated.
     */
    @Test
    void next_whenPolygonFaces_triangulatesThem() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 1 1 0", "v 0 1 0", "v -1 0.5 0", "f 1 2 3 4",
                "f 1 2 3 4 5");

        final var chunk = loadChunks(file, 1000, true).get(0);

        // 2 triangles for quad plus 3 triangles for pentagon
        assertEquals(15, chunk.getIndicesData().length);
    }

    /**
     * Checks that a quad with texture and normals indices is triangulated.
     */
    @Test
    void next_whenQuadWithTextureAndNormals_triangulatesPreservingIndices() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 1 1 0", "v 0 1 0", "vt 0 0", "vt 1 0", "vt 1 1", "vt 0 1",
                "vn 0 0 1", "f 1/1/1 2/2/1 3/3/1 4/4/1");

        final var chunk = loadChunks(file, 1000, false).get(0);

        assertEquals(6, chunk.getIndicesData().length);
        assertNotNull(chunk.getTextureCoordinatesData());
        assertNotNull(chunk.getNormalsData());
        assertNotNull(chunk.getVerticesCoordinatesData());
    }

    /**
     * Checks that degenerate polygons are skipped when continuing on triangulation errors.
     */
    @Test
    void next_whenDegeneratePolygonAndContinue_skipsPolygon() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 2 0 0", "v 3 0 0", "v 0 1 0", "f 1 2 3 4", "f 1 2 5");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertTrue(loader.isContinueIfTriangulationError());
            final var iterator = loader.load();
            final var chunk = iterator.next();

            assertNotNull(chunk.getIndicesData());
            assertFalse(iterator.hasNext());
        }
    }

    /**
     * Checks that degenerate polygons fail when not continuing on triangulation errors.
     */
    @Test
    void next_whenDegeneratePolygonAndNotContinue_throwsLoaderException() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 2 0 0", "v 3 0 0", "f 1 2 3 4");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            loader.setContinueIfTriangulationError(false);
            final var iterator = loader.load();

            assertThrows(LoaderException.class, iterator::next);
        }
    }

    /**
     * Checks that comments are collected and groups, objects, smoothing and blank lines are ignored.
     */
    @Test
    void load_whenCommentsGroupsAndBlankLines_collectsCommentsAndIgnoresRest() throws Exception {
        final var file = writeObj("# first comment", "", "o object", "g group", "s off", "   ", "v 0 0 0",
                "v 1 0 0", "v 0 1 0", "# second comment", "unknown line", "f 1 2 3", "");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();
            final var chunk = iterator.next();

            assertEquals(List.of("first comment", "second comment"), loader.getComments());
            assertEquals(3, chunk.getIndicesData().length);
            assertTrue(loader.isReady());
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that duplicate vertices are reused in a chunk and that index arrays grow when needed.
     */
    @Test
    void next_whenDuplicatesNotAllowedAndSharedVertices_reusesVertices() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 1 1 0", "v 0 1 0", "v 2 1 0", "f 1 2 3", "f 1 3 4",
                "f 2 5 3");

        final var chunks = loadChunks(file, 7, false);

        assertEquals(1, chunks.size());
        final var chunk = chunks.get(0);
        assertEquals(9, chunk.getIndicesData().length);
        assertEquals(15, chunk.getVerticesCoordinatesData().length);
    }

    /**
     * Checks that data is split in several chunks when maximum vertices in chunk is exceeded.
     */
    @Test
    void next_whenMaxVerticesExceeded_splitsInChunks() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "v 1 1 0", "f 1 2 3", "f 2 4 3", "f 1 2 4");

        final var chunks = loadChunks(file, 3, true);

        assertEquals(3, chunks.size());
        for (final var chunk : chunks) {
            assertEquals(3, chunk.getIndicesData().length);
        }
    }

    /**
     * Checks that a small stream position cache still allows resolving all vertices, textures and normals.
     */
    @Test
    void next_whenSmallStreamPositionCache_stillLoadsAllData() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "v 1 1 0", "vt 0 0", "vt 1 0", "vt 0 1", "vt 1 1",
                "vn 0 0 1", "vn 0 1 0", "vn 1 0 0", "vn 1 1 1", "f 4/4/4 3/3/3 2/2/2", "f 1/1/1 2/2/2 3/3/3",
                "f 2/2/2 4/4/4 3/3/3", "f 1/1/1 4/4/4 2/2/2");

        try (final var loader = new LoaderOBJ(file, 1000, true, 1)) {
            final var iterator = loader.load();
            final var chunk = iterator.next();

            assertEquals(12, chunk.getIndicesData().length);
            assertEquals(36, chunk.getVerticesCoordinatesData().length);
            assertEquals(1, loader.getMaxStreamPositions());
        }
    }

    /**
     * Checks materials loaded from an MTL library referenced with an absolute path and chunks split on material
     * changes.
     */
    @Test
    void next_whenMaterialLibrary_splitsChunksByMaterial() throws Exception {
        final var mtl = tempDir.resolve("lib.mtl");
        Files.writeString(mtl, "newmtl red\nKd 1 0 0\nnewmtl blue\nKd 0 0 1\n");
        final var file = writeObj("mtllib " + mtl.toAbsolutePath(), "v 0 0 0", "v 1 0 0", "v 0 1 0", "usemtl red",
                "f 1 2 3", "usemtl blue", "f 1 3 2", "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();
            final var first = iterator.next();
            final var second = iterator.next();

            assertFalse(iterator.hasNext());
            assertEquals(2, loader.getMaterials().size());
            assertEquals(255, first.getMaterial().getDiffuseRedColor());
            assertEquals(255, second.getMaterial().getDiffuseBlueColor());
            assertEquals(3, first.getIndicesData().length);
            assertEquals(6, second.getIndicesData().length);
        }
    }

    /**
     * Checks that usemtl without a library yields chunks without material.
     */
    @Test
    void next_whenUsemtlWithoutLibrary_returnsChunkWithoutMaterial() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "usemtl ghost", "f 1 2 3");

        final var chunk = loadChunks(file, 1000, true).get(0);

        assertNull(chunk.getMaterial());
    }

    /**
     * Checks that materials requested through the listener are loaded.
     */
    @Test
    void load_whenListenerProvidesMaterialLoader_loadsMaterials() throws Exception {
        final var file = writeObj("mtllib potro.mtl", "v 0 0 0", "v 1 0 0", "v 0 1 0", "usemtl blinn1SG1", "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, this, 1000)) {
            final var iterator = loader.load();
            final var chunk = iterator.next();

            assertNotNull(chunk.getMaterial());
            assertFalse(loader.getMaterials().isEmpty());
            assertTrue(isStartValid());
            assertTrue(isEndValid());
            assertTrue(isProgressValid());
            assertTrue(isLockedValid());
            resetListener();
        }
    }

    /**
     * Checks that a listener that cannot provide a material loader is tolerated.
     */
    @Test
    void load_whenListenerReturnsNullMaterialLoader_loadsWithoutMaterials() throws Exception {
        final var file = writeObj("mtllib pitcher.mtl", "v 0 0 0", "v 1 0 0", "v 0 1 0", "usemtl any", "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, this, 1000)) {
            final var chunk = loader.load().next();

            assertNull(chunk.getMaterial());
            resetListener();
        }
    }

    /**
     * Checks that a missing material library raises an IOException when loading.
     */
    @Test
    void load_whenMaterialLibraryMissing_throwsIOException() throws Exception {
        final var missing = new File(tempDir.toFile(), "missing.mtl");
        final var file = writeObj("mtllib " + missing.getAbsolutePath(), "v 0 0 0", "v 1 0 0", "v 0 1 0",
                "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertThrows(IOException.class, loader::load);
        }
    }

    /**
     * Checks that a corrupt material library raises a LoaderException when loading.
     */
    @Test
    void load_whenMaterialLibraryCorrupt_throwsLoaderException() throws Exception {
        final var mtl = tempDir.resolve("bad.mtl");
        Files.writeString(mtl, "newmtl bad\nKa 1 2\n");
        final var file = writeObj("mtllib " + mtl.toAbsolutePath(), "v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a file without faces cannot be loaded.
     */
    @Test
    void load_whenNoFaces_throwsLoaderException() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that an empty file cannot be loaded.
     */
    @Test
    void load_whenEmptyFile_throwsLoaderException() throws Exception {
        final var file = writeObj();

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that faces with fewer than three vertices fail.
     */
    @Test
    void next_whenFaceHasTwoVertices_throwsLoaderException() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();

            assertThrows(LoaderException.class, iterator::next);
        }
    }

    /**
     * Checks that malformed lines referenced by faces fail with a LoaderException.
     */
    @ParameterizedTest
    @MethodSource("malformedObjs")
    void next_whenMalformedContent_throwsLoaderException(final String[] lines) throws Exception {
        final var file = writeObj(lines);

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();

            assertThrows(LoaderException.class, iterator::next);
        }
    }

    /**
     * Checks that out of range and negative indices are rejected.
     */
    @ParameterizedTest
    @MethodSource("invalidIndexObjs")
    void next_whenInvalidIndices_throwsLoaderException(final String[] lines) throws Exception {
        final var file = writeObj(lines);

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();

            assertThrows(LoaderException.class, iterator::next);
        }
    }

    /**
     * Checks that requesting a chunk when iteration finished raises NotAvailableException and that the loader can
     * be reused afterwards.
     */
    @Test
    void next_whenIteratorFinished_throwsNotAvailableAndAllowsReload() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            final var iterator = loader.load();
            assertTrue(iterator.hasNext());
            iterator.next();

            assertFalse(iterator.hasNext());
            assertThrows(NotAvailableException.class, iterator::next);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that loading while locked or without a file fails and file validity.
     */
    @Test
    void load_whenNotReadyOrLocked_throwsExpectedExceptions() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3");

        try (final var empty = new LoaderOBJ()) {
            assertThrows(NotReadyException.class, empty::load);
            assertThrows(IOException.class, empty::isValidFile);
        }

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertTrue(loader.isValidFile());
            loader.load();
            assertTrue(loader.isLocked());
            assertThrows(LockedException.class, loader::load);
            assertThrows(LockedException.class, loader::isValidFile);
        }
    }

    /**
     * Checks that hostile face indices are rejected with a LoaderException, also when the first line of the file
     * is a vertex.
     *
     * @param lines OBJ contents.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @MethodSource("hostileIndexObjs")
    void next_whenIndicesAreHostile_throwsLoaderException(final String[] lines) throws Exception {
        final var file = writeObj(lines);

        try (final var loader = new LoaderOBJ(file, 1000)) {
            assertThrows(LoaderException.class, () -> consume(loader));
        }
    }

    /**
     * Checks that binary garbage only produces checked exceptions.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileIsBinaryGarbage_onlyCheckedExceptionsAreThrown() throws Exception {
        final var random = new java.util.Random(1234L);
        for (var i = 0; i < 20; i++) {
            final var bytes = new byte[1024 + i * 100];
            random.nextBytes(bytes);
            final var file = tempDir.resolve("garbage" + i + ".obj");
            Files.write(file, bytes);

            try (final var loader = new LoaderOBJ(file.toFile(), 1000)) {
                try {
                    consume(loader);
                } catch (final LoaderException | IOException ignore) {
                    // expected
                }
            }
        }
    }

    /**
     * Checks that garbage resembling OBJ syntax only produces checked exceptions.
     *
     * @param lines OBJ contents.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @MethodSource("garbageObjs")
    void load_whenFileIsMalformedGarbage_onlyCheckedExceptionsAreThrown(final String[] lines) throws Exception {
        final var file = writeObj(lines);

        try (final var loader = new LoaderOBJ(file, 1000)) {
            try {
                consume(loader);
            } catch (final LoaderException | IOException ignore) {
                // expected
            }
        }
    }

    /**
     * Checks that very long lines (a few MB) are handled. Lines are read completely into memory, so memory usage is
     * proportional to the longest line (documented limit).
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileHasVeryLongLines_isHandled() throws Exception {
        final var longComment = "# " + "a".repeat(4 * 1024 * 1024);
        final var file = writeObj(longComment, "v 0 0 0", "v 1 0 0", "v 0 1 0", longComment, "f 1 2 3");

        try (final var loader = new LoaderOBJ(file, 1000)) {
            consume(loader);
        }

        // a very long vertex line made of numbers is rejected or accepted, but never fails unexpectedly
        final var longVertex = "v 0 0 0 " + "1 ".repeat(1024 * 1024);
        final var file2 = writeObj("v 0 0 0", "v 1 0 0", longVertex, "f 1 2 3");
        try (final var loader = new LoaderOBJ(file2, 1000)) {
            try {
                consume(loader);
            } catch (final LoaderException | IOException ignore) {
                // allowed
            }
        }

        // a very long face line
        final var longFace = "f 1 2 3" + " 1".repeat(1024 * 1024);
        final var file3 = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", longFace);
        try (final var loader = new LoaderOBJ(file3, 1000)) {
            try {
                consume(loader);
            } catch (final LoaderException | IOException ignore) {
                // allowed
            }
        }
    }

    /**
     * Checks that faces that can never fit in a chunk are rejected instead of producing empty chunks forever.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenFaceNeverFitsInChunk_throwsLoaderException() throws Exception {
        final var file = writeObj("v 0 0 0", "v 1 0 0", "v 1 1 0", "v 0 1 0", "f 1 2 3 4");
        try (final var loader = new LoaderOBJ(file, 3)) {
            assertThrows(LoaderException.class, () -> consume(loader));
        }

        final var file2 = writeObj("v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3");
        try (final var loader = new LoaderOBJ(file2, 2)) {
            assertThrows(LoaderException.class, () -> consume(loader));
        }
    }

    /**
     * Loads a file and iterates over all its chunks.
     *
     * @param loader loader.
     * @throws Exception if any error occurs.
     */
    private static void consume(final LoaderOBJ loader) throws Exception {
        final var iterator = loader.load();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    /**
     * Provides OBJ contents whose face indices are hostile.
     *
     * @return stream of OBJ lines.
     */
    private static Stream<Arguments> hostileIndexObjs() {
        return Stream.of(
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f -3 -2 -1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 0 1 2"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 2147483647"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 -2147483648"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 99999999999"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3.5"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0 0", "f 1/1 2/1 3/-1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0 0", "f 1/1 2/1 3/0"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0 0",
                        "f 1/1 2/1 3/2147483647"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 0 0 1",
                        "f 1//1 2//1 3//-1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 0 0 1",
                        "f 1//1 2//1 3//99999999999"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1/2/3/4 2 3"}));
    }

    /**
     * Provides garbage OBJ contents.
     *
     * @return stream of OBJ lines.
     */
    private static Stream<Arguments> garbageObjs() {
        return Stream.of(
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3/"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 //"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 /"}),
                Arguments.of((Object) new String[]{"f", "f 1", "f 1 2", "v", "vt", "vn"}),
                Arguments.of((Object) new String[]{"v", "v", "v", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 1 2 3", "v 1 2 3", "v 1 2 3", "f   1   2   3"}),
                Arguments.of((Object) new String[]{"v 1 2 3", "v 1 2 3", "v 1 2 3", "f 1\t2\t3"}),
                Arguments.of((Object) new String[]{"v NaN NaN NaN", "v Infinity 0 0", "v 0 -Infinity 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 1e999 1e999 1e999", "v 0 0 0", "v 0 0 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 1 2 3 0", "v 1 2 3 0", "v 1 2 3 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 1 2 3 0", "v 1 2 3 1", "v 1 2 3 0", "f 1 2 3 1 2 3"}),
                Arguments.of((Object) new String[]{"usemtl", "mtllib", "g", "o", "s", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"mtllib", "v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"mtllib ../../../../etc/passwd", "v 0 0 0", "v 1 0 0",
                        "v 0 1 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"\u0000\u0000\u0000", "\uFFFF\uFFFF", "f \u0000 \u0000 \u0000"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 1 1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 0 0 0", "v 0 0 0", "v 0 0 0", "f 1 2 3 4"}));
    }

    /**
     * Provides OBJ contents with malformed vertex, texture, normal or face lines.
     *
     * @return stream of OBJ lines.
     */
    private static Stream<Arguments> malformedObjs() {
        return Stream.of(
                Arguments.of((Object) new String[]{"v 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 0 0 x", "v 1 0 0", "v 0 1 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 0 0 0 x", "v 1 0 0", "v 0 1 0", "f 1 2 3"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 x"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 1", "f 1/1 2/1 3/1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 1 x", "f 1/1 2/1 3/1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 1 2 x", "f 1/1 2/1 3/1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 1 2", "f 1//1 2//1 3//1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 1 2 x", "f 1//1 2//1 3//1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1/1 2/1 3/1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1//1 2//1 3//1"}),
                Arguments.of((Object) new String[]{"v 0 0 0", "v 1 0 0", "v 0 1 0", "v 1 1 x", "f 1 2 3 4"}),
                Arguments.of((Object) new String[]{"v 0 0", "v 1 0 0", "v 0 1 0", "v 1 1 0", "f 1 2 3 4"}),
                Arguments.of((Object) new String[]{"v 0 0 0 x", "v 1 0 0", "v 0 1 0", "v 1 1 0", "f 1 2 3 4"}));
    }

    /**
     * Provides OBJ contents whose face indices are out of range, zero or negative.
     *
     * @return stream of OBJ lines.
     */
    private static Stream<Arguments> invalidIndexObjs() {
        return Stream.of(
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 4"}),
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 99"}),
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "f -3 -2 -1"}),
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "f 0 1 2"}),
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0 0", "f 1/2 2/2 3/2"}),
                Arguments.of((Object) new String[]{"# header", "v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 0 0 1", "f 1//2 2//2 3//2"}));
    }

    /**
     * Loads all chunks of provided OBJ file.
     *
     * @param file                 file to be loaded.
     * @param maxVertices          maximum number of vertices in a chunk.
     * @param allowDuplicates      true to allow duplicate vertices in a chunk.
     * @return list of loaded chunks.
     * @throws Exception if loading fails.
     */
    private static List<DataChunk> loadChunks(final File file, final int maxVertices, final boolean allowDuplicates)
            throws Exception {
        final var result = new ArrayList<DataChunk>();
        try (final var loader = new LoaderOBJ(file, maxVertices, allowDuplicates)) {
            final var iterator = loader.load();
            while (iterator.hasNext()) {
                result.add(iterator.next());
            }
        }
        return result;
    }

    /**
     * Writes an OBJ file with provided lines into the temporary directory.
     *
     * @param lines lines of the file.
     * @return created file.
     * @throws IOException if the file cannot be written.
     */
    private File writeObj(final String... lines) throws IOException {
        final var path = tempDir.resolve("fixture" + (counter++) + ".obj");
        Files.writeString(path, lines.length == 0 ? "" : String.join("\n", lines) + "\n");
        return path.toFile();
    }

    @Override
    public void onLoadStart(final Loader loader) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        checkLocked((LoaderOBJ) loader);
    }

    @Override
    public void onLoadEnd(final Loader loader) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        checkLocked((LoaderOBJ) loader);
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

        checkLocked((LoaderOBJ) loader);
    }

    @Override
    public MaterialLoaderOBJ onMaterialLoaderRequested(final LoaderOBJ loader, final String path) {
        final var origF = new File(path);
        final var folder = "./src/test/java/com/irurueta/geometry/io/";
        final var f = new File(folder, origF.getName());
        var hasToFail = path.contains("pitcher") || path.contains("Camara fotos");
        // we don't have mtl file for pitcher or camera
        try {
            final var materialLoader = new MaterialLoaderOBJ(f);
            if (hasToFail) {
                fail("IOException was expected but not thrown");
            }
            return materialLoader;
        } catch (final IOException e) {
            if (!hasToFail) {
                fail("No IOException exception was expected");
            }
            return null;
        }
    }

    private void checkLocked(final LoaderOBJ loader) {
        if (!loader.isLocked()) {
            lockedValid = false;
        }

        try {
            loader.setListener(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable ignore) {
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

    /**
     * Checks that face indices one past the last vertex, texture coordinate or
     * normal are rejected, and that the loader is unlocked afterwards so that
     * it can be reused.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenIndexIsOnePastLast_throwsLoaderExceptionAndUnlocks() throws Exception {
        final var contents = new String[][]{
                {"v 0 0 0", "v 1 0 0", "v 0 1 0", "f 1 2 4"},
                {"v 0 0 0", "v 1 0 0", "v 0 1 0", "vt 0 0", "f 1/1 2/1 3/2"},
                {"v 0 0 0", "v 1 0 0", "v 0 1 0", "vn 0 0 1", "f 1//1 2//1 3//2"}};

        for (final var lines : contents) {
            final var file = writeObj(lines);
            try (final var loader = new LoaderOBJ(file, 1000)) {
                assertThrows(LoaderException.class, () -> consume(loader));
                assertFalse(loader.isLocked());

                // loader can be reused after the failure
                assertThrows(LoaderException.class, () -> consume(loader));
                assertFalse(loader.isLocked());
            }
        }
    }
}
