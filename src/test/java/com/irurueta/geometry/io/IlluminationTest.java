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

import static org.junit.Assert.assertEquals;

public class IlluminationTest {

    @Test
    public void testValue() {
        assertEquals(0, Illumination.COLOR_AND_AMBIENT_OFF.value());
        assertEquals(1, Illumination.COLOR_AND_AMBIENT_ON.value());
        assertEquals(2, Illumination.HIGHLIGHT_ON.value());
        assertEquals(3, Illumination.REFLECTION_ON_AND_RAY_TRACE_ON.value());
        assertEquals(4, Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAYTRACE_ON.value());
        assertEquals(5, Illumination.REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON.value());
        assertEquals(6, Illumination.
                TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_OFF_AND_RAY_TRACE_ON.value());
        assertEquals(7,
                Illumination.TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON.value());
        assertEquals(8, Illumination.REFLECTION_ON_AND_RAY_TRACE_OFF.value());
        assertEquals(9, Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAY_TRACE_OFF.value());
        assertEquals(10, Illumination.CAST_SHADOWS_ONTO_INVISIBLE_SURFACES.value());
    }

    @Test
    public void testForValue() {
        assertEquals(Illumination.COLOR_AND_AMBIENT_OFF, Illumination.forValue(0));
        assertEquals(Illumination.COLOR_AND_AMBIENT_ON, Illumination.forValue(1));
        assertEquals(Illumination.HIGHLIGHT_ON, Illumination.forValue(2));
        assertEquals(Illumination.REFLECTION_ON_AND_RAY_TRACE_ON, Illumination.forValue(3));
        assertEquals(Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAYTRACE_ON, Illumination.forValue(4));
        assertEquals(Illumination.REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON, Illumination.forValue(5));
        assertEquals(Illumination.TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_OFF_AND_RAY_TRACE_ON,
                Illumination.forValue(6));
        assertEquals(Illumination.TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON,
                Illumination.forValue(7));
        assertEquals(Illumination.REFLECTION_ON_AND_RAY_TRACE_OFF, Illumination.forValue(8));
        assertEquals(Illumination.TRANSPARENCY_GLASS_ON_REFLECTION_RAY_TRACE_OFF, Illumination.forValue(9));
        assertEquals(Illumination.CAST_SHADOWS_ONTO_INVISIBLE_SURFACES, Illumination.forValue(10));
    }
}
