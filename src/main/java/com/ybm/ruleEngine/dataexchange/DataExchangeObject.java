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
    private final String uniqueExchangeId;
    private final DataObject inDataObject;
    private DataObject outDataObject;
    private Map<String, Object> properties;
    private Map<String, Object> extData;

    public DataExchangeObject(String uniqueExchangeId, Map<String, Object> properties, DataObject inDataObject, DataObject outDataObject, Map<String, Object> extData ) {
        this.uniqueExchangeId = uniqueExchangeId;
        this.properties = properties;
        this.inDataObject = inDataObject.copy();
        this.outDataObject = outDataObject;
        this.extData = extData;
    }

    public DataExchangeObject(DataExchangeObject dataExchangeObject) {
        this.uniqueExchangeId = dataExchangeObject.uniqueExchangeId;
        this.properties = dataExchangeObject.properties.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.inDataObject = dataExchangeObject.inDataObject.copy();
        this.outDataObject = dataExchangeObject.outDataObject.copy();
        this.extData = dataExchangeObject.extData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    public void setExtData(Map<String, Object> extData) {
        this.extData = extData;
    }

    public void setOutDataObject(DataObject outDataObject) {
        this.outDataObject = outDataObject;
    }

    @Override
    public String toString() {
        return "DataExchangeObject{" +
                "uniqueExchangeId='" + uniqueExchangeId + '\'' +
                ", properties=" + properties +
                ", inDataObject=" + inDataObject +
                ", outDataObject=" + outDataObject +
                ", extData=" + extData +
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
                Objects.equals(extData, that.extData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueExchangeId, properties, inDataObject, outDataObject, extData);
    }

    public DataExchangeObject copy() {
        return new DataExchangeObject(this);
    }
}
