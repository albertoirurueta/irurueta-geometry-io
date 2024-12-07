
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

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderAndWriterTest {

    @Test
    void testConstructor() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        final var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);

        assertTrue(f.exists());
        assertTrue(f.delete());
        assertNotNull(readerWriter);
    }

    @Test
    void testReadWriteOneByte() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // write one byte
        final byte b = 100;
        readerWriter.write(b);

        // close file
        readerWriter.close();
        assertEquals(1, f.length());

        // reopen and read byte
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(b, readerWriter.read());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteArrayOfBytes() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());


        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        final var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(100, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var bytes2 = new byte[100];
        readerWriter.read(bytes2);

        // check correctness
        for (var b = 0; b < 100; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteArrayOfBytesWithOffsetAndLength() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        final var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }
        final var offset = 2;
        final var length = 3;

        // write array of bytes
        readerWriter.write(bytes, offset, length);

        // close file
        readerWriter.close();
        assertEquals(3, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var bytes2 = new byte[3];
        readerWriter.read(bytes2, 0, 2);

        // check correctness
        var counter = 0;
        for (var i = offset; i < offset + 2; i++) {
            assertEquals(bytes[i], bytes2[counter]);
            counter++;
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadFullyWriteArrayOfBytes() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(100, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var bytes2 = new byte[100];
        readerWriter.readFully(bytes2);

        // check correctness
        for (var b = 0; b < 100; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadFullyWriteArrayOfBytesWithOffsetAndLength() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        // initialize array of bytes
        final var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }
        var offset = 2;
        var length = 3;

        // write array of bytes
        readerWriter.write(bytes, offset, length);

        // close file
        readerWriter.close();
        assertEquals(3, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var bytes2 = new byte[3];
        readerWriter.readFully(bytes2, 0, 2);

        // check correctness
        var counter = 0;
        for (var i = offset; i < offset + 2; i++) {
            assertEquals(bytes[i], bytes2[counter]);
            counter++;
        }

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testSkipAndGetPosition() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // initialize array of bytes
        final var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(100, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        var bytes2 = new byte[20];
        readerWriter.read(bytes2);

        assertEquals(20, readerWriter.getPosition());

        // check correctness
        for (var b = 0; b < 20; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // skip 20 bytes
        readerWriter.skip(20);
        assertEquals(40, readerWriter.getPosition());

        // read remaining bytes
        bytes2 = new byte[60];
        readerWriter.read(bytes2);

        // check correctness
        var counter = 40;
        for (var i = 0; i < 60; i++) {
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }

        assertEquals(100, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testSeekAndClose() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // initialize array of bytes
        final var bytes = new byte[100];
        for (byte b = 0; b < 100; b++) {
            bytes[b] = b;
        }

        // write array of bytes
        readerWriter.write(bytes);

        // close file
        readerWriter.close();
        assertEquals(100, f.length());

        // reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        var bytes2 = new byte[20];
        readerWriter.read(bytes2);

        assertEquals(20, readerWriter.getPosition());

        // check correctness
        for (var b = 0; b < 20; b++) {
            assertEquals(bytes[b], bytes2[b]);
        }

        // seek to position 10
        readerWriter.seek(10);
        assertEquals(10, readerWriter.getPosition());

        // read remaining bytes
        bytes2 = new byte[90];
        readerWriter.read(bytes2);

        // check correctness
        var counter = 10;
        for (var i = 0; i < 90; i++) {
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }

        assertEquals(100, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteBoolean() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write two booleans
        readerWriter.writeBoolean(true);
        readerWriter.writeBoolean(false);

        // close file
        readerWriter.close();
        assertEquals(2, f.length());

        // reopen and read booleans
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertTrue(readerWriter.readBoolean());
        assertFalse(readerWriter.readBoolean());

        assertEquals(2, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteByte() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write byte
        final byte b = 100;
        readerWriter.writeByte(b);

        assertEquals(1, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(1, f.length());

        // reopen and read byte
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(b, readerWriter.readByte());

        assertEquals(1, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUnsignedByte() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write unsigned byte
        final short b = 200;
        readerWriter.writeUnsignedByte(b);

        assertEquals(1, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(1, f.length());

        // reopen and read unsigned byte
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(b, readerWriter.readUnsignedByte());

        assertEquals(1, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteShort() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write short
        final short value = -3252;
        readerWriter.writeShort(value);

        assertEquals(2, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(2, f.length());

        // reopen and read short
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readShort());

        assertEquals(2, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteShortWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write short twice with big and little endian
        final short value = -3252;
        readerWriter.writeShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(2, readerWriter.getPosition());

        readerWriter.writeShort(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(4, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(4, f.length());

        // reopen and read shorts
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readShort(EndianType.BIG_ENDIAN_TYPE));
        assertEquals(2, readerWriter.getPosition());

        assertEquals(value, readerWriter.readShort(EndianType.LITTLE_ENDIAN_TYPE));
        assertEquals(4, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUnsignedShort() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write short
        final var value = 45645;
        readerWriter.writeUnsignedShort(value);

        assertEquals(2, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(2, f.length());

        // reopen and read short
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedShort());

        assertEquals(2, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUnsignedShortWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write unsigned short twice with big and little endian
        final var value = 45645;
        readerWriter.writeUnsignedShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(2, readerWriter.getPosition());

        readerWriter.writeUnsignedShort(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(4, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(4, f.length());

        // reopen and read unsigned short
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedShort(EndianType.BIG_ENDIAN_TYPE));
        assertEquals(2, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedShort(EndianType.LITTLE_ENDIAN_TYPE));
        assertEquals(4, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteInt() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write int
        final var value = -5636634;
        readerWriter.writeInt(value);

        assertEquals(4, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(4, f.length());

        // reopen and read int
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readInt());

        assertEquals(4, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteIntWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write int twice with big and little endian
        final var value = -5636634;
        readerWriter.writeInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(4, readerWriter.getPosition());

        readerWriter.writeInt(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(8, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(8, f.length());

        // reopen and read integers
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readInt(EndianType.BIG_ENDIAN_TYPE));
        assertEquals(4, readerWriter.getPosition());

        assertEquals(value, readerWriter.readInt(EndianType.LITTLE_ENDIAN_TYPE));
        assertEquals(8, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUnsignedInt() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write unsigned int
        final var value = 436680954;
        readerWriter.writeUnsignedInt(value);

        assertEquals(4, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(4, f.length());

        // reopen and read unsigned int
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedInt());

        assertEquals(4, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUnsignedIntWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write unsigned integers twice with big and little endian
        final var value = 436680954L;
        readerWriter.writeUnsignedInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(4, readerWriter.getPosition());

        readerWriter.writeUnsignedInt(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(8, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(8, f.length());

        // reopen and read unsigned integers
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedInt(EndianType.BIG_ENDIAN_TYPE));
        assertEquals(4, readerWriter.getPosition());

        assertEquals(value, readerWriter.readUnsignedInt(EndianType.LITTLE_ENDIAN_TYPE));
        assertEquals(8, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteLong() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write long
        final var value = -436680954;
        readerWriter.writeLong(value);

        assertEquals(8, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(8, f.length());

        // reopen and read long
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readLong());

        assertEquals(8, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteLongWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write long twice with big and little endian
        final var value = -436680954;
        readerWriter.writeLong(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(8, readerWriter.getPosition());

        readerWriter.writeLong(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(16, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(16, f.length());

        // reopen and read longs
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readLong(EndianType.BIG_ENDIAN_TYPE));
        assertEquals(8, readerWriter.getPosition());

        assertEquals(value, readerWriter.readLong(EndianType.LITTLE_ENDIAN_TYPE));
        assertEquals(16, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadFloat() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write float
        final var value = 234523.34f;
        readerWriter.writeFloat(value);

        assertEquals(4, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(4, f.length());

        // reopen and read float
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readFloat(), 0.0);

        assertEquals(4, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteFloatWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write float twice with big and little endian
        final var value = 234523.34f;
        readerWriter.writeFloat(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(4, readerWriter.getPosition());

        readerWriter.writeFloat(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(8, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(8, f.length());

        // reopen and read floats
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readFloat(EndianType.BIG_ENDIAN_TYPE), 0.0);
        assertEquals(4, readerWriter.getPosition());

        assertEquals(value, readerWriter.readFloat(EndianType.LITTLE_ENDIAN_TYPE), 0.0);
        assertEquals(8, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadDouble() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write double
        final var value = 234523.344545;
        readerWriter.writeDouble(value);

        assertEquals(8, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(8, f.length());

        // reopen and read double
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readDouble(), 0.0);

        assertEquals(8, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteDoubleWithEndianType() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write double twice with big and little endian
        final var value = 234523.344545;
        readerWriter.writeDouble(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(8, readerWriter.getPosition());

        readerWriter.writeDouble(value, EndianType.LITTLE_ENDIAN_TYPE);
        assertEquals(16, readerWriter.getPosition());

        // close file
        readerWriter.close();
        assertEquals(16, f.length());

        // reopen and read doubles
        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        assertEquals(0, readerWriter.getPosition());

        assertEquals(value, readerWriter.readDouble(EndianType.BIG_ENDIAN_TYPE), 0.0);
        assertEquals(8, readerWriter.getPosition());

        assertEquals(value, readerWriter.readDouble(EndianType.LITTLE_ENDIAN_TYPE), 0.0);
        assertEquals(16, readerWriter.getPosition());

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadLine() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write text into file
        final var text = "first line\nsecond line";
        readerWriter.writeASCII(text);

        // close file
        readerWriter.close();

        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var firstLine = readerWriter.readLine();
        final var secondLine = readerWriter.readLine();

        assertEquals("first line", firstLine);
        assertEquals("second line", secondLine);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWord() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write text into file
        final var text = "first line\nsecond line";
        readerWriter.writeASCII(text);

        // close file
        readerWriter.close();

        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var firstWord = readerWriter.readWord();
        final var secondWord = readerWriter.readWord();
        final var thirdWord = readerWriter.readWord();
        final var fourthWord = readerWriter.readWord();
        // this is null because end is reached
        final var fifthWord = readerWriter.readWord();

        assertEquals("first", firstWord);
        assertEquals("line", secondWord);
        assertEquals("second", thirdWord);
        assertEquals("line", fourthWord);
        assertNull(fifthWord);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }

    @Test
    void testReadWriteUTF() throws IOException {
        final var f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());

        var readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());

        assertEquals(0, readerWriter.getPosition());

        // write text into file
        final var text = "first line\nsecond line";
        readerWriter.writeUTF(text);

        // close file
        readerWriter.close();

        readerWriter = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);

        final var text2 = readerWriter.readUTF();
        assertEquals(text, text2);

        // close and delete file
        readerWriter.close();
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
}
