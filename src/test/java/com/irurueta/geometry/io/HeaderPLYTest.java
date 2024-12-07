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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderPLYTest {

    @Test
    void testConstructor() {
        final var header = new HeaderPLY();

        assertEquals(PLYStorageMode.PLY_ASCII, header.getStorageMode());
        assertTrue(header.getElements().isEmpty());
        assertTrue(header.getComments().isEmpty());
        assertTrue(header.getObjInfos().isEmpty());
    }

    @Test
    void testToString() {
        final var header = new HeaderPLY();

        final var name = "name";
        final var number = 2143245L;

        final var property = new PropertyPLY(name, DataTypePLY.PLY_FLOAT32);
        final var element = new ElementPLY(name, number, property);
        header.getElements().add(element);

        assertEquals("ply\nformat ascii 1.0\n" + element + "end_header\n", header.toString());
    }
}
