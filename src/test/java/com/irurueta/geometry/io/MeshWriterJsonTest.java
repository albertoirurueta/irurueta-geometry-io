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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshWriterJsonTest implements MeshWriterJsonListener {

    private static final String INPUT_FOLDER = "./src/test/java/com/irurueta/geometry/io/";

    private static final String TMP_FOLDER = "./src/test/java/com/irurueta/geometry/io/tmp/";

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
}
