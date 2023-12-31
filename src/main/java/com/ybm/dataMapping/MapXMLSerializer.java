/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class MapXMLSerializer extends JsonSerializer<LinkedHashMap<String, Object>> {
    private static final Logger LOG = LoggerFactory.getLogger(MapXMLSerializer.class);
    private final String NAMESPACE_KEYWORD = "#{xmlns:";
    private final String ATTRIBUTE_STARTWITH = "#{";
    private final String ATTRIBUTE_ENDWITH = "}";

    /*public MapXMLSerializer() {
        this(null);
    }
    public MapXMLSerializer(Class t) {
        super(t);
    }*/

    @Override
    public void serialize(LinkedHashMap<String, Object> stringObjectHashMap, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        if (jsonGenerator instanceof ToXmlGenerator xmlGenerator)
        {
            //Start XML Root tag writing
            xmlGenerator.writeStartObject();

            String namespacePrefix = null;
            String namespaceURL = null;
            LinkedHashMap<String, String> attributeKeyValue = null;
            mapXMLSerialize(stringObjectHashMap,
                    xmlGenerator,
                    serializerProvider,
                    namespacePrefix,
                    namespaceURL,
                    attributeKeyValue,
                    false);

            //End XML Root tag writing
            xmlGenerator.writeEndObject();

        } else {
            //TODO :: Need to add logic for JSON Serialisation in case required, currently not in use.

            //Start JSON Root tag writing
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, Object> item : stringObjectHashMap.entrySet()) {
                if (item.getValue() == null) {
                    continue;
                }

                jsonGenerator.writeFieldName(item.getKey());
                jsonGenerator.writeString((String) item.getValue());
            }

            //End JSON Root tag writing
            jsonGenerator.writeEndObject();
        }
    }

    @Override
    public Class<LinkedHashMap<String, Object>> handledType() {
        Class<LinkedHashMap<String, Object>> typeClass = (Class<LinkedHashMap<String, Object>>)(Class<?>) LinkedHashMap.class;
        return typeClass;
    }

    private void mapXMLSerialize(Map<String, Object> stringObjectHashMap,
                                 ToXmlGenerator xmlGenerator,
                                 SerializerProvider serializerProvider,
                                 String namespacePrefix,
                                 String namespaceURL,
                                 LinkedHashMap<String, String> attributeKeyValue,
                                 boolean bNested) throws IOException {

        for (Map.Entry<String, Object> item : stringObjectHashMap.entrySet())
        {
            if (item.getValue() == null) {
                continue;
            }

            //Add attributes
            if (attributeKeyValue != null && !attributeKeyValue.isEmpty()) {
                //xmlGenerator.writeStartObject();
                for (Map.Entry<String, String> pair : attributeKeyValue.entrySet()) {
                    if (pair.getKey() != null && !pair.getKey().isEmpty()) {
                        String attributeKey = getAttributeKey(pair.getKey());
                        try {
                            xmlGenerator.getStaxWriter().writeAttribute(attributeKey, pair.getValue());
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                //xmlGenerator.writeEndObject();
                attributeKeyValue.clear();
            }


            attributeKeyValue = new LinkedHashMap<>();
            if( item.getValue() instanceof Map<?,?> )
            {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) item.getValue();
                String storeKeyToRemoveFromMap = null;
                for (String key : map.keySet()) {
                    if (key.startsWith(ATTRIBUTE_STARTWITH) && key.endsWith(ATTRIBUTE_ENDWITH)) {
                        namespacePrefix = getNamespacePrefix(key);
                        if( namespacePrefix != null )
                        {
                            storeKeyToRemoveFromMap = key;
                            namespaceURL = (String) map.get(key);
                        }
                        else {
                            attributeKeyValue.put(key, (String) map.get(key));
                        }
                    }
                }

                //Remove Namespace from MAP
                if(storeKeyToRemoveFromMap != null)
                    map.remove(storeKeyToRemoveFromMap);

                if(namespaceURL != null && namespacePrefix != null ) {
                    try {
                        //xmlGenerator.getStaxWriter().writeNamespace(namespacePrefix, namespaceURL);
                        xmlGenerator.getStaxWriter().setPrefix(namespacePrefix, namespaceURL);
                    } catch (XMLStreamException e) {
                        throw new RuntimeException(e);
                    }
                    QName qName = new QName(namespaceURL, item.getKey(), namespacePrefix);
                    xmlGenerator.setNextName(qName);
                    //xmlGenerator.writeFieldName(qName.getLocalPart());

                    //Clear Namespace Prefix & URL
                    namespacePrefix = null;
                    namespaceURL = null;
                }

                //Add attributes
                for (Map.Entry<String, String> pair : attributeKeyValue.entrySet()) {
                    if (pair.getKey() != null && !pair.getKey().isEmpty()) {
                        map.remove(pair.getKey());
                    }
                }

                //String tagName = namespacePrefix + ":" + item.getKey();
                String tagName = item.getKey();
                xmlGenerator.writeFieldName(tagName);

                //Handle blank key case for XML tag attribute
                Object obj = map.get("");
                if( obj != null )
                {
                    xmlGenerator.writeStartObject();
                    xmlGenerator.setNextIsAttribute(true);
                    for (Map.Entry<String, Object> pair : map.entrySet()) {
                        if( pair.getKey() != null && !pair.getKey().isEmpty() ) {
                            xmlGenerator.writeObjectField(pair.getKey(), pair.getValue());
                        }
                    }
                    xmlGenerator.setNextIsAttribute(false);
                    xmlGenerator.setNextIsUnwrapped(true);
                    xmlGenerator.writeObjectField(tagName, obj);
                    xmlGenerator.writeEndObject();
                }
                else {
                    if(!map.isEmpty())
                    {
                        xmlGenerator.writeStartObject();
                        mapXMLSerialize(map,
                                xmlGenerator,
                                serializerProvider,
                                namespacePrefix,
                                namespaceURL,
                                attributeKeyValue,
                                true);
                        xmlGenerator.writeEndObject();
                    }
                }
            }
            else {
                //String tagName = namespacePrefix + ":" + item.getKey();
                String tagName = item.getKey();
                xmlGenerator.writeObjectField(tagName, item.getValue());
            }
        }
    }

    private String getNamespacePrefix(String namespaceKey)
    {
        String result = null;
        if(namespaceKey.startsWith(NAMESPACE_KEYWORD))
        {
            result = namespaceKey.substring(NAMESPACE_KEYWORD.length(), namespaceKey.lastIndexOf(ATTRIBUTE_ENDWITH));
        }
        return result;
    }

    private String getAttributeKey(String namespaceKey)
    {
        String result = null;
        if(namespaceKey.startsWith(ATTRIBUTE_STARTWITH))
        {
            result = namespaceKey.substring(ATTRIBUTE_STARTWITH.length(), namespaceKey.lastIndexOf(ATTRIBUTE_ENDWITH));
        }
        return result;
    }

}

