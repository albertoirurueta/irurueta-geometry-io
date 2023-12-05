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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class provides methods to access file data at random positions using
 * memory mapping for faster data access.
 */
@SuppressWarnings("DuplicatedCode")
public class MappedFileReaderAndWriter extends AbstractFileReaderAndWriter {

    /**
     * Underlying input file.
     */
    private final RandomAccessFile randomAccessFile;

    /**
     * Buffer where data is stored.
     */
    private final MappedByteBuffer buffer;

    /**
     * Internal value indicating if read has been produced.
     */
    private boolean read;

    /**
     * Constructor.
     *
     * @param f    file to read from or write to.
     * @param mode file opening mode (read only or read write).
     * @throws IOException if an I/O error occurs .
     */
    public MappedFileReaderAndWriter(final File f, final FileChannel.MapMode mode)
            throws IOException {
        if (mode == FileChannel.MapMode.READ_ONLY) {
            this.randomAccessFile = new RandomAccessFile(f, "r");
        } else {
            this.randomAccessFile = new RandomAccessFile(f, "rw");
        }
        this.buffer = randomAccessFile.getChannel().map(mode, 0,
                randomAccessFile.length());
        this.buffer.load();
    }

    /**
     * Reads one byte at current file position and advances one position.
     *
     * @return Next byte of data or -1 if end of file is reached.
     * @throws IOException if an I/O error occurs. Not thrown if end-of-file has
     *                     been reached.
     */
    @Override
    public int read() throws IOException {
        if (isEndOfStream()) {
            return -1;
        }
        int value = buffer.get();
        value = ((value << 8) >> 8) & 0xff;
        read = true;
        return value;
    }

    /**
     * Reads up to b.length bytes of data from this file into an array of bytes.
     * This method blocks until at least one byte of input is available.
     *
     * @param b The buffer into which the data is read.
     * @return The total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of the file has been reached.
     * @throws IOException If the first byte cannot be read for any reason other
     *                     than end of file, or if the file has been closed, or if some other I/O
     *                     error occurs.
     */
    @Override
    public int read(final byte[] b) throws IOException {
        if (isEndOfStream()) {
            return -1;
        }
        final int length = Math.min(buffer.remaining(), b.length);
        if (length > 0) {
            buffer.get(b, 0, length);
        }
        read = true;
        return length;
    }

    /**
     * Reads up to len bytes of data from this file into an array of bytes. This
     * method blocks until at least one byte of input is available.
     * This method behaves in exactly the same way as the
     * InputStream.read(byte[], int, int) method of InputStream.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in array b at which the data is written.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     * no more data because the end of the file has been reached.
     * @throws IOException If the first byte cannot be read for any reason other
     *                     than end of file, or if the random access file has been closed, or if
     *                     some other I/O error occurs.
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (isEndOfStream()) {
            return -1;
        }
        final int length = Math.min(buffer.remaining(), len);
        if (length > 0) {
            buffer.get(b, off, length);
        }
        read = true;
        return length;
    }

    /**
     * Attempts to skip over n byte of input discarding the skipped bytes.
     * <p>
     * This method may skip over some number of bytes, possibly zero. This may
     * result from any of a number of conditions; reaching end of file before n
     * bytes have been skipped is only one possibility. The actual number of
     * bytes skipped is returned. If n is negative, no bytes are skipped.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     */
    @Override
    public long skip(final long n) {
        if (n < 0) {
            return 0;
        }

        final int skipped = Math.min(buffer.remaining(), (int) n);
        final int newPos = buffer.position() + skipped;
        buffer.position(newPos);
        read = true;
        return skipped;
    }

    /**
     * Writes the specified byte to this file. The write starts at the current
     * file pointer.
     *
     * @param b the byte to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final int b) throws IOException {
        randomAccessFile.write(b);
        read = false;
    }

    /**
     * Writes b.length bytes from the specified byte array to this file,
     * starting at the current file pointer.
     *
     * @param b the data.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final byte[] b) throws IOException {
        randomAccessFile.write(b);
        read = false;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off to
     * this file.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(final byte[] b, final int off, final int len)
            throws IOException {
        randomAccessFile.write(b, off, len);
        read = false;
    }

    /**
     * Returns the current offset in this file.
     *
     * @return the offset from the beginning of the file, in bytes, at which the
     * next read or write occurs.
     * @throws IOException in an I/O error occurs.
     */
    @Override
    public long getPosition() throws IOException {
        if (read) {
            return buffer.position();
        } else {
            return randomAccessFile.getFilePointer();
        }
    }

