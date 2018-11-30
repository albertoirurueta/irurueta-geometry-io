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

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.charset.Charset;

@SuppressWarnings("WeakerAccess")
public class MeshWriterJson extends MeshWriter {
    
    /**
     * Indicates if textures must be embedded into resulting file.
     */
    public static final boolean DEFAULT_EMBED_TEXTURES = true;
    
    /**
     * Indicates if by default a URL indicating where the texture can be located
     * should be included into resulting file.
     */
    public static final boolean DEFAULT_USE_REMOTE_TEXTURE_URL = false;
    
    /**
     * Indicates if by default an identifier for the texture should be included 
     * into resulting file so that texture image can be fetched by some other 
     * mean.
     */
    public static final boolean DEFAULT_USE_REMOTE_TEXTURE_ID = false;
    
    /**
     * Indicates charset to use in resulting JSON file. By default this will be
     * UTF-8.
     */
    private Charset charset;
    
    /**
     * Writer to write resulting JSON into output stream.
     */
    private BufferedWriter writer;
    
    /**
     * Counter for the number of textures that have been read.
     */
    private int textureCounter;
    
    /**
     * Indicates if textures will be embedded into resulting JSON stream of
     * data. When embedding textures their stream of bytes is written using 
     * BASE64 to the output stream.
     */
    private boolean embedTexturesEnabled;
    
    /**
     * Indicates if a URL indicating where the texture can be located should
     * be included into resulting file.
     */
    private boolean remoteTextureUrlEnabled;
    
    /**
     * Indicates if an identifier for the texture should be included into 
     * resulting file so that texture image can be fetched by some other mean.
     */
    private boolean remoteTextureIdEnabled;
    
    /**
     * Constructor.
     * @param loader loader to load a 3D file.
     * @param stream stream where transcoded data will be written to.
     */
    public MeshWriterJson(Loader loader, OutputStream stream) {
        super(loader, stream);
        this.charset = null;
        embedTexturesEnabled = DEFAULT_EMBED_TEXTURES;
        remoteTextureUrlEnabled = DEFAULT_USE_REMOTE_TEXTURE_URL;
        remoteTextureIdEnabled = DEFAULT_USE_REMOTE_TEXTURE_ID;        
    }
    
    /**
     * Constructor.
     * @param loader loader to load a 3D file.
     * @param stream stream where transcoded data will be written to.
     * @param listener listener to be notified of progress changes or when
     * transcoding process starts or finishes.
     */
    public MeshWriterJson(Loader loader, OutputStream stream,
            MeshWriterListener listener) {
        super(loader, stream, listener);
        this.charset = null;
        embedTexturesEnabled = DEFAULT_EMBED_TEXTURES;
        remoteTextureUrlEnabled = DEFAULT_USE_REMOTE_TEXTURE_URL;
        remoteTextureIdEnabled = DEFAULT_USE_REMOTE_TEXTURE_ID;                
    }
    
    /**
     * Returns charset to use in resulting JSON file. By default this will be
     * UTF-8.
     * @return charset to use in resulting JSON file..
     */
    public Charset getCharset() {
        return charset;
    }
    
