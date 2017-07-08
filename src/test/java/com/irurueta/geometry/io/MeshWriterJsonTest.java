/**
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.MeshWriterJson
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 20, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeshWriterJsonTest implements MeshWriterJsonListener{

    public static final String INPUT_FOLDER =
            "./src/test/java/com/irurueta/geometry/io/";

    public static final String TMP_FOLDER =
            "./src/test/java/com/irurueta/geometry/io/tmp/";
    
    public MeshWriterJsonTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        //create folder for generated files
        File folder = new File(TMP_FOLDER);
        folder.mkdirs();
    }
    
    @AfterClass
    public static void tearDownClass() {
        //remove any remaining files in thumbnails folder
        File folder = new File(TMP_FOLDER);
        for(File f : folder.listFiles()){
            f.delete();
        }
        //delete created folder
        folder.delete();
    }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testConstructors() throws IOException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        //test constructor with output stream and loader        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());
        assertEquals(writer.isEmbedTexturesEnabled(), 
                MeshWriterJson.DEFAULT_EMBED_TEXTURES);
        assertEquals(writer.isRemoteTextureUrlEnabled(),
                MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL);
        assertEquals(writer.isRemoteTextureIdEnabled(),
                MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID);
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(writer.getStream(), outStream);
        assertNull(writer.getListener());
        
        MeshWriterListener listener = new MeshWriterListener(){

            @Override
            public void onWriteStart(MeshWriter writer) {}

            @Override
            public void onWriteEnd(MeshWriter writer) {}

            @Override
            public void onWriteProgressChange(MeshWriter writer, 
                float progress){}

            @Override
            public File onMaterialFileRequested(MeshWriter writer, 
                String path){
                return null;
            }

            @Override
            public File onValidateTexture(MeshWriter writer, Texture texture){
                return null;
            }

            @Override
            public void onDidValidateTexture(MeshWriter writer, File f){}

            @Override
            public File onTextureReceived(MeshWriter writer, int textureWidth, 
                int textureHeight){
                return null;
            }

            @Override
            public File onTextureDataAvailable(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight){
                return null;
            }        
        
            @Override
            public void onTextureDataProcessed(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight){}            

            @Override
            public void onChunkAvailable(MeshWriter writer, DataChunk chunk) {}
        };
        
        writer = new MeshWriterJson(loader, outStream, listener);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(writer.getStream(), outStream);
        assertEquals(writer.getListener(), listener);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    public void testGetSetCharset() throws IOException, LockedException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);
        assertNull(writer.getCharset());
        assertTrue(writer.isDefaultCharsetBeingUsed());
        
        //set new charset
        Charset charset = Charset.forName("ISO-8859-1");
        writer.setCharset(charset);
        assertEquals(writer.getCharset(), charset);
        assertFalse(writer.isDefaultCharsetBeingUsed());
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    public void testGetSetEmbedTexturesEnabled() throws IOException, 
            LockedException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);
        assertEquals(writer.isEmbedTexturesEnabled(), 
                MeshWriterJson.DEFAULT_EMBED_TEXTURES);
        
        //set new value
        writer.setEmbedTexturedEnabled(!MeshWriterJson.DEFAULT_EMBED_TEXTURES);
        assertEquals(writer.isEmbedTexturesEnabled(), 
                !MeshWriterJson.DEFAULT_EMBED_TEXTURES);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    public void testGetSetRemoteTextureUrlEnabled() throws IOException, 
            LockedException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);
        assertEquals(writer.isRemoteTextureUrlEnabled(),
                MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL);
        
        //set new value
        writer.setRemoteTextureUrlEnabled(
                !MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL);
        assertEquals(writer.isRemoteTextureUrlEnabled(),
                !MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_URL);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }    
    
    @Test
    public void testGetSetRemoteTextureIdEnabled() throws IOException, 
            LockedException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);
        assertEquals(writer.isRemoteTextureIdEnabled(),
                MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID);
        
        //set new value
        writer.setRemoteTextureIdEnabled(
                !MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID);
        assertEquals(writer.isRemoteTextureIdEnabled(),
                !MeshWriterJson.DEFAULT_USE_REMOTE_TEXTURE_ID);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }    
    
    @Test
    public void testGetSetListener() throws IOException, LockedException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        assertNull(writer.getListener());
        
        //set new listener
        MeshWriterListener listener = new MeshWriterListener(){

            @Override
            public void onWriteStart(MeshWriter writer){}

            @Override
            public void onWriteEnd(MeshWriter writer){}

            @Override
            public void onWriteProgressChange(MeshWriter writer, 
                float progress){}

            @Override
            public File onMaterialFileRequested(MeshWriter writer, String path){
                return null;
            }

            @Override
            public File onValidateTexture(MeshWriter writer, Texture texture){
                return null;
            }

            @Override
            public void onDidValidateTexture(MeshWriter writer, File f){}

            @Override
            public File onTextureReceived(MeshWriter writer, int textureWidth, 
                int textureHeight){
                return null;
            }

            @Override
            public File onTextureDataAvailable(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight) {
                return null;
            }
            
            @Override
            public void onTextureDataProcessed(MeshWriter writer, 
                File textureFile, int textureWidth, int textureHeight){}            

            @Override
            public void onChunkAvailable(MeshWriter writer, DataChunk chunk) {}
        };
        
        writer.setListener(listener);
        assertEquals(writer.getListener(), listener);
        
        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }
    
    @Test
    public void testWriteAndIsReadyRandomAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "randomAscii.json");
        File inF = new File(INPUT_FOLDER, "randomAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyRandomLittleFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "randomLittle.json");
        File inF = new File(INPUT_FOLDER, "randomLittle.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyRandomBigFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "randomBig.json");
        File inF = new File(INPUT_FOLDER, "randomBig.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testWriteAndIsReadyBooksBinaryFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "booksBinary.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testWriteAndIsReadyBooksAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "booksAscii.json");
        File inF = new File(INPUT_FOLDER, "booksAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testWriteAndIsReadyBooksObjFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "booksObj.json");
        File inF = new File(INPUT_FOLDER, "books.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyBooksBinaryStlFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "booksBinaryStl.json");
        File inF = new File(INPUT_FOLDER, "booksBinary.stl");
        
        Loader loader = new LoaderSTL(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyBooksAsciiStlFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "booksAsciiStl.json");
        File inF = new File(INPUT_FOLDER, "booksAscii.stl");
        
        Loader loader = new LoaderSTL(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testWriteAndIsReadyPitcherFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{        
        File outF = new File(TMP_FOLDER, "pitcherObj.json");
        //This file references textures which are already in jpeg format, those
        //textures will be embedded in resulting json file
        File inF = new File(INPUT_FOLDER, "pitcher.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }    
    
    @Test
    public void testWriteAndIsReadyPotroFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{        
        File outF = new File(TMP_FOLDER, "potroObj.json");
        //This file references textures which are already in jpeg format, those
        //textures will be embedded in resulting json file
        File inF = new File(INPUT_FOLDER, "potro.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }    

    @Test
    public void testWriteAndIsReadyM1112File() throws IOException, 
            LockedException, LoaderException, NotReadyException{        
        File outF = new File(TMP_FOLDER, "M1112.json");
        //This file references textures which are already in jpeg format, those
        //textures will be embedded in resulting json file
        File inF = new File(INPUT_FOLDER, "M1112.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }    
    
    @Test
    public void testWriteAndIsReadyPilarAsciiFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "pilarAscii.json");
        File inF = new File(INPUT_FOLDER, "pilarAscii.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyPilarLittleEndianFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "pilarLittleEndian.json");
        File inF = new File(INPUT_FOLDER, "pilarLittleEndian.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }

    @Test
    public void testWriteAndIsReadyPilarBigEndianFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{
        File outF = new File(TMP_FOLDER, "pilarBigEndian.json");
        File inF = new File(INPUT_FOLDER, "pilarBigEndian.ply");
        
        Loader loader = new LoaderPLY(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testWriteAndIsNewObjectFile() throws IOException, 
            LockedException, LoaderException, NotReadyException{        
        File outF = new File(TMP_FOLDER, "newObject.json");
        //This file references textures which are already in jpeg format, those
        //textures will be embedded in resulting json file
        File inF = new File(INPUT_FOLDER, "newObject.obj");
        
        Loader loader = new LoaderOBJ(inF);
        FileOutputStream outStream = new FileOutputStream(outF);
        
        MeshWriterJson writer = new MeshWriterJson(loader, outStream);        
        writer.setListener(this);
        //enable texture embed, remote url and remote id
        writer.setEmbedTexturedEnabled(true);
        writer.setRemoteTextureUrlEnabled(true);
        writer.setRemoteTextureIdEnabled(true);        
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
        writer = new MeshWriterJson(loader, null);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
        writer = new MeshWriterJson(null, outStream);
        assertFalse(writer.isReady());
        try{
            writer.write();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }    
    
    private boolean startValid = true;
    private boolean endValid = true;
    private boolean progressValid = true;
    private boolean lockedValid = true;
    
    private int startCounter = 0;
    private int endCounter = 0;
    private float previousProgress = 0.0f;

    @Override
    public void onWriteStart(MeshWriter writer) {
        if(startCounter != 0) startValid = false;
        startCounter++;
        
        testLocked((MeshWriterJson)writer);
    }

    @Override
    public void onWriteEnd(MeshWriter writer) {
        if(endCounter != 0) endValid = false;
        endCounter++;
        
        testLocked((MeshWriterJson)writer);
    }

    @Override
    public void onWriteProgressChange(MeshWriter writer, float progress) {
        if((progress < 0.0) || (progress > 1.0)) progressValid = false;
        if(progress < previousProgress) progressValid = false;
        previousProgress = progress;
        
        testLocked((MeshWriterJson)writer);
    }
    
    @Override
    public void onChunkAvailable(MeshWriter writer, DataChunk chunk) {
        testLocked((MeshWriterJson)writer);
    }
    

    //FOR OBJ LOADEr
    @Override
    public File onMaterialFileRequested(MeshWriter writer, String path) {
        File origF = new File(path);
        File materialFile = new File(INPUT_FOLDER, origF.getName());
        return materialFile;
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
    
    //FOR BINARY LOADER
    private File textureFile;

    @Override
    public File onTextureReceived(MeshWriter writer, int textureWidth, 
            int textureHeight) {
        //generate a temporal file in TMP_FOLDER where texture data will be 
        //stored
        try{
            textureFile = File.createTempFile("tex", ".jpg", new File(
                    TMP_FOLDER));
            return textureFile;
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
    int textureWidth, int textureHeight){
        //this method is called to give an opportunity to delete any generated
        //texture files
        textureFile.delete();
    }
    
    @Override
    public String onRemoteTextureUrlRequested(MeshWriterJson writer, 
            Texture texture, File textureFile) {
        UUID uuid = UUID.randomUUID();
        String url = "http://www.irurueta.com/image?uuid=" + uuid + "&id=" + 
                texture.getId();
        return url;
    }

    @Override
    public String onRemoteTextureIdRequested(MeshWriterJson writer, 
            Texture texture, File textureFile) {
        return UUID.randomUUID().toString();
    }
    
    
    private void testLocked(MeshWriterJson writer){
        if(!writer.isLocked()) lockedValid = false;
        
        try{
            writer.setListener(null);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            writer.write();
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            writer.setCharset(null);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            writer.setEmbedTexturedEnabled(true);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            writer.setRemoteTextureUrlEnabled(true);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
        
        try{
            writer.setRemoteTextureIdEnabled(true);
            lockedValid = false;
        }catch(LockedException e){
        }catch(Throwable e){
            lockedValid = false;
        }
    }
    
    private void resetListener(){
        startValid = endValid = progressValid = lockedValid = true;
        startCounter = endCounter = 0;
        previousProgress = 0.0f;
    }
    
    private boolean isStartValid(){
        return startValid;
    }
    
    private boolean isEndValid(){
        return endValid;
    }
    
    private boolean isProgressValid(){
        return progressValid;
    }
    
    private boolean isLockedValid(){
        return lockedValid;
    }    
}
