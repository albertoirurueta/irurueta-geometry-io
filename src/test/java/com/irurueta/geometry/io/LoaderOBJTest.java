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
import java.io.IOException;

import static org.junit.Assert.*;

public class LoaderOBJTest implements LoaderListenerOBJ {

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

        // test constants are equal to LoaderPLY
        assertEquals(LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK,
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(!LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK,
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS,
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertEquals(LoaderOBJ.MIN_MAX_VERTICES_IN_CHUNK,
                LoaderPLY.MIN_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderOBJ.MIN_STREAM_POSITIONS,
                LoaderPLY.MIN_STREAM_POSITIONS);
        assertEquals(LoaderOBJ.PROGRESS_DELTA, LoaderPLY.PROGRESS_DELTA, 0.0);

        // test empty constructor
        LoaderOBJ loader = new LoaderOBJ();
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // test constructor with maxVerticesInChunk
        final int maxVerticesInChunk = 21423;
        loader = new LoaderOBJ(maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);


        // test constructor with maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final long maxStreamPositions = 500;
        loader = new LoaderOBJ(maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderOBJ(maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // constructor with file
        final File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderOBJ(f);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force IOException
        final File badF = new File("./non-existing");
        assertFalse(badF.exists());

        loader = null;
        try {
            loader = new LoaderOBJ(badF);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderOBJ(f, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(f, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, maxVerticesInChunk,
                    true);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderOBJ(f, 0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderOBJ(f, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderOBJ(f, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
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

        loader = new LoaderOBJ(listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);

        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);

        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);

        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(listener, 0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);

        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderOBJ(listener, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderOBJ(listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file and listener
        loader = new LoaderOBJ(f, listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, listener);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderOBJ(f, listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk,
                    true);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderOBJ(f, listener, 0,
                    true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(),
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderOBJ(f, listener, 0,
                    true, maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
    }

    @Test
    public void testHasSetFileAndIsReady() throws LockedException, IOException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        LoaderOBJ loader = new LoaderOBJ();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());

        // set file
        loader.setFile(f);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
    }


    @Test
    public void testGetSetListener() throws LockedException {
        final LoaderOBJ loader = new LoaderOBJ();
        assertNull(loader.getListener());

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

        loader.setListener(listener);
        assertEquals(loader.getListener(), listener);
    }

    @Test
    public void testGetSetMaxVerticesInChunk() throws LockedException {
        final LoaderOBJ loader = new LoaderOBJ();
        final int maxVerticesInChunk = 521351;

        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);

        // set new value
        loader.setMaxVerticesInChunk(maxVerticesInChunk);
        // check correctness
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);

        // Force IllegalArgumentException
        try {
            loader.setMaxVerticesInChunk(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testGetSetAllowDuplicateVerticesInChunk() throws LockedException {
        final LoaderOBJ loader = new LoaderOBJ();

        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);

        // set new value
        loader.setAllowDuplicateVerticesInChunk(true);
        // check correctness
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
    }

    @Test
    public void testGetSetMaxStreamPositions() throws LockedException {
        final long maxStreamPositions = 400;
        final LoaderOBJ loader = new LoaderOBJ();

        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);

        // set new value
        loader.setMaxStreamPositions(maxStreamPositions);
        // check correctness
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);

        // Force IllegalArgumentException
        try {
            loader.setMaxStreamPositions(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
    }


    @Test
    public void testIsValidFile() throws LockedException, IOException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final LoaderOBJ loader = new LoaderOBJ(f);
        assertTrue(loader.isValidFile());
        // to free file resources
        loader.close();
    }

    @Test
    public void testLoad() throws LockedException, NotReadyException,
            IOException, LoaderException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final LoaderOBJ loader = new LoaderOBJ(f);
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
    @SuppressWarnings("Duplicates")
    public void testLoadAndIterate() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 500;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        final File filePly = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        final LoaderPLY plyLoader = new LoaderPLY(filePly,
                maxNumberOfVerticesInChunk, false);
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        objLoader.setListener(this);


        final LoaderIterator plyIt = plyLoader.load();
        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext() && plyIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk plyChunk = plyIt.next();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            final float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            final float[] plyTexture = plyChunk.getTextureCoordiantesData();
            final short[] plyColors = plyChunk.getColorData();
            final float[] plyNormals = plyChunk.getNormalsData();
            final int[] plyIndices = plyChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);

            final int nVerticesInChunk = objVertices.length / 3;

            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);

            for (int i = 0; i < nVerticesInChunk; i++) {
                // check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i],
                        ERROR);

                // check y coordinate
                assertEquals(objVertices[3 * i + 1],
                        plyVertices[3 * i + 1], ERROR);

                // check z coordinate
                assertEquals(objVertices[3 * i + 2],
                        plyVertices[3 * i + 2], ERROR);

                // check x normal
                assertEquals(objNormals[3 * i], plyNormals[3 * i],
                        ERROR);

                // check y normal
                assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1],
                        ERROR);

                // check z normal
                assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2],
                        ERROR);
            }

            final int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
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

