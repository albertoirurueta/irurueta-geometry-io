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

import com.irurueta.geometry.Point3D;
import com.irurueta.statistics.UniformRandomizer;
import org.junit.*;

import java.util.Random;

import static org.junit.Assert.*;

public class VertexOBJTest {
    
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 1000;
    
    public VertexOBJTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testConstructor() {
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
    public void testGetSetVertex() {
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
    public void testGetSetVertexIndex() {
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
    public void testGetSetNormalIndex() {
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
    public void testGetSetTextureIndex() {
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
