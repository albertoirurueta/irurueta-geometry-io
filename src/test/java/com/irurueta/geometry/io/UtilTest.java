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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {

    @Test
    void testToBigEndian() {
        final var buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getShort(0), Util.toBigEndian(shortValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getInt(0), Util.toBigEndian(intValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var longValue = 542364326L;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getLong(0), Util.toBigEndian(longValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getFloat(0), Util.toBigEndian(floatValue), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getDouble(0), Util.toBigEndian(doubleValue), 0.0);
    }

    @Test
    void testToLittleEndian() {
        final var buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(buffer.getShort(0), Util.toLittleEndian(shortValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(buffer.getInt(0), Util.toLittleEndian(intValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var longValue = 542364326L;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(buffer.getLong(0), Util.toLittleEndian(longValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(buffer.getFloat(0), Util.toLittleEndian(floatValue), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(buffer.getDouble(0), Util.toLittleEndian(doubleValue), 0.0);
    }

    @Test
    void testFromBigEndian() {
        final var buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getShort(0), Util.fromBigEndian(shortValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getInt(0), Util.fromBigEndian(intValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var longValue = 542364326L;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getLong(0), Util.fromBigEndian(longValue));
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getFloat(0), Util.fromBigEndian(floatValue), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);

        final var doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getDouble(0), Util.fromBigEndian(doubleValue), 0.0);
    }

    @Test
    void testFromLittleEndian() {
        final var buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getShort(0), Util.fromLittleEndian(shortValue));
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final var intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getInt(0), Util.fromLittleEndian(intValue));
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final var longValue = 542364326L;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getLong(0), Util.fromLittleEndian(longValue));
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final var floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getFloat(0), Util.fromLittleEndian(floatValue), 0.0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        final var doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);

        assertEquals(buffer.getDouble(0), Util.fromLittleEndian(doubleValue), 0.0);
    }

    @Test
    void testToEndianType() {
        final short shortValue = 5436;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, shortValue), Util.toBigEndian(shortValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, shortValue), Util.toLittleEndian(shortValue));

        final var intValue = 231452564;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, intValue), Util.toBigEndian(intValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, intValue), Util.toLittleEndian(intValue));

        final var longValue = 542364326L;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, longValue), Util.toBigEndian(longValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, longValue), Util.toLittleEndian(longValue));

        final var floatValue = 413632.3245f;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, floatValue), Util.toBigEndian(floatValue),
                0.0);
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, floatValue), Util.toLittleEndian(floatValue),
                0.0);

        final var doubleValue = 5324623.23636645;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, doubleValue), Util.toBigEndian(doubleValue),
                0.0);
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, doubleValue), Util.toLittleEndian(doubleValue),
                0.0);
    }

    @Test
    void testFromEndianType() {
        final short shortValue = 5436;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, shortValue), Util.fromBigEndian(shortValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, shortValue), Util.fromLittleEndian(shortValue));

        final var intValue = 231452564;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, intValue), Util.fromBigEndian(intValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, intValue), Util.fromLittleEndian(intValue));

        final var longValue = 542364326L;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, longValue), Util.fromBigEndian(longValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, longValue), Util.fromLittleEndian(longValue));

        final var floatValue = 413632.3245f;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, floatValue), Util.fromBigEndian(floatValue),
                0.0);
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, floatValue), Util.fromLittleEndian(floatValue),
                0.0);

        final var doubleValue = 5324623.23636645;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, doubleValue), Util.fromBigEndian(doubleValue),
                0.0);
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, doubleValue),
                Util.fromLittleEndian(doubleValue), 0.0);
    }
}
