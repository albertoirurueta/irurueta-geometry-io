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
