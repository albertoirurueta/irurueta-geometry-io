/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.Texture
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 13, 2012
 */
package com.irurueta.geometry.io;

import com.irurueta.statistics.UniformRandomizer;
import java.io.File;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TextureTest {
    
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 100;
    
    public TextureTest() {
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
    public void testConstructorAndGetFileName(){
        
        String fileName = "fake.png";
        int id = 1;
        Texture tex = new Texture(fileName, id);
        
        //check correcntess
        assertNotNull(tex);
        assertEquals(tex.getFileName(), fileName);
        assertEquals(tex.getId(), id);
        assertTrue(tex.isFileNameAvailable());
        assertNotNull(tex.getFile());
        assertEquals(tex.getFile().getName(), fileName);
        assertTrue(tex.isFileAvailable());
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());
        assertFalse(tex.isValid());
        
        tex = new Texture(id);
        
        //check correctness
        assertTrue(tex.getFileName().isEmpty());
        assertEquals(tex.getId(), id);
        assertTrue(tex.isFileNameAvailable());
        assertNull(tex.getFile());
        assertFalse(tex.isFileAvailable());
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());
        assertFalse(tex.isValid());
        
    }
    
    @Test
    public void testGetSetFile(){
        String fileName = "fake.png";
        int id = 1;
        Texture tex = new Texture(fileName, id);
        
        //check correcntess
        assertNotNull(tex);
        assertEquals(tex.getFileName(), fileName);
        assertEquals(tex.getId(), id);
        assertNotNull(tex.getFile());
        assertEquals(tex.getFile().getName(), fileName);
        assertTrue(tex.isFileAvailable());
        
        File f1 = tex.getFile();
        
        String fileName2 = "fake2.png";
        File f2 = new File(fileName2);
        
        tex.setFile(f2);
        assertNotSame(tex.getFile(), f1);
        assertSame(tex.getFile(), f2);
        assertEquals(tex.getFile().getName(), fileName2);
        assertTrue(tex.isFileAvailable());
        
        tex.setFile(null);
        assertNull(tex.getFile());
        assertFalse(tex.isFileAvailable());
    }
    
    @Test
    public void testGetSetWidth(){
        int id = 1;
        Texture tex = new Texture("fake.png", id);
        
        //check default value
        assertEquals(tex.getWidth(), -1);
        assertFalse(tex.isWidthAvailable());
        
        //set new value
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int width = randomizer.nextInt(MIN_SIZE, MAX_SIZE);
        
        tex.setWidth(width);
        //check correctness
        assertEquals(tex.getWidth(), width);
        assertTrue(tex.isWidthAvailable());
        assertEquals(tex.getId(), id);
    }
    
    @Test
    public void testGetSetHeight(){
        int id = 1;
        Texture tex = new Texture("fake.png", id);
        
        //check default value
        assertEquals(tex.getHeight(), -1);
        assertFalse(tex.isHeightAvailable());
        
        //set new value
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int height = randomizer.nextInt(MIN_SIZE, MAX_SIZE);
        
        tex.setHeight(height);
        //check correctness
        assertEquals(tex.getHeight(), height);
        assertTrue(tex.isHeightAvailable());
        assertEquals(tex.getId(), id);
    }
    
    @Test
    public void testIsSetValid(){
        int id = 1;
        Texture tex = new Texture("fake.png", id);
        
        //check default value
        assertFalse(tex.isValid());
        
        //set valid
        tex.setValid(true);
        //check correctness
        assertTrue(tex.isValid());
        
        //unset valid
        tex.setValid(false);
        //check correctness
        assertFalse(tex.isValid());
        assertEquals(tex.getId(), id);
    }
}
