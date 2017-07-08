/*
 * @file
 * This file contains unit tests for
 * com.irurueta.geometry.io.Material
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 13, 2012
 */
package com.irurueta.geometry.io;

import com.irurueta.statistics.UniformRandomizer;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MaterialTest {    
    
    public static final int MIN_COLOR_VALUE = 0;
    public static final int MAX_COLOR_VALUE = 255;
    
    public static final float MIN_SPECULAR_COEFFICIENT = 0.0f;
    public static final float MAX_SPECULAR_COEFFICIENT = 1000.0f;
    
    public static final int MIN_ILLUMINATION_VALUE = 0;
    public static final int MAX_ILLUMINATION_VALUE = 10;
    
    public MaterialTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testConstructor(){
        Material material = new Material();
        assertNotNull(material);
    }
    
    @Test
    public void testGetSetId(){
        Material material = new Material();
        
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
    public void testGetSetAmbientRedColor(){
        Material material = new Material();
        
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
    public void testGetSetAmbientGreenColor(){
        Material material = new Material();
        
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
    public void testGetSetAmbientBlueColor(){
        Material material = new Material();
        
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
    public void testSetAmbientColor(){
        Material material = new Material();
        
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
    public void testGetSetDiffuseRedColor(){
        Material material = new Material();
        
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
    public void testGetSetDiffuseGreenColor(){
        Material material = new Material();
        
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
    public void testGetSetDiffuseBlueColor(){
        Material material = new Material();
        
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
    public void testSetDiffuseColor(){
        Material material = new Material();
        
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
    public void testGetSetSpecularRedColor(){
        Material material = new Material();
        
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
    public void testGetSetSpecularGreenColor(){
        Material material = new Material();
        
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
    public void testGetSetSpecularBlueColor(){
        Material material = new Material();
        
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
    public void testSetSpecularColor(){
        Material material = new Material();
        
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
    public void testGetSetSpecularCoefficient(){
        Material material = new Material();
        
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
    public void testGetSetAmbientTextureMap(){
        Material material = new Material();
        
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
    public void testGetSetDiffuseTextureMap(){
        Material material = new Material();
        
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
    public void testGetSetSpecularTextureMap(){
        Material material = new Material();
        
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
    public void testGetSetAlphaTextureMap(){
        Material material = new Material();
        
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
    public void testGetSetBumpTextureMap(){
        Material material = new Material();
        
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
    public void testGetSetTransparency(){
        Material material = new Material();
        
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
    public void testGetSetIllumination(){
        Material material = new Material();
        
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
}
