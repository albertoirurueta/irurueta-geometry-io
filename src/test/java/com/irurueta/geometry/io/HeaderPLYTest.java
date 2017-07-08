/**
 * @file
 * This file contains Unit Tests for
 * com.irurueta.geometry.io.HeaderPLY
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HeaderPLYTest {
    
    public HeaderPLYTest() {
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
        HeaderPLY header = new HeaderPLY();
        
        assertEquals(header.getStorageMode(), PLYStorageMode.PLY_ASCII);
        assertTrue(header.getElements().isEmpty());
        assertTrue(header.getComments().isEmpty());
        assertTrue(header.getObjInfos().isEmpty());
    }
    
    @Test
    public void testToString(){
        HeaderPLY header = new HeaderPLY();
        
        String name = "name";
        long number = 2143245;
        
        PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        ElementPLY element = new ElementPLY(name, number, property);     
        header.getElements().add(element);

        assertEquals(header.toString(), "ply\nformat ascii 1.0\n" + 
                element.toString() + "end_header\n");
    }
}
