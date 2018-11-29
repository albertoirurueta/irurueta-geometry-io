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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Loads 3D Studio Max files.
 * NOTE: implementation is not yet finished.
 */
@SuppressWarnings("WeakerAccess")
public class Loader3DS extends Loader {

    public static final int ID_MAIN_CHUNK = 0x4D4D;
    public static final int ID_3D_EDITOR_CHUNK = 0x3D3D;
    public static final int ID_OBJECT_BLOCK = 0x4000;
    public static final int ID_TRIANGULAR_MESH = 0x4100;
    public static final int ID_VERTICES_LIST = 0x4110;
    public static final int ID_FACES_DESCRIPTION = 0x4120;
    public static final int ID_FACES_MATERIAL = 0x4130;
    public static final int ID_SMOOTHING_GROUP_LIST = 0x4150;
    public static final int ID_MAPPING_COORDINATES_LIST = 0x4140;
    public static final int ID_LOCAL_COORDINATES_SYSTEM = 0x4160;
    public static final int ID_LIGHT = 0x4600;
    public static final int ID_SPOTLIGHT = 0x4610;
    public static final int ID_CAMERA = 0x4700;
    public static final int ID_MATERIAL_BLOCK = 0xAFFF;
    public static final int ID_MATERIAL_NAME = 0xA000;
    public static final int ID_AMBIENT_COLOR = 0xA010;
    public static final int ID_DIFFUSE_COLOR = 0xA020;
    public static final int ID_SPECULAR_COLOR = 0xA030;
    public static final int ID_TEXTURE_MAP_1 = 0xA200;
    public static final int ID_BUMP_MAP = 0xA230;
    public static final int ID_REFLECTION_MAP = 0xA220;
    public static final int ID_MAPPING_FILENAME = 0xA300;
    public static final int ID_MAPPING_PARAMETERS = 0xA351;
    public static final int ID_KEYFRAMER_CHUNK = 0xB000;
    public static final int ID_MESH_INFORMATION_BLOCK = 0xB002;
    public static final int ID_SPOT_LIGHT_INFORMATION_BLOCK = 0xB007;
    public static final int ID_FRAMES = 0xB008;
    public static final int ID_OBJECT_NAME = 0xB010;
    public static final int ID_OBJECT_PIVOT_POINT = 0xB013;
    public static final int ID_POSITION_TRACK = 0xB020;
    public static final int ID_ROTATION_TRACK = 0xB021;
    public static final int ID_SCALE_TRACK = 0xB022;
    public static final int ID_HIERARCHY_POSITION = 0xB030;
    
    
    public static final long DEFAULT_MESH_VERSION = 0;
    public static final float DEFAULT_MASTER_SCALE = 1.0f;
    public static final int CONSTRUCTION_PLANE_COORDS = 3;        
    public static final short DEFAULT_COLOR = 150;
    public static final short DEFAULT_SPECULAR_COLOR = 229;
    public static final short MAX_COLOR_VALUE = 255;

    protected static final float[] DEFAULT_CONSTRUCTION_PLANE =
            new float[]{0.0f, 0.0f, 1.0f};
    protected static final short[] DEFAULT_AMBIENT_COLOR =
            new short[]{DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR};


    private long meshVersion = DEFAULT_MESH_VERSION;
    private float masterScale = DEFAULT_MASTER_SCALE;
    private float[] constructionPlane = DEFAULT_CONSTRUCTION_PLANE;
    private int keyfRevision;
    private String name;
    private int frames;
    private int segmentFrom;
    private int segmentTo;
    private int currentFrame;
    
    private short[] ambientColor = DEFAULT_AMBIENT_COLOR;
    private Set<Material> materials;
    
    //TODO: to be modified when implementation is finished
    private Loader3DS(){}
    
    public long getMeshVersion(){
        return meshVersion;
    }
    
    public float getMasterScale(){
        return masterScale;
    }
    
    public float[] getConstructionPlane(){
        return constructionPlane;
    }
    
    public int getKeyfRevision(){
        return keyfRevision;
    }
    
    public String getName(){
        return name;
    }
    
    public int getFrames(){
        return frames;
    }
    
    public int getSegmentFrom(){
        return segmentFrom;
    }
    
    public int getSegmentTo(){
        return segmentTo;
    }
    
    public int getCurrentFrame(){
        return currentFrame;
    }
    
    public short[] getAmbientColor(){
        return ambientColor;
    }
    
    @Override
    public boolean isReady() {
        return hasFile();
    }

    @Override
    public MeshFormat getMeshFormat() {
        return MeshFormat.MESH_FORMAT_3DS;
    }

