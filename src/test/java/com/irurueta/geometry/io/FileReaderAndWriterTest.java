/**
 * @file
 * This file contains Unit Tests for
 * com.irurueta.geometry.io.FileReaderAndWriter
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileReaderAndWriterTest {
    
    public FileReaderAndWriterTest() { }

    @BeforeClass
    public static void setUpClass() throws Exception { }

    @AfterClass
    public static void tearDownClass() throws Exception { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testConstructor() throws IOException {
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
    
    @Test
    public void testReadWriteOneByte() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        //write one byte
        byte b = 100;
        readerWriter.write(b);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 1);
        
        //reoopen and read byte
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.read(), b);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
    
    @Test
    public void testReadWriteArrayOfBytes() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        
        //write array of bytes        
        readerWriter.write(bytes);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 100);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[100];
        readerWriter.read(bytes2);
        
        //check correctness
        for(byte b = 0; b < 100; b++){
            assertEquals(bytes[b], bytes2[b]);
        }
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());        
    }

    @Test
    public void testReadWriteArrayOfBytesWithOffsetAndLength() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        int offset = 2;
        int length = 3;
        
        //write array of bytes        
        readerWriter.write(bytes, offset, length);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 3);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[3];
        readerWriter.read(bytes2, 0, 2);
        
        //check correctness
        int counter = 0;
        for(int i = offset; i < 2; i++){
            assertEquals(bytes[i], bytes2[counter]);
            counter++;
        }
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }
 
    @Test
    public void testReadFullyWriteArrayOfBytes() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        
        //write array of bytes        
        readerWriter.write(bytes);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 100);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[100];
        readerWriter.readFully(bytes2);
        
        //check correctness
        for(byte b = 0; b < 100; b++){
            assertEquals(bytes[b], bytes2[b]);
        }
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }

    @Test
    public void testReadFullyWriteArrayOfBytesWithOffsetAndLength() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        int offset = 2;
        int length = 3;
        
        //write array of bytes        
        readerWriter.write(bytes, offset, length);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 3);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[3];
        readerWriter.readFully(bytes2, 0, 2);
        
        //check correctness
        int counter = 0;
        for(int i = offset; i < 2; i++){
            assertEquals(bytes[i], bytes2[counter]);
            counter++;
        }
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }

    @Test
    public void testSkipAndGetPosition() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        
        //write array of bytes        
        readerWriter.write(bytes);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 100);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[20];
        readerWriter.read(bytes2);
        
        assertEquals(readerWriter.getPosition(), 20);
        
        //check correctness
        for(byte b = 0; b < 20; b++){
            assertEquals(bytes[b], bytes2[b]);
        }
        
        //skip 20 bytes
        readerWriter.skip(20);
        assertEquals(readerWriter.getPosition(), 40);
        
        //read remaining bytes
        bytes2 = new byte[60];
        readerWriter.read(bytes2);
        
        //check correctness
        int counter = 40;
        for(int i = 0; i < 60; i++){
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }
        
        assertEquals(readerWriter.getPosition(), 100);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }

    @Test
    public void testSeekAndClose() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //initialize array of bytes
        byte[] bytes = new byte[100];
        for(byte b = 0; b < 100; b++){
            bytes[b] = b;
        }
        
        //write array of bytes        
        readerWriter.write(bytes);
        
        //close file
        readerWriter.close();
        assertEquals(f.length(), 100);
        
        //reopen and read array of bytes
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        byte[] bytes2 = new byte[20];
        readerWriter.read(bytes2);
        
        assertEquals(readerWriter.getPosition(), 20);        
        
        //check correctness
        for(byte b = 0; b < 20; b++){
            assertEquals(bytes[b], bytes2[b]);
        }
        
        //seek to position 10
        readerWriter.seek(10);
        assertEquals(readerWriter.getPosition(), 10);
        
        //read remaining bytes
        bytes2 = new byte[90];
        readerWriter.read(bytes2);
        
        //check correctness
        int counter = 10;
        for(int i = 0; i < 90; i++){
            assertEquals(bytes[counter], bytes2[i]);
            counter++;
        }
        
        assertEquals(readerWriter.getPosition(), 100);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }

    @Test
    public void testReadWriteBoolean() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write two booleans
        readerWriter.writeBoolean(true);
        readerWriter.writeBoolean(false);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 2);
        
        //reopen and read booleans
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertTrue(readerWriter.readBoolean());
        assertFalse(readerWriter.readBoolean());
        
        assertEquals(readerWriter.getPosition(), 2);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                
    }

    @Test
    public void testReadWriteByte() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write byte
        byte b = 100;
        readerWriter.writeByte(b);
        
        assertEquals(readerWriter.getPosition(), 1);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 1);
        
        //reopen and read byte
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readByte(), b);
        
        assertEquals(readerWriter.getPosition(), 1);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                        
    }

    @Test
    public void testReadWriteUnsignedByte() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write unsigned byte
        short b = 200;
        readerWriter.writeUnsignedByte(b);
        
        assertEquals(readerWriter.getPosition(), 1);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 1);
        
        //reopen and read unsigned byte
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readUnsignedByte(), b);
        
        assertEquals(readerWriter.getPosition(), 1);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteShort() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write short
        short value = -3252;
        readerWriter.writeShort(value);
        
        assertEquals(readerWriter.getPosition(), 2);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 2);
        
        //reopen and read short
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readShort(), value);
        
        assertEquals(readerWriter.getPosition(), 2);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteShortWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write short twice with big an little endian
        short value = -3252;
        readerWriter.writeShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 2);        
        
        readerWriter.writeShort(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 4);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 4);
        
        //reopen and read shorts
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readShort(EndianType.BIG_ENDIAN_TYPE), value);        
        assertEquals(readerWriter.getPosition(), 2);
        
        assertEquals(readerWriter.readShort(EndianType.LITTLE_ENDIAN_TYPE), 
                value);
        assertEquals(readerWriter.getPosition(), 4);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }
 
    @Test
    public void testReadWriteUnsigedShort() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write short
        int value = 45645;
        readerWriter.writeUnsignedShort(value);
        
        assertEquals(readerWriter.getPosition(), 2);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 2);
        
        //reopen and read short
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readUnsignedShort(), value);
        
        assertEquals(readerWriter.getPosition(), 2);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteUnsignedShortWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write unsigned short twice with big an little endian
        int value = 45645;
        readerWriter.writeUnsignedShort(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 2);        
        
        readerWriter.writeUnsignedShort(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 4);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 4);
        
        //reopen and read unsigned short
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readUnsignedShort(EndianType.BIG_ENDIAN_TYPE), 
                value);        
        assertEquals(readerWriter.getPosition(), 2);
        
        assertEquals(readerWriter.readUnsignedShort(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 4);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }    
 
    @Test
    public void testReadWriteInt() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write int
        int value = -5636634;
        readerWriter.writeInt(value);
        
        assertEquals(readerWriter.getPosition(), 4);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 4);
        
        //reopen and read int
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readInt(), value);
        
        assertEquals(readerWriter.getPosition(), 4);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteIntWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write int twice with big an little endian
        int value = -5636634;
        readerWriter.writeInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);        
        
        readerWriter.writeInt(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 8);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 8);
        
        //reopen and read ints
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readInt(EndianType.BIG_ENDIAN_TYPE), 
                value);        
        assertEquals(readerWriter.getPosition(), 4);
        
        assertEquals(readerWriter.readInt(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 8);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }    

    @Test
    public void testReadWriteUnsignedInt() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write unsigned int
        long value = 436680954;
        readerWriter.writeUnsignedInt(value);
        
        assertEquals(readerWriter.getPosition(), 4);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 4);
        
        //reopen and read unsigned int
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readUnsignedInt(), value);
        
        assertEquals(readerWriter.getPosition(), 4);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteUnsignedIntWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write unsigned ints twice with big an little endian
        long value = 436680954;
        readerWriter.writeUnsignedInt(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);        
        
        readerWriter.writeUnsignedInt(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 8);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 8);
        
        //reopen and read unsigned ints
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readUnsignedInt(EndianType.BIG_ENDIAN_TYPE), 
                value);        
        assertEquals(readerWriter.getPosition(), 4);
        
        assertEquals(readerWriter.readUnsignedInt(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 8);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }  

    @Test
    public void testReadWriteLong() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write long
        long value = -436680954;
        readerWriter.writeLong(value);
        
        assertEquals(readerWriter.getPosition(), 8);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 8);
        
        //reopen and read long
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readLong(), value);
        
        assertEquals(readerWriter.getPosition(), 8);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteLongWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write long twice with big an little endian
        long value = -436680954;
        readerWriter.writeLong(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);
        
        readerWriter.writeLong(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 16);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 16);
        
        //reopen and read longs
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readLong(EndianType.BIG_ENDIAN_TYPE), 
                value);        
        assertEquals(readerWriter.getPosition(), 8);
        
        assertEquals(readerWriter.readLong(
                EndianType.LITTLE_ENDIAN_TYPE), value);
        assertEquals(readerWriter.getPosition(), 16);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }    

    @Test
    public void testReadFloat() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write float
        float value = 234523.34f;
        readerWriter.writeFloat(value);
        
        assertEquals(readerWriter.getPosition(), 4);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 4);
        
        //reopen and read float
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readFloat(), value, 0.0);
        
        assertEquals(readerWriter.getPosition(), 4);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteFloatWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write float twice with big an little endian
        float value = 234523.34f;
        readerWriter.writeFloat(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 4);
        
        readerWriter.writeFloat(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 8);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 8);
        
        //reopen and read floats
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readFloat(EndianType.BIG_ENDIAN_TYPE), 
                value, 0.0);        
        assertEquals(readerWriter.getPosition(), 4);
        
        assertEquals(readerWriter.readFloat(
                EndianType.LITTLE_ENDIAN_TYPE), value, 0.0);
        assertEquals(readerWriter.getPosition(), 8);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }    

    @Test
    public void testReadDouble() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write double
        double value = 234523.344545;
        readerWriter.writeDouble(value);
        
        assertEquals(readerWriter.getPosition(), 8);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 8);
        
        //reopen and read double
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readDouble(), value, 0.0);
        
        assertEquals(readerWriter.getPosition(), 8);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }

    @Test
    public void testReadWriteDoubleWithEndianType() 
            throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);
        
        //write double twice with big an little endian
        double value = 234523.344545;
        readerWriter.writeDouble(value, EndianType.BIG_ENDIAN_TYPE);
        assertEquals(readerWriter.getPosition(), 8);
        
        readerWriter.writeDouble(value, EndianType.LITTLE_ENDIAN_TYPE);        
        assertEquals(readerWriter.getPosition(), 16);
                
        //close file
        readerWriter.close();
        assertEquals(f.length(), 16);
        
        //reopen and read doubles
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);
        
        assertEquals(readerWriter.getPosition(), 0);
        
        assertEquals(readerWriter.readDouble(EndianType.BIG_ENDIAN_TYPE), 
                value, 0.0);        
        assertEquals(readerWriter.getPosition(), 8);
        
        assertEquals(readerWriter.readDouble(
                EndianType.LITTLE_ENDIAN_TYPE), value, 0.0);
        assertEquals(readerWriter.getPosition(), 16);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                
    }    

    @Test
    public void testReadLine() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);

        //write text into file
        String text = "first line\nsecond line";
        readerWriter.writeASCII(text);
        
        //close file
        readerWriter.close();
        
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);

        String firstLine = readerWriter.readLine();
        String secondLine = readerWriter.readLine();
        
        assertEquals(firstLine, "first line");
        assertEquals(secondLine, "second line");
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                        
    }

    @Test
    public void testReadWord() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);

        //write text into file
        String text = "first line\nsecond line";
        readerWriter.writeASCII(text);
        
        //close file
        readerWriter.close();
        
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);

        String firstWord = readerWriter.readWord();
        String secondWord = readerWriter.readWord();
        String thirdWord = readerWriter.readWord();
        String fourthWord = readerWriter.readWord();
        String fifthWord = readerWriter.readWord(); //this is null because end 
                                                    //is reached

        assertEquals(firstWord, "first");
        assertEquals(secondWord, "line");
        assertEquals(thirdWord, "second");
        assertEquals(fourthWord, "line");
        assertNull(fifthWord);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());                                                
    }
 
    @Test
    public void testReadWriteUTF() throws FileNotFoundException, IOException{
        File f = new File("./src/test/java/readWriteFile");
        assertFalse(f.exists());
        
        
        FileReaderAndWriter readerWriter = new FileReaderAndWriter(
                f, FileChannel.MapMode.READ_WRITE);
        assertTrue(f.exists());        
        
        assertEquals(readerWriter.getPosition(), 0);

        //write text into file
        String text = "first line\nsecond line";
        readerWriter.writeUTF(text);
        
        //close file
        readerWriter.close();
        
        readerWriter = new FileReaderAndWriter(f, 
                FileChannel.MapMode.READ_ONLY);

        String text2 = readerWriter.readUTF();
        assertEquals(text, text2);
        
        //close and delete file
        readerWriter.close();        
        assertTrue(f.exists());
        assertTrue(f.delete());        
    }
}
