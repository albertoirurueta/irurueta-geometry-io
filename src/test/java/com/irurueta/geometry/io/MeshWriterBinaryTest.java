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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class MeshWriterBinaryTest implements MeshWriterListener {
    
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

    public MeshWriterBinaryTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        //create folder for generated files
        File folder = new File(TMP_FOLDER);
        //noinspection all
        folder.mkdirs();        
    }
    
    @AfterClass
    @SuppressWarnings("Duplicates")
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
    @SuppressWarnings("Duplicates")
    public void testConstructors() throws IOException {
        File outF = new File(TMP_FOLDER, "booksBinary.bin");
        File inF = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        //test constructor with output stream and loader        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(writer.getStream(), outStream);
        assertNull(writer.getListener());
        
        MeshWriterListener listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(MeshWriter writer) { }

            @Override
            public void onWriteEnd(MeshWriter writer) { }

            @Override
            public void onWriteProgressChange(MeshWriter writer, 
                float progress) { }

            @Override
            public File onMaterialFileRequested(MeshWriter writer, 
                String path) {
                return null;
            }

            @Override
            public File onValidateTexture(MeshWriter writer, Texture texture) {
                return null;
            }

            @Override
            public void onDidValidateTexture(MeshWriter writer, File f) { }

            @Override
            public File onTextureReceived(MeshWriter writer, int textureWidth, 
                    int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight) {
                return null;
            }        
        
            @Override
            public void onTextureDataProcessed(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight) { }

            @Override
            public void onChunkAvailable(MeshWriter writer, DataChunk chunk) { }
        };
        
        writer = new MeshWriterBinary(loader, outStream, listener);
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(writer.getStream(), outStream);
        assertEquals(writer.getListener(), listener);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    @SuppressWarnings("Duplicates")
    public void testGetSetListener() throws IOException, LockedException {
        File outF = new File(TMP_FOLDER, "booksBinary.bin");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        assertNull(writer.getListener());
        
        //set new listener
        MeshWriterListener listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(MeshWriter writer) { }

            @Override
            public void onWriteEnd(MeshWriter writer) { }

            @Override
            public void onWriteProgressChange(MeshWriter writer, 
                float progress) { }

            @Override
            public File onMaterialFileRequested(MeshWriter writer, String path) {
                return null;
            }

            @Override
            public File onValidateTexture(MeshWriter writer, Texture texture) {
                return null;
            }

            @Override
            public void onDidValidateTexture(MeshWriter writer, File f) { }

            @Override
            public File onTextureReceived(MeshWriter writer, int textureWidth, 
                int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight) {
                return null;
            }
            
            @Override
            public void onTextureDataProcessed(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight) { }

            @Override
            public void onChunkAvailable(MeshWriter writer, DataChunk chunk) { }
        };
        
        writer.setListener(listener);
        assertEquals(writer.getListener(), listener);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    public void testWriteAndIsReadyRandomAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "randomAscii.bin");
        File inF = new File(INPUT_FOLDER, "randomAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();        
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }
    
    @Test
    public void testWriteAndIsReadyRandomLittleFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "randomLittle.bin");
        File inF = new File(INPUT_FOLDER, "randomLittle.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }    
    
    @Test
    public void testWriteAndIsReadyRandomBigFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "randomBig.bin");
        File inF = new File(INPUT_FOLDER, "randomBig.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }
    
    @Test
    public void testWriteAndIsReadyBooksBinaryFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "booksBinary.bin");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }    
    
    @Test
    public void testWriteAndIsReadyBooksAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "booksAscii.bin");
        File inF = new File(INPUT_FOLDER, "booksAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }    
        
    @Test
    public void testWriteAndIsReadyBooksObjFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "booksObj.bin");
        File inF = new File(INPUT_FOLDER, "books.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }    
   
    @Test
    public void testWriteAndIsReadyBooksBinaryStlFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "booksBinaryStl.bin");
        File inF = new File(INPUT_FOLDER, "booksBinary.stl");
        
        Loader loader = new LoaderSTL(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }
   
    @Test
    public void testWriteAndIsReadyBooksAsciiStlFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "booksAsciiStl.bin");
        File inF = new File(INPUT_FOLDER, "booksAscii.stl");
        
        Loader loader = new LoaderSTL(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }    
    
   @Test
    public void testWriteAndIsReadyPitcherObjFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "pitcherObj.bin");
        File inF = new File(INPUT_FOLDER, "pitcher.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }        
    
    @Test
    public void testWriteAndIsReadyPotroFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "potroObj.bin");
        //This file references textures which are already in jpeg format, those
        //textures will be embedded in resulting json file
        File inF = new File(INPUT_FOLDER, "potro.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();        
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }     
    
    @Test
    public void testWriteAndIsReadyPilarAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "pilarAscii.bin");
        File inF = new File(INPUT_FOLDER, "pilarAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }
    
    @Test
    public void testWriteAndIsReadyPilarLittleEndianFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "pilarLittleEndian.bin");
        File inF = new File(INPUT_FOLDER, "pilarLittleEndian.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }
    
    @Test
    public void testWriteAndIsReadyPilarBigEndianFile() throws IOException, 
            LockedException, LoaderException, NotReadyException {
        File outF = new File(TMP_FOLDER, "pilarBigEndian.bin");
        File inF = new File(INPUT_FOLDER, "pilarBigEndian.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);        
        writer.setListener(this);
        assertTrue(writer.isReady());
        
        //write into json
        writer.write();
        outStream.close();
        
        assertTrue(isEndValid());
        assertTrue(isLockedValid());
        assertTrue(isProgressValid());
        assertTrue(isStartValid());
        resetListener();
                
        assertTrue(outF.exists());
        assertTrue(outF.delete());
        
        //Force NotReadyException
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (NotReadyException ignore) { }
    }

    @Override
    public void onWriteStart(MeshWriter writer) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;
        
        checkLocked((MeshWriterBinary)writer);
    }

    @Override
    public void onWriteEnd(MeshWriter writer) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;
        
        checkLocked((MeshWriterBinary)writer);
    }

    @Override
    public void onWriteProgressChange(MeshWriter writer, float progress) {
        if ((progress < 0.0) || (progress > 1.0)) {
            progressValid = false;
        }
        if (progress < previousProgress) {
            progressValid = false;
        }
        previousProgress = progress;
        
        checkLocked((MeshWriterBinary)writer);
    }
    
    @Override
    public void onChunkAvailable(MeshWriter writer, DataChunk chunk) {
        checkLocked((MeshWriterBinary)writer);
    }    

    //FOR OBJ LOADER
    @Override
    public File onMaterialFileRequested(MeshWriter writer, String path) {
        File origF = new File(path);
        return new File(INPUT_FOLDER, origF.getName());
    }

    @Override
    public File onValidateTexture(MeshWriter writer, Texture texture) {
        File origF = new File(texture.getFileName());
        File inputTextureFile = new File(INPUT_FOLDER, origF.getName());
        //image needs to be in jpg format, if it isn't here we have a chance
        //to convert it
        texture.setValid(true);
        return inputTextureFile;
    }

    @Override
    public void onDidValidateTexture(MeshWriter writer, File f) {
        //normally converted textures files need to be deleted, however because
        //we are using an input file as is (because it is already in jpg format)
        //we will not delete it
    }

    @Override
    public File onTextureReceived(MeshWriter writer, int textureWidth, 
            int textureHeight) {
        //generate a temporal file in TMP_FOLDER where texture data will be 
        //stored
        try{
            //FOR BINARY LOADER
            return File.createTempFile("tex", ".jpg", new File(
                    TMP_FOLDER));
        }catch(IOException e){
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
    
    
    private void checkLocked(MeshWriterBinary writer) {
        if (!writer.isLocked()) {
            lockedValid = false;
        }
        
        try {
            writer.setListener(null);
            lockedValid = false;
        } catch (LockedException ignore) {
        } catch (Throwable e) {
            lockedValid = false;
        }
        
        try {
            writer.write();
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
}
