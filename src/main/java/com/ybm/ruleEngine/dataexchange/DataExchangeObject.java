/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine.dataexchange;

import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class DataExchangeObject implements Serializable {
    private String uniqueExchangeId;
    private Map<String, Object> properties;
    private DataObject dataObject;
    private final DataObject originalDataObject;

    public DataExchangeObject(String uniqueExchangeId, Map<String, Object> properties, DataObject dataObject, DataObject originalDataObject) {
        this.uniqueExchangeId = uniqueExchangeId;
        this.properties = properties;
        this.dataObject = dataObject;
        this.originalDataObject = originalDataObject.copy();
    }

    public DataExchangeObject(DataExchangeObject dataExchangeObject) {
        this.uniqueExchangeId = dataExchangeObject.uniqueExchangeId;
        this.properties = dataExchangeObject.properties.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.dataObject = dataExchangeObject.dataObject.copy();
        this.originalDataObject = dataExchangeObject.originalDataObject.copy();
    }

    public void setUniqueExchangeId(String uniqueExchangeId) {
        this.uniqueExchangeId = uniqueExchangeId;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public String toString() {
        return "DataExchangeObject{" +
                "uniqueExchangeId='" + uniqueExchangeId + '\'' +
                ", properties=" + properties +
                ", dataObject=" + dataObject +
                ", orignalDataObject=" + originalDataObject +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataExchangeObject that)) return false;
        return Objects.equals(uniqueExchangeId, that.uniqueExchangeId) && Objects.equals(properties, that.properties) && Objects.equals(dataObject, that.dataObject) && Objects.equals(originalDataObject, that.originalDataObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueExchangeId, properties, dataObject, originalDataObject);
    }

    public DataExchangeObject copy() {
        return new DataExchangeObject(this);
    }
}
