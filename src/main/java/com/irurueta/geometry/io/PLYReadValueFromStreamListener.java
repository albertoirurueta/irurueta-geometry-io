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
import java.nio.ByteBuffer;

/**
 * Listener to read the appropriate amount of bytes from a PLY file 
 * corresponding to this data type. The amount of bytes read are stored within 
 * the read buffer.
 */
public interface PLYReadValueFromStreamListener {
    /**
     * Reads needed data from stream.
     * @param buffer Buffer where data is stored.
     * @throws IOException if an I/O error occurs.
     */
    void readFromStream(ByteBuffer buffer) throws IOException;
}
