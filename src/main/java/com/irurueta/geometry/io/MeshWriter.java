/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MeshWriter
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date November 19, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract class that defines the interface for writers. A MeshWriter will be
 * able to transcode a given 3D file into another format
 */
public abstract class MeshWriter{
    
    /**
     * Loader to load a file to be transcoded
     */
    protected Loader loader;
    
    /**
     * Stream where transcoded data will be written to
     */
    protected OutputStream stream;    
    
    /**
     * Listener to be notified when transcoding process starts, stops or when
     * progress changes
     */
    protected MeshWriterListener listener;
    
    /**
     * Indicates if mesh writer is locked because a file is being processed
     */
    protected boolean locked;
    
    /**
     * Indicates if texture validation will be ignored or not. If not ignored, 
     * it will be ensured that textures are valid image files.
     */
    protected boolean ignoreTextureValidation = false;
    
    /**
     * Internal class implementing specific listeners for some specific loader
     * implementations
     */
    Listeners internalListeners;
    
    /**
     * Constructor
     * @param loader loader to load a 3D file
     * @param stream stream where transcoded data will be written to
     */
    public MeshWriter(Loader loader, OutputStream stream){
        this.loader = loader;
        this.stream = stream;
        listener = null;
        locked = false;
        
        internalListeners = new Listeners(this);
    }
    
    /**
     * Constructor
     * @param loader loader to load a 3D file
     * @param stream stream where transcoded data will be written to
     * @param listener listener to be notified of progress changes or when
     * transcoding process starts or finishes.
     */
    public MeshWriter(Loader loader, OutputStream stream, 
            MeshWriterListener listener){
        this.loader = loader;
        this.stream = stream;
        this.listener = listener;
        locked = false;
        
        internalListeners = new Listeners(this);
    }
    
    /**
     * Boolean indicating whether this mesh writer is locked because a file is
     * being processed
     * @return true if this instance is locked, false otherwise
     */
    public boolean isLocked(){
        return locked;
    }
        
    /**
     * Returns stream where transcoded data will be written to
     * @return stream where transcoded data will be written to
     */
    public OutputStream getStream(){
        return stream;
    }
    
    /**
     * Returns listener to be notified when transcoding process starts, stops or 
     * when progress changes
     * @return listener of this mesh writer
     */
    public MeshWriterListener getListener(){
        return listener;
    }
    
    /**
     * Sets listener to be notified when transcoding process starts, stops or
     * when progress changes
     * @param listener listener to be set
     * @throws LockedException if this instance is locked because this mesh 
     * writer is already processing a file
     */
    public void setListener(MeshWriterListener listener) throws LockedException{
        if(isLocked()) throw new LockedException();
        this.listener = listener;
    }
        
    /**
     * Indicates if this mesh writer is ready because a file and a loader have
     * been provided
     * @return true if mesh writer is ready, false otherwise
     */
    public boolean isReady(){
        return (stream != null) && (loader != null);
    }

