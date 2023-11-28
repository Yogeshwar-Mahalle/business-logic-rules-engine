/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.util.Map;

public class JsonMessage implements PayloadMessageInterface {
    private Map<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public JsonMessage(String dataName, String jsonMessage) throws JsonProcessingException {
        ObjectMapper m_JsonMapper = new ObjectMapper();
        m_DataMap = m_JsonMapper.readValue(jsonMessage, new TypeReference<Map<String, Object>>() {});
        m_RootNodeName = dataName;
    }


    @Override
    public String accept(TransformVisitorInterface transformVisitorInterface) {
        transformVisitorInterface.visit(this);
        return transformVisitorInterface.getString();
    }

    @Override
    public void processor(ProcessingInterface processingInterface) {

    }

    @Override
    public String getRootNode() {
        return m_RootNodeName;
    }

    @Override
    public Map<String, Object> getDataMap() {
        return this.m_DataMap;
    }
}
