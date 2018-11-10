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

import org.junit.*;

import java.io.*;
import java.util.*;

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
    private List<DataChunk> chunks = new LinkedList<>();

    //OBJ Material loader
    private Map<Integer, File> objTextures = new HashMap<>();

    //Binary loader
    private Map<Integer, File> binTextures = new HashMap<>();

    public LoaderBinaryTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        //create folder for generated files
        File folder = new File(TMP_FOLDER);
        //noinspection all
        folder.mkdirs();        
    }
    
    @AfterClass
    public static void tearDownClass() {
        //remove any remaining files in thumbnails folder
        File folder = new File(TMP_FOLDER);

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            //noinspection all
            f.delete();
        }

        //delete created folder

        //noinspection all
        folder.delete();                
    }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testConstructors() throws LockedException, IOException, 
            LoaderException, NotReadyException {
        
        //test empty constructor
        LoaderBinary loader = new LoaderBinary();
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //convert first randomBig.ply into randomBig.bin
        File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);
        
        //constructor with file
        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());
        
        loader = new LoaderBinary(outputFile);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        
        //Force IOException
        File badF = new File("./non-existing");
        assertFalse(badF.exists());
        
        loader = null;
        try {
            loader = new LoaderBinary(badF);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //Test constructor with loader listener
        LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(Loader loader) { }

            @Override
            public void onLoadEnd(Loader loader) { }

            @Override
            public void onLoadProgressChange(Loader loader, float progress) { }
        };
        
        loader = new LoaderBinary(listener);
        assertFalse(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        try {
            loader.isValidFile();
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        
        //test constructor with file and listener
        loader = new LoaderBinary(outputFile, listener);
        assertTrue(loader.isReady());
        assertEquals(loader.getMeshFormat(), MeshFormat.MESH_FORMAT_BINARY2);
        assertFalse(loader.isLocked());
        assertEquals(loader.getListener(), listener);
        
        //Force IOException
        loader = null;
        try {
            loader = new LoaderBinary(badF, listener);
            fail("IOException expected but not thrown");
        } catch (IOException ignore) { }
        assertNull(loader);
        
        //delete generated outputFile
        //noinspection all
        outputFile.delete();
    }
    
    @Test
    public void testHasSetFileAndIsReady() throws LockedException, IOException, 
            LoaderException, NotReadyException {
        
        //convert first randomBig.ply into randomBig.bin
        File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);
        
        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());
        
        LoaderBinary loader = new LoaderBinary();
        assertFalse(loader.hasFile());
        assertFalse(loader.isReady());
        
        //set file
        loader.setFile(outputFile);
        assertTrue(loader.hasFile());
        assertTrue(loader.isReady());
        
        //delete generated file

        //noinspection all
        outputFile.delete();
    }
    
    @Test
    public void testGetSetListener() throws LockedException {
        LoaderBinary loader = new LoaderBinary();
        assertNull(loader.getListener());
        
        LoaderListener listener = new LoaderListener() {
            @Override
            public void onLoadStart(Loader loader) { }

            @Override
            public void onLoadEnd(Loader loader) { }

            @Override
            public void onLoadProgressChange(Loader loader, float progress) { }
        };
        
        loader.setListener(listener);
        assertEquals(loader.getListener(), listener);
    }
    
    @Test
    public void testIsValidFile() throws IOException, LockedException, 
            LoaderException, NotReadyException {
        
        //convert first randomBig.ply into randomBig.bin
        File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);

        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());
        
        LoaderBinary loader = new LoaderBinary();
        loader.close(); //to free file resources
        
        //delete generated file

        //noinspection all
        outputFile.delete();
    }
    
    @Test
    public void testLoad() throws IOException, LockedException, 
            NotReadyException, LoaderException {
        
        //convert first randomBig.ply into randomBig.bin
        File inputFile = new File(INPUT_FOLDER, "randomBig.ply");
        File outputFile = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(inputFile, outputFile, MeshFormat.MESH_FORMAT_PLY);
        
        assertTrue(outputFile.exists()); //check that file exists
        assertTrue(outputFile.isFile());
        
        LoaderBinary loader = new LoaderBinary(outputFile);
        loader.setListener(this);

        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isStartValid());
        resetListener();
        
        loader.close(); //to free file resources
        
        //delete generated file

        //noinspection all
        outputFile.delete();
    }
    
    @Test
    public void testLoadAndIterate() throws IOException, LockedException, 
            NotReadyException, LoaderException, NotAvailableException {
        
        //convert first randomBig.ply into randomBig.bin
        File filePly = new File(INPUT_FOLDER, "randomBig.ply");
        File fileBin = new File(TMP_FOLDER, "randomBig.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);        
        
        LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);
        
        LoaderPLY loaderPly = new LoaderPLY(filePly);
        
        LoaderIterator binIt = loaderBin.load();
        LoaderIterator plyIt = loaderPly.load();
        
        //check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            DataChunk binChunk = binIt.next();
            DataChunk plyChunk = plyIt.next();
            
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
        
        //delete generated bin file

        //noinspection all
        fileBin.delete();
    }
    
    @Test
    public void testLoadAndIterateRealFile() throws IOException, LockedException, 
            NotReadyException, LoaderException, NotAvailableException {
        
        //convert first pilarBigEndian.ply into pilarBigEndian.bin
        File filePly = new File(INPUT_FOLDER, "pilarBigEndian.ply");
        File fileBin = new File(TMP_FOLDER, "pilarBigEndian.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);        
                
        LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);
        
        LoaderPLY loaderPly = new LoaderPLY(filePly);
        
        LoaderIterator binIt = loaderBin.load();
        LoaderIterator plyIt = loaderPly.load();
        
        //check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            DataChunk binChunk = binIt.next();
            DataChunk plyChunk = plyIt.next();
            
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
        
        //delete generated bin file

        //noinspection all
        fileBin.delete();
    }    
    
    @Test
    public void testLoadAndIterateRealFileWithNormals() throws IOException, 
            LockedException, NotReadyException, LoaderException, 
            NotAvailableException {
        
        //convert first booksBinary.ply into booksBinary.bin
        File filePly = new File(INPUT_FOLDER, "booksBinary.ply");
        File fileBin = new File(TMP_FOLDER, "booksBinary.bin");
        convertToBin(filePly, fileBin, MeshFormat.MESH_FORMAT_PLY);        
                
        LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);
        
        LoaderPLY loaderPly = new LoaderPLY(filePly);
        
        LoaderIterator binIt = loaderBin.load();
        LoaderIterator plyIt = loaderPly.load();
        
        //check correctness of chunks
        while (binIt.hasNext() && plyIt.hasNext()) {
            DataChunk binChunk = binIt.next();
            DataChunk plyChunk = plyIt.next();
            
            checkChunkEqualness(binChunk, plyChunk);
        }
                
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
        
        if(binIt.hasNext()) fail("Wrong number of chunks for binary loader");
        if(plyIt.hasNext()) fail("Wrong number of chunks for ply loader");
        
        loaderBin.close();
        loaderPly.close();
        
        //delete generated bin file

        //noinspection all
        fileBin.delete();
    }    

    @Test
    public void testLoadAndIterateRealFileWithNormals2() throws IOException, 
            LockedException, NotReadyException, LoaderException, 
            NotAvailableException {
        
        //convert first pitcher.obj into pitcherObj.bin
        File fileObj = new File(INPUT_FOLDER, "pitcher.obj");
        File fileBin = new File(TMP_FOLDER, "pitcherObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);        
                
        LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);
        
        LoaderOBJ loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);
        
        LoaderIterator binIt = loaderBin.load();
        LoaderIterator objIt = loaderObj.load();
        
        //check correctness of chunks
        while (binIt.hasNext() && objIt.hasNext()) {
            DataChunk binChunk = binIt.next();
            DataChunk objChunk = objIt.next();
            
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
        
        //delete generated bin file

        //noinspection all
        fileBin.delete();        
    }        

    @Test
    public void testLoadAndIterateRealFileWithTextures() throws IOException, 
            LockedException, NotReadyException, LoaderException, 
            NotAvailableException {
        
        //convert first potro.obj into potroObj.bin
        saveChunks = true;
        //obj loader might return different results after consecutive executions
        //in this file, mostly because of the triangulation algorithm, for that
        //reason we store the original chunks loaded when doing the conversion
        //to binary
        File fileObj = new File(INPUT_FOLDER, "potro.obj");
        File fileBin = new File(TMP_FOLDER, "potroObj.bin");
        convertToBin(fileObj, fileBin, MeshFormat.MESH_FORMAT_OBJ);     
        saveChunks = false;
        
        LoaderBinary loaderBin = new LoaderBinary(fileBin);
        loaderBin.setListener(this);
        
        LoaderOBJ loaderObj = new LoaderOBJ(fileObj);
        loaderObj.setListener(this);
                
        LoaderIterator binIt = loaderBin.load();
        LoaderIterator objIt = loaderObj.load();
        
        
        //check correctness of chunks
        int counter = 0;
        while (binIt.hasNext() && objIt.hasNext()) {
            DataChunk binChunk = binIt.next();
            objIt.next();
            DataChunk objChunk2 = chunks.get(counter);
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
        
        Set<Integer> keys = binTextures.keySet();
        
        //compare texture files between the original obj file and the converted
        //binary file and then delete generated textures for binary file
        for (Integer key : keys) {
            File objTexFile = objTextures.get(key);
            File binTexFile = binTextures.get(key);
            
            assertTrue(areEqual(objTexFile, binTexFile));

            //noinspection all
            binTexFile.delete();
        }
        
        //delete generated bin file

        //noinspection all
        fileBin.delete();        
    }        
    
    private void checkChunkEqualness(DataChunk chunk, DataChunk otherChunk) {
        //checkMaterialEqualness(chunk.getMaterial(), otherChunk.getMaterial());
        
        assertTrue((chunk.getVerticesCoordinatesData() != null &&
                otherChunk.getVerticesCoordinatesData() != null) ||
                (chunk.getVerticesCoordinatesData() == null &&
                otherChunk.getVerticesCoordinatesData() == null));
        if (chunk.getVerticesCoordinatesData() != null) {
            //vertices coordinates available
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
            //color available
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
            //indices available
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
            //normals available
            float[] normals = chunk.getNormalsData();
            float[] otherNormals = chunk.getNormalsData();
            assertEquals(normals.length, otherNormals.length);
            assertArrayEquals(normals, otherNormals, 0.0f);
        }
        
        assertTrue((chunk.getTextureCoordiantesData() != null &&
                otherChunk.getTextureCoordiantesData() != null) ||
                (chunk.getTextureCoordiantesData() == null &&
                otherChunk.getTextureCoordiantesData() == null));
        if (chunk.getTextureCoordiantesData() != null) {
            //texture coordinates available
            float[] texCoords = chunk.getTextureCoordiantesData();
            float[] otherTexCoords = chunk.getTextureCoordiantesData();
            assertEquals(texCoords.length, otherTexCoords.length);
            assertArrayEquals(texCoords, otherTexCoords, 0.0f);
        }
        
        //check chunk boundaries
        assertEquals(chunk.getMinX(), otherChunk.getMinX(), 0.0);
        assertEquals(chunk.getMinY(), otherChunk.getMinY(), 0.0);
        assertEquals(chunk.getMinZ(), otherChunk.getMinZ(), 0.0);
        assertEquals(chunk.getMaxX(), otherChunk.getMaxX(), 0.0);
        assertEquals(chunk.getMaxY(), otherChunk.getMaxY(), 0.0);
        assertEquals(chunk.getMaxZ(), otherChunk.getMaxZ(), 0.0);             
    }    

    //OBJ loader
    @Override
    public MaterialLoaderOBJ onMaterialLoaderRequested(LoaderOBJ loader, String path) {
        File materialFile = new File(INPUT_FOLDER, path);
        if (!materialFile.exists() || !materialFile.isFile()) {
            return null;
        }
        try {
            return new MaterialLoaderOBJ(materialFile, this);
        } catch (IOException ex) {
            return null; //if file does not exist
        }
    }

    @Override
    public void onLoadStart(MaterialLoader loader) { }

    @Override
    public void onLoadEnd(MaterialLoader loader) { }

    @Override
    public boolean onValidateTexture(MaterialLoader loader, Texture texture) {
        //save texture files in a list to check correctness
        File texFile = new File(INPUT_FOLDER, texture.getFileName());
        objTextures.put(texture.getId(), texFile);
        return true;
    }

    @Override
    public File onTextureReceived(LoaderBinary loader, int textureId, 
            int textureImageWidth, int textureImageHeight) {
        try {
            File fileToStoreTexture = File.createTempFile(
                    "id" + textureId + "-", ".jpg", new File(TMP_FOLDER));
            binTextures.put(textureId, fileToStoreTexture);
            return fileToStoreTexture;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean onTextureDataAvailable(LoaderBinary loader, 
            File textureFile, int textureId, int textureImageWidth, 
            int textureImageHeight) {
        return true;
    }

    //Loader
    @Override
    @SuppressWarnings("Duplicates")
    public void onLoadStart(Loader loader) {        
        if (loader instanceof LoaderBinary) {
            if (startCounter != 0) {
                startValid = false;
            }
            startCounter++;

            testLocked((LoaderBinary)loader);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void onLoadEnd(Loader loader) {        
        if (loader instanceof LoaderBinary) {
            if (endCounter != 0) {
                endValid = false;
            }
            endCounter++;

            testLocked((LoaderBinary)loader);
        }
    }

    @Override
    public void onLoadProgressChange(Loader loader, float progress) {        
        if (loader instanceof LoaderBinary) {
            if ((progress < 0.0) || (progress > 1.0)) {
                progressValid = false;
            }
            if (progress < previousProgress) {
                progressValid = false;
            }
            previousProgress = progress;
            
            testLocked((LoaderBinary)loader);
        }
    }
    
    //Mesh writer listener
    @Override
    public void onWriteStart(MeshWriter writer) { }

    @Override
    public void onWriteEnd(MeshWriter writer) { }

    @Override
    public void onWriteProgressChange(MeshWriter writer, float progress) { }
    
    @Override
    public void onChunkAvailable(MeshWriter writer, DataChunk chunk) {        
        if (saveChunks) {
            chunks.add(chunk);
        }
    }    

    @Override
    public File onMaterialFileRequested(MeshWriter writer, String path) {
        File materialFile = new File(INPUT_FOLDER, path);
        if (materialFile.exists() && materialFile.isFile()) {
            return materialFile;
        } else {
            return null;
        }
    }

    @Override
    public File onValidateTexture(MeshWriter writer, Texture texture) {
        File origF = new File(texture.getFileName());
        File inputTextureFile = new File(INPUT_FOLDER, origF.getName());
        //image needs to be in jpg format, if it isn't here we have a chance
        //to convert it
        texture.setValid(true);
        if (inputTextureFile.exists() && inputTextureFile.isFile()) {
            return inputTextureFile;
        } else {
            return null;
        }
    }

    @Override
    public void onDidValidateTexture(MeshWriter writer, File f) { }

    @Override
    public File onTextureReceived(MeshWriter writer, int textureWidth, 
            int textureHeight) {
        //generate a temporal file in TMP_FOLDER where texture data will be 
        //stored
        try {
            //for binary loader within mesh writer
            return File.createTempFile("tex", ".jpg", new File(
                    TMP_FOLDER));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File onTextureDataAvailable(MeshWriter writer, File textureFile, 
            int textureWidth, int textureHeight) {
        //texture file needs to be rewritten into JPG format, since the data
        //contained in that file will be used by the MeshWriter.
        //Because textures are already in JPG format in this test, there is no
        //need to make the conversion        
        return textureFile;
    }

    @Override
    public void onTextureDataProcessed(MeshWriter writer, File textureFile, 
            int textureWidth, int textureHeight) {
        //this method is called to give an opportunity to delete any generated
        //texture files
        //noinspection all
        textureFile.delete();
    }    
    
    private void testLocked(LoaderBinary loader) {
        if (!loader.isLocked()) {
            lockedValid = false;
        }
        
        try {
            loader.setListener(null);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            loader.load();
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
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
    
    private boolean areEqual(File f1, File f2) throws IOException {
        if (f1.length() != f2.length()) {
            return false;
        }
        
        InputStream stream1 = new FileInputStream(f1);
        InputStream stream2 = new FileInputStream(f2);
        
        byte[] buffer1 = new byte[BUFFER_SIZE];
        byte[] buffer2 = new byte[BUFFER_SIZE];
        int n1, n2;
        boolean valid = true;
        do {
            n1 = stream1.read(buffer1);
            n2 = stream2.read(buffer2);
            //check number of bytes is equal
            if (n1 != n2) {
                valid = false;
            }
            //check buffers are equal
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
    
    private void convertToBin(File inputFile, File outputFile, 
            MeshFormat inputFormat) throws IOException, LockedException, 
            LoaderException, NotReadyException {
        
        Loader loader;
        if (inputFormat == MeshFormat.MESH_FORMAT_PLY) {
            loader = new LoaderPLY(inputFile);
        } else if (inputFormat == MeshFormat.MESH_FORMAT_STL) {
            loader = new LoaderSTL(inputFile);
        } else if (inputFormat == MeshFormat.MESH_FORMAT_OBJ) {
            loader = new LoaderOBJ(inputFile);
        } else {
            throw new IOException();
        }
        
        FileOutputStream outStream = new FileOutputStream(outputFile);
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
        
        writer.write();
        outStream.close();
    }
}
