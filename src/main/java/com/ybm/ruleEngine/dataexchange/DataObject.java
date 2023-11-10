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
public class DataObject implements Serializable {

    protected Map<String, String> headers;
    protected Payload payload;

    public DataObject(Map<String, String> headers, Payload payload) {
        this.headers = headers;
        this.payload = payload;
    }
    public DataObject(DataObject dataObject) {
        this.headers = dataObject.headers.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.payload = dataObject.payload.copy();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "headers=" + headers +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataObject that)) return false;
        return Objects.equals(headers, that.headers) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, payload);
    }

    protected DataObject copy() {
        return new DataObject(this);
    }
}
