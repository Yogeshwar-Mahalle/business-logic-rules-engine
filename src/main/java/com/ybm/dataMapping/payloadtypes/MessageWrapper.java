/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;

import java.util.HashMap;
import java.util.Map;

public class MessageWrapper implements PayloadMessageInterface {
    private final String m_OrgMessage;
    private Map<String, Object> m_DataMap = null;
    private String m_RootNodeName;

    public MessageWrapper(String dataName, String payloadMessage) throws JsonProcessingException {
        this.m_OrgMessage = payloadMessage;
        this.m_DataMap = new HashMap<>();
        this.m_RootNodeName = dataName;
        //ObjectMapper m_JsonMapper = new ObjectMapper();
        //m_DataMap = m_JsonMapper.readValue(jsonMessage, new TypeReference<Map<String, Object>>() {});
        this.m_DataMap.put(dataName, payloadMessage);
    }

    @Override
    public String accept(VisitorInterface visitorInterface) {
        visitorInterface.visit(this);
        return visitorInterface.getResult();
    }

    @Override
    public void processor(ProcessingInterface processingInterface) {
        processingInterface.process(this);
    }

    @Override
    public boolean validate() {
        //TODO :: Validate original message and enrich map for validation result

        return true;
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
