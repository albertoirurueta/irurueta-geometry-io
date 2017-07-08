/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.DataChunk
 */
package com.irurueta.geometry.io;

/**
 * Class containing a piece of 3D data loaded from a file.
 * This class is used along with a LoaderIterator so that very large 3D files
 * can be read in a step by step process returning consecutive DataChunk's of 
 * the file
 */
public class DataChunk {
    /**
     * Array containing 3D coordinates for all points in a chunk in consecutive
     * order, that is, array will contain values x0, y0, z0, x1, y1, z1, ...
     * and so on.
     */
    private float[] coords;
    
    /**
     * Array containing the color for each vertex in the chunk of data.
     * Values will be stored consecutively as r0, g0, b0, r1, g1, b1, ...
     * and so on. If transparency is provided, then array will be stored as
     * r0, g0, b0, a0, r1, g1, b1, a1...
     * @see #colorComponents
     */
    private short[] colors;
    
    /**
     * Array containing indices of vertices to build the triangles forming
     * the 3D shape on this chunk. Indices will be stored consecutively in sets
     * of 3, indicating the 3 vertices of each triangle, that is: p1a, p2a, p3a,
     * p1b, p2b, p3b, etc...
     * The indices will be used to pick appropriate point coordinates, texture
     * coordinates, normals or colors from their corresponding arrays
     */
    private int[] indices;
    
    /**
     * Array containing texture coordinates in a texture image for a given 3D
     * point. All texture coordinates are stored consecutively in the array as:
     * x0, y0, x1, y1, etc.
     * Usually coordinates are stored in a normalized form having values between
     * 0.0 and 1.0. Usually larger values indicate that the image will repeat
     * when exceeding its borders, and negative values indicate that the image
     * will be reversed.
     */
    private float[] textureCoords;
    
    /**
     * Array containing normal coordinates for each vertex. Normal coordinates
     * are useful to draw correct lighting. All normal coordinates are stored
     * consecutively in the array as: x0, y0, z0, x1, y1, z1, etc.
     */
    private float[] normals;
    
    /**
     * Indicates number of color components stored in the array.
     * Usually for RGB it will be 3. When transparency is available, it will be 
     * 4.
     */
    private int colorComponents;
    
    /**
     * X minimum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float minX;
    
    /**
     * Y minimum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float minY;
    
    /**
     * Z minimum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float minZ;
    
    /**
     * X maximum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float maxX;
    
    /**
     * Y maximum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float maxY;
    
    /**
     * Z maximum coordinate of the bounding box containing all the data of this
     * data chunk
     */
    private float maxZ;
    
    /**
     * Material of this chunk. This will be used to define ambient colors, 
     * textures etc for this chunk.
     */
    private Material material;
    
    /**
     * Constant defining minimum number of color components, which is 1 for
     * grayscale
     */
    public static int MIN_COLOR_COMPONENTS = 1;
    
    /**
     * Constant defining default number of color components for RGB
     */
    public static int DEFAULT_COLOR_COMPONENTS = 3;
    
    /**
     * Default Constructor
     */
    public DataChunk(){
        coords = null;
        colors = null;
        indices = null;
        textureCoords = null;
        normals = null;
        
        colorComponents = DEFAULT_COLOR_COMPONENTS;
        
        minX = minY = minZ = Float.MAX_VALUE;
        maxX = maxY = maxZ = -Float.MAX_VALUE;
    }
    
    /**
     * Sets array containing 3D coordinates for all points in a chunk in 
     * consecutive order, that is, array will contain values x0, y0, z0, x1, y1, 
     * z1, ... and so on.
     * @param coords Array containing 3D coordinates for all points in a chunk
     */
    public void setVerticesCoordinatesData(float[] coords){
        this.coords = coords;
    }
    
    /**
     * Returns array containing 3D coordinates for all points in a chunk in
     * consecutive order, that is, array will contain values x0, y0, z0, x1, y1,
     * z1... and so on.
     * @return Array containing 3D coordinates for all points in a chunk
     */
    public float[] getVerticesCoordinatesData(){
        return coords;
    }
    
    /**
     * Indicates if array containing 3D coordinates has been provided and is 
     * available for retrieval.
     * @return True if available, false otherwise.
     */
    public boolean isVerticesCoordinatesDataAvailable(){
        return coords != null;
    }
    
