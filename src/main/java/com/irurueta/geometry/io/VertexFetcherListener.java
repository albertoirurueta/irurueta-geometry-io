/*
 * Copyright (C) 2011 Alberto Irurueta Carro (alberto@irurueta.com)
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
 * Interface defining method to be implemented for vertex fetchers.
 * Vertex fetchers must be able to move the stream position to the location
 * where requested so that a LoaderIterator can read vertex values.
 */
public interface VertexFetcherListener {

    /**
     * Moves current file position so that on next read requested vertex data
     * can be read.
     *
     * @param index index of vertex to be requested.
     * @throws LoaderException       raised if file is corrupted or unexpected data is
     *                               found.
     * @throws IOException           if an I/O exception occurs.
     * @throws NotAvailableException raised if requested vertex can't be found.
     */
    void fetch(final long index)
            throws LoaderException, IOException, NotAvailableException;
}
