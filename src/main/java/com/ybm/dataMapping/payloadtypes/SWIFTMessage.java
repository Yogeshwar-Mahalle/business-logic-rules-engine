/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class SWIFTMessage implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(SWIFTMessage.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private String m_RootNodeName;
    private final String ROOT_NODE_NAME = "ROOT-NODE-NAME";
    private final String TEXT_DATA = "TEXTDATA";

    public SWIFTMessage(String dataName, String textMessage) {
        this.m_OrgMessage = textMessage;
        this.m_RootNodeName = dataName;

        String[] lines = textMessage.split(System.lineSeparator());
        this.m_DataMap = new LinkedHashMap<>();
        this.m_DataMap.put(TEXT_DATA, lines);
        this.m_DataMap.put(ROOT_NODE_NAME, this.m_RootNodeName);

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
