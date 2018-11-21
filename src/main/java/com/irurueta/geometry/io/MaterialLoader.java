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
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Set;

/**
 * Abstract class defining the interface for classes in charge of loading 
 * materials.
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public abstract class MaterialLoader {

    /**
     * Maximum allowed file size to keep cached in memory. Files exceeding this
     * size will just be streamed.
     */
    public static final long DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY =
            50000000;

    /**
     * Constant defining whether textures must be validated by default.
     */
    public static final boolean DEFAULT_TEXTURE_VALIDATION_ENABLED = true;

    /**
     * Instance in charge of reading data from file.
     */
    protected AbstractFileReaderAndWriter reader;
    
    /**
     * Boolean indicating that this instance is locked because decoding is being
     * done.
     */
    protected boolean locked;
    
    /**
     * Listener in charge of notifying when the decoding starts, ends or 
     * progress events.
     */
    protected MaterialLoaderListener listener;
    
    /**
     * File to be read.
     */
    protected File file;

    /**
     * Indicates if texture images must be validated to ensure that they are
     * valid image files.
     */
    protected boolean textureValidationEnabled;

    /**
     * Limit of bytes to keep mapped in memory. If provided file exceeds this
     * value, then it is not mapped into memory.
     */    
    private long fileSizeLimitToKeepInMemory;

    /**
     * Default Constructor.
     */
    public MaterialLoader() {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        reader = null;
        locked = false;
        listener = null;
        textureValidationEnabled = DEFAULT_TEXTURE_VALIDATION_ENABLED;
    }
    
    /**
     * Constructor.
     * @param f material file to be read.
     * @throws IOException raised if provided file does not exist or an I/O
     * exception occurs.
     */
    public MaterialLoader(File f) throws IOException {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        file = f;
        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, 
                    FileChannel.MapMode.READ_ONLY);            
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
        locked = false;
        listener = null;
        textureValidationEnabled = DEFAULT_TEXTURE_VALIDATION_ENABLED;
    }
    
    /**
     * Constructor.
     * @param listener material listener to notify start, end and progress 
     * events.
     */
    public MaterialLoader(MaterialLoaderListener listener) {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        reader = null;
        locked = false;
        this.listener = listener;
        textureValidationEnabled = DEFAULT_TEXTURE_VALIDATION_ENABLED;
    }
    
    /**
     * Constructor.
     * @param f material file to be read.
     * @param listener material listener to notify start, end and progress 
     * events.
     * @throws IOException raised if provided file does not exist or an I/O
     * exception occurs.
     */
    public MaterialLoader(File f, MaterialLoaderListener listener) 
            throws IOException {
        fileSizeLimitToKeepInMemory = DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY;
        file = f;
        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, 
                    FileChannel.MapMode.READ_ONLY);            
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
        locked = false;
        this.listener = listener;
        textureValidationEnabled = DEFAULT_TEXTURE_VALIDATION_ENABLED;
    }
    
    /**
     * Method called when an instance of this class is garbage collected.
     * This method ensures that resources such as files get closed.
     * @throws Throwable if something fails.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            close(); //attempt to close file resources
        } catch (Throwable ignore) { }
    }    
    
    /**
     * Returns maximum allowed file size to keep cached in memory. Files 
     * exceeding this size will just be streamed.
     * @return maximum allowed file size to keep cached in memory.
     */
    public long getFileSizeLimitToKeepInMemory() {
        return fileSizeLimitToKeepInMemory;
    }
    
    /**
     * Sets maximum allowed file size to keep cached in memory. Files exceeding
     * this size will just be streamed.
     * @param fileSizeLimitToKeepInMemory maximum allowed file size to keep 
     * cached in memory.
     * @throws LockedException if this instance is already loading another file.
     */
    public void setFileSizeLimitToKeepInMemoery(
            long fileSizeLimitToKeepInMemory) throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        
        this.fileSizeLimitToKeepInMemory = fileSizeLimitToKeepInMemory;
    }
    
    /**
     * Indicates whether a material file to be loaded has already been set.
     * @return True if material file has already been provided, false otherwise.
     */
    public boolean hasFile() {
        return (reader != null);
    }
    
    /**
     * Sets material file to be loaded.
     * @param f material file to be loaded.
     * @throws LockedException raised if this instance is loaded because a file
     * is already being loaded.
     * @throws IOException raised if provided file does not exist or if an I/O
     * exception occurs.
     */
    public void setFile(File f) 
            throws LockedException, IOException {
        if (isLocked()) {
            throw new LockedException();
        }
        file = f;
        
        if (reader != null) {
            reader.close(); //close previous file
        }
        
        if (f.length() < fileSizeLimitToKeepInMemory) {
            reader = new MappedFileReaderAndWriter(f, 
                    FileChannel.MapMode.READ_ONLY);            
        } else {
            reader = new FileReaderAndWriter(f, FileChannel.MapMode.READ_ONLY);
        }
    }
    
    /**
     * Closes file provided to this loader.
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
    
    /**
     * Indicates if textures must be validated to ensure that image files are
     * valid files.
     * @return true if textures must be validated, false otherwise.
     */
    public boolean isTextureValidationEnabled() {
        return textureValidationEnabled;
    }
    
    /**
     * Specifies whether textures must be validated to ensure that image files
     * are valid files.
     * @param textureValidationEnabled true if textures must be validated, false
     * otherwise.
     */
    public void setTextureValidationEnabled(boolean textureValidationEnabled) {
        this.textureValidationEnabled = textureValidationEnabled;
    }
      
    /**
     * Determines whether this instance is locked.
     * A material loader remains locked while decoding of a file is being done.
     * This instance will remain locked once the loading process starts until
     * it finishes either successfully or not.
     * While this instance remains locked no parameters can be changed, 
     * otherwise a LockedException will be raised.
     * @return True if instance is locked, false otherwise.
     */
    public synchronized boolean isLocked() {
        return locked;
    }
    
    /**
     * Sets the lock value of this instance so that no parameters cannot be 
     * changed until this instance is unlocked.
     * @param locked value that determines whether this instance is locked or 
     * not.
     */
    protected synchronized void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Returns material listener of this instance.
     * A material listener notifies of start, end and progress change events.
     * @return material listener of this instance.
     */
    public MaterialLoaderListener getListener() {
        return listener;
    }
    
    /**
     * Sets material listener of this instance.
     * A material listener notifies of start, end and progress change events.
     * @param listener material listener of this instance.
     * @throws LockedException Raised if this instance is already locked.
     */
    public void setListener(MaterialLoaderListener listener) 
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        this.listener = listener;
    }
   
    /**
     * Determines whether enough parameters have been set so that the loading
     * process can start.
     * @return True if this instance is ready to start loading a file, false
     * otherwise.
     */
    public abstract boolean isReady();
    
    /**
     * Starts the loading process of provided file.
     * This method returns a set containing all the materials that have been
     * loaded.
     * @return a set containing all the materials that have been loaded.
     * @throws LockedException raised if this instance is already locked.
     * @throws NotReadyException raised if this instance is not yet ready.
     * @throws IOException if an I/O error occurs.
     * @throws LoaderException if file is corrupted or cannot be interpreted.
     */
    public abstract Set<Material> load() throws LockedException, 
            NotReadyException, IOException, LoaderException;    
}
