/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;

import java.util.List;
import java.util.Map;

public class ToCsvTransformerVisitor implements VisitorInterface {
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final CsvMapper m_CsvMapper = new CsvMapper();
    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {
        try {

            List<Map<String, Object>> list = (List<Map<String, Object>>) m_PayloadMessageInterface.getDataMap()
                    .get( m_PayloadMessageInterface.getRootNode() );

            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();

            list.get(0).keySet().forEach(csvSchemaBuilder::addColumn);
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

            /*if (map != null && !map.isEmpty()) {
                for (String col : map.keySet()) {
                    csvSchemaBuilder.addColumn(col);
                }
                csvSchema = csvSchemaBuilder.build().withLineSeparator(System.lineSeparator()).withHeader();
            }*/

            return m_CsvMapper.writerFor(List.class)
                    .with(csvSchema)
                    .writeValueAsString(list);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}