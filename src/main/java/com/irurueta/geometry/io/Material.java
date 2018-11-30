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

/**
 * Structure containing parameters defining a material, such as color, texture, 
 * etc.
 */
@SuppressWarnings("WeakerAccess")
public class Material {
    
    /**
     * Id of material. 
     * This value is generated automatically when loading a 3D file and it is
     * intended to uniquely identify a material.
     */
    private int id = 0;
    
    /**
     * Ambient red color component value. If defined, must be between 0 and 255
     * (both included).
     */
    private short ambientRedColor = -1;
    
    /**
     * Ambient green color component value. If defined, must be between 0 and
     * 255 (both included).
     */
    private short ambientGreenColor = -1;
    
    /**
     * Ambient blue color component value. If defined, must be between 0 and 255
     * (both included).
     */
    private short ambientBlueColor = -1;
    
    /**
     * Diffuse red color component value. If defined, must be between 0 and 255
     * (both included).
     */
    private short diffuseRedColor = -1;
    
    /**
     * Diffuse green color component value. If defined, must be between 0 and 
     * 255 (both included).
     */
    private short diffuseGreenColor = -1;
    
    /**
     * Diffuse blue color component value. If defined, must be between 0 and 255
     * (both included).
     */
    private short diffuseBlueColor = -1;
    
    /**
     * Specular red color component value. If defined, must be between 0 and 255
     * (both included).
     */
    private short specularRedColor = -1;
    
    /**
     * Specular green color component value. If defined, must be between 0 and 
     * 255 (both included).
     */
    private short specularGreenColor = -1;
    
    /**
     * Specular blue color component value. If defined, must be between 0 and 
     * 255 (both included).
     */
    private short specularBlueColor = -1;
    
    
    /**
     * Specular coefficient that determines how much shininess the material has.
     */
    private float specularCoefficient = 0.0f;
    
    /**
     * Indicates if specular coefficient is available or not.
     */
    private boolean specularCoefficientAvailable = false;
    
    /**
     * Ambient texture.
     */
    private Texture ambientTextureMap;
    
    /**
     * Diffuse texture.
     */
    private Texture diffuseTextureMap;
    
    /**
     * Specular texture.
     */
    private Texture specularTextureMap;
    
    /**
     * Alpha texture.
     */
    private Texture alphaTextureMap;
    
    /**
     * Bump texture.
     */
    private Texture bumpTextureMap;
    
    /**
     * Transparency value. If defined, must be a value between 0 and 100 (both
     * included).
     */
    private short transparency = -1;
    
    /**
     * Illumination of this material.
     */
    private Illumination illumination;
    
    /**
     * Returns id of material.
     * This value is generated automatically when loading a 3D file and it is 
     * intended to uniquely identify a material
     * @return id of material.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets id of material.
     * This value must be generated when loading a 3D file and it is intended
     * to uniquely identify a material.
     * @param id id of material to be set.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Returns ambient red color component value. If defined, must be between 0
     * and 255 (both included).
     * @return ambient red color component value.
     */
    public short getAmbientRedColor() {
        return ambientRedColor;
    }

    /**
     * Sets ambient red color component value. If defined, must be between 0 and
     * 255 (both included).
     * @param ambientRedColor ambient red color component value to be set.
     */
    public void setAmbientRedColor(short ambientRedColor) {
        this.ambientRedColor = ambientRedColor;
    }        
    
    /**
     * Indicates if ambient red color component value is available.
     * Returns true if ambient red color is not negative.
     * @return true if ambient red color is not negative.
     */
    public boolean isAmbientRedColorAvailable() {
        return ambientRedColor >= 0;
    }
    
    /**
     * Returns ambient green color component value. If defined, must be between 
     * 0 and 255 (both included).
     * @return ambient green color component value.
     */
    public short getAmbientGreenColor() {
        return ambientGreenColor;
    }

    /**
     * Sets ambient green color component value. If defined, must be between 0
     * and 255 (both included).
     * @param ambientGreenColor ambient green color component value to be set.
     */
    public void setAmbientGreenColor(short ambientGreenColor) {
        this.ambientGreenColor = ambientGreenColor;
    }    
    
    /**
     * Indicates if ambient green color component value is available.
     * Returns true if ambient green color is not negative.
     * @return true if ambient green color is not negative.
     */
    public boolean isAmbientGreenColorAvailable() {
        return ambientGreenColor >= 0;
    }
    
    /**
     * Returns ambient blue color component value. If defined, must be between
     * 0 and 255 (both included).
     * @return ambient blue color component value.
     */
    public short getAmbientBlueColor() {
        return ambientBlueColor;
    }

