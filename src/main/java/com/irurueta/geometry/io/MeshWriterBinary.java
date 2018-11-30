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

import java.io.*;

/**
 * Reads a 3D object and converts it into custom binary format.
 */
@SuppressWarnings("WeakerAccess")
public class MeshWriterBinary extends MeshWriter {
    
    /**
     * Version of the binary file supported by this class.
     */
    public static final byte VERSION = 2;
    
    /**
     * Buffer size to write data into output stream.
     */
    public static final int BUFFER_SIZE = 1024;
    
    /**
     * Stream to write binary data to output.
     */
    private DataOutputStream dataStream;
    
    /**
     * Constructor.
     * @param loader loader to load a 3D file.
     * @param stream stream where transcoded data will be written to.
     */
    public MeshWriterBinary(Loader loader, OutputStream stream) {
        super(loader, stream);
    }
    
    /**
     * Constructor.
     * @param loader loader to load a 3D file.
     * @param stream stream where transcoded data will be written to.
     * @param listener listener to be notified of progress changes or when
     * transcoding process starts or finishes.
     */
    public MeshWriterBinary(Loader loader, OutputStream stream, 
            MeshWriterListener listener) {
        super(loader, stream, listener);
    }

    /**
     * Processes input file provided to loader and writes it transcoded into
     * output stream.
     * @throws LoaderException if 3D file loading fails.
     * @throws IOException if an I/O error occurs.
     * @throws NotReadyException if mesh writer is not ready because either a
     * loader has not been provided or an output stream has not been provided.
     * @throws LockedException if this mesh writer is locked processing a file.
     */
    @Override
    @SuppressWarnings("all")
    public void write() throws LoaderException, IOException, NotReadyException, 
            LockedException {
        
        if (!isReady()) {
            throw new NotReadyException();
        }
        if (isLocked()) {
            throw new LockedException();
        }
        
        try {
            dataStream = new DataOutputStream(new BufferedOutputStream(stream));
            
            locked = true;
            if (listener != null) {
                listener.onWriteStart(this);
            }
            
            try {
                loader.setListener(this.internalListeners);
            } catch (LockedException ignore) { }

            //write version
            dataStream.writeByte(VERSION);
            
            LoaderIterator iter = loader.load();
            
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, 
                    minZ = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, 
                    maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
            
            while (iter.hasNext()) {
                DataChunk chunk = iter.next();
                if (listener != null) {
                    listener.onChunkAvailable(this, chunk);
                }
                
                float[] coords = chunk.getVerticesCoordinatesData();
                short[] colors = chunk.getColorData();
                int[] indices = chunk.getIndicesData();
                float[] textureCoords = chunk.getTextureCoordiantesData();
                float[] normals = chunk.getNormalsData();
                int colorComponents = chunk.getColorComponents();
                
                boolean coordsAvailable = (coords != null);
                boolean colorsAvailable = (colors != null);
                boolean indicesAvailable = (indices != null);
                boolean textureCoordsAvailable = (textureCoords != null);
                boolean normalsAvailable = (normals != null);
                        
                if (chunk.getMinX() < minX) {
                    minX = chunk.getMinX();
                }
                if (chunk.getMinY() < minY) {
                    minY = chunk.getMinY();
                }
                if (chunk.getMinZ() < minZ) {
                    minZ = chunk.getMinZ();
                }
                
                if (chunk.getMaxX() > maxX) {
                    maxX = chunk.getMaxX();
                }
                if (chunk.getMaxY() > maxY) {
                    maxY = chunk.getMaxY();
                }
                if (chunk.getMaxZ() > maxZ) {
                    maxZ = chunk.getMaxZ();
                }
                
                //compute size of material in bytes
                Material material = chunk.getMaterial();
                int materialSizeInBytes = 1; //boolean indicating availability 
                                            //of material
                if (material != null) {
                    //material id (int)
                    materialSizeInBytes += Integer.SIZE / 8;
                    //ambient color (RGB) -> 3 bytes
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isAmbientColorAvailable()) {
                        materialSizeInBytes += 3; //RGB components
                    }
                    //diffuse color
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isDiffuseColorAvailable()) {
                        materialSizeInBytes += 3; //RGB components
                    }                    
                    //specular color
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isSpecularColorAvailable()) {
                        materialSizeInBytes += 3; //RGB components
                    }                    
                    //specular coefficient (float)
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isSpecularCoefficientAvailable()) {
                        materialSizeInBytes += Float.SIZE / 8;
                    }       
                    
