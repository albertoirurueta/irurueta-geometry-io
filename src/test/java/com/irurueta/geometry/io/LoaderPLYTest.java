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

public class LoaderPLYTest implements LoaderListener {

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @Test
    public void testConstants() {
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
    public void testConstructors() throws LockedException, LoaderException,
            IOException {
        LoaderPLY loader = new LoaderPLY();
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
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
        loader = new LoaderPLY(maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
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
            loader = new LoaderPLY(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);


        // test constructor with maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
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
            loader = new LoaderPLY(0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        final long maxStreamPositions = 500;
        loader = new LoaderPLY(maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
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
            loader = new LoaderPLY(0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderPLY(maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // constructor with file
        final File f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        final File badF = new File("./non-existing");
        assertFalse(badF.exists());

        loader = null;
        try {
            loader = new LoaderPLY(badF);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // test constructor with file and maxVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(f, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderPLY(f, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // Test constructor with loader listener
        loader = new LoaderPLY(this);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);
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
        loader = new LoaderPLY(this, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);

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
            loader = new LoaderPLY(this, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk and
        // allowDuplicateVerticesInChunk
        loader = new LoaderPLY(this, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());

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
            loader = new LoaderPLY(this, 0, true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with loader listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(this, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);

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
            loader = new LoaderPLY(this, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderPLY(this, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file and listener
        loader = new LoaderPLY(f, this);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderPLY(badF, this);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderPLY(f, this, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);
        // to free file resources
        loader.close();

        // Force IOException
        loader = null;
        try {
            loader = new LoaderPLY(badF, this, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, this, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk
        // and allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, this, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, this, maxVerticesInChunk,
                    true);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, this, 0,
                    true);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);

        // test constructor with file, listener, maxVerticesInChunk,
        // allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, this, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), this);
        // to free file resources
        loader.close();

        // Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, this, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, this, 0,
                    true, maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
        try {
            loader = new LoaderPLY(f, this, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (final IllegalArgumentException ignore) {
        }
        assertNull(loader);
    }

    @Test
    public void testHasSetFileAndIsReady() throws LockedException, IOException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        LoaderPLY loader = new LoaderPLY();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());

        // set file
        loader.setFile(f);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
    }

    @Test
    public void testGetSetListener() throws LockedException {
        final LoaderPLY loader = new LoaderPLY();
        assertNull(loader.getListener());

        loader.setListener(this);
        assertEquals(loader.getListener(), this);
    }

    @Test
    public void testGetSetMaxVerticesInChunk() throws LockedException {
        final LoaderPLY loader = new LoaderPLY();
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
        final LoaderPLY loader = new LoaderPLY();

        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);

        // set new value
        loader.setAllowDuplicateVerticesInChunk(true);
        // check correctness
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
    }

    @Test
    public void testGetSetMaxStreamPositions() throws LockedException {
        final long maxStreamPositions = 400;
        final LoaderPLY loader = new LoaderPLY();

        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);

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
        final File f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        // check that file exists
        assertTrue(f.exists());
        assertTrue(f.isFile());

        final LoaderPLY loader = new LoaderPLY(f);
        assertTrue(loader.isValidFile());
        // to free file resources
        loader.close();
    }

    @Test
    public void testLoadAndIterate() throws IOException, LockedException,
            LoaderException, NotReadyException, NotAvailableException {
        final File f = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        final int maxNumberOfVerticesInChunk = 500;
        final LoaderPLY loader = new LoaderPLY(f,
                maxNumberOfVerticesInChunk, false);
        loader.setListener(this);

        final LoaderIterator it = loader.load();

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

            final DataChunk chunk = it.next();

            final float[] vertices = chunk.getVerticesCoordinatesData();
            final float[] texture = chunk.getTextureCoordinatesData();
            final short[] colors = chunk.getColorData();
            final float[] normals = chunk.getNormalsData();
            final int[] indices = chunk.getIndicesData();

            assertEquals(vertices.length, normals.length);
            assertNull(texture);
            assertNotNull(colors);

            final int nVerticesInChunk = vertices.length / 3;

            for (int index : indices) {
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

    private void checkLocked(final LoaderPLY loader) {
        assertTrue(loader.isLocked());
        try {
            loader.setMaxVerticesInChunk(0);
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        }
        try {
            loader.setAllowDuplicateVerticesInChunk(false);
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        }
        try {
            loader.setMaxStreamPositions(1);
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        }
        try {
            loader.isValidFile();
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        } catch (final IOException e) {
            fail("LockedException expected but not thrown");
        }
        try {
            loader.load();
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        } catch (final Exception e) {
            fail("LockedException expected but not thrown");
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
