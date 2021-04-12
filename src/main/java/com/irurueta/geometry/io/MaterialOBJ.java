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
 * Implementation of a material for an OBJ file.
 */
public class MaterialOBJ extends Material {

    /**
     * Material name.
     */
    private String materialName;

    public MaterialOBJ(final String materialName) {
        super();
        this.materialName = materialName;
    }

    /**
     * Returns material name.
     *
     * @return material name.
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Sets material name.
     *
     * @param materialName material name.
     */
    public void setMaterialName(final String materialName) {
        this.materialName = materialName;
    }

    /**
     * Indicates if material name is available.
     *
     * @return true if material name is available, false otherwise.
     */
    public boolean isMaterialNameAvailable() {
        return materialName != null;
    }
}
