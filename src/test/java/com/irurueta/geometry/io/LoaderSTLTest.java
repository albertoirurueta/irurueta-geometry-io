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
import java.io.IOException;

import static org.junit.Assert.*;

public class LoaderSTLTest implements LoaderListener {

    private static final double ERROR = 1e-4;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;
    
    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;
    
    public LoaderSTLTest() { }
    
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
        
        //test constants are equal to LoaderPLY
        assertEquals(LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK,
                LoaderPLY.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderSTL.MIN_MAX_VERTICES_IN_CHUNK,
                LoaderPLY.MIN_MAX_VERTICES_IN_CHUNK);
        assertEquals(LoaderSTL.PROGRESS_DELTA, LoaderPLY.PROGRESS_DELTA, 0.0);
        
        //test empty constructor
        LoaderSTL loader = new LoaderSTL();
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //test constructor with maxVerticesInChunk
        int maxVerticesInChunk = 21423;
        loader = new LoaderSTL(maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderSTL(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        
        //test constructor with file
        File f = new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        loader = new LoaderSTL(f);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        loader.close(); //to free file resources
        
        //Force IOException
        File badF = new File("./non-existing");
        assertFalse(badF.exists());
        
        loader = null;
        try {
            loader = new LoaderSTL(badF);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //test constructor with file and maxVerticesInChunk
        loader = new LoaderSTL(f, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        loader.close(); //to free file resources
        
        
        //Force IOException
        loader = null;
        try {
            loader = new LoaderSTL(badF, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //Force IllegalArgumentException
        loader = null;
        try {
           loader = new LoaderSTL(f, 0);
           fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        //test constructor with loader listener
        LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(Loader loader) { }

            @Override
            public void onLoadEnd(Loader loader) { }

            @Override
            public void onLoadProgressChange(Loader loader, float progress) { }
        };
        
        loader = new LoaderSTL(listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderSTL(listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //Force IllegalArgumentException
        loader = null;
        try {
            loader = new LoaderSTL(listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
        
        //test constructor with file and listener
        loader = new LoaderSTL(f, listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderSTL.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force IOException
        loader = null;
        try {
            loader = new LoaderSTL(badF, listener);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderSTL(f, listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_STL);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        loader.close(); //to free file resources
        
        //Force IOException
        loader = null;
        try {
            loader = new LoaderSTL(badF, listener, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //Force IllegalArgumentException
        try {
            loader = new LoaderSTL(f, listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ignore) { }
        assertNull(loader);
    }
    
    @Test
    public void testHasSetFileAndIsReady() throws LockedException, IOException {
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderSTL loader = new LoaderSTL();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());
        
        //set file
        loader.setFile(f);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
    }    
    
    @Test
    public void testGetSetListener() throws LockedException {
        LoaderSTL loader = new LoaderSTL();
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
        LoaderSTL loader = new LoaderSTL();
        int maxVerticesInChunk = 521351;
        
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        
        //set new value
        loader.setMaxVerticesInChunk(maxVerticesInChunk);
        //check correctness
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        
        //Force IllegalArgumentException
        try {
            loader.setMaxVerticesInChunk(0);
            fail("IllegalArgumentException expected but not thrown");
        } catch (IllegalArgumentException ignore) { }
    }     
    
    @Test
    public void testIsValidFile() throws LockedException, IOException {
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        LoaderSTL loader = new LoaderSTL(f);
        assertTrue(loader.isValidFile());        
        loader.close(); //to free file resources
    }   
    
    @Test
    public void testLoad() throws LockedException, NotReadyException,
            IOException, LoaderException {
        File f = new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderSTL loader = new LoaderSTL(f);
        //loader.setListener(this);
        assertNotNull(loader.load());
                
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        loader.close(); //to free file resources
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
    
    @Test
    @SuppressWarnings("Duplicates")
    public void testLoadAndIterate() throws IllegalArgumentException, LockedException,
            NotReadyException, IOException, LoaderException, 
            NotAvailableException {
        
        int maxNumberOfVerticesInChunk = 500;        
        File fileStlBin = 
                new File("./src/test/java/com/irurueta/geometry/io/pitcher-bin.stl");
        File fileStlAscii = 
                new File("./src/test/java/com/irurueta/geometry/io/pitcher-ascii.stl");
        
        LoaderSTL binLoader = new LoaderSTL(fileStlBin, 
                maxNumberOfVerticesInChunk);
        LoaderSTL asciiLoader = new LoaderSTL(fileStlAscii, 
                maxNumberOfVerticesInChunk);
        binLoader.setListener(this);        
        asciiLoader.setListener(this);
        
        
        LoaderIterator binIt = binLoader.load();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        LoaderIterator asciiIt = asciiLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while (binIt.hasNext() && asciiIt.hasNext()) {
            DataChunk binChunk = binIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk asciiChunk = asciiIt.next();                                    

            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            float[] binVertices = binChunk.getVerticesCoordinatesData();
            float[] binTexture = binChunk.getTextureCoordiantesData();
            short[] binColors = binChunk.getColorData();
            float[] binNormals = binChunk.getNormalsData();
            int[] binIndices = binChunk.getIndicesData();
            
            float[] asciiVertices = asciiChunk.getVerticesCoordinatesData();
            float[] asciiTexture = asciiChunk.getTextureCoordiantesData();
            short[] asciiColors = asciiChunk.getColorData();
            float[] asciiNormals = asciiChunk.getNormalsData();
            int[] asciiIndices = asciiChunk.getIndicesData();
            
            assertEquals(binVertices.length, binNormals.length);
            assertNull(binTexture);
            assertNull(binColors);
            assertEquals(asciiVertices.length, asciiNormals.length);
            assertNull(asciiTexture);
            assertNull(asciiColors);
                        
            int nVerticesInChunk = binVertices.length / 3;
                        
            assertEquals(nVerticesInChunk, binVertices.length / 3);
            assertEquals(nVerticesInChunk, asciiVertices.length / 3);
            
            for (int i = 0; i < nVerticesInChunk; i++) {
                //check x coordinate
                assertEquals(binVertices[3 * i], asciiVertices[3 * i], 
                        ERROR);
                                
                //check y coordinate
                assertEquals(binVertices[3 * i + 1], 
                        asciiVertices[3 * i + 1], ERROR);
                
                //check z coordinate
                assertEquals(binVertices[3 * i + 2], 
                        asciiVertices[3 * i + 2], ERROR);
                
                //check x normal
                assertEquals(binNormals[3 * i], asciiNormals[3 * i],
                        ERROR);
                
                //check y normal
                assertEquals(binNormals[3 * i + 1], asciiNormals[3 * i + 1],
                        ERROR);
                
                //check z normal
                assertEquals(binNormals[3 * i + 2], asciiNormals[3 * i + 2],
                        ERROR);                                
            }
            
            int nIndicesInChunk = binIndices.length;
            assertEquals(nIndicesInChunk, asciiIndices.length);
            
            for (int i = 0; i < nIndicesInChunk; i++) {
                assertTrue(binIndices[i] < nVerticesInChunk);
                assertTrue(asciiIndices[i] < nVerticesInChunk);
                
                assertEquals(binIndices[i], asciiIndices[i]);
            }
            
            //check bounding box values
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
    

    @Override
    public void onLoadStart(Loader loader) {
        if(startCounter != 0) startValid = false;
        startCounter++;
        
        testLocked((LoaderSTL)loader);
    }

    @Override
    public void onLoadEnd(Loader loader) {
        if(endCounter != 0) endValid = false;
        endCounter++;
        
        testLocked((LoaderSTL)loader);
    }

    @Override
    public void onLoadProgressChange(Loader loader, float progress) {
        if((progress < 0.0) || (progress > 1.0)) progressValid = false;
        if(progress < previousProgress) progressValid = false;
        previousProgress = progress;
        
        testLocked((LoaderSTL)loader);
    }
    
    private void testLocked(LoaderSTL loader) {
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
    }    
}
