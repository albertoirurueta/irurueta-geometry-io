/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.PLYStorageMode
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 24, 2012
 */
package com.irurueta.geometry.io;

/**
 * Enumerator containing different storage modes supported for PLY files.
 */
public enum PLYStorageMode {
    /**
     * Binary big-endian. When using big-endian natural binary order of data is
     * preserved (high bytes come first).
     */
    PLY_BIG_ENDIAN,
    
    /**
     * Binary little-endian. When using little-endian natural binary order of
     * data is reversed (low bytes come first).
     */
    PLY_LITTLE_ENDIAN,
    
    /**
     * ASCII text. Data is stored in readable text format. This format might 
     * loose precision for floating point data.
     */
    PLY_ASCII
}
