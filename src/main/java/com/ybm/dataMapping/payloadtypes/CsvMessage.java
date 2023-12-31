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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class CsvMessage implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(CsvMessage.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private final String m_RootNodeName;

    public CsvMessage(String dataName, String csvMessage) throws IOException {
        this.m_OrgMessage = csvMessage;
        this.m_DataMap = new LinkedHashMap<>();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper m_CsvMapper = new CsvMapper();
        MappingIterator<LinkedHashMap<String, Object>> mappingIterator =
                m_CsvMapper.readerFor(LinkedHashMap.class).with(csvSchema).readValues(csvMessage);

        List<LinkedHashMap<String, Object>> listOfMap = mappingIterator.readAll();
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
    public LinkedHashMap<String, Object> getDataMap() {
        return this.m_DataMap;
    }

}
