/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.VertexOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 14, 2012
 */
package com.irurueta.geometry.io;

import com.irurueta.geometry.Point3D;
import com.irurueta.statistics.UniformRandomizer;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VertexOBJTest {
    
    public static final int MIN_INDEX = 0;
    public static final int MAX_INDEX = 1000;
    
    public VertexOBJTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testConstructor(){
        VertexOBJ vertex = new VertexOBJ();
        
        //test default values
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());
        assertEquals(vertex.getVertexIndex(), -1);
        assertFalse(vertex.isVertexIndexAvailable());
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isNormalIndexAvailable());
        assertEquals(vertex.getTextureIndex(), -1);
        assertFalse(vertex.isTextureIndexAvailable());
    }
    
    @Test
    public void testGetSetVertex(){
        VertexOBJ vertex = new VertexOBJ();
        
        Point3D point = Point3D.create();
        
        //check default value
        assertNull(vertex.getVertex());
        assertFalse(vertex.isVertexAvailable());
        
        //set vertex
        vertex.setVertex(point);
        //check correctness
        assertSame(vertex.getVertex(), point);
        assertTrue(vertex.isVertexAvailable());
    }
    
    @Test
    public void testGetSetVertexIndex(){
        VertexOBJ vertex = new VertexOBJ();
        
        //check default value
        assertEquals(vertex.getVertexIndex(), -1);
        assertFalse(vertex.isVertexIndexAvailable());
        
        //set new value
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int vertexIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);
        
        //set value
        vertex.setVertexIndex(vertexIndex);
        //check correctness
        assertEquals(vertex.getVertexIndex(), vertexIndex);
        assertTrue(vertex.isVertexIndexAvailable());
    }
    
    @Test
    public void testGetSetNormalIndex(){
        VertexOBJ vertex = new VertexOBJ();
        
        //check default value
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isNormalIndexAvailable());
        
        //set new value
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int normalIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);
        
        //set value
        vertex.setNormalIndex(normalIndex);
        //check correctness
        assertEquals(vertex.getNormalIndex(), normalIndex);
        assertTrue(vertex.isNormalIndexAvailable());
    }
    
    @Test
    public void testGetSetTextureIndex(){
        VertexOBJ vertex = new VertexOBJ();
        
        //check default value
        assertEquals(vertex.getNormalIndex(), -1);
        assertFalse(vertex.isTextureIndexAvailable());
        
        //set new value
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int textureIndex = randomizer.nextInt(MIN_INDEX, MAX_INDEX);
        
        //set value
        vertex.setTextureIndex(textureIndex);
        //check correctness
        assertEquals(vertex.getTextureIndex(), textureIndex);
        assertTrue(vertex.isTextureIndexAvailable());
    }
}
