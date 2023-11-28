/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.util.Map;

public class TextMessage implements PayloadMessageInterface {

    private Map<String, Object> m_DataMap = null;
    private String m_RootNodeName;

    public TextMessage(String dataName, String message) {
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
