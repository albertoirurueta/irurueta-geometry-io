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

public class LoaderSTL extends Loader {
    /**
     * Constant defining the default value of maximum number of vertices to keep
     * in a chunk. This is 65535, which corresponds to the maximum value allowed
     * by graphical layer such as OpenGL when working with Vertex Buffer Objects.
     */
    public static final int DEFAULT_MAX_VERTICES_IN_CHUNK = 0xffff;

    /**
     * Minimum allowed value for maximum number of vertices in chunk, which is
     * one.
     */
    public static final int MIN_MAX_VERTICES_IN_CHUNK = 1;

    /**
     * Amount of progress variation (1%) used to notify progress.
     */
    public static final float PROGRESS_DELTA = 0.01f;

    private LoaderIteratorSTL loaderIterator;

    private int maxVerticesInChunk;

    /**
     * Constructor.
     */
    public LoaderSTL() {
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
    }

    /**
     * Constructor.
     *
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk.
     *                           Once this value is exceeded when loading a file, a new chunk of data is
     *                           created.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     *                                  a chunk is lower than 1.
     */
    public LoaderSTL(final int maxVerticesInChunk) {
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Constructor.
     *
     * @param f file to be loaded.
     * @throws IOException if an I/O error occurs.
     */
    public LoaderSTL(final File f) throws IOException {
        super(f);
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
    }

    /**
     * Constructor.
     *
     * @param f                  file to be loaded.
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk.
     *                           Once this value is exceeded when loading a file, a new chunk of data is
     *                           created.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     *                                  a chunk is lower than 1.
     * @throws IOException              if an I/O error occurs.
     */
    public LoaderSTL(final File f, final int maxVerticesInChunk)
            throws IOException {
        super(f);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Constructor.
     *
     * @param listener listener to be notified of loading progress and when
     *                 loading process starts or finishes.
     */
    public LoaderSTL(final LoaderListener listener) {
        super(listener);
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
    }

    /**
     * Constructor.
     *
     * @param listener           listener to be notified of loading progress and when
     *                           loading process starts or finishes.
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk.
     *                           Once this value is exceeded when loading a file, a new chunk of data is
     *                           created.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     *                                  a chunk is lower than 1.
     */
    public LoaderSTL(final LoaderListener listener, final int maxVerticesInChunk) {
        super(listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Constructor.
     *
     * @param f        file to be loaded.
     * @param listener listener to be notified of loading progress and when
     *                 loading process starts or finishes.
     * @throws IOException if an I/O error occurs.
     */
    public LoaderSTL(final File f, final LoaderListener listener) throws IOException {
        super(f, listener);
        loaderIterator = null;
        maxVerticesInChunk = DEFAULT_MAX_VERTICES_IN_CHUNK;
    }

    /**
     * Constructor.
     *
     * @param f                  file to be loaded.
     * @param listener           listener to be notified of loading progress and when
     *                           loading process starts or finishes.
     * @param maxVerticesInChunk Maximum number of vertices allowed in a chunk.
     *                           Once this value is exceeded when loading a file, a new chunk of data is
     *                           created.
     * @throws IllegalArgumentException if maximum number of vertices allowed in
     *                                  a chunk is lower than 1.
     * @throws IOException              if an I/O error occurs.
     */
    public LoaderSTL(final File f, final LoaderListener listener, final int maxVerticesInChunk)
            throws IOException {
        super(f, listener);
        loaderIterator = null;
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Sets maximum number of vertices allowed in a chunk.
     * Once this value is exceeded when loading a file, a new chunk of data is
     * created.
     *
     * @param maxVerticesInChunk maximum allowed number of vertices to be set.
     * @throws IllegalArgumentException if provided value is lower than 1.
     * @throws LockedException          if this loader is currently loading a file.
     */
    public void setMaxVerticesInChunk(final int maxVerticesInChunk)
            throws LockedException {
        if (isLocked()) {
            throw new LockedException();
        }
        internalSetMaxVerticesInChunk(maxVerticesInChunk);
    }

    /**
     * Returns maximum number of vertices allowed in a chunk.
     * Once this value is exceeded when loading a file, a new chunk of data is
     * created.
     *
     * @return maximum number of vertices allowed in a chunk.
     */
    public int getMaxVerticesInChunk() {
        return maxVerticesInChunk;
    }

    /**
     * If loader is ready to start loading a file.
     * This is true once a file has been provided.
     *
     * @return true if ready to start loading a file, false otherwise.
     */
    @Override
    public boolean isReady() {
        return hasFile();
    }

    /**
     * Returns mesh format supported by this class, which is MESH_FORMAT_STL.
     *
     * @return mesh format supported by this class.
     */
    @Override
    public MeshFormat getMeshFormat() {
        return MeshFormat.MESH_FORMAT_STL;
    }

    /**
     * Determines if provided file is a valid file that can be read by this
     * loader.
     *
     * @return true if file is valid, false otherwise.
     * @throws LockedException raised if this instance is already locked.
     * @throws IOException     if an I/O error occurs.
     */
    @Override
    public boolean isValidFile() throws LockedException, IOException {
        if (!hasFile()) {
            throw new IOException();
        }
        if (isLocked()) {
            throw new LockedException();
        }
        return true;
    }

    /**
     * Starts the loading process of provided file.
     * This method returns a LoaderIterator to start the iterative process to
     * load a file in small chunks of data.
     *
     * @return a loader iterator to read the file in a step-by-step process.
     * @throws LockedException   raised if this instance is already locked.
     * @throws NotReadyException raised if this instance is not yet ready.
     * @throws IOException       if an I/O error occurs.
     * @throws LoaderException   if file is corrupted or cannot be interpreted.
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

        setLocked(true);
        if (listener != null) {
            listener.onLoadStart(this);
        }

        loaderIterator = new LoaderIteratorSTL(this);
        loaderIterator.setListener(new LoaderIteratorListenerImpl(this));
        return loaderIterator;
    }

    /**
     * Returns name for the 3D object.
     *
     * @return name for the 3D object.
     */
    protected String getSolidName() {
        return loaderIterator != null ? loaderIterator.getSolidName() : null;
    }

    /**
     * Gets number of vertices contained in the file.
     *
     * @return number of vertices contained in the file.
     */
    protected Long getNumberOfVertices() {
        return loaderIterator != null ? loaderIterator.getNumberOfVertices() : null;
    }


    /**
     * Internal method to set maximum number of vertices allowed in a chunk.
     * This method is reused both in the constructor and in the setter of
     * maximum number of vertices allowed in a chunk.
     *
     * @param maxVerticesInChunk maximum allowed number of vertices to be set.
     * @throws IllegalArgumentException if provided value is lower than 1.
     */
    private void internalSetMaxVerticesInChunk(final int maxVerticesInChunk) {
        if (maxVerticesInChunk < MIN_MAX_VERTICES_IN_CHUNK) {
            throw new IllegalArgumentException();
        }

        this.maxVerticesInChunk = maxVerticesInChunk;
    }

    /**
     * Internal listener to be notified when loading process finishes.
     * This listener is used to free resources when loading process finishes.
     */
    private class LoaderIteratorListenerImpl implements LoaderIteratorListener {
        /**
         * Reference to Loader loading an STL file.
         */
        private final LoaderSTL loader;

        public LoaderIteratorListenerImpl(final LoaderSTL loader) {
            this.loader = loader;
        }

        /**
         * Method to be notified when the loading process finishes.
         *
         * @param iterator iterator loading the file in chunks.
         */
        @Override
        public void onIteratorFinished(final LoaderIterator iterator) {
            // because iterator is finished, we should allow subsequent calls to
            // load method
            try {
                // attempt restart stream to initial position
                reader.seek(0);
            } catch (final Exception ignore) {
                // this is the best effort operation, if it fails it is ignored
            }


            // on subsequent calls
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
    private class LoaderIteratorSTL implements LoaderIterator {

        /**
         * Reference to loader loading STL file.
         */
        private final LoaderSTL loader;

        /**
         * X coordinate of the latest point that has been read.
         */
        private float coordX;

        /**
         * Y coordinate of the latest point that has been read.
         */
        private float coordY;

        /**
         * Z coordinate of the latest point that has been read.
         */
        private float coordZ;

        /**
         * X coordinate of the latest point normal that has been read.
         */
        private float nX;

        /**
         * Y coordinate of the latest point normal that has been read.
         */
        private float nY;

        /**
         * Z coordinate of the latest point normal that has been read.
         */
        private float nZ;

        // coordinates for bounding box in a chunk

        /**
         * X coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float minX;

        /**
         * Y coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float minY;

        /**
         * Z coordinate of the minimum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float minZ;

        /**
         * X coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float maxX;

        /**
         * Y coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float maxY;

        /**
         * Z coordinate of the maximum point forming the bounding box in a chunk
         * of data. This value will be updated while the chunk is being filled.
         */
        private float maxZ;

        /**
         * Reference to the listener of this loader iterator. This listener will
         * be notified when the loading process finishes so that resources can
         * be freed.
         */
        private LoaderIteratorListener listener;

        /**
         * Array containing vertices coordinates to be added to current chunk
         * of data.
         */
        private float[] coordsInChunkArray;

        /**
         * Array containing normal coordinates to be added to current chunk of
         * data.
         */
        private float[] normalsInChunkArray;

        /**
         * Array containing indices to be added to current chunk of data. Notice
         * that these indices are not the original indices appearing in the file.
         * Instead, they are indices referring to data in current chunk,
         * accounting for duplicate points, etc. This way, indices in a chunk
         * can be directly used to draw the chunk of data by the graphical layer.
         */
        private int[] indicesInChunkArray;

        /**
         * Number of vertices stored in chunk.
         */
        private int verticesInChunk;

        /**
         * Number of indices stored in chunk.
         */
        private int indicesInChunk;

        /**
         * Size of indices stored in chunk.
         */
        private int indicesInChunkSize;

        /**
         * Indicates if file is in ASCII format or in binary format.
         */
        private boolean isAscii;

        /**
         * Indicates number of triangles that are contained in the file (when
         * available).
         */
        private long numberOfTriangles;

        /**
         * Indicates current triangle being read (when available).
         */
        private long currentTriangle;

        /**
         * Contains number of vertices contained in the file.
         */
        private long numberOfVertices;

        /**
         * Indicates when end of file has been reached.
         */
        private boolean endOfFileReached;

        /**
         * Contains name for the 3D object.
         */
        private String solidName;

        /**
         * Constant defining beginning of 3D file.
         */
        public static final String ASCII_START = "solid";

        /**
         * Constant defining end of 3D file.
         */
        public static final String ASCII_END = "endsolid";

        /**
         * Constant defining a face (i.e. triangle or polygon).
         */
        public static final String ASCII_FACET = "facet";

        /**
         * Constant defining a normal.
         */
        public static final String ASCII_NORMAL = "normal";

        /**
         * Constant defining grouping levels.
         */
        public static final String ASCII_OUTER = "outer";

        /**
         * Constant defining a loop that will contain vertices.
         */
        public static final String ASCII_LOOP = "loop";

        /**
         * Constant defining a vertex.
         */
        public static final String ASCII_VERTEX = "vertex";

        /**
         * Constant defining end of loop containing vertices.
         */
        public static final String ASCII_END_LOOP = "endloop";

        /**
         * Constant defining end of 3D face (i.e. triangle or polygon).
         */
        public static final String ASCII_END_FACET = "endfacet";

        /**
         * Header size for binary format.
         */
        public static final int BINARY_HEADER_SIZE = 80;

        /**
         * Constant defining number of vertices in a triangle.
         */
        public static final int VERTICES_PER_TRIANGLE = 3;

        /**
         * Constructor.
         *
         * @param loader reference to loader loading binary file.
         * @throws IOException     if an I/O error occurs.
         * @throws LoaderException if file data is corrupt or cannot be
         *                         understood.
         */
        public LoaderIteratorSTL(final LoaderSTL loader) throws IOException,
                LoaderException {
            this.loader = loader;
            nX = nY = nZ = 1.0f;
            listener = null;
            coordsInChunkArray = null;
            normalsInChunkArray = null;
            indicesInChunkArray = null;

            verticesInChunk = indicesInChunk = indicesInChunkSize = 0;

            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;

            isAscii = false;
            numberOfTriangles = currentTriangle = 0;
            endOfFileReached = false;

            numberOfVertices = 0;

            setUp();
        }

        /**
         * Method to set listener of this loader iterator.
         * This listener will be notified when the loading process finishes.
         *
         * @param listener listener of this loader iterator.
         */
        public void setListener(final LoaderIteratorListener listener) {
            this.listener = listener;
        }

        /**
         * Indicates if there is another chunk of data to be loaded.
         *
         * @return true if there is another chunk of data, false otherwise.
         */
        @Override
        public boolean hasNext() {
            if (isAscii) {
                return !endOfFileReached;
            } else {
                return currentTriangle < numberOfTriangles;
            }
        }

        /**
         * Loads and returns next chunk of data, if available.
         *
         * @return next chunk of data.
         * @throws NotAvailableException thrown if no more data is available.
         * @throws LoaderException       if file data is corrupt or cannot be
         *                               understood.
         * @throws IOException           if an I/O error occurs.
         */
        @Override
        public DataChunk next() throws NotAvailableException, LoaderException,
                IOException {

            if (reader == null) {
                throw new IOException();
            }

            if (!hasNext()) {
                throw new NotAvailableException();
            }

            initChunkArrays();

            // reset chunk bounding box values
            minX = minY = minZ = Float.MAX_VALUE;
            maxX = maxY = maxZ = -Float.MAX_VALUE;

            // read data until chunk is full
            boolean endOfChunk = false;
            final long fileLength = file.length();

            final long progressStep = Math.max(
                    (long) (LoaderSTL.PROGRESS_DELTA * fileLength), 1);
            long previousPos = 0;

            try {
                if (isAscii) {

                    // ascii format
                    String word;
                    do {
                        // read facet
                        word = readNonEmptyWord();
                        if (word == null) {
                            // undefined word
                            throw new LoaderException();
                        }

                        if (word.equalsIgnoreCase(ASCII_FACET)) {
                            // read normal
                            word = readNonEmptyWord();
                            if (word == null) {
                                // undefined word
                                throw new LoaderException();
                            }

                            if (word.equalsIgnoreCase(ASCII_NORMAL)) {
                                // read 3 normal values

                                word = readNonEmptyWord();
                                nX = Float.parseFloat(word);
                                word = readNonEmptyWord();
                                nY = Float.parseFloat(word);
                                word = readNonEmptyWord();
                                nZ = Float.parseFloat(word);
                            } else {
                                // unexpected word
                                throw new LoaderException();
                            }

                        } else if (word.equalsIgnoreCase(ASCII_OUTER)) {
                            // next word has to be "loop"
                            word = readNonEmptyWord();
                            if (word == null) {
                                // undefined word
                                throw new LoaderException();
                            }

                            if (!word.equalsIgnoreCase(ASCII_LOOP)) {
                                // unexpected word
                                throw new LoaderException();
                            }

                        } else if (word.equalsIgnoreCase(ASCII_VERTEX)) {
                            // read vertex data
                            word = readNonEmptyWord();
                            coordX = Float.parseFloat(word);
                            word = readNonEmptyWord();
                            coordY = Float.parseFloat(word);
                            word = readNonEmptyWord();
                            coordZ = Float.parseFloat(word);

                            // add coordinates into chunk arrays
                            addNewVertexDataToChunk();

                            // check if chunk is full
                            if (verticesInChunk == loader.maxVerticesInChunk) {
                                // no more vertices can be added to this chunk
                                break;
                            }

                        } else if (word.equalsIgnoreCase(ASCII_END_LOOP)) {
                            // check if chunk is full
                            if (verticesInChunk + VERTICES_PER_TRIANGLE >=
                                    loader.maxVerticesInChunk) {
                                // no more triangles vertices can be added to
                                // this chunk
                                break;
                            }

                        } else if (word.equalsIgnoreCase(ASCII_END_FACET)) {

                            // check if chunk is full
                            if (verticesInChunk + VERTICES_PER_TRIANGLE >=
                                    loader.maxVerticesInChunk) {
                                // no more triangles vertices can be added to
                                // this chunk
                                break;
                            }
                        } else if (word.equalsIgnoreCase(ASCII_END)) {
                            endOfFileReached = true;
                            break;
                        } else {
                            // unexpected word
                            throw new LoaderException();
                        }

                        // compute progress
                        if ((loader.listener != null) &&
                                (reader.getPosition() - previousPos) >= progressStep) {
                            previousPos = reader.getPosition();
                            loader.listener.onLoadProgressChange(loader,
                                    (float) (reader.getPosition()) / fileLength);
                        }

                    } while (!endOfFileReached);

                } else {
                    do {
                        // read vertex data (triangle normal, triangle vertices
                        // and two bytes attribute byte count

                        // read vertex normals
                        nX = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        nY = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        nZ = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);

                        // read 1st vertex coordinates of triangle
                        coordX = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordY = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordZ = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);

                        // add coordinates into chunk arrays
                        addNewVertexDataToChunk();

                        // read 2nd vertex coordinates of triangle
                        coordX = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordY = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordZ = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);

                        // add coordinates into chunk arrays
                        addNewVertexDataToChunk();

                        // read 3rd vertex coordinates of triangle
                        coordX = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordY = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);
                        coordZ = reader.readFloat(
                                EndianType.LITTLE_ENDIAN_TYPE);

                        // add coordinates into chunk arrays
                        addNewVertexDataToChunk();

                        // read two bytes attribute byte count
                        reader.readUnsignedShort(EndianType.LITTLE_ENDIAN_TYPE);

                        currentTriangle++;

                        // compute progress
                        if ((loader.listener != null) &&
                                (reader.getPosition() - previousPos) >=
                                        progressStep) {
                            previousPos = reader.getPosition();
                            loader.listener.onLoadProgressChange(loader,
                                    (float) (reader.getPosition()) / fileLength);
                        }

                        if (verticesInChunk + VERTICES_PER_TRIANGLE >=
                                loader.maxVerticesInChunk) {
                            // no more triangles vertices can be added to this
                            // chunk
                            endOfChunk = true;
                            break;
                        }

                    } while (!reader.isEndOfStream() &&
                            currentTriangle < numberOfTriangles);

                    // file ended before all triangles were read
                    if (currentTriangle < numberOfTriangles && !endOfChunk) {
                        throw new LoaderException();
                    }
                }
            } catch (final IOException | LoaderException e) {
                throw e;
            } catch (final Exception e) {
                throw new LoaderException(e);
            }

            // trim arrays to store only needed data
            trimArrays();

            // Instantiate DataChunk with chunk arrays
            final DataChunk dataChunk = new DataChunk();

            dataChunk.setVerticesCoordinatesData(coordsInChunkArray);
            dataChunk.setMinX(minX);
            dataChunk.setMinY(minY);
            dataChunk.setMinZ(minZ);
            dataChunk.setMaxX(maxX);
            dataChunk.setMaxY(maxY);
            dataChunk.setMaxZ(maxZ);

            dataChunk.setIndicesData(indicesInChunkArray);

            dataChunk.setNormalsData(normalsInChunkArray);

            if (!hasNext() && listener != null) {
                // notify iterator finished
                listener.onIteratorFinished(this);
            }

            // if no more chunks are available, then close input reader
            if (!hasNext()) {
                reader.close();
            }

            return dataChunk;
        }

        /**
         * Returns name for the 3D object.
         *
         * @return name for the 3D object.
         */
        public String getSolidName() {
            return solidName;
        }

        /**
         * Gets number of vertices contained in the file.
         *
         * @return number of vertices contained in the file.
         */
        public long getNumberOfVertices() {
            return numberOfVertices;
        }

        /**
         * Internal method to read a word from ASCII file.
         *
         * @return next word that has been read.
         * @throws IOException if an I/O error occurs.
         */
        private String readNonEmptyWord() throws IOException {
            // read normal
            String word;
            do {
                // ignore empty words
                // (line feeds, etc.)
                word = reader.readWord();
            } while (word != null && word.isEmpty());
            return word;
        }

        /**
         * Initializes arrays forming current chunk of data.
         */
        private void initChunkArrays() {
            coordsInChunkArray = new float[loader.maxVerticesInChunk *
                    VERTICES_PER_TRIANGLE];
            normalsInChunkArray = new float[loader.maxVerticesInChunk *
                    VERTICES_PER_TRIANGLE];
            indicesInChunkArray = new int[loader.maxVerticesInChunk];

            verticesInChunk = indicesInChunk = 0;
            indicesInChunkSize = loader.maxVerticesInChunk;
        }

        /**
         * Adds data of last vertex being loaded to current chunk of data as a
         * new vertex.
         */
        private void addNewVertexDataToChunk() {
            int pos = 3 * verticesInChunk;

            coordsInChunkArray[pos] = coordX;
            normalsInChunkArray[pos] = nX;

            pos++;

            coordsInChunkArray[pos] = coordY;
            normalsInChunkArray[pos] = nY;

            pos++;

            coordsInChunkArray[pos] = coordZ;
            normalsInChunkArray[pos] = nZ;

            // update bounding box values
            if (coordX < minX) {
                minX = coordX;
            }
            if (coordY < minY) {
                minY = coordY;
            }
            if (coordZ < minZ) {
                minZ = coordZ;
            }

            if (coordX > maxX) {
                maxX = coordX;
            }
            if (coordY > maxY) {
                maxY = coordY;
            }
            if (coordZ > maxZ) {
                maxZ = coordZ;
            }

            // if arrays of indices become full, we need to resize them
            if (indicesInChunk >= indicesInChunkSize) {
                increaseIndicesArraySize();
            }
            indicesInChunkArray[indicesInChunk] = verticesInChunk;

            verticesInChunk++;
            indicesInChunk++;
        }

        /**
         * Increases size of arrays of data. This method is called when needed.
         */
        private void increaseIndicesArraySize() {
            final int newIndicesInChunkSize = indicesInChunkSize +
                    loader.maxVerticesInChunk;
            final int[] newIndicesInChunkArray = new int[newIndicesInChunkSize];

            // copy contents of old array
            System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray, 0,
                    indicesInChunkSize);

            // set new arrays and new size
            indicesInChunkArray = newIndicesInChunkArray;
            indicesInChunkSize = newIndicesInChunkSize;
        }

        /**
         * Trims arrays of data to reduce size of arrays to fit chunk data. This
         * method is loaded just before copying data to chunk being returned.
         */
        private void trimArrays() {
            if (verticesInChunk > 0) {
                final int elems = verticesInChunk * VERTICES_PER_TRIANGLE;

                final float[] newCoordsInChunkArray = new float[elems];
                final float[] newNormalsInChunkArray = new float[elems];

                // copy contents of old arrays
                System.arraycopy(coordsInChunkArray, 0, newCoordsInChunkArray,
                        0, elems);
                System.arraycopy(normalsInChunkArray, 0, newNormalsInChunkArray,
                        0, elems);

                // set new arrays
                coordsInChunkArray = newCoordsInChunkArray;
                normalsInChunkArray = newNormalsInChunkArray;
            } else {
                // allow garbage collection
                coordsInChunkArray = null;
                normalsInChunkArray = null;
            }

            if (indicesInChunk > 0) {
                final int[] newIndicesInChunkArray = new int[indicesInChunk];
                System.arraycopy(indicesInChunkArray, 0, newIndicesInChunkArray,
                        0, indicesInChunk);

                // set new array
                indicesInChunkArray = newIndicesInChunkArray;
            } else {
                // allow garbage collection
                indicesInChunkArray = null;
            }
        }

        /**
         * Setups loader iterator. This method is called when constructing
         * this iterator.
         *
         * @throws IOException     if an I/O error occurs.
         * @throws LoaderException if data is corrupted or cannot be understood.
         */
        private void setUp() throws IOException, LoaderException {
            // read start of file to determine whether it is in ascii or binary
            // format

            // move to start of file
            reader.seek(0);
            final int magicStartLength = ASCII_START.length();
            final byte[] buffer = new byte[magicStartLength];
            final int n = reader.read(buffer);
            if (n != magicStartLength) {
                throw new LoaderException();
            }
            final String str = new String(buffer);

            isAscii = str.equalsIgnoreCase(ASCII_START);
            if (isAscii) {
                // check
                if (reader.isEndOfStream()) {
                    throw new LoaderException();
                }
                // load solid name
                solidName = reader.readLine();
            } else {
                // Binary format (always is in little endian form)

                // set position after 80 byte header
                reader.seek(BINARY_HEADER_SIZE);

                // read number of triangles
                numberOfTriangles = reader.readUnsignedInt
                        (EndianType.LITTLE_ENDIAN_TYPE);
                numberOfVertices = VERTICES_PER_TRIANGLE * numberOfTriangles;
            }
        }

    }
}