    /**
     * Sets ambient blue color component value. If defined, must be between 0 
     * and 255 (both included).
     * @param ambientBlueColor ambient blue color component value to be set.
     */
    public void setAmbientBlueColor(short ambientBlueColor) {
        this.ambientBlueColor = ambientBlueColor;
    }
    
    /**
     * Indicates if ambient blue color component value is available.
     * Returns true if ambient blue color is not negative.
     * @return true if ambient blue color is not negative.
     */
    public boolean isAmbientBlueColorAvailable() {
        return ambientBlueColor >= 0;
    }
    
    /**
     * Sets all color components of ambient color.
     * Ambient color usually refers to the color of the light that gives a 
     * certain tonality to all the objects of a scene.
     * @param red ambient red color component to be set. If defined, must be
     * between 0 and 255 (both included).
     * @param green ambient green color component to be set. If defined, must be
     * between 0 and 255 (both included).
     * @param blue ambient blue color component to be set. If defined, must be
     * between 0 and 255 (both included).
     */
    public void setAmbientColor(short red, short green, short blue) {
        ambientRedColor = red;
        ambientGreenColor = green;
        ambientBlueColor = blue;
    }
    
    /**
     * Indicates if ambient color has been defined.
     * @return true if all components of ambient color have been defined (are
     * not negative).
     */
    public boolean isAmbientColorAvailable() {
        return ambientRedColor >= 0 && ambientGreenColor >= 0 &&
                ambientBlueColor >= 0;
    }
    
    /**
     * Returns diffuse red color component value. If defined, must be between 0
     * and 255 (both included).
     * @return diffuse red color component value.
     */
    public short getDiffuseRedColor() {
        return diffuseRedColor;
    }

    /**
     * Sets diffuse red color component value. If defined, must be between 0 and
     * 255 (both included).
     * @param diffuseRedColor diffuse red color component value to be set.
     */
    public void setDiffuseRedColor(short diffuseRedColor) {
        this.diffuseRedColor = diffuseRedColor;
    }        
    
    /**
     * Indicates if diffuse red color component value is available.
     * Returns true if diffuse red color is not negative.
     * @return true if diffuse red color is not negative.
     */
    public boolean isDiffuseRedColorAvailable() {
        return diffuseRedColor >= 0;
    }
    
    /**
     * Returns diffuse green color component value. If defined, must be between
     * 0 and 255 (both included).
     * @return diffuse green color component value.
     */
    public short getDiffuseGreenColor() {
        return diffuseGreenColor;
    }

    /**
     * Sets diffuse green color component value. If defined, must be between 0
     * and 255 (both included).
     * @param diffuseGreenColor diffuse green color component value to be set.
     */
    public void setDiffuseGreenColor(short diffuseGreenColor) {
        this.diffuseGreenColor = diffuseGreenColor;
    }    
    
    /**
     * Indicates if diffuse green color component value is available.
     * Returns true if diffuse green color is not negative.
     * @return true if diffuse green color is not negative.
     */
    public boolean isDiffuseGreenColorAvailable() {
        return diffuseGreenColor >= 0;
    }
    
    /**
     * Returns diffuse blue color component value. If defined, must be between
     * 0 and 255 (both included).
     * @return diffuse blue color component value.
     */
    public short getDiffuseBlueColor() {
        return diffuseBlueColor;
    }

    /**
     * Sets diffuse blue color component value. If defined, must be between 0 
     * and 255 (both included).
     * @param diffuseBlueColor diffuse blue color component value to be set.
     */
    public void setDiffuseBlueColor(short diffuseBlueColor) {
        this.diffuseBlueColor = diffuseBlueColor;
    }
    
    /**
     * Indicates if diffuse blue color component value is available.
     * Returns true if diffuse blue color is not negative.
     * @return true if diffuse blue color is not negative.
     */
    public boolean isDiffuseBlueColorAvailable() {
        return diffuseBlueColor >= 0;
    }
    
    /**
     * Sets all color components of diffuse color.
     * Diffuse color usually refers to the surface color of a given object 
     * without accounting for highlights or shininess.
     * @param red diffuse red color component to be set. If defined, must be
     * between 0 and 255 (both included).
     * @param green diffuse green color component to be set. If defined, must be
     * between 0 and 255 (both included).
     * @param blue diffuse blue color component to be set. If defined, must be
     * between 0 and 255 (both included).
     */
    public void setDiffuseColor(short red, short green, short blue) {
        diffuseRedColor = red;
        diffuseGreenColor = green;
        diffuseBlueColor = blue;
    }
    
