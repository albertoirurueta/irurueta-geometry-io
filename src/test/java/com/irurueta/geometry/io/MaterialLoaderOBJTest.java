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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MaterialLoaderOBJTest implements MaterialLoaderListener {

    @TempDir
    private Path tempDir;

    private int counter;

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

    /**
     * Checks that every color, coefficient, transparency and illumination directive is parsed.
     */
    @Test
    void load_whenAllScalarDirectives_parsesEveryValue() throws IOException, LockedException, NotReadyException,
            LoaderException {
        final var file = writeMtl("# comment", "", "   ", "\t", "newmtl first", "Ka 1.0 0.5 0.0", "Kd 0.0 1.0 0.5",
                "Ks 0.5 0.0 1.0", "Ns 10.5", "d 0.5", "illum 2");

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            final var materials = loader.load();

            assertEquals(1, materials.size());
            assertTrue(loader.areMaterialsAvailable());
            final var material = loader.getMaterialByName("first");
            assertNotNull(material);
            assertEquals("first", material.getMaterialName());
            assertEquals(0, material.getId());
            assertEquals(255, material.getAmbientRedColor());
            assertEquals(127, material.getAmbientGreenColor());
            assertEquals(0, material.getAmbientBlueColor());
            assertEquals(0, material.getDiffuseRedColor());
            assertEquals(255, material.getDiffuseGreenColor());
            assertEquals(127, material.getDiffuseBlueColor());
            assertEquals(127, material.getSpecularRedColor());
            assertEquals(0, material.getSpecularGreenColor());
            assertEquals(255, material.getSpecularBlueColor());
            assertEquals(10.5f, material.getSpecularCoefficient(), 0.0f);
            assertEquals(127, material.getTransparency());
            assertEquals(Illumination.HIGHLIGHT_ON, material.getIllumination());
        }
    }

    /**
     * Checks alias directives (Ni, Tr), mixed case commands and several materials.
     */
    @Test
    void load_whenAliasesAndMixedCase_parsesAllMaterials() throws IOException, LockedException, NotReadyException,
            LoaderException {
        final var file = writeMtl("NEWMTL one", "NI 3.0", "tr 1.0", "ILLUM 1", "unknown directive", "newmtl two words",
                "KA 0.0 0.0 0.0");

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            final var materials = loader.load();

            assertEquals(2, materials.size());
            final var one = loader.getMaterialByName("one");
            assertNotNull(one);
            assertEquals(3.0f, one.getSpecularCoefficient(), 0.0f);
            assertEquals(255, one.getTransparency());
            assertEquals(Illumination.COLOR_AND_AMBIENT_ON, one.getIllumination());
            assertTrue(loader.containsMaterial("two words"));
            assertFalse(loader.containsMaterial("three"));
            assertNull(loader.getMaterialByName(null));
            assertNull(loader.getMaterialByTextureMapName(null));
        }
    }

    /**
     * Checks that every texture map directive is parsed when texture validation succeeds.
     */
    @Test
    void load_whenTextureDirectives_parsesAllTextureMaps() throws IOException, LockedException, NotReadyException,
            LoaderException {
        final var file = writeMtl("newmtl tex", "map_Kd diffuse.png", "map_Ka ambient.png", "map_Ks specular.png",
                "map_d alpha.png", "map_bump bump.png");

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            loader.load();

            final var material = loader.getMaterialByName("tex");
            assertNotNull(material);
            assertEquals("diffuse.png", material.getDiffuseTextureMap().getFileName());
            assertEquals("ambient.png", material.getAmbientTextureMap().getFileName());
            assertEquals("specular.png", material.getSpecularTextureMap().getFileName());
            assertEquals("alpha.png", material.getAlphaTextureMap().getFileName());
            assertEquals("bump.png", material.getBumpTextureMap().getFileName());
            assertSame(material, loader.getMaterialByTextureMapName("diffuse.png"));
            assertSame(material, loader.getMaterialByTextureMapName("ambient.png"));
            assertSame(material, loader.getMaterialByTextureMapName("specular.png"));
            assertSame(material, loader.getMaterialByTextureMapName("alpha.png"));
        }
    }

    /**
     * Checks the bump alias directive without any listener (no validation possible).
     */
    @Test
    void load_whenBumpAliasWithoutListener_parsesBumpTexture() throws IOException, LockedException,
            NotReadyException, LoaderException {
        final var file = writeMtl("newmtl tex", "bump bump.png");

        try (final var loader = new MaterialLoaderOBJ(file)) {
            loader.load();

            final var material = loader.getMaterialByName("tex");
            assertNotNull(material);
            assertEquals("bump.png", material.getBumpTextureMap().getFileName());
        }
    }

    /**
     * Checks that texture validation failures raise a LoaderException for every texture directive.
     */
    @ParameterizedTest
    @ValueSource(strings = {"map_Kd", "map_Ka", "map_Ks", "map_d", "map_bump"})
    void load_whenTextureValidationFails_throwsLoaderException(final String directive) throws IOException {
        final var file = writeMtl("newmtl tex", directive + " texture.png");

        forceLoaderException = true;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that invalid textures are accepted when texture validation is disabled.
     */
    @Test
    void load_whenValidationDisabledAndTextureInvalid_loadsMaterial() throws IOException, LockedException,
            NotReadyException, LoaderException {
        final var file = writeMtl("newmtl tex", "map_Kd texture.png");

        forceLoaderException = true;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            loader.setTextureValidationEnabled(false);
            loader.load();

            assertTrue(loader.containsMaterial("tex"));
        } finally {
            forceLoaderException = false;
        }
    }

    /**
     * Checks that malformed directives raise a LoaderException.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Ka 1.0 0.5", "Ka a b c", "Kd 1.0", "Kd x y z", "Ks", "Ks 1 2 q", "Ns", "Ns abc", "d",
            "d xyz", "Tr", "illum", "illum abc", "Ni nope"})
    void load_whenMalformedDirective_throwsLoaderException(final String line) throws IOException {
        final var file = writeMtl("newmtl broken", line);

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that properties given before any material is declared raise a LoaderException.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Ka 1 1 1", "Kd 1 1 1", "Ks 1 1 1", "Ns 1", "Ni 1", "d 1", "Tr 1", "illum 1",
            "map_Kd a.png", "map_Ka a.png", "map_Ks a.png", "map_d a.png", "map_bump a.png"})
    void load_whenPropertyBeforeMaterial_throwsLoaderException(final String line) throws IOException {
        final var file = writeMtl(line, "newmtl late");

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            assertThrows(LoaderException.class, loader::load);
        }
    }

    /**
     * Checks that files without any material definition produce an empty set instead of failing unexpectedly.
     *
     * @param lines file contents.
     * @throws Exception if any unexpected error occurs.
     */
    @ParameterizedTest
    @MethodSource("filesWithoutMaterials")
    void load_whenNoMaterialIsDefined_returnsEmptySet(final String[] lines) throws Exception {
        final var file = writeMtl(lines);

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            final var materials = loader.load();
            assertTrue(materials.isEmpty());
            assertFalse(loader.isLocked());

            // can be loaded again
            assertTrue(loader.load().isEmpty());
        }
    }

    /**
     * Checks that a loader is unlocked after failing to load a hostile file, and that materials from a previous
     * load are not leaked into a later one.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileIsCorrupted_unlocksLoader() throws Exception {
        final var file = writeMtl("newmtl a", "Ka x y z");

        forceLoaderException = false;
        try (final var loader = new MaterialLoaderOBJ(file, this)) {
            assertThrows(LoaderException.class, loader::load);
            assertFalse(loader.isLocked());

            loader.setFile(writeMtl("# nothing"));
            assertTrue(loader.load().isEmpty());
        }
    }

    /**
     * Checks that binary garbage only produces checked exceptions.
     *
     * @throws Exception if any unexpected error occurs.
     */
    @Test
    void load_whenFileIsBinaryGarbage_onlyCheckedExceptionsAreThrown() throws Exception {
        final var random = new java.util.Random(4321L);
        forceLoaderException = false;
        for (var i = 0; i < 20; i++) {
            final var bytes = new byte[512 + i * 50];
            random.nextBytes(bytes);
            final var path = tempDir.resolve("garbage" + i + ".mtl");
            Files.write(path, bytes);

            try (final var loader = new MaterialLoaderOBJ(path.toFile(), this)) {
                try {
                    loader.load();
                } catch (final LoaderException ignore) {
                    // expected
                }
            }
        }
    }

    /**
     * Provides files without material definitions.
     *
     * @return stream of file contents.
     */
    private static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> filesWithoutMaterials() {
        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of((Object) new String[]{}),
                org.junit.jupiter.params.provider.Arguments.of((Object) new String[]{"# comment only"}),
                org.junit.jupiter.params.provider.Arguments.of((Object) new String[]{"", "   ", "\t"}),
                org.junit.jupiter.params.provider.Arguments.of((Object) new String[]{"unknown directive 1 2 3"}));
    }

    /**
     * Checks that an out of range illumination value yields a null illumination.
     */
    @Test
    void load_whenIllumOutOfRange_setsNullIllumination() throws IOException,
            LockedException, NotReadyException, LoaderException {
        final var file = writeMtl("newmtl outOfRange", "illum 99");

        try (final var loader = new MaterialLoaderOBJ(file)) {
            loader.load();

            final var material = loader.getMaterialByName("outOfRange");
            assertNotNull(material);
            assertNull(material.getIllumination());
        }
    }

    /**
     * Checks that a missing file raises an IOException when setting it.
     */
    @Test
    void setFile_whenFileMissing_throwsIOException() throws IOException {
        final var missing = new File(tempDir.toFile(), "missing.mtl");

        try (final var loader = new MaterialLoaderOBJ()) {
            assertThrows(IOException.class, () -> loader.setFile(missing));
            assertFalse(loader.isReady());
        }
    }

    /**
     * Checks that a loader without file is not ready to load.
     */
    @Test
    void load_whenNoFile_throwsNotReadyException() throws IOException {
        try (final var loader = new MaterialLoaderOBJ()) {
            assertThrows(NotReadyException.class, loader::load);
        }
    }

    /**
     * Writes an MTL file with provided lines into the temporary directory.
     *
     * @param lines lines of the file.
     * @return created file.
     * @throws IOException if the file cannot be written.
     */
    private File writeMtl(final String... lines) throws IOException {
        final var path = tempDir.resolve("fixture" + (counter++) + ".mtl");
        Files.writeString(path, String.join("\n", lines) + "\n");
        return path.toFile();
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
