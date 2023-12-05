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

import com.irurueta.geometry.InhomogeneousPoint3D;
import com.irurueta.geometry.Point3D;
import com.irurueta.geometry.Triangle3D;
import com.irurueta.geometry.Triangulator3D;
import com.irurueta.geometry.TriangulatorException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Loads PLY files.
 * This class is meant to read PLY files using an iterative process to read
 * the file in small pieces of data.
 * Because the file is loaded in small pieces, this class has a low memory
 * impact. Besides parameters such as maxVerticesInChunk or maxStreamPositions
 * can be adjusted in order to increase or reduce memory usage at the expense of
 * performance (the greater the memory usage the better the performance).
 * This class needs random access to file positions, and for that reason it
 * cannot be used with streams.
 * This class is based in the work of:
 * <a href="http://w3.impa.br/~diego/software/rply/">http://w3.impa.br/~diego/software/rply/</a>
 */
public class LoaderPLY extends Loader {
    /**
     * Size of internal buffer where bytes from stream of data read into.
     * This size (8 bytes) is meant to be able to fit any data type.
     *
     * @see DataTypePLY
     */
    public static final int BUFFER_SIZE = 8;

    /**
     * Constant defining maximum number of vertices to be stored in a single
     * data chunk.
     * By default, this is the maximum values stored in a short 65535. This is
     * so that data chunks can be compatible with technologies such as openGL
     * where vertex indices are short values, and hence only 65535 vertices can
     * be indexed at a time.
     */
    public static final int DEFAULT_MAX_VERTICES_IN_CHUNK = 0xffff;

    /**
     * Minimum allowed value for maximum vertices in a chunk. At least one
     * vertex must be contained on a data chunk, for that reason this constant
     * is 1.
     */
    public static final int MIN_MAX_VERTICES_IN_CHUNK = 1;

    /**
     * Constant defining if by default duplicate vertices are allowed in a data
     * chunk. By allowing duplicate vertices, PLY loading can be speed up a
     * little bit at the expense of getting larger sets of data which will
     * contain redundant vertices. If your environment is memory constrained,
     * this should be disabled. By default, it is disabled.
     */
    public static final boolean DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK =
            false;

    /**
     * Constant defining default maximum number of stream positions to be
     * cached.
     * This loader keeps track of a set of stream positions that have been
     * parsed on ASCII mode. By keeping a cache of positions loading times can
     * be largely reduced at the expense of using more memory during loading.
     * By default, this is set to 1000000 positions.
     * This only has effect on ASCII PLY files. For binary PLY files this
     * constant is ignored.
     */
    public static final int DEFAULT_MAX_STREAM_POSITIONS = 1000000;

    /**
     * Constant defining minimum allowed value for maximum stream positions.
     */
    public static final int MIN_STREAM_POSITIONS = 1;

    /**
     * Constant defining when progress change should be notified. When progress
     * is increased by this value from previous notification, then progress will
     * be notified again.
     */
    public static final float PROGRESS_DELTA = 0.01f;

    /**
     * Keeps PLY header data.
     */
    private HeaderPLY header;

    /**
     * Boolean indicating if file is a valid PLY stream of data.
     */
    private boolean validStream;

    /**
     * Boolean indicating whether validity of file has already been checked.
     */
    private boolean validityChecked;

    /**
     * Iterator currently loading provided file.
     */
    private LoaderIteratorPLY loaderIterator;

    /**
     * Indicates maximum number of vertices to keep in a chunk of data.
     * By default, this is the maximum values stored in a short 65535. This is
     * so that data chunks can be compatible with technologies such as openGL
     * where vertex indices are short values, and hence only 65535 vertices can
     * be indexed at a time.
     */
    private int maxVerticesInChunk;

    /**
     * Indicates whether duplicate vertices in a chunk are allowed. By allowing
     * duplicate vertices, PLY loading can be speed up a little bit at the
     * expense of getting larger sets of data which will contain redundant
     * vertices. If your environment is memory constrained, this should be
     * disabled. By default, it is disabled.
     */
    private boolean allowDuplicateVerticesInChunk;

    /**
     * Maximum number of stream positions to be cached.
     * This loader keeps track of a set of stream positions that have been
     * parsed on ASCII mode. By keeping a cache of positions loading times can
     * be largely reduced at the expense of using more memory during loading.
     * By default, this is set to 1000000 positions.
     * This only has effect on ASCII PLY files. For binary PLY files this
     * setting is ignored.
     */
    private long maxStreamPositions;

