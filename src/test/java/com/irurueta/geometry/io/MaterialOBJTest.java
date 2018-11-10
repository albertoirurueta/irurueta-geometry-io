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
import org.junit.*;

import java.util.Random;

import static org.junit.Assert.*;

public class MaterialOBJTest {
    
    private static final int MIN_COLOR_VALUE = 0;
    private static final int MAX_COLOR_VALUE = 255;
    
    private static final float MIN_SPECULAR_COEFFICIENT = 0.0f;
    private static final float MAX_SPECULAR_COEFFICIENT = 1000.0f;
    
    private static final int MIN_ILLUMINATION_VALUE = 0;
    private static final int MAX_ILLUMINATION_VALUE = 10;
        
    public MaterialOBJTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testConstructor() {
        String materialName = "materialName";
        MaterialOBJ material = new MaterialOBJ(materialName);
        assertNotNull(material);
        assertEquals(material.getMaterialName(), materialName);
    }
    
    @Test
    public void testGetSetId() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        int id = randomizer.nextInt();
        
        //check default value
        assertEquals(material.getId(), 0);
        
        //set new value
        material.setId(id);        
        //check correctness
        assertEquals(material.getId(), id);
    }
    
    @Test
    public void testGetSetAmbientRedColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getAmbientRedColor(), -1);
        assertFalse(material.isAmbientRedColorAvailable());
        
        //set new value
        material.setAmbientRedColor(color);
        //check correctness
        assertEquals(material.getAmbientRedColor(), color);
        assertTrue(material.isAmbientRedColorAvailable());
    }
    
    @Test
    public void testGetSetAmbientGreenColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getAmbientGreenColor(), -1);
        assertFalse(material.isAmbientGreenColorAvailable());
        
        //set new value
        material.setAmbientGreenColor(color);
        //check correctness
        assertEquals(material.getAmbientGreenColor(), color);
        assertTrue(material.isAmbientGreenColorAvailable());
    }
    
    @Test
    public void testGetSetAmbientBlueColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getAmbientBlueColor(), -1);
        assertFalse(material.isAmbientBlueColorAvailable());
        
        //set new value
        material.setAmbientBlueColor(color);
        //check correctness
        assertEquals(material.getAmbientBlueColor(), color);
        assertTrue(material.isAmbientBlueColorAvailable());
    }
    
    @Test
    public void testSetAmbientColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short red = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short green = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short blue = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getAmbientRedColor(), -1);
        assertFalse(material.isAmbientRedColorAvailable());
        assertEquals(material.getAmbientGreenColor(), -1);
        assertFalse(material.isAmbientGreenColorAvailable());
        assertEquals(material.getAmbientBlueColor(), -1);
        assertFalse(material.isAmbientBlueColorAvailable());
        
        assertFalse(material.isAmbientColorAvailable());
        
        //set new value
        material.setAmbientColor(red, green, blue);
        
        //check correctness
        assertEquals(material.getAmbientRedColor(), red);
        assertTrue(material.isAmbientRedColorAvailable());
        assertEquals(material.getAmbientGreenColor(), green);
        assertTrue(material.isAmbientGreenColorAvailable());
        assertEquals(material.getAmbientBlueColor(), blue);
        assertTrue(material.isAmbientBlueColorAvailable());
        
        assertTrue(material.isAmbientColorAvailable());
    }
    
    @Test
    public void testGetSetDiffuseRedColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getDiffuseRedColor(), -1);
        assertFalse(material.isDiffuseRedColorAvailable());
        
        //set new value
        material.setDiffuseRedColor(color);
        //check correctness
        assertEquals(material.getDiffuseRedColor(), color);
        assertTrue(material.isDiffuseRedColorAvailable());
    }
    
    @Test
    public void testGetSetDiffuseGreenColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getDiffuseGreenColor(), -1);
        assertFalse(material.isDiffuseGreenColorAvailable());
        
        //set new value
        material.setDiffuseGreenColor(color);
        //check correctness
        assertEquals(material.getDiffuseGreenColor(), color);
        assertTrue(material.isDiffuseGreenColorAvailable());
    }
    
    @Test
    public void testGetSetDiffuseBlueColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getDiffuseBlueColor(), -1);
        assertFalse(material.isDiffuseBlueColorAvailable());
        
        //set new value
        material.setDiffuseBlueColor(color);
        //check correctness
        assertEquals(material.getDiffuseBlueColor(), color);
        assertTrue(material.isDiffuseBlueColorAvailable());
    }
    
    @Test
    public void testSetDiffuseColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short red = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short green = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short blue = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getDiffuseRedColor(), -1);
        assertFalse(material.isDiffuseRedColorAvailable());
        assertEquals(material.getDiffuseGreenColor(), -1);
        assertFalse(material.isDiffuseGreenColorAvailable());
        assertEquals(material.getDiffuseBlueColor(), -1);
        assertFalse(material.isDiffuseBlueColorAvailable());
        
        assertFalse(material.isDiffuseColorAvailable());
        
        //set new value
        material.setDiffuseColor(red, green, blue);
        
        //check correctness
        assertEquals(material.getDiffuseRedColor(), red);
        assertTrue(material.isDiffuseRedColorAvailable());
        assertEquals(material.getDiffuseGreenColor(), green);
        assertTrue(material.isDiffuseGreenColorAvailable());
        assertEquals(material.getDiffuseBlueColor(), blue); 
        assertTrue(material.isDiffuseBlueColorAvailable());
        
        assertTrue(material.isDiffuseColorAvailable());
    }
    
    @Test
    public void testGetSetSpecularRedColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getSpecularRedColor(), -1);
        assertFalse(material.isSpecularRedColorAvailable());
        
        //set new value
        material.setSpecularRedColor(color);
        //check correctness
        assertEquals(material.getSpecularRedColor(), color);
        assertTrue(material.isSpecularRedColorAvailable());
    }
    
    @Test
    public void testGetSetSpecularGreenColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getSpecularGreenColor(), -1);
        assertFalse(material.isSpecularGreenColorAvailable());
        
        //set new value
        material.setSpecularGreenColor(color);
        //check correctness
        assertEquals(material.getSpecularGreenColor(), color);
        assertTrue(material.isSpecularGreenColorAvailable());
    }
    
    @Test
    public void testGetSetSpecularBlueColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short color = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getSpecularBlueColor(), -1);
        assertFalse(material.isSpecularBlueColorAvailable());
        
        //set new value
        material.setSpecularBlueColor(color);
        //check correctness
        assertEquals(material.getSpecularBlueColor(), color);
        assertTrue(material.isSpecularBlueColorAvailable());
    }
    
    @Test
    public void testSetSpecularColor() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short red = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short green = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        short blue = (short)randomizer.nextInt(MIN_COLOR_VALUE,
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getSpecularRedColor(), -1);
        assertFalse(material.isSpecularRedColorAvailable());
        assertEquals(material.getSpecularGreenColor(), -1);
        assertFalse(material.isSpecularGreenColorAvailable());
        assertEquals(material.getSpecularBlueColor(), -1);
        assertFalse(material.isSpecularBlueColorAvailable());
        
        assertFalse(material.isSpecularColorAvailable());
        
        //set new value
        material.setSpecularColor(red, green, blue);
        
        //check correctness
        assertEquals(material.getSpecularRedColor(), red);
        assertTrue(material.isSpecularRedColorAvailable());
        assertEquals(material.getSpecularGreenColor(), green);
        assertTrue(material.isSpecularGreenColorAvailable());
        assertEquals(material.getSpecularBlueColor(), blue);
        assertTrue(material.isSpecularBlueColorAvailable());
        
        assertTrue(material.isSpecularColorAvailable());
    }
    
    @Test
    public void testGetSetSpecularCoefficient() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        float coef = randomizer.nextFloat(MIN_SPECULAR_COEFFICIENT, 
                MAX_SPECULAR_COEFFICIENT);
        
        //check default value
        assertEquals(material.getSpecularCoefficient(), 0.0f, 0.0);
        assertFalse(material.isSpecularCoefficientAvailable());
        
        //set new value
        material.setSpecularCoefficient(coef);
        //check correctness
        assertEquals(material.getSpecularCoefficient(), coef, 0.0);
        assertTrue(material.isSpecularCoefficientAvailable());
    }
    
    @Test
    public void testGetSetAmbientTextureMap() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        Texture texture = new Texture("ambient.png", 0);
        
        //check default value
        assertNull(material.getAmbientTextureMap());
        assertFalse(material.isAmbientTextureMapAvailable());
        
        //set new value
        material.setAmbientTextureMap(texture);
        //check correctness
        assertSame(material.getAmbientTextureMap(), texture);
        assertTrue(material.isAmbientTextureMapAvailable());
    }
    
    @Test
    public void testGetSetDiffuseTextureMap() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        Texture texture = new Texture("diffuse.png", 0);
        
        //check default value
        assertNull(material.getDiffuseTextureMap());
        assertFalse(material.isDiffuseTextureMapAvailable());
        
        //set new value
        material.setDiffuseTextureMap(texture);
        //check correctness
        assertSame(material.getDiffuseTextureMap(), texture);
        assertTrue(material.isDiffuseTextureMapAvailable());
    }
    
    @Test
    public void testGetSetSpecularTextureMap() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        Texture texture = new Texture("specular.png", 0);
        
        //check default value
        assertNull(material.getSpecularTextureMap());
        assertFalse(material.isSpecularTextureMapAvailable());
        
        //set new value
        material.setSpecularTextureMap(texture);
        //check correctness
        assertSame(material.getSpecularTextureMap(), texture);
        assertTrue(material.isSpecularTextureMapAvailable());
    }
    
    @Test
    public void testGetSetAlphaTextureMap() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        Texture texture = new Texture("alpha.png", 0);
        
        //check default value
        assertNull(material.getAlphaTextureMap());
        assertFalse(material.isAlphaTextureMapAvailable());
        
        //set new value
        material.setAlphaTextureMap(texture);
        //check correctness
        assertSame(material.getAlphaTextureMap(), texture);
        assertTrue(material.isAlphaTextureMapAvailable());
    }
    
    @Test
    public void testGetSetBumpTextureMap() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        Texture texture = new Texture("bump.png", 0);
        
        //check default value
        assertNull(material.getBumpTextureMap());
        assertFalse(material.isBumpTextureMapAvailable());
        
        //set new value
        material.setBumpTextureMap(texture);
        //check correctness
        assertSame(material.getBumpTextureMap(), texture);
        assertTrue(material.isBumpTextureMapAvailable());
    }
    
    @Test
    public void testGetSetTransparency() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        
        short trans = (short)randomizer.nextInt(MIN_COLOR_VALUE, 
                MAX_COLOR_VALUE);
        
        //check default value
        assertEquals(material.getTransparency(), -1);
        assertFalse(material.isTransparencyAvailable());
        
        //set new value
        material.setTransparency(trans);
        //check correctness
        assertEquals(material.getTransparency(), trans);        
        assertTrue(material.isTransparencyAvailable());
    }
    
    @Test
    public void testGetSetIllumination() {
        MaterialOBJ material = new MaterialOBJ("materialName");
        
        assertNull(material.getIllumination());
        assertFalse(material.isIlluminationAvailable());
        
        UniformRandomizer randomizer = new UniformRandomizer(new Random());
        int illumValue = randomizer.nextInt(MIN_ILLUMINATION_VALUE, 
                MAX_ILLUMINATION_VALUE);
        Illumination illumination = Illumination.forValue(illumValue);
        
        //set new value        
        material.setIllumination(illumination);
        //check correctness
        assertEquals(material.getIllumination(), illumination);
        assertTrue(material.isIlluminationAvailable());
    }    
    
    @Test
    public void testGetSetMaterialName() {
        String materialName = "materialName";
        MaterialOBJ material = new MaterialOBJ(materialName);
        
        assertEquals(material.getMaterialName(), materialName);
        assertTrue(material.isMaterialNameAvailable());
        
        material.setMaterialName(null);
        assertNull(material.getMaterialName());
        assertFalse(material.isMaterialNameAvailable());        
    }
}
