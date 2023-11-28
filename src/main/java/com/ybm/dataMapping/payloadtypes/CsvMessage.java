/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.TransformVisitorInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvMessage implements PayloadMessageInterface {

    private Map<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public CsvMessage(String dataName, String csvMessage) throws IOException {
        m_DataMap = new HashMap<>();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper m_CsvMapper = new CsvMapper();
        MappingIterator<Map<String, Object>> mappingIterator =
                m_CsvMapper.readerFor(Map.class).with(csvSchema).readValues(csvMessage);

        List<Map<String, Object>> listOfMap = mappingIterator.readAll();
        listOfMap.forEach(
                map -> {
                    map.forEach((key, value) -> {
                        String str = (String) value;
                        String[] array = str.split(";");
                        if (array.length > 1) {
                            map.put(key, array);
                        }
                    });
                }
                );

        m_DataMap.put(dataName, listOfMap );
        m_RootNodeName = dataName;
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
