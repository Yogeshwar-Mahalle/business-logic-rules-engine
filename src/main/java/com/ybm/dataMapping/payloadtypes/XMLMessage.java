/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import lombok.Value;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;

public class XMLMessage implements PayloadMessageInterface {
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public XMLMessage(String dataName, String xmlMessage) throws IOException, ParserConfigurationException, SAXException {
        this.m_OrgMessage = xmlMessage;
        XmlMapper m_xmlMapper = new XmlMapper();
        this.m_DataMap = new LinkedHashMap<>();

        this.m_RootNodeName = m_xmlMapper.readValue(
                xmlMessage, XmlWrapper.class
        ).getXmlRootName();
        this.m_DataMap.put(this.m_RootNodeName, m_xmlMapper.readValue(xmlMessage, new TypeReference<LinkedHashMap<String, Object>>(){ }));
    }

    @Value
    @JsonDeserialize(using = XmlDeserializer.class)
    static class XmlWrapper {
        String xmlRootName;
    }

    static class XmlDeserializer extends JsonDeserializer<XmlWrapper> {
        @Override
        public XmlWrapper deserialize(JsonParser p, DeserializationContext ctxt) {
            return new XmlWrapper(((FromXmlParser) p).getStaxReader().getLocalName());
        }
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
