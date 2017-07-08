/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.LockedException
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

public class LockedExceptionTest {
    
    public LockedExceptionTest() {
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
        LockedException ex;
        assertNotNull(ex = new LockedException());
        
        ex = null;
        assertNotNull(ex = new LockedException("message"));
        
        ex = null;
        assertNotNull(ex = new LockedException(new Exception()));
        
        ex = null;
        assertNotNull(ex = new LockedException("message", new Exception()));
    }
}
