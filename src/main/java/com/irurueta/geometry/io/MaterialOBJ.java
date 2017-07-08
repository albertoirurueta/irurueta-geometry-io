/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MaterialOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 12, 2012
 */
package com.irurueta.geometry.io;

/**
 * Implementation of a material for an OBJ file
 */
public class MaterialOBJ extends Material{
    
    /**
     * Material name
     */
    private String materialName;
    
    public MaterialOBJ(String materialName){
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
     * @param materialName material name
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }    
    
    /**
     * Indicates if material name is available
     * @return true if material name is available, false otherwise
     */
    public boolean isMaterialNameAvailable(){
        return materialName != null;
    }
}
