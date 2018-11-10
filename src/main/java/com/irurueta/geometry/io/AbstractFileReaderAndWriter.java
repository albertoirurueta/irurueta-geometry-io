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

import java.io.IOException;

/**
 * Abstract class that provides methods to access file data at random positions.
 */
public abstract class AbstractFileReaderAndWriter {
    
    /**
     * Method to be called when this instance is garbage collected.
     * @throws Throwable if anything fails.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            close(); //attempt to close file resources
        } catch (Throwable ignore) { }
    }
    
    
    /**
     * Reads one byte at current file position and advances one position.
     * @return Next byte of data or -1 if end of file is reached.
     * @throws IOException if an I/O error occurs. Not thrown if end-of-file has
     * been reached.
     */    
    public abstract int read() throws IOException;
    
    /**
     * Reads up to b.length bytes of data from this file into an array of bytes.
     * This method blocks until at least one byte of input is available.
     * @param b The buffer into which the data is read.
     * @return The total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of the file has been reached.
     * @throws IOException If the first byte cannot be read for any reason other
     * than end of file, or if the file has been closed, or if some other I/O
     * error occurs.
     */
    public abstract int read(byte[] b) throws IOException;
    
    /**
     * Reads up to len bytes of data from this file into an array of bytes. This
     * method blocks until at least one byte of input is available.
     * This method behaves in exactly the same way as the 
     * InputStream.read(byte[], int, int) method of InputStream.
     * @param b the buffer into which the data is read.
     * @param off the start offset in array b at which the data is written.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of the file has been reached.
     * @throws IOException If the first byte cannot be read for any reason other
     * than end of file, or if the random access file has been closed, or if 
     * some other I/O error occurs.
     */
    public abstract int read(byte[] b, int off, int len) throws IOException;
        
    /**
     * Attempts to skip over n byte of input discarding the skipped bytes.
     * 
     * This method may skip over some number of bytes, possibly zero. This may
     * result from any of a number of conditions; reaching end of file before n
     * bytes have been skipped is only one possibility. The actual number of 
     * bytes skipped is returned. If n is negative, no bytes are skipped.
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    public abstract long skip(long n) throws IOException;
    
    /**
     * Writes the specified byte to this file. The write starts at the current
     * fiel pointer.
     * @param b the byte to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void write(int b) throws IOException;
    
    /**
     * Writes b.length bytes from the specified byte array to this file, 
     * starting at the current file pointer.
     * @param b the data.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void write(byte[] b) throws IOException;    
    
    /**
     * Writes len bytes from the specified byte array starting at offset off to
     * this file.
     * @param b the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void write(byte[] b, int off, int len) throws IOException;    
    
    /**
     * Returns the current offset in this file.
     * @return the offset from the beginning of the file, in bytes, at which the
     * next read or write occurs.
     * @throws IOException in an I/O error occurs.
     */
    public abstract long getPosition() throws IOException;    
    
    /**
     * Determines whether end of file has been reached (next read() will return 
     * -1). or not.
     * @return True if end of file has been reached, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public abstract boolean isEndOfStream() throws IOException;
    
    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs. The offset may be set beyond the
     * end of the file. Setting the offset beyond the end of the file does not
     * change the file length.
     * The file length will change only by writing after the offset has been set
     * beyond the end of the file.
     * @param pos the offset position, measured in bytes from the beginning of
     * the file, at which to set the file pointer.
     * @throws IOException if pos is less than 0 or if an I/O error occurs.
     */
    public abstract void seek(long pos) throws IOException;
    
    /**
     * Closes this file stream and releases any system resources associated with
     * the stream. A closed file cannot perform input or output operations and
     * cannot be reopened.
     * If this file has an associated channel then the channel is closed as 
     * well.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void close() throws IOException;
    
    /**
     * Reads a boolean from this file. This method reads a single byte from the
     * file, starting at the current file pointer. A value of 0 represents 
     * false. Any other value represents true. This method blocks until the byte
     * is read, the end of the stream is detected, or an exception is thrown.
     * @return the boolean value read.
     * @throws IOException if an I/O error occurs.
     */
    public abstract boolean readBoolean() throws IOException;
    
