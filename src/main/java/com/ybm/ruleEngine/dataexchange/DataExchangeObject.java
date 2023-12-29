/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine.dataexchange;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class DataExchangeObject implements Serializable {
    private final String uniqueExchangeId;
    private final DataObject inDataObject;
    private DataObject outDataObject;
    private Map<String, Object> properties;
    private Map<String, Object> dataExtension;

    public DataExchangeObject(String uniqueExchangeId,
                              Map<String, Object> properties,
                              DataObject inDataObject,
                              DataObject outDataObject,
                              Map<String, Object> dataExtension ) {
        this.uniqueExchangeId = uniqueExchangeId;
        this.properties = properties;
        this.inDataObject = inDataObject.copy();
        this.outDataObject = outDataObject;
        this.dataExtension = dataExtension;
    }

    public DataExchangeObject(DataExchangeObject dataExchangeObject) {
        this.uniqueExchangeId = dataExchangeObject.uniqueExchangeId;
        this.properties = dataExchangeObject.properties.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.inDataObject = dataExchangeObject.inDataObject.copy();
        this.outDataObject = dataExchangeObject.outDataObject.copy();
        this.dataExtension = dataExchangeObject.dataExtension.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String toString() {
        return "DataExchangeObject{" +
                "uniqueExchangeId='" + uniqueExchangeId + '\'' +
                ", properties=" + properties +
                ", inDataObject=" + inDataObject +
                ", outDataObject=" + outDataObject +
                ", dataExtension=" + dataExtension +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataExchangeObject that)) return false;
        return Objects.equals(uniqueExchangeId, that.uniqueExchangeId) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(inDataObject, that.inDataObject) &&
                Objects.equals(outDataObject, that.outDataObject) &&
                Objects.equals(dataExtension, that.dataExtension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueExchangeId, properties, inDataObject, outDataObject, dataExtension);
    }

    public DataExchangeObject copy() {
        return new DataExchangeObject(this);
    }
}