    @Override
    public boolean isValidFile() throws LockedException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LoaderIterator load() throws LockedException, NotReadyException, 
        IOException, LoaderException {
        
        //load first chunk and ensure it is the start chunk
        setLocked(true);
        
        //set reader to start of file
        reader.seek(0);
        long fileLength = file.length();
        boolean endReached = false;
        
        Chunk3DS chunk;
        Chunk3DS nextChunk;
        do{
            chunk = Chunk3DS.load(reader);
            switch(chunk.getChunkId()){
                case Chunk3DS.MDATA:
                    //read mdata
                    readMData(chunk);
                    break;
                case Chunk3DS.M3DMAGIC:
                case Chunk3DS.MLIBMAGIC:
                case Chunk3DS.CMAGIC:
                    do{
                        reader.seek(chunk.getStartStreamPosition() + 6);
                        nextChunk = Chunk3DS.load(reader);
                        switch(nextChunk.getChunkId()){
                            case Chunk3DS.M3D_VERSION:
                                //read file version
                                meshVersion = reader.readUnsignedInt(
                                        EndianType.LITTLE_ENDIAN_TYPE);
                                break;
                            case Chunk3DS.MDATA:
                                //read mdata
                                readMData(nextChunk);
                                break;
                            case Chunk3DS.KFDATA:
                                //read kfdata
                                readKfData(nextChunk);
                                break;
                            default:
                                //ignore unknown chunks
                                break;
                        }                        
                        if(reader.getPosition() >= chunk.getEndStreamPosition())
                            endReached = true;
                        
                        //advance to next chunk
                        reader.seek(nextChunk.getEndStreamPosition());
                    }while(!reader.isEndOfStream() && !endReached);
                    break;
                default:
                    //ignore unknown chunks
                    break;
            }
            if(chunk.getEndStreamPosition() >= fileLength) endReached = true;
            
            //advance to next chunk
            reader.seek(chunk.getEndStreamPosition());
        }while(!reader.isEndOfStream() && !endReached);
        
        //file ended unexpectedly
        if(!endReached) throw new LoaderException();
        
        //TODO: finish
        return null;
    }
    
    
    private void readMData(Chunk3DS chunk) throws IOException, LoaderException{
        if(chunk.getChunkId() != Chunk3DS.MDATA) throw new LoaderException();
        
        //read next chunks
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        do{
            nextChunk = Chunk3DS.load(reader);
            
            switch(nextChunk.getChunkId()){
                case Chunk3DS.MESH_VERSION:
                    //read file version
                    meshVersion = reader.readUnsignedInt(
                            EndianType.LITTLE_ENDIAN_TYPE);
                    break;
                case Chunk3DS.MASTER_SCALE:
                    //read master scale
                    masterScale = reader.readFloat(
                            EndianType.LITTLE_ENDIAN_TYPE);
                    break;
                case Chunk3DS.SHADOW_MAP_SIZE:
                case Chunk3DS.LO_SHADOW_BIAS:
                case Chunk3DS.HI_SHADOW_BIAS:
                case Chunk3DS.SHADOW_SAMPLES:
                case Chunk3DS.SHADOW_RANGE:
                case Chunk3DS.SHADOW_FILTER:
                case Chunk3DS.RAY_BIAS:
                    //TODO: read shadow (for future releases)
                    break;
                case Chunk3DS.VIEWPORT_LAYOUT:
                case Chunk3DS.DEFAULT_VIEW:
                    //TODO: read viewport (for future releases)
                    break;
                case Chunk3DS.O_CONSTS:
                    //read construction plane
                    for(int i = 0; i < CONSTRUCTION_PLANE_COORDS; i++){
                        constructionPlane[i] = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                    }
                    break;
                case Chunk3DS.AMBIENT_LIGHT:
                    //read ambient light
                    readAmbient(nextChunk);
                    break;
                case Chunk3DS.BIT_MAP:
                case Chunk3DS.SOLID_BGND:
                case Chunk3DS.V_GRADIENT:
                case Chunk3DS.USE_BIT_MAP:
                case Chunk3DS.USE_SOLID_BGND:
                case Chunk3DS.USE_V_GRADIENT:
                    //TODO: read background (for future releases)
                    break;
                case Chunk3DS.FOG:
                case Chunk3DS.LAYER_FOG:
                case Chunk3DS.DISTANCE_CUE:
                case Chunk3DS.USE_FOG:
                case Chunk3DS.USE_LAYER_FOG:
                case Chunk3DS.USE_DISTANCE_CUE:
                    //TODO: read atmosphere (for future releases)
                    break;
                case Chunk3DS.MAT_ENTRY:
                    //read material
                    readMaterial(nextChunk);
                    break;
                case Chunk3DS.NAMED_OBJECT:
                    //TODO: read named object
                    readNamedObject(nextChunk);
                    break;
                default:
                    //ignore unknown chunk
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());            
        }while(!reader.isEndOfStream() && !endReached);        
    }
    
