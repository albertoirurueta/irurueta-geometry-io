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

import static org.junit.jupiter.api.Assertions.*;

class PropertyPLYTest {

    @Test
    void testConstructors() throws NotAvailableException {
        final var name = "property name";

        // set property without name
        var property = new PropertyPLY(null, DataTypePLY.PLY_CHAR);
        assertThrows(NotAvailableException.class, property::getName);
        assertFalse(property.isNameAvailable());
        assertEquals(PropertyTypePLY.PROPERTY_PLY_SCALAR, property.getPropertyType());
        assertThrows(NotAvailableException.class, property::getLengthType);
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(DataTypePLY.PLY_CHAR, property.getValueType());
        assertTrue(property.isValueTypeAvailable());
        assertFalse(property.isValidProperty());

        assertThrows(NotAvailableException.class, property::getReadValueFromBufferListener);
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadValueFromStreamListener);
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromBufferListener);
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromStreamListener);
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // set property with name and value type
        property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        assertEquals(name, property.getName());
        assertTrue(property.isNameAvailable());
        assertEquals(PropertyTypePLY.PROPERTY_PLY_SCALAR, property.getPropertyType());
        assertThrows(NotAvailableException.class, property::getLengthType);
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(DataTypePLY.PLY_FLOAT32, property.getValueType());
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty());

        assertThrows(NotAvailableException.class, property::getReadValueFromBufferListener);
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadValueFromStreamListener);
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromBufferListener);
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromStreamListener);
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // property with name, length type and value type
        property = new PropertyPLY(name, DataTypePLY.PLY_UCHAR, DataTypePLY.PLY_UINT);
        assertEquals(name, property.getName());
        assertTrue(property.isNameAvailable());
        assertEquals(PropertyTypePLY.PROPERTY_PLY_LIST, property.getPropertyType());
        assertEquals(DataTypePLY.PLY_UCHAR, property.getLengthType());
        assertTrue(property.isLengthTypeAvailable());
        assertEquals(DataTypePLY.PLY_UINT, property.getValueType());
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty());

        assertThrows(NotAvailableException.class, property::getReadValueFromBufferListener);
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadValueFromStreamListener);
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromBufferListener);
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        assertThrows(NotAvailableException.class, property::getReadLengthValueFromStreamListener);
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());
    }

    @Test
    void testToString() {
        var name = "something";
        var property = new PropertyPLY(name, DataTypePLY.PLY_UCHAR, DataTypePLY.PLY_UINT);
        assertEquals("property list uchar uint something\n", property.toString());

        name = "x";
        property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        assertEquals("property float32 x\n", property.toString());

    }

    @Test
    void testGetSetReadValueFromBufferListener() throws NotAvailableException {

        final var property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        assertThrows(NotAvailableException.class, property::getReadValueFromBufferListener);
        assertFalse(property.isReadValueFromBufferListenerAvailable());

        // set listener
        final PLYReadValueFromBufferListener listener = buffer -> {};

        property.setReadValueFromBufferListener(listener);
        assertEquals(listener, property.getReadValueFromBufferListener());
        assertTrue(property.isReadValueFromBufferListenerAvailable());
    }

    @Test
    void testGetSetReadValueFromStreamListener() throws NotAvailableException {

        final var property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        assertThrows(NotAvailableException.class, property::getReadValueFromStreamListener);
        assertFalse(property.isReadValueFromStreamListenerAvailable());

        // set listener
        final PLYReadValueFromStreamListener listener = buffer -> {};

        property.setReadValueFromStreamListener(listener);
        assertEquals(listener, property.getReadValueFromStreamListener());
        assertTrue(property.isReadValueFromStreamListenerAvailable());
    }

    @Test
    void testGetSetReadLengthValueFromBufferListener() throws NotAvailableException {

        final var property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        assertThrows(NotAvailableException.class, property::getReadLengthValueFromBufferListener);
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());

        // set listener
        final PLYReadValueFromBufferListener listener = buffer -> {};

        property.setReadLengthValueFromBufferListener(listener);
        assertEquals(listener, property.getReadLengthValueFromBufferListener());
        assertTrue(property.isReadLengthValueFromBufferListenerAvailable());
    }

    @Test
    void testGetSetReadLengthValueFromStreamListener() throws NotAvailableException {

        final var property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        assertThrows(NotAvailableException.class, property::getReadLengthValueFromStreamListener);
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // set listener
        final PLYReadValueFromStreamListener listener = buffer -> {};

        property.setReadLengthValueFromStreamListener(listener);
        assertEquals(listener, property.getReadLengthValueFromStreamListener());
        assertTrue(property.isReadLengthValueFromStreamListenerAvailable());
    }

    @Test
    void testGetValueType() throws NotAvailableException {
        // test when value type available
        var property = new PropertyPLY("name", DataTypePLY.PLY_FLOAT);

        assertTrue(property.isValueTypeAvailable());
        assertEquals(DataTypePLY.PLY_FLOAT, property.getValueType());

        // test when value type not available
        property = new PropertyPLY("name", null);

        assertFalse(property.isValueTypeAvailable());
        assertThrows(NotAvailableException.class, property::getValueType);
    }
}
