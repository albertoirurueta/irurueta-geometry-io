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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Loads a custom binary file implemented for this library.
 * The binary format has been created to keep 3D data in a more compact way than
 * other formats.
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class LoaderBinary extends Loader {

    /**
     * Buffer size to load input file.
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * Version number of the binary format supported by this class.
     */
    private static final byte SUPPORTED_VERSION = 2;

    /**
     * Number of bytes required to determine the bounding box of a chunk or
     * the whole 3D object (which are 2 3D points = 2x3 coordinates =
     * 6 floats * 4 bytes per float.
     */
    private static final int BOUNDING_BYTES_SIZE = 6 * Float.SIZE / 8;

    /**
     * Iterator to load binary data in small chunks.
     * Usually data is divided in chunks that can be directly loaded by 
     * graphic layers such as OpenGL.
     */
    private LoaderIteratorBinary loaderIterator;   
    
    /**
     * Indicates if file has been checked to have a valid header. Notice that
     * file might still be corrupt or incomplete at some point.
     */
    private boolean validityChecked;
    
    /**
     * Indicates if after checking validity, the file header has been found to
     * be valid or not.
     */
    private boolean validFile;

    /**
     * Constructor.
     */
    public LoaderBinary() {
        loaderIterator = null;
        validityChecked = false;
        validFile = false;
    }
    
    /**
     * Constructor.
     * @param f file to be loaded.
     * @throws IOException if an I/O error occurs.
     */
    public LoaderBinary(File f) throws IOException {
        super(f);
        loaderIterator = null;
        validityChecked = false;
        validFile = false;
    }
    
    /**
     * Constructor.
     * @param listener listener to be notified of loading progress.
     */
    public LoaderBinary(LoaderListener listener) {
        super(listener);
        loaderIterator = null;
        validityChecked = false;
        validFile = false;
    }
    
    /**
     * Constructor.
     * @param f file to be loaded.
     * @param listener listener to be notified of loading progress.
     * @throws IOException if an I/O error occurs.
     */
    public LoaderBinary(File f, LoaderListener listener) throws IOException {
        super(f, listener);
        loaderIterator = null;
        validityChecked = false;
        validFile = false;
    }

    /**
     * If loader is ready to start loading a file.
     * This is true once a file has been provided.
     * @return true if ready to start loading a file, false otherwise.
     */
    @Override
    public boolean isReady() {
        return hasFile();
    }

    /**
     * Returns mesh format supported by this class, which is MESH_FORMAT_BINARY2.
     * @return mesh format supported by this class.
     */
    @Override
    public MeshFormat getMeshFormat() {
        return MeshFormat.MESH_FORMAT_BINARY2;
    }

    /**
     * Determines if provided file is a valid file that can be read by this 
     * loader.
     * @return true if file is valid, false otherwise.
     * @throws LockedException raised if this instance is already locked.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public boolean isValidFile() throws LockedException, IOException {
        if (!hasFile()) {
            throw new IOException();
        }
        if (isLocked()) {
            throw new LockedException();
        }
        
        if (!validityChecked) {
            //check that file version is supported
            byte version = reader.readByte();
            validFile = (version == SUPPORTED_VERSION);
        }
        
        return validFile;
    }

    /**
     * Starts the loading process of provided file.
     * This method returns a LoaderIterator to start the iterative process to
     * load a file in small chunks of data.
     * @return a loader iterator to read the file in a step by step process.
     * @throws LockedException raised if this instance is already locked.
     * @throws NotReadyException raised if this instance is not yet ready.
     * @throws IOException if an I/O error occurs.
     * @throws LoaderException if file is corrupted or cannot be interpreted.
     */    
    @Override
    public LoaderIterator load() throws LockedException, NotReadyException, 
            IOException, LoaderException {
        if (isLocked()) {
            throw new LockedException();
        }
        if (!isReady()) {
            throw new NotReadyException();
        }
        
        //check file validity by reading its version
        if (!isValidFile()) {
            throw new LoaderException();
        }
        
        setLocked(true);
        if (listener != null) {
            listener.onLoadStart(this);
        }
        
        //read textures until no more textures are available
        while (reader.readBoolean()) {
            //texture data follows
            int texId = reader.readInt();
            int texWidth = reader.readInt();
            int texHeight = reader.readInt();
            long texLength = reader.readLong();
            long textureFileStartPos = reader.getPosition();
            long textureFileEndPos = textureFileStartPos + texLength;
            
            //check that at least texLength bytes remain otherwise file is
            //incomplete or corrupted
            if (textureFileEndPos > file.length()) {
                throw new LoaderException();
            }
            
            //notify that texture data is available
            if (listener instanceof LoaderListenerBinary) {
                LoaderListenerBinary loaderListener = 
                        (LoaderListenerBinary)listener;
                
                //request file where texture will be stored
                File texFile = loaderListener.onTextureReceived(this, texId,
                        texWidth, texHeight);
                
                if (texFile != null) {
                    //write texture data at provided file
                    byte[] buffer = new byte[BUFFER_SIZE];

                    int counter = 0;
                    try (OutputStream outStream = new FileOutputStream(texFile)) {
                        int n;
                        int len = BUFFER_SIZE;
                        while (counter < texLength) {
                            if (counter + len > texLength) {
                                len = (int) texLength - counter;
                            }
                            n = reader.read(buffer, 0, len);
                            outStream.write(buffer, 0, n);
                            if (n > 0) {
                                counter += n;
                            } else break;
                        }

                        outStream.flush();
                    }

                    if (counter != texLength) {
                        throw new LoaderException();
                    }
                
                    //notify that texture data has been written to provided file
                    boolean valid = loaderListener.onTextureDataAvailable(this, 
                            texFile, texId, texWidth, texHeight);
                    //texture processing couldn't be correctly done
                    if (!valid) {
                        throw new LoaderException();
                    }
                } else {
                    //skip to end of texture file in case that listener 
                    //didn't provide a file to write texture data
                    reader.skip(texLength);
                }
            } else {
                //skip to end of texture file in case that there is no listener
                reader.skip(texLength);
            }
        }
        
        loaderIterator = new LoaderIteratorBinary(this);
        loaderIterator.setListener(new LoaderIteratorListenerImpl(this));
        return loaderIterator;
    }
    
    /**
     * Internal listener to be notified when loading process finishes.
     * This listener is used to free resources when loading process finishes.
     */
    private class LoaderIteratorListenerImpl implements LoaderIteratorListener {
        
        /**
         * Reference to Loader loading binary file.
         */
        private LoaderBinary loader;
        
        /**
         * Constructor.
         * @param loader reference to Loader.
         */
        public LoaderIteratorListenerImpl(LoaderBinary loader) {
            this.loader = loader;
        }
        
        /**
         * Method to be notified when the loading process finishes.
         * @param iterator iterator loading the file in chunks.
         */
        @Override
        public void onIteratorFinished(LoaderIterator iterator) {
            //because iterator is finished, we should allow subsequent calls to 
            //load method

            //on subsequent calls
            if (listener != null) {
                listener.onLoadEnd(loader);
            }
            setLocked(false);
        }        
    }
    
    /**
     * Loader iterator in charge of loading file data in small chunks.
     * Usually data is divided in chunks small enough that can be directly 
     * loaded by graphical layers such as OpenGL (which has a limit of 65535 
     * indices when using Vertex Buffer Objects, which increase graphical
     * performance).
     */
    private class LoaderIteratorBinary implements LoaderIterator {
        
        /**
         * Reference to loader loading binary file.
         */
        private LoaderBinary loader;
        
        /**
         * Reference to the listener of this loader iterator. This listener will
         * be notified when the loading process finishes so that resources can
         * be freed.
         */
        private LoaderIteratorListener listener;
        
        /**
         * Constructor.
         * @param loader reference to loader loading binary file.
         */
        public LoaderIteratorBinary(LoaderBinary loader) {
            this.loader = loader;
            listener = null;
        }

        /**
         * Returns listener of this loader iterator.
         * This listener will be notified when the loading process finishes.
         * @return listener of this loader iterator.
         */
        public LoaderIteratorListener getListener() {
            return listener;
        }

        /**
         * Method to set listener of this loader iterator.
         * This listener will be notified when the loading process finishes.
         * @param listener listener of this loader iterator.
         */
        public void setListener(LoaderIteratorListener listener) {
            this.listener = listener;
        }

        /**
         * Indicates if there is another chunk of data to be loaded.
         * @return true if there is another chunk of data, false otherwise.
         */
        @Override
        public boolean hasNext() {
            try {
                return !reader.isEndOfStream();
            } catch (IOException e) {
                return false;
            }
        }

        /**
         * Loads and returns next chunk of data, if available.
         * @return next chunk of data.
         * @throws NotAvailableException thrown if no more data is available.
         * @throws LoaderException if file data is corrupt or cannot be 
         * understood.
         * @throws IOException if an I/O error occurs.
         */
        @Override
        public DataChunk next() throws NotAvailableException, LoaderException, 
                IOException {
            if (!hasNext()) {
                throw new NotAvailableException();
            }
                        
            //read chunk size
            int chunkSize = reader.readInt();
                        
            //ensure that chunk size is positive, otherwise file is corrupted
            if (chunkSize < 0) {
                throw new LoaderException();
            }
            
            //get position of start of chunk
            long chunkStartPos = reader.getPosition();            
            
            //position of end of chunk
            long chunkEndPos = chunkStartPos + chunkSize;
            
            //check that at least chunkSize bytes remain otherwise file is
            //incomplete or corrupted
            long fileLength = file.length();
            if (chunkEndPos > fileLength) {
                throw new LoaderException();
            }
            
            DataChunk chunk = new DataChunk();
            
            //----- MATERIAL ------
            if (reader.readBoolean()) {
                //material is available
                int materialId = reader.readInt();
                
                Material material = new Material();
                chunk.setMaterial(material);
                material.setId(materialId);
                
                if (reader.readBoolean()) {
                    //ambient color is available
                    byte b;
                    //red
                    b = reader.readByte();
                    material.setAmbientRedColor(
                            (short)(((int)b) & 0x000000ff));
                    //green
                    b = reader.readByte();
                    material.setAmbientGreenColor(
                            (short)(((int)b) & 0x000000ff));
                    //blue
                    b = reader.readByte();
                    material.setAmbientBlueColor(
                            (short)(((int)b) & 0x000000ff));
                }
                
                if (reader.readBoolean()) {
                    //diffuse color is available
                    byte b;
                    //red
                    b = reader.readByte();
                    material.setDiffuseRedColor(
                            (short)(((int)b) & 0x000000ff));
                    //green
                    b = reader.readByte();
                    material.setDiffuseGreenColor(
                            (short)(((int)b) & 0x000000ff));
                    //blue
                    b = reader.readByte();
                    material.setDiffuseBlueColor(
                            (short)(((int)b) & 0x000000ff));
                }
                
                if (reader.readBoolean()) {
                    //specular color is available
                    byte b;
                    //red
                    b = reader.readByte();
                    material.setSpecularRedColor(
                            (short)(((int)b) & 0x000000ff));
                    //green
                    b = reader.readByte();
                    material.setSpecularGreenColor(
                            (short)(((int)b) & 0x000000ff));
                    //blue
                    b = reader.readByte();
                    material.setSpecularBlueColor(
                            (short)(((int)b) & 0x000000ff));
                }
                
                if (reader.readBoolean()) {
                    //specular coefficient is available
                    material.setSpecularCoefficient(reader.readFloat());
                }
                
                if (reader.readBoolean()) {
                    //ambient texture map is available
                    int textureId = reader.readInt();
                    Texture tex = new Texture(textureId);
                    material.setAmbientTextureMap(tex);
                    
                    tex.setWidth(reader.readInt());
                    tex.setHeight(reader.readInt());
                }
                
                if (reader.readBoolean()) {
                    //diffuse texture map is available
                    int textureId = reader.readInt();
                    Texture tex = new Texture(textureId);
                    material.setDiffuseTextureMap(tex);
                    
                    tex.setWidth(reader.readInt());
                    tex.setHeight(reader.readInt());
                }
                
                if (reader.readBoolean()) {
                    //specular texture map is available
                    int textureId = reader.readInt();
                    Texture tex = new Texture(textureId);
                    material.setSpecularTextureMap(tex);
                    
                    tex.setWidth(reader.readInt());
                    tex.setHeight(reader.readInt());
                }
                
                if (reader.readBoolean()) {
                    //alpha texture map is available
                    int textureId = reader.readInt();
                    Texture tex = new Texture(textureId);
                    material.setAlphaTextureMap(tex);
                    
                    tex.setWidth(reader.readInt());
                    tex.setHeight(reader.readInt());
                }
                
                if (reader.readBoolean()) {
                    //bump texture map is available
                    int textureId = reader.readInt();
                    Texture tex = new Texture(textureId);
                    material.setBumpTextureMap(tex);
                    
                    tex.setWidth(reader.readInt());
                    tex.setHeight(reader.readInt());
                }
                
                if (reader.readBoolean()) {
                    //transparency is available
                    byte b = reader.readByte();
                    material.setTransparency((short)(((int)b) & 0x000000ff));
                }
                
                if (reader.readBoolean()) {
                    //illumination is available
                    int value = reader.readInt();
                    material.setIllumination(Illumination.forValue(value));
                }                
            }
            
            //---- COORDS -------
            
            //read coords size
            int coordsSizeInBytes = reader.readInt();
            
            //ensure that coords size is positive, otherwise file is corrupted
            if (coordsSizeInBytes < 0) {
                throw new LoaderException();
            }
            //if size in bytes is not multiple of float size (4 bytes), then
            //file is corrupted
            if (coordsSizeInBytes % (Float.SIZE / 8) != 0) {
                throw new LoaderException();
            }
                        
            //if coords are available
            if (coordsSizeInBytes > 0) {
                //ensure that coords fit within chunk, otherwise file is 
                //corrupted
                if (reader.getPosition() + coordsSizeInBytes > chunkEndPos) {
                    throw new LoaderException();
                }
                
                //get number of floats in coords
                int coordsLength = coordsSizeInBytes / (Float.SIZE / 8);
                            
                //read coordsSize bytes into array of floats
                float [] coords = new float[coordsLength];
                byte [] bytes = new byte[coordsSizeInBytes];
                reader.read(bytes);                
                ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
                FloatBuffer floatBuffer = bytesBuffer.asFloatBuffer();
                floatBuffer.get(coords);
                
                chunk.setVerticesCoordinatesData(coords);
                                
                //compute progress
                if (loader.listener != null) {
                    loader.listener.onLoadProgressChange(loader, 
                            (float)(reader.getPosition()) / 
                            (float)(file.length()));
                }
            }
            
            
            //----- COLORS ------
            
            //read colors size
            int colorsSizeInBytes = reader.readInt();
            
            //ensure that colors size is positive, otherwise file is corrupted
            if (colorsSizeInBytes < 0) {
                throw new LoaderException();
            }
            
            //each color data is stored in a byte, so there is no need to
            //check size multiplicity
            
            //if colors are available
            if (colorsSizeInBytes > 0) {
                //ensure that colors fit within chunk, otherwise file is 
                //corrupted
                if (reader.getPosition() + colorsSizeInBytes > chunkEndPos) {
                    throw new LoaderException();
                }
             
                //read colorSizeInBytes into array of shorts (conversion
                //must be done from unsigned bytes to shorts, as java does not
                //support unsigned bytes values
                short [] colors = new short[colorsSizeInBytes];
                byte [] bytes = new byte[colorsSizeInBytes];
                reader.read(bytes);
                for (int i = 0; i < colorsSizeInBytes; i++) {
                    //convert signed bytes into unsigned bytes stored in shorts
                    colors[i] = (short)(((int)bytes[i]) & 0x000000ff);
                }
                chunk.setColorData(colors);
                
                //read color components
                chunk.setColorComponents(reader.readInt());
                
                //compute progress
                if (loader.listener != null) {
                    loader.listener.onLoadProgressChange(loader, 
                            (float)(reader.getPosition()) / 
                            (float)(file.length()));
                }                
            }
            
            //------ INDICES ------
            
            //read indices size
            int indicesSizeInBytes = reader.readInt();
            
            //ensure that indices size is positive, otherwise file is corrupted
            if (indicesSizeInBytes < 0) {
                throw new LoaderException();
            }
            //if size in bytes is not multiple of float size (4 bytes), then
            //file is corrupted
            if (indicesSizeInBytes % (Short.SIZE / 8) != 0) {
                throw new LoaderException();
            }
            
            //if indices are available
            if (indicesSizeInBytes > 0) {
                //ensure that indices fit within chunk, otherwise file is 
                //corrupted
                if (reader.getPosition() + indicesSizeInBytes > chunkEndPos) {
                    throw new LoaderException();
                }
                
                //get number of shorts in indices
                int indicesLength = indicesSizeInBytes / (Short.SIZE / 8);
                
                //read coordsSize bytes into array of floats
                int[] indices = new int[indicesLength];
                byte [] bytes = new byte[indicesSizeInBytes];
                reader.read(bytes);
                int firstByte;
                int secondByte;
                int counter = 0;
                for (int i = 0; i < indicesLength; i++) {
                    firstByte = ((int)bytes[counter]) & 0x000000ff;
                    counter++;
                    secondByte = ((int)bytes[counter]) & 0x000000ff;
                    counter++;
                    indices[i] = firstByte << 8 | secondByte;
                }
                chunk.setIndicesData(indices);
                
                //compute progress
                if (loader.listener != null) {
                    loader.listener.onLoadProgressChange(loader, 
                            (float)(reader.getPosition()) / 
                            (float)(file.length()));
                }                
            }
            
            //-------- TEXTURE COORDS --------
            
            //read texture coords size
            int texCoordsSizeInBytes = reader.readInt();
            
            //ensure that texture coords size is positive, otherwise file is 
            //corrupted
            if (texCoordsSizeInBytes < 0) {
                throw new LoaderException();
            }
            //if size in bytes is not multiple of float size (4 bytes), then
            //file is corrupted
            if (texCoordsSizeInBytes % (Float.SIZE / 8) != 0) {
                throw new LoaderException();
            }
                        
            //if texture coords are available
            if (texCoordsSizeInBytes > 0) {
                //ensure that texture coords fit within chunk, otherwise file is 
                //corrupted
                if (reader.getPosition() + texCoordsSizeInBytes > chunkEndPos) {
                    throw new LoaderException();
                }
                
                //get number of floats in coords
                int texCoordsLength = texCoordsSizeInBytes / (Float.SIZE / 8);
                                
                //read coordsSize bytes into array of floats
                float [] texCoords = new float[texCoordsLength];
                byte [] bytes = new byte[texCoordsSizeInBytes];
                reader.read(bytes);
                ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
                FloatBuffer floatBuffer = bytesBuffer.asFloatBuffer();
                floatBuffer.get(texCoords);
                chunk.setTextureCoordinatesData(texCoords);
                
                //compute progress
                if (loader.listener != null) {
                    loader.listener.onLoadProgressChange(loader, 
                            (float)(reader.getPosition()) / 
                            (float)(file.length()));
                }   
            }
            
            
            //-------- NORMALS --------
            
            //read normals size
            int normalsSizeInBytes = reader.readInt();
            
            //ensure that normals size is positive, otherwise file is 
            //corrupted
            if (normalsSizeInBytes < 0) {
                throw new LoaderException();
            }
            //if size in bytes is not multiple of float size (4 bytes), then
            //file is corrupted
            if (normalsSizeInBytes % (Float.SIZE / 8) != 0) {
                throw new LoaderException();
            }
                        
            //if texture coords are available
            if (normalsSizeInBytes > 0) {
                //ensure that normals fit within chunk, otherwise file is 
                //corrupted
                if (reader.getPosition() + normalsSizeInBytes > chunkEndPos) {
                    throw new LoaderException();
                }
                
                //get number of floats in coords
                int normalsLength = normalsSizeInBytes / (Float.SIZE / 8);
                                
                //read coordsSize bytes into array of floats
                float [] normals = new float[normalsLength];
                byte [] bytes = new byte[normalsSizeInBytes];
                reader.read(bytes);
                ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
                FloatBuffer floatBuffer = bytesBuffer.asFloatBuffer();
                floatBuffer.get(normals);
                chunk.setNormalsData(normals);
                
                //compute progress
                if (loader.listener != null) {
                    loader.listener.onLoadProgressChange(loader, 
                            (float)(reader.getPosition()) / 
                            (float)(file.length()));
                }                
            }
            
            //read bounding box for chunk (min/max x, y, z)
            
            //we need to load 6 floats, so position + 6 * Float.SIZE / 8 bytes 
            //must fit within chunk
            if (reader.getPosition() + (BOUNDING_BYTES_SIZE) > chunkEndPos) {
                throw new LoaderException();
            }
            
            
            byte [] bytes = new byte[BOUNDING_BYTES_SIZE];
            reader.read(bytes);
            ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
            FloatBuffer floatBuffer = bytesBuffer.asFloatBuffer();
            chunk.setMinX(floatBuffer.get());
            chunk.setMinY(floatBuffer.get());
            chunk.setMinZ(floatBuffer.get());
            
            chunk.setMaxX(floatBuffer.get());
            chunk.setMaxY(floatBuffer.get());
            chunk.setMaxZ(floatBuffer.get());
            
            //compute progress
            if (loader.listener != null) {
                loader.listener.onLoadProgressChange(loader, 
                        (float)(reader.getPosition()) / (float)(file.length()));
            }
            
            if (!hasNext() && listener != null) {
                //notify iterator finished
                listener.onIteratorFinished(this);
            }
            
            return chunk;
        }
        
    }    
}
