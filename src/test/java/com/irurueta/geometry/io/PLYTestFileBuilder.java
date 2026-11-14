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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Test helper that programmatically generates small PLY files (ASCII, binary little endian or binary big
 * endian) so that every data type, storage mode and face layout can be exercised without committing binaries.
 */
final class PLYTestFileBuilder {

    /**
     * ASCII storage format name.
     */
    static final String ASCII = "ascii";

    /**
     * Binary little endian storage format name.
     */
    static final String LITTLE = "binary_little_endian";

    /**
     * Binary big endian storage format name.
     */
    static final String BIG = "binary_big_endian";

    /**
     * All data type names supported by PLY files, including aliases.
     */
    static final List<String> TYPES = List.of("char", "uchar", "short", "ushort", "int", "uint", "float",
            "double", "int8", "uint8", "int16", "uint16", "int32", "uint32", "float32", "float64");

    /**
     * Value of the red color component of every generated vertex.
     */
    static final int RED = 10;

    /**
     * Value of the green color component of every generated vertex.
     */
    static final int GREEN = 20;

    /**
     * Value of the blue color component of every generated vertex.
     */
    static final int BLUE = 30;

    /**
     * Value of the alpha color component of every generated vertex.
     */
    static final int ALPHA = 40;

    /**
     * Default vertices (x, y, z): a planar square plus an apex.
     */
    private static final double[][] DEFAULT_VERTICES = {
            {0, 0, 0}, {10, 0, 0}, {10, 10, 0}, {0, 10, 0}, {5, 5, 10}};

    /**
     * Default faces: three triangles.
     */
    private static final int[][] DEFAULT_FACES = {{0, 1, 2}, {0, 2, 3}, {0, 1, 4}};

    /**
     * Storage format name.
     */
    private final String format;

    /**
     * Header comments.
     */
    private final List<String> comments = new ArrayList<>();

    /**
     * Data type of coordinates.
     */
    private String coordType = "float";

    /**
     * Data type of colors or null if there are no colors.
     */
    private String colorType;

    /**
     * Number of color components.
     */
    private int colorComponents = 3;

    /**
     * Data type of normals or null if there are no normals.
     */
    private String normalType;

    /**
     * Data type of texture coordinates or null if there are none.
     */
    private String textureType;

    /**
     * Data type of the number of indices of each face.
     */
    private String faceLengthType = "uchar";

    /**
     * Data type of face indices.
     */
    private String faceIndexType = "int";

    /**
     * Vertices to be written.
     */
    private final double[][] vertices = DEFAULT_VERTICES;

    /**
     * Faces to be written.
     */
    private int[][] faces = DEFAULT_FACES;

    /**
     * Constructor.
     *
     * @param format storage format name.
     */
    PLYTestFileBuilder(final String format) {
        this.format = format;
    }

    /**
     * Sets data type of coordinates.
     *
     * @param type data type name.
     * @return this instance.
     */
    PLYTestFileBuilder coordType(final String type) {
        coordType = type;
        return this;
    }

    /**
     * Enables colors.
     *
     * @param type       data type name.
     * @param components number of components (3 or 4).
     * @return this instance.
     */
    PLYTestFileBuilder colors(final String type, final int components) {
        colorType = type;
        colorComponents = components;
        return this;
    }

    /**
     * Enables normals (0, 0, 1).
     *
     * @param type data type name.
     * @return this instance.
     */
    PLYTestFileBuilder normals(final String type) {
        normalType = type;
        return this;
    }

    /**
     * Enables texture coordinates (s, t), which the loader does not interpret.
     *
     * @param type data type name.
     * @return this instance.
     */
    PLYTestFileBuilder texture(final String type) {
        textureType = type;
        return this;
    }

    /**
     * Sets data types of the face list.
     *
     * @param lengthType data type of list length.
     * @param indexType  data type of list values.
     * @return this instance.
     */
    PLYTestFileBuilder faceTypes(final String lengthType, final String indexType) {
        faceLengthType = lengthType;
        faceIndexType = indexType;
        return this;
    }

    /**
     * Sets faces to be written.
     *
     * @param faces faces.
     * @return this instance.
     */
    PLYTestFileBuilder faces(final int[][] faces) {
        this.faces = faces;
        return this;
    }

    /**
     * Adds a header comment.
     *
     * @param comment comment.
     * @return this instance.
     */
    PLYTestFileBuilder comment(final String comment) {
        comments.add(comment);
        return this;
    }

