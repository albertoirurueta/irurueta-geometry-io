/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 14, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoaderOBJTest implements LoaderListenerOBJ{
    
    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;
    
    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;
    
    
    public static double ERROR = 1e-4;
    
    public LoaderOBJTest() { }
    
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
        LoaderException{
        
        //test constants are equal to LoaderPLY
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
        
        //test empty constructor
        LoaderOBJ loader = new LoaderOBJ();
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() ==
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");            
        }catch(NotReadyException e){}
        
        
        
        //test constructor with maxVerticesInChunk
        int maxVerticesInChunk = 21423;
        loader = new LoaderOBJ(maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() == 
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());        
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){} 
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with maxVerticesInChunk and 
        //allowDuplicateVerticesInChunk
        boolean allowDuplicateVerticesInChunk = true;
        loader = new LoaderOBJ(maxVerticesInChunk, 
                allowDuplicateVerticesInChunk);
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
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        
        //Force IllegalArgumentException
        loader = null;
        try{
           loader = new LoaderOBJ(0, allowDuplicateVerticesInChunk);
           fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with maxVerticesInChunk, 
        //allowDuplicateVerticesInChunk and maxStreamPositions
        long maxStreamPositions = 500;
        loader = new LoaderOBJ(maxVerticesInChunk, 
                allowDuplicateVerticesInChunk, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener()); 
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(0, allowDuplicateVerticesInChunk, 
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        try{
            loader = new LoaderOBJ(maxVerticesInChunk, 
                    allowDuplicateVerticesInChunk, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //constructor with file
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        loader = new LoaderOBJ(f);
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() == 
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener()); 
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);       
        loader.close(); //to free file resources
                        
        //Force IOException
        File badF = new File("./non-existing");
        assertFalse(badF.exists());
        
        loader = null;
        try{
            loader = new LoaderOBJ(badF);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        
        //test constructor with file and maxVerticesInChunk        
        loader = new LoaderOBJ(f, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() == 
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);    
        loader.close(); //to free file resources

        //Force IOException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(f, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with file, maxVerticesInChunk and 
        //allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, maxVerticesInChunk, 
                allowDuplicateVerticesInChunk);
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
        loader.close(); //to free file resources
        
        //Force FileNotFoundException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, maxVerticesInChunk, 
                    allowDuplicateVerticesInChunk);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        try{
            loader = new LoaderOBJ(f, 0, allowDuplicateVerticesInChunk);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with file, maxVerticesInChunk, 
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, maxVerticesInChunk,
                allowDuplicateVerticesInChunk, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());  
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);        
        loader.close(); //to free file resources
        
        //Force FileNotFoundException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk, maxStreamPositions);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        try{
            loader = new LoaderOBJ(f, 0, allowDuplicateVerticesInChunk,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        try{
            loader = new LoaderOBJ(f, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //Test constructor with loader listener
        LoaderListener listener = new LoaderListener(){
            @Override
            public void onLoadStart(Loader loader){}

            @Override
            public void onLoadEnd(Loader loader){}

            @Override
            public void onLoadProgressChange(Loader loader, float progress){}
        };
        
        loader = new LoaderOBJ(listener);
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() == 
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}    
        
        
        //test constructor with loader listener and maxVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() == 
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener); 
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){} 
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with loader listener, maxVerticesInChunk and
        //allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(listener, maxVerticesInChunk, 
                allowDuplicateVerticesInChunk);
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
        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(listener, 0, allowDuplicateVerticesInChunk);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with loader listener, maxVerticesInChunk,
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(listener, maxVerticesInChunk,
                allowDuplicateVerticesInChunk, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);
        
        try{
            loader.isValidFile();
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        
        //Force IllegalArgumentException
        loader = null;
        try{
            loader = new LoaderOBJ(listener, 0, allowDuplicateVerticesInChunk,
                    maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        try{
            loader = new LoaderOBJ(listener, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with file and listener
        loader = new LoaderOBJ(f, listener);
        assertEquals(loader.getMaxVerticesInChunk(),
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() ==
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);   
        loader.close(); //to free file resources
        
        //Force IOException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, listener);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        
        //test constructor with file, listener and maxVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed() ==
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        assertEquals(loader.getMaxStreamPositions(),
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);  
        loader.close(); //to free file resources
        
        //Force IOException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        try{
            loader = new LoaderOBJ(f, listener, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with file, listener, maxVerticesInChunk
        //and allowDuplicateVerticesInChunk
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                allowDuplicateVerticesInChunk);
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
        loader.close(); //to free file resources
        
        //Force FileNotFoundException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        try{
            loader = new LoaderOBJ(f, listener, 0, 
                    allowDuplicateVerticesInChunk);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        
        
        //test constructor with file, listener, maxVerticesInChunk,
        //allowDuplicateVerticesInChunk and maxStreamPositions
        loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                allowDuplicateVerticesInChunk, maxStreamPositions);
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        assertTrue(loader.areDuplicateVerticesInChunkAllowed());
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_OBJ);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        assertEquals(loader.isContinueIfTriangulationError(), 
                LoaderOBJ.DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR);    
        loader.close(); //to free file resources
        
        //Force FileNotFoundException
        loader = null;
        try{
            loader = new LoaderOBJ(badF, listener, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk, maxStreamPositions);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        //Force IllegalArgumentException
        try{
            loader = new LoaderOBJ(f, listener, 0,
                    allowDuplicateVerticesInChunk, maxStreamPositions);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);
        try{
            loader = new LoaderOBJ(f, listener, maxVerticesInChunk,
                    allowDuplicateVerticesInChunk, 0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
        assertNull(loader);                
    }
    
    @Test
    public void testHasSetFileAndIsReady() 
            throws LockedException, LoaderException, IOException{
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderOBJ loader = new LoaderOBJ();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());
        
        //set file
        loader.setFile(f);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
    }    
    
    
    @Test
    public void testGetSetListener() throws LockedException{
        LoaderOBJ loader = new LoaderOBJ();
        assertNull(loader.getListener());
        
        LoaderListener listener = new LoaderListener(){
            @Override
            public void onLoadStart(Loader loader){}

            @Override
            public void onLoadEnd(Loader loader){}

            @Override
            public void onLoadProgressChange(Loader loader, float progress){}            
        };
        
        loader.setListener(listener);
        assertEquals(loader.getListener(), listener);
    }

    @Test
    public void testGetSetMaxVerticesInChunk() throws LockedException{
        LoaderOBJ loader = new LoaderOBJ();
        int maxVerticesInChunk = 521351;
        
        assertEquals(loader.getMaxVerticesInChunk(), 
                LoaderOBJ.DEFAULT_MAX_VERTICES_IN_CHUNK);
        
        //set new value
        loader.setMaxVerticesInChunk(maxVerticesInChunk);
        //check correctness
        assertEquals(loader.getMaxVerticesInChunk(), maxVerticesInChunk);
        
        //Force IllegalArgumentException
        try{
            loader.setMaxVerticesInChunk(0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
    } 

    @Test
    public void testGetSetAllowDuplicateVerticesInChunk() throws LockedException{
        LoaderOBJ loader = new LoaderOBJ();
        boolean allow = true;
        
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(),
                LoaderOBJ.DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK);
        
        //set new value
        loader.setAllowDuplicateVerticesInChunk(allow);
        //check correctness
        assertEquals(loader.areDuplicateVerticesInChunkAllowed(), allow);
    }    
    
    @Test
    public void testGetSetMaxStreamPositions() throws LockedException{
        long maxStreamPositions = 400;
        LoaderOBJ loader = new LoaderOBJ();
        
        assertEquals(loader.getMaxStreamPositions(), 
                LoaderOBJ.DEFAULT_MAX_STREAM_POSITIONS);
        
        //set new value
        loader.setMaxStreamPositions(maxStreamPositions);
        //check correctness
        assertEquals(loader.getMaxStreamPositions(), maxStreamPositions);
        
        //Force IllegalArgumentException
        try{
            loader.setMaxStreamPositions(0);
            fail("IllegalArgumentException expected but not thrown");
        }catch(IllegalArgumentException e){}
    }    
    
    
    @Test
    public void testIsValidFile() 
            throws IOException, LockedException, IOException{
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());

        LoaderOBJ loader = new LoaderOBJ(f);
        assertTrue(loader.isValidFile());        
        loader.close(); //to free file resources
    }
    
    @Test
    public void testLoad() 
            throws IOException, LockedException, NotReadyException, 
            IOException, LoaderException{
        File f = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        assertTrue(f.exists()); //check that file exists
        assertTrue(f.isFile());
        
        LoaderOBJ loader = new LoaderOBJ(f);
        //loader.setListener(this);
        LoaderIterator it = loader.load();
                
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        loader.close(); //to free file resources
    }    
   
    @Test
    public void testLoadAndIterate() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/books.obj");
        File filePly = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        
        LoaderPLY plyLoader = new LoaderPLY(filePly, 
                maxNumberOfVerticesInChunk, false); 
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator plyIt = plyLoader.load();        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext() && plyIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk plyChunk = plyIt.next();                                    
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
            
            float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            float[] plyTexture = plyChunk.getTextureCoordiantesData();
            short[] plyColors = plyChunk.getColorData();
            float[] plyNormals = plyChunk.getNormalsData();
            int[] plyIndices = plyChunk.getIndicesData();
            
            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);
                        
            int nVerticesInChunk = objVertices.length / 3;
                        
            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);
            
            for(int i = 0; i < nVerticesInChunk; i++){
                //check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i], 
                        ERROR);
                                
                //check y coordinate
                assertEquals(objVertices[3 * i + 1], 
                        plyVertices[3 * i + 1], ERROR);
                
                //check z coordinate
                assertEquals(objVertices[3 * i + 2], 
                        plyVertices[3 * i + 2], ERROR);
                
                //check x normal
                assertEquals(objNormals[3 * i], plyNormals[3 * i],
                        ERROR);
                
                //check y normal
                assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1],
                        ERROR);
                
                //check z normal
                assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2],
                        ERROR);                                
            }
            
            int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
                assertTrue(plyIndices[i] < nVerticesInChunk);
                
                assertEquals(objIndices[i], plyIndices[i]);
            }
            
            //check bounding box values
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
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
        if(plyIt.hasNext()) 
            fail("Wrong number of chunks in PLY");
    }       
    
    @Test
    public void testLoadAndIterateMacbook() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 100000; //500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook.obj");
        File filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");
        
        LoaderPLY plyLoader = new LoaderPLY(filePly, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator plyIt = plyLoader.load();        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext() && plyIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk plyChunk = plyIt.next();                                    
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
            
            float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            float[] plyTexture = plyChunk.getTextureCoordiantesData();
            short[] plyColors = plyChunk.getColorData();
            float[] plyNormals = plyChunk.getNormalsData();
            int[] plyIndices = plyChunk.getIndicesData();
            
            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);
                        
            int nVerticesInChunk = objVertices.length / 3;
                        
            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);
            
            for(int i = 0; i < nVerticesInChunk; i++){
                //check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i], 
                        ERROR);
                                
                //check y coordinate
                assertEquals(objVertices[3 * i + 1], 
                        plyVertices[3 * i + 1], ERROR);
                
                //check z coordinate
                assertEquals(objVertices[3 * i + 2], 
                        plyVertices[3 * i + 2], ERROR);
                
            }
            
            int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
                assertTrue(plyIndices[i] < nVerticesInChunk);
                
                assertEquals(objIndices[i], plyIndices[i]);
            }
            
            //check bounding box values
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
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
        if(plyIt.hasNext()) 
            fail("Wrong number of chunks in PLY");
    }        
    
    @Test
    public void testLoadAndIterateMacbook2() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 100000; //500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/macbook2.obj");
        File filePly = new File("./src/test/java/com/irurueta/geometry/io/macbook.ply");
        
        LoaderPLY plyLoader = new LoaderPLY(filePly, 
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, false); //disable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator plyIt = plyLoader.load();        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext() && plyIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk plyChunk = plyIt.next();                                    
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
            
            float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            float[] plyTexture = plyChunk.getTextureCoordiantesData();
            short[] plyColors = plyChunk.getColorData();
            float[] plyNormals = plyChunk.getNormalsData();
            int[] plyIndices = plyChunk.getIndicesData();
            
            assertEquals(objVertices.length, objNormals.length);
            assertNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);
                        
            int nVerticesInChunk = objVertices.length / 3;
                        
            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);
            
            for(int i = 0; i < nVerticesInChunk; i++){
                //check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i], 
                        ERROR);
                                
                //check y coordinate
                assertEquals(objVertices[3 * i + 1], 
                        plyVertices[3 * i + 1], ERROR);
                
                //check z coordinate
                assertEquals(objVertices[3 * i + 2], 
                        plyVertices[3 * i + 2], ERROR);
                
                //check x normal
                assertEquals(objNormals[3 * i], plyNormals[3 * i],
                        ERROR);
                
                //check y normal
                assertEquals(objNormals[3 * i + 1], plyNormals[3 * i + 1],
                        ERROR);
                
                //check z normal
                assertEquals(objNormals[3 * i + 2], plyNormals[3 * i + 2],
                        ERROR);                                
            }
            
            int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
                assertTrue(plyIndices[i] < nVerticesInChunk);
                
                assertEquals(objIndices[i], plyIndices[i]);
            }
            
            //check bounding box values
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
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
        if(plyIt.hasNext()) 
            fail("Wrong number of chunks in PLY");
    }        
        
    
    @Test
    public void testLoadAndIteratePitcher() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 100000; //500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/pitcher.obj");
        File filePly = new File("./src/test/java/com/irurueta/geometry/io/pitcher.ply");
        
        LoaderPLY plyLoader = new LoaderPLY(filePly, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator plyIt = plyLoader.load();        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext() && plyIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            DataChunk plyChunk = plyIt.next();                                    
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
            
            float[] plyVertices = plyChunk.getVerticesCoordinatesData();
            float[] plyTexture = plyChunk.getTextureCoordiantesData();
            short[] plyColors = plyChunk.getColorData();
            float[] plyNormals = plyChunk.getNormalsData();
            int[] plyIndices = plyChunk.getIndicesData();
            
            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);
            assertEquals(plyVertices.length, plyNormals.length);
            assertNull(plyTexture);
            assertNotNull(plyColors);
                        
            int nVerticesInChunk = objVertices.length / 3;
                        
            assertEquals(nVerticesInChunk, objVertices.length / 3);
            assertEquals(nVerticesInChunk, plyVertices.length / 3);
            
            for(int i = 0; i < nVerticesInChunk; i++){
                //check x coordinate
                assertEquals(objVertices[3 * i], plyVertices[3 * i], 
                        ERROR);
                                
                //check y coordinate
                assertEquals(objVertices[3 * i + 1], 
                        plyVertices[3 * i + 1], ERROR);
                
                //check z coordinate
                assertEquals(objVertices[3 * i + 2], 
                        plyVertices[3 * i + 2], ERROR);
                
            }
            
            int nIndicesInChunk = objIndices.length;
            assertEquals(nIndicesInChunk, plyIndices.length);
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
                assertTrue(plyIndices[i] < nVerticesInChunk);
                
                assertEquals(objIndices[i], plyIndices[i]);
            }
            
            //check bounding box values
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
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
        if(plyIt.hasNext()) 
            fail("Wrong number of chunks in PLY");
    }  
    
    @Test
    public void testLoadAndIterateCamera() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 100000; //500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/camera.obj");
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
                        
            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);
                        
            int nVerticesInChunk = objVertices.length / 3;                        
            int nIndicesInChunk = objIndices.length;
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
            }
            
            //check bounding box values            
            assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
            assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
            assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());            
        }
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
    }        
    
    @Test
    public void testLoadAndIteratePotro() 
            throws IllegalArgumentException, IOException, LockedException, 
            NotReadyException, IOException, LoaderException, 
            NotAvailableException{
        
        int maxNumberOfVerticesInChunk = 100000; //500;        
        File fileObj = new File("./src/test/java/com/irurueta/geometry/io/potro.obj");
        LoaderOBJ objLoader = new LoaderOBJ(fileObj, 
                maxNumberOfVerticesInChunk, true); //enable vertex duplicates
        objLoader.setListener(this);        
        
        
        LoaderIterator objIt = objLoader.load();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();                
        
        //check correctness of chunks
        while(objIt.hasNext()){
            DataChunk objChunk = objIt.next();  
            
            assertTrue(isEndValid());
            assertTrue(isLockedValid());
            assertTrue(isProgressValid());
            assertTrue(isStartValid());
            resetListener();
            
            float[] objVertices = objChunk.getVerticesCoordinatesData();
            float[] objTexture = objChunk.getTextureCoordiantesData();
            short[] objColors = objChunk.getColorData();
            float[] objNormals = objChunk.getNormalsData();
            int[] objIndices = objChunk.getIndicesData();
                        
            assertEquals(objVertices.length, objNormals.length);
            assertNotNull(objTexture);
            assertNull(objColors);
                        
            int nVerticesInChunk = objVertices.length / 3;                        
            int nIndicesInChunk = objIndices.length;
            
            for(int i = 0; i < nIndicesInChunk; i++){
                assertTrue(objIndices[i] < nVerticesInChunk);
            }
            
            //check bounding box values            
            assertTrue(objChunk.getMinX() <= objChunk.getMaxX());
            assertTrue(objChunk.getMinY() <= objChunk.getMaxY());
            assertTrue(objChunk.getMinZ() <= objChunk.getMaxZ());            
        }
        
        if(objIt.hasNext()) fail("Wrong number of chunks in OBJ");
    }            
        
    @Override
    public void onLoadStart(Loader loader){
        if(startCounter != 0) startValid = false;
        startCounter++;
        
        testLocked((LoaderOBJ)loader);
    }

    @Override
    public void onLoadEnd(Loader loader){
        if(endCounter != 0) endValid = false;
        endCounter++;
        
        testLocked((LoaderOBJ)loader);
    }

    @Override
    public void onLoadProgressChange(Loader loader, float progress) {
        if((progress < 0.0) || (progress > 1.0)) progressValid = false;
        if(progress < previousProgress) progressValid = false;
        previousProgress = progress;
        
        testLocked((LoaderOBJ)loader);
    }
    
    @Override
    public MaterialLoaderOBJ onMaterialLoaderRequested(LoaderOBJ loader, String path) {
        File origF = new File(path);
        String folder = "./src/test/java/com/irurueta/geometry/io/";
        File f = new File(folder, origF.getName());
        boolean hasToFail = false;
        if(path.contains("pitcher") || path.contains("Camara fotos")){
            //we don't have mtl file for pitcher or camera
            hasToFail = true;
        }
        try{
            MaterialLoaderOBJ materialLoader = new MaterialLoaderOBJ(f);
            if(hasToFail){
                fail("IOException was expected but not thrown");
            }
            return materialLoader;
        }catch(IOException e){
            if(!hasToFail){
                fail("No IOException exception was expected");
            }
            return null;
        }
    }
    
    
    private void testLocked(LoaderOBJ loader){
        if(!loader.isLocked()) lockedValid = false;
        
        try{
            loader.setListener(null);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            loader.load();
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            loader.setMaxVerticesInChunk(1);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            loader.setAllowDuplicateVerticesInChunk(true);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            loader.setMaxStreamPositions(1);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
    }
    
    private void resetListener(){
        startValid = endValid = progressValid = lockedValid = true;
        startCounter = endCounter = 0;
        previousProgress = 0.0f;
    }
    
    private boolean isStartValid(){
        return startValid;
    }
    
    private boolean isEndValid(){
        return endValid;
    }
    
    private boolean isProgressValid(){
        return progressValid;
    }
    
    private boolean isLockedValid(){
        return lockedValid;
    }    
}
