package com.irurueta.geometry.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataTypePLYTest {

    @Test
    void testValues() {
        assertEquals("int8", DataTypePLY.PLY_INT8.getValue());
        assertEquals("uint8", DataTypePLY.PLY_UINT8.getValue());
        assertEquals("int16", DataTypePLY.PLY_INT16.getValue());
        assertEquals("uint16", DataTypePLY.PLY_UINT16.getValue());
        assertEquals("int32", DataTypePLY.PLY_INT32.getValue());
        assertEquals("uint32", DataTypePLY.PLY_UINT32.getValue());
        assertEquals("float32", DataTypePLY.PLY_FLOAT32.getValue());
        assertEquals("float64", DataTypePLY.PLY_FLOAT64.getValue());
        assertEquals("char", DataTypePLY.PLY_CHAR.getValue());
        assertEquals("uchar", DataTypePLY.PLY_UCHAR.getValue());
        assertEquals("short", DataTypePLY.PLY_SHORT.getValue());
        assertEquals("ushort", DataTypePLY.PLY_USHORT.getValue());
        assertEquals("int", DataTypePLY.PLY_INT.getValue());
        assertEquals("uint", DataTypePLY.PLY_UINT.getValue());
        assertEquals("float", DataTypePLY.PLY_FLOAT.getValue());
        assertEquals("double", DataTypePLY.PLY_DOUBLE.getValue());
    }

    @Test
    void testForValue() {
        assertNull(DataTypePLY.forValue(null));
        assertNull(DataTypePLY.forValue("invalid"));

        assertEquals(DataTypePLY.PLY_INT8, DataTypePLY.forValue("int8"));
        assertEquals(DataTypePLY.PLY_UINT8, DataTypePLY.forValue("uint8"));
        assertEquals(DataTypePLY.PLY_INT16, DataTypePLY.forValue("int16"));
        assertEquals(DataTypePLY.PLY_UINT16, DataTypePLY.forValue("uint16"));
        assertEquals(DataTypePLY.PLY_INT32, DataTypePLY.forValue("int32"));
        assertEquals(DataTypePLY.PLY_UINT32, DataTypePLY.forValue("uint32"));
        assertEquals(DataTypePLY.PLY_FLOAT32, DataTypePLY.forValue("float32"));
        assertEquals(DataTypePLY.PLY_FLOAT64, DataTypePLY.forValue("float64"));
        assertEquals(DataTypePLY.PLY_CHAR, DataTypePLY.forValue("char"));
        assertEquals(DataTypePLY.PLY_UCHAR, DataTypePLY.forValue("uchar"));
        assertEquals(DataTypePLY.PLY_SHORT, DataTypePLY.forValue("short"));
        assertEquals(DataTypePLY.PLY_USHORT, DataTypePLY.forValue("ushort"));
        assertEquals(DataTypePLY.PLY_INT, DataTypePLY.forValue("int"));
        assertEquals(DataTypePLY.PLY_UINT, DataTypePLY.forValue("uint"));
        assertEquals(DataTypePLY.PLY_FLOAT, DataTypePLY.forValue("float"));
        assertEquals(DataTypePLY.PLY_DOUBLE, DataTypePLY.forValue("double"));
    }
}