    private void readKfData(Chunk3DS chunk) throws IOException, LoaderException{
        if(chunk.getChunkId() != Chunk3DS.KFDATA) throw new LoaderException();
        
        //read next chunks
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        do{
            nextChunk = Chunk3DS.load(reader);
            
            switch(nextChunk.getChunkId()){
                case Chunk3DS.KFHDR:
                    //Read keyf revision, file name and frames
                    keyfRevision = reader.readUnsignedShort(
                            EndianType.LITTLE_ENDIAN_TYPE);
                    byte[] buffer = new byte[12 + 1];
                    byte b;
                    for(int i = 0; i < buffer.length; i++){
                        b = reader.readByte();
                        buffer[i] = b;
                        if(b == 0) break;
                    }
                    name = new String(buffer, Charset.forName("ISO-8859-1 "));
                    frames = reader.readInt(EndianType.LITTLE_ENDIAN_TYPE);
                    break;
                case Chunk3DS.KFSEG:
                    //read segment from & to
                    segmentFrom = reader.readInt(EndianType.LITTLE_ENDIAN_TYPE);
                    segmentTo = reader.readInt(EndianType.LITTLE_ENDIAN_TYPE);
                    break;
                case Chunk3DS.KFCURTIME:
                    //read current frame
                    currentFrame = reader.readInt(
                            EndianType.LITTLE_ENDIAN_TYPE);
                    break;
                case Chunk3DS.VIEWPORT_LAYOUT:
                case Chunk3DS.DEFAULT_VIEW:
                    //TODO: read viewport (for future releases)
                    break;
                case Chunk3DS.AMBIENT_NODE_TAG:
                    //TODO: read ambient node (for future releases)
                    break;
                case Chunk3DS.OBJECT_NODE_TAG:
                    //TODO: read ambient node (for future releases)
                    break;
                case Chunk3DS.CAMERA_NODE_TAG:
                    //TODO: read camera node (for future releases)
                    break;
                case Chunk3DS.TARGET_NODE_TAG:
                    //TODO: read target node (for future releases)
                    break;
                case Chunk3DS.LIGHT_NODE_TAG:
                case Chunk3DS.SPOTLIGHT_NODE_TAG:
                    //TODO: read light (for future releases)
                    break;
                case Chunk3DS.L_TARGET_NODE_TAG:
                    //TODO: read spot node (for future releases)
                    break;
                default:
                    //ignore unknown chunks
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());            
            
        }while(!reader.isEndOfStream() && !endReached);        
    }    
    
