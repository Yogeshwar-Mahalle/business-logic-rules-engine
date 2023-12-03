/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine.dataexchange;

import com.ybm.exchangeDataRepo.ContentType;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Payload implements Serializable {
    protected String strMessage;
    protected ContentType contentType;
    protected Map<String, Object> dataMap = new HashMap<>(); ;

    public Payload(String strMessage, ContentType contentType, Map<String, Object> dataMap) {
        this.strMessage = strMessage;
        this.contentType = contentType;
        this.dataMap = dataMap;
    }

    public Payload(Payload payload) {
        this.strMessage = String.valueOf(payload.strMessage);
        this.contentType = payload.contentType;

        if(payload.dataMap != null)
            this.dataMap.putAll(payload.dataMap);
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setDataMap(Map<String, Object> mapPayload) {
        this.dataMap = mapPayload;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "strMessage='" + strMessage + '\'' +
                ", contentType=" + contentType +
                ", dataMap=" + dataMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payload payload)) return false;
        return Objects.equals(strMessage, payload.strMessage) &&
                contentType == payload.contentType &&
                Objects.equals(dataMap, payload.dataMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strMessage, contentType, dataMap);
    }

    protected Payload copy() {
        return new Payload(this);
    }

}