    /**
     * Builds the file content.
     *
     * @return file bytes.
     * @throws IOException if an I/O error occurs.
     */
    byte[] build() throws IOException {
        final var out = new ByteArrayOutputStream();
        out.write(header().getBytes(StandardCharsets.US_ASCII));

        final var ascii = ASCII.equals(format);
        final var order = BIG.equals(format) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        final var text = new StringBuilder();

        for (final var vertex : vertices) {
            final var values = new ArrayList<String>();
            final var buffer = ByteBuffer.allocate(256).order(order);
            for (var i = 0; i < 3; i++) {
                put(ascii, values, buffer, coordType, vertex[i]);
            }
            if (normalType != null) {
                put(ascii, values, buffer, normalType, 0);
                put(ascii, values, buffer, normalType, 0);
                put(ascii, values, buffer, normalType, 1);
            }
            if (colorType != null) {
                put(ascii, values, buffer, colorType, RED);
                put(ascii, values, buffer, colorType, GREEN);
                put(ascii, values, buffer, colorType, BLUE);
                if (colorComponents == 4) {
                    put(ascii, values, buffer, colorType, ALPHA);
                }
            }
            if (textureType != null) {
                put(ascii, values, buffer, textureType, 1);
                put(ascii, values, buffer, textureType, 2);
            }
            if (ascii) {
                text.append(String.join(" ", values)).append("\n");
            } else {
                out.write(buffer.array(), 0, buffer.position());
            }
        }

        for (final var face : faces) {
            final var values = new ArrayList<String>();
            final var buffer = ByteBuffer.allocate(512).order(order);
            put(ascii, values, buffer, faceLengthType, face.length);
            for (final var index : face) {
                put(ascii, values, buffer, faceIndexType, index);
            }
            if (ascii) {
                text.append(String.join(" ", values)).append("\n");
            } else {
                out.write(buffer.array(), 0, buffer.position());
            }
        }

        if (ascii) {
            out.write(text.toString().getBytes(StandardCharsets.US_ASCII));
        }
        return out.toByteArray();
    }

    /**
     * Writes the generated file.
     *
     * @param dir  directory where file is written.
     * @param name file name.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    File write(final Path dir, final String name) throws IOException {
        return writeBytes(dir, name, build());
    }

    /**
     * Writes bytes into a file.
     *
     * @param dir   directory where file is written.
     * @param name  file name.
     * @param bytes content.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    static File writeBytes(final Path dir, final String name, final byte[] bytes) throws IOException {
        final var path = dir.resolve(name);
        Files.write(path, bytes);
        return path.toFile();
    }

    /**
     * Writes a text file using ASCII encoding.
     *
     * @param dir  directory where file is written.
     * @param name file name.
     * @param text content.
     * @return written file.
     * @throws IOException if an I/O error occurs.
     */
    static File writeText(final Path dir, final String name, final String text) throws IOException {
        return writeBytes(dir, name, text.getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * Builds the textual header.
     *
     * @return header text.
     */
    private String header() {
        final var b = new StringBuilder("ply\nformat ").append(format).append(" 1.0\n");
        for (final var comment : comments) {
            b.append("comment ").append(comment).append("\n");
        }
        b.append("obj_info generated by tests\n");
        b.append("element vertex ").append(vertices.length).append("\n");
        for (final var name : new String[]{"x", "y", "z"}) {
            b.append("property ").append(coordType).append(" ").append(name).append("\n");
        }
        if (normalType != null) {
            for (final var name : new String[]{"nx", "ny", "nz"}) {
                b.append("property ").append(normalType).append(" ").append(name).append("\n");
            }
        }
        if (colorType != null) {
            final var names = colorComponents == 4 ? new String[]{"red", "green", "blue", "alpha"}
                    : new String[]{"red", "green", "blue"};
            for (final var name : names) {
                b.append("property ").append(colorType).append(" ").append(name).append("\n");
            }
        }
        if (textureType != null) {
            b.append("property ").append(textureType).append(" s\n");
            b.append("property ").append(textureType).append(" t\n");
        }
        b.append("element face ").append(faces.length).append("\n");
        b.append("property list ").append(faceLengthType).append(" ").append(faceIndexType)
                .append(" vertex_indices\n");
        b.append("end_header\n");
        return b.toString();
    }

    /**
     * Appends a value either as text or as binary.
     *
     * @param ascii  true to write text.
     * @param values text values.
     * @param buffer binary buffer.
     * @param type   data type name.
     * @param value  value to write.
     */
    private static void put(final boolean ascii, final List<String> values, final ByteBuffer buffer,
                            final String type, final double value) {
        final var floating = type.startsWith("float") || "double".equals(type);
        if (ascii) {
            values.add(floating ? Double.toString(value) : Long.toString((long) value));
            return;
        }

        switch (type) {
            case "char", "uchar", "int8", "uint8" -> buffer.put((byte) value);
            case "short", "ushort", "int16", "uint16" -> buffer.putShort((short) value);
            case "int", "uint", "int32", "uint32" -> buffer.putInt((int) value);
            case "float", "float32" -> buffer.putFloat((float) value);
            default -> buffer.putDouble(value);
        }
    }
}
