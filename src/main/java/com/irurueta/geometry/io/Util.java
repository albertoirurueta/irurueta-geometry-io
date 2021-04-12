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
 * Utility methods for I/O operations.
 */
public class Util {

    /**
     * Constructor.
     * Prevents instantiation.
     */
    private Util() {
    }

    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect.
     *
     * @param value Value to be converted to big endian.
     * @return Converted value.
     */
    static short toBigEndian(final short value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is
     * reversed.
     *
     * @param value Value to be converted to little endian.
     * @return Converted value.
     */
    static short toLittleEndian(final short value) {
        return Short.reverseBytes(value);
    }

    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big
     * endian puts high bytes first (following natural binary order).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static short toEndianType(final EndianType endianType, final short value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return toLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts value from big endian to machine native endian type (in Java
     * native endian type is always Big endian, for that reason this method has
     * no effect).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static short fromBigEndian(final short value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static short fromLittleEndian(short value) {
        return Short.reverseBytes(value);
    }

    /**
     * Converts provided value from provided endian type to machine native
     * endian type (in Java native endian type is always big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static short fromEndianType(final EndianType endianType, final short value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect.
     *
     * @param value Value to be converted to big endian.
     * @return Converted value.
     */
    static int toBigEndian(int value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is
     * reversed.
     *
     * @param value Value to be converted to little endian.
     * @return Converted value.
     */
    static int toLittleEndian(int value) {
        return Integer.reverseBytes(value);
    }

    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big
     * endian puts high bytes first (following natural binary order).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static int toEndianType(final EndianType endianType, final int value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return toLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts value from big endian to machine native endian type (in Java
     * native endian type is always Big endian, for that reason this method has
     * no effect).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static int fromBigEndian(final int value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static int fromLittleEndian(final int value) {
        return Integer.reverseBytes(value);
    }

    /**
     * Converts provided value from provided endian type to machine native
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static int fromEndianType(final EndianType endianType, final int value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect.
     *
     * @param value Value to be converted to big endian.
     * @return Converted value.
     */
    static long toBigEndian(final long value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is
     * reversed.
     *
     * @param value Value to be converted to little endian.
     * @return Converted value.
     */
    static long toLittleEndian(final long value) {
        return Long.reverseBytes(value);
    }

    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big
     * endian puts high bytes first (following natural binary order).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static long toEndianType(final EndianType endianType, final long value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return toLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts value from big endian to machine native endian type (in Java
     * native endian type is always Big endian, for that reason this method has
     * no effect).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static long fromBigEndian(final long value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static long fromLittleEndian(final long value) {
        return Long.reverseBytes(value);
    }

    /**
     * Converts provided value from provided endian type to machine native
     * endian type (in Java native endian type is always big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static long fromEndianType(final EndianType endianType, final long value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect.
     *
     * @param value Value to be converted to big endian.
     * @return Converted value.
     */
    static float toBigEndian(final float value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is
     * reversed.
     *
     * @param value Value to be converted to little endian.
     * @return Converted value.
     */
    static float toLittleEndian(final float value) {
        return Float.intBitsToFloat(Integer.reverseBytes(
                Float.floatToRawIntBits(value)));
    }

    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big
     * endian puts high bytes first (following natural binary order).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static float toEndianType(final EndianType endianType, final float value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return toLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts value from big endian to machine native endian type (in Java
     * native endian type is always Big endian, for that reason this method has
     * no effect).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static float fromBigEndian(final float value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static float fromLittleEndian(final float value) {
        return Float.intBitsToFloat(Integer.reverseBytes(
                Float.floatToRawIntBits(value)));
    }

    /**
     * Converts provided value from provided endian type to machine native
     * endian type (in Java native endian type is always big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static float fromEndianType(final EndianType endianType, final float value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect.
     *
     * @param value Value to be converted to big endian.
     * @return Converted value.
     */
    static double toBigEndian(final double value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is
     * reversed.
     *
     * @param value Value to be converted to little endian.
     * @return Converted value.
     */
    static double toLittleEndian(final double value) {
        return Double.longBitsToDouble(Long.reverseBytes(
                Double.doubleToRawLongBits(value)));
    }

    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big
     * endian puts high bytes first (following natural binary order).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static double toEndianType(final EndianType endianType, final double value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return toLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

    /**
     * Converts value from big endian to machine native endian type (in Java
     * native endian type is always Big endian, for that reason this method has
     * no effect).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static double fromBigEndian(final double value) {
        // Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     *
     * @param value Value to be converted.
     * @return Converted value.
     */
    static double fromLittleEndian(final double value) {
        return Double.longBitsToDouble(Long.reverseBytes(
                Double.doubleToRawLongBits(value)));
    }

    /**
     * Converts provided value from provided endian type to machine native
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     *
     * @param endianType Endian type.
     * @param value      Value to be converted.
     * @return Converted value.
     */
    static double fromEndianType(final EndianType endianType, final double value) {
        switch (endianType) {
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }

}
