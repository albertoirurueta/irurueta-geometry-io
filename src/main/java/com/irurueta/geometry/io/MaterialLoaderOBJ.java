/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MaterialLoaderOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 12, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * MaterialLoader implementation for OBJ files, which is capable of reading its
 * associated MTL file.
 */
public class MaterialLoaderOBJ extends MaterialLoader{

    /**
     * Set of materials loaded in MTL file
     */
    private Set<Material> materials = new HashSet<Material>();
    
    /**
     * Current material being loaded
     */
    private MaterialOBJ currentMaterial = null;    
    
    /**
     * Number of textures that have been found in MTL file
     */
    private int textureCounter;
    
    
    /**
     * Maximum allowed color value
     */
    public static final short MAX_COLOR_VALUE = 255;
    
    /**
     * Default Constructor
     */
    public MaterialLoaderOBJ(){
        super();
    }
    
    /**
     * Constructor
     * @param f material file to be read
     * @throws IOException raised if provided file does not exist or an I/O
     * exception occurs
     */
    public MaterialLoaderOBJ(File f) throws IOException{
        super(f);
    }
    
    /**
     * Constructor
     * @param listener material listener to notify start, end and progress 
     * events.
     */
    public MaterialLoaderOBJ(MaterialLoaderListener listener){
        super(listener);
    }
    
    /**
     * Constructor
     * @param f material file to be read
     * @param listener material listener to notify start, end and progress 
     * events
     * @throws IOException raised if provided file does not exist or an I/O
     * exception occurs
     */
    public MaterialLoaderOBJ(File f, MaterialLoaderListener listener) 
            throws IOException{
        super(f, listener);
    }
        
    /**
     * Indicates if material loader is ready to be used because a file has
     * already been provided.
     * @return true if material loader is ready, false otherwise.
     */
    @Override
    public boolean isReady() {
        return hasFile();
    }
    
    /**
     * Starts the loading process of provided file.
     * This method returns a set containing all the materials that have been
     * loaded
     * @return a set containing all the materials that have been loaded
     * @throws LockedException raised if this instance is already locked
     * @throws NotReadyException raised if this instance is not yet ready
     * @throws IOException if an I/O error occurs
     * @throws LoaderException if file is corrupted or cannot be interpreted.
     */
    @Override
    public Set<Material> load() throws LockedException, 
            NotReadyException, IOException, LoaderException{  

        if(!isReady()) throw new NotReadyException();        
        if(isLocked()) throw new LockedException();        

        setLocked(true);
        
        materials.clear();
        textureCounter = 0;
        
        if(listener != null) listener.onLoadStart(this);
        
        String line;        
        do{            
            line = reader.readLine();
            if(line != null){
                parseLine(line);
            }
        }while(line != null);
        
        currentMaterial.setId(materials.size());
        materials.add(currentMaterial);
        
        if(listener != null) listener.onLoadEnd(this);
        
        setLocked(false);
        
        return materials;        
    }
    
