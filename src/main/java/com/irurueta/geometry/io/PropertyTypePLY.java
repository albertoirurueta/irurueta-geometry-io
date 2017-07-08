/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.PropertyTypePLY
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date September 25, 2012
 */
package com.irurueta.geometry.io;

/**
 * Enumeration defining supported property types
 */
public enum PropertyTypePLY {
    /**
     * Scalar type for single valued elements, such as vertex coordinates
     */
    PROPERTY_PLY_SCALAR,
        
    /**
     * List type for array valued elements, such as faces containing vertex
     * indexes forming polygons.
     */    
    PROPERTY_PLY_LIST    
}
