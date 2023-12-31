/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final ObjectMapper m_JsonMapper = new ObjectMapper();
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {
        //try {
            //return m_JsonMapper.writeValueAsString(m_PayloadMessageInterface.getDataMap());
            return "Transformation for expected message type is not supported.";
        /*} catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
    }
}