    /**
     * Parses a line in a MTL file
     * @param line line being parsed
     * @throws LoaderException if line cannot be interpreted
     */
    private void parseLine(String line) throws LoaderException{
        
        if(line == null || line.isEmpty()) return;
        
        try{
            StringTokenizer tokenizer = new StringTokenizer(line);
            if(!tokenizer.hasMoreTokens()) return; //empty line or simply 
                                //containing separators (tabs, line feeds, etc)
            
            String command = tokenizer.nextToken();
            
            //search for command position
            int commandPos = line.indexOf(command);
        

            if(command.equalsIgnoreCase("newmtl")){
                if(currentMaterial != null)
                {
                    currentMaterial.setId(materials.size());
                    materials.add(currentMaterial);
                }             
                String name = line.substring(
                        commandPos + command.length()).trim();
                currentMaterial = new MaterialOBJ(name);                        
        
            }else if(command.equalsIgnoreCase("ka")){
                if(currentMaterial == null) throw new LoaderException();             
                currentMaterial.setAmbientColor( 
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE));
        
            }else if(command.equalsIgnoreCase("kd")){
                if(currentMaterial == null) throw new LoaderException();             
                currentMaterial.setDiffuseColor( 
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE));
        
            }else if(command.equalsIgnoreCase("Ks")){
                if(currentMaterial == null) throw new LoaderException();
                currentMaterial.setSpecularColor(
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE),
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE));
        
            }else if(command.equalsIgnoreCase("Ns") || 
                    command.equalsIgnoreCase("Ni")){
                if(currentMaterial == null) throw new LoaderException();
                currentMaterial.setSpecularCoefficient(
                        Float.valueOf(tokenizer.nextToken()));            
        
            }else if(command.equalsIgnoreCase("d") || 
                    command.equalsIgnoreCase("Tr")){
                if(currentMaterial == null) throw new LoaderException();             
                currentMaterial.setTransparency( 
                        (short)(Float.valueOf(tokenizer.nextToken()) * 
                        MAX_COLOR_VALUE));            
        
            }else if(command.equalsIgnoreCase("illum")){
                if(currentMaterial == null) throw new LoaderException();             
                currentMaterial.setIllumination( 
                        Illumination.forValue(
                        Integer.valueOf(tokenizer.nextToken()).intValue()));            
        
            }else if(command.equalsIgnoreCase("map_Kd")){
                String textureName = line.substring(
                        commandPos + command.length()).trim();
                Texture tex = new Texture(textureName, textureCounter);
                textureCounter++;
                
                boolean valid = true;
                if(textureValidationEnabled && listener != null){
                    valid = listener.onValidateTexture(this, tex);
                }
                if(!valid) throw new InvalidTextureException();
                
                currentMaterial.setDiffuseTextureMap(tex);
            
            }else if(command.equalsIgnoreCase("map_Ka")){
                String textureName = line.substring(
                        commandPos + command.length()).trim();
                Texture tex = new Texture(textureName, textureCounter);
                textureCounter++;
                
                boolean valid = true;
                if(textureValidationEnabled && listener != null){
                    valid = listener.onValidateTexture(this, tex);
                }
                if(!valid) throw new InvalidTextureException();
                
                currentMaterial.setAmbientTextureMap(tex);
            
            }else if(command.equalsIgnoreCase("map_Ks")){
                String textureName = line.substring(
                        commandPos + command.length()).trim();
                Texture tex = new Texture(textureName, textureCounter);
                textureCounter++;
                
                boolean valid = true;
                if(textureValidationEnabled && listener != null){
                    valid = listener.onValidateTexture(this, tex);
                }
                if(!valid) throw new InvalidTextureException();
                
                currentMaterial.setSpecularTextureMap(tex);
            
            }else if(command.equalsIgnoreCase("map_d")){
                String textureName = line.substring(
                        commandPos + command.length()).trim();
                Texture tex = new Texture(textureName, textureCounter);
                textureCounter++;
                
                boolean valid = true;
                if(textureValidationEnabled && listener != null){
                    valid = listener.onValidateTexture(this, tex);
                }
                if(!valid) throw new InvalidTextureException();
                
                currentMaterial.setAlphaTextureMap(tex);
            
            }else if(command.equalsIgnoreCase("map_bump") || 
                    command.equalsIgnoreCase("bump")){
                String textureName = line.substring(
                        commandPos + command.length()).trim();
                Texture tex = new Texture(textureName, textureCounter);
                textureCounter++;
                
                boolean valid = true;
                if(textureValidationEnabled && listener != null){
                    valid = listener.onValidateTexture(this, tex);
                }
                if(!valid) throw new InvalidTextureException();
                
                currentMaterial.setBumpTextureMap(tex);
            }
        }catch(LoaderException e){
            throw e;
        }catch(Throwable t){
            throw new LoaderException(t);
        }
    }
    
    /**
     * Indicates if any material has been loaded already
     * @return true if materials have been loaded, false otherwise
     */
    public boolean areMaterialsAvailable(){
        return materials != null && materials.size() > 0;
    }

    /**
     * Returns set of materials that have been read
     * @return set of materials that have been read
     */
    public Set<Material> getMaterials(){
        return materials;
    }

    /**
     * Gets a material by its name, or null if material is not found
     * @param name name of material to be found
     * @return a material having provided name or null if none is found
     */
    public MaterialOBJ getMaterialByName(String name){
        if(name == null) return null;
        
        for(Material m: this.materials){
            if(m instanceof MaterialOBJ){
                MaterialOBJ m2 = (MaterialOBJ)m;
                
                if(m2.getMaterialName() == null) continue;
                
                if(m2.getMaterialName().equals(name)){
                    return m2;
                }
            }
        }
        return null;
    }
    
    /**
     * Indicates if material having provided name has been loaded or not
     * @param name name to search material by
     * @return true if material having provided name exists, false otherwise
     */
    public boolean containsMaterial(String name){
        return getMaterialByName(name) != null;
    }
    
    /**
     * Returns material by texture name
     * @param name name of texture
     * @return texture that has been found or null if none has been found
     */
    public Material getMaterialByTextureMapName(String name){
        if(name == null) return null;
        
        for(Material m: this.materials){
            if(m.getAlphaTextureMap() != null && 
                    m.getAlphaTextureMap().getFileName() != null &&
                    m.getAlphaTextureMap().getFileName().equals(name)){
                return m;
            }
        
            if(m.getAmbientTextureMap() != null &&
                    m.getAmbientTextureMap().getFileName() != null &&
                    m.getAmbientTextureMap().getFileName().equals(name)){
                return m;
            }
            
            if(m.getDiffuseTextureMap() != null &&
                    m.getDiffuseTextureMap().getFileName() != null &&
                    m.getDiffuseTextureMap().getFileName().equals(name)){
                return m;
            }
            
            if(m.getSpecularTextureMap() != null &&
                    m.getSpecularTextureMap().getFileName() != null &&
                    m.getSpecularTextureMap().getFileName().equals(name)){
                return m;
            }
        }
        return null;
    }        
}
