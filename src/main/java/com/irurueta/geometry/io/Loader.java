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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Abstract class defining the interface for file loaders.
 * Specific implementations must be done for each file format to be supported.
 */
public abstract class Loader implements Closeable {

    /**
     * Default limit of bytes to keep mapped in memory.
     */
    public static final long DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY = 50000000;

    /**
     * Instance in charge of reading data from file.
     */
    protected AbstractFileReaderAndWriter reader;

    /**
     * Boolean indicating that this instance is locked because decoding is being
     * done.
     */
    protected boolean locked;

    /**
     * Listener in charge of notifying when the decoding starts, ends or
     * progress events.
     */
    protected LoaderListener listener;

    /**
     * File to be read.
     */
    protected File file;

    /**
     * Limit of bytes to keep mapped in memory. If provided file exceeds this
     * value, then it is not mapped into memory.
     */
    private long fileSizeLimitToKeepInMemory;

    /**
     * Default Constructor.
     */
    protected Loader() {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        reader = null;
        locked = false;
        listener = null;
    }

    /**
     * Constructor.
     *
     * @param f file to be read.
     * @throws IOException raised if provided file does not exist or an I/O
     *                     exception occurs.
     */
    protected Loader(final File f) throws IOException {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        file = f;
        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
        locked = false;
        listener = null;
    }

    /**
     * Constructor.
     *
     * @param listener listener to notify start, end and progress events.
     */
    protected Loader(final LoaderListener listener) {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        reader = null;
        locked = false;
        this.listener = listener;
    }

    /**
     * Constructor.
     *
     * @param f        file to be read.
     * @param listener listener to notify start, end and progress events.
     * @throws IOException raised if provided file does not exist or an I/O
     *                     exception occurs.
     */
    protected Loader(final File f, final LoaderListener listener) throws IOException {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        file = f;
        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
        locked = false;
        this.listener = listener;
    }

    /**
     * Get maximum size (in bytes) to determine whether a file is completely
     * cached in memory (if lower than maximum size), or if it is just streamed
     * (if greater than maximum size).
     *
     * @return Maximum size to determine whether file is cached in memory or not.
     */
    public long getFileSizeLimitToKeepInMemory() {
        return fileSizeLimitToKeepInMemory;
    }

    /**
     * Sets maximum size (in bytes) to determine whether a file is completely
     * cached in memory (if lower than maximum size), or if it is just streamed
     * (if greater than maximum size).
     *
     * @param fileSizeLimitToKeepInMemory maximum size to determine whether file
     *                                    is cached in memory or not.
     * @throws LockedException if loader is locked because it is currently
     *                         processing a file.
     */
    public void setFileSizeLimitToKeepInMemory(final long fileSizeLimitToKeepInMemory) throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }

        this.fileSizeLimitToKeepInMemory = fileSizeLimitToKeepInMemory;
    }

    /**
     * Indicates whether a file to be loaded has already been set.
     *
     * @return True if file has already been provided, false otherwise.
     */
    public boolean hasFile() {
        return reader != null;
    }

    /**
     * Sets file to be loaded.
     *
     * @param f file to be loaded.
     * @throws LockedException raised if this instance is loaded because a file
     *                         is already being loaded.
     * @throws IOException     raised if provided file does not exist or if an I/O
     *                         exception occurs.
     */
    @SuppressWarnings("DuplicatedCode")
    public void setFile(final File f) throws LockedException, IOException {
        if (isLocked()) {
            throw new LockedException();
        }
        file = f;

        if (reader != null) {
            // close previous file
            reader.close();
        }

        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
    }

    /**
     * Closes file provided to this loader.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    /**
     * Determines whether this instance is locked.
     * A loader remains locked while decoding of a file is being done.
     * This instance will remain locked once the loading process starts until
     * it finishes either successfully or not.
     * While this instance remains locked no parameters can be changed,
     * otherwise a LockedException will be raised.
     *
     * @return True if instance is locked, false otherwise.
     */
    public synchronized boolean isLocked() {
        return locked;
    }

    /**
     * Sets the lock value of this instance so that no parameters cannot be
     * changed until this instance is unlocked.
     *
     * @param locked value that determines whether this instance is locked or
     *               not.
     */
    protected synchronized void setLocked(final boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns listener of this instance.
     * A listener notifies of start, end and progress change events.
     *
     * @return listener of this instance.
     */
    public LoaderListener getListener() {
        return listener;
    }

    /**
     * Sets listener of this instance.
     * A listener notifies of start, end and progress change events.
     *
     * @param listener listener of this instance.
     * @throws LockedException Raised if this instance is already locked.
     */
    public void setListener(final LoaderListener listener) throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.listener = listener;
    }

    /**
     * Determines whether enough parameters have been set so that the loading
     * process can start.
     *
     * @return True if this instance is ready to start loading a file, false
     * otherwise.
     */
    public abstract boolean isReady();

    /**
     * Returns mesh format this instance is capable of loading.
     *
     * @return Mesh format.
     */
    public abstract MeshFormat getMeshFormat();

    /**
     * Determines if provided file is a valid file that can be read by this
     * loader.
     *
     * @return true if file is valid, false otherwise.
     * @throws LockedException raised if this instance is already locked.
     * @throws IOException     if an I/O error occurs.
     */
    public abstract boolean isValidFile() throws LockedException, IOException;

    /**
     * Starts the loading process of provided file.
     * This method returns a LoaderIterator to start the iterative process to
     * load a file in small chunks of data.
     *
     * @return a loader iterator to read the file in a step-by-step process.
     * @throws LockedException   raised if this instance is already locked.
     * @throws NotReadyException raised if this instance is not yet ready.
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if file is corrupted or cannot be interpreted.
     */
    public abstract LoaderIterator load() throws LockedException, NotReadyException, IOException, LoaderException;
}
