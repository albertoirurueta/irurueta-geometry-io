/**
 * @file
 * This file contains definition of
 * com.irurueta.geometry.io
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 26, 2012
 */
package com.irurueta.geometry.io;

/**
 *  Utility methods for I/O operations
 */
public class Util {
    
    /**
     * Converts provided value to big endian.
     * NOTE: Java virtual machine always uses big endian values, so this method
     * has no effect
     * @param value Value to be converted to big endian
     * @return Converted value
     */
    static short toBigEndian(short value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is 
     * reversed.
     * @param value Value to be converted to little endian
     * @return Converted value
     */
    static short toLittleEndian(short value){
        return Short.reverseBytes(value);
    }
    
    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big 
     * endian puts high bytes first (following natural binary order)
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */
    static short toEndianType(EndianType endianType, short value){
        switch(endianType){
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
     * @param value Value to be converted
     * @return Converted value
     */
    static short fromBigEndian(short value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     * @param value Value to be converted
     * @return Converted value
     */
    static short fromLittleEndian(short value){
        return Short.reverseBytes(value);
    }
    
    /**
     * Converts provided value from provided endian type to machine native 
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */
    static short fromEndianType(EndianType endianType, short value){
        switch(endianType){
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
     * has no effect
     * @param value Value to be converted to big endian
     * @return Converted value
     */    
    static int toBigEndian(int value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is 
     * reversed.
     * @param value Value to be converted to little endian
     * @return Converted value
     */    
    static int toLittleEndian(int value){
        return Integer.reverseBytes(value);        
    }
    
    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big 
     * endian puts high bytes first (following natural binary order)
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static int toEndianType(EndianType endianType, int value){
        switch(endianType){
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
     * @param value Value to be converted
     * @return Converted value
     */    
    static int fromBigEndian(int value){
        //Java virtual machine always is big endian
        return value;
    }

    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     * @param value Value to be converted
     * @return Converted value
     */    
    static int fromLittleEndian(int value){
        return Integer.reverseBytes(value);        
    }
    
    /**
     * Converts provided value from provided endian type to machine native 
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static int fromEndianType(EndianType endianType, int value){
        switch(endianType){
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
     * has no effect
     * @param value Value to be converted to big endian
     * @return Converted value
     */    
    static long toBigEndian(long value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is 
     * reversed.
     * @param value Value to be converted to little endian
     * @return Converted value
     */    
    static long toLittleEndian(long value){
        return Long.reverseBytes(value);
    }
    
    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big 
     * endian puts high bytes first (following natural binary order)
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static long toEndianType(EndianType endianType, long value){
        switch(endianType){
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
     * @param value Value to be converted
     * @return Converted value
     */    
    static long fromBigEndian(long value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     * @param value Value to be converted
     * @return Converted value
     */    
    static long fromLittleEndian(long value){
        return Long.reverseBytes(value);
    }
    
    /**
     * Converts provided value from provided endian type to machine native 
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static long fromEndianType(EndianType endianType, long value){
        switch(endianType){
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
     * has no effect
     * @param value Value to be converted to big endian
     * @return Converted value
     */    
    static float toBigEndian(float value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is 
     * reversed.
     * @param value Value to be converted to little endian
     * @return Converted value
     */    
    static float toLittleEndian(float value){
        return Float.intBitsToFloat(Integer.reverseBytes(
                Float.floatToRawIntBits(value)));
    }
    
    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big 
     * endian puts high bytes first (following natural binary order)
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static float toEndianType(EndianType endianType, float value){
        switch(endianType){
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
     * @param value Value to be converted
     * @return Converted value
     */    
    static float fromBigEndian(float value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     * @param value Value to be converted
     * @return Converted value
     */    
    static float fromLittleEndian(float value){
        return Float.intBitsToFloat(Integer.reverseBytes(
                Float.floatToRawIntBits(value)));        
    }
    
    /**
     * Converts provided value from provided endian type to machine native 
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static float fromEndianType(EndianType endianType, float value){
        switch(endianType){
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
     * has no effect
     * @param value Value to be converted to big endian
     * @return Converted value
     */    
    static double toBigEndian(double value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts provided value to little endian.
     * Little endian puts high bytes last, or in other words, byte order is 
     * reversed.
     * @param value Value to be converted to little endian
     * @return Converted value
     */    
    static double toLittleEndian(double value){
        return Double.longBitsToDouble(Long.reverseBytes(
                Double.doubleToRawLongBits(value)));
    }
    
    /**
     * Converts provided value to provided endian type.
     * Little endian puts high bytes last (having reversed byte order), and big 
     * endian puts high bytes first (following natural binary order)
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static double toEndianType(EndianType endianType, double value){
        switch(endianType){
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
     * @param value Value to be converted
     * @return Converted value
     */    
    static double fromBigEndian(double value){
        //Java virtual machine always is big endian
        return value;
    }
    
    /**
     * Converts value from little endian to machine native endian type (in Java
     * native endian type is always Big endian).
     * Little endian puts high bytes last, so byte order has to be reversed to
     * convert to native machine endian type. Big endian preserves natural byte
     * order (high bytes first).
     * @param value Value to be converted
     * @return Converted value
     */    
    static double fromLittleEndian(double value){
        return Double.longBitsToDouble(Long.reverseBytes(
                Double.doubleToRawLongBits(value)));        
    }
    
    /**
     * Converts provided value from provided endian type to machine native 
     * endian type (in Java native endian type is always big endian).
     * Little endian puts hight bytes last, so byte order has to be reversed to
     * convert to native machine endian. Big endian preserves natural byte order
     * (high bytes first).
     * @param endianType Endian type
     * @param value Value to be converted
     * @return Converted value
     */    
    static double fromEndianType(EndianType endianType, double value){
        switch(endianType){
            case LITTLE_ENDIAN_TYPE:
                return fromLittleEndian(value);
            case BIG_ENDIAN_TYPE:
            default:
                return value;
        }
    }            
    
}
