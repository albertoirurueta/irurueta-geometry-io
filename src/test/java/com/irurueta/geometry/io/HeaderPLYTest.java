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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HeaderPLYTest {
    
    public HeaderPLYTest() { }

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
        HeaderPLY header = new HeaderPLY();
        
        assertEquals(header.getStorageMode(), PLYStorageMode.PLY_ASCII);
        assertTrue(header.getElements().isEmpty());
        assertTrue(header.getComments().isEmpty());
        assertTrue(header.getObjInfos().isEmpty());
    }
    
    @Test
    public void testToString() {
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
