/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class PropMessage implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PropMessage.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public PropMessage(String dataName, String csvMessage) throws JsonProcessingException {
        this.m_OrgMessage = csvMessage;
        JavaPropsMapper m_PropMapper = new JavaPropsMapper();
        this.m_DataMap = m_PropMapper.readValue(csvMessage, new TypeReference<LinkedHashMap<String, Object>>(){});
        this.m_RootNodeName = dataName;
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

}