    /**
     * Indicates if diffuse color has been defined.
     * @return true if all components of diffuse color have been defined (are 
     * not negative).
     */
    public boolean isDiffuseColorAvailable() {
        return diffuseRedColor >= 0 && diffuseGreenColor >= 0 &&
                diffuseBlueColor >= 0;
    }

    /**
     * Returns specular red color component value. If defined, must be between 0
     * and 255 (both included).
     * @return specular red color component value.
     */
    public short getSpecularRedColor() {
        return specularRedColor;
    }

    /**
     * Sets specular red color component value. If defined, must be between 0 
     * and 255 (both included).
     * @param specularRedColor specular red color component value to be set.
     */
    public void setSpecularRedColor(short specularRedColor) {
        this.specularRedColor = specularRedColor;
    }
    
    /**
     * Indicates if specular red color component value is available.
     * Returns true if specular red color is not negative.
     * @return true if specular red color is not negative.
     */
    public boolean isSpecularRedColorAvailable() {
        return specularRedColor >= 0;
    }
    
    /**
     * Returns specular green color component value. If defined, must be between
     * 0 and 255 (both included).
     * @return specular green color component value.
     */
    public short getSpecularGreenColor() {
        return specularGreenColor;
    }

    /**
     * Sets specular green color component value. If defined, must be between 0
     * and 255 (both included).
     * @param specularGreenColor specular green color component value to be set.
     */
    public void setSpecularGreenColor(short specularGreenColor) {
        this.specularGreenColor = specularGreenColor;
    }    
    
    /**
     * Indicates if specular green color component value is available.
     * Returns true if specular green color is not negative.
     * @return true if specular green color is not negative.
     */
    public boolean isSpecularGreenColorAvailable() {
        return specularGreenColor >= 0;
    }
    
    /**
     * Returns specular blue color component value. If defined, must be between
     * 0 and 255 (both included).
     * @return specular blue color component value.
     */
    public short getSpecularBlueColor() {
        return specularBlueColor;
    }

    /**
     * Sets specular blue color component value. If defined, must be between 0
     * and 255 (both included).
     * @param specularBlueColor specular blue color component value to be set.
     */
    public void setSpecularBlueColor(short specularBlueColor) {
        this.specularBlueColor = specularBlueColor;
    }
    
    /**
     * Indicates if specular blue color component value is available.
     * Returns true if specular blue color is not negative.
     * @return true if specular blue color is not negative.
     */
    public boolean isSpecularBlueColorAvailable() {
        return specularBlueColor >= 0;
    }
    
    /**
     * Sets all color components of specular color.
     * Specular color usually refers to the color of the highlights on an object
     * surface when it shines because of the light.
     * @param red specular red color component to be set. If defined, must be
     * between 0 and 255 (both included).
     * @param green specular green color component to be set. If defined, must 
     * be between 0 and 255 (both included).
     * @param blue specular blue color component to be set. If defined, must be
     * between 0 and 255 (both included).
     */
    public void setSpecularColor(short red, short green, short blue) {
        specularRedColor = red;
        specularGreenColor = green;
        specularBlueColor = blue;
    }
    
    /**
     * Indicates if specular color has been defined.
     * @return true if all components of specular color have been defined (are
     * not negative).
     */
    public boolean isSpecularColorAvailable() {
        return specularRedColor >= 0 && specularGreenColor >= 0 &&
                specularBlueColor >= 0;
    }
    
    /**
     * Returns specular coefficient. This indicates the amount of shininess of
     * an object.
     * @return specular coefficient.
     */
    public float getSpecularCoefficient() {
        return specularCoefficient;
    }

    /**
     * Sets specular coefficient. This indicates the amount of shininess of an
     * object.
     * @param specularCoefficient specular coefficient to be set.
     */
    public void setSpecularCoefficient(float specularCoefficient) {
        this.specularCoefficient = specularCoefficient;
        specularCoefficientAvailable = true;
    }            
    
    /**
     * Indicates if specular coefficient has been defined for this material.
     * @return true if specular coefficient has been defined and is available,
     * false otherwise.
     */
    public boolean isSpecularCoefficientAvailable() {
        return specularCoefficientAvailable;
    }

    /**
     * Returns ambient texture.
     * Ambient texture usually refers to the texture used to give a certain 
     * tonality to an object, as if it was lit by some source of light.
     * @return ambient texture.
     */
    public Texture getAmbientTextureMap() {
        return ambientTextureMap;
    }

    /**
     * Sets ambient texture.
     * Ambient texture usually refers to the texture used to give a certain
     * tonality to anb object, as if it was lit by some source of light.
     * @param ambientTextureMap ambient texture to be set.
     */
    public void setAmbientTextureMap(Texture ambientTextureMap) {
        this.ambientTextureMap = ambientTextureMap;        
    }
    