    /**
     * Reads a signed eight-bit value from this file. This method reads a byte 
     * from the file, starting from the current file pointer. If the byte read 
     * is b, where 0 &lt;= b &lt;= 255, then the result is: (byte)(b)
     * This method blocks until the byte is read, the end of the stream is
     * detected, or an exception is thrown.
     * @return the next byte of this file is a signed eight-bit byte.
     * @throws IOException if an I/O error occurs.
     */
    public abstract byte readByte() throws IOException;
    
    /**
     * Reads an unsigned eight-bit number from this file. This method reads a
     * byte from this file, starting at the current file pointer, and returns
     * that byte.
     * This method blocks until the byte is read, the end of the stream is
     * detected, or an exception is thrown.
     * @return the next byte of this file, interpreted as an unsigned eight-bit
     * number.
     * @throws IOException if an I/O error occurs.
     */
    public abstract short readUnsignedByte() throws IOException;
    
    /**
     * Reads a signed 16-bit number from this file. The method reads two byte 
     * from this file, starting at the current file pointer. If the two bytes
     * read, in order, are b1 and b2, where each of the two values is between 0 
     * and 255, inclusive, then the result is equal to: (short)((b1 &lt;&lt; 8 |
     * b2).
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     * @return the next two bytes of this file, interpreted as a signed 16-bit 
     * number.
     * @throws IOException if an I/O error occurs.
     */
    public abstract short readShort() throws IOException;    
    
    /**
     * Reads a signed 16-bit number from this file assuming that file is encoded
     * using provided endian type. If endian type is big endian type, then 
     * natural binary order is preserved, otherwise byte order is reversed.
     * This method blocks until the two bytes of the 16-bit number are read, the
     * end of the stream is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next two bytes of this file, interpreted as a signed 16-bit
     * number encoded in provided endian type.
     * @throws IOException if an I/O error occurs.
     */
    public abstract short readShort(EndianType endianType) throws IOException;
    
    /**
     * Reads an unsigned 16-bit number from this file. This method reads two 
     * bytes from this file, starting at the current file pointer. If the bytes
     * read, in order, are b1 and b2, where 0 &lt;= b1, b2 &lt;= 255, then the
     * result is equal to: (b1 &lt;&lt; 8) | b2
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     * @return the next two bytes of this file, interpreted as an unsigned 
     * 16-bit integer.
     * @throws IOException if an I/O error occurs.
     */
    public abstract int readUnsignedShort() throws IOException;
    
    /**
     * Reads an unsigned 16-bit number from this file. This method reads two
     * bytes from this file, starting at the current file pointer and using 
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next two bytes of this file, interpreted as an unsigned 
     * 16-bit integer.
     * @throws IOException if an I/O error occurs.
     */
    public abstract int readUnsignedShort(EndianType endianType) 
            throws IOException;
    
    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer. If the bytes read,
     * in order, are b1, b2, b3, and b4, where 0 &lt;= b1, b3, b4 &lt;= 255, then
     * the result is equal to: (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) +
     * b4.
     * This method blocks until the four bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @return the next four bytes of this file, interpreted as an int.
     * @throws IOException if an I/O error occurs.
     */
    public abstract int readInt() throws IOException;
    
    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer and using provided 
     * endian type. If endian type is big endian, then natural binary order is 
     * preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as an int.
     * @throws IOException if an I/O error occurs.
     */
    public abstract int readInt(EndianType endianType) throws IOException;
    
    /**
     * Reads an unsigned 32-bit integer from this file. This method reads 4 
     * bytes from the file, starting at the current file pointer. If the bytes 
     * read, in order, are b1, b2, b3, and b4, where 0 &lt;= b1, b3, b4 &lt;= 255,
     * then the result is equal to: (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) +
     * (b3 &lt;&lt; 8) + b4
     * This method blocks until the four bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @return the next four bytes of this file, interpreted as a long.
     * @throws IOException if an I/O error occurs.
     */    
    public abstract long readUnsignedInt() throws IOException;
    
    /**
     * Reads an unsigned 32-bit integer from this file. This method reads 4 
     * bytes from the file, starting at the current file pointer and using 
     * provided endian type. If endian type is big endian, then natural binary 
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as an int.
     * @throws IOException if an I/O error occurs.
     */    
    public abstract long readUnsignedInt(EndianType endianType) 
            throws IOException;
    
