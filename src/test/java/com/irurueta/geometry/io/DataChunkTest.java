/**
 * @file
 * This file contains Unit Tests for
 * com.irurueta.geometry.io.DataChunk
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

import org.junit.*;
import static org.junit.Assert.*;

public class DataChunkTest {
    
    public DataChunkTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testConstructor(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getVerticesCoordinatesData());
        assertNull(chunk.getColorData());
        assertNull(chunk.getIndicesData());
        assertNull(chunk.getTextureCoordiantesData());
        assertNull(chunk.getNormalsData());
        
        assertEquals(chunk.getColorComponents(), 
                DataChunk.DEFAULT_COLOR_COMPONENTS);
        
        assertEquals(chunk.getMinX(), Float.MAX_VALUE, 0.0);
        assertEquals(chunk.getMinY(), Float.MAX_VALUE, 0.0);
        assertEquals(chunk.getMinZ(), Float.MAX_VALUE, 0.0);
        
        assertEquals(chunk.getMaxX(), -Float.MAX_VALUE, 0.0);
        assertEquals(chunk.getMaxY(), -Float.MAX_VALUE, 0.0);
        assertEquals(chunk.getMaxZ(), -Float.MAX_VALUE, 0.0);
    }
    
    @Test
    public void testGetSetVerticesCoordinatesData(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getVerticesCoordinatesData());
        
        //set data
        float[] data = new float[1024];
        chunk.setVerticesCoordinatesData(data);
        
        //check correctness
        assertEquals(chunk.getVerticesCoordinatesData(), data);
    }
    
    @Test
    public void testGetSetColorData(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getColorData());
        
        //set data
        short[] data = new short[1024];
        chunk.setColorData(data);
        
        //check correctness
        assertEquals(chunk.getColorData(), data);
    }
    
    @Test
    public void testGetSetIndicesData(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getIndicesData());
        
        //set data
        int[] data = new int[1024];
        chunk.setIndicesData(data);
        
        //check correctness
        assertEquals(chunk.getIndicesData(), data);
    }
    
    @Test
    public void testGetSetTextureCoordinatesData(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getTextureCoordiantesData());
        
        //set data
        float[] data = new float[1024];
        chunk.setTextureCoordinatesData(data);
        
        //check correctness
        assertEquals(chunk.getTextureCoordiantesData(), data);
    }
    
    @Test
    public void testGetSetNormalsData(){
        DataChunk chunk = new DataChunk();
        
        assertNull(chunk.getNormalsData());
        
        //set data
        float[] data = new float[1024];
        chunk.setNormalsData(data);
        
        //check correctness
        assertEquals(chunk.getNormalsData(), data);
    }
    
    @Test
    public void testGetSetColorComponents(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getColorComponents(), 
                DataChunk.DEFAULT_COLOR_COMPONENTS);
        
        int components = 4;
        
        //set new value
        chunk.setColorComponents(components);
        //check correctness
        assertEquals(chunk.getColorComponents(), components);
        
        //Force IllegalArgumentException
        try{
            chunk.setColorComponents(0);
            fail("IllegalArgumentException not thrown");
        }catch(IllegalArgumentException e){}
    }
    
    @Test
    public void testGetSetMinX(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMinX(), Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 54213.23f;
        chunk.setMinX(value);
        
        //check correctness
        assertEquals(chunk.getMinX(), value, 0.0);
    }
    
    @Test
    public void testGetSetMinY(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMinY(), Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 533.423f;
        chunk.setMinY(value);
        
        //check correctness
        assertEquals(chunk.getMinY(), value, 0.0);
    }
    
    @Test
    public void testGetSetMinZ(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMinZ(), Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 21542314.2523f;
        chunk.setMinZ(value);
        
        //check correctness
        assertEquals(chunk.getMinZ(), value, 0.0);
    }
    
    @Test
    public void testGetSetMaxX(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMaxX(), -Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 3255.52f;
        chunk.setMaxX(value);
        
        //check correctness
        assertEquals(chunk.getMaxX(), value, 0.0);
    }
    
    @Test
    public void testGetSetMaxY(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMaxX(), -Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 43215324.32231f;
        chunk.setMaxY(value);
        
        //check correctness
        assertEquals(chunk.getMaxY(), value, 0.0);
    }
    
    @Test
    public void testGetSetMaxZ(){
        DataChunk chunk = new DataChunk();
        
        assertEquals(chunk.getMaxZ(), -Float.MAX_VALUE, 0.0);
        
        //set value
        float value = 4514235.3245f;
        chunk.setMaxZ(value);
        
        //check correctness
        assertEquals(chunk.getMaxZ(), value, 0.0);
    }  
    
    @Test
    public void testGetSetMaterial(){
        DataChunk chunk = new DataChunk();
        
        //check default values
        assertNull(chunk.getMaterial());
        assertFalse(chunk.isMaterialAvailable());
        
        Material material = new Material();
        
        //set new material
        chunk.setMaterial(material);
        //check correctness
        assertSame(chunk.getMaterial(), material);
        assertTrue(chunk.isMaterialAvailable());
    }
}
