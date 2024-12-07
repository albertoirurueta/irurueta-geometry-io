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

import com.irurueta.statistics.UniformRandomizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {

    private static final int MIN_COLOR_VALUE = 0;
    private static final int MAX_COLOR_VALUE = 255;

    private static final float MIN_SPECULAR_COEFFICIENT = 0.0f;
    private static final float MAX_SPECULAR_COEFFICIENT = 1000.0f;

    private static final int MIN_ILLUMINATION_VALUE = 0;
    private static final int MAX_ILLUMINATION_VALUE = 10;

    @Test
    void testConstructor() {
        final var material = new Material();
        assertNotNull(material);
    }

    @Test
    void testGetSetId() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var id = randomizer.nextInt();

        // check default value
        assertEquals(0, material.getId());

        // set new value
        material.setId(id);
        // check correctness
        assertEquals(id, material.getId());
    }

    @Test
    void testGetSetAmbientRedColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getAmbientRedColor());
        assertFalse(material.isAmbientRedColorAvailable());

        // set new value
        material.setAmbientRedColor(color);
        // check correctness
        assertEquals(color, material.getAmbientRedColor());
        assertTrue(material.isAmbientRedColorAvailable());
    }

    @Test
    void testGetSetAmbientGreenColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getAmbientGreenColor());
        assertFalse(material.isAmbientGreenColorAvailable());

        // set new value
        material.setAmbientGreenColor(color);
        // check correctness
        assertEquals(color, material.getAmbientGreenColor());
        assertTrue(material.isAmbientGreenColorAvailable());
    }

    @Test
    void testGetSetAmbientBlueColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getAmbientBlueColor());
        assertFalse(material.isAmbientBlueColorAvailable());

        // set new value
        material.setAmbientBlueColor(color);
        // check correctness
        assertEquals(color, material.getAmbientBlueColor());
        assertTrue(material.isAmbientBlueColorAvailable());
    }

    @Test
    void testSetAmbientColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var red = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var green = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var blue = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getAmbientRedColor());
        assertFalse(material.isAmbientRedColorAvailable());
        assertEquals(-1, material.getAmbientGreenColor());
        assertFalse(material.isAmbientGreenColorAvailable());
        assertEquals(-1, material.getAmbientBlueColor());
        assertFalse(material.isAmbientBlueColorAvailable());

        assertFalse(material.isAmbientColorAvailable());

        // set new value
        material.setAmbientColor(red, green, blue);

        // check correctness
        assertEquals(red, material.getAmbientRedColor());
        assertTrue(material.isAmbientRedColorAvailable());
        assertEquals(green, material.getAmbientGreenColor());
        assertTrue(material.isAmbientGreenColorAvailable());
        assertEquals(blue, material.getAmbientBlueColor());
        assertTrue(material.isAmbientBlueColorAvailable());

        assertTrue(material.isAmbientColorAvailable());
    }

    @Test
    void testGetSetDiffuseRedColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getDiffuseRedColor());
        assertFalse(material.isDiffuseRedColorAvailable());

        // set new value
        material.setDiffuseRedColor(color);
        // check correctness
        assertEquals(color, material.getDiffuseRedColor());
        assertTrue(material.isDiffuseRedColorAvailable());
    }

    @Test
    void testGetSetDiffuseGreenColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getDiffuseGreenColor());
        assertFalse(material.isDiffuseGreenColorAvailable());

        // set new value
        material.setDiffuseGreenColor(color);
        // check correctness
        assertEquals(color, material.getDiffuseGreenColor());
        assertTrue(material.isDiffuseGreenColorAvailable());
    }

    @Test
    void testGetSetDiffuseBlueColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getDiffuseBlueColor());
        assertFalse(material.isDiffuseBlueColorAvailable());

        // set new value
        material.setDiffuseBlueColor(color);
        // check correctness
        assertEquals(color, material.getDiffuseBlueColor());
        assertTrue(material.isDiffuseBlueColorAvailable());
    }

    @Test
    void testSetDiffuseColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var red = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var green = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var blue = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getDiffuseRedColor());
        assertFalse(material.isDiffuseRedColorAvailable());
        assertEquals(-1, material.getDiffuseGreenColor());
        assertFalse(material.isDiffuseGreenColorAvailable());
        assertEquals(-1, material.getDiffuseBlueColor());
        assertFalse(material.isDiffuseBlueColorAvailable());

        assertFalse(material.isDiffuseColorAvailable());

        // set new value
        material.setDiffuseColor(red, green, blue);

        // check correctness
        assertEquals(red, material.getDiffuseRedColor());
        assertTrue(material.isDiffuseRedColorAvailable());
        assertEquals(green, material.getDiffuseGreenColor());
        assertTrue(material.isDiffuseGreenColorAvailable());
        assertEquals(blue, material.getDiffuseBlueColor());
        assertTrue(material.isDiffuseBlueColorAvailable());

        assertTrue(material.isDiffuseColorAvailable());
    }

    @Test
    void testGetSetSpecularRedColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getSpecularRedColor());
        assertFalse(material.isSpecularRedColorAvailable());

        // set new value
        material.setSpecularRedColor(color);
        // check correctness
        assertEquals(color, material.getSpecularRedColor());
        assertTrue(material.isSpecularRedColorAvailable());
    }

    @Test
    void testGetSetSpecularGreenColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getSpecularGreenColor());
        assertFalse(material.isSpecularGreenColorAvailable());

        // set new value
        material.setSpecularGreenColor(color);
        // check correctness
        assertEquals(color, material.getSpecularGreenColor());
        assertTrue(material.isSpecularGreenColorAvailable());
    }

    @Test
    void testGetSetSpecularBlueColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var color = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getSpecularBlueColor());
        assertFalse(material.isSpecularBlueColorAvailable());

        // set new value
        material.setSpecularBlueColor(color);
        // check correctness
        assertEquals(color, material.getSpecularBlueColor());
        assertTrue(material.isSpecularBlueColorAvailable());
    }

    @Test
    void testSetSpecularColor() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var red = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var green = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);
        final var blue = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getSpecularRedColor());
        assertFalse(material.isSpecularRedColorAvailable());
        assertEquals(-1, material.getSpecularGreenColor());
        assertFalse(material.isSpecularGreenColorAvailable());
        assertEquals(-1, material.getSpecularBlueColor());
        assertFalse(material.isSpecularBlueColorAvailable());

        assertFalse(material.isSpecularColorAvailable());

        // set new value
        material.setSpecularColor(red, green, blue);

        // check correctness
        assertEquals(red, material.getSpecularRedColor());
        assertTrue(material.isSpecularRedColorAvailable());
        assertEquals(green, material.getSpecularGreenColor());
        assertTrue(material.isSpecularGreenColorAvailable());
        assertEquals(blue, material.getSpecularBlueColor());
        assertTrue(material.isSpecularBlueColorAvailable());

        assertTrue(material.isSpecularColorAvailable());
    }

    @Test
    void testGetSetSpecularCoefficient() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var coef = randomizer.nextFloat(MIN_SPECULAR_COEFFICIENT, MAX_SPECULAR_COEFFICIENT);

        // check default value
        assertEquals(0.0f, material.getSpecularCoefficient(), 0.0);
        assertFalse(material.isSpecularCoefficientAvailable());

        // set new value
        material.setSpecularCoefficient(coef);
        // check correctness
        assertEquals(coef, material.getSpecularCoefficient(), 0.0);
        assertTrue(material.isSpecularCoefficientAvailable());
    }

    @Test
    void testGetSetAmbientTextureMap() {
        final var material = new Material();

        final var texture = new Texture("ambient.png", 0);

        // check default value
        assertNull(material.getAmbientTextureMap());
        assertFalse(material.isAmbientTextureMapAvailable());

        // set new value
        material.setAmbientTextureMap(texture);
        // check correctness
        assertSame(texture, material.getAmbientTextureMap());
        assertTrue(material.isAmbientTextureMapAvailable());
    }

    @Test
    void testGetSetDiffuseTextureMap() {
        final var material = new Material();

        final var texture = new Texture("diffuse.png", 0);

        // check default value
        assertNull(material.getDiffuseTextureMap());
        assertFalse(material.isDiffuseTextureMapAvailable());

        // set new value
        material.setDiffuseTextureMap(texture);
        // check correctness
        assertSame(texture, material.getDiffuseTextureMap());
        assertTrue(material.isDiffuseTextureMapAvailable());
    }

    @Test
    void testGetSetSpecularTextureMap() {
        final var material = new Material();

        final var texture = new Texture("specular.png", 0);

        // check default value
        assertNull(material.getSpecularTextureMap());
        assertFalse(material.isSpecularTextureMapAvailable());

        // set new value
        material.setSpecularTextureMap(texture);
        // check correctness
        assertSame(texture, material.getSpecularTextureMap());
        assertTrue(material.isSpecularTextureMapAvailable());
    }

    @Test
    void testGetSetAlphaTextureMap() {
        final var material = new Material();

        final var texture = new Texture("alpha.png", 0);

        // check default value
        assertNull(material.getAlphaTextureMap());
        assertFalse(material.isAlphaTextureMapAvailable());

        // set new value
        material.setAlphaTextureMap(texture);
        // check correctness
        assertSame(texture, material.getAlphaTextureMap());
        assertTrue(material.isAlphaTextureMapAvailable());
    }

    @Test
    void testGetSetBumpTextureMap() {
        final var material = new Material();

        final var texture = new Texture("bump.png", 0);

        // check default value
        assertNull(material.getBumpTextureMap());
        assertFalse(material.isBumpTextureMapAvailable());

        // set new value
        material.setBumpTextureMap(texture);
        // check correctness
        assertSame(texture, material.getBumpTextureMap());
        assertTrue(material.isBumpTextureMapAvailable());
    }

    @Test
    void testGetSetTransparency() {
        final var material = new Material();

        final var randomizer = new UniformRandomizer();

        final var trans = (short) randomizer.nextInt(MIN_COLOR_VALUE, MAX_COLOR_VALUE);

        // check default value
        assertEquals(-1, material.getTransparency());
        assertFalse(material.isTransparencyAvailable());

        // set new value
        material.setTransparency(trans);
        // check correctness
        assertEquals(trans, material.getTransparency());
        assertTrue(material.isTransparencyAvailable());
    }

    @Test
    void testGetSetIllumination() {
        final var material = new Material();

        assertNull(material.getIllumination());
        assertFalse(material.isIlluminationAvailable());

        final var randomizer = new UniformRandomizer();
        final var illumValue = randomizer.nextInt(MIN_ILLUMINATION_VALUE, MAX_ILLUMINATION_VALUE);
        final var illumination = Illumination.forValue(illumValue);

        // set new value
        material.setIllumination(illumination);
        // check correctness
        assertEquals(illumination, material.getIllumination());
        assertTrue(material.isIlluminationAvailable());
    }
}
