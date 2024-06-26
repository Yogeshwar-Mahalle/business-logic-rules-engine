/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class JsonMessage implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMessage.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public JsonMessage(String dataName, String jsonMessage) throws JsonProcessingException {
        this.m_OrgMessage = jsonMessage;
        ObjectMapper m_JsonMapper = new ObjectMapper();
        this.m_DataMap = m_JsonMapper.readValue(jsonMessage, new TypeReference<LinkedHashMap<String, Object>>() {});
        this.m_RootNodeName = dataName;

        removeNull(this.m_DataMap);
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
    public LinkedHashMap<String, Object> getDataMap() {
        return this.m_DataMap;
    }

    private void removeNull(Map map) {
        if( map != null ) {
            map.values().removeIf(Objects::isNull);
            for (Object value : map.values()) {
                if (value instanceof Map) {
                    // Apply a recursion on inner maps
                    removeNull((Map) value);
                }
            }
        }
    }

}
