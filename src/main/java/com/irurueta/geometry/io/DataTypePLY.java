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
 * Enumeration defining supported data types for PLY format.
 */
public enum DataTypePLY {
    /**
     * Signed 8 bit integer.
     */
    PLY_INT8("int8"),
    
    /**
     * Unsigned 8 bit integer.
     */
    PLY_UINT8("uint8"),
    
    /**
     * Signed 16 bit integer.
     */
    PLY_INT16("int16"),
    
    /**
     * Unsigned 16 bit integer.
     */
    PLY_UINT16("uint16"),
    
    /**
     * Signed 32 bit integer.
     */
    PLY_INT32("int32"),
    
    /**
     * Unsigned 32 bit integer.
     */
    PLY_UINT32("uint32"),
    
    /**
     * 32 bit floating-point.
     */
    PLY_FLOAT32("float32"),
    
    /**
     * 64 bit floating-point.
     */
    PLY_FLOAT64("float64"),
    
    /**
     * Signed character (signed 8 bit integer).
     */
    PLY_CHAR("char"),
    
    /**
     * Unsigned character (unsigned 8 bit integer).
     */
    PLY_UCHAR("uchar"),
    
    /**
     * Signed short (signed 16 bit integer).
     */
    PLY_SHORT("short"),
    
    /**
     * Unsigned short (unsigned 16 bit integer).
     */
    PLY_USHORT("ushort"),
    
    /**
     * Signed integer (signed 32 bit integer).
     */
    PLY_INT("int"),
    
    /**
     * Unsigned integer (unsigned 32 bit integer).
     */
    PLY_UINT("uint"),
    
    /**
     * Float (32 bit floating-point).
     */
    PLY_FLOAT("float"),
    
    /**
     * Double (64 bit floating-point).
     */
    PLY_DOUBLE("double");
    
    
    /**
     * Internal representation of this enum as a string.
     */
    private String value;
    
    /**
     * Constructor of this enum with a string representation.
     * @param value String value for this enum.
     */
    DataTypePLY(String value) {
        this.value = value;
    }
    
    /**
     * Returns string representation of an instance of this enum.
     * @return String representation.
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * Builds an instance of this enum.
     * @param value String representation of this enum.
     * @return An instance of this enum.
     */
    public static DataTypePLY forValue(String value) {
        if (value != null) {
            if (value.equalsIgnoreCase("int8")) {
                return PLY_INT8;
            } else if (value.equalsIgnoreCase("uint8")) {
                return PLY_UINT8;
            } else if (value.equalsIgnoreCase("int16")) {
                return PLY_INT16;
            } else if (value.equalsIgnoreCase("uint16")) {
                return PLY_UINT16;
            } else if (value.equalsIgnoreCase("int32")) {
                return PLY_INT32;
            } else if (value.equalsIgnoreCase("uint32")) {
                return PLY_UINT32;
            } else if (value.equalsIgnoreCase("float32")) {
                return PLY_FLOAT32;
            } else if (value.equalsIgnoreCase("float64")) {
                return PLY_FLOAT64;
            } else if (value.equalsIgnoreCase("char")) {
                return PLY_CHAR;
            } else if (value.equalsIgnoreCase("uchar")) {
                return PLY_UCHAR;
            } else if (value.equalsIgnoreCase("short")) {
                return PLY_SHORT;
            } else if (value.equalsIgnoreCase("ushort")) {
                return PLY_USHORT;
            } else if (value.equalsIgnoreCase("int")) {
                return PLY_INT;
            } else if (value.equalsIgnoreCase("uint")) {
                return PLY_UINT;
            } else if (value.equalsIgnoreCase("float")) {
                return PLY_FLOAT;
            } else if (value.equalsIgnoreCase("double")) {
                return PLY_DOUBLE;
            } else {
                return null;
            }
        }
        return null;
    }
}
