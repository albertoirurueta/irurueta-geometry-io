/**
 * @file
 * This file contains Unit Tests for
 * com.irurueta.geometry.io.ElementPLY
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElementPLYTest {
    
    public ElementPLYTest() {
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
        String name = "name";
        long number = 2143245;
        
        //constructor without name
        ElementPLY element = new ElementPLY(null, number);
        
        try{
            element.getName();
            fail("NotAvailableException not thrown");
        }catch(NotAvailableException e){}
        assertFalse(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertFalse(element.isValidElement());
        
        //constructor with name and number of instances
        element = new ElementPLY(name, number);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertTrue(element.getProperties().isEmpty());
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());
        
        //constructor with name, number of instances and property
        PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        element = new ElementPLY(name, number, property);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertFalse(element.getProperties().isEmpty());
        assertTrue(element.getProperties().contains(property));
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());        
        
        //constructor with name, number of instances and property list
        List<PropertyPLY> properties = new LinkedList<PropertyPLY>();
        element = new ElementPLY(name, number, properties);
        assertEquals(element.getName(), name);
        assertTrue(element.isNameAvailable());
        assertEquals(element.getNumberOfInstances(), number);
        assertEquals(element.getProperties(), properties);
        assertTrue(element.arePropertiesAvailable());
        assertTrue(element.isValidElement());                
    }
    
    @Test
    public void testToString(){
        String name = "name";
        long number = 2143245;
        
        PropertyPLY property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        ElementPLY element = new ElementPLY(name, number, property);        
        
        assertEquals(element.toString(), "element name " + number + "\n" + 
                property.toString());
    }
}
