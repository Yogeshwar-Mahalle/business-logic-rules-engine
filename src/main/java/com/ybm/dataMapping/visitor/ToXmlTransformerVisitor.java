/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

public class ToXmlTransformerVisitor implements TransformVisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final XmlMapper m_XmlMapper = new XmlMapper();

    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getString() {
        try {
            return m_XmlMapper
                    .configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true )
                    .writer()
                    .withRootName(m_PayloadMessageInterface.getRootNode())
                    .writeValueAsString(m_PayloadMessageInterface.getDataMap()
                            .get(m_PayloadMessageInterface.getRootNode()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
