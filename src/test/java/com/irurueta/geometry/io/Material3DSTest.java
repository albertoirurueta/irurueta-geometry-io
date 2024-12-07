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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Material3DSTest {

    private static final String MATERIAL_NAME = "name";

    @Test
    void testConstructor() {
        var material = new Material3DS();
        assertNull(material.getMaterialName());
        assertFalse(material.isMaterialNameAvailable());

        material = new Material3DS(MATERIAL_NAME);
        assertEquals(MATERIAL_NAME, material.getMaterialName());
        assertTrue(material.isMaterialNameAvailable());
    }

    @Test
    void testGetSetMaterialName() {
        final var material = new Material3DS();

        // check default value
        assertNull(material.getMaterialName());
        assertFalse(material.isMaterialNameAvailable());

        // set new value
        material.setMaterialName(MATERIAL_NAME);

        // check
        assertEquals(MATERIAL_NAME, material.getMaterialName());
        assertTrue(material.isMaterialNameAvailable());
    }
}
