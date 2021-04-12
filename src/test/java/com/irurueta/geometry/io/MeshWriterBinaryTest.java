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

    @BeforeClass
    public static void setUpClass() {
        // create folder for generated files
        final File folder = new File(TMP_FOLDER);
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();
    }

    @AfterClass
    public static void tearDownClass() {
        // remove any remaining files in thumbnails folder
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
    public void testConstructors() throws IOException {
        final File outF = new File(TMP_FOLDER, "booksBinary.bin");
        final File inF = new File("./src/test/java/com/irurueta/geometry/io/booksBinary.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        // test constructor with output stream and loader
        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        assertTrue(writer.isReady());
        assertFalse(writer.isLocked());
        assertEquals(writer.getStream(), outStream);
        assertNull(writer.getListener());

        final MeshWriterListener listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(final MeshWriter writer) {
            }

            @Override
            public void onWriteEnd(final MeshWriter writer) {
            }

            @Override
            public void onWriteProgressChange(final MeshWriter writer,
                                              final float progress) {
            }

            @Override
            public File onMaterialFileRequested(final MeshWriter writer,
                                                final String path) {
                return null;
            }

            @Override
            public File onValidateTexture(final MeshWriter writer, final Texture texture) {
                return null;
            }

            @Override
            public void onDidValidateTexture(final MeshWriter writer, final File f) {
            }

            @Override
            public File onTextureReceived(
                    final MeshWriter writer, final int textureWidth,
                    final int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(
                    final MeshWriter writer,
                    final File textureFile, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public void onTextureDataProcessed(
                    final MeshWriter writer,
                    final File textureFile, final int textureWidth, final int textureHeight) {
            }

            @Override
            public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
            }
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
    public void testGetSetListener() throws IOException, LockedException {
        final File outF = new File(TMP_FOLDER, "booksBinary.bin");
        final File inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        final MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        assertNull(writer.getListener());

        // set new listener
        final MeshWriterListener listener = new MeshWriterListener() {

            @Override
            public void onWriteStart(final MeshWriter writer) {
            }

            @Override
            public void onWriteEnd(final MeshWriter writer) {
            }

            @Override
            public void onWriteProgressChange(final MeshWriter writer,
                                              final float progress) {
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
            }

            @Override
            public File onTextureReceived(
                    final MeshWriter writer, final int textureWidth,
                    final int textureHeight) {
                return null;
            }

            @Override
            public File onTextureDataAvailable(
                    final MeshWriter writer,
                    final File textureFile, final int textureWidth, final int textureHeight) {
                return null;
            }

            @Override
            public void onTextureDataProcessed(
                    final MeshWriter writer,
                    final File textureFile, final int textureWidth, final int textureHeight) {
            }

            @Override
            public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
            }
        };

        writer.setListener(listener);
        assertEquals(writer.getListener(), listener);

        assertTrue(outF.exists());
        assertTrue(outF.delete());
    }

    @Test
    public void testWriteAndIsReadyRandomAsciiFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "randomAscii.bin");
        final File inF = new File(INPUT_FOLDER, "randomAscii.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyRandomLittleFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "randomLittle.bin");
        final File inF = new File(INPUT_FOLDER, "randomLittle.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyRandomBigFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "randomBig.bin");
        final File inF = new File(INPUT_FOLDER, "randomBig.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyBooksBinaryFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "booksBinary.bin");
        final File inF = new File(INPUT_FOLDER, "booksBinary.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyBooksAsciiFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "booksAscii.bin");
        final File inF = new File(INPUT_FOLDER, "booksAscii.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyBooksObjFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "booksObj.bin");
        final File inF = new File(INPUT_FOLDER, "books.obj");

        final Loader loader = new LoaderOBJ(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyBooksBinaryStlFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "booksBinaryStl.bin");
        final File inF = new File(INPUT_FOLDER, "booksBinary.stl");

        final Loader loader = new LoaderSTL(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyBooksAsciiStlFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "booksAsciiStl.bin");
        final File inF = new File(INPUT_FOLDER, "booksAscii.stl");

        final Loader loader = new LoaderSTL(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyPitcherObjFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "pitcherObj.bin");
        final File inF = new File(INPUT_FOLDER, "pitcher.obj");

        final Loader loader = new LoaderOBJ(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyPotroFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "potroObj.bin");
        // This file references textures which are already in jpeg format, those
        // textures will be embedded in resulting binary file
        final File inF = new File(INPUT_FOLDER, "potro.obj");

        final Loader loader = new LoaderOBJ(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyPilarAsciiFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "pilarAscii.bin");
        final File inF = new File(INPUT_FOLDER, "pilarAscii.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyPilarLittleEndianFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "pilarLittleEndian.bin");
        final File inF = new File(INPUT_FOLDER, "pilarLittleEndian.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testWriteAndIsReadyPilarBigEndianFile() throws IOException,
            LockedException, LoaderException, NotReadyException {
        final File outF = new File(TMP_FOLDER, "pilarBigEndian.bin");
        final File inF = new File(INPUT_FOLDER, "pilarBigEndian.ply");

        final Loader loader = new LoaderPLY(inF);
        final FileOutputStream outStream = new FileOutputStream(outF);

        MeshWriterBinary writer = new MeshWriterBinary(loader, outStream);
        writer.setListener(this);
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
        writer = new MeshWriterBinary(loader, null);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
        writer = new MeshWriterBinary(null, outStream);
        assertFalse(writer.isReady());
        try {
            writer.write();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Override
    public void onWriteStart(final MeshWriter writer) {
        if (startCounter != 0) {
            startValid = false;
        }
        startCounter++;

        checkLocked((MeshWriterBinary) writer);
    }

    @Override
    public void onWriteEnd(final MeshWriter writer) {
        if (endCounter != 0) {
            endValid = false;
        }
        endCounter++;

        checkLocked((MeshWriterBinary) writer);
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

        checkLocked((MeshWriterBinary) writer);
    }

    @Override
    public void onChunkAvailable(final MeshWriter writer, final DataChunk chunk) {
        checkLocked((MeshWriterBinary) writer);
    }

    // FOR OBJ LOADER
    @Override
    public File onMaterialFileRequested(final MeshWriter writer, final String path) {
        File origF = new File(path);
        return new File(INPUT_FOLDER, origF.getName());
    }

    @Override
    public File onValidateTexture(final MeshWriter writer, final Texture texture) {
        final File origF = new File(texture.getFileName());
        final File inputTextureFile = new File(INPUT_FOLDER, origF.getName());
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
    public File onTextureReceived(final MeshWriter writer, final int textureWidth,
                                  final int textureHeight) {
        // generate a temporal file in TMP_FOLDER where texture data will be
        // stored
        try {
            // FOR BINARY LOADER
            return File.createTempFile("tex", ".jpg", new File(
                    TMP_FOLDER));
        } catch (final IOException e) {
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
        // this method is called to give an opportunity to delete any generated
        // texture files

        //noinspection ResultOfMethodCallIgnored
        textureFile.delete();
    }


    private void checkLocked(final MeshWriterBinary writer) {
        if (!writer.isLocked()) {
            lockedValid = false;
        }

        try {
            writer.setListener(null);
            lockedValid = false;
        } catch (final LockedException ignore) {
        } catch (final Throwable e) {
            lockedValid = false;
        }

        try {
            writer.write();
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
}
