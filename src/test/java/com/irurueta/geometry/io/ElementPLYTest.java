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

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ElementPLYTest {

    @Test
    void testConstructors() throws NotAvailableException {
        final var name = "name";
        final var number = 2143245L;
        
        // constructor without name
        var element = new ElementPLY(null, number);

        assertThrows(NotAvailableException.class, element::getName);
        assertFalse(element.isNameAvailable());
        assertEquals(number, element.getNumberOfInstances());
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertFalse(element.isValidElement());
        
        // constructor with name and number of instances
        element = new ElementPLY(name, number);
        assertEquals(name, element.getName());
        assertTrue(element.isNameAvailable());
        assertEquals(number, element.getNumberOfInstances());
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());
        
        // constructor with name, number of instances and property
        final var property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        element = new ElementPLY(name, number, property);
        assertEquals(name, element.getName());
        assertTrue(element.isNameAvailable());
        assertEquals(number, element.getNumberOfInstances());
        assertFalse(element.getProperties().isEmpty());
        assertTrue(element.getProperties().contains(property));
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());        
        
        // constructor with name, number of instances and property list
        final var properties = new LinkedList<PropertyPLY>();
        element = new ElementPLY(name, number, properties);
        assertEquals(name, element.getName());
        assertTrue(element.isNameAvailable());
        assertEquals(number, element.getNumberOfInstances());
        assertEquals(properties, element.getProperties());
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());                
    }
    
    @Test
    void testToString() {
        final var name = "name";
        final var number = 2143245L;
        
        final var property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        final var element = new ElementPLY(name, number, property);
        
        assertEquals(element.toString(), "element name " + number + "\n" + property);
    }
}