    /**
     * Constructor.
     */
    public LoaderPLY() {
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     */
    public LoaderPLY(final int maxVerticesInChunk) {
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     */
    public LoaderPLY(final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk) {
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @param maxStreamPositions            Maximum number of stream positions to be
     *                                      cached.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK or if maximum stream positions is
     *                                  smaller than MIN_STREAM_POSITIONS.
     */
    public LoaderPLY(final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk, long maxStreamPositions) {
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        internalSetMaxStreamPositions(maxStreamPositions);
    }

    /**
     * Constructor.
     *
     * @param f file to be loaded.
     * @throws IOException if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f) throws IOException {
        super(f);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                  file to be loaded.
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final int maxVerticesInChunk)
            throws IOException {
        super(f);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                             file to be loaded.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk) throws IOException {
        super(f);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                             file to be loaded.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @param maxStreamPositions            Maximum number of stream positions to be
     *                                      cached.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK or if maximum stream positions is
     *                                  smaller than MIN_STREAM_POSITIONS.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk,
                     final long maxStreamPositions) throws IOException {
        super(f);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        internalSetMaxStreamPositions(maxStreamPositions);
    }

    /**
     * Constructor.
     *
     * @param listener listener to notify start, end and progress events.
     */
    public LoaderPLY(final LoaderListener listener) {
        super(listener);
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param listener           listener to notify start, end and progress events.
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     */
    public LoaderPLY(final LoaderListener listener, final int maxVerticesInChunk) {
        super(listener);
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param listener                      listener to notify start, end and progress events.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     */
    public LoaderPLY(final LoaderListener listener, final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk) {
        super(listener);
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param listener                      listener to notify start, end and progress events.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @param maxStreamPositions            Maximum number of stream positions to be
     *                                      cached.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK or if maximum stream positions is
     *                                  smaller than MIN_STREAM_POSITIONS.
     */
    public LoaderPLY(final LoaderListener listener, final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk,
                     final long maxStreamPositions) {
        super(listener);
        reader = null;
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        internalSetMaxStreamPositions(maxStreamPositions);
    }

    /**
     * Constructor.
     *
     * @param f        file to be loaded.
     * @param listener listener to notify start, end and progress events.
     * @throws IOException if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final LoaderListener listener)
            throws IOException {
        super(f, listener);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                  file to be loaded.
     * @param listener           listener to notify start, end and progress events.
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final LoaderListener listener,
                     final int maxVerticesInChunk) throws IOException {
        super(f, listener);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                             file to be loaded.
     * @param listener                      listener to notify start, end and progress events.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final LoaderListener listener,
                     final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk) throws IOException {
        super(f, listener);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
    }

    /**
     * Constructor.
     *
     * @param f                             file to be loaded.
     * @param listener                      listener to notify start, end and progress events.
     * @param maxVerticesInChunk            Indicates maximum number of vertices to keep in
     *                                      a chunk of data.
     * @param allowDuplicateVerticesInChunk Indicates whether duplicate vertices
     *                                      in a chunk are allowed.
     * @param maxStreamPositions            Maximum number of stream positions to be
     *                                      cached.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK or if maximum stream positions is
     *                                  smaller than MIN_STREAM_POSITIONS.
     * @throws IOException              if file does not exist or cannot be loaded.
     */
    public LoaderPLY(final File f, final LoaderListener listener,
                     final int maxVerticesInChunk,
                     final boolean allowDuplicateVerticesInChunk,
                     final long maxStreamPositions) throws IOException {
        super(f, listener);
        header = null;
        validStream = false;
        validityChecked = false;
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        internalSetAllowDuplicateVerticesInChunk(allowDuplicateVerticesInChunk);
        internalSetMaxStreamPositions(maxStreamPositions);
    }

    /**
     * Returns maximum number of vertices to keep in a chunk of data.
     * By default, this is the maximum values stored in a short is 65535. This is
     * so that data chunks can be compatible with technologies such as openGL
     * where vertex indices are short values, and hence only 65535 vertices can
     * be indexed at a time.
     *
     * @return Maximum number of vertices to keep in a chunk of data.
     */
    public int getMaxVerticesInChunk() {
        return maxVerticesInChunk;
    }

    /**
     * Sets maximum number of vertices to keep in a chunk of data.
     *
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     * @throws LockedException          Raised if this instance is locked because loading
     *                                  is in progress.
     */
    public void setMaxVerticesInChunk(final int maxVerticesInChunk)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Determines whether duplicate vertices in a chunk are allowed. By allowing
     * duplicate vertices, PLY loading can be speed up a little bit at the
     * expense of getting larger sets of data which will contain redundant
     * vertices. If your environment is memory constrained, this should be
     * disabled. By default, it is disabled.
     *
     * @return Determines whether duplicate vertices in a chunk are allowed.
     */
    public boolean areDuplicateVerticesInChunkAllowed() {
        return allowDuplicateVerticesInChunk;
    }

    /**
     * Sets whether duplicate vertices in a chunk are allowed. By allowing
     * duplicate vertices, PLY loading can be speed up a little bit at the
     * expense of getting larger sets of data which will contain redundant
     * vertices. If your environment is memory constrained, this should be
     * disabled. By default, it is disabled.
     *
     * @param allow Indicates whether duplicates will be allowed or not.
     * @throws LockedException Raised if this instance is locked because loading
     *                         is in progress.
     */
    public void setAllowDuplicateVerticesInChunk(final boolean allow)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        internalSetAllowDuplicateVerticesInChunk(allow);
    }

    /**
     * Returns maximum number of stream positions to be cached.
     * This loader keeps track of a set of stream positions that have been
     * parsed on ASCII mode. By keeping a cache of positions loading times can
     * be largely reduced at the expense of using more memory during loading.
     * By default, this is set to 1000000 positions.
     * This only has effect on ASCII PLY files. For binary PLY files this
     * setting is ignored.
     *
     * @return maximum number of stream positions to be cached.
     */
    public long getMaxStreamPositions() {
        return maxStreamPositions;
    }

    /**
     * Sets maximum number of stream positions to be cached.
     * This loader keeps track of a set of stream positions that have been
     * parsed on ASCII mode. By keeping a cache of positions loading times can
     * be largely reduced at the expense of using more memory during loading.
     * By default, this is set to 1000000 positions.
     * This only has effect on ASCII PLY files. For binary PLY files this
     * setting is ignored.
     *
     * @param maxStreamPositions Maximum number of stream positions to be cached.
     * @throws IllegalArgumentException Raised if provided value is lower than
     *                                  DEFAULT_MAX_STREAM_POSITIONS.
     * @throws LockedException          Raised if this instance is locked because loading
     *                                  is in progress.
     */
    public void setMaxStreamPositions(final long maxStreamPositions)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        internalSetMaxStreamPositions(maxStreamPositions);
    }

    /**
     * Indicates it this loader has enough parameters to start the loading
     * process.
     *
     * @return True if ready, false otherwise.
     */
    @Override
    public boolean isReady() {
        return hasFile();
    }

    /**
     * Returns mesh format supported by this loader, which is PLY_MESH_FORMAT.
     *
     * @return Format supported by this loader, which is PLY_MESH_FORMAT.
     */
    @Override
    public MeshFormat getMeshFormat() {
        return MeshFormat.MESH_FORMAT_PLY;
    }

    /**
     * Reads the header of provided file and determines whether file is valid or
     * not.
     *
     * @return True if file is a valid PLY file, false otherwise.
     * @throws LockedException Raised if this instance is locked because loading
     *                         is in progress.
     * @throws IOException     if an i/O error occurs.
     */
    @Override
    public boolean isValidFile() throws LockedException, IOException {
        if (isLocked()) {
            throw new LockedException();
        }

        setLocked(true);
        if (reader == null) {
            throw new IOException();
        }
        try {
            readHeader();
        } catch (final LoaderException ex) {
            validStream = false;
        }
        setLocked(false);
        return validStream;
    }

    /**
     * Starts the loading process.
     * This method reads the file header, checks its validity and prepares an
     * iterator so the loading process can be carried in an iterative process.
     *
     * @return Iterator to load the file in small chunks of data.
     * @throws LockedException   Raised if this instance is locked because loading
     *                           is in progress.
     * @throws NotReadyException raised if this instance is not yet ready
     *                           because not enough parameters have been set.
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   Raised if file is not a valid PLY or is corrupted.
     */
    @Override
    public LoaderIterator load() throws LockedException, NotReadyException,
            IOException, LoaderException {
        if (!isReady()) {
            throw new NotReadyException();
        }

        readFromStream();
        return loaderIterator;
    }

    /**
     * Internal method to set maximum number of vertices to keep in a chunk of
     * data.
     *
     * @param maxVerticesInChunk Indicates maximum number of vertices to keep in
     *                           a chunk of data.
     * @throws IllegalArgumentException Raised if maximum number of vertices is
     *                                  smaller than MIN_MAX_VERTICES_IN_CHUNK.
     */
    private void internalSetMaxVerticesInChunk(final int maxVerticesInChunk) {
        if (maxVerticesInChunk < MIN_MAX_VERTICES_IN_CHUNK) {
            throw new IllegalArgumentException();
        }

        this.maxVerticesInChunk = maxVerticesInChunk;
    }

    /**
     * Internal method to set whether duplicate vertices in a chunk are allowed.
     * By allowing duplicate vertices, PLY loading can be speed up a little bit
     * at the expense of getting larger sets of data which will contain
     * redundant vertices. If your environment is memory constrained, this
     * should be disabled. By default, it is disabled.
     *
     * @param allow Indicates whether duplicates will be allowed or not.
     */
    private void internalSetAllowDuplicateVerticesInChunk(final boolean allow) {
        allowDuplicateVerticesInChunk = allow;
    }

    /**
     * Internal method to set maximum number of stream positions to be cached.
     * This loader keeps track of a set of stream positions that have been
     * parsed on ASCII mode. By keeping a cache of positions loading times can
     * be largely reduced at the expense of using more memory during loading.
     * By default, this is set to 1000000 positions.
     * This only has effect on ASCII PLY files. For binary PLY files this
     * setting is ignored.
     *
     * @param maxStreamPositions Maximum number of stream positions to be cached.
     * @throws IllegalArgumentException Raised if provided value is lower than
     *                                  DEFAULT_MAX_STREAM_POSITIONS.
     */
    private void internalSetMaxStreamPositions(final long maxStreamPositions) {
        if (maxStreamPositions < MIN_STREAM_POSITIONS) {
            throw new IllegalArgumentException();
        }

        this.maxStreamPositions = maxStreamPositions;
    }

    /**
     * Reads header of provided file and initializes iterator to read data
     * chunks of this file.
     *
     * @throws LockedException Raised if this instance is locked because loading
     *                         is in progress.
     * @throws IOException     if an i/O error occurs.
     * @throws LoaderException Raised if file is not a valid PLY or is corrupted
     */
    private void readFromStream()
            throws LockedException, IOException, LoaderException {

        if (isLocked()) {
            throw new LockedException();
        }

        setLocked(true);
        if (listener != null) {
            listener.onLoadStart(this);
        }

        if (reader == null) {
            throw new IOException();
        }

        if (!validityChecked) {
            readHeader();
        }
        if (!validStream) {
            setLocked(false);
            throw new LoaderException();
        }

        loaderIterator = new LoaderIteratorPLY(this);
        loaderIterator.setListener(new LoaderIteratorListenerImpl(this));
    }

    /**
     * Reads the header of provided file.
     *
     * @throws IOException     if an I/O error occurs.
     * @throws LoaderException Raised if file is not a valid PLY or is
     *                         corrupted.
     */
    private void readHeader() throws IOException, LoaderException {
        if (reader == null) {
            throw new IOException();
        }

        validityChecked = true;

        // first line must be equal to string "ply"
        String str = reader.readLine();

        if (!str.equals("ply") || reader.isEndOfStream()) {
            validStream = false;
            throw new LoaderException();
        }

        // second line contains format (i.e: format ascii 1.0)
        // must start with "format" word
        do {
            // loop is to avoid empty strings
            // (because of duplicate spaces or carriage returns)
            str = reader.readWord();
        } while ((str.isEmpty()) && !reader.isEndOfStream());

        if (!str.equals("format") || reader.isEndOfStream()) {
            validStream = false;
            throw new LoaderException();
        }

        // next to format word comes storage mode
        do {
            // loop to avoid empty strings
            str = reader.readWord();
        } while ((str.isEmpty()) && !reader.isEndOfStream());

        if (reader.isEndOfStream()) {
            validStream = false;
            throw new LoaderException();
        }

        final PLYStorageMode storageMode;
        switch (str) {
            case "ascii":
                // ASCII storage mode
                storageMode = PLYStorageMode.PLY_ASCII;
                break;
            case "binary_big_endian":
                // Binary big endian storage mode
                storageMode = PLYStorageMode.PLY_BIG_ENDIAN;
                break;
            case "binary_little_endian":
                // Binary little endian storage mode
                storageMode = PLYStorageMode.PLY_LITTLE_ENDIAN;
                break;
            default:
                // non supported storage mode
                validStream = false;
                throw new LoaderException();
        }

        // next comes version (always must be 1.0)
        do {
            // loop to avoid empty strings
            str = reader.readWord();
        } while ((str.isEmpty()) && !reader.isEndOfStream());

        if (!str.equals("1.0") || reader.isEndOfStream()) {
            validStream = false;
            throw new LoaderException();
        }

        // instantiate header member
        header = new HeaderPLY();
        // set storage mode
        header.setStorageMode(storageMode);

        // read until we find the line end_header
        boolean endOfHeader = false;
        ElementPLY lastElement = null;
        PropertyPLY property;
        do {
            do {
                // loop to avoid empty strings
                str = reader.readWord();
            } while ((str.isEmpty()) && !reader.isEndOfStream());

            if (str.equals("comment")) {
                // if word is "comment", read until end of line
                str = reader.readLine();
                // add comment to list of comments in header
                header.getComments().add(str);
            } else if (str.equals("obj_info")) {
                // if word is "obj_info", read until end of line
                str = reader.readLine();
                // add obj_info to list of obj_infos in header
                header.getObjInfos().add(str);
            } else if (str.endsWith("element")) {
                // and element has been found. We read its information and
                // add it to the list of elements

                // next word contains element name
                do {
                    // loop to avoid empty strings
                    str = reader.readWord();
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                if (reader.isEndOfStream()) {
                    validStream = false;
                    throw new LoaderException();
                }

                final String elementName = str;

                // next word contains number of instances of this element
                do {
                    str = reader.readWord();
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                if (reader.isEndOfStream()) {
                    validStream = false;
                    throw new LoaderException();
                }

                final long elementInstances;
                try {
                    elementInstances = Long.parseLong(str);
                } catch (NumberFormatException e) {
                    throw new IOException(e);
                }

                // instantiate element
                lastElement = new ElementPLY(elementName, elementInstances);

                // add element to header
                header.getElements().add(lastElement);
            } else if (str.equals("property")) {
                // a property for last element that has been found. We read its
                // information and add it to the element

                if (lastElement == null) {
                    // no previous element was defined
                    validStream = false;
                    throw new LoaderException();
                }

                // read next word
                do {
                    // loop to avoid empty strings
                    str = reader.readWord();
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                if (reader.isEndOfStream()) {
                    validStream = false;
                    throw new LoaderException();
                }

                if (str.equals("list")) {
                    // property is a list
                    final DataTypePLY lengthDataType;
                    final DataTypePLY valueDataType;
                    try {
                        // read length data type
                        do {
                            // loop to avoid empty strings
                            str = reader.readWord();
                        } while ((str.isEmpty()) && !reader.isEndOfStream());

                        if (reader.isEndOfStream()) {
                            validStream = false;
                            throw new LoaderException();
                        }

                        lengthDataType = wordToDataType(str);

                        // read value data type
                        do {
                            str = reader.readWord();
                        } while ((str.isEmpty()) && !reader.isEndOfStream());

                        if (reader.isEndOfStream()) {
                            validStream = false;
                            throw new LoaderException();
                        }

                        valueDataType = wordToDataType(str);
                    } catch (final LoaderException ex) {
                        // some error was found
                        validStream = false;
                        throw ex;
                    }

                    // read property name
                    do {
                        str = reader.readWord();
                    } while ((str.isEmpty()) && !reader.isEndOfStream());

                    if (reader.isEndOfStream()) {
                        validStream = false;
                        throw new LoaderException();
                    }

                    final String propertyName = str;

                    property = new PropertyPLY(propertyName, lengthDataType,
                            valueDataType);
                } else {
                    // property is scalar
                    // word that we have already read should contain value data
                    // type

                    final DataTypePLY valueDataType;
                    try {
                        valueDataType = wordToDataType(str);
                    } catch (final LoaderException ex) {
                        // invalid data type was found
                        validStream = false;
                        throw ex;
                    }
                    // read property name
                    do {
                        str = reader.readWord();
                    } while ((str.isEmpty()) && !reader.isEndOfStream());

                    if (reader.isEndOfStream()) {
                        validStream = false;
                        throw new LoaderException();
                    }

                    final String propertyName = str;

                    property = new PropertyPLY(propertyName, valueDataType);
                }
                try {
                    // add property to last element
                    lastElement.getProperties().add(property);
                } catch (final NotAvailableException ex) {
                    validStream = false;
                    throw new LoaderException(ex);
                }
            } else if (str.equals("end_header")) {
                // end of header has been found
                endOfHeader = true;
            } else {
                // something else that cannot be understood
                validStream = false;
                throw new LoaderException();
            }
        } while (!endOfHeader);

        validStream = true;
    }

    /**
     * Converts a word into a data type. If an unsupported word is found then a
     * LoaderException is raised.
     *
     * @param word word to be converted.
     * @return DataType obtained from word.
     * @throws LoaderException Raised if file is not a valid PLY or is
     *                         corrupted (i.e. an unsupported word is found).
     */
    private DataTypePLY wordToDataType(final String word) throws LoaderException {
        final DataTypePLY dataType = DataTypePLY.forValue(word);
        if (dataType == null) {
            throw new LoaderException();
        }
        return dataType;
    }

    /**
     * Internal listener to be notified when loading process finishes.
     * This listener is used to free resources when loading process finishes.
     */
    private class LoaderIteratorListenerImpl implements LoaderIteratorListener {

        /**
         * Reference to Loader loading PLY file.
         */
        private final LoaderPLY loader;

        /**
         * Constructor.
         *
         * @param loader reference to Loader.
         */
        public LoaderIteratorListenerImpl(final LoaderPLY loader) {
            this.loader = loader;
        }

        /**
         * Called when a loader iterator has no more data to be read.
         *
         * @param iterator Iterator loading a file.
         */
        @Override
        public void onIteratorFinished(final LoaderIterator iterator) {
            // because iterator is finished, we should allow subsequent calls to
            // load method
            try {
                reader.seek(0); // attempt restart stream to current position
            } catch (final Exception ignore) {
                // operation is attempted, if it fails it is ignored
            }

            // on subsequent calls
            validityChecked = false; // reset in case we want to read more data
            if (listener != null) {
                listener.onLoadEnd(loader);
            }
            setLocked(false);
        }
    }

    /**
     * Internal implementation of a loader iterator for PLY files.
     */
    private class LoaderIteratorPLY implements LoaderIterator {

        /**
         * Reference to a LoaderPLY.
         */
        private final LoaderPLY loader;

        /**
         * Number of elements in a list property.
         */
        private int listElems;

        /**
         * Last x vertex coordinate that was read.
         */
        private float coordX;

        /**
         * Last y vertex coordinate that was read.
         */
        private float coordY;

        /**
         * Last z vertex coordinate that was read.
         */
        private float coordZ;

        /**
         * Last red color component that was read.
         */
        private short red;

        /**
         * Last green color component that was read.
         */
        private short green;

        /**
         * Last blue color component that was read.
         */
        private short blue;

        /**
         * Last alpha color component that was read.
         */
        private short alpha;

        /**
         * Last normal x component that was red.
         */
        private float nX;

        /**
         * Last normal y component that was read.
         */
        private float nY;

        /**
         * Last normal z component that was read.
         */
        private float nZ;

        /**
         * Last vertex index that was read.
         */
        private long index;

        // coordinates for bounding box in a chunk

        /**
         * Minimum x coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float minX;

        /**
         * Minimum y coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float minY;

        /**
         * Minimum z coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float minZ;

        /**
         * Maximum x coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float maxX;

        /**
         * Maximum y coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float maxY;

        /**
         * Maximum z coordinate of all vertices that have been read so far in
         * current chunk of data.
         */
        private float maxZ;

        /**
         * Indicates whether file contains vertices.
         */
        private boolean verticesAvailable;

        /**
         * Indicates whether file contains colors.
         */
        private boolean colorsAvailable;

        /**
         * Indicates whether file contains vertex indices.
         */
        private boolean indicesAvailable;

        /**
         * Indicates whether file contains normals.
         */
        private boolean normalsAvailable;

        /**
         * Indicates number of color components of vertices.
         */
        private int colorComponents;

        /**
         * Stores position where header of file finishes.
         */
        private long endHeaderStreamPosition;

        /**
         * Number of vertices contained in the file.
         */
        private long numberOfVertices;

        /**
         * Number of faces (triangles or polygons) contained in the file.
         */
        private long numberOfFaces;

        /**
         * Stores position where first vertex is located.
         */
        private long firstVertexStreamPosition;

        /**
         * Indicates whether first vertex position has already been read and is
         * available.
         */
        private boolean firstVertexStreamPositionAvailable;

        /**
         * Indicates size of vertex data.
         */
        private long vertexDataSize;

        /**
         * Stores position where first vertex index containing a triangle or
         * polygon is located.
         */
        private long firstFaceStreamPosition;

        /**
         * Indicates whether first vertex index position has already been read
         * and is available.
         */
        private boolean firstFaceStreamPositionAvailable;

        /**
         * Stores current position in file stream.
         */
        private long currentStreamPosition;

        /**
         * Number of instances of a given element in the header of the file
         * (i.e. number of vertices or faces).
         */
        private long totalInstances;

        /**
         * A header element that contains vertex properties.
         */
        private ElementPLY vertexElement;

        /**
         * A header element that contains face (triangles/polygons) properties.
         */
        private ElementPLY faceElement;

        /**
         * Current triangle/polygon being read from all faces available in the
         * file.
         */
        private long currentFace;

        /**
         * Listener to fetch a vertex position in the stream of data based on
         * its vertex index.
         * There are implementations for binary and text (ascii) fetchers.
         */
        private VertexFetcherListener fetchVertexListener;

        /**
         * Listener of this iterator that notifies the loader when the iterator
         * has finished loading the file, so that the loader becomes unlocked
         * again.
         */
        private LoaderIteratorListener listener;

        /**
         * Array containing vertex coordinates in current chunk of data.
         * Data is stored sequentially as x1, y1, z1, x2, y2, z2, etc.
         */
        private float[] coordsInChunkArray;

        /**
         * Array containing vertex colors in current chunk of data.
         * Data is stored sequentially depending on the number of color
         * components. For instance, for RGB this would be: r1, g1, b1, r2, g2,
         * b2, etc.
         */
        private short[] colorsInChunkArray;

        /**
         * Indices of vertices in current chunk.
         * Indices are stored sequentially in sets of 3 forming triangles like
         * this: t1a, t1b, t1c, t2a, t2b, t2c, etc.
         */
        private int[] indicesInChunkArray;

        /**
         * Indices of vertices in file. Original indices might differ of indices
         * numeration in current chunk because for each chunk indices start
         * again at zero. Indices are stored sequentially in sets of 3
         * forming triangles like: t1a, t1b, t1c, t2a, t2b, t2c, etc.
         */
        private long[] originalIndicesInChunkArray;

        /**
         * Normals of vertices in current chunk.
         * Normals are stored sequentially for each vertex like this: n1x, n1y,
         * n1z, n2x, n2y, n2z, etc.
         */
        private float[] normalsInChunkArray;

        /**
         * Number of vertices currently stored in chunk.
         * This is used to determine when no more vertices can be stored in
         * a chunk and an additional chunk needs to be loaded.
         */
        private int verticesInChunk;

        /**
         * Number of indices currently stored in chunk.
         */
        private int indicesInChunk;

        /**
         * Number of indices used as a default to initialize arrays.
         * When the number of indices exceeds this value arrays will be resized.
         */
        private int indicesInChunkSize;

        /**
         * Stores current stream position so that vertices positions can be
         * cached.
         */
        private long vertexStreamPosition;

        /**
         * Map containing relations between original indices of the stream (key)
         * and their corresponding index in the chunk (value).
         */
        private final TreeMap<Long, Integer> indicesMap;

        /**
         * Map relating original indices in stream (key) and stream positions
         * (value).
         */
        private final TreeMap<Long, Long> verticesStreamPositionsMap;

        /**
         * Constructor.
         *
         * @param loader Reference to the loader controlling this iterator.
         * @throws LoaderException Raised if file is corrupted and cannot be
         *                         loaded.
         * @throws IOException     if an I/O error occurs.
         */
        public LoaderIteratorPLY(final LoaderPLY loader)
                throws LoaderException, IOException {
            this.loader = loader;
            listElems = 1;
            nX = nY = nZ = 1.0f;
            alpha = 255;
            index = 0;
            colorComponents = 0;
            verticesAvailable = colorsAvailable = indicesAvailable =
                    normalsAvailable = false;
            endHeaderStreamPosition = numberOfVertices = numberOfFaces = 0;
            firstVertexStreamPosition = 0;
            firstVertexStreamPositionAvailable = false;
            vertexDataSize = 0;
            firstFaceStreamPositionAvailable = false;
            currentStreamPosition = 0;
            totalInstances = 0;
            vertexElement = faceElement = null;
            currentFace = 0;
            fetchVertexListener = null;
            listener = null;
            coordsInChunkArray = null;
            colorsInChunkArray = null;
            indicesInChunkArray = null;
            originalIndicesInChunkArray = null;
            normalsInChunkArray = null;
            verticesInChunk = indicesInChunk = 0;
            indicesInChunkSize = 0;
            vertexStreamPosition = 0;
            indicesMap = new TreeMap<>();
            verticesStreamPositionsMap = new TreeMap<>();

            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;

            setUp();
        }

        /**
         * Sets listener to notify when this iterator has finished loading the
         * PLY file.
         *
         * @param listener listener to notify when this iterator has finished
         *                 loading the PLY file.
         */
        public void setListener(final LoaderIteratorListener listener) {
            this.listener = listener;
        }

        /**
         * Indicates if there are still more chunks of data to be read on this
         * PLY file.
         *
         * @return True if there are more chunks of data, false otherwise.
         */
        @Override
        public boolean hasNext() {
            return (currentFace < numberOfFaces);
        }

        /**
         * Reads next chunk of data from PLY file.
         *
         * @return A chunk of data containing vertex coordinates, colors, vertex
         * normals, textures, etc.
         * @throws NotAvailableException Raised if no more chunks are available.
         * @throws LoaderException       Raised if file is corrupted and cannot be
         *                               loaded.
         * @throws IOException           Raised if an I/O error occurs.
         */
        @Override
        @SuppressWarnings("all")
        public DataChunk next() throws NotAvailableException, LoaderException,
                IOException {

            if (reader == null) {
                throw new IOException();
            }

            if (!hasNext()) {
                throw new NotAvailableException();
            }

            initChunkArrays();

            // reset chunk bounding box values
            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;

            final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            final long nElems = faceElement.getNumberOfInstances();
            final long progressStep = Math.max(
                    (long) (LoaderPLY.PROGRESS_DELTA * nElems), 1);

            boolean end = false;

            // initialize list indices to a list of one element to be reused
            int previousListElems = 1;
            long[] listIndices = new long[previousListElems];

            while ((currentFace < nElems) && !end) {

                final long faceStreamPos = reader.getPosition();
                // Iterate on properties
                for (final PropertyPLY property : faceElement.getProperties()) {
                    if (!property.isValidProperty()) {
                        throw new LoaderException();
                    }

                    // number of elements in list (initially assume that is
                    // scalar, hence 1)
                    listElems = 1;

                    // set listeners to read property length value
                    PLYReadValueFromStreamListener readValueFromStreamListener =
                            property.getReadLengthValueFromStreamListener();
                    PLYReadValueFromBufferListener readValueFromBufferListener =
                            property.getReadLengthValueFromBufferListener();

                    if (property.getPropertyType() == PropertyTypePLY.PROPERTY_PLY_LIST) {
                        // read number of list elements
                        readValueFromStreamListener.readFromStream(buffer);

                        if (reader.isEndOfStream() &&
                                (currentFace < (nElems - 1))) {
                            throw new LoaderException();
                        }

                        // set listElems
                        readValueFromBufferListener.readValueFromBuffer(buffer);
                    }

                    boolean needsTriangulation = false;
                    if (listElems > 3) {
                        needsTriangulation = true;
                    } else if (listElems < 3) {
                        // list does not form a triangle or polygon
                        throw new LoaderException();
                    }

                    if (listElems > loader.maxVerticesInChunk) {
                        // this list will never fit in a chunk with current
                        // maxVerticesInChunk setting
                        throw new LoaderException();
                    }

                    if ((verticesInChunk + listElems) >
                            loader.maxVerticesInChunk) {
                        // no more vertices can be added to chunk so we reset stream
                        // to start on current face
                        reader.seek(faceStreamPos);
                        end = true;
                        break;
                    }

                    // set listeners to read property value
                    readValueFromStreamListener =
                            property.getReadValueFromStreamListener();
                    readValueFromBufferListener =
                            property.getReadValueFromBufferListener();

                    if (previousListElems != listElems) {
                        // listIndices can no longer be reused
                        listIndices = new long[listElems];
                    }

                    // read property list data
                    for (int u = 0; u < listElems; u++) {
                        // reads face index from stream and saves it into buffer
                        readValueFromStreamListener.readFromStream(buffer);

                        if (reader.isEndOfStream() && (currentFace <
                                (nElems - 1))) {
                            throw new LoaderException();
                        }

                        // copies buffer content into index member doing proper
                        // type conversion
                        readValueFromBufferListener.readValueFromBuffer(buffer);

                        listIndices[u] = index;
                    }

                    // keep current face stream position
                    currentStreamPosition = reader.getPosition();

                    if (needsTriangulation) {
                        // search vertices data corresponding to read indices
                        final List<Point3D> polygonVertices =
                                new ArrayList<>(listElems);
                        for (int u = 0; u < listElems; u++) {
                            index = listIndices[u];
                            // vertex needs to be added into chunk, so we need to
                            // read vertex data

                            // fetch vertex data position
                            fetchVertexListener.fetch(index);

                            // read all vertex data
                            for (final PropertyPLY vertexProperty :
                                    vertexElement.getProperties()) {

                                // set delegates to read property value from stream
                                readValueFromStreamListener =
                                        vertexProperty.getReadValueFromStreamListener();

                                // read property from stream to buffer
                                readValueFromStreamListener.readFromStream(buffer);

                                if (vertexProperty.isReadValueFromBufferListenerAvailable()) {
                                    // only read from buffer if property is recognized by this class
                                    readValueFromBufferListener =
                                            vertexProperty.getReadValueFromBufferListener();

                                    // move value from buffer to apropriate member
                                    readValueFromBufferListener.readValueFromBuffer(buffer);
                                }
                            }

                            final Point3D point = new InhomogeneousPoint3D(
                                    coordX, coordY, coordZ);
                            polygonVertices.add(point);
                        }

                        try {
                            listIndices = buildTriangulatedIndices(
                                    polygonVertices, listIndices);
                            listElems = listIndices.length;
                        } catch (final TriangulatorException e) {
                            // reset face stream position
                            reader.seek(currentStreamPosition);
                            continue;
                        }
                    }

                    // search for vertices indices contained in list
                    for (int u = 0; u < listElems; u++) {
                        index = listIndices[u];
                        // index contains original face index in PLY file
                        int chunkIndex;
                        if (!loader.allowDuplicateVerticesInChunk &&
                                (chunkIndex = searchIndexInChunk(index)) >= 0) {
                            // vertex is already stored in chunk with chunkIndex
                            addExistingVertexToChunk(chunkIndex);
                        } else {
                            // vertex needs to be added into chunk, so we need to
                            // read vertex data

                            // fetch vertex data position
                            fetchVertexListener.fetch(index);

                            // read all vertex data
                            for (final PropertyPLY vertexProperty :
                                    vertexElement.getProperties()) {

                                // set delegates to read property value from stream
                                readValueFromStreamListener =
                                        vertexProperty.getReadValueFromStreamListener();

                                // read property from stream to buffer
                                readValueFromStreamListener.readFromStream(buffer);

                                if (vertexProperty.isReadValueFromBufferListenerAvailable()) {
                                    // only read from buffer if property is recognized by this class
                                    readValueFromBufferListener =
                                            vertexProperty.getReadValueFromBufferListener();

                                    // move value from buffer to apropriate member
                                    readValueFromBufferListener.readValueFromBuffer(buffer);
                                }
                            }

                            // store all vertex data into chunk arrays
                            addNewVertexDataToChunk();
                        }
                    }

                    // reset face stream position
                    reader.seek(currentStreamPosition);

                    // to reduce memory consumption and delete listIndices
                    if (previousListElems != listElems) {
                        // list indices haven't been reused

                        // update previousListElems
                        previousListElems = listElems;
                    }
                }

                if (!end) {
                    currentFace++;
                }

                // compute progress
                if (loader.listener != null) {
                    if ((currentFace % progressStep) == 0) {
                        loader.listener.onLoadProgressChange(loader,
                                (float) (currentFace) / (float) (nElems));
                    }
                }
            }

            // trim arrays to store only needed data
            trimArrays();

            // Instantiate DataChunk with chunk arrays
            final DataChunk dataChunk = new DataChunk();
            if (verticesAvailable) {
                dataChunk.setVerticesCoordinatesData(coordsInChunkArray);
                dataChunk.setMinX(minX);
                dataChunk.setMinY(minY);
                dataChunk.setMinZ(minZ);
                dataChunk.setMaxX(maxX);
                dataChunk.setMaxY(maxY);
                dataChunk.setMaxZ(maxZ);
            } else {
                // so it can be garbage collected
                coordsInChunkArray = null;
            }

            if (colorsAvailable) {
                dataChunk.setColorData(colorsInChunkArray);
                dataChunk.setColorComponents(colorComponents);
            } else {
                // so it can be garbage collected
                colorsInChunkArray = null;
            }

            if (indicesAvailable) {
                dataChunk.setIndicesData(indicesInChunkArray);
            } else {
                // so it can be garbage collected
                indicesInChunkArray = null;
            }

            if (normalsAvailable) {
                dataChunk.setNormalsData(normalsInChunkArray);
            } else {
                // so it can be garbage collected
                normalsInChunkArray = null;
            }

            if (!hasNext()) {
                // notify iterator finished
                if (listener != null) {
                    listener.onIteratorFinished(this);
                }
            }

            // if no more chunks are available, then close input reader
            if (!hasNext()) {
                reader.close();
            }

            return dataChunk;
        }

        /**
         * Triangulates provided polygon having vertices corresponding to
         * provided indices and returns an array of indices corresponding to the
         * triangles forming the polygon.
         *
         * @param polygonVertices vertices forming a polygon to be triangulated.
         * @param plyIndices      indices corresponding to provided polygon.
         * @return array of indices corresponding to the triangles forming the
         * polygon.
         * @throws TriangulatorException if triangulation fails because polygon
         *                               is degenerate or vertices contains invalid values such as NaN or
         *                               infinity.
         */
        private long[] buildTriangulatedIndices(
                final List<Point3D> polygonVertices,
                final long[] plyIndices) throws TriangulatorException {

            final List<int[]> indices = new ArrayList<>();
            final Triangulator3D triangulator = Triangulator3D.create();
            final List<Triangle3D> triangles = triangulator.triangulate(
                    polygonVertices, indices);

            final long[] result = new long[triangles.size() * 3];
            int pos = 0;
            long plyIndex;
            for (final int[] triangleIndices : indices) {
                for (final int triangleIndex : triangleIndices) {
                    plyIndex = plyIndices[triangleIndex];
                    result[pos] = plyIndex;
                    pos++;
                }
            }

            return result;
        }


        /**
         * Initializes arrays where chunk data will be stored.
         */
        private void initChunkArrays() {
            coordsInChunkArray = new float[loader.maxVerticesInChunk * 3];
            colorsInChunkArray = new short[
                    loader.maxVerticesInChunk * colorComponents];
            indicesInChunkArray = new int[loader.maxVerticesInChunk];
            originalIndicesInChunkArray = new long[loader.maxVerticesInChunk];
            normalsInChunkArray = new float[loader.maxVerticesInChunk * 3];
            verticesInChunk = 0;
            indicesInChunk = 0;
            indicesInChunkSize = loader.maxVerticesInChunk;
            indicesMap.clear();
        }

        /**
         * Searches corresponding index in chunk for provided stream vertex
         * index.
         *
         * @param originalIndex Index in original stream of data.
         * @return Vertex index in current chunk of data.
         */
        private int searchIndexInChunk(final long originalIndex) {
            final Integer chunkIndex = indicesMap.get(originalIndex);

            if (chunkIndex == null) {
                return -1;
            }

            return indicesInChunkArray[chunkIndex];
        }

        /**
         * Caches provided vertex index to relate it to given stream position
         * where vertex data can be found.
         * This method only has effect for ASCII PLY files.
         *
         * @param originalIndex  Vertex index in original stream of data.
         * @param streamPosition Stream position where vertex is found.
         */
        private void addVertexPositionToMap(final long originalIndex,
                                            final long streamPosition) {

            if (loader.header.getStorageMode() == PLYStorageMode.PLY_ASCII) {
                if (verticesStreamPositionsMap.size() > loader.maxStreamPositions) {
                    // Map is full. Remove 1st item before adding a new one
                    final Long origIndex = verticesStreamPositionsMap.firstKey();
                    verticesStreamPositionsMap.remove(origIndex);
                }
                // add new item
                verticesStreamPositionsMap.put(originalIndex,
                        streamPosition);
            }
        }

        /**
         * Adds data of last vertex that has been read to the arrays of current
         * chunk of data.
         */
        private void addNewVertexDataToChunk() {
            int pos = 3 * verticesInChunk;
            int colorPos = colorComponents * verticesInChunk;
            coordsInChunkArray[pos] = coordX;
            normalsInChunkArray[pos] = nX;
            if (colorComponents >= 1) {
                colorsInChunkArray[colorPos] = red;
            }

            pos++;
            colorPos++;

            coordsInChunkArray[pos] = coordY;
            normalsInChunkArray[pos] = nY;
            if (colorComponents >= 2) {
                colorsInChunkArray[colorPos] = green;
            }

            pos++;
            colorPos++;

            coordsInChunkArray[pos] = coordZ;
            normalsInChunkArray[pos] = nZ;
            if (colorComponents >= 3) {
                colorsInChunkArray[colorPos] = blue;
            }

            if (colorComponents >= 4) {
                colorPos++;
                colorsInChunkArray[colorPos] = alpha;
            }

            // update bounding box values
            if (coordX < minX) {
                minX = coordX;
            }
            if (coordY < minY) {
                minY = coordY;
            }
            if (coordZ < minZ) {
                minZ = coordZ;
            }

            if (coordX > maxX) {
                maxX = coordX;
            }
            if (coordY > maxY) {
                maxY = coordY;
            }
            if (coordZ > maxZ) {
                maxZ = coordZ;
            }

            // if arrays of indices become full, we need to resize them
            if (indicesInChunk >= indicesInChunkSize) {
                increaseIndicesArraySize();
            }
            indicesInChunkArray[indicesInChunk] = verticesInChunk;
            originalIndicesInChunkArray[indicesInChunk] = index;
            // store original index in map, so we can search chunk index by
            // original index
            indicesMap.put(index, indicesInChunk);

            // store vertex stream position in ascii mode
            addVertexPositionToMap(index, vertexStreamPosition);

            verticesInChunk++;
            indicesInChunk++;
        }

        /**
         * Adds provided vertex index into chunk of data.
         * This method is only called when duplicate vertices are allowed in
         * chunks of data.
         *
         * @param existingIndex Index to be added into chunk.
         */
        private void addExistingVertexToChunk(final int existingIndex) {
            // if arrays of indices become full, we need to resize them
            if (indicesInChunk >= indicesInChunkSize) {
                increaseIndicesArraySize();
            }
            indicesInChunkArray[indicesInChunk] = existingIndex;
            originalIndicesInChunkArray[indicesInChunk] = index;

            indicesInChunk++;
        }

        /**
         * This method increases the size of arrays containing data of current
         * chunk. This method is called when arrays get full and need to be
         * enlarged.
         */
        private void increaseIndicesArraySize() {
            final int newIndicesInChunkSize = indicesInChunkSize +
                    loader.maxVerticesInChunk;
            final int[] newIndicesInChunkArray = new int[newIndicesInChunkSize];
            final long[] newOriginalIndicesInChunkArray =
                    new long[newIndicesInChunkSize];

            // copy contents of old arrays
            System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray, 0,
                    indicesInChunkSize);
            System.arraycopy(originalIndicesInChunkArray, 0,
                    newOriginalIndicesInChunkArray, 0, indicesInChunkSize);

            // set new arrays and new size
            indicesInChunkArray = newIndicesInChunkArray;
            originalIndicesInChunkArray = newOriginalIndicesInChunkArray;
            indicesInChunkSize = newIndicesInChunkSize;
        }

        /**
         * This method removes unnecessary data of arrays.
         * This method is called when finishing the processing of current chunk
         * of data.
         */
        private void trimArrays() {
            if (verticesInChunk > 0) {
                final int elems = verticesInChunk * 3;
                final int colorElems = verticesInChunk * colorComponents;

                final float[] newCoordsInChunkArray = new float[elems];
                final short[] newColorsInChunkArray = new short[colorElems];
                final float[] newNormalsInChunkArray = new float[elems];

                // copy contents of old arrays
                System.arraycopy(coordsInChunkArray, 0, newCoordsInChunkArray,
                        0, elems);
                System.arraycopy(colorsInChunkArray, 0, newColorsInChunkArray,
                        0, colorElems);
                System.arraycopy(normalsInChunkArray, 0, newNormalsInChunkArray,
                        0, elems);

                // set new arrays
                coordsInChunkArray = newCoordsInChunkArray;
                colorsInChunkArray = newColorsInChunkArray;
                normalsInChunkArray = newNormalsInChunkArray;
            } else {
                // allow garbage collection
                coordsInChunkArray = null;
                colorsInChunkArray = null;
                normalsInChunkArray = null;
            }

            if (indicesInChunk > 0) {
                final int[] newIndicesInChunkArray = new int[indicesInChunk];
                System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray,
                        0, indicesInChunk);

                // set new array
                indicesInChunkArray = newIndicesInChunkArray;
            } else {
                // allow garbage collection
                indicesInChunkArray = null;
                originalIndicesInChunkArray = null;
            }
        }

        /**
         * Reads header data to set up listeners capable of reading stream data
         * according to data types contained in header.
         *
         * @throws LoaderException Raised if file is corrupted (header is
         *                         invalid).
         * @throws IOException     if an I/O error occurs.
         */
        private void setUp() throws LoaderException, IOException {

            endHeaderStreamPosition = reader.getPosition();
            long streamPositionOffset = endHeaderStreamPosition;

            // read header to set up listeners
            try {
                totalInstances = 0;
                colorComponents = 0;
                for (final ElementPLY element : loader.header.getElements()) {
                    if (!element.isValidElement()) {
                        throw new LoaderException();
                    }
                    totalInstances += element.getNumberOfInstances();

                    if (element.getName().equals("vertex")) {
                        vertexElement = element;

                        // obtain position of first vertex

                        // obtain number of vertices
                        numberOfVertices = element.getNumberOfInstances();

                        vertexDataSize = 0;
                        for (final PropertyPLY property : element.getProperties()) {

                            // set listener to read data from stream
                            setReadValueFromStreamListener(property,
                                    loader.header.getStorageMode());

                            // set listener to read data stored in buffer
                            switch (property.getName()) {
                                case "x":
                                    verticesAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new Xint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new Xuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new Xint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new Xuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new Xint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new Xuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new Xfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new Xfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new XcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new XucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new XshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new XushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new XintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new XuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new XfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new XdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "y":
                                    verticesAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new Yint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new Yuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new Yint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new Yuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new Yint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new Yuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new Yfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new Yfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new YcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new YucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new YshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new YushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new YintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new YuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new YfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new YdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "z":
                                    verticesAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new Zint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new Zuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new Zint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new Zuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new Zint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new Zuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new Zfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new Zfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new ZcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new ZucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new ZshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new ZushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new ZintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new ZuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new ZfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new ZdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "nx":
                                    normalsAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new NXint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new NXuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new NXint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new NXuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new NXint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new NXuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new NXfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new NXfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NXcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NXucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NXshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NXushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new NXintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new NXuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new NXfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new NXdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "ny":
                                    normalsAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new NYint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new NYuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new NYint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new NYuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new NYint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new NYuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new NYfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new NYfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NYcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NYucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NYshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NYushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new NYintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new NYuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new NYfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new NYdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "nz":
                                    normalsAvailable = true;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new NZint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new NZuint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new NZint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new NZuint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new NZint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new NZuint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new NZfloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new NZfloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NZcharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new NZucharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NZshortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new NZushortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new NZintReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new NZuintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new NZfloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new NZdoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "red":
                                    colorsAvailable = true;
                                    colorComponents++;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new RedInt8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new RedUint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new RedInt16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new RedUint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new RedInt32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new RedUint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new RedFloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new RedFloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new RedCharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new RedUcharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new RedShortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new RedUshortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new RedIntReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new RedUintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new RedFloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new RedDoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "green":
                                    colorsAvailable = true;
                                    colorComponents++;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new GreenInt8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new GreenInt16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new GreenInt32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new GreenFloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new GreenFloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new GreenCharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUcharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new GreenShortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUshortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new GreenIntReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new GreenUintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new GreenFloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new GreenDoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "blue":
                                    colorsAvailable = true;
                                    colorComponents++;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new BlueInt8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new BlueInt16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new BlueInt32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new BlueFloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new BlueFloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new BlueCharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUcharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new BlueShortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUshortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadLengthValueFromBufferListener(
                                                    new BlueIntReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new BlueUintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new BlueFloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new BlueDoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                case "alpha":
                                    colorsAvailable = true;
                                    colorComponents++;
                                    switch (property.getValueType()) {
                                        case PLY_INT8:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaInt8ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT8:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUint8ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT16:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaInt16ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT16:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUint16ReadValueFromBufferListener());
                                            break;
                                        case PLY_INT32:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaInt32ReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT32:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUint32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT32:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaFloat32ReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT64:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaFloat64ReadValueFromBufferListener());
                                            break;
                                        case PLY_CHAR:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaCharReadValueFromBufferListener());
                                            break;
                                        case PLY_UCHAR:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUcharReadValueFromBufferListener());
                                            break;
                                        case PLY_SHORT:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaShortReadValueFromBufferListener());
                                            break;
                                        case PLY_USHORT:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUshortReadValueFromBufferListener());
                                            break;
                                        case PLY_INT:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaIntReadValueFromBufferListener());
                                            break;
                                        case PLY_UINT:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaUintReadValueFromBufferListener());
                                            break;
                                        case PLY_FLOAT:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaFloatReadValueFromBufferListener());
                                            break;
                                        case PLY_DOUBLE:
                                            property.setReadValueFromBufferListener(
                                                    new AlphaDoubleReadValueFromBufferListener());
                                            break;
                                        default:
                                            throw new LoaderException();
                                    }
                                    break;
                                default:
                                    // not recognized properties are ignored
                                    break;
                            }

                            // update vertex data size
                            if (loader.header.getStorageMode() != PLYStorageMode.PLY_ASCII) {
                                vertexDataSize += sizeForDataType(property.getValueType());
                            }
                        }

                        if (loader.header.getStorageMode() != PLYStorageMode.PLY_ASCII &&
                                !firstFaceStreamPositionAvailable) {

                            firstVertexStreamPosition = streamPositionOffset;
                            firstVertexStreamPositionAvailable = true;
                            // update offset taking into account all vertex data
                            streamPositionOffset += numberOfVertices * vertexDataSize;
                        }

                    } else if (element.getName().equals("face")) {
                        faceElement = element;
                        indicesAvailable = true;

                        // obtain number of faces
                        numberOfFaces = element.getNumberOfInstances();

                        for (final PropertyPLY property : element.getProperties()) {

                            // set listener to read data from stream
                            setReadValueFromStreamListener(property,
                                    loader.header.getStorageMode());

                            if (property.getPropertyType() ==
                                    PropertyTypePLY.PROPERTY_PLY_LIST) {
                                // set listeners to get number of elements in a
                                // list
                                setReadLengthValueFromBufferListener(property);
                                setReadLengthValueFromStreamListener(property,
                                        loader.header.getStorageMode());
                            }

                            switch (property.getValueType()) {
                                case PLY_INT8:
                                    property.setReadValueFromBufferListener(
                                            new FaceInt8ReadValueFromBufferListener());
                                    break;
                                case PLY_UINT8:
                                    property.setReadValueFromBufferListener(
                                            new FaceUint8ReadValueFromBufferListener());
                                    break;
                                case PLY_INT16:
                                    property.setReadValueFromBufferListener(
                                            new FaceInt16ReadValueFromBufferListener());
                                    break;
                                case PLY_UINT16:
                                    property.setReadValueFromBufferListener(
                                            new FaceUint16ReadValueFromBufferListener());
                                    break;
                                case PLY_INT32:
                                    property.setReadValueFromBufferListener(
                                            new FaceInt32ReadValueFromBufferListener());
                                    break;
                                case PLY_UINT32:
                                    property.setReadValueFromBufferListener(
                                            new FaceUint32ReadValueFromBufferListener());
                                    break;
                                case PLY_FLOAT32:
                                    property.setReadValueFromBufferListener(
                                            new FaceFloat32ReadValueFromBufferListener());
                                    break;
                                case PLY_FLOAT64:
                                    property.setReadValueFromBufferListener(
                                            new FaceFloat64ReadValueFromBufferListener());
                                    break;
                                case PLY_CHAR:
                                    property.setReadValueFromBufferListener(
                                            new FaceCharReadValueFromBufferListener());
                                    break;
                                case PLY_UCHAR:
                                    property.setReadValueFromBufferListener(
                                            new FaceUcharReadValueFromBufferListener());
                                    break;
                                case PLY_SHORT:
                                    property.setReadValueFromBufferListener(
                                            new FaceShortReadValueFromBufferListener());
                                    break;
                                case PLY_USHORT:
                                    property.setReadValueFromBufferListener(
                                            new FaceUshortReadValueFromBufferListener());
                                    break;
                                case PLY_INT:
                                    property.setReadValueFromBufferListener(
                                            new FaceIntReadValueFromBufferListener());
                                    break;
                                case PLY_UINT:
                                    property.setReadValueFromBufferListener(
                                            new FaceUintReadValueFromBufferListener());
                                    break;
                                case PLY_FLOAT:
                                    property.setReadValueFromBufferListener(
                                            new FaceFloatReadValueFromBufferListener());
                                    break;
                                case PLY_DOUBLE:
                                    property.setReadValueFromBufferListener(
                                            new FaceDoubleReadValueFromBufferListener());
                                    break;
                                default:
                                    throw new LoaderException();
                            }
                        }

                        if (loader.header.getStorageMode() != PLYStorageMode.PLY_ASCII) {
                            // Binary storage mode (either Little endian or Big
                            // endian)
                            firstFaceStreamPosition = streamPositionOffset;
                            firstFaceStreamPositionAvailable = true;

                            fetchVertexListener =
                                    new BinaryVertexFetcherListener();
                        } else {
                            // ASCII storage mode
                            fetchVertexListener =
                                    new AsciiVertexFetcherListener();
                        }
                    }
                }

