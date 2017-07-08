/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.NotReadyException
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 19, 2012
 */
package com.irurueta.geometry.io;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NotReadyExceptionTest {
    
    public NotReadyExceptionTest() {
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
        NotReadyException ex;
        assertNotNull(ex = new NotReadyException());
        
        ex = null;
        assertNotNull(ex = new NotReadyException("message"));
        
        ex = null;
        assertNotNull(ex = new NotReadyException(new Exception()));
        
        ex = null;
        assertNotNull(ex = new NotReadyException("message", new Exception()));
    }
}
