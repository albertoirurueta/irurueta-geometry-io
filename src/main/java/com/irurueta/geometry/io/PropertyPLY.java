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

/**
 * Property contained within a header element.
 */
@SuppressWarnings("WeakerAccess")
public class PropertyPLY {
    /**
     * Name of the property.
     */
    String name;
    
    /**
     * Property type (either scalar or list).
     */
    PropertyTypePLY type;
    
    /**
     * Data type of the value indicating length of the array for list properties.
     */
    DataTypePLY lengthType;
    
    /**
     * Data type for the values contained within this property.
     */
    DataTypePLY valueType;
    
    /**
     * Listener to read the value of this property contained within the byte 
     * read buffer and transform it into the appropriate data type for this 
     * property.
     */
    PLYReadValueFromBufferListener readValueFromBufferListener;    
    
    /**
     * Listener to read the appropriate amount of bytes from a PLY file 
     * corresponding to this property data type. The amount of bytes read are
     * stored within the read buffer.
     */
    PLYReadValueFromStreamListener readValueFromStreamListener;
    
    /**
     * Listener to read the length value of this property contained within the 
     * byte read buffer and transform it into the appropriate data type for this
     * property.
     */
    PLYReadValueFromBufferListener readLengthValueFromBufferListener;
    
    /**
     * Listener to read the appropriate amount of bytes from a PLY file 
     * corresponding to this property length data type. The amount of bytes 
     * read are stored within the read buffer.
     */
    PLYReadValueFromStreamListener readLengthValueFromStreamListener;
    
    /**
     * Constructor.
     * @param name name of this property.
     * @param valueType data type of the value of this property.
     */
    public PropertyPLY(String name, DataTypePLY valueType) {
        this.name = name;
        type = PropertyTypePLY.PROPERTY_PLY_SCALAR;
        lengthType = null;
        this.valueType = valueType;
        readValueFromBufferListener = null;
        readValueFromStreamListener = null;
    }
    
    /**
     * Constructor.
     * @param name name of this property.
     * @param lengthType data type of the length value of this property.
     * @param valueType data type of the value of this property.
     */
    public PropertyPLY(String name, DataTypePLY lengthType, 
            DataTypePLY valueType) {
        this.name = name;
        type = PropertyTypePLY.PROPERTY_PLY_LIST;
        this.lengthType = lengthType;
        this.valueType = valueType;
        readValueFromBufferListener = null;
        readValueFromStreamListener = null;
    }
        
    /**
     * Returns name of this property.
     * @return Name of this property.
     * @throws NotAvailableException Raised if name has not been already 
     * provided.
     */
    public String getName() throws NotAvailableException {
        if (!isNameAvailable()) {
            throw new NotAvailableException();
        }
        
        return name;
    }
    
    /**
     * Determines if name has already been provided and is ready for retrieval.
     * @return True if name is available, false otherwise.
     */
    public boolean isNameAvailable() {
        return (name != null);
    }
    
    /**
     * Property type (either scalar or list).
     * @return property type.
     * @throws NotAvailableException Raised if property type has not yet been
     * provided and is not available for retrieval.
     */
    public PropertyTypePLY getPropertyType() throws NotAvailableException {
        if (!isPropertyTypeAvailable()) {
            throw new NotAvailableException();
        }
        return type;
    }
    
    /**
     * Determines whether property type has been provided and is available for
     * retrieval.
     * @return True if property type has been provided, false otherwise.
     */
    public boolean isPropertyTypeAvailable() {
        return (type != null);
    }
    
    /**
     * Returns data type of the value indicating length of the array if this is
     * a list property.
     * @return data type of the value indicating length of the array if this is
     * a list property.
     * @throws NotAvailableException raised if property type has not yet been
     * provided and is not available for retrieval.
     */
    public DataTypePLY getLengthType() throws NotAvailableException {
        if (!isLengthTypeAvailable()) {
            throw new NotAvailableException();
        }
        return lengthType;
    }
    
    /**
     * Determines whether length property type has been provided and is 
     * available for retrieval.
     * @return True if length property type has been provided, false otherwise.
     */
    public boolean isLengthTypeAvailable() {
        return (lengthType != null);
    }
    
    /**
     * Returns data type for the value contained within this property.
     * @return data type for the value contained within this property.
     * @throws NotAvailableException raised if property type has not yet been
     * provided and is not available for retrieval.
     */
    public DataTypePLY getValueType() throws NotAvailableException {
        if (!isValueTypeAvailable()) {
            throw new NotAvailableException();
        }
        return valueType;
    }
    
    /**
     * Determines if data type for the value contained within this property has
     * already been provided and is available for retrieval.
     * @return True if data type for the value contained within this property
     * is available for retrieval, false otherwise.
     */
    public boolean isValueTypeAvailable() {
        return (valueType != null);
    }
    
    /**
     * Determines if this property is valid with the values that have already
     * been provided.
     * @return True if property is valid, false otherwise.
     */
    public boolean isValidProperty() {
        return isNameAvailable() && isValueTypeAvailable();
    }
    