    /**
     * Determines whether end of file has been reached (next read() will return
     * -1). or not.
     *
     * @return True if end of file has been reached, false otherwise.
     * @throws IOException if an I/O error occurs..
     */
    @Override
    public boolean isEndOfStream() throws IOException {
        return getPosition() >= randomAccessFile.length();
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs. Setting the offset beyond the end
     * of the file will raise an exception.
     *
     * @param pos the offset position, measured in bytes from the beginning of
     *            the file, at which to set the file pointer.
     */
    @Override
    public void seek(final long pos) {
        buffer.position((int) pos);
        read = true;
    }

    /**
     * Closes this file stream and releases any system resources associated with
     * the stream. A closed file cannot perform input or output operations and
     * cannot be reopened.
     * If this file has an associated channel then the channel is closed as
     * well.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        buffer.force();
        randomAccessFile.close();
    }

    /**
     * Reads a boolean from this file. This method reads a single byte from the
     * file, starting at the current file pointer. A value of 0 represents
     * false. Any other value represents true. This method blocks until the byte
     * is read, the end of the stream is detected, or an exception is thrown.
     *
     * @return the boolean value read.
     */
    @Override
    public boolean readBoolean() {
        read = true;
        return buffer.get() != 0;
    }

    /**
     * Reads a signed eight-bit value from this file. This method reads a byte
     * from the file, starting from the current file pointer. If the byte read
     * is b, where 0 &lt;= b &lt;= 255, then the result is: (byte)(b)
     * This method blocks until the byte is read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return the next byte of this file is a signed eight-bit byte.
     */
    @Override
    public byte readByte() {
        read = true;
        return buffer.get();
    }

    /**
     * Reads an unsigned eight-bit number from this file. This method reads a
     * byte from this file, starting at the current file pointer, and returns
     * that byte.
     * This method blocks until the byte is read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return the next byte of this file, interpreted as an unsigned eight-bit
     * number.
     */
    @Override
    public short readUnsignedByte() {
        read = true;
        return (short) (((buffer.get() << 8) >> 8) & 0xff);
    }

    /**
     * Reads a signed 16-bit number from this file. The method reads two byte
     * from this file, starting at the current file pointer. If the two bytes
     * read, in order, are b1 and b2, where each of the two values is between 0
     * and 255, inclusive, then the result is equal to: (short)(b1 &lt;&lt; 8 |
     * b2).
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a signed 16-bit
     * number.
     */
    @Override
    public short readShort() {
        read = true;
        return buffer.getShort();
    }

    /**
     * Reads a signed 16-bit number from this file assuming that file is encoded
     * using provided endian type. If endian type is big endian type, then
     * natural binary order is preserved, otherwise byte order is reversed.
     * This method blocks until the two bytes of the 16-bit number are read, the
     * end of the stream is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next two bytes of this file, interpreted as a signed 16-bit
     * number encoded in provided endian type.
     */
    @Override
    public short readShort(final EndianType endianType) {
        read = true;
        return Util.fromEndianType(endianType, buffer.getShort());
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads two
     * bytes from this file, starting at the current file pointer. If the bytes
     * read, in order, are b1 and b2, where 0 &lt;= b1, b2 &lt;= 255, then the
     * result is equal to: (b1 &lt;&lt; 8) | b2
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as an unsigned
     * 16-bit integer.
     */
    @Override
    public int readUnsignedShort() {
        read = true;
        final short value = buffer.getShort();

        // convert value to byte array
        final int firstShortByte = 0xff & (value >> 8);
        final int secondShortByte = 0xff & ((value << 8) >> 8);

        // return it as integer
        return 0xffff & ((firstShortByte << 8) |
                secondShortByte);
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads two
     * bytes from this file, starting at the current file pointer and using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next two bytes of this file, interpreted as an unsigned
     * 16-bit integer.
     */
    @Override
    public int readUnsignedShort(final EndianType endianType) {
        read = true;
        final short streamValue = buffer.getShort();
        final short value = Util.fromEndianType(endianType, streamValue);

        // convert value to byte array
        final int firstShortByte = 0xff & (value >> 8);
        final int secondShortByte = 0xff & ((value << 8) >> 8);

        // return it as integer
        return 0xffff & ((firstShortByte << 8) |
                secondShortByte);
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer. If the bytes read,
     * in order, are b1, b2, b3, and b4, where 0 &lt;= b1, b3, b4 &lt;= 255, then
     * the result is equal to: (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) +
     * b4.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as an int.
     */
    @Override
    public int readInt() {
        read = true;
        return buffer.getInt();
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer and using provided
     * endian type. If endian type is big endian, then natural binary order is
     * preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as an int.
     */
    @Override
    public int readInt(final EndianType endianType) {
        read = true;
        return Util.fromEndianType(endianType, buffer.getInt());
    }

    /**
     * Reads an unsigned 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer. If the bytes
     * read, in order, are b1, b2, b3, and b4, where 0 &lt;= b1, b3, b4 &lt;= 255,
     * then the result is equal to: (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) +
     * (b3 &lt;&lt; 8) + b4.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as a long.
     */
    @Override
    public long readUnsignedInt() {
        read = true;
        final int value = buffer.getInt();

        // convert value to byte array
        final int firstIntByte = 0xff & (value >> 24);
        final int secondIntByte = 0xff & ((value << 8) >> 24);
        final int thirdIntByte = 0xff & ((value << 16) >> 24);
        final int fourthIntByte = 0xff & ((value << 24) >> 24);

        // return it as integer
        return ((long) firstIntByte << 24) |
                (secondIntByte << 16) |
                (thirdIntByte << 8) |
                fourthIntByte;
    }

    /**
     * Reads an unsigned 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer and using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as an int.
     */
    @Override
    public long readUnsignedInt(final EndianType endianType) {
        read = true;
        final int streamValue = buffer.getInt();
        final int value = Util.fromEndianType(endianType, streamValue);

        // convert value to byte array
        final int firstIntByte = 0xff & (value >> 24);
        final int secondIntByte = 0xff & ((value << 8) >> 24);
        final int thirdIntByte = 0xff & ((value << 16) >> 24);
        final int fourthIntByte = 0xff & ((value << 24) >> 24);

        // return it as integer
        return ((long) firstIntByte << 24) |
                (secondIntByte << 16) |
                (thirdIntByte << 8) |
                fourthIntByte;
    }

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
     *
     * @return the next eight bytes of this file, interpreted as a long
     */
    @Override
    public long readLong() {
        read = true;
        return buffer.getLong();
    }

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer and using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next eight bytes of this file, interpreted as a long
     */
    @Override
    public long readLong(final EndianType endianType) {
        read = true;
        return Util.fromEndianType(endianType, buffer.getLong());
    }

    /**
     * Reads a float from this file. This method reads an int value, starting at
     * the current file pointer, as if by the readInt method and then converts
     * that in to a float using the intBitsToFloat method in class Float.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as a float.
     */
    @Override
    public float readFloat() {
        read = true;
        return buffer.getFloat();
    }

    /**
     * Reads a float from this file. This method reads four bytes using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next four bytes of this file, interpreted as a float.
     */
    @Override
    public float readFloat(final EndianType endianType) {
        read = true;
        return Util.fromEndianType(endianType, buffer.getFloat());
    }

    /**
     * Reads a double from this file. This method reads a long value, starting
     * at the current file pointer, as if by the readLong method and then
     * converts that long to a double using the longBitsToDouble method in class
     * Double.
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a double.
     */
    @Override
    public double readDouble() {
        read = true;
        return buffer.getDouble();
    }

    /**
     * Reads a double from this file. This method reads eight bytes using
     * provided endian type. If endian type is big endian, then natural binary
     * order is preserved, otherwise byte order is reversed.
     * This method blocks until the eight bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @param endianType Endian type. Big endian preserves natural binary order,
     *                   little endian reverses byte order.
     * @return the next eight bytes of this file, interpreted as a double.
     */
    @Override
    public double readDouble(final EndianType endianType) {
        read = true;
        return Util.fromEndianType(endianType, buffer.getDouble());
    }

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
     *
     * @return the next line of text from this file, or null if end of file is
     * encountered before even one byte is read.
     */
    @Override
    public String readLine() {
        read = true;
        if (buffer.hasRemaining()) {
            return readUntilAnyOfTheseCharactersIsFound("\n");
        } else {
            return null;
        }
    }

    /**
     * Sequentially reads characters starting at current file position until one
     * of the characters in provided pattern is found.
     * All characters read so far will be returned without including any of the
     * pattern characters.
     *
     * @param pattern Stop characters to stop reading when they are found.
     * @return String read so far until any of the pattern characters was found
     * or an empty string if the first character is contained in provided
     * pattern.
     * @throws IllegalArgumentException if no pattern characters are provided.
     */
    @Override
    public String readUntilAnyOfTheseCharactersIsFound(final String pattern) {

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException();
        }

        read = true;

        final StringBuilder stringBuffer = new StringBuilder();
        final byte[] charBuffer = new byte[1];
        String character;
        while (buffer.hasRemaining()) {
            charBuffer[0] = buffer.get();
            character = new String(charBuffer);
            if (pattern.contains(character)) {
                // character found
                break;
            }
            // add character to output buffer
            stringBuffer.append(character);
        }

        return stringBuffer.toString();
    }

    /**
     * Writes a boolean to the file as a one-byte value. The value true is
     * written out as the value (byte)1; the value false is written out as the
     * value (byte)0. The write starts at the current position of the file
     * pointer.
     *
     * @param v a boolean value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        randomAccessFile.writeBoolean(v);
        read = false;
    }

    /**
     * Writes a byte to the file as a one-byte value. The write starts at the
     * current position of the file pointer.
     *
     * @param v a byte value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeByte(final byte v) throws IOException {
        randomAccessFile.writeByte(v);
        read = false;
    }

    /**
     * Writes provided value in the range 0-255 as an unsigned byte. The write
     * starts at the current position of the file pointer.
     *
     * @param v a value to be written as an unsigned byte.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUnsignedByte(final short v) throws IOException {
        final byte data = (byte) (0xff & v);
        randomAccessFile.writeByte(data);
        read = false;
    }

    /**
     * Writes a short to the file as two bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v a short to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeShort(final short v) throws IOException {
        randomAccessFile.writeShort(v);
        read = false;
    }

    /**
     * Writes a short to the file as two bytes using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed
     *
     * @param v          a short to be written
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeShort(final short v, final EndianType endianType)
            throws IOException {
        final short value = Util.toEndianType(endianType, v);
        randomAccessFile.writeShort(value);
        read = false;
    }

    /**
     * Writes an unsigned short to the file as two bytes, high byte first.
     * Provided integer value is converted to an unsigned short by taking into
     * account only the two lower bytes. The write starts at the current
     * position of the file pointer.
     *
     * @param v an unsigned short to be written (int is converted to unsigned
     *          short).
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUnsignedShort(final int v) throws IOException {
        final int firstShortByte = 0xff & (v >> 8);
        final int secondShortByte = 0xff & ((v << 8) >> 8);

        final short value = (short) ((firstShortByte << 8) |
                secondShortByte);

        randomAccessFile.writeShort(value);
        read = false;
    }

    /**
     * Writes an unsigned short to the file as two bytes, using provided endian
     * type.
     * Provided integer value is converted to an unsigned short by taking into
     * account only the two lower bytes.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed
     * The write starts at the current position of the file pointer.
     *
     * @param v          an unsigned short to be written (int is converted to unsigned
     *                   short).
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUnsignedShort(final int v, final EndianType endianType)
            throws IOException {
        final int firstShortByte = 0xff & (v >> 8);
        final int secondShortByte = 0xff & ((v << 8) >> 8);

        final short machineValue = (short) ((firstShortByte << 8) |
                secondShortByte);
        final short value = Util.toEndianType(endianType, machineValue);

        randomAccessFile.writeShort(value);
        read = false;
    }

    /**
     * Writes an int to the file as four bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v an int to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeInt(final int v) throws IOException {
        randomAccessFile.writeInt(v);
        read = false;
    }

    /**
     * Writes an int to the file as four bytes, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     *
     * @param v          an int to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeInt(final int v, final EndianType endianType) throws IOException {
        final int value = Util.toEndianType(endianType, v);
        randomAccessFile.writeInt(value);
        read = false;
    }

    /**
     * Writes an unsigned int to the file as four bytes, high byte first.
     * Provided integer value is converted to an unsigned int by taking into
     * account only the four lower bytes. The write starts at the current
     * position of the file pointer.
     *
     * @param v an unsigned int to be written (long is converted to unsigned
     *          int).
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUnsignedInt(final long v) throws IOException {
        final int firstIntByte = (int) (0xff & (v >> 24));
        final int secondIntByte = (int) (0xff & ((v << 8) >> 24));
        final int thirdIntByte = (int) (0xff & ((v << 16) >> 24));
        final int fourthIntByte = (int) (0xff & ((v << 24) >> 24));

        final int value = (firstIntByte << 24) |
                (secondIntByte << 16) |
                (thirdIntByte << 8) |
                fourthIntByte;

        randomAccessFile.writeInt(value);
        read = false;
    }

    /**
     * Writes an unsigned int to the file as four bytes, using provided endian
     * type.
     * Provided integer value is converted to an unsigned int by taking into
     * account only the four lower bytes.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     *
     * @param v          an unsigned int to be written (long is converted to unsigned
     *                   short).
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUnsignedInt(final long v, final EndianType endianType)
            throws IOException {
        final int firstIntByte = (int) (0xff & (v >> 24));
        final int secondIntByte = (int) (0xff & ((v << 8) >> 24));
        final int thirdIntByte = (int) (0xff & ((v << 16) >> 24));
        final int fourthIntByte = (int) (0xff & ((v << 24) >> 24));

        final int machineValue = (firstIntByte << 24) |
                (secondIntByte << 16) |
                (thirdIntByte << 8) |
                fourthIntByte;
        final int value = Util.toEndianType(endianType, machineValue);

        randomAccessFile.writeInt(value);
        read = false;
    }

    /**
     * Writes a long to the file as eight bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v a long to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeLong(final long v) throws IOException {
        randomAccessFile.writeLong(v);
        read = false;
    }

    /**
     * Writes a long to the file as eight bytes, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     *
     * @param v          a long to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeLong(final long v, final EndianType endianType)
            throws IOException {
        final long value = Util.toEndianType(endianType, v);
        randomAccessFile.writeLong(value);
        read = false;
    }

    /**
     * Converts the float argument to an int using the floatToIntBits method in
     * class Float, and then write that int value to the file as a four-byte
     * quantity, high byte first. The write starts at the current position of
     * the file pointer.
     *
     * @param v a float value to be written
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeFloat(final float v) throws IOException {
        randomAccessFile.writeFloat(v);
        read = false;
    }

    /**
     * Converts the float argument to an int using the floatToIntBits method in
     * class Float, and then write that int value to the file as a four-byte
     * quantity, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     *
     * @param v          a float value to be written
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeFloat(final float v, final EndianType endianType)
            throws IOException {
        final float value = Util.toEndianType(endianType, v);
        randomAccessFile.writeFloat(value);
        read = false;
    }

    /**
     * Converts the double argument to a long using the doubleToLongBits method
     * in class Double, and then writes that long value to the file as an eight
     * byte quantity, high byte first. The write starts at the current position
     * of the file pointer.
     *
     * @param v a double value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeDouble(final double v) throws IOException {
        randomAccessFile.writeDouble(v);
        read = false;
    }

    /**
     * Converts the double argument to a long using the doubleToLongBits method
     * in class Double, and then writes that long value to the file as an eight
     * byte quantity, using provided endian type.
     * If endian type is big endian, then natural byte order is preserved (and
     * high byte is written first), if little endian order is chosen, then byte
     * order is reversed.
     * The write starts at the current position of the file pointer.
     *
     * @param v          a double value to be written.
     * @param endianType endian type. If it is big endian, natural byte order is
     *                   preserved, otherwise byte order is reversed.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeDouble(final double v, final EndianType endianType)
            throws IOException {
        final double value = Util.toEndianType(endianType, v);
        randomAccessFile.writeDouble(value);
        read = false;
    }

    /**
     * Writes the string to the file as a sequence of bytes. Each character in
     * the string is written out, in sequence, by discarding its high eight
     * bits. The write starts at the current position of the file pointer.
     *
     * @param s a string of bytes to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeASCII(final String s) throws IOException {
        randomAccessFile.writeBytes(s);
        read = false;
    }
}
