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

import java.util.LinkedList;
import java.util.List;

/**
 * Contains an element of the header in a PLY file.
 */
public class ElementPLY {
    /**
     * Name of the element (i.e. 'vertex' or 'face').
     */
    private final String name;

    /**
     * Number of instances of this element (i.e. number of vertices or faces).
     */
    private final long nInstances;

    /**
     * List of properties forming this element. For a property it could be x, y,
     * z properties for vertex coordinates, nx, ny, nz for vertex normals or
     * red, green, blue, alpha for vertex color.
     */
    private final List<PropertyPLY> properties;

    /**
     * Constructor.
     *
     * @param name       Name of this element (i.e. 'vertex' or 'face').
     * @param nInstances Number of instances of this element (i.e. number of
     *                   vertices or faces).
     */
    public ElementPLY(final String name, final long nInstances) {
        this.name = name;
        this.nInstances = nInstances;
        properties = new LinkedList<>();
    }

    /**
     * Constructor.
     *
     * @param name       Name of this element (i.e. 'vertex' or 'face').
     * @param nInstances Number of instances of this element (i.e. number of
     *                   vertices or faces).
     * @param property   Property to be set on this element.
     */
    public ElementPLY(final String name, final long nInstances, final PropertyPLY property) {
        this.name = name;
        this.nInstances = nInstances;
        properties = new LinkedList<>();
        properties.add(property);
    }

    /**
     * Constructor.
     *
     * @param name       Name of this element (i.e. 'vertex' or 'face').
     * @param nInstances Number of instances of this element (i.e. number of
     *                   vertices or faces).
     * @param properties List of properties to be assigned to this element.
     */
    public ElementPLY(final String name, final long nInstances,
                      final List<PropertyPLY> properties) {
        this.name = name;
        this.nInstances = nInstances;
        this.properties = properties;
    }

    /**
     * Returns name of this element.
     *
     * @return name of this element.
     * @throws NotAvailableException Raised if name is not available.
     */
    public String getName() throws NotAvailableException {
        if (!isNameAvailable()) {
            throw new NotAvailableException();
        }
        return name;
    }

    /**
     * Determines whether a name for this element has been provided or not.
     *
     * @return True if element is set (different of null), false otherwise.
     */
    public boolean isNameAvailable() {
        return (name != null);
    }

    /**
     * Returns number of instances of this element (i.e. number of vertices or
     * number of faces).
     *
     * @return Number of instances of this element in the file.
     */
    public long getNumberOfInstances() {
        return nInstances;
    }

    /**
     * Returns list of properties contained in this element.
     *
     * @return List of properties contained in this element.
     * @throws NotAvailableException Raised if no properties have already been
     *                               set.
     */
    public List<PropertyPLY> getProperties() throws NotAvailableException {
        if (!arePropertiesAvailable()) {
            throw new NotAvailableException();
        }
        return properties;
    }

    /**
     * Indicates whether properties have already been set and are available for
     * retrieval.
     *
     * @return True if properties are available, false otherwise.
     */
    public boolean arePropertiesAvailable() {
        return (properties != null);
    }

    /**
     * Indicates if this element is valid (i.e. it has a name)
     *
     * @return True if element is valid, false otherwise.
     */
    public boolean isValidElement() {
        return isNameAvailable() && arePropertiesAvailable();
    }

    /**
     * Converts this element to string representation ready to be written in the
     * header of a PLY file.
     *
     * @return String representation of this element.
     */
    @Override
    public String toString() {
        // if element is invalid, return empty string
        if (!isValidElement()) {
            return "";
        }

        final StringBuilder builder = new StringBuilder("element ");

        // add name and number of instances
        builder.append(name).append(" ").append(nInstances).append("\n");

        // add properties of this element

        for (final PropertyPLY property : properties) {
            // NOTE: properties already contain carrier return on their textual
            // representation
            builder.append(property.toString());
        }

        return builder.toString();
    }
}
