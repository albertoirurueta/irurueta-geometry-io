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

import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class LoaderIteratorPLYTest implements LoaderListener {
    
    private static double ERROR = 1e-4;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;
    
    public LoaderIteratorPLYTest() { }

    @BeforeClass
    public static void setUpClass() { }

    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    
    @Test
    public void testConstructors() throws LockedException, IOException, 
            LoaderException {
        
        //test empty constructor
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
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }

        
        
        //test constructor with maxVerticesInChunk
        int maxVerticesInChunk = 21423;
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
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with maxVerticesInChunk and 
        //allowDuplicateVerticesInChunk
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
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
           loader = new LoaderPLY(0, true);
           fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with maxVerticesInChunk, 
        //allowDuplicateVerticesInChunk and maxStreamPositions
        long maxStreamPositions = 500;
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
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        try {
            loader = new LoaderPLY(maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //constructor with file
        File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
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
                        
        //Force FileNotFoundException
        File badF = new File("./non-existing");
        assertFalse(badF.exists());

        loader = null;
        try {
            loader = new LoaderPLY(badF);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file and maxVerticesInChunk        
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

        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file, maxVerticesInChunk and 
        //allowDuplicateVerticesInChunk
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
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file, maxVerticesInChunk, 
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());        
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        try {
            loader = new LoaderPLY(f, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //Test constructor with loader listener
        LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(Loader loader) { }

            @Override
            public void onLoadEnd(Loader loader) { }

            @Override
            public void onLoadProgressChange(Loader loader, float progress) { }
        };
        
        loader = new LoaderPLY(listener);
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        
        //test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderPLY(listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);        
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with loader listener, maxVerticesInChunk and
        //allowDuplicateVerticesInChunk
        loader = new LoaderPLY(listener, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0, true);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with loader listener, maxVerticesInChunk,
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        try {
            loader.isValidFile();
            fail("IOException not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderPLY(listener, 0, true,
                    maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        try {
            loader = new LoaderPLY(listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file and listener
        loader = new LoaderPLY(f, listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderPLY(f, listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file, listener, maxVerticesInChunk
        //and allowDuplicateVerticesInChunk
        loader = new LoaderPLY(f, listener, maxVerticesInChunk,
                true);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(),
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk,
                    true);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0,
                    true);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file, listener, maxVerticesInChunk,
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderPLY(f, listener, maxVerticesInChunk,
                true, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_PLY);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force FileNotFoundException
        loader = null;
        try {
            loader = new LoaderPLY(badF, listener, maxVerticesInChunk,
                    true, maxStreamPositions);
            fail("FileNotFoundException not thrown");
        } catch (FileNotFoundException ignore) { }
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderPLY(f, listener, 0,
                    true, maxStreamPositions);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        try {
            loader = new LoaderPLY(f, listener, maxVerticesInChunk,
                    true, 0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
    }
    
    @Test
    public void testHasSetFileAndIsReady() 
            throws LockedException, IOException {
        File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderPLY loader = new LoaderPLY();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());
        
        //set file
        loader.setFile(f);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
    }
    
    @Test
    public void testGetSetFileSizeLimitToKeepInMemory() throws LockedException {
        LoaderPLY loader = new LoaderPLY();
        assertEquals(loader.getFileSizeLimitToKeepInMemory(), 
                LoaderPLY.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        
        //set new value
        long value = 1000000;
        loader.setFileSizeLimitToKeepInMemoery(value);
        //and check correctness
        assertEquals(loader.getFileSizeLimitToKeepInMemory(), value);
    }
    
    @Test
    public void testGetSetListener() throws LockedException {
        LoaderPLY loader = new LoaderPLY();
        assertNull(loader.getListener());
        
        LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(Loader loader) { }

            @Override
            public void onLoadEnd(Loader loader) { }

            @Override
            public void onLoadProgressChange(Loader loader, float progress) { }
        };
        
        loader.setListener(listener);
        assertEquals(loader.getListener(), listener);
    }
    
    @Test
    public void testGetSetMaxVerticesInChunk() throws LockedException {
        LoaderPLY loader = new LoaderPLY();
        int maxVerticesInChunk = 521351;
        
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        
        //set new value
        loader.setMaxVerticesInChunk(maxVerticesInChunk);
        //check correctness
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        
        //Force IllegalArgumentException
        try {
            loader.setMaxVerticesInChunk(0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
    }
    
    @Test
    public void testGetSetAllowDuplicateVerticesInChunk() throws LockedException {
        LoaderPLY loader = new LoaderPLY();
        
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderPLY.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        
        //set new value
        loader.setAllowDuplicateVerticesInChunk(true);
        //check correctness
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
    }
    
    @Test
    public void testGetSetMaxStreamPositions() throws LockedException {
        long maxStreamPositions = 400;
        LoaderPLY loader = new LoaderPLY();
        
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderPLY.DEFAULT_MAX_STREAM_POSITIONS);
        
        //set new value
        loader.setMaxStreamPositions(maxStreamPositions);
        //check correctness
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        
        //Force IllegalArgumentException
        try {
            loader.setMaxStreamPositions(0);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) { }
    }
    
    @Test
    public void testIsValidFile() throws LockedException, IOException {
        //Test for BIG ENDIAN
        File f = new File("./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        LoaderPLY loader = new LoaderPLY(f);
        assertTrue(loader.isValidFile());
        
        //Test for LITTLE ENDIAN
        f = new File("./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertTrue(loader.isValidFile());        
        
        //Test for ASCII
        f = new File("./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        loader = new LoaderPLY(f);
        assertTrue(loader.isValidFile());                
    }
    
    @Test
    public void testLoad() throws LockedException, NotReadyException,
            IOException, LoaderException {
        //Test for BIG ENDIAN
        File f = new File("./src/test/java/com/irurueta/geometry/io/randomBig.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderPLY loader = new LoaderPLY(f);
        loader.setListener(this);
        assertNotNull(loader.load());
        loader.close(); //releases file resources
        
        //Test for LITTLE_ENDIAN
        f = new File("./src/test/java/com/irurueta/geometry/io/randomLittle.ply");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        loader = new LoaderPLY(f);
        assertNotNull(loader.load());
        loader.close(); //releases file resources
        
        //Test for ASCII
        f = new File("./src/test/java/com/irurueta/geometry/io/randomAscii.ply");
        assertTrue(f.exists());
        assertTrue(f.isFile());
        
        loader = new LoaderPLY(f);
        assertNotNull(loader.load());
        loader.close(); //releases file resources
        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
    }
    
    @Test
    @SuppressWarnings("Duplicates")
    public void testLoadAndIterate() 
            throws IllegalArgumentException, LockedException, NotReadyException,
            IOException, LoaderException, NotAvailableException {
        int maxNumberOfVerticesInChunk = 9;
        
        File fileAscii = new File(
                "./src/test/java/com/irurueta/geometry/io/randomAscii.ply");
        File fileLittle = new File(
                "./src/test/java/com/irurueta/geometry/io/randomLittle.ply");
        File fileBig = new File(
                "./src/test/java/com/irurueta/geometry/io/randomBig.ply");
        
        LoaderPLY asciiLoader = new LoaderPLY(fileAscii, 
                maxNumberOfVerticesInChunk);
        asciiLoader.setListener(this);        
        LoaderPLY littleLoader = new LoaderPLY(fileLittle,
                maxNumberOfVerticesInChunk);
        littleLoader.setListener(this);
        LoaderPLY bigLoader = new LoaderPLY(fileBig,
                maxNumberOfVerticesInChunk);
        bigLoader.setListener(this);
        
        
        
        LoaderIterator asciiIt = asciiLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        LoaderIterator littleIt = littleLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        LoaderIterator bigIt = bigLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        
        //check correctness of chunks
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            DataChunk asciiChunk = asciiIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk littleChunk = littleIt.next();
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk bigChunk = bigIt.next();            
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            
            
            float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            short[] asciiColors = asciiChunk.getColorData();
            int[] asciiIndices = asciiChunk.getIndicesData();
            
            float[] littleVertices = littleChunk.getVerticesCoordinatesData();
            short[] littleColors = littleChunk.getColorData();
            int[] littleIndices = littleChunk.getIndicesData();
            
            float[] bigVertices = bigChunk.getVerticesCoordinatesData();
            short[] bigColors = bigChunk.getColorData();
            int[] bigIndices = bigChunk.getIndicesData();
            
            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);
            
            int nVerticesInChunk = asciiVertices.length / 3;
            
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
                //check x coordinate
                x = littleVertices[3 * i];
                assertEquals(asciiVertices[3 * i], littleVertices[3 * i], 
                        ERROR);
                assertEquals(littleVertices[3 * i], bigVertices[3 * i], 0.0);
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                
                
                //check that values are integers in floating point format
                //(the files contains vertices as integer values)
                assertEquals(asciiVertices[3 * i], 
                        Math.round(asciiVertices[3 * i]), ERROR);
                assertEquals(littleVertices[3 * i], 
                        Math.round(littleVertices[3 * i]), ERROR);
                assertEquals(bigVertices[3 * i], Math.round(bigVertices[3 * i]),
                        ERROR);
                
                //check y coordinate
                y = littleVertices[3 * i + 1];
                assertEquals(asciiVertices[3 * i + 1], 
                        littleVertices[3 * i + 1], ERROR);
                assertEquals(littleVertices[3 * i + 1], bigVertices[3 * i + 1], 
                        0.0);
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                
                
                //check that values are integers in floating point format
                //(the files contains vertices as integer values)
                assertEquals(asciiVertices[3 * i + 1], 
                        Math.round(asciiVertices[3 * i + 1]), ERROR);
                assertEquals(littleVertices[3 * i + 1], 
                        Math.round(littleVertices[3 * i + 1]), ERROR);
                assertEquals(bigVertices[3 * i + 1], 
                        Math.round(bigVertices[3 * i + 1]), ERROR);

                //check z coordinate
                z = littleVertices[3 * i + 2];
                assertEquals(asciiVertices[3 * i + 2], 
                        littleVertices[3 * i + 2], ERROR);
                assertEquals(littleVertices[3 * i + 2], bigVertices[3 * i + 2], 
                        0.0);
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
                
                
                //check that values are integers in floating point format
                //(the files contains vertices as integer values)
                assertEquals(asciiVertices[3 * i + 2], 
                        Math.round(asciiVertices[3 * i + 2]), ERROR);
                assertEquals(littleVertices[3 * i + 2], 
                        Math.round(littleVertices[3 * i + 2]), ERROR);
                assertEquals(bigVertices[3 * i + 2], 
                        Math.round(bigVertices[3 * i + 2]), ERROR);
                
                //check red color
                assertEquals(asciiColors[3 * i], littleColors[3 * i]);
                assertEquals(littleColors[3 * i], bigColors[3 * i]);
                assertTrue(asciiColors[3 * i] >= 0 && 
                        asciiColors[3 * i] <= 255);
                
                //check green color
                assertEquals(asciiColors[3 * i + 1], littleColors[3 * i + 1]);
                assertEquals(littleColors[3 * i + 1], bigColors[3 * i + 1]);
                assertTrue(asciiColors[3 * i + 1] >= 0 && 
                        asciiColors[3 * i + 1] <= 255);                
                
                //check blue color
                assertEquals(asciiColors[3 * i + 2], littleColors[3 * i + 2]);
                assertEquals(littleColors[3 * i + 2], bigColors[3 * i + 2]);
                assertTrue(asciiColors[3 * i + 2] >= 0 && 
                        asciiColors[3 * i + 2] <= 255);                                
            }
            
            int nIndicesInChunk = asciiIndices.length;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);
            
            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(littleIndices[i] < nVerticesInChunk);
                assertTrue(bigIndices[i] < nVerticesInChunk);
                
                assertEquals(asciiIndices[i], littleIndices[i]);
                assertEquals(littleIndices[i], bigIndices[i]);
            }
            
            //check bounding box values
            assertEquals(asciiChunk.getMinX(), minX, ERROR);
            assertEquals(asciiChunk.getMinX(), littleChunk.getMinX(), ERROR);
            assertEquals(littleChunk.getMinX(), minX, 0.0);
            assertEquals(littleChunk.getMinX(), bigChunk.getMinX(), 0.0);
            
            assertEquals(asciiChunk.getMinY(), minY, ERROR);
            assertEquals(asciiChunk.getMinY(), littleChunk.getMinY(), ERROR);
            assertEquals(littleChunk.getMinY(), minY, 0.0);
            assertEquals(littleChunk.getMinY(), bigChunk.getMinY(), 0.0);
            
            assertEquals(asciiChunk.getMinZ(), minZ, ERROR);
            assertEquals(asciiChunk.getMinZ(), littleChunk.getMinZ(), ERROR);
            assertEquals(littleChunk.getMinZ(), minZ, 0.0);
            assertEquals(littleChunk.getMinZ(), bigChunk.getMinZ(), 0.0);
            
            assertEquals(asciiChunk.getMaxX(), maxX, ERROR);
            assertEquals(asciiChunk.getMaxX(), littleChunk.getMaxX(), ERROR);
            assertEquals(littleChunk.getMaxX(), maxX, 0.0);
            assertEquals(littleChunk.getMaxX(), bigChunk.getMaxX(), 0.0);
            
            assertEquals(asciiChunk.getMaxY(), maxY, ERROR);
            assertEquals(asciiChunk.getMaxY(), littleChunk.getMaxY(), ERROR);
            assertEquals(littleChunk.getMaxY(), maxY, 0.0);
            assertEquals(littleChunk.getMaxY(), bigChunk.getMaxY(), 0.0);
            
            assertEquals(asciiChunk.getMaxZ(), maxZ, ERROR);
            assertEquals(asciiChunk.getMaxZ(), littleChunk.getMaxZ(), ERROR);
            assertEquals(littleChunk.getMaxZ(), maxZ, 0.0);
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
        
        //releases file resources
        asciiLoader.close(); 
        littleLoader.close();
        bigLoader.close();
    }
    
    @Test
    @SuppressWarnings("all")
    public void testLoadAndIterateRealFile() throws LockedException, NotReadyException,
            IOException, LoaderException, NotAvailableException {
        
        File fileAscii = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarAscii.ply");
        File fileLittle = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarLittleEndian.ply");
        File fileBig = new File(
                "./src/test/java/com/irurueta/geometry/io/pilarBigEndian.ply");
        
        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileLittle.exists());
        assertTrue(fileLittle.isFile());
        assertTrue(fileBig.exists());
        assertTrue(fileBig.isFile());
        
        LoaderPLY asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        LoaderPLY littleLoader = new LoaderPLY(fileLittle);
        littleLoader.setListener(this);
        LoaderPLY bigLoader = new LoaderPLY(fileBig);
        bigLoader.setListener(this);
        
        LoaderIterator asciiIt = asciiLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        LoaderIterator littleIt = littleLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        LoaderIterator bigIt = bigLoader.load();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        
        //read and compare chunks of data
        while (asciiIt.hasNext() && littleIt.hasNext() && bigIt.hasNext()) {
            DataChunk asciiChunk = asciiIt.next();            
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk littleChunk = littleIt.next();
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk bigChunk = bigIt.next();   
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            
            float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            short[] asciiColors = asciiChunk.getColorData();
            int[] asciiIndices = asciiChunk.getIndicesData();
            
            float[] littleVertices = littleChunk.getVerticesCoordinatesData();
            short[] littleColors = littleChunk.getColorData();
            int[] littleIndices = littleChunk.getIndicesData();
            
            float[] bigVertices = bigChunk.getVerticesCoordinatesData();
            short[] bigColors = bigChunk.getColorData();
            int[] bigIndices = bigChunk.getIndicesData();

            assertEquals(asciiVertices.length, asciiColors.length);
            assertEquals(littleVertices.length, littleColors.length);
            assertEquals(bigVertices.length, bigColors.length);

            //compare ascii, little and big endian chunks
            assertEquals(asciiVertices.length, littleVertices.length);
            assertEquals(asciiVertices.length, bigVertices.length);
            
            int arraySize = asciiVertices.length;
            for (int i = 0; i < arraySize; i++) {
                assertEquals(littleVertices[i], bigVertices[i], 0.0);
                assertEquals(asciiVertices[i], littleVertices[i], ERROR);                
                
                assertEquals(littleColors[i], bigColors[i]);
                assertEquals(asciiColors[i], littleColors[i]);
                
                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(littleColors[i] >= 0 && littleColors[i] <= 255);
                assertTrue(bigColors[i] >= 0 && bigColors[i] <= 255);
            }
            
            int nIndicesInChunk = asciiIndices.length;
            int nVerticesInChunk = arraySize / 3;
            assertEquals(nIndicesInChunk, littleIndices.length);
            assertEquals(nIndicesInChunk, bigIndices.length);
            
            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(littleIndices[i] < nVerticesInChunk);
                assertTrue(bigIndices[i] < nVerticesInChunk);
                
                assertEquals(asciiIndices[i], littleIndices[i]);
                assertEquals(littleIndices[i], bigIndices[i]);
            }
            
            //check bounding box values
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, 
                    minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE,
                    maxZ = -Float.MAX_VALUE;
            float x, y, z;
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
            
            assertEquals(asciiChunk.getMinX(), minX, ERROR);
            assertEquals(asciiChunk.getMinX(), littleChunk.getMinX(), ERROR);
            assertEquals(littleChunk.getMinX(), minX, 0.0);
            assertEquals(littleChunk.getMinX(), bigChunk.getMinX(), 0.0);
            
            assertEquals(asciiChunk.getMinY(), minY, ERROR);
            assertEquals(asciiChunk.getMinY(), littleChunk.getMinY(), ERROR);
            assertEquals(littleChunk.getMinY(), minY, 0.0);
            assertEquals(littleChunk.getMinY(), bigChunk.getMinY(), 0.0);
            
            assertEquals(asciiChunk.getMinZ(), minZ, ERROR);
            assertEquals(asciiChunk.getMinZ(), littleChunk.getMinZ(), ERROR);
            assertEquals(littleChunk.getMinZ(), minZ, 0.0);
            assertEquals(littleChunk.getMinZ(), bigChunk.getMinZ(), 0.0);
            
            assertEquals(asciiChunk.getMaxX(), maxX, ERROR);
            assertEquals(asciiChunk.getMaxX(), littleChunk.getMaxX(), ERROR);
            assertEquals(littleChunk.getMaxX(), maxX, 0.0);
            assertEquals(littleChunk.getMaxX(), bigChunk.getMaxX(), 0.0);
            
            assertEquals(asciiChunk.getMaxY(), maxY, ERROR);
            assertEquals(asciiChunk.getMaxY(), littleChunk.getMaxY(), ERROR);
            assertEquals(littleChunk.getMaxY(), maxY, 0.0);
            assertEquals(littleChunk.getMaxY(), bigChunk.getMaxY(), 0.0);
            
            assertEquals(asciiChunk.getMaxZ(), maxZ, ERROR);
            assertEquals(asciiChunk.getMaxZ(), littleChunk.getMaxZ(), ERROR);
            assertEquals(littleChunk.getMaxZ(), maxZ, 0.0);
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
        
        //releases file resources
        asciiLoader.close(); 
        littleLoader.close();
        bigLoader.close();        
    }
    
    @Test
    @SuppressWarnings("all")
    public void testLoadAndIterateRealFileWithNormals() throws LockedException,
            NotReadyException, IOException, LoaderException, NotAvailableException {
        
        File fileAscii = 
                new File("./src/test/java/com/irurueta/geometry/io/booksAscii.ply");
        File fileBinary = 
                new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        
        assertTrue(fileAscii.exists());
        assertTrue(fileAscii.isFile());
        assertTrue(fileBinary.exists());
        assertTrue(fileBinary.isFile());
        
        LoaderPLY asciiLoader = new LoaderPLY(fileAscii);
        asciiLoader.setListener(this);
        LoaderPLY binaryLoader = new LoaderPLY(fileBinary);
        binaryLoader.setListener(this);
        
        LoaderIterator asciiIt = asciiLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        LoaderIterator binaryIt = binaryLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        
        //read and compare chunks of data
        while (asciiIt.hasNext() && binaryIt.hasNext()) {
            DataChunk asciiChunk = asciiIt.next();      
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk binaryChunk = binaryIt.next();
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            
            float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            short[] asciiColors = asciiChunk.getColorData();
            int[] asciiIndices = asciiChunk.getIndicesData();
            float[] asciiTextureCoords = asciiChunk.getTextureCoordiantesData();
            float[] asciiNormals = asciiChunk.getNormalsData();
            
            
            float[] binaryVertices = binaryChunk.getVerticesCoordinatesData();
            short[] binaryColors = binaryChunk.getColorData();
            int[] binaryIndices = binaryChunk.getIndicesData();
            float[] binaryTextureCoords = binaryChunk.getTextureCoordiantesData();
            float[] binaryNormals = binaryChunk.getNormalsData();
            
            int arraySize = asciiVertices.length;     
            int nVerticesInChunk = arraySize / 3;
            
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

            //compare ascii and binary chunks                        
            for (int i = 0; i < asciiVertices.length; i++) {
                assertEquals(asciiVertices[i], binaryVertices[i], ERROR);                
                assertEquals(asciiNormals[i], binaryNormals[i], ERROR);
            }
            
            for (int i = 0; i < asciiColors.length; i++) {
                assertEquals(asciiColors[i], binaryColors[i]);
                assertTrue(asciiColors[i] >= 0 && asciiColors[i] <= 255);
                assertTrue(binaryColors[i] >= 0 && binaryColors[i] <= 255);
            }
            
            int nIndicesInChunk = asciiIndices.length;
            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                assertTrue(binaryIndices[i] < nVerticesInChunk);
                
                assertEquals(asciiIndices[i], binaryIndices[i]);
            }
            
            //check bounding box values
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, 
                    minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE,
                    maxZ = -Float.MAX_VALUE;
            float x, y, z;
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

            assertEquals(asciiChunk.getMinX(), minX, ERROR);
            assertEquals(binaryChunk.getMinX(), minX, 0.0);
            assertEquals(asciiChunk.getMinX(), binaryChunk.getMinX(), ERROR);
            
            assertEquals(asciiChunk.getMinY(), minY, ERROR);
            assertEquals(binaryChunk.getMinY(), minY, 0.0);
            assertEquals(asciiChunk.getMinY(), binaryChunk.getMinY(), ERROR);
            
            assertEquals(asciiChunk.getMinZ(), minZ, ERROR);
            assertEquals(binaryChunk.getMinZ(), minZ, 0.0);
            assertEquals(asciiChunk.getMinZ(), binaryChunk.getMinZ(), ERROR);
            
            assertEquals(asciiChunk.getMaxX(), maxX, ERROR);
            assertEquals(binaryChunk.getMaxX(), maxX, 0.0);
            assertEquals(asciiChunk.getMaxX(), binaryChunk.getMaxX(), ERROR);
            
            assertEquals(asciiChunk.getMaxY(), maxY, ERROR);
            assertEquals(binaryChunk.getMaxY(), maxY, 0.0);
            assertEquals(asciiChunk.getMaxY(), binaryChunk.getMaxY(), ERROR);
            
            assertEquals(asciiChunk.getMaxZ(), maxZ, ERROR);
            assertEquals(binaryChunk.getMaxZ(), maxZ, 0.0);
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
        
        //releases file resources
        asciiLoader.close(); 
        binaryLoader.close();
    }

    @Override
    public void onLoadStart(Loader loader) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;
        
        checkLocked((LoaderPLY)loader);
    }

    @Override
    public void onLoadEnd(Loader loader) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;
        
        checkLocked((LoaderPLY)loader);
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
        
        checkLocked((LoaderPLY)loader);
    }

    @SuppressWarnings("Duplicates")
    private void checkLocked(LoaderPLY loader) {
        if (!loader.isLocked()) {
            lockedValid = false;
        }
        
        try {
            loader.setListener(null);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            loader.load();
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            loader.setMaxVerticesInChunk(1);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            loader.setAllowDuplicateVerticesInChunk(true);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            loader.setMaxStreamPositions(1);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
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
