/**
 * This package contains classes to read and transcode 3D files in OBJ, STL or
 * PLY formats.
 * 
 * Support for custom binary and JSON formats is also provided
 * 
 * A file can be read in any of the supported formats using the required Loader
 * implementation. A loader simply allows reading a file iteratively in small
 * chunks.
 * 
 * Writer implementations allow reading a 3D file using a Loader and write it
 * to provided output stream using the format implemented by the specific Writer
 * being used (i.e. JSON, binary, etc)
 */
package com.irurueta.geometry.io;
