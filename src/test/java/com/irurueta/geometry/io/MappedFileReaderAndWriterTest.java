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

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static org.junit.Assert.*;

public class MappedFileReaderAndWriterTest {

    @Test
    public void testConstructor() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        final MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);

        assertTrue(f.exists());
        assertTrue(f.delete());
        assertNotNull(readerWriter);
    }

    @Test
    public void testReadWriteOneByte() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        // initial Writer has to be done with usual class
        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // write one byte
        final byte b = 100;
        readerWriter.write(b);

        //close file
        readerWriter.close();
        assertEquals(f.length(), 1);

        //reopen and read byte
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.read(), b);

        //close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteArrayOfBytes() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        final byte[] bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 100);

        // reopen and read array of bytes
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        final byte[] bytes2 = new byte[100];
        readerWriter.read(bytes2);

        // check correctness
        for (byte b = 0; b < 100; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteArrayOfBytesWithOffsetAndLength()
            throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        final byte[] bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }
        final int offset = 2;
        final int length = 3;

        // write array of bytes
        readerWriter.write(bytes, offset, length);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 3);

        // reopen and read array of bytes
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        final byte[] bytes2 = new byte[3];
        readerWriter.read(bytes2, 0, 2);

        // check correctness
        int counter = 0;
        for (int i = offset; i < offset + 2; i++) {
            assertEquals(bytes[i], bytes2[counter]);
            counter++;
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testSkipAndGetPosition() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // initialize array of bytes
        final byte[] bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 100);

        // reopen and read array of bytes
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        byte[] bytes2 = new byte[20];
        readerWriter.read(bytes2);

        assertEquals(readerWriter.getPosition(), 20);

        // check correctness
        for (byte b = 0; b < 20; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // skip 20 bytes
        readerWriter.skip(20);
        assertEquals(readerWriter.getPosition(), 40);

        // read remaining bytes
        bytes2 = new byte[60];
        readerWriter.read(bytes2);

        // check correctness
        int counter = 40;
        for (int i = 0; i < 60; i++) {
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }

        assertEquals(readerWriter.getPosition(), 100);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testSeekAndClose() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // initialize array of bytes
        final byte[] bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 100);

        // reopen and read array of bytes
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        byte[] bytes2 = new byte[20];
        readerWriter.read(bytes2);

        assertEquals(readerWriter.getPosition(), 20);

        // check correctness
        for (byte b = 0; b < 20; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // seek to position 10
        readerWriter.seek(10);
        assertEquals(readerWriter.getPosition(), 10);

        // read remaining bytes
        bytes2 = new byte[90];
        readerWriter.read(bytes2);

        // check correctness
        int counter = 10;
        for (int i = 0; i < 90; i++) {
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }

        assertEquals(readerWriter.getPosition(), 100);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteBoolean() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write two booleans
        readerWriter.writeBoolean(true);
        readerWriter.writeBoolean(false);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 2);

        // reopen and read booleans
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertTrue(readerWriter.readBoolean());
        assertFalse(readerWriter.readBoolean());

        assertEquals(readerWriter.getPosition(), 2);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteByte() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write byte
        final byte b = 100;
        readerWriter.writeByte(b);

        assertEquals(readerWriter.getPosition(), 1);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 1);

        // reopen and read byte
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readByte(), b);

        assertEquals(readerWriter.getPosition(), 1);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteUnsignedByte() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write unsigned byte
        final short b = 200;
        readerWriter.writeUnsignedByte(b);

        assertEquals(readerWriter.getPosition(), 1);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 1);

        // reopen and read unsigned byte
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readUnsignedByte(), b);

        assertEquals(readerWriter.getPosition(), 1);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteShort() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write short
        final short value = -3252;
        readerWriter.writeShort(value);

        assertEquals(readerWriter.getPosition(), 2);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 2);

        // reopen and read short
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readShort(), value);

        assertEquals(readerWriter.getPosition(), 2);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteShortWithEndianType()
            throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write short twice with big an little endian
        final short value = -3252;
        readerWriter.writeShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 2);

        readerWriter.writeShort(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 4);

        // reopen and read shorts
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readShort(EndianType.BIG_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 2);

        assertEquals(readerWriter.readShort(EndianType.LITTLE_ENDIAN_TYPE),
                value);
        assertEquals(readerWriter.getPosition(), 4);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteUnsigedShort() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write short
        final int value = 45645;
        readerWriter.writeUnsignedShort(value);

        assertEquals(readerWriter.getPosition(), 2);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 2);

        // reopen and read short
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readUnsignedShort(), value);

        assertEquals(readerWriter.getPosition(), 2);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteUnsignedShortWithEndianType()
            throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write unsigned short twice with big an little endian
        final int value = 45645;
        readerWriter.writeUnsignedShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 2);

        readerWriter.writeUnsignedShort(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 4);

        // reopen and read unsigned short
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readUnsignedShort(EndianType.BIG_ENDIAN_TYPE),
                value);
        assertEquals(readerWriter.getPosition(), 2);

        assertEquals(readerWriter.readUnsignedShort(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 4);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteInt() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write int
        final int value = -5636634;
        readerWriter.writeInt(value);

        assertEquals(readerWriter.getPosition(), 4);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 4);

        // reopen and read int
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readInt(), value);

        assertEquals(readerWriter.getPosition(), 4);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteIntWithEndianType() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write int twice with big an little endian
        final int value = -5636634;
        readerWriter.writeInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);

        readerWriter.writeInt(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 8);

        // reopen and read ints
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readInt(EndianType.BIG_ENDIAN_TYPE),
                value);
        assertEquals(readerWriter.getPosition(), 4);

        assertEquals(readerWriter.readInt(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 8);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteUnsignedInt() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write unsigned int
        final long value = 436680954;
        readerWriter.writeUnsignedInt(value);

        assertEquals(readerWriter.getPosition(), 4);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 4);

        // reopen and read unsigned int
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readUnsignedInt(), value);

        assertEquals(readerWriter.getPosition(), 4);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteUnsignedIntWithEndianType() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write unsigned ints twice with big an little endian
        final long value = 436680954;
        readerWriter.writeUnsignedInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);

        readerWriter.writeUnsignedInt(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 8);

        // reopen and read unsigned ints
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readUnsignedInt(EndianType.BIG_ENDIAN_TYPE),
                value);
        assertEquals(readerWriter.getPosition(), 4);

        assertEquals(readerWriter.readUnsignedInt(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 8);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteLong() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write long
        final long value = -436680954;
        readerWriter.writeLong(value);

        assertEquals(readerWriter.getPosition(), 8);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 8);

        // reopen and read long
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readLong(), value);

        assertEquals(readerWriter.getPosition(), 8);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteLongWithEndianType() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write long twice with big an little endian
        final long value = -436680954;
        readerWriter.writeLong(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);

        readerWriter.writeLong(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 16);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 16);

        // reopen and read longs
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readLong(EndianType.BIG_ENDIAN_TYPE),
                value);
        assertEquals(readerWriter.getPosition(), 8);

        assertEquals(readerWriter.readLong(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 16);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadFloat() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write float
        final float value = 234523.34f;
        readerWriter.writeFloat(value);

        assertEquals(readerWriter.getPosition(), 4);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 4);

        // reopen and read float
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readFloat(), value, 0.0);

        assertEquals(readerWriter.getPosition(), 4);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteFloatWithEndianType() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write float twice with big an little endian
        final float value = 234523.34f;
        readerWriter.writeFloat(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);

        readerWriter.writeFloat(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 8);

        // reopen and read floats
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readFloat(EndianType.BIG_ENDIAN_TYPE),
                value, 0.0);
        assertEquals(readerWriter.getPosition(), 4);

        assertEquals(readerWriter.readFloat(
                EndianType.LITTLE_ENDIAN_TYPE), value, 0.0);
        assertEquals(readerWriter.getPosition(), 8);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadDouble() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write double
        final double value = 234523.344545;
        readerWriter.writeDouble(value);

        assertEquals(readerWriter.getPosition(), 8);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 8);

        // reopen and read double
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readDouble(), value, 0.0);

        assertEquals(readerWriter.getPosition(), 8);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWriteDoubleWithEndianType() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write double twice with big an little endian
        final double value = 234523.344545;
        readerWriter.writeDouble(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);

        readerWriter.writeDouble(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 16);

        // close file
        readerWriter.close();
        assertEquals(f.length(), 16);

        // reopen and read doubles
        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        assertEquals(readerWriter.getPosition(), 0);

        assertEquals(readerWriter.readDouble(EndianType.BIG_ENDIAN_TYPE),
                value, 0.0);
        assertEquals(readerWriter.getPosition(), 8);

        assertEquals(readerWriter.readDouble(
                EndianType.LITTLE_ENDIAN_TYPE), value, 0.0);
        assertEquals(readerWriter.getPosition(), 16);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadLine() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write text into file
        final String text = "first line\nsecond line";
        readerWriter.writeASCII(text);

        // close file
        readerWriter.close();

        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        final String firstLine = readerWriter.readLine();
        final String secondLine = readerWriter.readLine();

        assertEquals(firstLine, "first line");
        assertEquals(secondLine, "second line");

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    public void testReadWord() throws IOException {
        final File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        MappedFileReaderAndWriter readerWriter = new MappedFileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(readerWriter.getPosition(), 0);

        // write text into file
        final String text = "first line\nsecond line";
        readerWriter.writeASCII(text);

        // close file
        readerWriter.close();

        readerWriter = new MappedFileReaderAndWriter(f,
                FileChannel.MapMode.READ_ONLY);

        final String firstWord = readerWriter.readWord();
        final String secondWord = readerWriter.readWord();
        final String thirdWord = readerWriter.readWord();
        final String fourthWord = readerWriter.readWord();
        // this is null because end is reached
        final String fifthWord = readerWriter.readWord();

        assertEquals(firstWord, "first");
        assertEquals(secondWord, "line");
        assertEquals(thirdWord, "second");
        assertEquals(fourthWord, "line");
        assertNull(fifthWord);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
}