    @Test
    public void testLoadAndIterateMacbook() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 100000;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook.obj");
        final File filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");

        final LoaderPLY plyLoader = new LoaderPLY(filePly,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);

        final LoaderIterator plyIt = plyLoader.load();
        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext() && plyIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk plyChunk = plyIt.next();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            final float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            final float[] plyTexture = plyChunk.getTextureCoordiantesData();
            final short[] plyColors = plyChunk.getColorData();
            final float[] plyNormals = plyChunk.getNormalsData();
            final int[] plyIndices = plyChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);

            final int nVerticesInChunk = objVertices.length / 3;

            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);

            for (int i = 0; i < nVerticesInChunk; i++) {
                // check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i],
                        ERROR);

                // check y coordinate
                assertEquals(objVertices[3 * i + 1],
                        plyVertices[3 * i + 1], ERROR);

                // check z coordinate
                assertEquals(objVertices[3 * i + 2],
                        plyVertices[3 * i + 2], ERROR);
            }

            final int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
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

    @Test
    public void testLoadAndIterateMacbook2() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 100000;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook2.obj");
        final File filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");

        final LoaderPLY plyLoader = new LoaderPLY(filePly,
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        objLoader.setListener(this);


        final LoaderIterator plyIt = plyLoader.load();
        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext() && plyIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk plyChunk = plyIt.next();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            final float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            final float[] plyTexture = plyChunk.getTextureCoordiantesData();
            final short[] plyColors = plyChunk.getColorData();
            final float[] plyNormals = plyChunk.getNormalsData();
            final int[] plyIndices = plyChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);

            final int nVerticesInChunk = objVertices.length / 3;

            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);

            for (int i = 0; i < nVerticesInChunk; i++) {
                // check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i],
                        ERROR);

                // check y coordinate
                assertEquals(objVertices[3 * i + 1],
                        plyVertices[3 * i + 1], ERROR);

                // check z coordinate
                assertEquals(objVertices[3 * i + 2],
                        plyVertices[3 * i + 2], ERROR);

                // check x normal
                assertEquals(objNormals[3 * i], plyNormals[3 * i],
                        ERROR);

                // check y normal
                assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1],
                        ERROR);

                // check z normal
                assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2],
                        ERROR);
            }

            final int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
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

    @Test
    public void testLoadAndIteratePitcher() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 100000;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/pitcher.obj");
        final File filePly = new File("./src/test/java/com/irurueta/geometry/io/pitcher.ply");

        final LoaderPLY plyLoader = new LoaderPLY(filePly,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);

        final LoaderIterator plyIt = plyLoader.load();
        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext() && plyIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final DataChunk plyChunk = plyIt.next();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            final float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            final float[] plyTexture = plyChunk.getTextureCoordiantesData();
            final short[] plyColors = plyChunk.getColorData();
            final float[] plyNormals = plyChunk.getNormalsData();
            final int[] plyIndices = plyChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);

            final int nVerticesInChunk = objVertices.length / 3;

            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);

            for (int i = 0; i < nVerticesInChunk; i++) {
                // check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i],
                        ERROR);

                // check y coordinate
                assertEquals(objVertices[3 * i + 1],
                        plyVertices[3 * i + 1], ERROR);

                // check z coordinate
                assertEquals(objVertices[3 * i + 2],
                        plyVertices[3 * i + 2], ERROR);
            }

            final int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);

            for (int i = 0; i < nIndicesInChunk; i++) {
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

    @Test
    public void testLoadAndIterateCamera() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 100000;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/camera.obj");
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);

        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);

            final int nVerticesInChunk = objVertices.length / 3;

            for (final int objIndex : objIndices) {
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

    @Test
    public void testLoadAndIteratePotro() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {

        final int maxNumberOfVerticesInChunk = 100000;
        final File fileObj = new File("./src/test/java/com/irurueta/geometry/io/potro.obj");
        final LoaderOBJ objLoader = new LoaderOBJ(fileObj,
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);

        final LoaderIterator objIt = objLoader.load();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        // check correctness of chunks
        while (objIt.hasNext()) {
            final DataChunk objChunk = objIt.next();

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();

            final float[] objVertices = objChunk.getVerticesCoordinatesData();
            final float[] objTexture = objChunk.getTextureCoordiantesData();
            final short[] objColors = objChunk.getColorData();
            final float[] objNormals = objChunk.getNormalsData();
            final int[] objIndices = objChunk.getIndicesData();

            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);

            final int nVerticesInChunk = objVertices.length / 3;

            for (final int objIndice : objIndices) {
                assertTrue(objIndice < nVerticesInChunk);
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
        final File origF = new File(path);
        final String folder = "./src/test/java/com/irurueta/geometry/io/";
        final File f = new File(folder, origF.getName());
        boolean hasToFail = path.contains("pitcher") || path.contains("Camara fotos");
        // we don't have mtl file for pitcher or camera
        try {
            final MaterialLoaderOBJ materialLoader = new MaterialLoaderOBJ(f);
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
        } catch (final Throwable ignore) {
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
