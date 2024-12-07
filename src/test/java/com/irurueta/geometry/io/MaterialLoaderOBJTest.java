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

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MaterialLoaderOBJTest implements MaterialLoaderListener {

    private boolean forceLoaderException;

    @Test
    void testConstructor() throws IOException {
        // Test default constructor
        var loader = new MaterialLoaderOBJ();

        // check correctness
        assertFalse(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertFalse(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED, loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Test constructor with file
        final var f1 = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f1);

        // check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertTrue(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED, loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // release file resources
        loader.close();

        // Force IOException
        final var f2 = new File("fake.mtl");
        //noinspection resource
        assertThrows(IOException.class, () -> new MaterialLoaderOBJ(f2));


        // Test constructor with listener
        loader = new MaterialLoaderOBJ(this);

        // check correctness
        assertFalse(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertFalse(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED, loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertSame(this, loader.getListener());

        // Test constructor with file and listener
        final var f3 = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f3, this);

        // check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertTrue(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED, loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertSame(this, loader.getListener());
        // release file resources
        loader.close();

        // Force IOException
        final var f4 = new File("fake.mtl");
        //noinspection resource
        assertThrows(IOException.class, () -> new MaterialLoaderOBJ(f4, this));
    }

    @Test
    void testLoad() throws IOException, LockedException, NotReadyException, LoaderException {

        final var f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        var loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final var materials = loader.load();
        loader.close();

        assertFalse(materials.isEmpty());
        assertTrue(loader.areMaterialsAvailable());
        assertSame(materials, loader.getMaterials());

        for (final var material : materials) {
            assertTrue(material.getId() >= 0 && material.getId() < materials.size());
        }

        // Force LoaderException
        loader = new MaterialLoaderOBJ(f, this);
        forceLoaderException = true;
        assertThrows(LoaderException.class, loader::load);
        loader.close();

        // Force NotReadyException
        loader = new MaterialLoaderOBJ(this);
        assertThrows(NotReadyException.class, loader::load);
    }

    @Test
    void testGetMaterialByName() throws IOException, LockedException, NotReadyException, LoaderException {

        final var f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        final var loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final var materials = loader.load();
        loader.close();

        for (final var material : materials) {
            final var material2 = (MaterialOBJ) material;
            final var material3 = loader.getMaterialByName(material2.getMaterialName());
            assertSame(material2, material3);
            assertTrue(loader.containsMaterial(material2.getMaterialName()));
        }

        // test for non-existing material
        assertNull(loader.getMaterialByName(""));
        assertFalse(loader.containsMaterial(""));
    }

    @Test
    void testGetMaterialByTextureMapName() throws IOException, LockedException, NotReadyException, LoaderException {

        final var f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        final var loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final var materials = loader.load();
        loader.close();

        for (final var material : materials) {
            final var tex1 = material.getAlphaTextureMap();
            final var tex2 = material.getAmbientTextureMap();
            final var tex3 = material.getBumpTextureMap();
            final var tex4 = material.getDiffuseTextureMap();
            final var tex5 = material.getSpecularTextureMap();

            if (tex1 != null) {
                final var material2 = loader.getMaterialByTextureMapName(tex1.getFileName());
                assertSame(material, material2);
            }

            if (tex2 != null) {
                final var material2 = loader.getMaterialByTextureMapName(tex2.getFileName());
                assertSame(material, material2);
            }

            if (tex3 != null) {
                final var material2 = loader.getMaterialByTextureMapName(tex3.getFileName());
                assertSame(material, material2);
            }

            if (tex4 != null) {
                final var material2 = loader.getMaterialByTextureMapName(tex4.getFileName());
                assertSame(material, material2);
            }

            if (tex5 != null) {
                final var material2 = loader.getMaterialByTextureMapName(tex5.getFileName());
                assertSame(material, material2);
            }
        }

        // test for non-existing texture name
        assertNull(loader.getMaterialByTextureMapName(""));
    }

    @Test
    void testIsSetTextureValidationEnabled() throws IOException {
        try (final var loader = new MaterialLoaderOBJ()) {

            // check default value
            assertTrue(loader.isTextureValidationEnabled());

            // set new value
            loader.setTextureValidationEnabled(false);

            // check
            assertFalse(loader.isTextureValidationEnabled());
        }
    }

    @Test
    void testGetFileSizeLimitToKeepInMemory() throws LockedException, IOException {
        try (final var loader = new MaterialLoaderOBJ()) {

            // check default value
            assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                    loader.getFileSizeLimitToKeepInMemory());

            // set new value
            loader.setFileSizeLimitToKeepInMemory(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY / 2);

            // check
            assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY / 2,
                    loader.getFileSizeLimitToKeepInMemory());
        }
    }

    @Test
    void testGetSetListener() throws LockedException, IOException {
        try (final var loader = new MaterialLoaderOBJ()) {

            // check default value
            assertNull(loader.getListener());

            // set new value
            loader.setListener(this);

            // check
            assertSame(this, loader.getListener());
        }
    }

    @Test
    void testSetFile() throws LockedException, IOException {
        final var loader = new MaterialLoaderOBJ();

        // check
        assertFalse(loader.hasFile());

        // set file
        final var f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader.setFile(f);

        // check
        assertTrue(loader.hasFile());

        // set file again
        loader.setFile(f);

        assertTrue(loader.hasFile());

        // release file resources
        loader.close();

        assertFalse(loader.hasFile());

    }

    @Override
    public void onLoadStart(final MaterialLoader loader) {
        // test locked exception because loader is loading
        assertThrows(LockedException.class, loader::load);
    }

    @Override
    public void onLoadEnd(final MaterialLoader loader) {
        // test locked exception because loader is loading
        assertThrows(LockedException.class, loader::load);
    }

    @Override
    public boolean onValidateTexture(final MaterialLoader loader, final Texture texture) {
        // test locked exception because loader is loading
        assertThrows(LockedException.class, loader::load);

        if (texture != null) {
            texture.setValid(!forceLoaderException);
        }

        return !forceLoaderException;
    }
}
