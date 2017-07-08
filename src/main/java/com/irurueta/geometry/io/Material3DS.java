/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.Material3DS
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 4, 2012
 */
package com.irurueta.geometry.io;

/**
 * Defines a material in a 3D Studio Max file
 */
public class Material3DS extends Material{
    
    /**
     * Material name
     */
    private String materialName;
    
    /**
     * Constructor
     */
    public Material3DS(){
        super();
        materialName = null;
    }
    
    /**
     * Constructor
     * @param materialName material name
     */
    public Material3DS(String materialName){
        super();
        this.materialName = materialName;
    }
    
    /**
     * Returns material name
     * @return material name
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Sets material name
     * @param materialName material name to be set
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }    
    
    /**
     * Indicates if material name has been set and is available
     * @return true if available, false otherwise
     */
    public boolean isMaterialNameAvailable(){
        return materialName != null;
    }
}
