/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.Illumination
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 13, 2012
 */

package com.irurueta.geometry.io;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IlluminationTest {
    
    public IlluminationTest() {
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
    public void testValue(){
        assertEquals(Illumination.COLOR_AND_AMBIENT_OFF.value(), 0);
        assertEquals(Illumination.COLOR_AND_AMBIENT_ON.value(), 1);
        assertEquals(Illumination.HIGHLIGHT_ON.value(), 2);
        assertEquals(Illumination.REFLECTION_ON_AND_RAY_TRACE_ON.value(), 3);
        assertEquals(Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAYTRACE_ON.
                value(), 4);
        assertEquals(Illumination.REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON.
                value(), 5);
        assertEquals(Illumination.
                TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_OFF_AND_RAY_TRACE_ON.
                value(), 6);
        assertEquals(Illumination.
                TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON.
                value(), 7);
        assertEquals(Illumination.REFLECTION_ON_AND_RAY_TRACE_OFF.value(), 8);
        assertEquals(Illumination.
                TRANSPARENCY_GLASS_ON_REFLECTION_RAY_TRACE_OFF.value(), 9);
        assertEquals(Illumination.CAST_SHADOWS_ONTO_INVISIBLE_SURFACES.value(), 
                10);        
    }

    @Test
    public void testForValue(){
        assertEquals(Illumination.forValue(0), 
                Illumination.COLOR_AND_AMBIENT_OFF);
        assertEquals(Illumination.forValue(1),
                Illumination.COLOR_AND_AMBIENT_ON);
        assertEquals(Illumination.forValue(2),
                Illumination.HIGHLIGHT_ON);
        assertEquals(Illumination.forValue(3),
                Illumination.REFLECTION_ON_AND_RAY_TRACE_ON);
        assertEquals(Illumination.forValue(4),
                Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAYTRACE_ON);
        assertEquals(Illumination.forValue(5),
                Illumination.REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON);
        assertEquals(Illumination.forValue(6),
                Illumination.TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_OFF_AND_RAY_TRACE_ON);
        assertEquals(Illumination.forValue(7),
                Illumination.TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON);
        assertEquals(Illumination.forValue(8),
                Illumination.REFLECTION_ON_AND_RAY_TRACE_OFF);
        assertEquals(Illumination.forValue(9),
                Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAY_TRACE_OFF);
        assertEquals(Illumination.forValue(10),
                Illumination.CAST_SHADOWS_ONTO_INVISIBLE_SURFACES);
    }
}
