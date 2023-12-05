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

/**
 * Enumerator containing the available illumination options and their
 * corresponding integer values, as shown below:
 * 0. Color on and Ambient off.
 * 1. Color on and Ambient on.
 * 2. Highlight on.
 * 3. Reflection on and Ray trace on.
 * 4. Transparency: Glass on, Reflection: Ray trace on.
 * 5. Reflection: Fresnel on and Ray trace on.
 * 6. Transparency: Refraction on, Reflection: Fresnel off and Ray trace on.
 * 7. Transparency: Refraction on, Reflection: Fresnel on and Ray trace on.
 * 8. Reflection on and Ray trace off.
 * 9. Transparency: Glass on, Reflection: Ray trace off.
 * 10. Casts shadows onto invisible surfaces.
 */
public enum Illumination {
    /**
     * Color on and ambient off.
     */
    COLOR_AND_AMBIENT_OFF(0),

    /**
     * Color on and ambient on.
     */
    COLOR_AND_AMBIENT_ON(1),

    /**
     * Highlight on.
     */
    HIGHLIGHT_ON(2),

    /**
     * Reflection on and ray trace on.
     */
    REFLECTION_ON_AND_RAY_TRACE_ON(3),

    /**
     * Transparency: glass on, reflection: ray trace on.
     */
    TRANSPARENCY_GLASS_ON_REFLECTION_RAYTRACE_ON(4),

    /**
     * Reflection: Fresnel on and ray trace on.
     */
    REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON(5),

    /**
     * Transparency: refraction on, Reflection: Fresnel off and ray trace on.
     */
    TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_OFF_AND_RAY_TRACE_ON(6),

    /**
     * Transparency: refraction on, Reflection: Fresnel on and ray trace on.
     */
    TRANSPARENCY_REFRACTION_ON_REFLECTION_FRESNEL_ON_AND_RAY_TRACE_ON(7),

    /**
     * Reflection on and ray trace off.
     */
    REFLECTION_ON_AND_RAY_TRACE_OFF(8),

    /**
     * Transparency: glass on, Reflection: ray trace off.
     */
    TRANSPARENCY_GLASS_ON_REFLECTION_RAY_TRACE_OFF(9),

    /**
     * Cast shadows onto invisible surfaces.
     */
    CAST_SHADOWS_ONTO_INVISIBLE_SURFACES(10);

    /**
     * Internal value to be stored as part of this enum.
     */
    private final int value;

    /**
     * Constructor.
     *
     * @param value value associated to this enum.
     */
    Illumination(final int value) {
        this.value = value;
    }

    /**
     * Returns value associated to this enum.
     *
     * @return value associated to this enum.
     */
    public int value() {
        return value;
    }

    /**
     * Factory method to create and instance of this enum from its associated
     * value.
     *
     * @param value value associated to enum to be created.
     * @return an illumination instance.
     */
    public static Illumination forValue(final int value) {
        if (value >= 0 && value <= 10) {
            return Illumination.values()[value];
        } else {
            return null;
        }
    }
}