                    //ambient texture map
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isAmbientTextureMapAvailable()) {
                        //id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }
                    
                    //diffuse texture map
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isDiffuseTextureMapAvailable()) {
                        //id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;                        
                    }
                    
                    //specular texture map
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isSpecularTextureMapAvailable()) {
                        //id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;                        
                    }
                    
                    //alpha texture map
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isAlphaTextureMapAvailable()) {
                        //id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;                        
                    }
                    
                    //bump texture map
                    materialSizeInBytes += 1; //boolean indicating availability
                    if (material.isBumpTextureMapAvailable()) {
                        //id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        //texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;                        
                    }
                    
                    //transparency
                    materialSizeInBytes += 1; //availability
                    if (material.isTransparencyAvailable()) {
                        materialSizeInBytes += 1;   //one byte 0-255 of 
                                                    //transparency
                    }
                    
                    //enum of illumination(int)
                    materialSizeInBytes += 1; //boolean containing availability
                    if (material.isIlluminationAvailable()) {
                        materialSizeInBytes += Integer.SIZE / 8;
                    }
                }
                
                //compute size of chunk data in bytes
                int coordsSizeInBytes = 0, colorsSizeInBytes = 0, 
                        indicesSizeInBytes = 0, textureCoordsSizeInBytes = 0,
                        normalsSizeInBytes = 0;
                
                if(coordsAvailable) 
                    coordsSizeInBytes = coords.length * Float.SIZE / 8;
                if(colorsAvailable)
                    colorsSizeInBytes = colors.length;
                if(indicesAvailable)
                    indicesSizeInBytes = indices.length * Short.SIZE / 8;
                if(textureCoordsAvailable)
                    textureCoordsSizeInBytes = textureCoords.length * 
                            Float.SIZE / 8;
                if(normalsAvailable)
                    normalsSizeInBytes = normals.length * Float.SIZE / 8;
                
                int chunkSize = materialSizeInBytes + coordsSizeInBytes + 
                        colorsSizeInBytes + indicesSizeInBytes + 
                        textureCoordsSizeInBytes + normalsSizeInBytes + 
                        + (5 * Integer.SIZE / 8) + //sizes
                        (6 * Float.SIZE / 8); //min/max values
                if (colorsAvailable) {
                    chunkSize += Integer.SIZE / 8; //bytes for number of color components
                }
                
                //indicate that no more textures follow                
                if (!ignoreTextureValidation) {
                    //byte below is written only once after all textures and
                    //before all vertex data
                    dataStream.writeBoolean(false);
                    ignoreTextureValidation = true; //so that no more textures are 
                                                //written into stream
                }
                
                //write total chunk size
                dataStream.writeInt(chunkSize);
                
                //indicate material availability
                dataStream.writeBoolean(material != null);
                if (material != null) {
                    //material id
                    dataStream.writeInt(material.getId());
                    
                    //ambient color
                    dataStream.writeBoolean(material.isAmbientColorAvailable());
                    if (material.isAmbientColorAvailable()) {
                        byte b = (byte)(material.getAmbientRedColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getAmbientGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getAmbientBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }
                    
                    //diffuse color
                    dataStream.writeBoolean(material.isDiffuseColorAvailable());
                    if (material.isDiffuseColorAvailable()) {
                        byte b = (byte)(material.getDiffuseRedColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getDiffuseGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getDiffuseBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }
                    
                    //specular color
                    dataStream.writeBoolean(material.
                            isSpecularColorAvailable());
                    if (material.isSpecularColorAvailable()) {
                        byte b = (byte)(material.getSpecularRedColor() & 
                                0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getSpecularGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte)(material.getSpecularBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }
                    
                    //specular coefficient (float)
                    dataStream.writeBoolean(
                            material.isSpecularCoefficientAvailable());
                    if (material.isSpecularCoefficientAvailable()) {
                        dataStream.writeFloat(
                                material.getSpecularCoefficient());
                    }
                    
                    //ambient texture map
                    dataStream.writeBoolean(
                            material.isAmbientTextureMapAvailable());
                    if (material.isAmbientTextureMapAvailable()) {
                        Texture tex = material.getAmbientTextureMap();
                        //texture id
                        dataStream.writeInt(tex.getId());
                        //texture width
                        dataStream.writeInt(tex.getWidth());
                        //texture height
                        dataStream.writeInt(tex.getHeight());
                    }
                    
                    //diffuse texture map
                    dataStream.writeBoolean(
                            material.isDiffuseTextureMapAvailable());
                    if (material.isDiffuseTextureMapAvailable()) {
                        Texture tex = material.getDiffuseTextureMap();
                        //texture id
                        dataStream.writeInt(tex.getId());
                        //texture width
                        dataStream.writeInt(tex.getWidth());
                        //texture height
                        dataStream.writeInt(tex.getHeight());                        
                    }
                    
                    //specular texture map
                    dataStream.writeBoolean(
                            material.isSpecularTextureMapAvailable());
                    if (material.isSpecularTextureMapAvailable()) {
                        Texture tex = material.getSpecularTextureMap();
                        //texture id
                        dataStream.writeInt(tex.getId());
                        //texture width
                        dataStream.writeInt(tex.getWidth());
                        //texture height
                        dataStream.writeInt(tex.getHeight());
                    }
                    
                    //alpha texture map
                    dataStream.writeBoolean(
                            material.isAlphaTextureMapAvailable());
                    if (material.isAlphaTextureMapAvailable()) {
                        Texture tex = material.getAlphaTextureMap();
                        //texture id
                        dataStream.writeInt(tex.getId());
                        //texture width
                        dataStream.writeInt(tex.getWidth());
                        //texture height
                        dataStream.writeInt(tex.getHeight());
                    }
                    
                    //bump texture map
                    dataStream.writeBoolean(
                            material.isBumpTextureMapAvailable());
                    if (material.isBumpTextureMapAvailable()) {
                        Texture tex = material.getBumpTextureMap();
                        //texture id
                        dataStream.writeInt(tex.getId());
                        //texture width
                        dataStream.writeInt(tex.getWidth());
                        //texture height
                        dataStream.writeInt(tex.getHeight());
                    }
                    
                    dataStream.writeBoolean(material.isTransparencyAvailable());
                    if (material.isTransparencyAvailable()) {
                        byte b = (byte)(material.getTransparency() & 0x00ff);
                        dataStream.writeByte(b);
                    }
                    
                    dataStream.writeBoolean(material.isIlluminationAvailable());
                    if (material.isIlluminationAvailable()) {
                        dataStream.writeInt(material.getIllumination().value());
                    }
                }
                
                
                //write coords size
                dataStream.writeInt(coordsSizeInBytes);                    
                //write coords
                if (coordsAvailable) {
                    for (float coord : coords) {
                        dataStream.writeFloat(coord);
                    }
                }

                //write colors size
                dataStream.writeInt(colorsSizeInBytes);                    
                //write colors                
                if (colorsAvailable) {
                    int i = 0;
                    while (i < colors.length) {
                        byte b = (byte)(colors[i] & 0x00ff);
                        dataStream.writeByte(b);
                        i++;
                    }
                    dataStream.writeInt(colorComponents);
                }

                //write indices size
                dataStream.writeInt(indicesSizeInBytes);
                //write indices                
                if (indicesAvailable) {
                    for (int index : indices) {
                        short s = (short) (index & 0x0000ffff);
                        dataStream.writeShort(s);
                    }
                }

                //write texture coords
                dataStream.writeInt(textureCoordsSizeInBytes);
                //write texture coords                
                if (textureCoordsAvailable) {
                    for (float textureCoord : textureCoords) {
                        dataStream.writeFloat(textureCoord);
                    }
                }                

                //write normals
                dataStream.writeInt(normalsSizeInBytes);
                //write normals                
                if (normalsAvailable) {
                    for (float normal : normals) {
                        dataStream.writeFloat(normal);
                    }
                }                
                
                //write min/max x, y, z
                dataStream.writeFloat(chunk.getMinX());
                dataStream.writeFloat(chunk.getMinY());
                dataStream.writeFloat(chunk.getMinZ());
                
                dataStream.writeFloat(chunk.getMaxX());
                dataStream.writeFloat(chunk.getMaxY());
                dataStream.writeFloat(chunk.getMaxZ());

                dataStream.flush();
                
                //to avoid out of memory errors we attempt to force garbage
                //collection if possible
                System.gc();
            }
            
            if (listener != null) {
                listener.onWriteEnd(this);
            }
            locked = false;
            
        } catch (LoaderException | IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new LoaderException(e);
        }
    }    

    /**
     * Processes texture file. By reading provided texture file that has been 
     * created in a temporal location and embedding it into resulting output
     * stream.
     * @param texture reference to texture that uses texture image.
     * @param textureFile file containing texture image. File will usually be
     * created in a temporal location.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void processTextureFile(Texture texture, File textureFile) 
            throws IOException {
        if (!textureFile.exists()) {
            return;
        }
                
        //write boolean to true indicating that a texture follows
        dataStream.writeBoolean(true);
        
        //write int containing texture id
        int textureId = texture.getId();
        dataStream.writeInt(textureId);
        
        //write int containing image width
        int width = texture.getWidth();
        dataStream.writeInt(width);
        
        //write int containing image height
        int height = texture.getHeight();
        dataStream.writeInt(height);
        
        //write length of texture file in bytes
        long length = textureFile.length();
        dataStream.writeLong(length);
        
        //write file data
        try (InputStream textureStream = new FileInputStream(textureFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = textureStream.read(buffer)) > 0) {
                dataStream.write(buffer, 0, n);
            }
            dataStream.flush();
        }
    }
}
