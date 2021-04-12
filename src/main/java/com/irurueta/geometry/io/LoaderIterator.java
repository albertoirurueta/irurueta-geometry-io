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

/**
 * Defines the interface to iterate on the loading process of a file.
 */
public interface LoaderIterator {

    /**
     * Returns boolean indicating if there is more data to be read.
     *
     * @return True if there are more chunks to be read, false otherwise.
     */
    boolean hasNext();

    /**
     * Reads next chunk of data on the file.
     *
     * @return Chunk of data containing vertices, indices, colors, textures,
     * etc.
     * @throws NotAvailableException Raised if no more chunks are available.
     * @throws LoaderException       Raised if file cannot be read because it is
     *                               either corrupted or cannot be interpreted.
     * @throws IOException           if an I/O error occurs.
     */
    DataChunk next() throws NotAvailableException, LoaderException, IOException;
}
