/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class MessageWrapperVisitor implements TransformVisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final ObjectMapper m_JsonMapper = new ObjectMapper();
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getString() {
        //try {
            //return m_JsonMapper.writeValueAsString(m_PayloadMessageInterface.getDataMap());
            return (String) m_PayloadMessageInterface.getDataMap().get(m_PayloadMessageInterface.getRootNode());
        /*} catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
    }
}
