/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.CRCDisabledException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 20, 2012
 */
package com.irurueta.geometry.io;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CRCDisabledExceptionTest {
    
    public CRCDisabledExceptionTest() {
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
    public void testConstructor(){
        CRCDisabledException ex;
        assertNotNull(ex = new CRCDisabledException());
        
        ex = null;
        assertNotNull(ex = new CRCDisabledException("message"));
        
        ex = null;
        assertNotNull(ex = new CRCDisabledException(new Exception()));
        
        ex = null;
        assertNotNull(ex = new CRCDisabledException("message", 
                new Exception()));
    }
}