    /**
     * Indicates if ambient texture has been defined and is available.
     * @return true if ambient texture has been defined and is available, false
     * otherwise.
     */
    public boolean isAmbientTextureMapAvailable() {
        return ambientTextureMap != null;
    }

    /**
     * Returns diffuse texture.
     * Diffuse texture usually refers to the texture applied on the surface of
     * an object as its color.
     * @return diffuse texture.
     */
    public Texture getDiffuseTextureMap() {
        return diffuseTextureMap;
    }

    /**
     * Sets diffuse texture.
     * Diffuse texture usually refers to the texture applied on the surface of 
     * an object as its color.
     * @param diffuseTextureMap diffuse texture to be set.
     */
    public void setDiffuseTextureMap(Texture diffuseTextureMap) {
        this.diffuseTextureMap = diffuseTextureMap;
    }
    
    /**
     * Indicates if diffuse texture has been defined and is available.
     * @return true if diffuse texture has been defined and is available, false
     * otherwise.
     */
    public boolean isDiffuseTextureMapAvailable() {
        return diffuseTextureMap != null;
    }
    
    /**
     * Returns specular texture.
     * Specular texture usually refers to the highlights or sparkles applied
     * on the surface of an object.
     * @return specular texture.
     */
    public Texture getSpecularTextureMap() {
        return specularTextureMap;
    }

    /**
     * Sets specular texture.
     * Specular texture usually refers to the highlights or sparkles applied
     * on the surface of an object.
     * @param specularTextureMap specular texture to be set.
     */
    public void setSpecularTextureMap(Texture specularTextureMap) {
        this.specularTextureMap = specularTextureMap;
    }    
    
    /**
     * Indicates if specular texture has been defined and is available
     * @return true if specular texture has been defined and is available, false
     * otherwise.
     */
    public boolean isSpecularTextureMapAvailable() {
        return specularTextureMap != null;
    }

    /**
     * Returns alpha texture.
     * Alpha texture usually contains information related to transparency.
     * @return alpha texture.
     */
    public Texture getAlphaTextureMap() {
        return alphaTextureMap;
    }

    /**
     * Sets alpha texture.
     * Alpha texture usually contains information related to transparency.
     * @param alphaTextureMap alpha texture to be set.
     */
    public void setAlphaTextureMap(Texture alphaTextureMap) {
        this.alphaTextureMap = alphaTextureMap;
    }
    
    /**
     * Indicates if alpha texture has been defined and is available.
     * @return true if alpha texture has been defined and is available, false
     * otherwise.
     */
    public boolean isAlphaTextureMapAvailable() {
        return alphaTextureMap != null;
    }
    
    /**
     * Returns bump texture.
     * Bump textures are used to give the appearance of bumpiness or rugosity to
     * plain surfaces.
     * @return bump texture.
     */
    public Texture getBumpTextureMap() {
        return bumpTextureMap;
    }

    /**
     * Sets bump texture.
     * Bump textures are used to give the appearance of bumpiness or rugosity to
     * plain surfaces.
     * @param bumpTextureMap bump texture.
     */
    public void setBumpTextureMap(Texture bumpTextureMap) {
        this.bumpTextureMap = bumpTextureMap;
    }    
    
    /**
     * Indicates if bump texture has been defined and is available.
     * @return true if bump texture has been defined and is available, false
     * otherwise.
     */
    public boolean isBumpTextureMapAvailable() {
        return bumpTextureMap != null;
    }
    
    /**
     * Returns transparency of this material. If defined, value must be between
     * 0 (no transparency) and 100 (totally transparent).
     * @return transparency of this material.
     */
    public short getTransparency() {
        return transparency;
    }

    /**
     * Sets transparency of this material. If defined, value must be between
     * 0 (no transparency) and 100 (totally transparent).
     * @param transparency transparency of this material to be set.
     */
    public void setTransparency(short transparency) {
        this.transparency = transparency;
    }
    
    /**
     * Indicates if transparency has been defined and is available.
     * @return true if transparency is not negative, false otherwise.
     */
    public boolean isTransparencyAvailable() {
        return transparency >= 0;
    }
    
    /**
     * Returns illumination method to be used for this material.
     * @return illumination.
     */
    public Illumination getIllumination() {
        return illumination;
    }    
    
    /**
     * Sets illumination method to be used for this material.
     * @param illumination illumination method to be used.
     */
    public void setIllumination(Illumination illumination) {
        this.illumination = illumination;
    }
    
    /**
     * Indicates if an illumination has been defined and is available.
     * @return true if illumination has been defined, false otherwise.
     */
    public boolean isIlluminationAvailable() {
        return illumination != null;
    }
}
