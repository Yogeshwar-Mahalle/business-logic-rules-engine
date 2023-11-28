/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class ToPropTransformerVisitor implements TransformVisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final JavaPropsMapper m_PropMapper = new JavaPropsMapper();
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getString() {
        try {
            return m_PropMapper.writeValueAsString(m_PayloadMessageInterface.getDataMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