    /**
     * Sets array containing the color for each vertex in the chunk of data.
     * Values will be stored consecutively as r0, g0, b0, r1, g1, b1, ...
     * and so on. If transparency is provided, then array will be stored as
     * r0, g0, b0, a0, r1, g1, b1, a1...
     * @param colors Array containing the color for each vertex to be set
     * @see #getColorComponents()
     */
    public void setColorData(short[] colors){
        this.colors = colors;
    }
    
    /**
     * Returns array containing the color for each vertex in the chunk of data.
     * Array usually contains data stored consecutively as 
     * r0, g0, b0, r1, g1, b1... and so on. If transparency is used, then array 
     * will be stored as r0, g0, b0, a0, r1, g1, b1, a1...
     * There will be as many values per vertex as the number of color components
     * @return array containing the color for each vertex in the chunk of data
     */
    public short[] getColorData(){
        return colors;
    }
    
    /**
     * Indicates if array containing the color for each vertex has been provided
     * and is available for retrieval.
     * @return True if available, false otherwise.
     */
    public boolean isColorDataAvailable(){
        return colors != null;
    }
    
    /**
     * Sets array that contains indices of vertices to build the triangles 
     * forming the 3D shape on this chunk. Indices need to be stored 
     * consecutively in sets of 3, indicating the 3 vertices of each triangle, 
     * that is: p1a, p2a, p3a, p1b, p2b, p3b, etc.
     * Provided indices will be used to pick appropriate point coordinates,
     * texture coordinates, normals or colors from their corresponding arrays
     * @param indices indices of vertices to build triangles.
     */    
    public void setIndicesData(int[] indices){
        this.indices = indices;
    }
    
    /**
     * Returns array containing indices of vertices to build the triangles 
     * forming the 3D shape on this chunk. Indices will be stored consecutively
     * in sets of 3, indicating the 3 vertices of each triangle, that is: p1a,
     * p2a, p3a, p1b, p2b, p3b, etc.
     * Provided indices will be used to pick appropriate point coordinates,
     * texture coordinates, normals or colors from their corresponding arrays.
     * @return Array containing indices of vertices to build the triangles.
     */
    public int[] getIndicesData(){
        return indices;
    }
    
    /**
     * Indicates if array containing the indices of vertices to build the 
     * triangles has been provided and is available for retrieval.
     * @return True if available, false otherwise.
     */
    public boolean isIndicesDataAvailable(){
        return indices != null;
    }
    
    /**
     * Sets array containing texture coordinates in a texture image for a given 
     * 3D point. All texture coordinates are stored consecutively in the array 
     * as: x0, y0, x1, y1, etc.
     * Usually coordinates are stored in a normalized form having values between
     * 0.0 and 1.0. Usually larger values indicate that the image will repeat
     * when exceeding its borders, and negative values indicate that the image
     * will be reversed.
     * @param textureCoords Array containing texture coordinates in a texture
     * image
     */
    public void setTextureCoordinatesData(float[] textureCoords){
        this.textureCoords = textureCoords;
    }
    
    /**
     * Returns array containing texture coordinates in a texture image for a 
     * given 3D point. All texture coordinates are stored consecutively in the 
     * array as: x0, y0, x1, y1, etc.
     * Usually coordinates are stored in a normalized form having values between
     * 0.0 and 1.0. Usually larger values indicate that the image will repeat
     * when exceeding its borders, and negative values indicate that the image
     * will be reversed.
     * @return Array containing texture coordinates in a texture image
     */
    public float[] getTextureCoordiantesData(){
        return textureCoords;
    }
    
    /**
     * Indicates if array containing texture coordinates in a texture image has
     * been provided and is available for retrieval.
     * @return True if available, false otherwise.
     */
    public boolean isTextureCoordinatesDataAvailable(){
        return textureCoords != null;
    }
    
    /**
     * Sets array containing normal coordinates for each vertex. Normal 
     * coordinates are useful to draw correct lighting. All normal coordinates 
     * are stored consecutively in the array as: x0, y0, z0, x1, y1, z1, etc.
     * @param normals Array containing normal coordinates for each vertex.
     */
    public void setNormalsData(float[] normals){
        this.normals = normals;
    }
    
