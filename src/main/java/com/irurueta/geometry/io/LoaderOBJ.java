/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.LoaderOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 7, 2012
 */
package com.irurueta.geometry.io;

import com.irurueta.geometry.Point3D;
import com.irurueta.geometry.Triangle3D;
import com.irurueta.geometry.Triangulator3D;
import com.irurueta.geometry.TriangulatorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Loads an OBJ file.
 * If a LoaderListenerOBJ is provided, this class might also attempt to load the
 * associated material file if available.
 */
public class LoaderOBJ extends Loader{

    /**
     * Iterator to load OBJ file data in small chunks.
     * Usually data is divided in chunks that can be directly loaded by
     * graphic layers such as OpenGL
     */
    private LoaderIteratorOBJ loaderIterator;
    
    /**
     * Maximum number of vertices allowed in a chunk. Once this value is 
     * exceeded when loading a file, a new chunk of data is created
     */
    private int maxVerticesInChunk;
    
    /**
     * To allow faster file loading, it might be allowed to repeat points in a
     * chunk. When representing data graphically, this has no visual 
     * consequences but chunks will take up more memory. This value represents
     * a tradeoff between loading speed and memory usage
     */
    private boolean allowDuplicateVerticesInChunk;
    
    /**
     * Maximum number of file stream positions to be cached.
     * This class keeps a cache of positions in the file to allow faster file 
     * loading at the expense of larger memory usage.
     * If the geometry of a file reuses a large number of points, keeping a 
     * large cache will increase the speed of loading a file, otherwise the
     * impact of this parameter will be low.
     * The default value will work fine for most cases.
     */
    private long maxStreamPositions;
    
    /**
     * List containing comments contained in the file
     */
    private List<String> comments;    
    
    /**
     * Collection of materials contained in the material's file associated to an
     * OBJ file.
     */
    private Set<Material> materials;
    
    /**
     * Indicates if garbage collection should be attempted from time to time
     * to reduce memory usage. Garbage collection might slow down slightly the
     * loading process but in memory constrained environments this setting 
     * should be enabled.
     * By default it is enabled.
     */
    private boolean periodicGarbageCollection;
    
    /**
     * Number of attempts to clean memory before garbage collection is actually
     * demanded. This only takes effect if periodic garbage collection is 
     * enabled. By default this is set to 10000 attempts.
     */
    private int itersBeforeGarbageCollection;
    
    /**
     * Determines if file loading should continue even if the triangulation of
     * a polygon fails. The triangulation of a polygon might fail if the polygon
     * is degenerate or has invalid numerical values such as NaN of infinity.
     * If true, loading will continue but the result will lack the polygons that
     * failed
     */
    private boolean continueIfTriangulationError;
    
    /**
     * Constant defining the default value of maximum number of vertices to keep
     * in a chunk. This is 65535, which corresponds to the maximum value allowed
     * by graphical layer such as OpenGL when working with Vertex Buffer Objects
     */
    public static final int DEFAULT_MAX_VERTICES_IN_CHUNK = 0xffff;
    
    /**
     * Minimum allowed value for maximum number of vertices in chunk, which is 
     * one
     */
    public static final int MIN_MAX_VERTICES_IN_CHUNK = 1;
    
    /**
     * Constant indicating that duplicated vertices are allowed by default, 
     * which allows faster loading
     */
    public static final boolean DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK =
            true;
    
    /**
     * Maximum number of stream positions to be cached by default
     */
    public static final int DEFAULT_MAX_STREAM_POSITIONS = 1000000;
    
    /**
     * Minimum allowed number of stream positions
     */
    public static final int MIN_STREAM_POSITIONS = 1;
    
    /**
     * Amount of progress variation (1%) used to notify progress
     */
    public static final float PROGRESS_DELTA = 0.01f;
    
    /**
     * Indicates that loading should continue even if triangulation of some
     * polygons fails.
     */
    public static final boolean DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR = true;
    
    /**
     * Constant below enabled periodic garbage collection. This helps to reduce
     * memory usage, which is of special interest on mobile devices and servers
     */
    public static final boolean DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION = 
            true;
    
