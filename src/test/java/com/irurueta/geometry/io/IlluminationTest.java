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

import static org.junit.Assert.assertEquals;

public class IlluminationTest {
    
    public IlluminationTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testValue() {
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
    public void testForValue() {
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
