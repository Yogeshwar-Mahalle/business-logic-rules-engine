/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;

public class ToYamlTransformerVisitor implements VisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final YAMLMapper m_YamlMapper = new YAMLMapper();
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {
        try {
            return m_YamlMapper.writeValueAsString(m_PayloadMessageInterface.getDataMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
