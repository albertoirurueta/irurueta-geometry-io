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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the shared behaviour of {@link Loader} using {@link LoaderOBJ} as the concrete implementation.
 */
class LoaderTest {

    /**
     * Size in bytes of files that must not be kept in memory.
     */
    private static final long LARGE_FILE_SIZE = Loader.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;

    /**
     * Content written into small temporary files.
     */
    private static final String CONTENT = "# empty\n";

    /**
     * Temporary folder where generated files are stored.
     */
    @TempDir
    Path tempDir;

    /**
     * Checks that a file smaller than the limit is read through a memory mapped reader.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void constructor_whenSmallFile_usesMappedReader() throws IOException {
        final var f = createSmallFile("small.txt");

        try (final var loader = new LoaderOBJ(f)) {
            assertTrue(loader.hasFile());
            assertInstanceOf(MappedFileReaderAndWriter.class, loader.reader);
        }
    }

    /**
     * Checks that a file reaching the limit is read through a regular file reader.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void constructor_whenLargeFile_usesFileReader() throws IOException {
        final var f = createLargeFile("large.txt");

        try (final var loader = new LoaderOBJ(f)) {
            assertTrue(loader.hasFile());
            assertInstanceOf(FileReaderAndWriter.class, loader.reader);
        }
    }

    /**
     * Checks that a file and listener constructor uses a regular file reader for large files.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void constructor_whenLargeFileAndListener_usesFileReader() throws IOException {
        final var f = createLargeFile("large.txt");
        final var small = createSmallFile("small.txt");

        try (final var loader = new LoaderOBJ(f, (LoaderListener) null)) {
            assertInstanceOf(FileReaderAndWriter.class, loader.reader);
            assertNull(loader.getListener());
        }
        try (final var loader = new LoaderOBJ(small, (LoaderListener) null)) {
            assertInstanceOf(MappedFileReaderAndWriter.class, loader.reader);
        }
    }

    /**
     * Checks that changing the file size limit while unlocked is stored and that it is rejected while locked.
     *
     * @throws IOException     if an I/O error occurs.
     * @throws LockedException if the loader is unexpectedly locked.
     */
    @Test
    void setFileSizeLimitToKeepInMemory_whenLocked_throwsLockedException() throws IOException, LockedException {
        try (final var loader = new LoaderOBJ()) {
            assertEquals(LARGE_FILE_SIZE, loader.getFileSizeLimitToKeepInMemory());

            loader.setFileSizeLimitToKeepInMemory(1L);
            assertEquals(1L, loader.getFileSizeLimitToKeepInMemory());

            loader.setLocked(true);
            assertTrue(loader.isLocked());
            assertThrows(LockedException.class, () -> loader.setFileSizeLimitToKeepInMemory(2L));
            assertEquals(1L, loader.getFileSizeLimitToKeepInMemory());
        }
    }

    /**
     * Checks that setting a file replaces and closes any previous reader, and that the limit selects the reader type.
     *
     * @throws IOException     if an I/O error occurs.
     * @throws LockedException if the loader is unexpectedly locked.
     */
    @Test
    void setFile_whenPreviousFileAndLimitChanges_replacesReader() throws IOException, LockedException {
        final var f = createSmallFile("small.txt");

        try (final var loader = new LoaderOBJ()) {
            assertFalse(loader.hasFile());

            loader.setFile(f);
            assertTrue(loader.hasFile());
            assertInstanceOf(MappedFileReaderAndWriter.class, loader.reader);

            // a limit of zero forces regular file reader
            loader.setFileSizeLimitToKeepInMemory(0L);
            loader.setFile(f);
            assertInstanceOf(FileReaderAndWriter.class, loader.reader);
        }
    }

    /**
     * Checks that setting a file or a listener while locked throws.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void setFile_whenLocked_throwsLockedException() throws IOException {
        final var f = createSmallFile("small.txt");

        try (final var loader = new LoaderOBJ()) {
            loader.setLocked(true);

            assertThrows(LockedException.class, () -> loader.setFile(f));
            assertThrows(LockedException.class, () -> loader.setListener(null));
            assertFalse(loader.hasFile());
        }
    }

    /**
     * Checks that closing releases the reader and that closing twice is harmless.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void close_whenCalledTwice_releasesReaderOnce() throws IOException {
        final var f = createSmallFile("small.txt");

        final var loader = new LoaderOBJ(f);
        assertTrue(loader.hasFile());

        loader.close();
        assertFalse(loader.hasFile());

        // closing again does nothing
        loader.close();
        assertFalse(loader.hasFile());
    }

    /**
     * Creates a small file in the temporary folder.
     *
     * @param name name of the file.
     * @return created file.
     * @throws IOException if an I/O error occurs.
     */
    private File createSmallFile(final String name) throws IOException {
        final var f = tempDir.resolve(name);
        Files.writeString(f, CONTENT);
        return f.toFile();
    }

    /**
     * Creates a (sparse) file whose size reaches the limit to keep files in memory.
     *
     * @param name name of the file.
     * @return created file.
     * @throws IOException if an I/O error occurs.
     */
    private File createLargeFile(final String name) throws IOException {
        final var f = tempDir.resolve(name).toFile();
        try (final var raf = new RandomAccessFile(f, "rw")) {
            raf.setLength(LARGE_FILE_SIZE);
        }
        return f;
    }
}
