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

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ElementPLYTest {

    @Test
    public void testConstructors() throws NotAvailableException {
        final String name = "name";
        final long number = 2143245;
        
        // constructor without name
        ElementPLY element = new ElementPLY(null, number);
        
        try {
            element.getName();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) { }
        assertFalse(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertFalse(element.isValidElement());
        
        // constructor with name and number of instances
        element = new ElementPLY(name, number);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());
        
        // constructor with name, number of instances and property
        final PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        element = new ElementPLY(name, number, property);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertFalse(element.getProperties().isEmpty());
        assertTrue(element.getProperties().contains(property));
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());        
        
        // constructor with name, number of instances and property list
        final List<PropertyPLY> properties = new LinkedList<>();
        element = new ElementPLY(name, number, properties);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertEquals(element.getProperties(), properties);
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());                
    }
    
    @Test
    public void testToString() {
        final String name = "name";
        final long number = 2143245;
        
        final PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        final ElementPLY element = new ElementPLY(name, number, property);
        
        assertEquals(element.toString(), "element name " + number + "\n" +
                property);
    }
}
