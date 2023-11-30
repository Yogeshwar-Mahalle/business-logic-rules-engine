/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.util.HashMap;
import java.util.Map;

public class MessageWrapper implements PayloadMessageInterface {
    private Map<String, Object> m_DataMap = null;
    private String m_RootNodeName;
    private String m_PayloadMessage;
    public MessageWrapper(String dataName, String payloadMessage) throws JsonProcessingException {
        m_DataMap = new HashMap<>();
        this.m_RootNodeName = dataName;
        this.m_PayloadMessage = payloadMessage;
        //ObjectMapper m_JsonMapper = new ObjectMapper();
        //m_DataMap = m_JsonMapper.readValue(jsonMessage, new TypeReference<Map<String, Object>>() {});
        m_DataMap.put(dataName, payloadMessage);
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
