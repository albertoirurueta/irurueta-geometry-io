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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LoaderBinaryTest implements LoaderListenerBinary, LoaderListenerOBJ, MaterialLoaderListener, MeshWriterListener {

    private static final int BUFFER_SIZE = 1024;

    private static final String INPUT_FOLDER = "./src/test/java/com/irurueta/geometry/io/";

    private static final String TMP_FOLDER = "./src/test/java/com/irurueta/geometry/io/tmp/";

    private static final byte SUPPORTED_VERSION = 2;

    private static final int TEXTURE_ID = 5;

    @TempDir
    private Path tempDir;

    private boolean returnNullTextureFile = false;
    private boolean textureDataValid = true;

    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;

    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    private boolean saveChunks = false;
    private final List<DataChunk> chunks = new LinkedList<>();

    // OBJ Material loader
    private final Map<Integer, File> objTextures = new HashMap<>();

    // Binary loader
    private final Map<Integer, File> binTextures = new HashMap<>();

    @BeforeAll
    static void setUpClass() {
        // create folder for generated files
        final var folder = new File(TMP_FOLDER);
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();
    }

    @AfterAll
    static void tearDownClass() {
        //remove any remaining files in thumbnails folder
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
    void testConstructors() throws LockedException, IOException, LoaderException, NotReadyException {

        final var inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final var outputFile = new File(TMP_FOLDER, "randomBig.bin");

        // test empty constructor
        try (final var loader = new LoaderBinary()) {
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_BINARY2, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);

            // convert first randomBig.ply into randomBig.bin
            convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);


            // constructor with file
            assertTrue(outputFile.exists()); //check that file exists
            assertTrue(outputFile.isFile());
        }

        final var badF = new File("./non-existing");
        try (final var loader = new LoaderBinary(outputFile)) {
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_BINARY2, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertNull(loader.getListener());

            // Force IOException
            assertFalse(badF.exists());
            //noinspection resource
            assertThrows(IOException.class, () -> new LoaderBinary(badF));
        }

        // Test constructor with loader listener
        final var listener = new LoaderListener() {
            @Override
            public void onLoadStart(final Loader loader) {
                // no action needed
            }

            @Override
            public void onLoadEnd(final Loader loader) {
                // no action needed
            }

            @Override
            public void onLoadProgressChange(final Loader loader, final float progress) {
                // no action needed
            }
        };

        try (final var loader = new LoaderBinary(listener)) {
            assertFalse(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_BINARY2, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());
            assertThrows(IOException.class, loader::isValidFile);
            assertThrows(NotReadyException.class, loader::load);
        }

        // test constructor with file and listener
        try (final var loader = new LoaderBinary(outputFile, listener)) {
            assertTrue(loader.isReady());
            assertEquals(MeshFormat.MESH_FORMAT_BINARY2, loader.getMeshFormat());
            assertFalse(loader.isLocked());
            assertEquals(listener, loader.getListener());

            // Force IOException
            //noinspection resource
            assertThrows(IOException.class, () -> new LoaderBinary(badF, listener));

            // delete generated outputFile
            //noinspection all
            outputFile.delete();
        }
    }

    @Test
    void testHasSetFileAndIsReady() throws LockedException, IOException, LoaderException, NotReadyException {

        // convert first randomBig.ply into randomBig.bin
        final var inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final var outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());

        try (final var loader = new LoaderBinary()) {
            assertFalse(loader.hasFile());
            assertFalse(loader.isReady());

            // set file
            loader.setFile(outputFile);
            assertTrue(loader.hasFile());
            assertTrue(loader.isReady());

            // delete generated file

            //noinspection all
            outputFile.delete();
        }
    }

    @Test
    void testGetSetListener() throws LockedException, IOException {
        try (final var loader = new LoaderBinary()) {
            assertNull(loader.getListener());

            final var listener = new LoaderListener() {
                @Override
                public void onLoadStart(final Loader loader) {
                    // no action needed
                }

                @Override
                public void onLoadEnd(final Loader loader) {
                    // no action needed
                }

                @Override
                public void onLoadProgressChange(final Loader loader, final float progress) {
                    // no action needed
                }
            };

            loader.setListener(listener);
            assertEquals(listener, loader.getListener());
        }
    }

    @Test
    void testIsValidFile() throws IOException, LockedException, LoaderException, NotReadyException {
        // convert first randomBig.ply into randomBig.bin
        final var inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final var outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        // check that file exists
        assertTrue(outputFile.exists());
        assertTrue(outputFile.isFile());

        final var loader = new LoaderBinary();
        // to free file resources
        loader.close();

        // delete generated file

        //noinspection all
        outputFile.delete();
    }

    @Test
    void testLoad() throws IOException, LockedException, NotReadyException, LoaderException {

        // convert first randomBig.ply into randomBig.bin
        final var inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final var outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        // check that file exists
        assertTrue(outputFile.exists());
        assertTrue(outputFile.isFile());

        final var loader = new LoaderBinary(outputFile);
        loader.setListener(this);

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isStartValid());
        resetListener();

        // to free file resources
        loader.close();

        // delete generated file

        //noinspection all
        outputFile.delete();
    }

    @ParameterizedTest(name = "{index} => inputFile={0}, binFile={1}")
    @CsvSource({"randomBig.ply,randomBig.bin",
            "pilarBigEndian.ply,pilarBigEndian.bin",
            "booksBinary.ply,booksBinary.bin"})
    void testLoadAndIterate(final String inputFile, final String binFile) throws IOException, LockedException,
            NotReadyException, LoaderException, NotAvailableException {

        // convert first randomBig.ply into randomBig.bin
        final var filePly = new File(INPUT_FOLDER, inputFile);
        final var fileBin = new File(TMP_FOLDER, binFile);
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);

        final var loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final var loaderPly = new LoaderPLY(filePly);

        final var binIt = loaderBin.load();
        final var plyIt = loaderPly.load();

        // check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            final var binChunk = binIt.next();
            final var plyChunk = plyIt.next();

            checkChunkEqualness(binChunk, plyChunk);
        }

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        if (binIt.hasNext()) {
            fail("Wrong number of chunks for binary loader");
        }
        if (plyIt.hasNext()) {
            fail("Wrong number of chunks for ply loader");
        }

        loaderBin.close();
        loaderPly.close();

        // delete generated bin file

        //noinspection all
        fileBin.delete();
    }

    @Test
    void testLoadAndIterateRealFileWithNormals2() throws IOException, LockedException, NotReadyException,
            LoaderException, NotAvailableException {

        // convert first pitcher.obj into pitcherObj.bin
        final var fileObj = new File(INPUT_FOLDER, "pitcher.obj");
        final var fileBin = new File(TMP_FOLDER, "pitcherObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);

        final var loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final var loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);

        final var binIt = loaderBin.load();
        final var objIt = loaderObj.load();

        // check correctness of chunks
        while (binIt.hasNext() && objIt.hasNext()) {
            final var binChunk = binIt.next();
            final var objChunk = objIt.next();

            checkChunkEqualness(binChunk, objChunk);
        }

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();

        if (binIt.hasNext()) {
            fail("Wrong number of chunks for binary loader");
        }
        if (objIt.hasNext()) {
            fail("Wrong number of chunks for ply loader");
        }

        loaderBin.close();
        loaderObj.close();

        // delete generated bin file

        //noinspection all
        fileBin.delete();
    }

    @Test
    void testLoadAndIterateRealFileWithTextures() throws IOException, LockedException, NotReadyException,
            LoaderException, NotAvailableException {

        // convert first "potro.obj" into "potroObj.bin"
        saveChunks = true;
        // obj loader might return different results after consecutive executions
        // in this file, mostly because of the triangulation algorithm, for that
        // reason we store the original chunks loaded when doing the conversion
        // to binary
        final var fileObj = new File(INPUT_FOLDER, "potro.obj");
        final var fileBin = new File(TMP_FOLDER, "potroObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);
        saveChunks = false;

        final var loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final var loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);

        final var binIt = loaderBin.load();
        final var objIt = loaderObj.load();

        // check correctness of chunks
        var counter = 0;
        while (binIt.hasNext() && objIt.hasNext()) {
            final var binChunk = binIt.next();
            objIt.next();
            final var objChunk2 = chunks.get(counter);
            counter++;

            checkChunkEqualness(binChunk, objChunk2);
        }

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        chunks.clear();

        if (binIt.hasNext()) {
            fail("Wrong number of chunks for binary loader");
        }
        if (objIt.hasNext()) {
            fail("Wrong number of chunks for obj loader");
        }

        loaderBin.close();
        loaderObj.close();

        final var keys = binTextures.keySet();

        // compare texture files between the original obj file and the converted
        // binary file and then delete generated textures for binary file
        for (final var key : keys) {
            final var objTexFile = objTextures.get(key);
            final var binTexFile = binTextures.get(key);

            assertTrue(areEqual(objTexFile, binTexFile));

            //noinspection all
            binTexFile.delete();
        }

        // delete generated bin file

        //noinspection all
        fileBin.delete();
    }

    private void checkChunkEqualness(DataChunk chunk, DataChunk otherChunk) {
        assertTrue((chunk.getVerticesCoordinatesData() != null
                && otherChunk.getVerticesCoordinatesData() != null) || (chunk.getVerticesCoordinatesData() == null
                && otherChunk.getVerticesCoordinatesData() == null));
        if (chunk.getVerticesCoordinatesData() != null) {
            // vertices coordinates available
            var coords = chunk.getVerticesCoordinatesData();
            var otherCoords = otherChunk.getVerticesCoordinatesData();
            assertEquals(coords.length, otherCoords.length);
            assertArrayEquals(coords, otherCoords, 0.0f);
        }

        assertTrue((chunk.getColorData() != null && otherChunk.getColorData() != null)
                || (chunk.getColorData() == null && otherChunk.getColorData() == null));
        if (chunk.getColorData() != null) {
            // color available
            assertEquals(chunk.getColorData().length, otherChunk.getColorData().length);
            for (var i = 0; i < chunk.getColorData().length; i++) {
                assertEquals(chunk.getColorData()[i], otherChunk.getColorData()[i]);
            }
            assertEquals(chunk.getColorComponents(), otherChunk.getColorComponents());
        }

        assertTrue((chunk.getIndicesData() != null && otherChunk.getIndicesData() != null)
                || (chunk.getIndicesData() == null && otherChunk.getIndicesData() == null));
        if (chunk.getIndicesData() != null) {
            // indices available
            assertEquals(chunk.getIndicesData().length, otherChunk.getIndicesData().length);
            for (var i = 0; i < chunk.getIndicesData().length; i++) {
                assertEquals(chunk.getIndicesData()[i], otherChunk.getIndicesData()[i]);
            }
        }

        assertTrue((chunk.getNormalsData() != null && otherChunk.getNormalsData() != null)
                || (chunk.getNormalsData() == null && otherChunk.getNormalsData() == null));
        if (chunk.getNormalsData() != null) {
            // normals available
            final var normals = chunk.getNormalsData();
            final var otherNormals = chunk.getNormalsData();
            assertEquals(normals.length, otherNormals.length);
            assertArrayEquals(normals, otherNormals, 0.0f);
        }

        assertTrue((chunk.getTextureCoordinatesData() != null
                && otherChunk.getTextureCoordinatesData() != null) || (chunk.getTextureCoordinatesData() == null
                && otherChunk.getTextureCoordinatesData() == null));
        if (chunk.getTextureCoordinatesData() != null) {
            // texture coordinates available
            final var texCoords = chunk.getTextureCoordinatesData();
            final var otherTexCoords = chunk.getTextureCoordinatesData();
            assertEquals(texCoords.length, otherTexCoords.length);
            assertArrayEquals(texCoords, otherTexCoords, 0.0f);
        }

        // check chunk boundaries
        assertEquals(chunk.getMinX(), otherChunk.getMinX(), 0.0);
        assertEquals(chunk.getMinY(), otherChunk.getMinY(), 0.0);
        assertEquals(chunk.getMinZ(), otherChunk.getMinZ(), 0.0);
        assertEquals(chunk.getMaxX(), otherChunk.getMaxX(), 0.0);
        assertEquals(chunk.getMaxY(), otherChunk.getMaxY(), 0.0);
        assertEquals(chunk.getMaxZ(), otherChunk.getMaxZ(), 0.0);
    }

    //OBJ loader
    @Override
    public MaterialLoaderOBJ onMaterialLoaderRequested(final LoaderOBJ loader, final String path) {
        final var materialFile = new File(INPUT_FOLDER, path);
        if (!materialFile.exists() || !materialFile.isFile()) {
            return null;
        }
        try {
            return new MaterialLoaderOBJ(materialFile, this);
        } catch (final IOException ex) {
            // if file does not exist
            return null;
        }
    }

    @Override
    public void onLoadStart(final MaterialLoader loader) {
        // no action needed
    }

    @Override
    public void onLoadEnd(final MaterialLoader loader) {
        // no action needed
    }

    @Override
    public boolean onValidateTexture(final MaterialLoader loader, final Texture texture) {
        // save texture files in a list to check correctness
        final var texFile = new File(INPUT_FOLDER, texture.getFileName());
        objTextures.put(texture.getId(), texFile);
        return true;
    }

    /**
     * Checks that a chunk containing every kind of data is loaded correctly.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkContainsAllData_returnsAllData() throws Exception {
        final var f = writeFile(fullPayload(false));

        try (var loader = new LoaderBinary(f)) {
            loader.setListener(this);
            final var it = loader.load();
            assertTrue(it.hasNext());
            final var chunk = it.next();
            assertFalse(it.hasNext());
            assertThrows(NotAvailableException.class, it::next);

            assertNull(chunk.getMaterial());
            assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, chunk.getVerticesCoordinatesData());
            assertArrayEquals(new short[]{255, 128, 0}, chunk.getColorData());
            assertEquals(3, chunk.getColorComponents());
            assertArrayEquals(new int[]{1, 258}, chunk.getIndicesData());
            assertArrayEquals(new float[]{0.5f, 0.25f}, chunk.getTextureCoordinatesData());
            assertArrayEquals(new float[]{0.0f, 1.0f, 0.0f}, chunk.getNormalsData());
            assertEquals(-1.0f, chunk.getMinX());
            assertEquals(-2.0f, chunk.getMinY());
            assertEquals(-3.0f, chunk.getMinZ());
            assertEquals(4.0f, chunk.getMaxX());
            assertEquals(5.0f, chunk.getMaxY());
            assertEquals(6.0f, chunk.getMaxZ());
            assertTrue(isEndValid());
            assertTrue(isStartValid());
            assertTrue(isProgressValid());
            assertTrue(isLockedValid());
        }
    }

    /**
     * Checks that a chunk containing a material with every property is loaded correctly.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkContainsFullMaterial_returnsMaterial() throws Exception {
        final var f = writeFile(fullPayload(true));

        try (var loader = new LoaderBinary(f)) {
            final var chunk = loader.load().next();
            final var material = chunk.getMaterial();
            assertNotNull(material);
            assertEquals(7, material.getId());
            assertEquals(200, material.getAmbientRedColor());
            assertEquals(100, material.getAmbientGreenColor());
            assertEquals(50, material.getAmbientBlueColor());
            assertEquals(201, material.getDiffuseRedColor());
            assertEquals(101, material.getDiffuseGreenColor());
            assertEquals(51, material.getDiffuseBlueColor());
            assertEquals(202, material.getSpecularRedColor());
            assertEquals(102, material.getSpecularGreenColor());
            assertEquals(52, material.getSpecularBlueColor());
            assertEquals(0.75f, material.getSpecularCoefficient());
            assertEquals(10, material.getAmbientTextureMap().getId());
            assertEquals(11, material.getDiffuseTextureMap().getId());
            assertEquals(12, material.getSpecularTextureMap().getId());
            assertEquals(13, material.getAlphaTextureMap().getId());
            assertEquals(14, material.getBumpTextureMap().getId());
            assertEquals(640, material.getBumpTextureMap().getWidth());
            assertEquals(480, material.getBumpTextureMap().getHeight());
            assertEquals(220, material.getTransparency());
            assertEquals(Illumination.HIGHLIGHT_ON, material.getIllumination());
            assertEquals(4.0f, chunk.getMaxX());
        }
    }

    /**
     * Checks that a material with no optional properties is loaded.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenMaterialHasNoOptionalData_returnsMaterialWithOnlyId() throws Exception {
        final var f = writeFile(bytes(true, 3, false, false, false, false, false, false, false, false, false,
                false, false, 0, 0, 0, 0, 0, bbox()));

        try (var loader = new LoaderBinary(f)) {
            final var chunk = loader.load().next();
            assertEquals(3, chunk.getMaterial().getId());
            assertNull(chunk.getVerticesCoordinatesData());
            assertNull(chunk.getColorData());
            assertNull(chunk.getIndicesData());
            assertNull(chunk.getTextureCoordinatesData());
            assertNull(chunk.getNormalsData());
        }
    }

    /**
     * Checks that several chunks are returned sequentially.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenSeveralChunks_returnsEachChunk() throws Exception {
        final var onlyBox = bytes(false, 0, 0, 0, 0, 0, bbox());
        final var f = writeFile(onlyBox, fullPayload(false), onlyBox);

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            var count = 0;
            while (it.hasNext()) {
                it.next();
                count++;
            }
            assertEquals(3, count);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that a file with an unsupported version is not valid and cannot be loaded.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenVersionIsNotSupported_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("version.bin").toFile();
        Files.write(f.toPath(), bytes((byte) 1, false));

        try (var loader = new LoaderBinary(f)) {
            assertFalse(loader.isValidFile());
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that a file without chunks has nothing to iterate.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void hasNext_whenNoChunks_returnsFalse() throws Exception {
        final var f = tempDir.resolve("nochunks.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertFalse(it.hasNext());
            assertThrows(NotAvailableException.class, it::next);
        }
    }

    /**
     * Checks that textures are skipped when the listener is not a binary listener.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureAndNoListener_skipsTexture() throws Exception {
        final var f = writeFileWithTexture(new byte[]{1, 2, 3, 4, 5}, bytes(false, 0, 0, 0, 0, 0, bbox()));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertTrue(it.hasNext());
            assertEquals(4.0f, it.next().getMaxX());
        }
    }

    /**
     * Checks that textures are stored in the file provided by the listener.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureAndListenerProvidesFile_storesTextureData() throws Exception {
        final var data = new byte[BUFFER_SIZE * 2 + 17];
        for (var i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        final var f = writeFileWithTexture(data, bytes(false, 0, 0, 0, 0, 0, bbox()));

        try (var loader = new LoaderBinary(f)) {
            loader.setListener(this);
            final var it = loader.load();
            it.next();
            final var texFile = binTextures.get(TEXTURE_ID);
            assertNotNull(texFile);
            assertArrayEquals(data, Files.readAllBytes(texFile.toPath()));
        }
    }

    /**
     * Checks that texture data is skipped when the listener does not provide a file.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureAndListenerProvidesNoFile_skipsTexture() throws Exception {
        final var f = writeFileWithTexture(new byte[]{1, 2, 3}, bytes(false, 0, 0, 0, 0, 0, bbox()));
        returnNullTextureFile = true;

        try (var loader = new LoaderBinary(f)) {
            loader.setListener(this);
            final var it = loader.load();
            assertEquals(4.0f, it.next().getMaxX());
            assertTrue(binTextures.isEmpty());
        }
    }

    /**
     * Checks that an invalid texture reported by the listener makes loading fail.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureIsRejectedByListener_throwsLoaderException() throws Exception {
        final var f = writeFileWithTexture(new byte[]{1, 2, 3}, bytes(false, 0, 0, 0, 0, 0, bbox()));
        textureDataValid = false;

        try (var loader = new LoaderBinary(f)) {
            loader.setListener(this);
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a texture longer than the file makes loading fail.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureLengthExceedsFile_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("textrunc.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, true, TEXTURE_ID, 4, 4, 1000L, new byte[]{1, 2}));

        try (var loader = new LoaderBinary(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that a negative chunk size is detected.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkSizeIsNegative_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("negchunk.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false, -5, new byte[8]));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that a chunk size beyond the end of file is detected.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkSizeExceedsFile_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("bigchunk.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false, 1000, new byte[8]));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that corrupted chunk contents are reported as loader exceptions.
     *
     * @param payload corrupted chunk payload.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @MethodSource("corruptedPayloads")
    void next_whenChunkContentIsCorrupted_throwsLoaderException(final byte[] payload) throws Exception {
        final var f = writeFile(payload);

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that texture lengths that are negative or overflow when added to the position are rejected.
     *
     * @param length crafted texture length.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(longs = {Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MIN_VALUE, -1L, Integer.MAX_VALUE})
    void load_whenTextureLengthIsHostile_throwsLoaderException(final long length) throws Exception {
        final var f = tempDir.resolve("hostiletex" + length + ".bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, true, TEXTURE_ID, 4, 4, length, new byte[]{1, 2}));

        try (var loader = new LoaderBinary(f)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that chunk sizes near the integer limit are rejected without allocating memory.
     *
     * @param size crafted chunk size.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 3, Integer.MIN_VALUE})
    void next_whenChunkSizeIsNearIntegerLimit_throwsLoaderException(final int size) throws Exception {
        final var f = tempDir.resolve("hugechunk" + size + ".bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false, size, new byte[8]));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that data sizes near the integer limit declared inside a chunk are rejected without allocating memory.
     *
     * @param payload crafted chunk payload.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @MethodSource("hostileSizePayloads")
    void next_whenDataSizeIsNearIntegerLimit_throwsLoaderException(final byte[] payload) throws Exception {
        final var f = writeFile(payload);

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Gets payloads declaring huge data sizes.
     *
     * @return stream of payloads.
     */
    private static Stream<byte[]> hostileSizePayloads() {
        final var max = Integer.MAX_VALUE;
        final var maxMultipleOf4 = Integer.MAX_VALUE - 3;
        final var maxMultipleOf2 = Integer.MAX_VALUE - 1;
        return Stream.of(
                // coords
                bytes(false, max, new byte[8]),
                bytes(false, maxMultipleOf4, new byte[8]),
                bytes(false, Integer.MIN_VALUE),
                // colors
                bytes(false, 0, max, new byte[8]),
                bytes(false, 0, Integer.MIN_VALUE),
                // indices
                bytes(false, 0, 0, max, new byte[8]),
                bytes(false, 0, 0, maxMultipleOf2, new byte[8]),
                // texture coords
                bytes(false, 0, 0, 0, max, new byte[8]),
                bytes(false, 0, 0, 0, maxMultipleOf4, new byte[8]),
                // normals
                bytes(false, 0, 0, 0, 0, max, new byte[8]),
                bytes(false, 0, 0, 0, 0, maxMultipleOf4, new byte[8]));
    }

    /**
     * Checks that a chunk is rejected when reading it from a file with no body after the size.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkDeclaresDataButFileEnds_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("endsearly.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false, 0));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertThrows(LoaderException.class, it::next);
        }
    }

    /**
     * Checks that files truncated while reading textures are reported as loader exceptions.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileEndsInsideTextureHeader_throwsLoaderException() throws Exception {
        final var f = tempDir.resolve("textheader.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, true, TEXTURE_ID));

        try (var loader = new LoaderBinary(f)) {
            assertThrows(LoaderException.class, loader::load);
        }

        final var f2 = tempDir.resolve("notextflag.bin").toFile();
        Files.write(f2.toPath(), bytes(SUPPORTED_VERSION));

        try (var loader = new LoaderBinary(f2)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Gets corrupted chunk payloads.
     *
     * @return stream of payloads.
     */
    private static Stream<byte[]> corruptedPayloads() {
        return Stream.of(
                // coords: negative, not multiple of float size, exceeding chunk
                bytes(false, -4),
                bytes(false, 3, new byte[3]),
                bytes(false, 400, new byte[8]),
                // colors: negative, exceeding chunk
                bytes(false, 0, -1),
                bytes(false, 0, 100, new byte[8]),
                // indices: negative, odd, exceeding chunk
                bytes(false, 0, 0, -2),
                bytes(false, 0, 0, 3, new byte[3]),
                bytes(false, 0, 0, 100, new byte[8]),
                // texture coords: negative, odd, exceeding chunk
                bytes(false, 0, 0, 0, -4),
                bytes(false, 0, 0, 0, 3, new byte[3]),
                bytes(false, 0, 0, 0, 400, new byte[8]),
                // normals: negative, odd, exceeding chunk
                bytes(false, 0, 0, 0, 0, -4),
                bytes(false, 0, 0, 0, 0, 3, new byte[3]),
                bytes(false, 0, 0, 0, 0, 400, new byte[8]),
                // bounding box does not fit in chunk
                bytes(false, 0, 0, 0, 0, 0, 1.0f, 2.0f));
    }

    /**
     * Builds a payload with every kind of data.
     *
     * @param withMaterial true to include a full material.
     * @return chunk payload.
     */
    private static byte[] fullPayload(final boolean withMaterial) {
        final var material = withMaterial
                ? bytes(true, 7,
                true, (byte) 200, (byte) 100, (byte) 50,
                true, (byte) 201, (byte) 101, (byte) 51,
                true, (byte) 202, (byte) 102, (byte) 52,
                true, 0.75f,
                true, 10, 1, 2,
                true, 11, 3, 4,
                true, 12, 5, 6,
                true, 13, 7, 8,
                true, 14, 640, 480,
                true, (byte) 220,
                true, 2)
                : bytes(false);
        return bytes(material,
                12, 1.0f, 2.0f, 3.0f,
                3, (byte) 255, (byte) 128, (byte) 0, 3,
                4, (short) 1, (short) 258,
                8, 0.5f, 0.25f,
                12, 0.0f, 1.0f, 0.0f,
                bbox());
    }

    /**
     * Gets bounding box bytes.
     *
     * @return bounding box bytes.
     */
    private static byte[] bbox() {
        return bytes(-1.0f, -2.0f, -3.0f, 4.0f, 5.0f, 6.0f);
    }

    /**
     * Writes a file with no textures and the provided chunk payloads.
     *
     * @param payloads chunk payloads.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    private File writeFile(final byte[]... payloads) throws IOException {
        final var f = Files.createTempFile(tempDir, "chunks", ".bin").toFile();
        final var content = new ByteArrayOutputStream();
        content.write(bytes(SUPPORTED_VERSION, false));
        for (final var payload : payloads) {
            content.write(bytes(payload.length, payload));
        }
        Files.write(f.toPath(), content.toByteArray());
        return f;
    }

    /**
     * Writes a file with a single texture followed by the provided chunk payload.
     *
     * @param texture texture data.
     * @param payload chunk payload.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    private File writeFileWithTexture(final byte[] texture, final byte[] payload) throws IOException {
        final var f = Files.createTempFile(tempDir, "texture", ".bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, true, TEXTURE_ID, 4, 4, (long) texture.length, texture,
                false, payload.length, payload));
        return f;
    }

    /**
     * Serializes values in big endian order.
     *
     * @param items values (Boolean, Byte, Short, Integer, Long, Float or byte arrays).
     * @return serialized bytes.
     */
    private static byte[] bytes(final Object... items) {
        try {
            final var out = new ByteArrayOutputStream();
            final var data = new DataOutputStream(out);
            for (final var item : items) {
                switch (item) {
                    case Boolean b -> data.writeBoolean(b);
                    case Byte b -> data.writeByte(b);
                    case Short s -> data.writeShort(s);
                    case Integer i -> data.writeInt(i);
                    case Long l -> data.writeLong(l);
                    case Float fl -> data.writeFloat(fl);
                    case byte[] arr -> data.write(arr);
                    default -> throw new IllegalArgumentException();
                }
            }
            data.flush();
            return out.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public File onTextureReceived(final LoaderBinary loader, final int textureId, final int textureImageWidth,
                                  final int textureImageHeight) {
        if (returnNullTextureFile) {
            return null;
        }
        try {
            final var fileToStoreTexture = File.createTempFile("id" + textureId + "-", ".jpg",
                    new File(TMP_FOLDER));
            binTextures.put(textureId, fileToStoreTexture);
            return fileToStoreTexture;
        } catch (final IOException e) {
            return null;
        }
    }

    @Override
    public boolean onTextureDataAvailable(
            final LoaderBinary loader, final File textureFile, final int textureId, final int textureImageWidth,
            final int textureImageHeight) {
        return textureDataValid;
    }

    // Loader
    @Override
    public void onLoadStart(final Loader loader) {
        if (loader instanceof LoaderBinary) {
            if (startCounter != 0) {
                startValid = false;
            }
            startCounter++;

            testLocked((LoaderBinary) loader);
        }
    }

    @Override
    public void onLoadEnd(final Loader loader) {
        if (loader instanceof LoaderBinary) {
            if (endCounter != 0) {
                endValid = false;
            }
            endCounter++;

            testLocked((LoaderBinary) loader);
        }
    }

    @Override
    public void onLoadProgressChange(final Loader loader, final float progress) {
        if (loader instanceof LoaderBinary) {
            if ((progress < 0.0) || (progress > 1.0)) {
                progressValid = false;
            }
            if (progress < previousProgress) {
                progressValid = false;
            }
            previousProgress = progress;

            testLocked((LoaderBinary) loader);
        }
    }

    // Mesh writer listener
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
    public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
        if (saveChunks) {
            chunks.add(chunk);
        }
    }

    @Override
    public File onMaterialFileRequested(final MeshWriter writer, final String path) {
        final var materialFile = new File(INPUT_FOLDER, path);
        if (materialFile.exists() && materialFile.isFile()) {
            return materialFile;
        } else {
            return null;
        }
    }

    @Override
    public File onValidateTexture(final MeshWriter writer, final Texture texture) {
        final var origF = new File(texture.getFileName());
        final var inputTextureFile = new File(INPUT_FOLDER, origF.getName());
        // image needs to be in jpg format, if it isn't here we have a chance
        // to convert it
        texture.setValid(true);
        if (inputTextureFile.exists() && inputTextureFile.isFile()) {
            return inputTextureFile;
        } else {
            return null;
        }
    }

    @Override
    public void onDidValidateTexture(final MeshWriter writer, final File f) {
        // no action needed
    }

    @Override
    public File onTextureReceived(final MeshWriter writer, final int textureWidth, final int textureHeight) {
        // generate a temporal file in TMP_FOLDER where texture data will be stored
        try {
            // for binary loader within mesh writer
            return File.createTempFile("tex", ".jpg", new File(TMP_FOLDER));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File onTextureDataAvailable(final MeshWriter writer, final File textureFile, final int textureWidth,
                                       final int textureHeight) {
        // texture file needs to be rewritten into JPG format, since the data
        // contained in that file will be used by the MeshWriter.
        // Because textures are already in JPG format in this test, there is no
        // need to make the conversion
        return textureFile;
    }

    @Override
    public void onTextureDataProcessed(final MeshWriter writer, final File textureFile, final int textureWidth,
                                       final int textureHeight) {
        //this method is called to give an opportunity to delete any generated
        //texture files
        //noinspection all
        textureFile.delete();
    }

    private void testLocked(final LoaderBinary loader) {
        if (!loader.isLocked()) {
            lockedValid = false;
        }

        try {
            loader.setListener(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
            // no action needed
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.load();
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

    private boolean areEqual(final File f1, final File f2) throws IOException {
        if (f1.length() != f2.length()) {
            return false;
        }

        final var stream1 = Files.newInputStream(f1.toPath());
        final var stream2 = Files.newInputStream(f2.toPath());

        final var buffer1 = new byte[BUFFER_SIZE];
        final var buffer2 = new byte[BUFFER_SIZE];
        int n1;
        boolean valid = true;
        do {
            n1 = stream1.read(buffer1);
            final var n2 = stream2.read(buffer2);
            // check number of bytes is equal
            if (n1 != n2) {
                valid = false;
            }
            // check buffers are equal
            if (valid) {
                for (var i = 0; i < n1; i++) {
                    if (buffer1[i] != buffer2[i]) {
                        valid = false;
                        break;
                    }
                }
            }

            if (!valid) {
                stream1.close();
                stream2.close();
                return false;
            }
        } while (n1 > 0);
        stream1.close();
        stream2.close();
        return true;
    }

    private void convertToBin(
            final File inputFile, final File outputFile, final MeshFormat inputFormat) throws IOException,
            LockedException, LoaderException, NotReadyException {

        final Loader loader;
        if (inputFormat == MeshFormat.MESH_FORMAT_PLY) {
            loader = new LoaderPLY(inputFile);
        } else if (inputFormat == MeshFormat.MESH_FORMAT_STL) {
            loader = new LoaderSTL(inputFile);
        } else if (inputFormat == MeshFormat.MESH_FORMAT_OBJ) {
            loader = new LoaderOBJ(inputFile);
        } else {
            throw new IOException();
        }

        final var outStream = new FileOutputStream(outputFile);
        final var writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);

        writer.write();
        outStream.close();
    }

    /**
     * Checks that the loader is unlocked when a texture declares more bytes
     * than the file contains, so that it can be reused.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenTextureLengthExceedsFile_throwsLoaderExceptionAndUnlocks() throws Exception {
        final var f = tempDir.resolve("bigtexture.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, true, TEXTURE_ID, 4, 4, 1000L, new byte[4]));

        try (var loader = new LoaderBinary(f)) {
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());

            // loader can be reused after the failure
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());
        }
    }

    /**
     * Checks that the loader is unlocked when a chunk cannot be read while
     * iterating, so that it can be reused.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void next_whenChunkIsCorrupted_throwsLoaderExceptionAndUnlocks() throws Exception {
        final var f = tempDir.resolve("corruptedchunk.bin").toFile();
        Files.write(f.toPath(), bytes(SUPPORTED_VERSION, false, 0));

        try (var loader = new LoaderBinary(f)) {
            final var it = loader.load();
            assertTrue(loader.isLocked());
            assertThrows(LoaderException.class, it::next);
            assertFalse(loader.isLocked());
        }
    }
}
