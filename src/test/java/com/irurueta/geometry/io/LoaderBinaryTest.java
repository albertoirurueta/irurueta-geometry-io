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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class LoaderBinaryTest implements LoaderListenerBinary,
        LoaderListenerOBJ, MaterialLoaderListener, MeshWriterListener {

    private static final int BUFFER_SIZE = 1024;

    private static final String INPUT_FOLDER =
            "./src/test/java/com/irurueta/geometry/io/";

    private static final String TMP_FOLDER =
            "./src/test/java/com/irurueta/geometry/io/tmp/";

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

    @BeforeClass
    public static void setUpClass() {
        // create folder for generated files
        final File folder = new File(TMP_FOLDER);
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();
    }

    @AfterClass
    public static void tearDownClass() {
        //remove any remaining files in thumbnails folder
        final File folder = new File(TMP_FOLDER);

        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (final File f : files) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }

        // delete created folder
        //noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    @Test
    public void testConstructors() throws LockedException, IOException,
            LoaderException, NotReadyException {

        // test empty constructor
        LoaderBinary loader = new LoaderBinary();
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // convert first randomBig.ply into randomBig.bin
        final File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        // constructor with file
        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());

        loader = new LoaderBinary(outputFile);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Force IOException
        final File badF = new File("./non-existing");
        assertFalse(badF.exists());

        loader = null;
        try {
            loader = new LoaderBinary(badF);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // Test constructor with loader listener
        final LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(final Loader loader) {
            }

            @Override
            public void onLoadEnd(final Loader loader) {
            }

            @Override
            public void onLoadProgressChange(final Loader loader, final float progress) {
            }
        };

        loader = new LoaderBinary(listener);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }

        // test constructor with file and listener
        loader = new LoaderBinary(outputFile, listener);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);

        // Force IOException
        loader = null;
        try {
            loader = new LoaderBinary(badF, listener);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

        // delete generated outputFile
        //noinspection all
        outputFile.delete();
    }

    @Test
    public void testHasSetFileAndIsReady() throws LockedException, IOException,
            LoaderException, NotReadyException {

        // convert first randomBig.ply into randomBig.bin
        final File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());

        final LoaderBinary loader = new LoaderBinary();
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

    @Test
    public void testGetSetListener() throws LockedException {
        final LoaderBinary loader = new LoaderBinary();
        assertNull(loader.getListener());

        final LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(final Loader loader) {
            }

            @Override
            public void onLoadEnd(final Loader loader) {
            }

            @Override
            public void onLoadProgressChange(final Loader loader, final float progress) {
            }
        };

        loader.setListener(listener);
        assertEquals(loader.getListener(), listener);
    }

    @Test
    public void testIsValidFile() throws IOException, LockedException,
            LoaderException, NotReadyException {

        // convert first randomBig.ply into randomBig.bin
        final File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        // check that file exists
        assertTrue(outputFile.exists());
        assertTrue(outputFile.isFile());

        final LoaderBinary loader = new LoaderBinary();
        // to free file resources
        loader.close();

        // delete generated file

        //noinspection all
        outputFile.delete();
    }

    @Test
    public void testLoad() throws IOException, LockedException,
            NotReadyException, LoaderException {

        // convert first randomBig.ply into randomBig.bin
        final File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        final File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        // check that file exists
        assertTrue(outputFile.exists());
        assertTrue(outputFile.isFile());

        final LoaderBinary loader = new LoaderBinary(outputFile);
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

    @Test
    public void testLoadAndIterate() throws IOException, LockedException,
            NotReadyException, LoaderException, NotAvailableException {

        // convert first randomBig.ply into randomBig.bin
        final File filePly = new File(INPUT_FOLDER, "randomBig.ply");
        final File fileBin = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);

        final LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final LoaderPLY loaderPly = new LoaderPLY(filePly);

        final LoaderIterator binIt = loaderBin.load();
        final LoaderIterator plyIt = loaderPly.load();

        // check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            final DataChunk binChunk = binIt.next();
            final DataChunk plyChunk = plyIt.next();

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
    public void testLoadAndIterateRealFile() throws IOException, LockedException,
            NotReadyException, LoaderException, NotAvailableException {

        // convert first pilarBigEndian.ply into pilarBigEndian.bin
        final File filePly = new File(INPUT_FOLDER, "pilarBigEndian.ply");
        final File fileBin = new File(TMP_FOLDER, "pilarBigEndian.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);

        final LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final LoaderPLY loaderPly = new LoaderPLY(filePly);

        final LoaderIterator binIt = loaderBin.load();
        final LoaderIterator plyIt = loaderPly.load();

        // check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            final DataChunk binChunk = binIt.next();
            final DataChunk plyChunk = plyIt.next();

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
    public void testLoadAndIterateRealFileWithNormals() throws IOException,
            LockedException, NotReadyException, LoaderException,
            NotAvailableException {

        // convert first booksBinary.ply into booksBinary.bin
        final File filePly = new File(INPUT_FOLDER, "booksBinary.ply");
        final File fileBin = new File(TMP_FOLDER, "booksBinary.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);

        final LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final LoaderPLY loaderPly = new LoaderPLY(filePly);

        final LoaderIterator binIt = loaderBin.load();
        final LoaderIterator plyIt = loaderPly.load();

        // check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            final DataChunk binChunk = binIt.next();
            final DataChunk plyChunk = plyIt.next();

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
    public void testLoadAndIterateRealFileWithNormals2() throws IOException,
            LockedException, NotReadyException, LoaderException,
            NotAvailableException {

        // convert first pitcher.obj into pitcherObj.bin
        final File fileObj = new File(INPUT_FOLDER, "pitcher.obj");
        final File fileBin = new File(TMP_FOLDER, "pitcherObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);

        final LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final LoaderOBJ loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);

        final LoaderIterator binIt = loaderBin.load();
        final LoaderIterator objIt = loaderObj.load();

        // check correctness of chunks
        while (binIt.hasNext() && objIt.hasNext()) {
            final DataChunk binChunk = binIt.next();
            final DataChunk objChunk = objIt.next();

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
    public void testLoadAndIterateRealFileWithTextures() throws IOException,
            LockedException, NotReadyException, LoaderException,
            NotAvailableException {

        // convert first potro.obj into potroObj.bin
        saveChunks = true;
        // obj loader might return different results after consecutive executions
        // in this file, mostly because of the triangulation algorithm, for that
        // reason we store the original chunks loaded when doing the conversion
        // to binary
        final File fileObj = new File(INPUT_FOLDER, "potro.obj");
        final File fileBin = new File(TMP_FOLDER, "potroObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);
        saveChunks = false;

        final LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);

        final LoaderOBJ loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);

        final LoaderIterator binIt = loaderBin.load();
        final LoaderIterator objIt = loaderObj.load();

        // check correctness of chunks
        int counter = 0;
        while (binIt.hasNext() && objIt.hasNext()) {
            final DataChunk binChunk = binIt.next();
            objIt.next();
            final DataChunk objChunk2 = chunks.get(counter);
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

        final Set<Integer> keys = binTextures.keySet();

        // compare texture files between the original obj file and the converted
        // binary file and then delete generated textures for binary file
        for (final Integer key : keys) {
            final File objTexFile = objTextures.get(key);
            final File binTexFile = binTextures.get(key);

            assertTrue(areEqual(objTexFile, binTexFile));

            //noinspection all
            binTexFile.delete();
        }

        // delete generated bin file

        //noinspection all
        fileBin.delete();
    }

    private void checkChunkEqualness(DataChunk chunk, DataChunk otherChunk) {
        assertTrue((chunk.getVerticesCoordinatesData() != null &&
                otherChunk.getVerticesCoordinatesData() != null) ||
                (chunk.getVerticesCoordinatesData() == null &&
                        otherChunk.getVerticesCoordinatesData() == null));
        if (chunk.getVerticesCoordinatesData() != null) {
            // vertices coordinates available
            float[] coords = chunk.getVerticesCoordinatesData();
            float[] otherCoords = otherChunk.getVerticesCoordinatesData();
            assertEquals(coords.length, otherCoords.length);
            assertArrayEquals(coords, otherCoords, 0.0f);
        }

        assertTrue((chunk.getColorData() != null &&
                otherChunk.getColorData() != null) ||
                (chunk.getColorData() == null &&
                        otherChunk.getColorData() == null));
        if (chunk.getColorData() != null) {
            // color available
            assertEquals(chunk.getColorData().length,
                    otherChunk.getColorData().length);
            for (int i = 0; i < chunk.getColorData().length; i++) {
                assertEquals(chunk.getColorData()[i],
                        otherChunk.getColorData()[i]);
            }
            assertEquals(chunk.getColorComponents(),
                    otherChunk.getColorComponents());
        }

        assertTrue((chunk.getIndicesData() != null &&
                otherChunk.getIndicesData() != null) ||
                (chunk.getIndicesData() == null &&
                        otherChunk.getIndicesData() == null));
        if (chunk.getIndicesData() != null) {
            // indices available
            assertEquals(chunk.getIndicesData().length,
                    otherChunk.getIndicesData().length);
            for (int i = 0; i < chunk.getIndicesData().length; i++) {
                assertEquals(chunk.getIndicesData()[i],
                        otherChunk.getIndicesData()[i]);
            }
        }

        assertTrue((chunk.getNormalsData() != null &&
                otherChunk.getNormalsData() != null) ||
                (chunk.getNormalsData() == null &&
                        otherChunk.getNormalsData() == null));
        if (chunk.getNormalsData() != null) {
            // normals available
            final float[] normals = chunk.getNormalsData();
            final float[] otherNormals = chunk.getNormalsData();
            assertEquals(normals.length, otherNormals.length);
            assertArrayEquals(normals, otherNormals, 0.0f);
        }

        assertTrue((chunk.getTextureCoordinatesData() != null &&
                otherChunk.getTextureCoordinatesData() != null) ||
                (chunk.getTextureCoordinatesData() == null &&
                        otherChunk.getTextureCoordinatesData() == null));
        if (chunk.getTextureCoordinatesData() != null) {
            // texture coordinates available
            final float[] texCoords = chunk.getTextureCoordinatesData();
            final float[] otherTexCoords = chunk.getTextureCoordinatesData();
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
        final File materialFile = new File(INPUT_FOLDER, path);
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
    }

    @Override
    public void onLoadEnd(final MaterialLoader loader) {
    }

    @Override
    public boolean onValidateTexture(final MaterialLoader loader, final Texture texture) {
        // save texture files in a list to check correctness
        final File texFile = new File(INPUT_FOLDER, texture.getFileName());
        objTextures.put(texture.getId(), texFile);
        return true;
    }

    @Override
    public File onTextureReceived(final LoaderBinary loader, final int textureId,
                                  final int textureImageWidth, final int textureImageHeight) {
        try {
            final File fileToStoreTexture = File.createTempFile(
                    "id" + textureId + "-", ".jpg", new File(TMP_FOLDER));
            binTextures.put(textureId, fileToStoreTexture);
            return fileToStoreTexture;
        } catch (final IOException e) {
            return null;
        }
    }

    @Override
    public boolean onTextureDataAvailable(
            final LoaderBinary loader,
            final File textureFile, final int textureId, final int textureImageWidth,
            final int textureImageHeight) {
        return true;
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
    }

    @Override
    public void onWriteEnd(final MeshWriter writer) {
    }

    @Override
    public void onWriteProgressChange(final MeshWriter writer, final float progress) {
    }

    @Override
    public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
        if (saveChunks) {
            chunks.add(chunk);
        }
    }

    @Override
    public File onMaterialFileRequested(final MeshWriter writer, final String path) {
        final File materialFile = new File(INPUT_FOLDER, path);
        if (materialFile.exists() && materialFile.isFile()) {
            return materialFile;
        } else {
            return null;
        }
    }

    @Override
    public File onValidateTexture(final MeshWriter writer, final Texture texture) {
        final File origF = new File(texture.getFileName());
        final File inputTextureFile = new File(INPUT_FOLDER, origF.getName());
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
    }

    @Override
    public File onTextureReceived(final MeshWriter writer, final int textureWidth,
                                  final int textureHeight) {
        // generate a temporal file in TMP_FOLDER where texture data will be
        // stored
        try {
            // for binary loader within mesh writer
            return File.createTempFile("tex", ".jpg", new File(
                    TMP_FOLDER));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File onTextureDataAvailable(final MeshWriter writer, final File textureFile,
                                       final int textureWidth, final int textureHeight) {
        // texture file needs to be rewritten into JPG format, since the data
        // contained in that file will be used by the MeshWriter.
        // Because textures are already in JPG format in this test, there is no
        // need to make the conversion
        return textureFile;
    }

    @Override
    public void onTextureDataProcessed(final MeshWriter writer, final File textureFile,
                                       final int textureWidth, final int textureHeight) {
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
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            loader.load();
            lockedValid = false;
        } catch (final LockedException ignore) {
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

        final InputStream stream1 = new FileInputStream(f1);
        final InputStream stream2 = new FileInputStream(f2);

        final byte[] buffer1 = new byte[BUFFER_SIZE];
        final byte[] buffer2 = new byte[BUFFER_SIZE];
        int n1, n2;
        boolean valid = true;
        do {
            n1 = stream1.read(buffer1);
            n2 = stream2.read(buffer2);
            // check number of bytes is equal
            if (n1 != n2) {
                valid = false;
            }
            // check buffers are equal
            if (valid) {
                for (int i = 0; i < n1; i++) {
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
            final File inputFile, final File outputFile, final MeshFormat inputFormat)
            throws IOException, LockedException, LoaderException, NotReadyException {

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

        final FileOutputStream outStream = new FileOutputStream(outputFile);
        final MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);

        writer.write();
        outStream.close();
    }
}