    /**
     * Sets charset to use in resulting JSON file. By default this will be UTF-8.
     * @param charset charset to use in resulting JSON file.
     * @throws LockedException if this mesh writer is locked processing a file.
     */
    public void setCharset(Charset charset) throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.charset = charset;
    }
    
    /**
     * Indicates if default charset will be used or not.
     * @return true if default charset (UTF-8) will be used, false otherwise.
     */
    public boolean isDefaultCharsetBeingUsed() {
        return (charset == null);
    }

    /**
     * Indicates if textures are embedded into resulting JSON or not.
     * When embedding textures their stream of bytes is written using BASE64 to 
     * the output stream.
     * @return true if textures are embedded into resulting JSON, false 
     * otherwise.
     */
    public boolean isEmbedTexturesEnabled() {
        return embedTexturesEnabled;
    }
    
    /**
     * Specified whether textures are embedded into resulting JSON or not.
     * When embedding textures their stream of bytes is written using BASE64 to
     * the output stream.
     * @param embedTexturesEnabled true if textures will be embedded into 
     * resulting JSON, false.
     * @throws LockedException if this mesh writer is locked processing a file.
     */
    public void setEmbedTexturedEnabled(boolean embedTexturesEnabled)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.embedTexturesEnabled = embedTexturesEnabled;
    }
    
    /**
     * Indicates if a URL indicating where the texture can be located should
     * be included into resulting file.
     * @return true if a URL indicating where the texture can be located will
     * be included into resulting file, false otherwise.
     */
    public boolean isRemoteTextureUrlEnabled() {
        return remoteTextureUrlEnabled;
    }
    
    /**
     * Specifies whether a URL indicating where the texture can be located 
     * should be included into resulting file.
     * @param remoteTextureUrlEnabled true if a URL indicating where the texture
     * can be located will be included into resulting file, false otherwise.
     * @throws LockedException if this mesh writer is locked processing a file.
     */
    public void setRemoteTextureUrlEnabled(boolean remoteTextureUrlEnabled)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.remoteTextureUrlEnabled = remoteTextureUrlEnabled;
    }
    
    /**
     * Indicates if an identifier for the texture should be included into 
     * resulting file so that texture image can be fetched by some other mean.
     * @return true if an identifier for the texture should be included into.
     * resulting file so that texture image can be fetched by some other mean.
     */
    public boolean isRemoteTextureIdEnabled() {
        return remoteTextureIdEnabled;
    }
    
    /**
     * Specifies whether an identifier for the texture should be included into
     * resulting file so that texture image can be fetched by some other mean.
     * @param remoteTextureIdEnabled true if identifier for the texture should
     * be included into resulting file.
     * @throws LockedException if this mesh writer is locked processing  file.
     */
    public void setRemoteTextureIdEnabled(boolean remoteTextureIdEnabled)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.remoteTextureIdEnabled = remoteTextureIdEnabled;
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
    @SuppressWarnings("Duplicates")
    public void write() throws LoaderException, IOException, NotReadyException, 
            LockedException {
        
        if (!isReady()) {
            throw new NotReadyException();
        }
        if (isLocked()) {
            throw new LockedException();
        }
        
        try {
            if (charset == null) {
                //use default charset
                writer = new BufferedWriter(new OutputStreamWriter(stream));                
            } else {
                //use provided charset
                writer = new BufferedWriter(new OutputStreamWriter(stream, 
                        charset));
            }                       
            
            locked = true;
            textureCounter = 0;
            if (listener != null) {
                listener.onWriteStart(this);
            }
            
            try {
                loader.setListener(this.internalListeners);
            } catch (LockedException ignore) {
                //never happens
            }

                        
            writer.write("{\"textures\":[");
            LoaderIterator iter = loader.load();
            
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;
            
            //write array opening
            writer.write("],\"chunks\":[");   
            //indicate that no more textures can follow
            ignoreTextureValidation = true;
            while (iter.hasNext()) {
                DataChunk chunk = iter.next();
                float[] coords = chunk.getVerticesCoordinatesData();
                short[] colors = chunk.getColorData();
                int[] indices = chunk.getIndicesData();
                float[] textureCoords = chunk.getTextureCoordiantesData();
                float[] normals = chunk.getNormalsData();
                
                Material material = chunk.getMaterial();
                        
                boolean coordsAvailable = (coords != null);
                boolean colorsAvailable = (colors != null);
                boolean indicesAvailable = (indices != null);
                boolean textureCoordsAvailable = (textureCoords != null);
                boolean normalsAvailable = (normals != null);
                boolean hasPreviousContent = false;
                        
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
                
                //write chunk opening
                writer.write("{");
                
                //CHUNK CONTENTS
                if (material != null) {
                    //write material
                    writeMaterial(material);
                    hasPreviousContent = true;
                }
                if (indicesAvailable) {
                    //write separator for next piece of data
                    if (hasPreviousContent) writer.write(",");

                    //write indices opening
                    writer.write("\"indices\":[");
                    for (int i = 0; i < indices.length; i++) {
                        writer.write(Integer.toString(indices[i]));
                        //write separator if more elements in array
                        if(i < (indices.length - 1)) writer.write(",");
                    }
                    //write indices closing
                    writer.write("]");
                    hasPreviousContent = true;
                }
                if (normalsAvailable) {
                    //write separator for next piece of data
                    if (hasPreviousContent) {
                        writer.write(",");
                    }
                    
                    //write normals opening
                    writer.write("\"vertexNormals\":[");
                    for (int i = 0; i < normals.length; i++) {
                        if (Float.isInfinite(normals[i]) ||
                                Float.isNaN(normals[i])) {
                            writer.write(Float.toString(Float.MAX_VALUE));
                        } else {
                            writer.write(Float.toString(normals[i]));
                        }
                        //write separator if more elements in array
                        if (i < (normals.length - 1)) {
                            writer.write(",");
                        }
                    }
                    //write normals closing
                    writer.write("]");
                    hasPreviousContent = true;
                }
                if (coordsAvailable) {
                    //write separator for next piece of data
                    if (hasPreviousContent) {
                        writer.write(",");
                    }
                    
                    //write coords opening
                    writer.write("\"vertexPositions\":[");
                    for (int i = 0; i < coords.length; i++) {
                        if (Float.isInfinite(coords[i]) ||
                                Float.isNaN(coords[i])) {
                            writer.write(Float.toString(Float.MAX_VALUE));
                        } else {
                            writer.write(Float.toString(coords[i]));
                        }
                        //write separator if more elements in array
                        if (i < (coords.length - 1)) {
                            writer.write(",");
                        }
                    }
                    //write coords closing
                    writer.write("]");
                    hasPreviousContent = true;
                }
                if (textureCoordsAvailable) {
                    //write separator for next piece of data
                    if (hasPreviousContent) {
                        writer.write(",");
                    }
                    
                    //write texture coords opening
                    writer.write("\"vertexTextureCoords\":[");
                    for (int i = 0; i < textureCoords.length; i++) {
                        if (Float.isInfinite(textureCoords[i]) ||
                                Float.isNaN(textureCoords[i])) {
                            writer.write(Float.toString(Float.MAX_VALUE));
                        } else {
                            writer.write(Float.toString(textureCoords[i]));
                        }
                        //write separator if more elements in array
                        if (i < (textureCoords.length - 1)) {
                            writer.write(",");
                        }
                    }
                    //write texture coords closing
                    writer.write("]");
                    hasPreviousContent = true;
                }
                if (coordsAvailable) {
                    //write separator for next piece of data
                    writer.write(",");

                    //write min corner
                    writer.write("\"minCorner\":[" + chunk.getMinX() + "," + 
                            chunk.getMinY() + "," + chunk.getMinZ() + "],");
                    
                    //write max corner
                    writer.write("\"maxCorner\":[" + chunk.getMaxX() + "," +
                            chunk.getMaxY() + "," + chunk.getMaxZ() + "]");
                    hasPreviousContent = true;
                }
                if (colorsAvailable) {
                    //write separator for next piece of data
                    if (hasPreviousContent) {
                        writer.write(",");
                    }
                    
                    //write colors opening
                    writer.write("\"vertexColors\":[");                                            
                    for (int i = 0; i < colors.length; i++) {
                        writer.write(Short.toString(colors[i]));
                        
                        if (i < (colors.length - 1)) {
                            writer.write(",");
                        }
                    }
                    
                    //write colors closing and color components
                    writer.write("],\"colorComponents\": " +
                            chunk.getColorComponents());
                }
                                
                //write chunk closing
                writer.write("}");
                
                //write chunk separator if more chunks are available
                if (iter.hasNext()) {
                    writer.write(",");
                }
                
                writer.flush();
            }
            //write array closing
            writer.write("],");
            //write bounding box for all chunks
            //write min corner
            writer.write("\"minCorner\":[" + minX + "," + minY + "," + minZ + 
                    "],");
                    
            //write max corner
            writer.write("\"maxCorner\":[" + maxX + "," + maxY + "," + maxZ + 
                    "]");            
            
            //write object closing
            writer.write("}");
            writer.flush();
            
            if (listener != null) {
                listener.onWriteEnd(this);
            }
            locked = false;
            
        } catch (LoaderException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new LoaderException(e);
        }

    }

    /**
     * Processes texture file. By reading provided texture file that has been 
     * created in a temporal location and embedding it into resulting output
     * stream.
     * @param texture reference to texture that uses texture image
     * @param textureFile file containing texture image. File will usually be
     * created in a temporal location.
     * @throws IOException if an I/O error occurs
     */    
    @Override
    protected void processTextureFile(Texture texture, File textureFile) 
            throws IOException {
        if (textureCounter > 0) {
            writer.write(",");
        }
        writer.write("{\"id\":" + texture.getId());
        writer.write(",\"width\":" + texture.getWidth());
        writer.write(",\"height\":" + texture.getHeight());
        if (listener instanceof MeshWriterJsonListener) {
            MeshWriterJsonListener listener2 = (MeshWriterJsonListener)listener;
            if (remoteTextureUrlEnabled) {
                String remoteUrl = listener2.onRemoteTextureUrlRequested(this, 
                        texture, textureFile);
                if (remoteUrl != null) {
                    //add url to json
                    writer.write(",\"remoteUrl\":\"" + remoteUrl + "\"");
                }
            }
            if (remoteTextureIdEnabled) {
                String remoteId = listener2.onRemoteTextureIdRequested(this, 
                        texture, textureFile);
                if (remoteId != null) {
                    //add id to json
                    writer.write(",\"remoteId\": \"" + remoteId + "\"");
                }
            }
            
        }
        if (embedTexturesEnabled) {
            writer.write(",\"data\":\"");
            //write teture file data as base64
            writer.flush(); //flush to sync writer and stream


            try (InputStream textureStream = new FileInputStream(textureFile)) {
                byte[] imageData = new byte[(int) textureFile.length()];

                if (textureStream.read(imageData) > 0) {
                    //convert image byte array into Base64 string
                    //TODO: could we use a Base64OutputStream to reduce memory usage,
                    //but doing the same replacement for safe json generation?
                    String base64 = Base64.encodeBase64String(imageData);
                    base64 = base64.replace("/", "\\/");
                    writer.write(base64);
                    writer.flush();
                }
            }
            writer.write("\"");
        }
        writer.write("}");
        textureCounter++;
    }
    
    /**
     * Writes material into output JSON file.
     * @param material material to be written.
     * @throws IOException if an I/O error occurs.
     */
    private void writeMaterial(Material material) throws IOException {
        writer.write("\"material\":{");
        writer.write("\"id\":" + material.getId());
        if (material.isAmbientColorAvailable()) {
            writer.write(",\"ambientColor\":[" + material.getAmbientRedColor() +
                    "," + material.getAmbientGreenColor() + "," + 
                    material.getAmbientBlueColor() + "]");
        }
        if (material.isDiffuseColorAvailable()) {
            writer.write(",\"diffuseColor\":[" + material.getDiffuseRedColor() +
                    "," + material.getDiffuseGreenColor() + "," +
                    material.getDiffuseBlueColor() + "]");
        }
        if (material.isSpecularColorAvailable()) {
            writer.write(",\"specularColor\":[" + material.getSpecularRedColor() 
                    + "," + material.getSpecularGreenColor() + "," +
                    material.getSpecularBlueColor() + "]");
        }
        if (material.isSpecularCoefficientAvailable()) {
            writer.write(",\"specularCoefficient\":" + 
                    material.getSpecularCoefficient());
        }
        if (material.isAmbientTextureMapAvailable()) {
            Texture tex = material.getAmbientTextureMap();
            writer.write(",\"ambientTextureId\":" + tex.getId());
        }
        if (material.isDiffuseTextureMapAvailable()) {
            Texture tex = material.getDiffuseTextureMap();
            writer.write(",\"diffuseTextureId\":" + tex.getId());
        }
        if (material.isSpecularTextureMapAvailable()) {
            Texture tex = material.getSpecularTextureMap();
            writer.write(",\"specularTextureId\":" + tex.getId());
        }
        if (material.isAlphaTextureMapAvailable()) {
            Texture tex = material.getAlphaTextureMap();
            writer.write(",\"alphaTextureId\":" + tex.getId());
        }
        if (material.isBumpTextureMapAvailable()) {
            Texture tex = material.getBumpTextureMap();
            writer.write(",\"bumpTextureId\":" + tex.getId());
        }
        if (material.isTransparencyAvailable()) {
            writer.write(",\"transparency\":" + material.getTransparency());
        }
        if (material.isIlluminationAvailable()) {
            writer.write(",\"illumination\":\"" + material.getIllumination().
                    name() + "\"");
        }
        writer.write("}");
    }
}
