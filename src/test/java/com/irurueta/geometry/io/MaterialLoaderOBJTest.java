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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class MaterialLoaderOBJTest implements MaterialLoaderListener {

    private boolean forceLoaderException;

    @Test
    public void testConstructor() throws IOException {
        // Test default constructor
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ();

        // check correctness
        assertFalse(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertFalse(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED,
                loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());

        // Test constructor with file
        File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f);

        // check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertTrue(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED,
                loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        // release file resources
        loader.close();

        // Force IOException
        loader = null;
        f = new File("fake.mtl");
        try {
            loader = new MaterialLoaderOBJ(f);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);


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
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED,
                loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertSame(loader.getListener(), this);

        // Test constructor with file and listener
        f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f, this);

        // check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(0, loader.getMaterials().size());
        assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                loader.getFileSizeLimitToKeepInMemory());
        assertTrue(loader.hasFile());
        assertEquals(MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED,
                loader.isTextureValidationEnabled());
        assertFalse(loader.isLocked());
        assertSame(this, loader.getListener());
        // release file resources
        loader.close();

        // Force IOException
        loader = null;
        f = new File("fake.mtl");
        try {
            loader = new MaterialLoaderOBJ(f, this);
            fail("IOException expected but not thrown");
        } catch (final IOException ignore) {
        }
        assertNull(loader);

    }

    @Test
    public void testLoad() throws IOException, LockedException,
            NotReadyException, LoaderException {

        final File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final Set<Material> materials = loader.load();
        loader.close();

        assertFalse(materials.isEmpty());
        assertTrue(loader.areMaterialsAvailable());
        assertSame(materials, loader.getMaterials());

        for (final Material material : materials) {
            assertTrue(material.getId() >= 0 && material.getId() < materials.size());
        }

        // Force LoaderException
        loader = new MaterialLoaderOBJ(f, this);
        forceLoaderException = true;
        try {
            loader.load();
            fail("LoaderException expected but not thrown");
        } catch (final LoaderException ignore) {
        }
        loader.close();

        // Force NotReadyException
        loader = new MaterialLoaderOBJ(this);
        try {
            loader.load();
            fail("NotReadyException expected but not thrown");
        } catch (final NotReadyException ignore) {
        }
    }

    @Test
    public void testGetMaterialByName() throws IOException, LockedException,
            NotReadyException, LoaderException {

        final File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        final MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final Set<Material> materials = loader.load();
        loader.close();

        for (final Material material : materials) {
            final MaterialOBJ material2 = (MaterialOBJ) material;
            final MaterialOBJ material3 = loader.getMaterialByName(material2.getMaterialName());
            assertSame(material2, material3);
            assertTrue(loader.containsMaterial(material2.getMaterialName()));
        }

        // test for non-existing material
        assertNull(loader.getMaterialByName(""));
        assertFalse(loader.containsMaterial(""));
    }

    @Test
    public void testGetMaterialByTextureMapName() throws IOException,
            LockedException, NotReadyException, LoaderException {

        final File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        final MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);

        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());

        forceLoaderException = false;
        final Set<Material> materials = loader.load();
        loader.close();

        for (final Material material : materials) {
            final Texture tex1 = material.getAlphaTextureMap();
            final Texture tex2 = material.getAmbientTextureMap();
            final Texture tex3 = material.getBumpTextureMap();
            final Texture tex4 = material.getDiffuseTextureMap();
            final Texture tex5 = material.getSpecularTextureMap();

            if (tex1 != null) {
                final Material material2 = loader.getMaterialByTextureMapName(tex1.getFileName());
                assertSame(material, material2);
            }

            if (tex2 != null) {
                final Material material2 = loader.getMaterialByTextureMapName(tex2.getFileName());
                assertSame(material, material2);
            }

            if (tex3 != null) {
                final Material material2 = loader.getMaterialByTextureMapName(tex3.getFileName());
                assertSame(material, material2);
            }

            if (tex4 != null) {
                final Material material2 = loader.getMaterialByTextureMapName(tex4.getFileName());
                assertSame(material, material2);
            }

            if (tex5 != null) {
                final Material material2 = loader.getMaterialByTextureMapName(tex5.getFileName());
                assertSame(material, material2);
            }
        }

        // test for non-existing texture name
        assertNull(loader.getMaterialByTextureMapName(""));
    }

    @Test
    public void testIsSetTextureValidationEnabled() throws IOException {
        try (final MaterialLoaderOBJ loader = new MaterialLoaderOBJ()) {

            // check default value
            assertTrue(loader.isTextureValidationEnabled());

            // set new value
            loader.setTextureValidationEnabled(false);

            // check
            assertFalse(loader.isTextureValidationEnabled());
        }
    }

    @Test
    public void testGetFileSizeLimitToKeepInMemory() throws LockedException, IOException {
        try (final MaterialLoaderOBJ loader = new MaterialLoaderOBJ()) {

            // check default value
            assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY,
                    loader.getFileSizeLimitToKeepInMemory());

            // set new value
            loader.setFileSizeLimitToKeepInMemory(
                    MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY / 2);

            // check
            assertEquals(MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY / 2,
                    loader.getFileSizeLimitToKeepInMemory());
        }
    }

    @Test
    public void testGetSetListener() throws LockedException, IOException {
        try (final MaterialLoaderOBJ loader = new MaterialLoaderOBJ()) {

            // check default value
            assertNull(loader.getListener());

            // set new value
            loader.setListener(this);

            // check
            assertSame(this, loader.getListener());
        }
    }

    @Test
    public void testSetFile() throws LockedException, IOException {
        final MaterialLoaderOBJ loader = new MaterialLoaderOBJ();

        // check
        assertFalse(loader.hasFile());

        // set file
        final File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
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
        try {
            loader.load();
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        } catch (final Exception e) {
            fail("LockedException expected but not thrown");
        }
    }

    @Override
    public void onLoadEnd(final MaterialLoader loader) {
        // test locked exception because loader is loading
        try {
            loader.load();
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        } catch (final Exception e) {
            fail("LockedException expected but not thrown");
        }
    }

    @Override
    public boolean onValidateTexture(
            final MaterialLoader loader, final Texture texture) {
        // test locked exception because loader is loading
        try {
            loader.load();
            fail("LockedException expected but not thrown");
        } catch (final LockedException ignore) {
        } catch (final Exception e) {
            fail("LockedException expected but not thrown");
        }

        if (texture != null) {
            texture.setValid(!forceLoaderException);
        }

        return !forceLoaderException;
    }
}