                // find first vertex and face positions in stream in case it
                // couldn't be computed
                findFirstVertexAndFaceStreamPosition();

                // set stream into 1st face position
                if (!firstFaceStreamPositionAvailable) {
                    throw new LoaderException();
                }
                reader.seek(firstFaceStreamPosition);
                currentFace = 0;

            } catch (final NotAvailableException e) {
                throw new LoaderException(e);
            }
        }

        /**
         * Returns size in bytes for a given data type.
         *
         * @param type A data type.
         * @return Size in bytes for a given data type.
         * @throws LoaderException Raised if provided data type is not supported.
         */
        private long sizeForDataType(final DataTypePLY type) throws LoaderException {
            switch (type) {
                case PLY_INT8:
                case PLY_UINT8:
                case PLY_CHAR:
                case PLY_UCHAR:
                    return 1; // 1 byte
                case PLY_INT16:
                case PLY_UINT16:
                case PLY_SHORT:
                case PLY_USHORT:
                    return 2; // 2 bytes
                case PLY_INT32:
                case PLY_UINT32:
                case PLY_FLOAT32:
                case PLY_INT:
                case PLY_UINT:
                case PLY_FLOAT:
                    return 4; // 4 bytes
                case PLY_FLOAT64:
                case PLY_DOUBLE:
                    return 8; // 8 bytes
                default:
                    throw new LoaderException();
            }
        }

        /**
         * Finds in file stream the location of the first vertex and face.
         *
         * @throws LoaderException       Raised if file is corrupted and location of
         *                               first vertex or face cannot be found.
         * @throws NotAvailableException Raised if header does not contain a
         *                               given element.
         * @throws IOException           if an I/O error occurs.
         */
        private void findFirstVertexAndFaceStreamPosition()
                throws LoaderException, NotAvailableException, IOException {

            if (firstVertexStreamPositionAvailable &&
                    firstFaceStreamPositionAvailable) {
                // already computed
                return;
            }

            final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            long nElems;
            long counter = 0;

            // iterate over elements in header
            for (final ElementPLY element : loader.header.getElements()) {
                if (!element.isValidElement()) {
                    throw new LoaderException();
                }

                boolean stop = false;
                if (element.getName().equals("vertex")) {
                    // FIRST VERTEX FOUND!
                    firstVertexStreamPosition = reader.getPosition();
                    firstVertexStreamPositionAvailable = true;

                    vertexElement = element;

                    // stop if both 1st vertex and face are known
                    stop = firstFaceStreamPositionAvailable;
                } else if (element.getName().equals("face")) {
                    // FIRST FACE FOUND!
                    firstFaceStreamPosition = reader.getPosition();
                    firstFaceStreamPositionAvailable = true;

                    faceElement = element;

                    // stop if both 1st vertex and face are known
                    stop = firstVertexStreamPositionAvailable;
                }

                if (stop) {
                    break;
                }

                nElems = element.getNumberOfInstances();
                // repeat properties iteration for each element
                for (long i = 0; i < nElems; i++) {

                    PLYReadValueFromStreamListener
                            readLengthValueFromStreamListener;
                    PLYReadValueFromBufferListener
                            readLengthValueFromBufferListener;
                    PLYReadValueFromStreamListener readValueFromStreamListener;

                    // iterate on properties of element
                    for (final PropertyPLY property : element.getProperties()) {
                        if (!property.isValidProperty()) {
                            throw new LoaderException();
                        }

                        // number of elements in list (initially assume that is
                        // scalar, hence 1
                        listElems = 1;

                        if (property.getPropertyType() ==
                                PropertyTypePLY.PROPERTY_PLY_LIST) {
                            // read number of list elements
                            readLengthValueFromStreamListener =
                                    property.getReadLengthValueFromStreamListener();
                            readLengthValueFromStreamListener.readFromStream(
                                    buffer);

                            if (reader.isEndOfStream() &&
                                    (counter < (totalInstances - 1))) {
                                throw new LoaderException();
                            }

                            readLengthValueFromBufferListener =
                                    property.getReadLengthValueFromBufferListener();
                            readLengthValueFromBufferListener.readValueFromBuffer(buffer);
                        }

                        // set delegate to read from stream
                        readValueFromStreamListener = property.getReadValueFromStreamListener();

                        // read property data
                        for (long u = 0; u < listElems; u++) {
                            readValueFromStreamListener.readFromStream(buffer);

                            if (reader.isEndOfStream() &&
                                    (counter < (totalInstances - 1))) {
                                throw new LoaderException();
                            }
                        }
                    }

                    counter++;
                }
            }
        }

        /**
         * Class to fetch vertex position within the file stream for a binary.
         * file.
         */
        private class BinaryVertexFetcherListener implements VertexFetcherListener {

            /**
             * Fetches vertex position and sets current stream position to
             * desired vertex.
             *
             * @param index index of vertex to be searched.
             * @throws LoaderException if file is corrupted.
             * @throws IOException     if an I/O error occurs.
             */
            @Override
            public void fetch(final long index)
                    throws LoaderException, IOException {

                final long nElems = vertexElement.getNumberOfInstances();
                if (index >= nElems) {
                    throw new LoaderException();
                }

                final long pos = firstVertexStreamPosition + (index * vertexDataSize);
                if (reader.getPosition() != pos) {
                    reader.seek(pos);
                }
            }
        }

        /**
         * Class to fetch vertex position within the file stream for an ascii
         * file.
         */
        private class AsciiVertexFetcherListener implements VertexFetcherListener {

            /**
             * Fetches vertex position and sets current stream position to
             * desired vertex.
             *
             * @param index index of vertex to be searched.
             * @throws LoaderException       if file is corrupted.
             * @throws IOException           if an I/O error occurs.
             * @throws NotAvailableException if a given element in the header
             *                               is not available.
             */
            @Override
            public void fetch(final long index)
                    throws LoaderException, IOException, NotAvailableException {

                final long nElems = vertexElement.getNumberOfInstances();
                if (index >= nElems) {
                    throw new LoaderException();
                }

                final ByteBuffer fetchBuffer = ByteBuffer.allocate(BUFFER_SIZE);

                long startStreamPos = firstVertexStreamPosition;
                long startIndex = 0;

                if (!verticesStreamPositionsMap.isEmpty()) {
                    // with floorEntry, we will pick element immediately
                    // before or equal to index if any exists
                    final Map.Entry<Long, Long> entry =
                            verticesStreamPositionsMap.floorEntry(index);
                    if (entry != null) {
                        final long origIndex = entry.getKey();
                        final long pos = entry.getValue();
                        if ((origIndex <= index) && (pos >= 0)) {
                            startIndex = origIndex;
                            startStreamPos = pos;
                        }
                    }
                }

                // if we need to read next vertex, don't do anything, otherwise
                // move to next vertex location if reading some vertex located
                // further on the stream. For previous vertex indices, start from
                // beginning
                if (reader.getPosition() != startStreamPos) {
                    reader.seek(startStreamPos);
                }

                // read from stream until start of data of desired vertex
                for (long i = startIndex; i < index; i++) {

                    // when traversing stream of data until reaching desired
                    // index, we add all vertex positions into map
                    final long streamPosition = reader.getPosition();
                    addVertexPositionToMap(i, streamPosition);

                    PLYReadValueFromStreamListener readValueFromStreamListener;

                    // iterate on vertex element properties
                    for (final PropertyPLY property : vertexElement.getProperties()) {
                        if (!property.isValidProperty()) {
                            throw new LoaderException();
                        }

                        // set delegates to read property value
                        readValueFromStreamListener =
                                property.getReadValueFromStreamListener();
                        // and read value
                        readValueFromStreamListener.readFromStream(fetchBuffer);

                        if (reader.isEndOfStream() && (i < (index - 1))) {
                            throw new LoaderException();
                        }
                    }
                }

                vertexStreamPosition = reader.getPosition();
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using int8 data type.
         */
        private class Xint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.get(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using int8 data type.
         */
        private class Yint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.get(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using int8 data type.
         */
        private class Zint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.get(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using uint8 data type.
         */
        private class Xuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getShort(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using uint8 data type.
         */
        private class Yuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getShort(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using uint8 data type.
         */
        private class Zuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getShort(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using int16 data type.
         */
        private class Xint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getShort(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using int16 data type.
         */
        private class Yint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getShort(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using int16 data type.
         */
        private class Zint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getShort(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using uint16 data
         * type.
         */
        private class Xuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getInt(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using uint16 data
         * type.
         */
        private class Yuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getInt(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using uint16 data
         * type.
         */
        private class Zuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getInt(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using int32 data type.
         */
        private class Xint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getInt(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using int32 data type.
         */
        private class Yint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getInt(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using int32 data type.
         */
        private class Zint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getInt(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using uint32 data
         * type.
         */
        private class Xuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getLong(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using uint32 data
         * type.
         */
        private class Yuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getLong(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using uint32 data
         * type.
         */
        private class Zuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getLong(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using float32 data
         * type.
         */
        private class Xfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getFloat(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using float32 data
         * type.
         */
        private class Yfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getFloat(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using float32 data
         * type.
         */
        private class Zfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getFloat(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using float64 data
         * type.
         */
        private class Xfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordX = (float) value;
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using float64 data
         * type.
         */
        private class Yfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordY = (float) value;
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using float64 data
         * type.
         */
        private class Zfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordZ = (float) value;
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using char data type.
         */
        private class XcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.get(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using char data type.
         */
        private class YcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.get(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using char data type.
         */
        private class ZcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.get(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using uchar data type.
         */
        private class XucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getShort(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using uchar data type.
         */
        private class YucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getShort(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using uchar data type.
         */
        private class ZucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getShort(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using short data type.
         */
        private class XshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getShort(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using short data type.
         */
        private class YshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getShort(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using short data type.
         */
        private class ZshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getShort(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using ushort data
         * type.
         */
        private class XushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getInt(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using ushort data
         * type.
         */
        private class YushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getInt(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using ushort data
         * type.
         */
        private class ZushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getInt(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using int data type.
         */
        private class XintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getInt(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using int data type.
         */
        private class YintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getInt(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using int data type.
         */
        private class ZintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getInt(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using uint data type.
         */
        private class XuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordX = buffer.getLong(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using uint data type.
         */
        private class YuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordY = buffer.getLong(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using uint data type.
         */
        private class ZuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.coordZ = buffer.getLong(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using float data type.
         */
        private class XfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordX = buffer.getFloat(0);
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using float data type.
         */
        private class YfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordY = buffer.getFloat(0);
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using float data type.
         */
        private class ZfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.coordZ = buffer.getFloat(0);
            }
        }

        /**
         * Reads x vertex coordinate from temporal buffer using double data
         * type.
         */
        private class XdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads x vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordX = (float) value;
            }
        }

        /**
         * Reads y vertex coordinate from temporal buffer using double data
         * type.
         */
        private class YdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads y vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordY = (float) value;
            }
        }

        /**
         * Reads z vertex coordinate from temporal buffer using double data
         * type.
         */
        private class ZdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads z vertex coordinate from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.coordZ = (float) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using int8 data type.
         */
        private class RedInt8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.red = buffer.get(0);
            }
        }

        /**
         * Reads green color component from temporal buffer using int8 data
         * type.
         */
        private class GreenInt8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.green = buffer.get(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using int8 data type.
         */
        private class BlueInt8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.blue = buffer.get(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using int8 data
         * type.
         */
        private class AlphaInt8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.alpha = buffer.get(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using uint8 data type.
         */
        private class RedUint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.red = buffer.getShort(0);
            }
        }

        /**
         * Read green color component from temporal buffer using uint8 data
         * type.
         */
        private class GreenUint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.green = buffer.getShort(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using uint8 data
         * type.
         */
        private class BlueUint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.blue = buffer.getShort(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using uint8 data
         * type.
         */
        private class AlphaUint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.alpha = buffer.getShort(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using int16 data type.
         */
        private class RedInt16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.red = buffer.getShort(0);
            }
        }

        /**
         * Reads green color component from temporal buffer using int16 data
         * type.
         */
        private class GreenInt16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.green = buffer.getShort(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using int16 data
         * type.
         */
        private class BlueInt16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.blue = buffer.getShort(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using int16 data
         * type.
         */
        private class AlphaInt16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.alpha = buffer.getShort(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using uint16 data
         * type.
         */
        private class RedUint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using uint16 data
         * type.
         */
        private class GreenUint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using uint16 data
         * type.
         */
        private class BlueUint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using uint16 data
         * type.
         */
        private class AlphaUint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using int32 data type.
         */
        private class RedInt32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using int32 data
         * type.
         */
        private class GreenInt32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using int32 data
         * type.
         */
        private class BlueInt32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using int32 data
         * type.
         */
        private class AlphaInt32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using uint32 data
         * type.
         */
        private class RedUint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using uint32 data
         * type.
         */
        private class GreenUint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using uint32 data
         * type.
         */
        private class BlueUint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using uint32 data
         * type.
         */
        private class AlphaUint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using float32 data
         * type.
         */
        private class RedFloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using float32 data
         * type.
         */
        private class GreenFloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using float32 data
         * type.
         */
        private class BlueFloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using float32 data
         * type.
         */
        private class AlphaFloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using float64 data
         * type.
         */
        private class RedFloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using float64 data
         * type.
         */
        private class GreenFloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using float64 data
         * type.
         */
        private class BlueFloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using float64 data
         * type.
         */
        private class AlphaFloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using char data type.
         */
        private class RedCharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.red = buffer.get(0);
            }
        }

        /**
         * Reads green color component from temporal buffer using char data
         * type.
         */
        private class GreenCharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.green = buffer.get(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using char data type.
         */
        private class BlueCharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.blue = buffer.get(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using char data
         * type.
         */
        private class AlphaCharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.alpha = buffer.get(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using uchar data type.
         */
        private class RedUcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.red = buffer.getShort(0);
            }
        }

        /**
         * Reads green color component from temporal buffer using uchar data
         * type.
         */
        private class GreenUcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.green = buffer.getShort(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using uchar data
         * type.
         */
        private class BlueUcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.blue = buffer.getShort(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using uchar data
         * type.
         */
        private class AlphaUcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.alpha = buffer.getShort(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using short data type.
         */
        private class RedShortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.red = buffer.getShort(0);
            }
        }

        /**
         * Reads green color component from temporal buffer using short data
         * type.
         */
        private class GreenShortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.green = buffer.getShort(0);
            }
        }

        /**
         * Reads blue color component from temporal buffer using short data
         * type.
         */
        private class BlueShortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.blue = buffer.getShort(0);
            }
        }

        /**
         * Reads alpha color component from temporal buffer using short data
         * type.
         */
        private class AlphaShortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.alpha = buffer.getShort(0);
            }
        }

        /**
         * Reads red color component from temporal buffer using ushort data
         * type.
         */
        private class RedUshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using ushort data
         * type.
         */
        private class GreenUshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using ushort data
         * type.
         */
        private class BlueUshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using ushort data
         * type.
         */
        private class AlphaUshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final int value = buffer.getInt(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using int data type.
         */
        private class RedIntReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using int data type.
         */
        private class GreenIntReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using int data type.
         */
        private class BlueIntReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using int data type.
         */
        private class AlphaIntReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final int value = buffer.getInt(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using uint data type.
         */
        private class RedUintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using uint data
         * type.
         */
        private class GreenUintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using uint data type.
         */
        private class BlueUintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using uint data
         * type.
         */
        private class AlphaUintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                final long value = buffer.getLong(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using float data type.
         */
        private class RedFloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using float data
         * type.
         */
        private class GreenFloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using float data
         * type.
         */
        private class BlueFloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using float data
         * type.
         */
        private class AlphaFloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads red color component from temporal buffer using double data
         * type.
         */
        private class RedDoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads red color component from temporal buffer and sets its value
             * on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.red = (short) value;
            }
        }

        /**
         * Reads green color component from temporal buffer using double data
         * type.
         */
        private class GreenDoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads green color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.green = (short) value;
            }
        }

        /**
         * Reads blue color component from temporal buffer using double data
         * type.
         */
        private class BlueDoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads blue color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.blue = (short) value;
            }
        }

        /**
         * Reads alpha color component from temporal buffer using double data
         * type.
         */
        private class AlphaDoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads alpha color component from temporal buffer and sets its
             * value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.alpha = (short) value;
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using int8 type.
         */
        private class NXint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using int8 type.
         */
        private class NYint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using int8 type.
         */
        private class NZint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using uint8 type.
         */
        private class NXuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using uint8 type.
         */
        private class NYuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using uint8 type.
         */
        private class NZuint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using int16 type.
         */
        private class NXint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using int16 type.
         */
        private class NYint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using int16 type.
         */
        private class NZint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using uint16 type.
         */
        private class NXuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using uint16 type.
         */
        private class NYuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using uint16 type.
         */
        private class NZuint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using int32 type.
         */
        private class NXint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using int32 type.
         */
        private class NYint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using int32 type.
         */
        private class NZint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using int32 type.
         */
        private class NXuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using uint32 type.
         */
        private class NYuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using uint32 type.
         */
        private class NZuint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using float32 type.
         */
        private class NXfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using float32 type.
         */
        private class NYfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using float32 type.
         */
        private class NZfloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using float64 type.
         */
        private class NXfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nX = (float) value;
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using float64 type.
         */
        private class NYfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nY = (float) value;
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using float64 type.
         */
        private class NZfloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nZ = (float) value;
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using char type.
         */
        private class NXcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using char type.
         */
        private class NYcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using char type.
         */
        private class NZcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.get(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using uchar type.
         */
        private class NXucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using uchar type.
         */
        private class NYucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using uchar type.
         */
        private class NZucharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using short type.
         */
        private class NXshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using short type.
         */
        private class NYshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using short type.
         */
        private class NZshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getShort(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using ushort type.
         */
        private class NXushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using ushort type.
         */
        private class NYushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using ushort type.
         */
        private class NZushortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using int type.
         */
        private class NXintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using int type.
         */
        private class NYintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using int type.
         */
        private class NZintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getInt(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using uint type.
         */
        private class NXuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nX = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using uint type.
         */
        private class NYuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nY = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using uint type.
         */
        private class NZuintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.nZ = buffer.getLong(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component form temporal buffer
         * using float type.
         */
        private class NXfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nX = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using float type.
         */
        private class NYfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nY = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using float type.
         */
        private class NZfloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.nZ = buffer.getFloat(0);
            }
        }

        /**
         * Reads a vertex normal x coordinate component from temporal buffer
         * using double type.
         */
        private class NXdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal x coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nX = (float) value;
            }
        }

        /**
         * Reads a vertex normal y coordinate component from temporal buffer
         * using double type.
         */
        private class NYdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal y coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nY = (float) value;
            }
        }

        /**
         * Reads a vertex normal z coordinate component from temporal buffer
         * using double type.
         */
        private class NZdoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads a vertex normal z coordinate from temporal buffer and sets
             * its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.nZ = (float) value;
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * int8 type.
         */
        private class FaceInt8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.get(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * uint8 type.
         */
        private class FaceUint8ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getShort(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * int16 type.
         */
        private class FaceInt16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.getShort(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * uint16 type.
         */
        private class FaceUint16ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getInt(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * int32 type.
         */
        private class FaceInt32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.getInt(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * uint32 type.
         */
        private class FaceUint32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getLong(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * float32 type.
         */
        private class FaceFloat32ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.index = (long) value;
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * float64 type.
         */
        private class FaceFloat64ReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.index = (long) value;
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * char type.
         */
        private class FaceCharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.get(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * uchar type.
         */
        private class FaceUcharReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getShort(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * short type.
         */
        private class FaceShortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.getShort(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * ushort type.
         */
        private class FaceUshortReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getInt(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * int type.
         */
        private class FaceIntReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.index = buffer.getInt(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * uint type.
         */
        private class FaceUintReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // because java doesn't support unsigned types we use the next
                // type that can hold all desired values
                loaderIterator.index = buffer.getLong(0);
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * float type.
         */
        private class FaceFloatReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.index = (long) value;
            }
        }

        /**
         * Reads the index of a face/polygon vertex from temporal buffer using
         * double type.
         */
        private class FaceDoubleReadValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads the index of a face/polygon vertex from temporal buffer and
             * sets its value on current loader iterator.
             *
             * @param buffer Temporal buffer.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.index = (long) value;
            }
        }

        /**
         * Reads an int8 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiInt8ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an int8 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an int8.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final byte value = Byte.parseByte(str);

                    // save to buffer
                    buffer.put(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a uint8 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUint8ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an uint8 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an uint8.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final short value = Short.parseShort(str);

                    // save to buffer
                    buffer.putShort(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads an int16 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiInt16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an int16 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an int16.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final short value = Short.parseShort(str);

                    // save to buffer
                    buffer.putShort(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a uint16 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUint16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an uint16 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an uint16.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final int value = Integer.parseInt(str);

                    // save to buffer
                    buffer.putInt(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads an int32 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiInt32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an int32 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an int32.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final int value = Integer.parseInt(str);

                    // save to buffer
                    buffer.putInt(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a uint32 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUint32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an uint32 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an uint32.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final long value = Long.parseLong(str);

                    // save to buffer
                    buffer.putLong(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a float32 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiFloat32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a float32 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an float32.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final float value = Float.parseFloat(str);

                    // save to buffer
                    buffer.putFloat(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a float64 from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiFloat64ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a float64 value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an float64.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final double value = Double.parseDouble(str);

                    // save to buffer
                    buffer.putDouble(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a char from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiCharReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a char value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a char.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final byte value = Byte.parseByte(str);

                    // save to buffer
                    buffer.put(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a uchar from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUcharReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a uchar value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a uchar.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final short value = Short.parseShort(str);

                    // save to buffer
                    buffer.putShort(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a short from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiShortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a short value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a short.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final short value = Short.parseShort(str);

                    // save to buffer
                    buffer.putShort(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a ushort from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUshortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a ushort value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a ushort.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final int value = Integer.parseInt(str);

                    // save to buffer
                    buffer.putInt(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads an int from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiIntReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into an int value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into an int.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final int value = Integer.parseInt(str);

                    // save to buffer
                    buffer.putInt(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a uint from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiUintReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a uint value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a uint.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // Because Java doesn't support unsigned types we use the next type
                // capable of holding all values
                try {
                    final long value = Long.parseLong(str);

                    // save to buffer
                    buffer.putLong(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a float from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiFloatReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a float value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a float.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final float value = Float.parseFloat(str);

                    // save to buffer
                    buffer.putFloat(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads a double from the file stream of data assuming that file is in
         * ascii text format.
         */
        private class AsciiDoubleReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads next word of text within file at current position and
             * attempts to parse it into a double value.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs or data cannot be
             *                     parsed into a double.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                String str;

                // read word
                do {
                    str = reader.readWord();
                    // loop to avoid empty strings
                } while ((str.isEmpty()) && !reader.isEndOfStream());

                // retrieve word value
                try {
                    final double value = Double.parseDouble(str);

                    // save to buffer
                    buffer.putDouble(0, value);
                } catch (final NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }

        /**
         * Reads an int8 from the file stream of data assuming that file is in
         * binary format.
         */
        private class BinaryInt8ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int8 from the stream of data at current file position
             * and stores the result into provided byte buffer.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final byte value = reader.readByte();

                // save to buffer
                buffer.put(0, value);
            }
        }

        /**
         * Reads a uint8 from the file stream of data assuming that file is in
         * binary format.
         */
        private class BinaryUint8ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint8 from the stream of data at current file position
             * and stores the result into provided byte buffer.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final short value = reader.readUnsignedByte();

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads a char from the file stream of data assuming that file is in
         * binary format.
         */
        private class BinaryCharReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a char from the stream of data at current file position
             * and stores the result into provided byte buffer.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final byte value = reader.readByte();

                // save to buffer
                buffer.put(0, value);
            }
        }

        /**
         * Reads a uchar from the file stream of data assuming that file is in
         * binary format.
         */
        private class BinaryUcharReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uchar from the stream of data at current file position
             * and stores the result into provided byte buffer.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final short value = reader.readUnsignedByte();

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads an int16 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianInt16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int16 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final short value = reader.readShort(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads a uint16 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianUint16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint16 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final int value = reader.readUnsignedShort(
                        EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads an int32 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianInt32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final int value = reader.readInt(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads a uint32 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianUint32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final long value = reader.readUnsignedInt(
                        EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putLong(0, value);
            }
        }

        /**
         * Reads a float32 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianFloat32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final float value = reader.readFloat(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putFloat(0, value);
            }
        }

        /**
         * Reads a float64 from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianFloat64ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float64 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final double value = reader.readDouble(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putDouble(0, value);
            }
        }

        /**
         * Reads a short from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianShortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a short from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final short value = reader.readShort(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads a ushort from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianUshortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a ushort from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final int value = reader.readUnsignedShort(
                        EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads an int from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianIntReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final int value = reader.readInt(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads a uint from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianUintReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final long value = reader.readUnsignedInt(
                        EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putLong(0, value);
            }
        }

        /**
         * Reads a float from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianFloatReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final float value = reader.readFloat(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putFloat(0, value);
            }
        }

        /**
         * Reads a double from the file stream of data assuming that file is in
         * little endian binary format.
         */
        private class BinaryLittleEndianDoubleReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a double from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in little endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final double value = reader.readDouble(EndianType.LITTLE_ENDIAN_TYPE);

                // save to buffer
                buffer.putDouble(0, value);
            }
        }

        /**
         * Reads an int16 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianInt16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int16 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final short value = reader.readShort(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads a uint16 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianUint16ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint16 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final int value = reader.readUnsignedShort(
                        EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads an int32 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianInt32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final int value = reader.readInt(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads a uint32 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianUint32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final long value = reader.readUnsignedInt(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putLong(0, value);
            }
        }

        /**
         * Reads a float32 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianFloat32ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float32 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final float value = reader.readFloat(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putFloat(0, value);
            }
        }

        /**
         * Reads a float64 from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianFloat64ReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float64 from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final double value = reader.readDouble(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putDouble(0, value);
            }
        }

        /**
         * Reads a short from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianShortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a short from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final short value = reader.readShort(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putShort(0, value);
            }
        }

        /**
         * Reads a ushort from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianUshortReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a ushort from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final int value = reader.readUnsignedShort(
                        EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads an int from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianIntReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads an int from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final int value = reader.readInt(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putInt(0, value);
            }
        }

        /**
         * Reads a uint from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianUintReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a uint from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                // Because Java doesn't support unsigned types, we use the next
                // type that can hold all values
                final long value = reader.readUnsignedInt(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putLong(0, value);
            }
        }

        /**
         * Reads a float from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianFloatReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a float from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final float value = reader.readFloat(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putFloat(0, value);
            }
        }

        /**
         * Reads a double from the file stream of data assuming that file is in
         * big endian binary format.
         */
        private class BinaryBigEndianDoubleReadValueFromStreamListener
                implements PLYReadValueFromStreamListener {

            /**
             * Reads a double from the stream of data at current file position
             * and stores the result into provided byte buffer. Stream data is
             * assumed to be in big endian format.
             *
             * @param buffer Buffer where data will be stored.
             * @throws IOException if an I/O error occurs.
             */
            @Override
            public void readFromStream(final ByteBuffer buffer) throws IOException {
                final double value = reader.readDouble(EndianType.BIG_ENDIAN_TYPE);

                // save to buffer
                buffer.putDouble(0, value);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has int8 data type.
         */
        private class FaceInt8ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has int8 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.get(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has uint8 data type.
         */
        private class FaceUint8ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has uint8 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                loaderIterator.listElems = buffer.getShort(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has int16 data type.
         */
        private class FaceInt16ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has int16 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.getShort(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has uint16 data type.
         */
        private class FaceUint16ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has uint16 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                loaderIterator.listElems = buffer.getInt(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has int32 data type.
         */
        private class FaceInt32ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has int32 data type. Value read from buffer
             * will be stored into this PLY loader iterator
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.getInt(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has uint32 data type.
         */
        private class FaceUint32ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has uint32 data type. Value read from buffer
             * will be stored into this PLY loader iterator
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                final long value = buffer.getLong(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has float32 data type.
         */
        private class FaceFloat32ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has float32 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has float64 data type.
         */
        private class FaceFloat64ReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has float64 data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has char data type.
         */
        private class FaceCharReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has char data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.get(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has uchar data type.
         */
        private class FaceUcharReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has uchar data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                loaderIterator.listElems = buffer.getShort(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has short data type.
         */
        private class FaceShortReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has short data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.getShort(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has ushort data type.
         */
        private class FaceUshortReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has ushort data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                loaderIterator.listElems = buffer.getInt(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has int data type.
         */
        private class FaceIntReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has int data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                loaderIterator.listElems = buffer.getInt(0);
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has uint data type.
         */
        private class FaceUintReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has uint data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                // Because Jave doesn't support unsigned types, we use next
                // type capable of holding all values
                final long value = buffer.getLong(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has float data type.
         */
        private class FaceFloatReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has float data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final float value = buffer.getFloat(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Reads length value of a list header element from temporal buffer of
         * data assuming it has double data type.
         */
        private class FaceDoubleReadLengthValueFromBufferListener
                implements PLYReadValueFromBufferListener {

            /**
             * Reads length value of a list header element from temporal buffer
             * of data assuming it has double data type. Value read from buffer
             * will be stored into this PLY loader iterator.
             *
             * @param buffer Temporal buffer of data.
             */
            @Override
            public void readValueFromBuffer(final ByteBuffer buffer) {
                final double value = buffer.getDouble(0);

                loaderIterator.listElems = (int) value;
            }
        }

        /**
         * Sets the listener to read data from the file stream for a given
         * PLY header property and using provided storage mode.
         *
         * @param property    A PLY header property.
         * @param storageMode Storage mode of file.
         * @throws NotAvailableException Raised if the listener cannot be set
         *                               for provided property and storage mode.
         */
        private void setReadValueFromStreamListener(
                final PropertyPLY property,
                final PLYStorageMode storageMode) throws NotAvailableException {
            property.setReadValueFromStreamListener(getReadFromStreamListener(
                    property.getValueType(), storageMode));
        }

        /**
         * Sets the listener to read length value of a property from the file
         * stream for a given PLY header property and using provided storage
         * mode.
         *
         * @param property    A PLY header property.
         * @param storageMode Storage mode of file.
         * @throws NotAvailableException Raised if the listener cannot be set
         *                               for provided property and storage mode.
         */
        private void setReadLengthValueFromStreamListener(
                final PropertyPLY property,
                final PLYStorageMode storageMode) throws NotAvailableException {
            property.setReadLengthValueFromStreamListener(
                    getReadFromStreamListener(property.getLengthType(),
                            storageMode));
        }

        /**
         * Returns a listener to read data from the file stream using provided
         * data type (int8, uint8, int16, uint16, etc.) and storage mode (ascii,
         * little endian or big endian).
         *
         * @param dataType    Data type to read.
         * @param storageMode Storage mode of file.
         * @return A listener.
         * @throws NotAvailableException Raised if the combination of data type
         *                               and storage mode is not supported.
         */
        private PLYReadValueFromStreamListener getReadFromStreamListener(
                final DataTypePLY dataType, final PLYStorageMode storageMode)
                throws NotAvailableException {
            switch (dataType) {
                case PLY_INT8:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiInt8ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                        case PLY_BIG_ENDIAN:
                            return new BinaryInt8ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_UINT8:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUint8ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                        case PLY_BIG_ENDIAN:
                            return new BinaryUint8ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_INT16:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiInt16ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianInt16ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianInt16ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_UINT16:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUint16ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianUint16ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianUint16ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_INT32:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiInt32ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianInt32ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianInt32ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_UINT32:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUint32ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianUint32ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianUint32ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_FLOAT32:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiFloat32ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianFloat32ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianFloat32ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_FLOAT64:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiFloat64ReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianFloat64ReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianFloat64ReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_CHAR:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiCharReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                        case PLY_BIG_ENDIAN:
                            return new BinaryCharReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_UCHAR:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUcharReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                        case PLY_BIG_ENDIAN:
                            return new BinaryUcharReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_SHORT:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiShortReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianShortReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianShortReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_USHORT:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUshortReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianUshortReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianUshortReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_INT:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiIntReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianIntReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianIntReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_UINT:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiUintReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianUintReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianUintReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_FLOAT:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiFloatReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianFloatReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianFloatReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                case PLY_DOUBLE:
                    switch (storageMode) {
                        case PLY_ASCII:
                            return new AsciiDoubleReadValueFromStreamListener();
                        case PLY_LITTLE_ENDIAN:
                            return new BinaryLittleEndianDoubleReadValueFromStreamListener();
                        case PLY_BIG_ENDIAN:
                            return new BinaryBigEndianDoubleReadValueFromStreamListener();
                        default:
                            throw new NotAvailableException();
                    }

                default:
                    throw new NotAvailableException();
            }
        }

        /**
         * Sets the listener to read length value from temporal buffer to
         * provided property.
         *
         * @param property A PLY header property.
         * @throws NotAvailableException Raised if a listener cannot be set
         *                               for given property because its data type is not supported.
         */
        private void setReadLengthValueFromBufferListener(final PropertyPLY property)
                throws NotAvailableException {
            switch (property.getLengthType()) {
                case PLY_INT8:
                    property.setReadLengthValueFromBufferListener(
                            new FaceInt8ReadLengthValueFromBufferListener());
                    break;
                case PLY_UINT8:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUint8ReadLengthValueFromBufferListener());
                    break;
                case PLY_INT16:
                    property.setReadLengthValueFromBufferListener(
                            new FaceInt16ReadLengthValueFromBufferListener());
                    break;
                case PLY_UINT16:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUint16ReadLengthValueFromBufferListener());
                    break;
                case PLY_INT32:
                    property.setReadLengthValueFromBufferListener(
                            new FaceInt32ReadLengthValueFromBufferListener());
                    break;
                case PLY_UINT32:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUint32ReadLengthValueFromBufferListener());
                    break;
                case PLY_FLOAT32:
                    property.setReadLengthValueFromBufferListener(
                            new FaceFloat32ReadLengthValueFromBufferListener());
                    break;
                case PLY_FLOAT64:
                    property.setReadLengthValueFromBufferListener(
                            new FaceFloat64ReadLengthValueFromBufferListener());
                    break;
                case PLY_CHAR:
                    property.setReadLengthValueFromBufferListener(
                            new FaceCharReadLengthValueFromBufferListener());
                    break;
                case PLY_UCHAR:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUcharReadLengthValueFromBufferListener());
                    break;
                case PLY_SHORT:
                    property.setReadLengthValueFromBufferListener(
                            new FaceShortReadLengthValueFromBufferListener());
                    break;
                case PLY_USHORT:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUshortReadLengthValueFromBufferListener());
                    break;
                case PLY_INT:
                    property.setReadLengthValueFromBufferListener(
                            new FaceIntReadLengthValueFromBufferListener());
                    break;
                case PLY_UINT:
                    property.setReadLengthValueFromBufferListener(
                            new FaceUintReadLengthValueFromBufferListener());
                    break;
                case PLY_FLOAT:
                    property.setReadLengthValueFromBufferListener(
                            new FaceFloatReadLengthValueFromBufferListener());
                    break;
                case PLY_DOUBLE:
                    property.setReadLengthValueFromBufferListener(
                            new FaceDoubleReadLengthValueFromBufferListener());
                    break;
                default:
                    throw new NotAvailableException();
            }
        }
    }
}

//TODO: add cancel method
//TODO: add unit tests for loader ply
