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

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class PropertyPLYTest {

    @Test
    public void testConstructors() throws NotAvailableException {
        final String name = "property name";

        // set property without name
        PropertyPLY property = new PropertyPLY(null,
                DataTypePLY.PLY_CHAR);
        try {
            property.getName();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isNameAvailable());
        assertEquals(property.getPropertyType(),
                PropertyTypePLY.PROPERTY_PLY_SCALAR);
        try {
            property.getLengthType();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_CHAR);
        assertTrue(property.isValueTypeAvailable());
        assertFalse(property.isValidProperty());

        try {
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try {
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try {
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try {
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // set property with name and value type
        property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        assertEquals(property.getName(), name);
        assertTrue(property.isNameAvailable());
        assertEquals(property.getPropertyType(),
                PropertyTypePLY.PROPERTY_PLY_SCALAR);
        try {
            property.getLengthType();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_FLOAT32);
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty());

        try {
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try {
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try {
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try {
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // property with name, length type and value type
        property = new PropertyPLY(name, DataTypePLY.PLY_UCHAR,
                DataTypePLY.PLY_UINT);
        assertEquals(property.getName(), name);
        assertTrue(property.isNameAvailable());
        assertEquals(property.getPropertyType(),
                PropertyTypePLY.PROPERTY_PLY_LIST);
        assertEquals(property.getLengthType(), DataTypePLY.PLY_UCHAR);
        assertTrue(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_UINT);
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty());

        try {
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try {
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try {
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try {
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());
    }

    @Test
    public void testToString() {
        String name = "something";
        PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_UCHAR,
                DataTypePLY.PLY_UINT);
        assertEquals(property.toString(), "property list uchar uint something\n");

        name = "x";
        property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        assertEquals(property.toString(), "property float32 x\n");

    }

    @Test
    public void testGetSetReadValueFromBufferListener()
            throws NotAvailableException {

        final PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        try {
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromBufferListenerAvailable());

        // set listener
        final PLYReadValueFromBufferListener listener =
                new PLYReadValueFromBufferListener() {
                    @Override
                    public void readValueFromBuffer(final ByteBuffer buffer) {
                    }
                };

        property.setReadValueFromBufferListener(listener);
        assertEquals(property.getReadValueFromBufferListener(), listener);
        assertTrue(property.isReadValueFromBufferListenerAvailable());
    }

    @Test
    public void testGetSetReadValueFromStreamListener()
            throws NotAvailableException {

        final PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        try {
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadValueFromStreamListenerAvailable());

        // set listener
        final PLYReadValueFromStreamListener listener =
                new PLYReadValueFromStreamListener() {
                    @Override
                    public void readFromStream(final ByteBuffer buffer) {
                    }
                };

        property.setReadValueFromStreamListener(listener);
        assertEquals(property.getReadValueFromStreamListener(), listener);
        assertTrue(property.isReadValueFromStreamListenerAvailable());
    }

    @Test
    public void testGetSetReadLengthValueFromBufferListener()
            throws NotAvailableException {

        final PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        try {
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());

        // set listener
        final PLYReadValueFromBufferListener listener =
                new PLYReadValueFromBufferListener() {
                    @Override
                    public void readValueFromBuffer(final ByteBuffer buffer) {
                    }
                };

        property.setReadLengthValueFromBufferListener(listener);
        assertEquals(property.getReadLengthValueFromBufferListener(), listener);
        assertTrue(property.isReadLengthValueFromBufferListenerAvailable());
    }

    @Test
    public void testGetSetReadLengthValueFromStreamListener()
            throws NotAvailableException {

        final PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);

        try {
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        } catch (final NotAvailableException ignore) {
        }
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());

        // set listener
        final PLYReadValueFromStreamListener listener =
                new PLYReadValueFromStreamListener() {
                    @Override
                    public void readFromStream(final ByteBuffer buffer) {
                    }
                };

        property.setReadLengthValueFromStreamListener(listener);
        assertEquals(property.getReadLengthValueFromStreamListener(), listener);
        assertTrue(property.isReadLengthValueFromStreamListenerAvailable());
    }

    @Test
    public void testGetValueType() throws NotAvailableException {
        // test when value type available
        PropertyPLY property = new PropertyPLY("name",
                DataTypePLY.PLY_FLOAT);

        assertTrue(property.isValueTypeAvailable());
        assertEquals(DataTypePLY.PLY_FLOAT, property.getValueType());

        // test when value type not available
        property = new PropertyPLY("name", null);

        assertFalse(property.isValueTypeAvailable());
        try {
            property.getValueType();
            fail("NotAvailableException expected but not thrown");
        } catch (final NotAvailableException ignore) {
        }
    }
}
