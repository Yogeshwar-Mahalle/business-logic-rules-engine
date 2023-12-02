/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvMessage implements PayloadMessageInterface {

    private final String m_OrgMessage;
    private Map<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public CsvMessage(String dataName, String csvMessage) throws IOException {
        this.m_OrgMessage = csvMessage;
        this.m_DataMap = new HashMap<>();
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

        this.m_DataMap.put(dataName, listOfMap );
        this.m_RootNodeName = dataName;
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
    public Map<String, Object> getDataMap() {
        return this.m_DataMap;
    }

}
