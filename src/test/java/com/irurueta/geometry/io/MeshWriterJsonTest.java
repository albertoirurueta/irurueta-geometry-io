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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshWriterJsonTest implements MeshWriterJsonListener {

    private static final String INPUT_FOLDER = "./src/test/java/com/irurueta/geometry/io/";

    private static final String TMP_FOLDER = "./src/test/java/com/irurueta/geometry/io/tmp/";

    /**
     * Name of the texture image used by generated materials.
     */
    private static final String TEXTURE_NAME = "texture.jpg";

    /**
     * Lines of a small mesh with a triangle using a material.
     */
    private static final String[] OBJ_LINES = {"mtllib mesh.mtl", "g default", "v 0 0 0", "v 1 0 0", "v 0 1 0",
            "vt 0 0", "vt 1 0", "vt 0 1", "vn 0 0 1", "usemtl m1", "f 1/1/1 2/2/1 3/3/1"};

    /**
     * Lines of a mesh containing malformed vertices.
     */
    private static final String[] MALFORMED_OBJ_LINES = {"mtllib mesh.mtl", "g default", "v a b c", "v 1 0 0",
            "v 0 1 0", "usemtl m1", "f 1 2 3"};

    /**
     * Lines of a material library having all kinds of texture maps.
     */
    private static final String[] MTL_LINES = {"newmtl m1", "illum 2", "Ka 0.1 0.1 0.1", "Kd 0.5 0.5 0.5",
            "Ks 0.2 0.2 0.2", "d 0.5", "map_Ka texture.jpg", "map_Kd texture.jpg", "map_Ks texture.jpg",
            "map_d texture.jpg", "bump texture.jpg"};

    /**
     * Lines of a mesh whose vertices, normals and texture coordinates are not finite numbers.
     */
    private static final String[] NON_FINITE_OBJ_LINES = {"mtllib mesh.mtl", "g default", "v NaN 0 0", "v 1 0 0",
            "v 0 1 0", "vt NaN 0", "vt 1 0", "vt 0 1", "vn NaN 0 1", "usemtl m1", "f 1/1/1 2/2/1 3/3/1"};

    /**
     * Temporary folder where generated files are stored.
     */
    @TempDir
    Path tempDir;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @BeforeAll
    static void setUpClass() {
        // create folder for generated files
        final var folder = new File(TMP_FOLDER);

        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();
    }

    @AfterAll
    static void tearDownClass() {
        // remove any remaining files in thumbnails folder
        final var folder = new File(TMP_FOLDER);
        final var files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (final var f : files) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }

        // delete created folder
        //noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    @Test
    void testConstructors() throws IOException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        // test constructor with output stream and loader
        var writer = new MeshWriterJson(loader, outStream);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());
        assertEquals(MeshWriterJson.DEFAULT_EMBED_TEXTURES, writer.isEmbedTexturesEnabled());
        assertEquals(MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL, writer.isRemoteTextureUrlEnabled());
        assertEquals(MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID, writer.isRemoteTextureIdEnabled());
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(outStream, writer.getStream());
        assertNull(writer.getListener());

        final var listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(final MeshWriter writer) {
                // no action needed
            }

            @Override
            public void onWriteEnd(final MeshWriter writer) {
                // no action needed
            }

            @Override
            public void onWriteProgressChange(final MeshWriter writer, final float progress) {
                // no action needed
            }

            @Override
            public File onMaterialFileRequested(final MeshWriter writer, final String path) {
                return null;
            }

            @Override
            public File onValidateTexture(final MeshWriter writer, final Texture texture) {
                return null;
            }

            @Override
            public void onDidValidateTexture(final MeshWriter writer, final File f) {
                // no action needed
            }

            @Override
            public File onTextureReceived(final MeshWriter writer, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(
                    final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public void onTextureDataProcessed(
                    final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
                // no action needed
            }

            @Override
            public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
                // no action needed
            }
        };

        writer = new MeshWriterJson(loader, outStream, listener);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(outStream, writer.getStream());
        assertEquals(listener, writer.getListener());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    void testGetSetCharset() throws IOException, LockedException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        final var writer = new MeshWriterJson(loader, outStream);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());

        // set new charset
        final var charset = StandardCharsets.ISO_8859_1;
        writer.setCharset(charset);
        assertEquals(charset, writer.getCharset());
        assertFalse(writer.isDefaultCharsetBeingUsed());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    void testGetSetEmbedTexturesEnabled() throws IOException, LockedException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        final var writer = new MeshWriterJson(loader, outStream);
        assertEquals(MeshWriterJson.DEFAULT_EMBED_TEXTURES, writer.isEmbedTexturesEnabled());

        // set new value
        writer.setEmbedTexturedEnabled(!MeshWriterJson.DEFAULT_EMBED_TEXTURES);
        assertEquals(!MeshWriterJson.DEFAULT_EMBED_TEXTURES, writer.isEmbedTexturesEnabled());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    void testGetSetRemoteTextureUrlEnabled() throws IOException, LockedException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        final var writer = new MeshWriterJson(loader, outStream);
        assertEquals(MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL, writer.isRemoteTextureUrlEnabled());

        // set new value
        writer.setRemoteTextureUrlEnabled(!MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL);
        assertEquals(!MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL, writer.isRemoteTextureUrlEnabled());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    void testGetSetRemoteTextureIdEnabled() throws IOException, LockedException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        final var writer = new MeshWriterJson(loader, outStream);
        assertEquals(MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID, writer.isRemoteTextureIdEnabled());

        // set new value
        writer.setRemoteTextureIdEnabled(!MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID);
        assertEquals(!MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID, writer.isRemoteTextureIdEnabled());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    void testGetSetListener() throws IOException, LockedException {
        final var outF = new File(TMP_FOLDER, "booksBinary.json");
        final var inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        final var writer = new MeshWriterJson(loader, outStream);
        assertNull(writer.getListener());

        // set new listener
        final var listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(final MeshWriter writer) {
                // no action needed
            }

            @Override
            public void onWriteEnd(final MeshWriter writer) {
                // no action needed
            }

            @Override
            public void onWriteProgressChange(final MeshWriter writer, final float progress) {
                // no action needed
            }

            @Override
            public File onMaterialFileRequested(final MeshWriter writer, final String path) {
                return null;
            }

            @Override
            public File onValidateTexture(final MeshWriter writer, final Texture texture) {
                return null;
            }

            @Override
            public void onDidValidateTexture(final MeshWriter writer, final File f) {
                // no action needed
            }

            @Override
            public File onTextureReceived(final MeshWriter writer, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(
                    final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public void onTextureDataProcessed(
                    final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
                // no action needed
            }

            @Override
            public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
                // no action needed
            }
        };

        writer.setListener(listener);
        assertEquals(listener, writer.getListener());

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @ParameterizedTest(name = "{index} - inputFile = {0}, outputFile = {1}")
    @CsvSource({"randomAscii.ply,randomAscii.json",
            "randomLittle.ply,randomLittle.json",
            "randomBig.ply,randomBig.json",
            "booksBinary.ply,booksBinary.json",
            "booksAscii.ply,booksAscii.json",
            "pilarAscii.ply,pilarAscii.json",
            "pilarLittleEndian.ply,pilarLittleEndian.json",
            "pilarBigEndian.ply,pilarBigEndian.json"})
    void testWriteAndIsReadyPly(final String inputFile, final String outputFile) throws IOException, LockedException,
            LoaderException, NotReadyException {
        final var outF = new File(TMP_FOLDER, outputFile);
        final var inF = new File(INPUT_FOLDER, inputFile);

        final var loader = new LoaderPLY(inF);
        final var outStream = new FileOutputStream(outF);

        var writer = new MeshWriterJson(loader, outStream);
        writer.setListener(this);
        // enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
        assertTrue(writer.isReady());

        // write into json
        writer.write();
        outStream.close();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        assertTrue(outF.exists());
        assertTrue(outF.delete());

        // Force NotReadyException
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
    }

    @ParameterizedTest(name = "{index} - inputFile = {0}, outputFile = {1}")
    @CsvSource({"books.obj,booksObj.json",
            "pitcher.obj,pitcherObj.json",
            "potro.obj,potroObj.json",
            "M1112.obj,M1112.json",
            "newObject.obj,newObject.json"})
    void testWriteAndIsReadyObj(final String inputFile, final String outputFile) throws IOException, LockedException,
            LoaderException, NotReadyException {
        final var outF = new File(TMP_FOLDER, outputFile);
        final var inF = new File(INPUT_FOLDER, inputFile);

        final var loader = new LoaderOBJ(inF);
        final var outStream = new FileOutputStream(outF);

        var writer = new MeshWriterJson(loader, outStream);
        writer.setListener(this);
        // enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
        assertTrue(writer.isReady());

        // write into json
        writer.write();
        outStream.close();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        assertTrue(outF.exists());
        assertTrue(outF.delete());

        // Force NotReadyException
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
    }

    @ParameterizedTest(name = "{index} - inputFile = {0}, outputFile = {1}")
    @CsvSource({"booksBinary.stl,booksBinaryStl.json",
            "booksAscii.stl,booksAsciiStl.json"})
    void testWriteAndIsReadyStl(final String inputFile, final String outputFile) throws IOException, LockedException,
            LoaderException, NotReadyException {
        final var outF = new File(TMP_FOLDER, outputFile);
        final var inF = new File(INPUT_FOLDER, inputFile);

        final var loader = new LoaderSTL(inF);
        final var outStream = new FileOutputStream(outF);

        var writer = new MeshWriterJson(loader, outStream);
        writer.setListener(this);
        // enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
        assertTrue(writer.isReady());

        // write into json
        writer.write();
        outStream.close();

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        assertTrue(outF.exists());
        assertTrue(outF.delete());

        // Force NotReadyException
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        assertThrows(NotReadyException.class, writer::write);
    }

    /**
     * Checks that a material using every kind of texture map is written when a listener provides the files.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenAllTextureMapsAvailable_validatesEveryTexture() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var listener = new ConfigurableListener(tempDir.toFile());
        final var out = new ByteArrayOutputStream();

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out, listener);
        writer.write();

        assertTrue(out.size() > 0);
        assertEquals(1, listener.startCount);
        assertEquals(1, listener.endCount);
        assertTrue(listener.progressCount > 0);
        assertTrue(listener.validateTextureCount > 0);
        assertEquals(listener.validateTextureCount, listener.didValidateTextureCount);
        assertFalse(writer.isLocked());
    }

    /**
     * Checks that materials and textures are ignored when no listener is set.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenNoListener_ignoresMaterials() throws IOException, LoaderException, NotReadyException,
            LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var out = new ByteArrayOutputStream();

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out);
        writer.write();

        assertTrue(out.size() > 0);
    }

    /**
     * Checks that a listener returning no material file or a missing one results in the material being ignored.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenMaterialFileMissing_ignoresMaterials() throws IOException, LoaderException, NotReadyException,
            LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);

        // listener does not provide a material file
        final var listener1 = new ConfigurableListener(null);
        final var out1 = new ByteArrayOutputStream();
        new MeshWriterJson(new LoaderOBJ(objFile), out1, listener1).write();
        assertTrue(out1.size() > 0);
        assertEquals(0, listener1.validateTextureCount);

        // listener provides a file that does not exist
        final var listener2 = new ConfigurableListener(tempDir.toFile());
        listener2.materialFile = new File(tempDir.toFile(), "missing.mtl");
        final var out2 = new ByteArrayOutputStream();
        new MeshWriterJson(new LoaderOBJ(objFile), out2, listener2).write();
        assertTrue(out2.size() > 0);
        assertEquals(0, listener2.validateTextureCount);
    }

    /**
     * Checks that an invalid texture makes the writer fail, and that a listener providing no texture file is accepted.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenTextureInvalidOrWithoutFile_failsOrSkipsTexture() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);

        final var listener1 = new ConfigurableListener(tempDir.toFile());
        listener1.textureValid = false;
        final var writer1 = new MeshWriterJson(new LoaderOBJ(objFile), new ByteArrayOutputStream(), listener1);
        // an invalid texture makes the material loader, and hence the writer, fail
        assertThrows(LoaderException.class, writer1::write);
        assertTrue(listener1.validateTextureCount > 0);
        assertEquals(0, listener1.didValidateTextureCount);

        final var listener2 = new ConfigurableListener(tempDir.toFile());
        listener2.noTextureFile = true;
        new MeshWriterJson(new LoaderOBJ(objFile), new ByteArrayOutputStream(), listener2).write();
        assertTrue(listener2.validateTextureCount > 0);
        assertEquals(0, listener2.didValidateTextureCount);
    }

    /**
     * Checks that texture data coming from a binary file is dispatched to the listener, and that a listener returning
     * a converted file, no file at all, or no listener, are all handled.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenSourceIsBinaryWithTextures_notifiesTextureCallbacks() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var binaryFile = createBinaryWithTextures();

        // listener providing files for received textures
        final var listener1 = new ConfigurableListener(tempDir.toFile());
        final var out1 = new ByteArrayOutputStream();
        new MeshWriterJson(new LoaderBinary(binaryFile), out1, listener1).write();
        assertTrue(out1.size() > 0);
        assertTrue(listener1.textureReceivedCount > 0);
        assertEquals(listener1.textureReceivedCount, listener1.textureDataAvailableCount);
        assertEquals(listener1.textureReceivedCount, listener1.textureDataProcessedCount);

        // listener that does not provide files for received textures
        final var listener2 = new ConfigurableListener(tempDir.toFile());
        listener2.noReceivedFile = true;
        new MeshWriterJson(new LoaderBinary(binaryFile), new ByteArrayOutputStream(), listener2).write();
        assertTrue(listener2.textureReceivedCount > 0);
        assertEquals(0, listener2.textureDataAvailableCount);

        // listener returning no converted file
        final var listener3 = new ConfigurableListener(tempDir.toFile());
        listener3.noConvertedFile = true;
        new MeshWriterJson(new LoaderBinary(binaryFile), new ByteArrayOutputStream(), listener3).write();
        assertTrue(listener3.textureDataAvailableCount > 0);
        assertEquals(0, listener3.textureDataProcessedCount);

        // listener returning a converted file that does not exist
        final var listener4 = new ConfigurableListener(tempDir.toFile());
        listener4.convertedFile = new File(tempDir.toFile(), "missing.jpg");
        final var writer4 = new MeshWriterJson(new LoaderBinary(binaryFile), new ByteArrayOutputStream(), listener4);
        // embedded texture cannot be read, which is reported as a loader failure
        assertThrows(LoaderException.class, writer4::write);
        assertTrue(listener4.textureDataAvailableCount > 0);

        // no listener
        final var out5 = new ByteArrayOutputStream();
        new MeshWriterJson(new LoaderBinary(binaryFile), out5).write();
        assertTrue(out5.size() > 0);
    }

    /**
     * Checks that I/O errors of the output stream are propagated by the writer.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void write_whenStreamFails_throwsIOException() throws IOException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var stream = new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                throw new IOException();
            }
        };

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), stream);
        assertThrows(IOException.class, writer::write);
    }

    /**
     * Checks that malformed mesh content makes the writer fail.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    void write_whenMeshContentIsMalformed_throwsException() throws IOException {
        final var objFile = createObjWithAllTextureMaps(MALFORMED_OBJ_LINES);

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), new ByteArrayOutputStream());
        assertThrows(Exception.class, writer::write);
    }

    /**
     * Checks that non finite values and a custom charset are written, and remote ids and urls and embedded textures
     * are supported.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenNonFiniteValuesAndCustomCharset_replacesValuesByMaxFloat() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(NON_FINITE_OBJ_LINES);
        final var out = new ByteArrayOutputStream();

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out);
        writer.setCharset(StandardCharsets.UTF_8);
        writer.write();

        final var json = out.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains(Float.toString(Float.MAX_VALUE)));
        assertFalse(json.contains("NaN"));
    }

    /**
     * Checks that remote urls, remote ids and embedded data are written for textures.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenRemoteTextureAndEmbedEnabled_writesTextureProperties() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var listener = new ConfigurableListener(tempDir.toFile());
        final var out = new ByteArrayOutputStream();

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out, listener);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
        writer.setEmbedTexturedEnabled(true);
        writer.write();

        final var json = out.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"remoteUrl\""));
        assertTrue(json.contains("\"remoteId\""));
        assertTrue(json.contains("\"data\""));
        assertTrue(json.contains("\"ambientTextureId\""));
        assertTrue(json.contains("\"bumpTextureId\""));
    }

    /**
     * Checks that remote url and id are omitted when the listener returns no value.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenRemoteValuesAreNull_omitsRemoteProperties() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var listener = new ConfigurableListener(tempDir.toFile());
        listener.noRemoteValues = true;
        final var out = new ByteArrayOutputStream();

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out, listener);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
        writer.write();

        final var json = out.toString(StandardCharsets.UTF_8);
        assertFalse(json.contains("\"remoteUrl\""));
        assertFalse(json.contains("\"remoteId\""));
    }

    /**
     * Checks that a missing texture file is reported as a loader failure when embedding textures.
     *
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    @Test
    void write_whenEmbeddedTextureFileMissing_throwsLoaderException() throws IOException, LoaderException,
            NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var listener = new ConfigurableListener(tempDir.toFile());
        final var out = new ByteArrayOutputStream();
        Files.delete(tempDir.resolve(TEXTURE_NAME));

        final var writer = new MeshWriterJson(new LoaderOBJ(objFile), out, listener);
        writer.setEmbedTexturedEnabled(true);
        // texture cannot be read and the material loader reports it as invalid
        assertThrows(LoaderException.class, writer::write);
        assertTrue(listener.validateTextureCount > 0);
    }

    @Override
    public void onWriteStart(final MeshWriter writer) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        testLocked((MeshWriterJson) writer);
    }

    @Override
    public void onWriteEnd(final MeshWriter writer) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        testLocked((MeshWriterJson) writer);
    }

    @Override
    public void onWriteProgressChange(final MeshWriter writer, final float progress) {
        if ((progress < 0.0) || (progress > 1.0)) {
            progressValid = false;
        }
        if (progress < previousProgress) {
            progressValid = false;
        }
        previousProgress = progress;

        testLocked((MeshWriterJson) writer);
    }

    @Override
    public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
        testLocked((MeshWriterJson) writer);
    }


    // FOR OBJ LOADER
    @Override
    public File onMaterialFileRequested(final MeshWriter writer, final String path) {
        final var origF = new File(path);
        return new File(INPUT_FOLDER, origF.getName());
    }

    @Override
    public File onValidateTexture(final MeshWriter writer, final Texture texture) {
        final var origF = new File(texture.getFileName());
        final var inputTextureFile = new File(INPUT_FOLDER, origF.getName());
        // image needs to be in jpg format, if it isn't here we have a chance
        // to convert it
        texture.setValid(true);
        return inputTextureFile;
    }

    @Override
    public void onDidValidateTexture(final MeshWriter writer, final File f) {
        // normally converted textures files need to be deleted, however because
        // we are using an input file as is (because it is already in jpg format)
        // we will not delete it
    }

    @Override
    public File onTextureReceived(final MeshWriter writer, final int textureWidth, final int textureHeight) {
        // generate a temporal file in TMP_FOLDER where texture data will be
        // stored
        try {
            // FOR BINARY LOADER
            return File.createTempFile("tex", ".jpg", new File(TMP_FOLDER));
        } catch (final IOException ignore) {
            return null;
        }
    }

    @Override
    public File onTextureDataAvailable(
            final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
        // texture file needs to be rewritten into JPG format, since the data
        // contained in that file will be used by the MeshWriter.
        // Because textures are already in JPG format in this test, there is no
        // need to make the conversion
        return textureFile;
    }

    @Override
    public void onTextureDataProcessed(
            final MeshWriter writer, final File textureFile, final int textureWidth, final int textureHeight) {
        // this method is called to give an opportunity to delete any generated
        // texture files

        //noinspection ResultOfMethodCallIgnored
        textureFile.delete();
    }

    @Override
    public String onRemoteTextureUrlRequested(
            final MeshWriterJson writer, final Texture texture, final File textureFile) {
        final var uuid = UUID.randomUUID();
        return "https://www.irurueta.com/image?uuid=" + uuid + "&id=" + texture.getId();
    }

    @Override
    public String onRemoteTextureIdRequested(
            final MeshWriterJson writer, final Texture texture, final File textureFile) {
        return UUID.randomUUID().toString();
    }


    private void testLocked(final MeshWriterJson writer) {
        if (!writer.isLocked()) {
            lockedValid = false;
        }

        try {
            writer.setListener(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.write();
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.setCharset(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.setEmbedTexturedEnabled(true);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.setRemoteTextureUrlEnabled(true);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.setRemoteTextureIdEnabled(true);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }
    }

    private void resetListener() {
        startValid = endValid = progressValid = lockedValid = true;
        startCounter = endCounter = 0;
        previousProgress = 0.0f;
    }

    private boolean isStartValid() {
        return startValid;
    }

    private boolean isEndValid() {
        return endValid;
    }

    private boolean isProgressValid() {
        return progressValid;
    }

    private boolean isLockedValid() {
        return lockedValid;
    }

    /**
     * Creates a binary mesh file with every texture map embedded, using a mesh with textures.
     *
     * @return created binary file.
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if the mesh cannot be loaded.
     * @throws NotReadyException if the writer is not ready.
     * @throws LockedException   if the writer is locked.
     */
    private File createBinaryWithTextures() throws IOException, LoaderException, NotReadyException, LockedException {
        final var objFile = createObjWithAllTextureMaps(OBJ_LINES);
        final var binaryFile = tempDir.resolve("mesh.bin").toFile();
        final var listener = new ConfigurableListener(tempDir.toFile());
        try (final var stream = new FileOutputStream(binaryFile)) {
            new MeshWriterBinary(new LoaderOBJ(objFile), stream, listener).write();
        }
        return binaryFile;
    }

    /**
     * Creates a mesh file, its material library and the texture image in the temporary folder.
     *
     * @param lines lines of the mesh file.
     * @return created mesh file.
     * @throws IOException if an I/O error occurs.
     */
    private File createObjWithAllTextureMaps(final String[] lines) throws IOException {
        final var objFile = tempDir.resolve("mesh.obj");
        Files.write(objFile, Arrays.asList(lines));
        Files.write(tempDir.resolve("mesh.mtl"), Arrays.asList(MTL_LINES));
        Files.copy(Path.of(INPUT_FOLDER, "hierronegro.jpg"), tempDir.resolve(TEXTURE_NAME),
                StandardCopyOption.REPLACE_EXISTING);
        return objFile.toFile();
    }

    /**
     * Configurable listener recording received notifications.
     */
    private static final class ConfigurableListener implements MeshWriterJsonListener {

        /**
         * Folder containing material and texture files, or null if none must be provided.
         */
        private final File folder;

        /**
         * Material file to be returned, overriding the one found in the folder.
         */
        private File materialFile;

        /**
         * Converted file to be returned, overriding the received one.
         */
        private File convertedFile;

        /**
         * Indicates that texture is flagged as valid.
         */
        private boolean textureValid = true;

        /**
         * Indicates that no texture file is provided on texture validation.
         */
        private boolean noTextureFile;

        /**
         * Indicates that no file is provided for received textures.
         */
        private boolean noReceivedFile;

        /**
         * Indicates that no converted file is returned.
         */
        private boolean noConvertedFile;

        /**
         * Indicates that no remote url or id is provided.
         */
        private boolean noRemoteValues;

        private int startCount;
        private int endCount;
        private int chunkCount;
        private int progressCount;
        private int validateTextureCount;
        private int didValidateTextureCount;
        private int textureReceivedCount;
        private int textureDataAvailableCount;
        private int textureDataProcessedCount;

        /**
         * Constructor.
         *
         * @param folder folder containing material and texture files, or null.
         */
        ConfigurableListener(final File folder) {
            this.folder = folder;
        }

        @Override
        public void onWriteStart(final MeshWriter writer) {
            startCount++;
        }

        @Override
        public void onWriteEnd(final MeshWriter writer) {
            endCount++;
        }

        @Override
        public void onWriteProgressChange(final MeshWriter writer, final float progress) {
            progressCount++;
        }

        @Override
        public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
            chunkCount++;
        }

        @Override
        public File onMaterialFileRequested(final MeshWriter writer, final String path) {
            if (materialFile != null) {
                return materialFile;
            }
            return folder != null ? new File(folder, new File(path).getName()) : null;
        }

        @Override
        public File onValidateTexture(final MeshWriter writer, final Texture texture) {
            validateTextureCount++;
            texture.setValid(textureValid);
            return noTextureFile ? null : new File(folder, new File(texture.getFileName()).getName());
        }

        @Override
        public void onDidValidateTexture(final MeshWriter writer, final File f) {
            didValidateTextureCount++;
        }

        @Override
        public File onTextureReceived(final MeshWriter writer, final int textureWidth, final int textureHeight) {
            textureReceivedCount++;
            if (noReceivedFile) {
                return null;
            }
            try {
                return Files.createTempFile(folder.toPath(), "tex", ".jpg").toFile();
            } catch (final IOException e) {
                return null;
            }
        }

        @Override
        public File onTextureDataAvailable(final MeshWriter writer, final File textureFile, final int textureWidth,
                                           final int textureHeight) {
            textureDataAvailableCount++;
            if (noConvertedFile) {
                return null;
            }
            return convertedFile != null ? convertedFile : textureFile;
        }

        @Override
        public void onTextureDataProcessed(final MeshWriter writer, final File textureFile, final int textureWidth,
                                           final int textureHeight) {
            textureDataProcessedCount++;
        }

        @Override
        public String onRemoteTextureUrlRequested(final MeshWriterJson writer, final Texture texture,
                                                  final File textureFile) {
            return noRemoteValues ? null : "https://www.irurueta.com/image?id=" + texture.getId();
        }

        @Override
        public String onRemoteTextureIdRequested(final MeshWriterJson writer, final Texture texture,
                                                 final File textureFile) {
            return noRemoteValues ? null : Integer.toString(texture.getId());
        }
    }
}
