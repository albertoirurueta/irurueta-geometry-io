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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Reads a 3D object and converts it into custom binary format.
 */
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
     *
     * @param loader loader to load a 3D file.
     * @param stream stream where trans-coded data will be written to.
     */
    public MeshWriterBinary(final Loader loader, final OutputStream stream) {
        super(loader, stream);
    }

    /**
     * Constructor.
     *
     * @param loader   loader to load a 3D file.
     * @param stream   stream where trans-coded data will be written to.
     * @param listener listener to be notified of progress changes or when
     *                 transcoding process starts or finishes.
     */
    public MeshWriterBinary(final Loader loader, final OutputStream stream,
                            final MeshWriterListener listener) {
        super(loader, stream, listener);
    }

    /**
     * Processes input file provided to loader and writes it trans-coded into
     * output stream.
     *
     * @throws LoaderException   if 3D file loading fails.
     * @throws IOException       if an I/O error occurs.
     * @throws NotReadyException if mesh writer is not ready because either a
     *                           loader has not been provided or an output stream has not been provided.
     * @throws LockedException   if this mesh writer is locked processing a file.
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
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

            loader.setListener(this.internalListeners);

            // write version
            dataStream.writeByte(VERSION);

            final LoaderIterator iter = loader.load();

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;

            while (iter.hasNext()) {
                final DataChunk chunk = iter.next();
                if (listener != null) {
                    listener.onChunkAvailable(this, chunk);
                }

                final float[] coords = chunk.getVerticesCoordinatesData();
                final short[] colors = chunk.getColorData();
                final int[] indices = chunk.getIndicesData();
                final float[] textureCoords = chunk.getTextureCoordinatesData();
                final float[] normals = chunk.getNormalsData();
                final int colorComponents = chunk.getColorComponents();

                final boolean coordsAvailable = (coords != null);
                final boolean colorsAvailable = (colors != null);
                final boolean indicesAvailable = (indices != null);
                final boolean textureCoordsAvailable = (textureCoords != null);
                final boolean normalsAvailable = (normals != null);

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

                // compute size of material in bytes
                final Material material = chunk.getMaterial();
                // boolean indicating availability
                // of material
                int materialSizeInBytes = 1;

                if (material != null) {
                    // material id (int)
                    materialSizeInBytes += Integer.SIZE / 8;
                    // ambient color (RGB) -> 3 bytes

                    //boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isAmbientColorAvailable()) {
                        // RGB components
                        materialSizeInBytes += 3;
                    }

                    // diffuse color

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isDiffuseColorAvailable()) {
                        // RGB components
                        materialSizeInBytes += 3;
                    }

                    // specular color

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isSpecularColorAvailable()) {
                        // RGB components
                        materialSizeInBytes += 3;
                    }

                    // specular coefficient (float)

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isSpecularCoefficientAvailable()) {
                        materialSizeInBytes += Float.SIZE / 8;
                    }

                    // ambient texture map

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isAmbientTextureMapAvailable()) {
                        // id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }

                    // diffuse texture map

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isDiffuseTextureMapAvailable()) {
                        // id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }

                    // specular texture map

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isSpecularTextureMapAvailable()) {
                        // id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }

                    // alpha texture map

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isAlphaTextureMapAvailable()) {
                        // id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }

                    // bump texture map

                    // boolean indicating availability
                    materialSizeInBytes += 1;
                    if (material.isBumpTextureMapAvailable()) {
                        // id of texture (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture width (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                        // texture height (int)
                        materialSizeInBytes += Integer.SIZE / 8;
                    }

                    // transparency

                    // availability
                    materialSizeInBytes += 1;
                    if (material.isTransparencyAvailable()) {
                        // one byte 0-255 of
                        // transparency
                        materialSizeInBytes += 1;
                    }

                    // enum of illumination(int)
                    // boolean containing availability
                    materialSizeInBytes += 1;
                    if (material.isIlluminationAvailable()) {
                        materialSizeInBytes += Integer.SIZE / 8;
                    }
                }

                // compute size of chunk data in bytes
                int coordsSizeInBytes = 0;
                int colorsSizeInBytes = 0;
                int indicesSizeInBytes = 0;
                int textureCoordsSizeInBytes = 0;
                int normalsSizeInBytes = 0;

                if (coordsAvailable) {
                    coordsSizeInBytes = coords.length * Float.SIZE / 8;
                }
                if (colorsAvailable) {
                    colorsSizeInBytes = colors.length;
                }
                if (indicesAvailable) {
                    indicesSizeInBytes = indices.length * Short.SIZE / 8;
                }
                if (textureCoordsAvailable) {
                    textureCoordsSizeInBytes = textureCoords.length *
                            Float.SIZE / 8;
                }
                if (normalsAvailable) {
                    normalsSizeInBytes = normals.length * Float.SIZE / 8;
                }

                int chunkSize = materialSizeInBytes + coordsSizeInBytes +
                        colorsSizeInBytes + indicesSizeInBytes +
                        textureCoordsSizeInBytes + normalsSizeInBytes +
                        (5 * Integer.SIZE / 8) + // sizes
                        (6 * Float.SIZE / 8); // min/max values
                if (colorsAvailable) {
                    // bytes for number of color components
                    chunkSize += Integer.SIZE / 8;
                }

                // indicate that no more textures follow
                if (!ignoreTextureValidation) {
                    // byte below is written only once after all textures and
                    // before all vertex data
                    dataStream.writeBoolean(false);

                    // so that no more textures are
                    // written into stream
                    ignoreTextureValidation = true;
                }

                // write total chunk size
                dataStream.writeInt(chunkSize);

                // indicate material availability
                dataStream.writeBoolean(material != null);
                if (material != null) {
                    // material id
                    dataStream.writeInt(material.getId());

                    // ambient color
                    dataStream.writeBoolean(material.isAmbientColorAvailable());
                    if (material.isAmbientColorAvailable()) {
                        byte b = (byte) (material.getAmbientRedColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getAmbientGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getAmbientBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }

                    // diffuse color
                    dataStream.writeBoolean(material.isDiffuseColorAvailable());
                    if (material.isDiffuseColorAvailable()) {
                        byte b = (byte) (material.getDiffuseRedColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getDiffuseGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getDiffuseBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }

                    // specular color
                    dataStream.writeBoolean(material.
                            isSpecularColorAvailable());
                    if (material.isSpecularColorAvailable()) {
                        byte b = (byte) (material.getSpecularRedColor() &
                                0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getSpecularGreenColor() & 0x00ff);
                        dataStream.writeByte(b);
                        b = (byte) (material.getSpecularBlueColor() & 0x00ff);
                        dataStream.writeByte(b);
                    }

                    // specular coefficient (float)
                    dataStream.writeBoolean(
                            material.isSpecularCoefficientAvailable());
                    if (material.isSpecularCoefficientAvailable()) {
                        dataStream.writeFloat(
                                material.getSpecularCoefficient());
                    }

                    // ambient texture map
                    dataStream.writeBoolean(
                            material.isAmbientTextureMapAvailable());
                    if (material.isAmbientTextureMapAvailable()) {
                        final Texture tex = material.getAmbientTextureMap();
                        // texture id
                        dataStream.writeInt(tex.getId());
                        // texture width
                        dataStream.writeInt(tex.getWidth());
                        // texture height
                        dataStream.writeInt(tex.getHeight());
                    }

                    // diffuse texture map
                    dataStream.writeBoolean(
                            material.isDiffuseTextureMapAvailable());
                    if (material.isDiffuseTextureMapAvailable()) {
                        final Texture tex = material.getDiffuseTextureMap();
                        // texture id
                        dataStream.writeInt(tex.getId());
                        // texture width
                        dataStream.writeInt(tex.getWidth());
                        // texture height
                        dataStream.writeInt(tex.getHeight());
                    }

                    // specular texture map
                    dataStream.writeBoolean(
                            material.isSpecularTextureMapAvailable());
                    if (material.isSpecularTextureMapAvailable()) {
                        final Texture tex = material.getSpecularTextureMap();
                        // texture id
                        dataStream.writeInt(tex.getId());
                        // texture width
                        dataStream.writeInt(tex.getWidth());
                        // texture height
                        dataStream.writeInt(tex.getHeight());
                    }

                    // alpha texture map
                    dataStream.writeBoolean(
                            material.isAlphaTextureMapAvailable());
                    if (material.isAlphaTextureMapAvailable()) {
                        final Texture tex = material.getAlphaTextureMap();
                        // texture id
                        dataStream.writeInt(tex.getId());
                        // texture width
                        dataStream.writeInt(tex.getWidth());
                        // texture height
                        dataStream.writeInt(tex.getHeight());
                    }

                    // bump texture map
                    dataStream.writeBoolean(
                            material.isBumpTextureMapAvailable());
                    if (material.isBumpTextureMapAvailable()) {
                        final Texture tex = material.getBumpTextureMap();
                        // texture id
                        dataStream.writeInt(tex.getId());
                        // texture width
                        dataStream.writeInt(tex.getWidth());
                        // texture height
                        dataStream.writeInt(tex.getHeight());
                    }

                    dataStream.writeBoolean(material.isTransparencyAvailable());
                    if (material.isTransparencyAvailable()) {
                        final byte b = (byte) (material.getTransparency() & 0x00ff);
                        dataStream.writeByte(b);
                    }

                    dataStream.writeBoolean(material.isIlluminationAvailable());
                    if (material.isIlluminationAvailable()) {
                        dataStream.writeInt(material.getIllumination().value());
                    }
                }

                // write coords size
                dataStream.writeInt(coordsSizeInBytes);
                // write coords
                if (coordsAvailable) {
                    for (final float coord : coords) {
                        dataStream.writeFloat(coord);
                    }
                }

                // write colors size
                dataStream.writeInt(colorsSizeInBytes);
                // write colors
                if (colorsAvailable) {
                    int i = 0;
                    while (i < colors.length) {
                        final byte b = (byte) (colors[i] & 0x00ff);
                        dataStream.writeByte(b);
                        i++;
                    }
                    dataStream.writeInt(colorComponents);
                }

                // write indices size
                dataStream.writeInt(indicesSizeInBytes);
                // write indices
                if (indicesAvailable) {
                    for (final int index : indices) {
                        final short s = (short) (index & 0x0000ffff);
                        dataStream.writeShort(s);
                    }
                }

                // write texture coords
                dataStream.writeInt(textureCoordsSizeInBytes);
                // write texture coords
                if (textureCoordsAvailable) {
                    for (final float textureCoord : textureCoords) {
                        dataStream.writeFloat(textureCoord);
                    }
                }

                // write normals
                dataStream.writeInt(normalsSizeInBytes);
                // write normals
                if (normalsAvailable) {
                    for (final float normal : normals) {
                        dataStream.writeFloat(normal);
                    }
                }

                // write min/max x, y, z
                dataStream.writeFloat(chunk.getMinX());
                dataStream.writeFloat(chunk.getMinY());
                dataStream.writeFloat(chunk.getMinZ());

                dataStream.writeFloat(chunk.getMaxX());
                dataStream.writeFloat(chunk.getMaxY());
                dataStream.writeFloat(chunk.getMaxZ());

                dataStream.flush();
            }

            if (listener != null) {
                listener.onWriteEnd(this);
            }
            locked = false;

        } catch (final LoaderException | IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new LoaderException(e);
        }
    }

    /**
     * Processes texture file. By reading provided texture file that has been
     * created in a temporal location and embedding it into resulting output
     * stream.
     *
     * @param texture     reference to texture that uses texture image.
     * @param textureFile file containing texture image. File will usually be
     *                    created in a temporal location.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void processTextureFile(final Texture texture, final File textureFile)
            throws IOException {
        if (!textureFile.exists()) {
            return;
        }

        // write boolean to true indicating that a texture follows
        dataStream.writeBoolean(true);

        // write int containing texture id
        final int textureId = texture.getId();
        dataStream.writeInt(textureId);

        // write int containing image width
        final int width = texture.getWidth();
        dataStream.writeInt(width);

        // write int containing image height
        final int height = texture.getHeight();
        dataStream.writeInt(height);

        // write length of texture file in bytes
        final long length = textureFile.length();
        dataStream.writeLong(length);

        // write file data
        try (final InputStream textureStream = Files.newInputStream(textureFile.toPath())) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = textureStream.read(buffer)) > 0) {
                dataStream.write(buffer, 0, n);
            }
            dataStream.flush();
        }
    }
}
