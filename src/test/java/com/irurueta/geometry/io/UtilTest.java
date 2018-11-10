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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;

public class UtilTest {
    
    public UtilTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testToBigEndian() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);         
        
        assertEquals(Util.toBigEndian(shortValue), 
                buffer.getShort(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        int intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.toBigEndian(intValue), 
                buffer.getInt(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        long longValue = 542364326;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.toBigEndian(longValue), 
                buffer.getLong(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        float floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.toBigEndian(floatValue), buffer.getFloat(0), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        double doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.toBigEndian(doubleValue), buffer.getDouble(0), 0.0);
    }
    
    @Test
    public void testToLittleEndian() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);         
        
        assertEquals(Util.toLittleEndian(shortValue), 
                buffer.getShort(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        int intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        assertEquals(Util.toLittleEndian(intValue), 
                buffer.getInt(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        long longValue = 542364326;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        assertEquals(Util.toLittleEndian(longValue), 
                buffer.getLong(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        float floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        assertEquals(Util.toLittleEndian(floatValue), buffer.getFloat(0), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        double doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        assertEquals(Util.toLittleEndian(doubleValue), buffer.getDouble(0), 0.0);
    }
    
    @Test
    public void testFromBigEndian() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);         
        
        assertEquals(Util.fromBigEndian(shortValue), 
                buffer.getShort(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        int intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromBigEndian(intValue), 
                buffer.getInt(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        long longValue = 542364326;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromBigEndian(longValue), 
                buffer.getLong(0));
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        float floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromBigEndian(floatValue), buffer.getFloat(0), 0.0);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        
        double doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromBigEndian(doubleValue), buffer.getDouble(0), 0.0);        
    }
    
    @Test
    public void testFromLittleEndian() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        short shortValue = 5436;
        buffer.putShort(0, shortValue);
        buffer.order(ByteOrder.BIG_ENDIAN);         
        
        assertEquals(Util.fromLittleEndian(shortValue), 
                buffer.getShort(0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        
        int intValue = 231452564;
        buffer.putInt(0, intValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromLittleEndian(intValue), 
                buffer.getInt(0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        
        long longValue = 542364326;
        buffer.putLong(0, longValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromLittleEndian(longValue), 
                buffer.getLong(0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        
        float floatValue = 413632.3245f;
        buffer.putFloat(0, floatValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromLittleEndian(floatValue), buffer.getFloat(0), 0.0);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        
        double doubleValue = 5324623.23636645;
        buffer.putDouble(0, doubleValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        assertEquals(Util.fromLittleEndian(doubleValue), buffer.getDouble(0), 0.0);        
    }
    
    @Test
    public void testToEndianType() {
        
        short shortValue = 5436;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, 
                shortValue), Util.toBigEndian(shortValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                shortValue), Util.toLittleEndian(shortValue));
        
        
        int intValue = 231452564;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, 
                intValue), Util.toBigEndian(intValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                intValue), Util.toLittleEndian(intValue));
        
        
        long longValue = 542364326;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, 
                longValue), Util.toBigEndian(longValue));
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                longValue), Util.toLittleEndian(longValue));
        
        
        
        float floatValue = 413632.3245f;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, 
                floatValue), Util.toBigEndian(floatValue), 0.0);
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                floatValue), Util.toLittleEndian(floatValue), 0.0);
        
        
        
        double doubleValue = 5324623.23636645;
        assertEquals(Util.toEndianType(EndianType.BIG_ENDIAN_TYPE, 
                doubleValue), Util.toBigEndian(doubleValue), 0.0);
        assertEquals(Util.toEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                doubleValue), Util.toLittleEndian(doubleValue), 0.0);
                
    }
    
    @Test
    public void testFromEndianType() {
        
        short shortValue = 5436;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, 
                shortValue), Util.fromBigEndian(shortValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                shortValue), Util.fromLittleEndian(shortValue));
        
        
        int intValue = 231452564;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, 
                intValue), Util.fromBigEndian(intValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                intValue), Util.fromLittleEndian(intValue));
        
        
        long longValue = 542364326;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, 
                longValue), Util.fromBigEndian(longValue));
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                longValue), Util.fromLittleEndian(longValue));
        
        
        
        float floatValue = 413632.3245f;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, 
                floatValue), Util.fromBigEndian(floatValue), 0.0);
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                floatValue), Util.fromLittleEndian(floatValue), 0.0);
        
        
        
        double doubleValue = 5324623.23636645;
        assertEquals(Util.fromEndianType(EndianType.BIG_ENDIAN_TYPE, 
                doubleValue), Util.fromBigEndian(doubleValue), 0.0);
        assertEquals(Util.fromEndianType(EndianType.LITTLE_ENDIAN_TYPE, 
                doubleValue), Util.fromLittleEndian(doubleValue), 0.0);
    }    
}