    private void readAmbient(Chunk3DS chunk) throws IOException, LoaderException{
        if(chunk.getChunkId() != Chunk3DS.AMBIENT_LIGHT) throw new LoaderException();
        
        //read next chunks
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        boolean haveLin = false;
        do{
            nextChunk = Chunk3DS.load(reader);
            
            switch(nextChunk.getChunkId()){
                case Chunk3DS.LIN_COLOR_F:
                    for(int i = 0; i < 3; i++){
                        ambientColor[i] = (short)(reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE) * 255);
                    }
                    haveLin = true;
                    break;
                case Chunk3DS.COLOR_F:
                    //gamma corrected color chunk replaved in 3ds R3 by
                    //LIN_COLOR_24
                    if(!haveLin){
                        for(int i = 0; i < 3; i++){
                            ambientColor[i] = (short)(reader.readFloat(
                                    EndianType.LITTLE_ENDIAN_TYPE) * 255);
                        }
                    }
                    break;
                default:
                    //ignore unknown chunk
                    break;
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());            
            
        }while(!reader.isEndOfStream() && !endReached); 
    }
    
    private void readMaterial(Chunk3DS chunk) throws LoaderException, 
            IOException{
        
        if(chunk.getChunkId() != Chunk3DS.MAT_ENTRY) 
            throw new LoaderException();
        
        Material3DS material = new Material3DS();
        
        //set default values
        material.setId(materials.size());        
        material.setAmbientColor(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR);
        material.setDiffuseColor(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR);
        material.setSpecularColor(DEFAULT_SPECULAR_COLOR, 
                DEFAULT_SPECULAR_COLOR, DEFAULT_SPECULAR_COLOR);
        
        
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        short[] rgba = new short[4];
        do{
            nextChunk = Chunk3DS.load(reader);
            
            switch(nextChunk.getChunkId()){
                case Chunk3DS.MAT_NAME:
                    byte[] buffer = new byte[64 + 1];
                    byte b;
                    for(int i = 0; i < buffer.length; i++){
                        b = reader.readByte();
                        buffer[i] = b;
                        if(b == 0) break;
                    }
                    String materialName = new String(buffer, 
                            Charset.forName("ISO-8859-1 "));
                    material.setMaterialName(materialName);                    
                    break;
                    
                case Chunk3DS.MAT_AMBIENT:
                    readColor(nextChunk, rgba);
                    material.setAmbientColor(rgba[0], rgba[1], rgba[2]);
                    break;
                case Chunk3DS.MAT_DIFFUSE:
                    readColor(nextChunk, rgba);
                    material.setDiffuseColor(rgba[0], rgba[1], rgba[2]);
                    break;
                case Chunk3DS.MAT_SPECULAR:
                    readColor(nextChunk, rgba);
                    material.setSpecularColor(rgba[0], rgba[1], rgba[2]);                    
                    break;
                case Chunk3DS.MAT_SHININESS:
                    //TODO: shininess is ignored (for future releases)
                    break;
                case Chunk3DS.MAT_SHIN2PCT:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TRANSPARENCY:
                    material.setTransparency((short)(readPercentage(nextChunk) * 
                            (double)MAX_COLOR_VALUE));                    
                    break;
                case Chunk3DS.MAT_XPFALL:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SELF_ILPCT:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_USE_XPFALL:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_REFBLUR:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_USE_REFBLUR:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SHADING:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SELF_ILLUM:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TWO_SIDE:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_DECAL:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_ADDITIVE:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_FACEMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_PHONGSOFT:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_WIRE:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_WIREABS:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_WIRE_SIZE:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TEXMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TEXMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TEX2MAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_TEX2MASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_OPACMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_OPACMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_BUMPMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_BUMPMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SPECMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SPECMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SHINMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SHINMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SELFIMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_SELFIMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_REFLMAP:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_REFLMASK:
                    //TODO: for future releases
                    break;
                case Chunk3DS.MAT_ACUBIC:
                    //TODO: for future releases
                    break;
                default:
                    //ignore unknown chunks
                    break;
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());
            
        }while(!reader.isEndOfStream() && !endReached);         
        materials.add(material);
    }
    
    private void readColor(Chunk3DS chunk, short[] rgba) throws IOException{
        
        //read next chunks
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        boolean haveLin = false;
        do{
            nextChunk = Chunk3DS.load(reader);
            
            switch(nextChunk.getChunkId()){
                case Chunk3DS.LIN_COLOR_24:
                    for(int i = 0; i < 3; i++){
                        rgba[i] = reader.readUnsignedByte();
                    }
                    rgba[3] = 255;
                    haveLin = true;
                    break;
                case Chunk3DS.COLOR_24:
                    //gamma corrected color chunk replaved in 3ds R3 by
                    //LIN_COLOR_24
                    if(!haveLin){
                        for(int i = 0; i < 3; i++){
                            rgba[i] = reader.readUnsignedByte();
                        }
                        rgba[3] = 255;
                    }
                    break;
                case Chunk3DS.LIN_COLOR_F:
                    for(int i = 0; i < 3; i++){
                        rgba[i] = (short)(reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE) * 255.0f);
                    }
                    rgba[3] = 255;
                    haveLin = true;
                    break;
                case Chunk3DS.COLOR_F:
                    if(!haveLin){
                        for(int i = 0; i < 3; i++){
                            rgba[i] = (short)(reader.readFloat(
                                    EndianType.LITTLE_ENDIAN_TYPE) * 255.0f);
                        }
                        rgba[3] = 255;
                    }
                    break;
                default:
                    //ignore unknown chunk
                    break;
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());            
            
        }while(!reader.isEndOfStream() && !endReached);         
    }
    
    private double readPercentage(Chunk3DS chunk) throws IOException{
        
        //read next chunks
        reader.seek(chunk.getStartStreamPosition() + 6);
        boolean endReached = false;
        Chunk3DS nextChunk;        
        do{
            nextChunk = Chunk3DS.load(reader);

            //ignore unknown chunk
            if (nextChunk.getChunkId() == Chunk3DS.INT_PERCENTAGE) {
                return (double) reader.readShort(
                        EndianType.LITTLE_ENDIAN_TYPE) / 100.0;
            }
            
            if(reader.getPosition() >= chunk.getEndStreamPosition())
                endReached = true;
                        
            //advance to next chunk
            reader.seek(nextChunk.getEndStreamPosition());            
            
        }while(!reader.isEndOfStream() && !endReached);
        
        return 100.0;
    }
    
    private void readNamedObject(Chunk3DS chunk){
        //TODO: for future releases
    }
    
}