    /**
     * Returns array containing normal coordinates for each vertex. Normal 
     * coordinates are useful to draw correct lighting. All normal coordinates 
     * are stored consecutively in the array as: x0, y0, z0, x1, y1, z1, etc.
     * @return Array containing normal coordinates for each vertex.
     */
    public float[] getNormalsData(){
        return normals;
    }
    
    /**
     * Indicates if array containing normal coordinates for each vertex has
     * been provided and is available for retrieval.
     * @return True if available, false otherwise.
     */
    public boolean isNormalsDataAvailable(){
        return normals != null;
    }
    
    /**
     * Returns number of color components stored in the array.
     * Usually for RGB it will be 3. When transparency is available, it will be 
     * 4. For gray-scale it will be 1.
     * @return Number of color components
     */
    public int getColorComponents(){
        return colorComponents;
    }
    
    /**
     * Sets number of color components stored in the array.
     * Usually for RGB it will be 3. When transparency is available, it will be
     * 4. For gray-scale it will be 1.
     * @param colorComponents Number of color components stored in the array.
     * @throws IllegalArgumentException Raised if provided color components is
     * negative.
     */
    public void setColorComponents(int colorComponents) 
            throws IllegalArgumentException{
        if(colorComponents < MIN_COLOR_COMPONENTS) 
            throw new IllegalArgumentException();
        
        this.colorComponents = colorComponents;
    }

    /**
     * Returns X minimum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return X minimum coordinate of the bounding box.
     */
    public float getMinX(){
        return minX;
    }
    
    /**
     * Sets X minimum coordinate of the bounding box containing all the data of
     * this data chunk
     * @param minX X minimum coordinate of the bounding box.
     */
    public void setMinX(float minX){
        this.minX = minX;
    }
    
    /**
     * Returns Y minimum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return Y minimum coordinate of the bounding box.
     */
    public float getMinY(){
        return minY;
    }

    /**
     * Sets Y minimum coordinate of the bounding box containing all the data of
     * this data chunk
     * @param minY Y minimum coordinate of the bounding box.
     */    
    public void setMinY(float minY){
        this.minY = minY;
    }
    
    /**
     * Returns Z minimum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return Z minimum coordinate of the bounding box.
     */    
    public float getMinZ(){
        return minZ;
    }
    
    /**
     * Sets Z minimum coordinate of the bounding box containing all the data of
     * this data chunk
     * @param minZ Z minimum coordinate of the bounding box.
     */        
    public void setMinZ(float minZ){
        this.minZ = minZ;
    }
    
    /**
     * Returns X maximum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return X maximum coordinate of the bounding box.
     */    
    public float getMaxX(){
        return maxX;
    }

    /**
     * Sets X maximum coordinate of the bounding box containing all the data of
     * this data chunk
     * @param maxX X maximum coordinate of the bounding box.
     */    
    public void setMaxX(float maxX){
        this.maxX = maxX;
    }
    
    /**
     * Returns Y maximum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return Y maximum coordinate of the bounding box.
     */        
    public float getMaxY(){
        return maxY;
    }

    /**
     * Sets Y maximum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @param maxY Y maximum coordinate of the bounding box.
     */        
    public void setMaxY(float maxY){
        this.maxY = maxY;
    }

    /**
     * Returns Z maximum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @return Z maximum coordinate of the bounding box.
     */            
    public float getMaxZ(){
        return maxZ;
    }
    
    /**
     * Sets Z maximum coordinate of the bounding box containing all the data 
     * of this data chunk
     * @param maxZ Z maximum coordinate of the bounding box.
     */            
    public void setMaxZ(float maxZ){
        this.maxZ = maxZ;
    }
    
    /**
     * Returns material of this chunk. This will be used to define ambient 
     * colors, textures, etc for this chunk.
     * @return Materials of this chunk.
     */
    public Material getMaterial(){
        return material;
    }
    
    /**
     * Sets material of this chunk. This will be used to define ambient colors,
     * textures, etc for this chunk.
     * @param material Material of this chunk.
     */
    public void setMaterial(Material material){
        this.material = material;
    }
    
    /**
     * Returns boolean indicating whether a material has been provided for this
     * chunk
     * @return True if material is available, false otherwise
     */
    public boolean isMaterialAvailable(){
        return material != null;
    }

    //TODO: create class to compute DataChunk statistics: bounding box, vertex
    //average, color limits, standard deviation and average, normal limits,
    //standard deviation and average, etc.
}
