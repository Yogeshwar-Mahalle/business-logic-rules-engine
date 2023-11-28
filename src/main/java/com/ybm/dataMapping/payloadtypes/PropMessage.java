/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.util.Map;

public class PropMessage implements PayloadMessageInterface {

    private Map<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public PropMessage(String dataName, String csvMessage) throws JsonProcessingException {
        JavaPropsMapper m_PropMapper = new JavaPropsMapper();
        m_DataMap = m_PropMapper.readValue(csvMessage, new TypeReference<Map<String, Object>>(){});
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