    /**
     * Reads a signed 64-bit integer from this file. This method reads eight 
     * bytes from the file, starting at the current file pointer. If the bytes
     * read, in order, are b1, b2, b3, b4, b5, b6, b7, and b8, where:
     * 0 &lt;= b1, b2, b3, b4. b5. b6. b7. b8 &lt;= 255,
     * then the result is equal to:
     * ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     * + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     * + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     * + ((long)b7 &lt;&lt; 8) + b8
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     * @return the next eight bytes of this file, interpreted as a long.
     * @throws IOException if an I/O error occurs.
     */
    public abstract long readLong() throws IOException;
    
    /**
     * Reads a signed 64-bit integer from this file. This method reads eight 
     * bytes from the file, starting at the current file pointer and using 
     * provided endian type. If endian type is big endian, then natural binary 
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next eight bytes of this file, interpreted as a long
     * @throws IOException if an I/O error occurs.
     */
    public abstract long readLong(EndianType endianType) throws IOException;
    
    /**
     * Reads a float from this file. This method reads an int value, starting at
     * the current file pointer, as if by the readInt method and then converts
     * that in to a float using the intBitsToFloat method in class Float.
     * This method blocks until the four bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @return the next four bytes of this file, interpreted as a float.
     * @throws IOException if an I/O error occurs.
     */
    public abstract float readFloat() throws IOException;
    
    /**
     * Reads a float from this file. This method reads four bytes using 
     * provided endian type. If endian type is big endian, then natural binary 
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as a float.
     * @throws IOException if an I/O error occurs.
     */
    public abstract float readFloat(EndianType endianType) throws IOException;
    
    /**
     * Reads a double from this file. This method reads a long value, starting 
     * at the current file pointer, as if by the readLong method and then 
     * converts that long to a double using the longBitsToDouble method in class
     * Double.
     * This method blocks until the eight bytes are read, the end of the stream 
     * is detected, or an exception is thrown.
     * @return the next eight bytes of this file, interpreted as a double.
     * @throws IOException if an I/O error occurs.
     */
    public abstract double readDouble() throws IOException;
    
    /**
     * Reads a double from this file. This method reads eight bytes using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     * @param endianType Endian type. Big endian preserves natural binary order,
     * little endian reverses byte order.
     * @return the next eight bytes of this file, interpreted as a double.
     * @throws IOException if an I/O error occurs.
     */
    public abstract double readDouble(EndianType endianType) throws IOException;
    
    /**
     * Reads the next line of text from this file. This method successively 
     * reads bytes from the file, starting at the current file pointer, until it
     * reaches a line terminator of the end of the file. Each byte is converted
     * into a character by taking the byte's value for the lower eight bits of
     * the character and setting the high eight bits of the character to zero.
     * This method does not, therefore, support the full Unicode character set.
     * A line of text is terminated by a carriage-return character ('\r'), a
     * newline character('\n'), a carriage-return character immediately followed
     * by a newline character, or the end of the file. Line-terminating 
     * characters are discarded and are not included as part of the string 
     * returned.
     * This method blocks until a newline character is read, a carriage return
     * and the byte following it are read (to see if it is a newline), the end
     * of the file is reached, or an exception is thrown.
     * @return the next line of text from this file, or null if end of file is
     * encountered before even one byte is read.
     * @throws IOException if an I/O error occurs.
     */
    public abstract String readLine() throws IOException;
    
    /**
     * Sequentially reads characters starting at current file position until
     * either carriage return, new line, tab or space character is found.
     * The result is considered a word and it is returned.
     * @return Next work starting at current file position or null if end of
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("WeakerAccess")
    public String readWord() throws IOException{
        //reads until space, line feed, carriage return or tab is found
        if(isEndOfStream()) return null;
        else return readUntilAnyOfTheseCharactersIsFound(" \r\n\t");        
    }
        
    /**
     * Sequentially reads characters starting at current file position until one
     * of the characters in provided pattern is found.
     * All characters read so far will be returned without including any of the
     * pattern characters.
     * @param pattern Stop characters to stop reading when they are found.
     * @return String read so far until any of the pattern characters was found.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if no pattern characters are provided.
     */
    public abstract String readUntilAnyOfTheseCharactersIsFound(String pattern) 
            throws IOException, IllegalArgumentException;    
    
