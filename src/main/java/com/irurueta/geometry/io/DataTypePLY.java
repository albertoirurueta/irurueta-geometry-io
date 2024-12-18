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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration defining supported data types for PLY format.
 */
public enum DataTypePLY {
    /**
     * Signed 8-bit integer.
     */
    PLY_INT8("int8"),

    /**
     * Unsigned 8-bit integer.
     */
    PLY_UINT8("uint8"),

    /**
     * Signed 16-bit integer.
     */
    PLY_INT16("int16"),

    /**
     * Unsigned 16-bit integer.
     */
    PLY_UINT16("uint16"),

    /**
     * Signed 32-bit integer.
     */
    PLY_INT32("int32"),

    /**
     * Unsigned 32-bit integer.
     */
    PLY_UINT32("uint32"),

    /**
     * 32-bit floating-point.
     */
    PLY_FLOAT32("float32"),

    /**
     * 64-bit floating-point.
     */
    PLY_FLOAT64("float64"),

    /**
     * Signed character (signed 8-bit integer).
     */
    PLY_CHAR("char"),

    /**
     * Unsigned character (unsigned 8-bit integer).
     */
    PLY_UCHAR("uchar"),

    /**
     * Signed short (signed 16-bit integer).
     */
    PLY_SHORT("short"),

    /**
     * Unsigned short (unsigned 16-bit integer).
     */
    PLY_USHORT("ushort"),

    /**
     * Signed integer (signed 32-bit integer).
     */
    PLY_INT("int"),

    /**
     * Unsigned integer (unsigned 32-bit integer).
     */
    PLY_UINT("uint"),

    /**
     * Float (32-bit floating-point).
     */
    PLY_FLOAT("float"),

    /**
     * Double (64-bit floating-point).
     */
    PLY_DOUBLE("double");

    private static final Map<String, DataTypePLY> LOOKUP = new HashMap<>();

    static {
        for (final var value : DataTypePLY.values()) {
            LOOKUP.put(value.getValue(), value);
        }
    }

    /**
     * Internal representation of this enum as a string.
     */
    private final String value;

    /**
     * Constructor of this enum with a string representation.
     *
     * @param value String value for this enum.
     */
    DataTypePLY(String value) {
        this.value = value;
    }

    /**
     * Returns string representation of an instance of this enum.
     *
     * @return String representation.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Builds an instance of this enum.
     *
     * @param value String representation of this enum.
     * @return An instance of this enum.
     */
    public static DataTypePLY forValue(final String value) {
        if (value != null) {
            return LOOKUP.get(value);
        }
        return null;
    }
}
