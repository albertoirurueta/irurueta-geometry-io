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

import java.nio.ByteBuffer;

/**
 * Listener to read a value contained within the byte read buffer and transform
 * it into the appropriate data type.
 */
public interface PLYReadValueFromBufferListener {

    /**
     * Reads a value from the byte read buffer.
     *
     * @param buffer Byte buffer.
     */
    void readValueFromBuffer(final ByteBuffer buffer);
}
