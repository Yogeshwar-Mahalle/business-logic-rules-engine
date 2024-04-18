/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.ybm.dataMapping.MapXMLSerializer;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ToXmlTransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ToXmlTransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private XmlMapper m_XmlMapper = new XmlMapper();

    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;

        /*XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        m_XmlMapper = new XmlMapper(new XmlFactory(input, factory));*/
    }

    @Override
    public String getResult() {
        try {

            SimpleModule module = new SimpleModule();
            module.addSerializer(new MapXMLSerializer( m_PayloadMessageInterface.getRootNode() ));
            m_XmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            m_XmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            m_XmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
            m_XmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            m_XmlMapper.registerModule(module);

            return m_XmlMapper
                    .writer()
                    .withRootName( m_PayloadMessageInterface.getRootNode() )
                    .writeValueAsString( m_PayloadMessageInterface.getDataMap() );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