    /**
     * Constant defining number of iterations to wait before starting garbage
     * collection.
     * The lower the value the more frequent garbage collection will be and 
     * hence the smaller the memory usage, at the expense of slower execution.
     */
    public static final int DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION = 10000;
    
    
    /**
     * Constructor
     */
    public LoaderOBJ(){
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk = 
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(int maxVerticesInChunk) throws IllegalArgumentException{
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(int maxVerticesInChunk, 
            boolean allowDuplicateVerticesInChunk) 
            throws IllegalArgumentException{
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @param maxStreamPositions Maximum number of file stream positions to be 
     * cached.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk, long maxStreamPositions)
            throws IllegalArgumentException{
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        internalSetMaxStreamPositions(maxStreamPositions);
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f) throws IOException{
        super(f);
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, int maxVerticesInChunk) 
            throws IllegalArgumentException, IOException{
        super(f);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk)
            throws IllegalArgumentException, IOException{
        super(f);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @param maxStreamPositions Maximum number of file stream positions to be 
     * cached.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk, long maxStreamPositions)
            throws IllegalArgumentException, IOException{
        super(f);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        internalSetMaxStreamPositions(maxStreamPositions);
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     */
    public LoaderOBJ(LoaderListener listener){
        super(listener);
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk = 
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(LoaderListener listener, int maxVerticesInChunk)
            throws IllegalArgumentException{
        super(listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(LoaderListener listener, int maxVerticesInChunk, 
            boolean allowDuplicateVerticesInChunk) 
            throws IllegalArgumentException{
        super(listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @param maxStreamPositions Maximum number of file stream positions to be 
     * cached.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     */
    public LoaderOBJ(LoaderListener listener, int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk, long maxStreamPositions)
            throws IllegalArgumentException{
        super(listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        internalSetMaxStreamPositions(maxStreamPositions);
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, LoaderListener listener) throws IOException{
        super(f, listener);
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, LoaderListener listener, int maxVerticesInChunk) 
            throws IllegalArgumentException, IOException{
        super(f, listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        allowDuplicateVerticesInChunk =
                DEFAULT_ALLOW_DUPLICATE_VERTICES_IN_CHUNK;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, LoaderListener listener, int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk)
            throws IllegalArgumentException, IOException{
        super(f, listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        maxStreamPositions = DEFAULT_MAX_STREAM_POSITIONS;
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }
    
    /**
     * Constructor
     * @param f file to be loaded
     * @param listener listener to be notified of loading progress and when
     * loading process starts or finishes
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created
     * @param allowDuplicateVerticesInChunk  indicates if repeated vertices in a
     * chunk are allowed to provide faster file loading. When representing data 
     * graphically, this has no visual consequences but chunks will take up more
     * memory. 
     * @param maxStreamPositions Maximum number of file stream positions to be 
     * cached.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     * a chunk is lower than 1
     * @throws IOException if an I/O error occurs
     */
    public LoaderOBJ(File f, LoaderListener listener, int maxVerticesInChunk,
            boolean allowDuplicateVerticesInChunk, long maxStreamPositions)
            throws IllegalArgumentException, IOException{
        super(f, listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
        this.allowDuplicateVerticesInChunk = allowDuplicateVerticesInChunk;
        internalSetMaxStreamPositions(maxStreamPositions);
        comments = new LinkedList<String>();
        continueIfTriangulationError = DEFAULT_CONTINUE_IF_TRIANGULATION_ERROR;
        
        periodicGarbageCollection = DEFAULT_ENABLE_PERIODIC_GARBAGE_COLLECTION;
        itersBeforeGarbageCollection = DEFAULT_ITERS_BEFORE_GARBAGE_COLLECTION;        
    }    
    
    /**
     * Sets maximum number of vertices allowed in a chunk. 
     * Once this value is exceeded when loading a file, a new chunk of data is 
     * created.
     * @param maxVerticesInChunk maximum allowed number of vertices to be set
     * @throws IllegalArgumentException if provided value is lower than 1
     * @throws LockedException if this loader is currently loading a file
     */
    public void setMaxVerticesInChunk(int maxVerticesInChunk)
            throws IllegalArgumentException, LockedException{
        if(isLocked()) throw new LockedException();
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }
    
    /**
     * Returns maximum number of vertices allowed in a chunk.
     * Once this value is exceeded when loading a file, a new chunk of data is
     * created.
     * @return maximum number of vertices allowed in a chunk
     */
    public int getMaxVerticesInChunk(){
        return maxVerticesInChunk;
    }
    
    /**
     * Internal method to set maximum number of vertices allowed in a chunk.
     * This method is reused both in the constructor and in the setter of
     * maximum number of vertices allowed in a chunk
     * @param maxVerticesInChunk maximum allowed number of vertices to be set
     * @throws IllegalArgumentException  if provided value is lower than 1
     */
    private void internalSetMaxVerticesInChunk(int maxVerticesInChunk)
            throws IllegalArgumentException{
        if(maxVerticesInChunk < MIN_MAX_VERTICES_IN_CHUNK)
            throw new IllegalArgumentException();
        
        this.maxVerticesInChunk = maxVerticesInChunk;
    }
    
    /**
     * Sets boolean indicating if repeated vertices in a chunk are allowed to 
     * provide faster file loading. When representing data graphically, this has
     * no visual consequences but chunks will take up more memory. 
     * @param allow true if duplicate vertices are allowed, false otherwise
     * @throws LockedException if this loader is currently loading a file
     */
    public void setAllowDuplicateVerticesInChunk(boolean allow)
            throws LockedException{
        if(isLocked()) throw new LockedException();
        allowDuplicateVerticesInChunk = allow;
    }
    
    /**
     * Returns boolean indicating if repeated vertices in a chunk are allowed to
     * provide faster file loading. When representing data graphically, this has
     * no visual consequences but chunks will take up more memory.
     * @return true if duplicate vertices are allowed, false otherwise
     */
    public boolean areDuplicateVerticesInChunkAllowed(){
        return allowDuplicateVerticesInChunk;
    }
    
    /**
     * Sets maximum number of file stream positions to be cached.
     * This class keeps a cache of positions in the file to allow faster file 
     * loading at the expense of larger memory usage.
     * If the geometry of a file reuses a large number of points, keeping a 
     * large cache will increase the speed of loading a file, otherwise the
     * impact of this parameter will be low.
     * The default value will work fine for most cases.
     * @param maxStreamPositions maximum number of file stream positions to be
     * set
     * @throws IllegalArgumentException if provided value is lower than 1
     * @throws LockedException if this loader is currently loading a file
     */
    public void setMaxStreamPositions(long maxStreamPositions)
            throws IllegalArgumentException, LockedException{
        if(isLocked()) throw new LockedException();
        internalSetMaxStreamPositions(maxStreamPositions);        
    }
    
    /**
     * Returns maximum number of file stream positions to be cached.
     * This class keeps a cache of positions in the file to allow faster file 
     * loading at the expense of larger memory usage.
     * If the geometry of a file reuses a large number of points, keeping a 
     * large cache will increase the speed of loading a file, otherwise the
     * impact of this parameter will be low.
     * The default value will work fine for most cases.
     * @return maximum number of file stream positions to be cached
     */
    public long getMaxStreamPositions(){
        return maxStreamPositions;
    }
        
    /**
     * Internal method to set maximum number of file stream positions to be 
     * cached
     * This method is reused both in the constructor and in the setter of
     * maximum number stream positions
     * @param maxStreamPositions maximum number of file stream positions to be 
     * cached
     * @throws IllegalArgumentException if provided value is lower than 1
     */
    private void internalSetMaxStreamPositions(long maxStreamPositions)
            throws IllegalArgumentException{
        if(maxStreamPositions < MIN_STREAM_POSITIONS)
            throw new IllegalArgumentException();
        
        this.maxStreamPositions = maxStreamPositions;
    }
    
    /**
     * Returns boolean indicating if file loading should continue even if the 
     * triangulation of a polygon fails. The triangulation of a polygon might 
     * fail if the polygon is degenerate or has invalid numerical values such as 
     * NaN of infinity.
     * @return If true, loading will continue but the result will lack the 
     * polygons that failed
     */
    public boolean isContinueIfTriangulationError(){
        return continueIfTriangulationError;
    }
    
    /**
     * Sets boolean indicating if file loading should continue even if the
     * triangulation of a polygon fails. The triangulation of a polygon might
     * fail if the polygon is degenerate or has invalid numerical values such as
     * NaN or infinity.
     * @param continueIfTriangulationError if ture, loading will continue but 
     * the result will lack the polygons that failed
     */
    public void setContinueIfTriangulationError(
            boolean continueIfTriangulationError){
        this.continueIfTriangulationError = continueIfTriangulationError;
    }
    
    /**
     * Returns a list of the comments contained in the file
     * @return list of the comments contained in the file
     */
    public List<String> getComments(){
        return Collections.unmodifiableList(comments);
    }

    /**
     * Indicates if garbage collection should be attempted from time to time
     * to reduce memory usage. Garbage collection might slow down slightly the
     * loading process but in memory constrained environments this setting 
     * should be enabled. By default it is enabled.
     * @return True if periodic garbage collection is enabled, false otherwise.
     */
    public boolean isPeriodicGarbageCollection(){
        return periodicGarbageCollection;
    }
    
    /**
     * Sets whether periodic garbage collection should be attempted from time to
     * time to reduce memory usage. Garbage collection might slow down slightly
     * the loading process but in memory constrained environments this setting
     * should be enabled. By default it is enabled.
     * @param periodicGarbageCollection True if periodic garbage collection must
     * be enabled, false otherwise
     * @throws LockedException Raised if this instance is locked because loading
     * is in progress.
     */
    public void setPeriodicGarbageCollection(boolean periodicGarbageCollection)
            throws LockedException{
        if(isLocked()) throw new LockedException();
        
        this.periodicGarbageCollection = periodicGarbageCollection;
    }

    /**
     * Returns number of attempts to clean memory before garbage collection is 
     * actually demanded. This only takes effect if periodic garbage collection 
     * is  enabled. By default this is set to 10000 attempts.
     * @return Number of attempts to clean memory before garbage collection is
     * actually demanded.
     */
    public int getItersBeforeGarbageCollection(){
        return itersBeforeGarbageCollection;
    }
    
    /**
     * Sets number of attempts to clean memory before garbage collection is 
     * actually demanded. This only takes effect if periodic garbage collection
     * is enabled. By default this is set to 10000 attempts.
     * @param itersBeforeGarbageCollection number of iterations before actually claiming grbage collection.
     * @throws IllegalArgumentException if provided value is negative
     * @throws LockedException Raised if this instance is locked because loading
     * is in progress.
     */
    public void setItersBeforeGarbageCollection(
            int itersBeforeGarbageCollection) throws IllegalArgumentException,
            LockedException{
        if(isLocked()) throw new LockedException();
        
        if(itersBeforeGarbageCollection < 0) 
            throw new IllegalArgumentException();
        
        this.itersBeforeGarbageCollection = itersBeforeGarbageCollection;
    }        
    
    /**
     * If loader is ready to start loading a file.
     * This is true once a file has been provided.
     * @return true if ready to start loading a file, false otherwise
     */    
    @Override
    public boolean isReady() {
        return hasFile();
    }

    /**
     * Returns mesh format supported by this class, which is MESH_FORMAT_OBJ
     * @return mesh format supported by this class
     */    
    @Override
    public MeshFormat getMeshFormat() {
        return MeshFormat.MESH_FORMAT_OBJ;
    }

    /**
     * Determines if provided file is a valid file that can be read by this 
     * loader
     * @return true if file is valid, false otherwise
     * @throws LockedException raised if this instance is already locked
     * @throws IOException if an I/O error occurs.
     */    
    @Override
    public boolean isValidFile() throws LockedException, IOException {
        if(!hasFile()) throw new IOException();
        if(isLocked()) throw new LockedException();
        return true;
    }

    /**
     * Starts the loading process of provided file.
     * This method returns a LoaderIterator to start the iterative process to
     * load a file in small chunks of data.
     * @return a loader iterator to read the file in a step by step process
     * @throws LockedException raised if this instance is already locked
     * @throws NotReadyException raised if this instance is not yet ready
     * @throws IOException if an I/O error occurs
     * @throws LoaderException if file is corrupted or cannot be interpreted.
     */        
    @Override
    public LoaderIterator load() throws LockedException, NotReadyException, 
        IOException, LoaderException {
        
        if(isLocked()) throw new LockedException();
        if(!isReady()) throw new NotReadyException();
        
        setLocked(true);
        if(listener != null) listener.onLoadStart(this);
        
        loaderIterator = new LoaderIteratorOBJ(this);
        loaderIterator.setListener(new LoaderIteratorListenerImpl(this));
        return loaderIterator;
    }
    
    /**
     * Internal listener to be notified when loading process finishes.
     * This listener is used to free resources when loading process finishes
     */    
    private class LoaderIteratorListenerImpl implements LoaderIteratorListener{

        /**
         * Reference to Loader loading an OBJ file
         */
        private LoaderOBJ loader;
        
        /**
         * Constructor
         * @param loader reference to Loader
         */
        public LoaderIteratorListenerImpl(LoaderOBJ loader){
            this.loader = loader;
        }
        
        /**
         * Method to be notified when the loading process finishes
         * @param iterator iterator loading the file in chunks
         */        
        @Override
        public void onIteratorFinished(LoaderIterator iterator) {
            //because iterator is finished, we should allow subsequent calls to 
            //load method
            try{
                reader.seek(0); //attempt restart stream to initial position
            }catch(Throwable t){}


            //on subsequent calls
            if(listener != null) listener.onLoadEnd(loader);
            setLocked(false);
        }        
    }

    /**
     * Loader iterator in charge of loading file data in small chunks.
     * Usually data is divided in chunks small enough that can be directly 
     * loaded by graphical layers such as OpenGL (which has a limit of 65535 
     * indices when using Vertex Buffer Objects, which increase graphical 
     * performance)
     */    
    private class LoaderIteratorOBJ implements LoaderIterator{

        /**
         * Reference to loader loading OBJ file
         */
        private LoaderOBJ loader;
        
        /**
         * X coordinate of the latest point that has been read
         */
        private float coordX;
        
        /**
         * Y coordinate of the latest point that has been read
         */
        private float coordY;
        
        /**
         * Z coordinate of the latest point that has been read
         */
        private float coordZ;
        
        /**
         * U texture coordinate of the latest point that has been read.
         * U coordinate refers to the horizontal axis in the texture image and
         * usually is a normalized value between 0.0 and 1.0. Larger values can
         * be used to repeat textures, negative values can be used to reverse
         * textures
         */
        private float textureU;
        
        /**
         * V texture coordinate of the latest point that has been read.
         * V coordinate refers to the vertical axis in the texture image and
         * usually is a normalized value between 0.0 and 1.0. Larger values can
         * be used to repeat textures, negative values can be used to reverse
         * textures
         */
        private float textureV;
        
        /**
         * X coordinate of the latest point normal that has been read
         */
        private float nX;
        
        /**
         * Y coordinate of the latest point normal that has been read
         */
        private float nY;
        
        /**
         * Z coordinate of the latest point normal that has been read
         */
        private float nZ;
        
        /**
         * Vertex index in the file of the latest point that has been read
         */
        private int vertexIndex;
        
        /**
         * Texture index in the file of the latest point that has been read
         */
        private int textureIndex;
        
        /**
         * Normal index in the file of the latest point that has been read
         */
        private int normalIndex;
        
        //coordinates for bounding box in a chunk
        /**
         * X coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float minX;
        
        /**
         * Y coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float minY;
        
        /**
         * Z coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float minZ;
        
        /**
         * X coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float maxX;
        
        /**
         * Y coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float maxY;
        
        /**
         * Z coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled
         */
        private float maxZ;
        
        /**
         * Indicates if vertices have been loaded and must be added to current
         * chunk being loaded
         */
        private boolean verticesAvailable;
        
        /**
         * Indicates if texture coordinates have been loaded and must be added
         * to current chunk being loaded
         */
        private boolean textureAvailable;
        
        /**
         * Indicates if normals have been loaded and must be added to current
         * chunk being loaded
         */
        private boolean normalsAvailable;
        
        /**
         * Indicates if indices have been loaded and must be added to current
         * chunk being loaded
         */
        private boolean indicesAvailable;
        
        /**
         * Indicates if materials have been loaded and must be added to current
         * chunk being loaded
         */
        private boolean materialsAvailable;
        
        /**
         * Number of vertices that have been loaded in current chunk
         */
        private long numberOfVertices;
        
        /**
         * Number of texture coordinates that have been loaded in current chunk
         */
        private long numberOfTextureCoords;
        
        /**
         * Number of normals that have been loaded in current chunk
         */
        private long numberOfNormals;
        
        /**
         * Number of faces (i.e. polygons) that have been loaded in current 
         * chunk
         */
        private long numberOfFaces;
        
        /**
         * Index of current face (i.e. polygon) that has been loaded
         */
        private long currentFace;
        
        /**
         * Position of first vertex in the file. This is stored to reduce 
         * fetching time when parsing the OBJ file
         */
        private long firstVertexStreamPosition;
        
        /**
         * Indicates if first vertex position has been found
         */
        private boolean firstVertexStreamPositionAvailable;
        
        /**
         * Position of first texture coordinate in the file. This is stored to
         * reduce fetching time when parsing the OBJ file
         */
        private long firstTextureCoordStreamPosition;
        
        /**
         * Indicates if first texture coordinate has been found
         */
        private boolean firstTextureCoordStreamPositionAvailable;
        
        /**
         * Position of first normal coordinate in the file. This is stored to
         * reduce fetching time when parsing the OBJ file
         */
        private long firstNormalStreamPosition;
        
        /**
         * Indicates if first normal coordinate has been found
         */
        private boolean firstNormalStreamPositionAvailable;
        
        /**
         * Position of first face (i.e. polygon) in the file. This is stored to
         * reduce fetching time when parsing the OBJ file
         */
        private long firstFaceStreamPosition;
        
        /**
         * Indicates if first face has been found
         */
        private boolean firstFaceStreamPositionAvailable;
        
        /**
         * Indicates location of first material in the file. This is stored to
         * reduce fetching time when parsing the OBJ file
         */
        private long firstMaterialStreamPosition;
        
        /**
         * Indicates if first material has been found
         */
        private boolean firstMaterialStreamPositionAvailable;
        
        /**
         * Contains position where file is currently being loaded
         */
        private long currentStreamPosition;
        
        /**
         * Reference to the listener of this loader iterator. This listener will
         * be notified when the loading process finishes so that resources can
         * be freed
         */        
        private LoaderIteratorListener listener;
        
        /**
         * Array containing vertices coordinates to be added to current chunk
         * of data
         */
        private float[] coordsInChunkArray;
        
        /**
         * Array containing texture coordinates to be added to current chunk of
         * data
         */
        private float[] textureCoordsInChunkArray;
        
        /**
         * Array containing normal coordinates to be added to current chunk of
         * data
         */
        private float[] normalsInChunkArray;        
        
        /**
         * Array containing indices to be added to current chunk of data. Notice
         * that this indices are not the original indices appearing in the file.
         * Instead, they are indices referring to data in current chunk, 
         * accounting for duplicate points, etc. This way, indices in a chunk 
         * can be directly used to draw the chunk of data by the graphical layer
         */
        private int[] indicesInChunkArray;
        
        /**
         * Array containing vertex indices as they appear in the OBJ file.
         * These indices are only used to fetch data, they will never appear in
         * resulting chunk of data
         */
        private long[] originalVertexIndicesInChunkArray;
        
        /**
         * Array containing texture indices as they appear in the OBJ file.
         * These indices are only used to fetch data, they will never appear in
         * resulting chunk of data
         */
        private long[] originalTextureIndicesInChunkArray;
        
        /**
         * Array containing normal indices as they appear in the OBJ file.
         * These indices are only used to fetch data, they will never appear in
         * resulting chunk of data
         */
        private long[] originalNormalIndicesInChunkArray;
        
        /**
         * Map to relate vertex indices in a file respect to chunk indices
         */
        private TreeMap<Long, Integer> vertexIndicesMap;
        
        /**
         * Map to relate texture coordinates indices in a file respect to chunk
         * indices
         */
        private TreeMap<Long, Integer> textureCoordsIndicesMap;
        
        /**
         * Map to relate normals coordinates indices in a file respect to chunk
         * indices
         */
        private TreeMap<Long, Integer> normalsIndicesMap;
        
        /**
         * Map to cache vertex positions in a file
         */
        private TreeMap<Long, Long> verticesStreamPositionMap;
        
        /**
         * Map to cache texture coordinates positions in a file
         */
        private TreeMap<Long, Long> textureCoordsStreamPositionMap;
        
        /**
         * Map to cache normals coordinates positions in a file
         */
        private TreeMap<Long, Long> normalsStreamPositionMap;
        
        /**
         * Number of vertices stored in chunk
         */
        private int verticesInChunk;
        
        /**
         * Number of indices stored in chunk
         */
        private int indicesInChunk;
        
        /**
         * Size of indices stored in chunk
         */
        private int indicesInChunkSize;
        
        /**
         * Vertex position in file
         */
        private long vertexStreamPosition;
        
        /**
         * Texture coordinate position in file
         */
        private long textureCoordStreamPosition;
        
        /**
         * Normal coordinate position in file
         */
        private long normalStreamPosition;
        
        /**
         * Name of current material of data being loaded
         */
        private String currentChunkMaterialName;
        
        /**
         * Reference to current material of data being loaded
         */
        private MaterialOBJ currentMaterial;
        
        /**
         * Reference to material loader in charge of loading the associated MTL
         * of file to this OBJ file.
         */
        private MaterialLoaderOBJ materialLoader;
        
        /**
         * Counter of number of times that garbage collection has been 
         * requested. When this value exceeds the itersBeforeGarbageCollection,
         * then an actual garbage collection is requested as long as this 
         * feature is enabled.
         */
        private int gcCounter;
        
        /**
         * Constructor
         * @param loader reference to loader loading binary file
         * @throws IOException if an I/O error occurs
         * @throws LoaderException if file data is corrupt or cannot be 
         * understood
         */
        public LoaderIteratorOBJ(LoaderOBJ loader) throws IOException, 
                LoaderException{
            this.loader = loader;
            coordX = coordY = coordZ = 0.0f;
            textureU = textureV = 0.0f;
            nX = nY = nZ = 1.0f;
            vertexIndex = textureIndex = normalIndex = 0;
            verticesAvailable = textureAvailable = normalsAvailable = 
                    indicesAvailable = materialsAvailable = false;
            numberOfVertices = numberOfTextureCoords = numberOfNormals = 
                    numberOfFaces = 0;
            currentFace = 0;
            firstVertexStreamPosition = 0;
            firstVertexStreamPositionAvailable = false;
            firstTextureCoordStreamPosition = 0;
            firstTextureCoordStreamPositionAvailable = false;
            firstNormalStreamPosition = 0;
            firstNormalStreamPositionAvailable = false;
            firstFaceStreamPosition = 0;
            firstFaceStreamPositionAvailable = false;
            firstMaterialStreamPosition = 0;
            firstMaterialStreamPositionAvailable = false;
            currentStreamPosition = 0;
            listener = null;
            coordsInChunkArray = null;
            textureCoordsInChunkArray = null;
            normalsInChunkArray = null;
            indicesInChunkArray = null;
            
            originalVertexIndicesInChunkArray = null;
            originalTextureIndicesInChunkArray = null;
            originalNormalIndicesInChunkArray = null;
            
            vertexIndicesMap = new TreeMap<Long, Integer>();
            textureCoordsIndicesMap = new TreeMap<Long, Integer>();
            normalsIndicesMap = new TreeMap<Long, Integer>();
            
            verticesStreamPositionMap = new TreeMap<Long, Long>();
            textureCoordsStreamPositionMap = new TreeMap<Long, Long>();
            normalsStreamPositionMap = new TreeMap<Long, Long>();
                        
            verticesInChunk = indicesInChunk = 0;
            indicesInChunkSize = 0;
            
            vertexStreamPosition = 0;
            textureCoordStreamPosition = 0;
            normalStreamPosition = 0;
            
            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;
            
            currentChunkMaterialName = "";
            
            materialLoader = null;
                    
            setUp();
        }
        
        /**
         * Method to set listener of this loader iterator.
         * This listener will be notified when the loading process finishes
         * @param listener listener of this loader iterator
         */        
        public void setListener(LoaderIteratorListener listener){
            this.listener = listener;
        }
        
        /**
         * Returns listener of this loader iterator.
         * This listener will be notified when the loading process finishes
         * @return listener of this loader iterator
         */        
        public LoaderIteratorListener getListener(){
            return listener;
        }
        
        /**
         * Attempts to clear memory.
         * If garbage collection is enabled this method will attempt a garbage
         * collection after being called a few times.
         * Memory will then be released depending on system implementation.
         */
        private void cleanMemory(){
            if(loader.periodicGarbageCollection){
                gcCounter++;
                if(gcCounter > loader.itersBeforeGarbageCollection){
                    System.gc();
                    gcCounter = 0;
                }
            }
        }
        
        /**
         * Indicates if there is another chunk of data to be loaded
         * @return true if there is another chunk of data, false otherwise
         */        
        @Override
        public boolean hasNext() {
            return currentFace < numberOfFaces;
        }

        /**
         * Loads and returns next chunk of data, if available
         * @return next chunk of data
         * @throws NotAvailableException thrown if no more data is available
         * @throws LoaderException if file data is corrupt or cannot be 
         * understood
         * @throws IOException if an I/O error occurs
         */        
        @Override
        public DataChunk next() throws NotAvailableException, LoaderException, 
            IOException {
            
            if(reader == null) throw new IOException();
            
            if(!hasNext()) throw new NotAvailableException();
            
            initChunkArrays();
            
            //reset chunk bounding box values
            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;
            
            
            long progressStep = Math.max(
                    (long)(LoaderOBJ.PROGRESS_DELTA * numberOfFaces), 1);            
            
            boolean materialChange = false;
            
            while((currentFace < numberOfFaces) && !materialChange){
                
                long faceStreamPos = reader.getPosition();
                String str = reader.readLine();
                if((str == null) && (currentFace < (numberOfFaces - 1))){
                    //unexpected end of file
                    throw new LoaderException();
                }else if(str == null){
                    break;
                }
                
                //check if line corresponds to face or material, otherwise, 
                //ignore
                if(str.startsWith("usemtl ")){
                    
                    if(currentChunkMaterialName.equals("")){
                        currentChunkMaterialName = str.substring(
                                "usemtl ".length()).trim();
                        //search current material on material library
                        currentMaterial = null;
                        if(materialLoader != null){                        
                           currentMaterial = materialLoader.getMaterialByName(
                                   currentChunkMaterialName);
                        }                        
                    }else{
                        //stop reading this chunk and reset position to 
                        //beginning of line so that usemtl is read again                        
                        materialChange = true;
                        reader.seek(faceStreamPos);                        
                        break;
                    }
                    
                }else if(str.startsWith("f ")){
                    
                    //line is a face, so we keep data after "f"
                    str = str.substring("f ".length()).trim();
                    //retrieve words in data
                    String [] valuesTemp = str.split(" ");
                    Set<String []> valuesSet = new HashSet<String[]>();
                    
                    //check that each face contains three elements to define a
                    //triangle only
                    if(valuesTemp.length == 3){
                        valuesSet.add(valuesTemp);
                        
                    }else if(valuesTemp.length > 3){
                        //if instead of a triangle we have a polygon then we
                        //to divide valuesTemp into a set of values forming 
                        //triangles
                        List<VertexOBJ> verticesList = 
                                getFaceValues(valuesTemp);
                        try{
                            valuesSet.addAll(buildTriangulatedIndices(
                                    verticesList));
                        }catch(TriangulatorException e){
                            //triangulation failed for some reason, but
                            //file reading continues if configured like that
                            //(by default it is)
                            if(!continueIfTriangulationError) 
                                throw new LoaderException(e);
                        }
                    }else{
                        throw new LoaderException();
                        
                    }
                    
                    
                    //each word corresponds to a vertex/texture/normal index,
                    //so we chek if such number of indices cand be added into
                    //this chunk
                    if((verticesInChunk + valuesSet.size() * 3) >   
                            loader.maxVerticesInChunk){ //values.length
                        //no more vertices can be added to chunk so we reset 
                        //stream to start on current face
                        reader.seek(faceStreamPos);
                        break;
                    }

                    //keep current stream position for next face
                    currentStreamPosition = reader.getPosition();

                    for(String [] values: valuesSet){
                    
                        //otherwise values can be added into chunk, so we read
                        //vertex index, texture index and normal index
                        for(int u = 0; u < values.length; u++){
                            String value = values[u];

                            //vslue can be of the form v/vt/vn, where v stands 
                            //for vertex index, vt for texture index and vn for 
                            //normal index, and where vt and vn are optional
                            String [] indices = value.split("/");
                            boolean addExistingVertexCoords = false;
                            boolean addExistingTextureCoords = false;
                            boolean addExistingNormal = false;
                            int vertexCoordsChunkIndex = -1;
                            int textureCoordsChunkIndex = -1;
                            int normalChunkIndex = -1;

                            boolean addExisting;
                            int chunkIndex = 0;

                            //first check if vertex has to be added as new or 
                            //not
                            if(indices.length >= 1 && 
                                    (indices[0].length() != 0)){
                                indicesAvailable = true;
                                //indices start at 1 in OBJ
                                vertexIndex = Integer.valueOf(indices[0]) - 1;

                                //determine if vertex coordinates have to be 
                                //added as new or they can be reused from an 
                                //existing vertex
                                if(!loader.allowDuplicateVerticesInChunk &&
                                        (vertexCoordsChunkIndex = searchVertexIndexInChunk(vertexIndex)) >= 0){
                                    addExistingVertexCoords = true;
                                }else addExistingVertexCoords = false;
                            }
                            if(indices.length >= 2 && 
                                    (indices[1].length() != 0)){
                                textureAvailable = true;
                                 //indices start at 1 in OBJ
                                textureIndex = Integer.valueOf(indices[1]) - 1;

                                //determine if texture coordinates have to be 
                                //added as new or they can be reused from an 
                                //existing vertex
                                if(!loader.allowDuplicateVerticesInChunk &&
                                        (textureCoordsChunkIndex = searchTextureCoordIndexInChunk(textureIndex)) >= 0){
                                    addExistingTextureCoords = true;
                                }else addExistingTextureCoords = false;
                            }
                            if(indices.length >= 3 && (indices[2].length() != 0)){
                                normalsAvailable = true;
                                //indices start at 1 in OBJ
                                normalIndex = Integer.valueOf(indices[2]) - 1;

                                //determine if normal coordinates have to be 
                                //added as new or they can be reused from an 
                                //existing vertex
                                if(!loader.allowDuplicateVerticesInChunk &&
                                        (normalChunkIndex = searchNormalIndexInChunk(normalIndex)) >= 0){
                                    addExistingNormal = true;
                                }else addExistingNormal = false;
                            }

                            //if either vertex coordinates, texture coordinates 
                            //or normal indicate that a new vertex needs to be 
                            //added, then do so, only if all three use an 
                            //existing vertex into chunk use that existing 
                            //vertex. Also, in case that existing vertex is 
                            //added, if chunk indices of existing vertex, 
                            //texture and normal are not the same add as a new 
                            //vertex into chunk

                            //if some chunk index is found, set add existing to 
                            //true
                            addExisting = (vertexCoordsChunkIndex >= 0) ||
                                    (textureCoordsChunkIndex >= 0) || 
                                    (normalChunkIndex >= 0);
                            //ensure that if index is present an existing vertex 
                            //in chunk exists
                            if(indices.length >= 1 && 
                                    (indices[0].length() != 0))
                                addExisting &= addExistingVertexCoords;                         
                            if(indices.length >= 2 && 
                                    (indices[1].length() != 0))
                                addExisting &= addExistingTextureCoords;
                            if(indices.length >= 3 && 
                                    (indices[2].length() != 0))
                                addExisting &= addExistingNormal;
                            
                            if(addExisting){
                                //if finally an existing vertex is added, set 
                                //chunk index
                                if(vertexCoordsChunkIndex >= 0)
                                    chunkIndex = vertexCoordsChunkIndex;
                                if(textureCoordsChunkIndex >= 0)
                                    chunkIndex = textureCoordsChunkIndex;
                                if(normalChunkIndex >= 0)
                                    chunkIndex = normalChunkIndex;
                            }



                            if(indices.length >= 1 && 
                                    (indices[0].length() != 0)){
                                if(!addExistingVertexCoords){
                                    //new vertex needs to be added into chunk, 
                                    //so we need to read vertex data

                                    //fetch vertex data position
                                    fetchVertex(vertexIndex);
                                    vertexStreamPosition = reader.getPosition();

                                    //read all vertex data
                                    String vertexLine = reader.readLine();
                                    if(!vertexLine.startsWith("v ")) 
                                        throw new LoaderException();
                                    vertexLine = vertexLine.substring(
                                            "v ".length()).trim();
                                    //retrieve words in vertexLine, which contain
                                    //vertex coordinates either as x, y, z or x,
                                    //y, z, w
                                    String [] vertexCoordinates = 
                                            vertexLine.split(" ");
                                    if(vertexCoordinates.length == 4){
                                        //homogeneous coordinates x, y, z, w

                                        //check that values are valid
                                        if(vertexCoordinates[0].length() == 0) 
                                            throw new LoaderException();                                    
                                        if(vertexCoordinates[1].length() == 0) 
                                            throw new LoaderException();                                    
                                        if(vertexCoordinates[2].length() == 0) 
                                            throw new LoaderException();                                    
                                        if(vertexCoordinates[3].length() == 0) 
                                            throw new LoaderException();

                                        float w = Float.valueOf(
                                                vertexCoordinates[3]);
                                        coordX = Float.valueOf(
                                                vertexCoordinates[0]) / w;
                                        coordY = Float.valueOf(
                                                vertexCoordinates[1]) / w;
                                        coordZ = Float.valueOf(
                                                vertexCoordinates[2]) / w;                                    

                                    }else if(vertexCoordinates.length >= 3){
                                        //inhomogeneous coordinates x, y, z

                                        //check that values are valid
                                        if(vertexCoordinates[0].length() == 0) 
                                            throw new LoaderException();                                    
                                        if(vertexCoordinates[1].length() == 0) 
                                            throw new LoaderException();                                    
                                        if(vertexCoordinates[2].length() == 0) 
                                            throw new LoaderException();                                    

                                        coordX = Float.valueOf(
                                                vertexCoordinates[0]);
                                        coordY = Float.valueOf(
                                                vertexCoordinates[1]);
                                        coordZ = Float.valueOf(
                                                vertexCoordinates[2]);

                                    }else throw new LoaderException(); //unsupported length

                                    addExisting = false;
                                }
                            }
                            if(indices.length >= 2 && 
                                    (indices[1].length() != 0)){
                                if(!addExistingTextureCoords){
                                    //new texture values need to be added into 
                                    //chunk, so we need to read texture 
                                    //coordinates data

                                    //fetch texture data position
                                    fetchTexture(textureIndex);
                                    textureCoordStreamPosition = 
                                            reader.getPosition();

                                    //read all texture data
                                    String textureLine = reader.readLine();
                                    if(!textureLine.startsWith("vt "))
                                        throw new LoaderException();
                                    textureLine = textureLine.substring(
                                            "vt ".length()).trim();
                                    //retrieve words in textureLine, which contain
                                    //texture coordinates either as u, w or u, v, w
                                    String [] textureCoordinates = 
                                            textureLine.split(" ");
                                    if(textureCoordinates.length == 3){
                                        //homogeneous coordinates u, v, w

                                        //check that values are valid
                                        if(textureCoordinates[0].length() == 0) 
                                            throw new LoaderException();
                                        if(textureCoordinates[1].length() == 0) 
                                            throw new LoaderException();
                                        if(textureCoordinates[2].length() == 0) 
                                            throw new LoaderException();

                                        float w = Float.valueOf(
                                                textureCoordinates[2]).
                                                floatValue();
                                        
                                        textureU = Float.valueOf(
                                                textureCoordinates[0]).
                                                floatValue() / w;
                                        textureV = Float.valueOf(
                                                textureCoordinates[1]).
                                                floatValue() / w;
                                        if(Math.abs(w) < Float.MIN_VALUE || 
                                                Float.isInfinite(textureU) ||
                                                Float.isNaN(textureU) || 
                                                Float.isInfinite(textureV) ||
                                                Float.isNaN(textureV)){
                                            textureU = Float.valueOf(
                                                    textureCoordinates[0]).floatValue();
                                            textureV = Float.valueOf(
                                                    textureCoordinates[1]).floatValue();
                                        }

                                    }else if(textureCoordinates.length >= 2){
                                        //inhomogeneous coordinates u, v

                                        //check that values are valid
                                        if(textureCoordinates[0].length() == 0) 
                                            throw new LoaderException();
                                        if(textureCoordinates[1].length() == 0) 
                                            throw new LoaderException();

                                        textureU = Float.valueOf(
                                                textureCoordinates[0]).
                                                floatValue();
                                        textureV = Float.valueOf(
                                                textureCoordinates[1]).
                                                floatValue();
                                    }else throw new LoaderException(); //unsupported length

                                    addExisting = false;
                                }
                            }
                            if(indices.length >= 3 && 
                                    (indices[2].length() != 0)){
                                if(!addExistingNormal){
                                    //new normal needs to be added into chunk, 
                                    //so we need to read vertex data

                                    //fetch normal data position
                                    fetchNormal(normalIndex);
                                    normalStreamPosition = reader.getPosition();

                                    //read all normal data
                                    String normalLine = reader.readLine();
                                    if(!normalLine.startsWith("vn "))
                                        throw new LoaderException();
                                    normalLine = normalLine.substring(
                                            "vn ".length()).trim();
                                    //retrieve words in normalLine, which must 
                                    //contain normal coordinates as x, y, z
                                    String [] normalCoordinates = 
                                            normalLine.split(" ");
                                    if(normalCoordinates.length == 3){
                                        //normal coordinates x, y, z

                                        //check that values are valid
                                        if(normalCoordinates[0].length() == 0) 
                                            throw new LoaderException();
                                        if(normalCoordinates[1].length() == 0) 
                                            throw new LoaderException();
                                        if(normalCoordinates[2].length() == 0) 
                                            throw new LoaderException();

                                        nX = Float.valueOf(
                                                normalCoordinates[0]);
                                        nY = Float.valueOf(
                                                normalCoordinates[1]);
                                        nZ = Float.valueOf(
                                                normalCoordinates[2]);
                                    }else throw new LoaderException(); //unsupported length

                                    addExisting = false;
                                }
                            }

                            if(addExisting) 
                                addExistingVertexToChunk(chunkIndex);
                            else addNewVertexDataToChunk();
                        }
                    }
                    //reset face stream position
                    reader.seek(currentStreamPosition);
                    currentFace++;
                    
                    //to reduce memory consumption and delete listIndices
                    //attempt to clean memory
                    cleanMemory();                        
                }
                
                //compute progress
                if(loader.listener != null){
                    if((currentFace % progressStep) == 0){
                        loader.listener.onLoadProgressChange(loader, 
                                (float)(currentFace) / (float)(numberOfFaces));
                    }
                }
            }
            
            //trim arrays to store only needed data
            trimArrays();
            
            //Instantiate DataChunk with chunk arrays
            DataChunk dataChunk = new DataChunk();
            
            if(verticesAvailable){
                dataChunk.setVerticesCoordinatesData(coordsInChunkArray);
                dataChunk.setMinX(minX);
                dataChunk.setMinY(minY);
                dataChunk.setMinZ(minZ);
                dataChunk.setMaxX(maxX);
                dataChunk.setMaxY(maxY);
                dataChunk.setMaxZ(maxZ);
            }else coordsInChunkArray = null; //so it can be garbage collected
            
            if(textureAvailable){
                dataChunk.setTextureCoordinatesData(textureCoordsInChunkArray);
            }else textureCoordsInChunkArray = null; //so it can be garbage 
                                                    //collected
                        
            if(currentMaterial != null){
                dataChunk.setMaterial(currentMaterial);
            }
           
            if(materialChange){
                currentChunkMaterialName = "";
                currentMaterial = null;
            }
            
            if(indicesAvailable)
                dataChunk.setIndicesData(indicesInChunkArray);
            else indicesInChunkArray = null; //so it can be garbage collected
            
            if(normalsAvailable)
                dataChunk.setNormalsData(normalsInChunkArray);
            else normalsInChunkArray = null; //so it can be garbage collected            
            
            if(!hasNext()){
                //notify iterator finished
                if(listener != null) listener.onIteratorFinished(this);
            }
            
            //if no more chunks are available, then close input reader
            if(!hasNext()) reader.close();
            
            cleanMemory(); // to reduce memory consumption            
            return dataChunk;
        }
        
        /**
         * Internal method to decompose a an array of vertices forming a polygon
         * in a set of arrays of vertices corresponding to triangles after
         * triangulation of the polygon. This method is used to triangulate
         * polygons with more than 3 vertices contained in the file.
         * @param vertices list of vertices forming a polygon to be triangulated
         * @return a set containing arrays of indices of vertices (in string 
         * format) corresponding to the triangles forming the polygon after the
         * triangulation
         * @throws TriangulatorException if triangulation fails (because polygon
         * is degenerate or contains invalid values such as NaN or infinity)
         */
        private Set<String []> buildTriangulatedIndices(
                List<VertexOBJ> vertices) throws TriangulatorException{
            List<Point3D> polygonVertices = new ArrayList<Point3D>(
                    vertices.size());
            for(VertexOBJ v : vertices){
                if(v.getVertex() == null) throw new TriangulatorException();
                polygonVertices.add(v.getVertex());
            }
            List<int[]> indices = new ArrayList<int[]>();
            Triangulator3D triangulator = Triangulator3D.create();
            List<Triangle3D> triangles = triangulator.triangulate(
                    polygonVertices, indices);
            
            Set<String[]> result = new HashSet<String[]>(); 
            String[] face;
            int counter = 0;
            int[] triangleIndices;
            int index;
            VertexOBJ vertex;
            String tmp;
            for(Triangle3D t : triangles){
                triangleIndices = indices.get(counter);
                face = new String[Triangle3D.NUM_VERTICES];
                for(int i = 0; i < Triangle3D.NUM_VERTICES; i++){
                    index = triangleIndices[i];
                    vertex = vertices.get(index);
                    tmp = "";
                    if(vertex.isVertexIndexAvailable()) 
                        tmp += vertex.getVertexIndex();
                    if(vertex.isTextureIndexAvailable() || 
                            vertex.isNormalIndexAvailable()){
                        tmp += "/";
                        if(vertex.isTextureIndexAvailable()){
                            tmp += vertex.getTextureIndex();
                        }
                        if(vertex.isNormalIndexAvailable()){
                            tmp += "/" + vertex.getNormalIndex();
                        }
                    }
                    
                    face[i] = tmp;
                }
                counter++;
                result.add(face);
            }            
            
            return result;
        }
        
        /**
         * This method reads a line containing face (i.e. polygon) indices of
         * vertices and fetches those vertices coordinates and associated data
         * such as texture coordinates or normal coordinates
         * @param values a string containing vertex indices forming a polygon.
         * Note that indices refer to the values contained in OBJ file, not the
         * indices in the chunk of data
         * @return a list of vertices forming a face (i.e, polygon)
         * @throws IOException if an I/O error occurs
         * @throws LoaderException if loading fails because data is corrupted or
         * cannot be interpreted
         */
        private List<VertexOBJ> getFaceValues(String [] values) 
                throws IOException, LoaderException{

            VertexOBJ tmpVertex;
            Point3D point;
            List<VertexOBJ> vertices = new ArrayList<VertexOBJ>(values.length);
            
            //keep current stream position for next face
            long tempPosition = reader.getPosition();

            for(int u = 0; u < values.length; u++){
                
                String value = values[u];
                tmpVertex = new VertexOBJ();
                point = Point3D.create();
                tmpVertex.setVertex(point);
                
                String [] indices = value.split("/");
                
                if(indices.length >= 1 && (indices[0].length() != 0)){
                    vertexIndex = Integer.valueOf(indices[0]) - 1;
                    tmpVertex.setVertexIndex(vertexIndex + 1);
                    fetchVertex(vertexIndex);
                    vertexStreamPosition = reader.getPosition();

                    String vertexLine = reader.readLine();
                    if(!vertexLine.startsWith("v ")) 
                        throw new LoaderException();
                    vertexLine = vertexLine.substring("v ".length()).trim();
                    String [] vertexCoordinates = vertexLine.split(" ");
                    
                    if(vertexCoordinates.length == 4){
                        //homogeneous coordinates x, y, z, w
                        //ensure that vertex coordinates are not empty
                        if(vertexCoordinates[0].length() == 0) 
                            throw new LoaderException();                                    
                        if(vertexCoordinates[1].length() == 0) 
                            throw new LoaderException();
                        if(vertexCoordinates[2].length() == 0) 
                            throw new LoaderException();
                        if(vertexCoordinates[3].length() == 0) 
                            throw new LoaderException();
                        
                        try{
                            point.setHomogeneousCoordinates(
                                    Double.valueOf(vertexCoordinates[0]), 
                                    Double.valueOf(vertexCoordinates[1]), 
                                    Double.valueOf(vertexCoordinates[2]), 
                                    Double.valueOf(vertexCoordinates[3]));
                        }catch(NumberFormatException e){
                            //some vertex coordinate value could not be parsed
                            throw new LoaderException(e);
                        }
                    }else if(vertexCoordinates.length >= 3){
                        //inhomogeneous coordinates x, y, z
                        //ensure that vertex coordinate are not empty
                        if(vertexCoordinates[0].length() == 0) 
                            throw new LoaderException();
                        if(vertexCoordinates[1].length() == 0) 
                            throw new LoaderException();
                        if(vertexCoordinates[2].length() == 0) 
                            throw new LoaderException();
                        
                        try{
                            point.setInhomogeneousCoordinates(
                                    Double.valueOf(vertexCoordinates[0]), 
                                    Double.valueOf(vertexCoordinates[1]), 
                                    Double.valueOf(vertexCoordinates[2]));
                        }catch(NumberFormatException e){
                            //some vertex coordinate value could not be parsed
                            throw new LoaderException(e);
                        }
                    }else throw new LoaderException(); //unsupported length
                }
                if(indices.length >= 2 && (indices[1].length() != 0)){
                    tmpVertex.setTextureIndex(
                            Integer.valueOf(indices[1]).intValue());
                }
                if(indices.length >= 3 && (indices[2].length() != 0)){
                   tmpVertex.setNormalIndex(
                           Integer.valueOf(indices[2]).intValue());
                }

                vertices.add(tmpVertex);
            }
            
            reader.seek(tempPosition);
            return vertices;
        }
        
        /**
         * Initializes arrays forming current chunk of data
         */
        private void initChunkArrays(){
            coordsInChunkArray = new float[loader.maxVerticesInChunk * 3];
            textureCoordsInChunkArray = 
                    new float[loader.maxVerticesInChunk * 2];
            normalsInChunkArray = new float[loader.maxVerticesInChunk * 3];
            indicesInChunkArray = new int[loader.maxVerticesInChunk];           
            
            originalVertexIndicesInChunkArray = 
                    new long[loader.maxVerticesInChunk];
            originalTextureIndicesInChunkArray = 
                    new long[loader.maxVerticesInChunk];
            originalNormalIndicesInChunkArray = 
                    new long[loader.maxVerticesInChunk];
            verticesInChunk = 0;
            indicesInChunk = 0;
            indicesInChunkSize = loader.maxVerticesInChunk;            
            
            vertexIndicesMap.clear();
            textureCoordsIndicesMap.clear();
            normalsIndicesMap.clear();             
        }
        
        /**
         * Searches vertex index in current chunk of data by using the index
         * used in the OBJ file.
         * This method searches within the cached indices which relate indices
         * in the chunk of data respect to indices in the OBJ file.
         * @param originalIndex vertex index used in the OBJ file
         * @return vertex index used in current chunk of data or -1 if not found
         */
        private int searchVertexIndexInChunk(long originalIndex){
            //returns chunk index array position where index is found
            Integer chunkIndex = vertexIndicesMap.get(Long.valueOf(
                    originalIndex));
            
            if(chunkIndex == null) return -1;
            
            //returns index of vertex in chunk
            return indicesInChunkArray[chunkIndex.intValue()];
        }
        
        /**
         * Searches texture index in current chunk of data by using the index
         * used in the OBJ file.
         * This method searches within the cached indices which relate indices
         * in the chunk of data respect to indices in the OBJ file.
         * @param originalIndex texture index used in the OBJ file
         * @return texture index used in current chunk of data or -1 if not 
         * found
         */
        private int searchTextureCoordIndexInChunk(long originalIndex){
            //returns chunk index array position where index is found
            Integer chunkIndex = vertexIndicesMap.get(Long.valueOf(
                    originalIndex));
            
            if(chunkIndex == null) return -1;
            
            //returns index of texture coordinate in chunk
            return indicesInChunkArray[chunkIndex.intValue()];
        }
        
        /**
         * Searches normal index in current chunk of data by using the index
         * used in the OBJ file.
         * This method searches within the cached indices which relate indices
         * in the chunk of data respect to indices in the OBJ file.
         * @param originalIndex normal index used in the OBJ file
         * @return normal index used in current chunk of data or -1 if not found
         */
        private int searchNormalIndexInChunk(long originalIndex){
            //returns chunk index array position where index is found
            Integer chunkIndex = vertexIndicesMap.get(Long.valueOf(
                    originalIndex));
            
            if(chunkIndex == null) return -1;
            
            //returns index of texture coordinate in chunk
            return indicesInChunkArray[chunkIndex.intValue()];
        }
        
        /**
         * Add vertex position to cache of file positions
         * @param originalIndex vertex index used in OBJ file
         * @param streamPosition stream position where vertex is located
         */
        private void addVertexPositionToMap(long originalIndex, 
                long streamPosition){
            if(verticesStreamPositionMap.size() > loader.maxStreamPositions){
                //Map is full. Remove 1st item before adding a new one
                Long origIndex = verticesStreamPositionMap.firstKey();
                verticesStreamPositionMap.remove(origIndex);
            }
            //add new item
            verticesStreamPositionMap.put(Long.valueOf(originalIndex), 
                    Long.valueOf(streamPosition));
        }
        
        /**
         * Add texture coordinate position to cache of file positions
         * @param originalIndex texture coordinate index used in OBJ file
         * @param streamPosition stream position where texture coordinate is 
         * located
         */
        private void addTextureCoordPositionToMap(long originalIndex,
                long streamPosition){
            if(textureCoordsStreamPositionMap.size() > 
                    loader.maxStreamPositions){
                //Map is full. Remove 1st item before adding a new one
                Long origIndex = textureCoordsStreamPositionMap.firstKey();
                textureCoordsStreamPositionMap.remove(origIndex);
            }
            //add new item
            textureCoordsStreamPositionMap.put(Long.valueOf(originalIndex), 
                    Long.valueOf(streamPosition));
        }
        
        /**
         * Add normal coordinate to cache of file positions
         * @param originalIndex normal coordinate index used in OBJ file
         * @param streamPosition stream position where normal coordinate is
         * located
         */
        private void addNormalPositionToMap(long originalIndex,
                long streamPosition){
            if(normalsStreamPositionMap.size() > loader.maxStreamPositions){
                //Map is full. Remove 1st item before adding a new one
                Long origIndex = normalsStreamPositionMap.firstKey();
                normalsStreamPositionMap.remove(origIndex);
            }
            //add new item
            normalsStreamPositionMap.put(Long.valueOf(originalIndex), 
                    Long.valueOf(streamPosition));
        }
        
        /**
         * Adds data of last vertex being loaded to current chunk of data as a
         * new vertex
         */
        private void addNewVertexDataToChunk(){
            int pos = 3 * verticesInChunk;
            int textPos = 2 * verticesInChunk;
            
            coordsInChunkArray[pos] = coordX;
            normalsInChunkArray[pos] = nX;
            textureCoordsInChunkArray[textPos] = textureU;
            
            pos++;
            textPos++;
            
            coordsInChunkArray[pos] = coordY;
            normalsInChunkArray[pos] = nY;
            textureCoordsInChunkArray[textPos] = textureV;
            
            pos++;
            
            coordsInChunkArray[pos] = coordZ;
            normalsInChunkArray[pos] = nZ;
            
            //update bounding box values
            if(coordX < minX) minX = coordX;
            if(coordY < minY) minY = coordY;
            if(coordZ < minZ) minZ = coordZ;
            
            if(coordX > maxX) maxX = coordX;
            if(coordY > maxY) maxY = coordY;
            if(coordZ > maxZ) maxZ = coordZ;
            
            //if arrays of indices become full, we need to resize them
            if(indicesInChunk >= indicesInChunkSize){
                increaseIndicesArraySize();
            }
            indicesInChunkArray[indicesInChunk] = verticesInChunk;
            originalVertexIndicesInChunkArray[indicesInChunk] = vertexIndex;
            originalTextureIndicesInChunkArray[indicesInChunk] = textureIndex;            
            originalNormalIndicesInChunkArray[indicesInChunk] = normalIndex;
            //store original indices in maps so we can search chunk index by
            //original indices of vertices, texture or normal
            vertexIndicesMap.put(Long.valueOf(vertexIndex), 
                    Integer.valueOf(indicesInChunk));
            textureCoordsIndicesMap.put(Long.valueOf(textureIndex), 
                    Integer.valueOf(indicesInChunk));
            normalsIndicesMap.put(Long.valueOf(normalIndex), 
                    Integer.valueOf(indicesInChunk));
            
            //store vertex, texture and normal stream positions
            addVertexPositionToMap(vertexIndex, vertexStreamPosition);
            addTextureCoordPositionToMap(textureIndex, 
                    textureCoordStreamPosition);
            addNormalPositionToMap(normalIndex, normalStreamPosition);
            
            verticesInChunk++;
            indicesInChunk++;
        }
        
        /**
         * Adds index to current chunk of data referring to a previously 
         * existing vertex in the chunk
         * @param existingIndex index of vertex that already exists in the chunk
         */
        private void addExistingVertexToChunk(int existingIndex){
            //if arrays of indices become full, we need to resize them
            if(indicesInChunk >= indicesInChunkSize){
                increaseIndicesArraySize();
            }
            indicesInChunkArray[indicesInChunk] = existingIndex;
            originalVertexIndicesInChunkArray[indicesInChunk] = vertexIndex;
            originalTextureIndicesInChunkArray[indicesInChunk] = textureIndex;            
            originalNormalIndicesInChunkArray[indicesInChunk] = normalIndex;
            
            indicesInChunk++;
        }
        
        /**
         * Increases size of arrays of data. This method is called when needed
         */
        private void increaseIndicesArraySize(){
            int newIndicesInChunkSize = indicesInChunkSize +
                    loader.maxVerticesInChunk;
            int[] newIndicesInChunkArray = new int[newIndicesInChunkSize];
            long[] newOriginalVertexIndicesInChunkArray = 
                    new long[newIndicesInChunkSize];
            long[] newOriginalTextureIndicesInChunkArray =
                    new long[newIndicesInChunkSize];
            long[] newOriginalNormalIndicesInChunkArray =
                    new long[newIndicesInChunkSize];
            
            //copy contents of old array
            System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray, 0, 
                    indicesInChunkSize);
            System.arraycopy(originalVertexIndicesInChunkArray, 0, 
                    newOriginalVertexIndicesInChunkArray, 0, 
                    indicesInChunkSize);
            System.arraycopy(originalTextureIndicesInChunkArray, 0, 
                    newOriginalTextureIndicesInChunkArray, 0, 
                    indicesInChunkSize);
            System.arraycopy(originalNormalIndicesInChunkArray, 0, 
                    newOriginalNormalIndicesInChunkArray, 0, 
                    indicesInChunkSize);
            
            //set new arrays and new size
            indicesInChunkArray = newIndicesInChunkArray;
            originalVertexIndicesInChunkArray = 
                    newOriginalVertexIndicesInChunkArray;
            originalTextureIndicesInChunkArray =
                    newOriginalTextureIndicesInChunkArray;
            originalNormalIndicesInChunkArray =
                    newOriginalNormalIndicesInChunkArray;
            indicesInChunkSize = newIndicesInChunkSize;
            
            cleanMemory(); //to reduce memory consumption            
        }        
        
        /**
         * Trims arrays of data to reduce size of arrays to fit chunk data. This 
         * method is loaded just before copying data to chunk being returned
         */
        private void trimArrays(){
            if(verticesInChunk > 0){
                int elems = verticesInChunk * 3;
                int textElems = verticesInChunk * 2;
                
                float[] newCoordsInChunkArray = new float[elems];
                float[] newTextureCoordsInChunkArray = new float[elems];
                float[] newNormalsInChunkArray = new float[elems];
                
                //copy contents of old arrays
                System.arraycopy(coordsInChunkArray, 0, newCoordsInChunkArray,
                        0, elems);
                System.arraycopy(textureCoordsInChunkArray, 0, 
                        newTextureCoordsInChunkArray, 0, textElems);
                System.arraycopy(normalsInChunkArray, 0, newNormalsInChunkArray, 
                        0, elems);
                
                //set new arrays
                coordsInChunkArray = newCoordsInChunkArray;
                textureCoordsInChunkArray = newTextureCoordsInChunkArray;
                normalsInChunkArray = newNormalsInChunkArray;
            }else{
                //allow garbage collection
                coordsInChunkArray = null;
                textureCoordsInChunkArray = null;
                normalsInChunkArray = null;
            }
            
            if(indicesInChunk > 0){
                int[] newIndicesInChunkArray = new int[indicesInChunk];
                System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray,
                        0, indicesInChunk);
                
                //set new array
                indicesInChunkArray = newIndicesInChunkArray;
            }else{
                //allow garbage collection
                indicesInChunkArray = null;
                originalVertexIndicesInChunkArray = null;
                originalTextureIndicesInChunkArray = null;
                originalNormalIndicesInChunkArray = null;
            }
            
            cleanMemory(); //to reduce memory consumption            
        }
        
        /**
         * Set ups loader iterator. This method is called when constructing
         * this iterator
         * @throws IOException if an I/O error occurs
         * @throws LoaderException if data is corrupted or cannot be understood
         */
        private void setUp() throws IOException, LoaderException{
            String str;
            long streamPosition;
            numberOfVertices = numberOfTextureCoords = numberOfNormals = 
                    numberOfFaces = 0;
            
            do{
                streamPosition = reader.getPosition();
                str = reader.readLine();
                if(str == null) break;
                
                if(str.startsWith("#")){
                    //line is a comment, so we should add it to the list of
                    //comments
                    loader.comments.add(str.substring("#".length()).trim());
                }else if(str.startsWith("vt ")){
                    //line contains texture coordinates, so we keep its stream
                    //position and indicate that chunks will contain texture
                    //coordinates
                    if(!firstTextureCoordStreamPositionAvailable){
                        firstTextureCoordStreamPosition = streamPosition;
                        firstTextureCoordStreamPositionAvailable = true;
                        textureAvailable = true;
                    }
                    numberOfTextureCoords++;
                }else if(str.startsWith("vn ")){
                    //line contains normal, so we keep its stream position and
                    //indicate that chunks will contain normals
                    if(!firstNormalStreamPositionAvailable){
                        firstNormalStreamPosition = streamPosition;
                        firstNormalStreamPositionAvailable = true;
                        normalsAvailable = true;
                    }
                    numberOfNormals++;                    
                }else if(str.startsWith("v ")){
                    //line contains vertex coordinates, so we keep its stream
                    //position and indicate that chunks will contain vertex
                    //coordinates
                    if(!firstVertexStreamPositionAvailable){
                        firstVertexStreamPosition = streamPosition;
                        firstVertexStreamPositionAvailable = true;
                        verticesAvailable = true;
                    }
                    numberOfVertices++;
                }else if(str.startsWith("f ")){
                    //line contains face definiton, so we keep its stream 
                    //position and indicate that chunks will contain indices
                    if(!firstFaceStreamPositionAvailable){
                        firstFaceStreamPosition = streamPosition;
                        firstFaceStreamPositionAvailable = true;
                        indicesAvailable = true;
                    }
                    
                    numberOfFaces++;
                    
                }else if(str.startsWith("mtllib ")){
                    //a material library is found
                    String path = str.substring("mtllib ".length()).trim();
                    if(loader.listener != null && 
                            loader.listener instanceof LoaderListenerOBJ){
                        LoaderListenerOBJ loaderListener = 
                                (LoaderListenerOBJ)loader.listener;                        
                        materialLoader = 
                                loaderListener.onMaterialLoaderRequested(loader,
                                path);                        
                    }else{
                        materialLoader = new MaterialLoaderOBJ(new File(path));
                    }
                    
                    //now load library of materials
                    try{
                        if(materialLoader != null){
                            loader.materials = materialLoader.load();
                            materialLoader.close(); //to release file resources
                        }
                    }catch(LoaderException e){
                        throw e;
                    }catch(Throwable t){
                        throw new LoaderException(t);
                    }
                    
                }else if(str.startsWith("usemtl ")){
                    if(!firstMaterialStreamPositionAvailable){
                        firstMaterialStreamPositionAvailable = true;
                        firstMaterialStreamPosition = streamPosition;                        
                        materialsAvailable = true;
                    }
                }
                    
                //ignore any other line
            }while(true); //read until end of file when str == null
            
            //move to first face tream position
            if(!firstFaceStreamPositionAvailable) throw new LoaderException();
            
            if(materialsAvailable && 
                    firstMaterialStreamPosition < firstFaceStreamPosition)
                reader.seek(firstMaterialStreamPosition);
            else reader.seek(firstFaceStreamPosition);
            
        }
        
        /**
         * Fetches vertex data in the file using provided index. Index refers
         * to indices contained in OBJ file
         * @param index index corresponding to vertex being fetched
         * @throws LoaderException if data is corrupted or cannot be understood
         * @throws IOException if an I/O error occurs
         */
        public void fetchVertex(long index) throws LoaderException, IOException{
            if(index > numberOfVertices) throw new LoaderException();
            
            long startStreamPos = firstVertexStreamPosition;
            long startIndex = 0;
            
            if(verticesStreamPositionMap.size() > 0){
                //with floorEntry, we will pick element immediately
                //before or equal to index if any exists
                Map.Entry<Long, Long> entry =
                        verticesStreamPositionMap.floorEntry(index);
                if(entry != null){
                    long origIndex = entry.getKey().longValue();
                    long pos = entry.getValue().longValue();
                    if((origIndex <= index) && (pos >= 0)){
                        startIndex = origIndex;
                        startStreamPos = pos;
                    }
                }
            }
                
            //if we need to read next vertex, don't do anything, otherwise
            //move to next vertex location if reading some vertex located
            //further on the stream. For previous vertex indices, start
            //from beginning
            if(reader.getPosition() != startStreamPos){
                reader.seek(startStreamPos);
            }
                
            //read from stream until start of data of desired vertex
            long streamPosition = 0;            
            for(long i = startIndex; i <= index; i++){
                    
                //when traversing stream of data until reaching desired
                //index, we add all vertex, texture and normal positions 
                //into maps
                String str;
                boolean end = false;
                do{
                    streamPosition = reader.getPosition();
                    str = reader.readLine();
                    if(str == null){
                        end = true;
                        break;
                    }
                     
                    if(str.startsWith("v ")){
                        //line contains vertex coordinates, so we store 
                        //stream position into corresponding map and exit
                        //while loop
                        addVertexPositionToMap(i, streamPosition);
                        break;
                    }
                }while(true); //read until end of file when str == null
                    
                //unexpected end
                if(end) throw new LoaderException();                    
            }
            
            //seek to last streamPosition which contains the desired data
            reader.seek(streamPosition);
        }
        
        /**
         * Fetches texture data in the file using provided index. Index refers
         * to indices contained in OBJ file
         * @param index index corresponding to texture being fetched
         * @throws LoaderException if data is corrupted or cannot be understood
         * @throws IOException if an I/O error occurs
         */
        public void fetchTexture(long index) throws LoaderException, 
                IOException{
            if(index > numberOfTextureCoords) throw new LoaderException();
            
            long startStreamPos = firstTextureCoordStreamPosition;
            long startIndex = 0;
            
            if(textureCoordsStreamPositionMap.size() > 0){
                //with floorEntry, we will pick element immediately
                //before or equal to index if any exists
                Map.Entry<Long, Long> entry =
                        textureCoordsStreamPositionMap.floorEntry(index);
                if(entry != null){
                    long origIndex = entry.getKey().longValue();
                    long pos = entry.getValue().longValue();
                    if((origIndex <= index) && (pos >= 0)){
                        startIndex = origIndex;
                        startStreamPos = pos;
                    }
                }
            }
                
            //if we need to read next texture vertex, don't do anything, 
            //otherwise move to next texture vertex located further on the
            //stream. For previous texture vertex indices, start from
            //beginning
            if(reader.getPosition() != startStreamPos){
                reader.seek(startStreamPos);
            }
                
            //read from stream until start of data of desired texture vertex
            long streamPosition = 0;            
            for(long i = startIndex; i <= index; i++){
                    
                //when traversing stream of data until reaching desired
                //index, we add all vertex, texture and normal positions
                //into maps                
                String str;
                boolean end = false;
                do{
                    streamPosition = reader.getPosition();
                    str = reader.readLine();
                    if(str == null){
                        end = true;
                        break;
                    }
                     
                    if(str.startsWith("vt ")){
                        //line contains texture coordinates, so we store
                        //stream position into corresponding map and exit
                        //while loop
                        addTextureCoordPositionToMap(i, streamPosition);
                        break;
                    }
                }while(true); //read until end of file when str == null
                    
                //unexpected end
                if(end) throw new LoaderException();
            }
            
            //seek to last streamPosition which contains the desired data
            reader.seek(streamPosition);            
        }
        
        /**
         * Fetches normal data in the file using provided index. Index refers
         * to indices contained in OBJ file
         * @param index index corresponding to normal being fetched
         * @throws LoaderException if data is corrupted or cannot be understood
         * @throws IOException if an I/O error occurs
         */
        public void fetchNormal(long index) throws LoaderException, IOException{
            if(index > numberOfNormals) throw new LoaderException();
            
            long startStreamPos = firstNormalStreamPosition;
            long startIndex = 0;
            
            if(normalsStreamPositionMap.size() > 0){
                //with floorEntry, we will pick element inmediately before or
                //equal to index if any exists
                Map.Entry<Long, Long> entry =
                        normalsStreamPositionMap.floorEntry(index);
                if(entry != null){
                    long origIndex = entry.getKey().longValue();
                    long pos = entry.getValue().longValue();
                    if((origIndex <= index) && (pos >= 0)){
                        startIndex = origIndex;
                        startStreamPos = pos;
                    }
                }
            }
                
            //if we need to read next normal, don't do anything, otherwise
            //move to next normal located further on the stream.
            //For previous normals indices, start from beginning
            if(reader.getPosition() != startStreamPos){
                reader.seek(startStreamPos);
            }
                
            //read from stream until start of data of desired normal
            long streamPosition = 0;
            for(long i = startIndex; i <= index; i++){
                
                //when traversing stream of data until reaching desired
                //index, we add all vertex, teture and normal positions
                //into maps                
                String str;
                boolean end = false;
                do{
                    streamPosition = reader.getPosition();
                    str = reader.readLine();
                    if(str == null){
                        end = true;
                        break;
                    }
                    
                    if(str.startsWith("vn ")){
                        //line contains normal, so we store stream position
                        //into corresponding map and exit while loop
                        addNormalPositionToMap(i, streamPosition);
                        break;
                    }
                }while(true); //read until end of file when str == null
                
                //unexpected end
                if(end) throw new LoaderException();
            }
            
            //seek to last streamPosition which contains the desired data
            reader.seek(streamPosition);                        
        }
    }
}