    /**
     * Converts this property to string representation ready to be written in 
     * the header of a PLY file.
     * @return String representation of this property.
     */
    @Override
    public String toString() {
        //if element is invalid, return empty string
        if (!isValidProperty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder("property ");
        
        //depending if property is scalar or list
        switch (type) {
            case PROPERTY_PLY_SCALAR:
                //add value data type
                builder.append(valueType.getValue()).append(" ");
                break;
            case PROPERTY_PLY_LIST:
                //indicate it is a list by adding length data type and values
                //data type
                builder.append("list ").append(lengthType.getValue()).append(
                        " ").append(valueType.getValue()).append(" ");
                        
                break;
        }
        //add name
        builder.append(name).append("\n");
        
        return builder.toString();
    }
      
    /**
     * Returns listener to read the value of this property contained within the 
     * byte read buffer and transforms it into the appropriate data type for 
     * this property.
     * @return listener to read the value of this property.
     * @throws NotAvailableException Raised if listener has not yet been 
     * provided and is not available for retrieval.
     */
    public PLYReadValueFromBufferListener getReadValueFromBufferListener() 
            throws NotAvailableException {
        if (!isReadValueFromBufferListenerAvailable()) {
            throw new NotAvailableException();
        }
        
        return readValueFromBufferListener;
    }
    
    /**
     * Sets listener to read the value of this property contained within the
     * byte read buffer and transforms it into the appropriate data type for 
     * this property.
     * @param listener listener to read the value of this property.
     */
    public void setReadValueFromBufferListener(
            PLYReadValueFromBufferListener listener) {
        readValueFromBufferListener = listener;
    }
    
    /**
     * Determines if listener to read the value of this property has been 
     * provided and is available for retrieval or not.
     * @return True if listener is available, false otherwise.
     */
    public boolean isReadValueFromBufferListenerAvailable() {
        return readValueFromBufferListener != null;
    }
        
    /**
     * Returns listener to read the appropriate amount of bytes from a PLY file 
     * corresponding to this property data type. The amount of bytes read are
     * stored within the read buffer.
     * @return listener to read the appropriate amount of bytes from a PLY file.
     * @throws NotAvailableException Raised if listener has not yet been
     * provided and is not available for retrieval.
     */
    public PLYReadValueFromStreamListener getReadValueFromStreamListener()
            throws NotAvailableException {
        if (!isReadValueFromStreamListenerAvailable()) {
            throw new NotAvailableException();
        }
        
        return readValueFromStreamListener;
    }
    
    /**
     * Sets listener to read the appropriate amount of bytes from a PLY file
     * corresponding to this property data type. The amount of bytes read are
     * stored within the read buffer.
     * @param listener listener to read the appropriate amount of bytes from a
     * PLY file.
     */
    public void setReadValueFromStreamListener(
            PLYReadValueFromStreamListener listener) {
        readValueFromStreamListener = listener;
    }
    
    /**
     * Determines if listener to read the appropriate amount of bytes from a PLY
     * file has been provided and is available for retrieval or not.
     * @return True if listener is available, false otherwise.
     */
    public boolean isReadValueFromStreamListenerAvailable() {
        return readValueFromStreamListener != null;
    }
    
    /**
     * Returns listener to read the length value of this property contained 
     * within the byte read buffer and transform it into the appropriate data 
     * type for this property.
     * @return listener to read the length value of this property.
     * @throws NotAvailableException Raised if listener has not yet been
     * provided and is not available for retrieval.
     */
    public PLYReadValueFromBufferListener getReadLengthValueFromBufferListener()
            throws NotAvailableException {
        if (!isReadLengthValueFromBufferListenerAvailable()) {
            throw new NotAvailableException();
        }
        
        return readLengthValueFromBufferListener;
    }
    
    /**
     * Sets listener to read the length value of this property contained within 
     * the byte read buffer and transform it into the appropriate data type for 
     * this property.
     * @param listener listener to read the length value of this property.
     */
    public void setReadLengthValueFromBufferListener(
            PLYReadValueFromBufferListener listener) {
        readLengthValueFromBufferListener = listener;
    }
    
    /**
     * Determines if listener to read the length value of this property has been
     * provided and is available for retrieval or not.
     * @return True if listener is available, false otherwise.
     */
    public boolean isReadLengthValueFromBufferListenerAvailable() {
        return readLengthValueFromBufferListener != null;
    }
    
    /**
     * Returns listener to read the appropriate amount of bytes from a PLY file 
     * corresponding to this property length data type. The amount of bytes 
     * read are stored within the read buffer.
     * @return listener to read the appropriate amount of bytes from a PLY file.
     * @throws NotAvailableException Raised if listener has not yet been 
     * provided and is not available for retrieval.
     */
    public PLYReadValueFromStreamListener getReadLengthValueFromStreamListener()
            throws NotAvailableException {
        if (!isReadLengthValueFromStreamListenerAvailable()) {
            throw new NotAvailableException();
        }
        
        return readLengthValueFromStreamListener;
    }
    
    /**
     * Sets listener to read the appropriate amount of bytes from a PLY file
     * corresponding to this property length data type. The amount of bytes
     * read are stored within the read buffer.
     * @param listener listener to read the appropriate amount of bytes from a
     * PLY file.
     */
    public void setReadLengthValueFromStreamListener(
            PLYReadValueFromStreamListener listener) {
            readLengthValueFromStreamListener = listener;
    }
    
    /**
     * Determines if listener to read the appropriate amount of bytes from a PLY
     * file has been provided and is available for retrieval or not.
     * @return True if listener is available, false otherwise.
     */
    public boolean isReadLengthValueFromStreamListenerAvailable() {
        return readLengthValueFromStreamListener != null;
    }
}