    /**
     * Internal class implementing listeners for different specific loaders
     */
    private class Listeners implements LoaderListener,
        LoaderListenerOBJ, LoaderListenerBinary, MaterialLoaderListener{
        
        /**
         * Reference to mesh writer
         */
        private MeshWriter writer;
        
        /**
         * Constructor
         * @param writer reference to mesh writer
         */
        public Listeners(MeshWriter writer){
            this.writer = writer;
        }
        
        //Loader listener
        
        /**
         * Method called when the loader starts processing a file
         * @param loader reference to loader
         */
        @Override
        public void onLoadStart(Loader loader){}

        /**
         * Method called when the loader ends processing a file
         * @param loader reference to loader
         */
        @Override
        public void onLoadEnd(Loader loader){}

        /**
         * Method called when loading progress changes enough to be notified
         * @param loader reference to loader
         * @param progress progress amount as a value between 0.0 and 1.0
         */
        @Override
        public void onLoadProgressChange(Loader loader, float progress) {
            if(listener != null){
                listener.onWriteProgressChange(writer, progress);
            }
        }

        //OBJ loader listener (requesting a material loader for a given path)
        /**
         * Called when an OBJ loader requires a material loader to load the
         * associated material (MTL) file
         * @param loader reference to loader
         * @param path path where MTL file should be found. It's up to the
         * final implementation to determine where the file will be finally 
         * found
         * @return an instance of a material loader
         */
        @Override
        public MaterialLoaderOBJ onMaterialLoaderRequested(LoaderOBJ loader, 
                String path) {
            if(listener != null){
                File materialFile = listener.onMaterialFileRequested(writer, 
                        path);
                
                if(materialFile == null) return null;
                
                try{
                    //when instantiating material loader it checks if file
                    //exists, if not we return null
                    return new MaterialLoaderOBJ(materialFile, this);
                } catch (IOException ex) {
                    return null;
                }
            }
        
            return null;
        }
    
        /**
         * Called when a texture has been found
         * @param loader reference to loader
         * @param textureId id assigned to the texture that has been found
         * @param textureImageWidth width of texture image that has been found
         * @param textureImageHeight height of texture image that has been found
         * @return File where texture data will be copied to
         */
        @Override
        public File onTextureReceived(LoaderBinary loader, int textureId,
                int textureImageWidth, int textureImageHeight){
            if(listener != null){
                return listener.onTextureReceived(writer, textureImageWidth, 
                        textureImageHeight);
            }
        
            return null;
        }
    
        /**
         * Called when texture data is available to be retrieved
         * @param loader reference to loader
         * @param textureFile reference to a File where texture data has been
         * temporarily copied
         * @param textureId id assigned to the texture
         * @param textureImageWidth width of texture image
         * @param textureImageHeight height of texture image
         * @return converted image transcoded into JPG format or resized if
         * needed, or input textureFile if no changes are needed.
         */
        @Override
        public boolean onTextureDataAvailable(LoaderBinary loader, 
                File textureFile, int textureId, int textureImageWidth, 
                int textureImageHeight){            
            boolean valid = true;
            File convertedFile = textureFile;
            if(textureFile != null){
                if(listener != null){
                    //notify that texture data has been copied into texture file 
                    //in case it needs to be handled elsewhere (to transform it 
                    //into another format)
                    convertedFile = listener.onTextureDataAvailable(writer, 
                            textureFile, textureImageWidth, textureImageHeight);
                }
                
                if(convertedFile != null){
                    Texture tex = new Texture(textureId);
                    tex.setWidth(textureImageWidth);
                    tex.setHeight(textureImageHeight);
                    tex.setValid(true);
            
                    try{
                        processTextureFile(tex, convertedFile);
                    }catch(IOException ex){
                        valid = false;
                    }
                    
                    if(listener != null){
                        listener.onTextureDataProcessed(writer, textureFile, 
                                textureId, textureImageHeight);
                    }                    
                }                
            }
            return valid;
        }
    
        //Material loader listener
        /**
         * Called when material loader starts processing materials
         * @param loader reference to loader
         */
        @Override
        public void onLoadStart(MaterialLoader loader){}

        /**
         * Called when material loader finishes processing materials
         * @param loader reference to loader
         */
        @Override
        public void onLoadEnd(MaterialLoader loader){}

        /**
         * Called when a texture assigned to a material must be validated to
         * ensure that texture is valid
         * @param loader reference to loader
         * @param texture texture reference to be validated
         * @return true if texture is valid, false otherwise
         */
        @Override
        public boolean onValidateTexture(MaterialLoader loader, 
                Texture texture){
            boolean valid = true;
            if(listener != null && !ignoreTextureValidation){
                File textureFile = listener.onValidateTexture(writer, texture);
                valid = texture.isValid();
            
                if(valid && textureFile != null){
                    try {
                        processTextureFile(texture, textureFile);
                    } catch (IOException ex) {
                        valid = false;
                    }
            
                    //notify that texture was validated in case that texture 
                    //file needs to be removed
                    listener.onDidValidateTexture(writer, textureFile);
                }
            }
            return valid;
        }
    }
    
    /**
     * Abstract method to process input file and write it into output stream
     * @throws LoaderException if 3D file loading fails
     * @throws IOException if an I/O error occurs
     * @throws NotReadyException if mesh writer is not ready because either a 
     * loader has not been provided or an output stream has not been provided
     * @throws LockedException if this mesh writer is locked processing a file
     */
    public abstract void write() throws LoaderException, IOException, 
            NotReadyException, LockedException;    
    
    /**
     * Abstract method to processes texture file. Usually this will imply 
     * validating that image file is not corrupt and has proper size (power of 
     * 2). If not image will be resized
     * @param texture reference to texture
     * @param textureFile file where texture is temporarily copied
     * @throws IOException if an I/O error occurs
     */
    protected abstract void processTextureFile(Texture texture, 
            File textureFile) throws IOException;
}