    /**
     * Writes a boolean to the file as a one-byte value. The value true is 
     * written out as the value (byte)1; the value false is written out as the
     * value (byte)0. The write starts at the current position of the file 
     * pointer.
     * @param v a boolean value to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeBoolean(boolean v) throws IOException;
    
    /**
     * Writes a byte to the file as a one-byte value. The write starts at the
     * current position of the file pointer.
     * @param v a byte value to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeByte(byte v) throws IOException;    
    
    /**
     * Writes provided value in the range 0-255 as an unsigned byte. The write
     * starts at the current position of the file pointer.
     * @param v a value to be written as an unsigned byte.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeUnsignedByte(short v) throws IOException;    
    
    /**
     * Writes a short to the file as two bytes, high byte first. The write 
     * starts at the current position of the file pointer.
     * @param v a short to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeShort(short v) throws IOException;    
    
    /**
     * Writes a short to the file as two bytes using provided enadian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * @param v a short to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeShort(short v, EndianType endianType) 
            throws IOException;    
    
    /**
     * Writes an unsigned short to the file as two bytes, high byte first. 
     * Provided integer value is converted to an unsigned short by taking into
     * account only the two lower bytes. The write starts at the current 
     * position of the file pointer.
     * @param v an unsigned short to be written (int is converted to unsigned 
     * short).
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeUnsignedShort(int v) throws IOException;
    
    /**
     * Writes an unsigned short to the file as two bytes, using provided endian
     * type. 
     * Provided integer value is converted to an unsigned short by taking into
     * account only the two lower bytes. 
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v an unsigned short to be written (int is converted to unsigned 
     * short).
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeUnsignedShort(int v, EndianType endianType) 
            throws IOException;    
    
    /**
     * Writes an int to the file as four bytes, high byte first. The write 
     * starts at the current position of the file pointer.
     * @param v an int to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeInt(int v) throws IOException;
    
    /**
     * Writes an int to the file as four bytes, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v an int to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeInt(int v, EndianType endianType) 
            throws IOException;
    
    /**
     * Writes an unsigned int to the file as four bytes, high byte first. 
     * Provided integer value is converted to an unsigned int by taking into
     * account only the four lower bytes. The write starts at the current 
     * position of the file pointer.
     * @param v an unsigned int to be written (long is converted to unsigned 
     * int).
     * @throws IOException if an I/O error occurs.
     */    
    public abstract void writeUnsignedInt(long v) throws IOException;
    
    /**
     * Writes an unsigned int to the file as four bytes, using provided endian
     * type. 
     * Provided integer value is converted to an unsigned int by taking into
     * account only the four lower bytes. 
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v an unsigned int to be written (long is converted to unsigned 
     * short).
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */    
    public abstract void writeUnsignedInt(long v, EndianType endianType) 
            throws IOException;    
    
    /**
     * Writes a long to the file as eight bytes, high byte first. The write 
     * starts at the current position of the file pointer.
     * @param v a long to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeLong(long v) throws IOException;
    
    /**
     * Writes a long to the file as eight bytes, using provided endian type. 
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v a long to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeLong(long v, EndianType endianType) 
            throws IOException;
    
    /**
     * Converts the float argument to an int using the floatToIntBits method in
     * class Float, and then write that int value to the file as a four-byte 
     * quantity, high byte first. The write starts at the current position of 
     * the file pointer.
     * @param v a float value to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeFloat(float v) throws IOException;
    
    /**
     * Converts the float argument to an int using the floatToIntBits method in
     * class Float, and then write that int value to the file as a four-byte 
     * quantity, using provided endian type. 
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v a float value to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeFloat(float v, EndianType endianType) 
            throws IOException;
    
    /**
     * Converts the double argument to a long using the doubleToLongBits method
     * in class Double, and then writes that long value to the file as an eight-
     * byte quantity, high byte first. The write starts at the current position
     * of the file pointer.
     * @param v a double value to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeDouble(double v) throws IOException;
    
    /**
     * Converts the double argument to a long using the doubleToLongBits method
     * in class Double, and then writes that long value to the file as an eight-
     * byte quantity, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     * @param v a double value to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     * preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeDouble(double v, EndianType endianType) 
            throws IOException;
    
    /**
     * Writes the string to the file as a sequence of bytes. Each character in
     * the string is written out, in sequence, by discarding its high eight 
     * bits. The write starts at the current position of the file pointer.
     * @param s a string of bytes to be written.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void writeASCII(String s) throws IOException;    
}
