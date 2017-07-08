/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderListenerOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 13, 2012
 */
package com.irurueta.geometry.io;

public interface LoaderListenerOBJ extends LoaderListener{
    
    //Returns a material loader if one is found, or null otherwise
    public MaterialLoaderOBJ onMaterialLoaderRequested(LoaderOBJ loader, 
            String path);
}
