/**
 * @file
 * This file contains Unit Tests for
 * com.irurueta.geometry.io.PropertyPLY
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertyPLYTest {
    
    public PropertyPLYTest() {
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
    public void testConstructors() throws NotAvailableException{
        String name = "property name";
        
        //set property without name
        PropertyPLY property = new PropertyPLY(null, 
                DataTypePLY.PLY_CHAR);
        try{
            property.getName();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isNameAvailable());
        assertEquals(property.getPropertyType(), 
                PropertyTypePLY.PROPERTY_PLY_SCALAR);
        assertTrue(property.isPropertyTypeAvailable());
        try{
            property.getLengthType();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_CHAR);
        assertTrue(property.isValueTypeAvailable());
        assertFalse(property.isValidProperty());
        
        try{
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try{
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try{
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try{
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());
        
        
        //set property with name and vlaue type
        property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        assertEquals(property.getName(), name);
        assertTrue(property.isNameAvailable());
        assertEquals(property.getPropertyType(), 
                PropertyTypePLY.PROPERTY_PLY_SCALAR);
        assertTrue(property.isPropertyTypeAvailable());
        try{
            property.getLengthType();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_FLOAT32);
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty()); 
        
        try{
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try{
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try{
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try{
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());
        
        
        //property with name, length type and value type
        property = new PropertyPLY(name, DataTypePLY.PLY_UCHAR, 
                DataTypePLY.PLY_UINT);
        assertEquals(property.getName(), name);
        assertTrue(property.isNameAvailable());
        assertEquals(property.getPropertyType(),
                PropertyTypePLY.PROPERTY_PLY_LIST);
        assertTrue(property.isPropertyTypeAvailable());
        assertEquals(property.getLengthType(), DataTypePLY.PLY_UCHAR);
        assertTrue(property.isLengthTypeAvailable());
        assertEquals(property.getValueType(), DataTypePLY.PLY_UINT);
        assertTrue(property.isValueTypeAvailable());
        assertTrue(property.isValidProperty());
        
        try{
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        try{
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        try{
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        try{
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());                     
    }
    
    @Test
    public void testToString(){
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
            throws NotAvailableException{
        
        PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);
        
        try{
            property.getReadValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromBufferListenerAvailable());
        
        //set listener
        PLYReadValueFromBufferListener listener = 
                new PLYReadValueFromBufferListener(){
            @Override
            public void readValueFromBuffer(ByteBuffer buffer){}            
                };
        
        property.setReadValueFromBufferListener(listener);
        assertEquals(property.getReadValueFromBufferListener(), listener);
        assertTrue(property.isReadValueFromBufferListenerAvailable());
        
    }
    
    @Test
    public void testGetSetReadValueFromStreamListener()
            throws NotAvailableException{
        
        PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);
        
        try{
            property.getReadValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadValueFromStreamListenerAvailable());
        
        //set listener
        PLYReadValueFromStreamListener listener =
                new PLYReadValueFromStreamListener(){
            @Override
            public void readFromStream(ByteBuffer buffer) throws IOException{}                    
                };
        
        property.setReadValueFromStreamListener(listener);
        assertEquals(property.getReadValueFromStreamListener(), listener);
        assertTrue(property.isReadValueFromStreamListenerAvailable());
    }
    
    @Test
    public void testGetSetReadLengthValueFromBufferListener() 
            throws NotAvailableException{
        
        PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);
        
        try{
            property.getReadLengthValueFromBufferListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromBufferListenerAvailable());
        
        //set listener
        PLYReadValueFromBufferListener listener =
                new PLYReadValueFromBufferListener(){
            @Override
            public void readValueFromBuffer(ByteBuffer buffer){}
                };
        
        property.setReadLengthValueFromBufferListener(listener);
        assertEquals(property.getReadLengthValueFromBufferListener(), listener);
        assertTrue(property.isReadLengthValueFromBufferListenerAvailable());
    }
    
    @Test
    public void testGetSetReadLengthValueFromStreamListener()
            throws NotAvailableException{
        
        PropertyPLY property = new PropertyPLY("x", DataTypePLY.PLY_FLOAT32);
        
        try{
            property.getReadLengthValueFromStreamListener();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(property.isReadLengthValueFromStreamListenerAvailable());
        
        //set listener
        PLYReadValueFromStreamListener listener =
                new PLYReadValueFromStreamListener(){
            @Override
            public void readFromStream(ByteBuffer buffer) throws IOException {}
                };
        
        property.setReadLengthValueFromStreamListener(listener);
        assertEquals(property.getReadLengthValueFromStreamListener(), listener);
        assertTrue(property.isReadLengthValueFromStreamListenerAvailable());
    }
}
