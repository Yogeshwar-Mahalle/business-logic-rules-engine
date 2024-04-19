/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.ISO8583FieldInfo;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import com.ybm.dataMapping.models.ISO8583Structure;
import com.ybm.dataMappingRepo.FieldsDataTransformMappingService;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ToISO8583TransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ToISO8583TransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final ObjectMapper m_JsonMapper = new ObjectMapper();
    private String m_LinkedEntity = "EuclidPro";
    private String m_Version = "01";
    private final FieldsDataTransformMappingService m_fieldsDataTransformMappingService;

    String configStr = "{\"f41\":{\"format\":\"%-8s\", \"name\":\"card_acceptor_terminal\", \"options\":\"\", \"length\":\"8\", \"type\":\"STRING\"},\"f63\":{\"format\":\"%-32s%-30s%-50s%-18s\", \"name\":\"locket_code, locket_name, locket_address, locket_phone\", \"options\":\"\", \"length\":\"130\", \"type\":\"LLLVAR\"},\"f32\":{\"format\":\"%-11s\", \"name\":\"acq_institution_code\", \"options\":\"\", \"length\":\"11\", \"type\":\"LLVAR\"},\"f42\":{\"format\":\"%15s\", \"name\":\"acceptor_identification_code\", \"options\":\"\", \"length\":\"15\", \"type\":\"STRING\"},\"f121\":{\"format\":\"%-32s\", \"name\":\"payment_reference\", \"options\":\"\", \"length\":\"32\", \"type\":\"LLLVAR\"},\"f12\":{\"format\":\"%-6s\", \"name\":\"local_time\", \"options\":\"\", \"length\":\"6\", \"type\":\"STRING\"},\"f120\":{\"format\":\"%-20s\", \"name\":\"product_code\", \"options\":\"\", \"length\":\"20\", \"type\":\"LLLVAR\"},\"f11\":{\"format\":\"%06d\", \"name\":\"stan\", \"options\":\"\", \"length\":\"6\", \"type\":\"NUMERIC\"},\"f33\":{\"format\":\"%-11s\", \"name\":\"fwd_institution_code\", \"options\":\"\", \"length\":\"11\", \"type\":\"LLVAR\"},\"f13\":{\"format\":\"%-4s\", \"name\":\"local_date\", \"options\":\"\", \"length\":\"4\", \"type\":\"STRING\"},\"f49\":{\"format\":\"%03d\", \"name\":\"transaction_currency_code\", \"options\":\"\", \"length\":\"3\", \"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%-4s\", \"name\":\"settlement_date\", \"options\":\"\", \"length\":\"4\", \"type\":\"STRING\"},\"f37\":{\"format\":\"%012d\", \"name\":\"reference_number\", \"options\":\"\", \"length\":\"12\", \"type\":\"NUMERIC\"},\"f48\":{\"format\":\"%11s%12s%01d\", \"name\":\"meter_id, customer_id, id_selector\", \"options\":\"\", \"length\":\"24\", \"type\":\"LLLVAR\"},\"f2\":{\"format\":\"%-19s\", \"name\":\"pan\", \"options\":\"\", \"length\":\"19\", \"type\":\"LLVAR\"},\"f18\":{\"format\":\"%04d\", \"name\":\"merchant_type\", \"options\":\"\", \"length\":\"4\", \"type\":\"NUMERIC\"},\"f3\":{\"format\":\"%06d\", \"name\":\"processing_code\", \"options\":\"\", \"length\":\"6\", \"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\", \"name\":\"amount\", \"options\":\"\", \"length\":\"12\", \"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\", \"name\":\"transmission_date_time\", \"options\":\"\", \"length\":\"10\", \"type\":\"STRING\"},\"f127\":{\"format\":\"%-20s%-32s\", \"name\":\"username, password\", \"options\":\"\", \"length\":\"52\", \"type\":\"LLLVAR\"}}";

    public ToISO8583TransformerVisitor(String linkedEntity, String version, FieldsDataTransformMappingService fieldsDataTransformMappingService) {
        m_LinkedEntity = linkedEntity;
        m_Version = version;
        m_fieldsDataTransformMappingService = fieldsDataTransformMappingService;
    }

    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {

        String iso8583Batch = "";

        try {

            Object objValue = m_PayloadMessageInterface.getDataMap().get( m_PayloadMessageInterface.getRootNode() );
            if( objValue instanceof Map || objValue instanceof ArrayList || objValue instanceof Object[] )
            {
                LinkedHashMap<String, LinkedHashMap<String, String>> formatMapStd = m_JsonMapper.readValue(configStr, new TypeReference<>() {});

                if( objValue instanceof Map )
                {
                    LinkedHashMap<String, Object> iso8583Map = (LinkedHashMap) objValue;

                    for( String mtiTypeKey : iso8583Map.keySet() )
                    {
                        LinkedHashMap<String, LinkedHashMap<String, String>> formatMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) formatMapStd.clone();
                        objValue = iso8583Map.get( mtiTypeKey );
                        iso8583Batch = iso8583Batch.concat( transformData( objValue, mtiTypeKey, formatMap ) );
                    }
                }
                else {
                    Object[] jsonArray = null;
                    if( objValue instanceof ArrayList )
                    {
                        jsonArray = ((ArrayList<?>) objValue).toArray();
                    }
                    else {
                        jsonArray = (Object[]) objValue;
                    }

                    for ( Object object : jsonArray ) {
                        LinkedHashMap<String, Object> iso8583Map = (LinkedHashMap) object;
                        for( String mtiTypeKey : iso8583Map.keySet() )
                        {
                            LinkedHashMap<String, LinkedHashMap<String, String>> formatMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) formatMapStd.clone();
                            objValue = iso8583Map.get( mtiTypeKey );
                            iso8583Batch = iso8583Batch.concat( transformData( objValue, mtiTypeKey, formatMap ) );
                        }
                    }
                }
            }
            else {
                LOG.error("Invalid data map.");
                return "Invalid data map.";
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return iso8583Batch;
    }

    /**
     * ISO 8583 message builder
     * @param objValue Data package on common specs
     * @param mtiTypeKey Message type identifier starts with MTI + number
     * @return Return byte contains ISO 8583 message
     */
    private String transformData( Object objValue, String mtiTypeKey, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap ) throws JsonProcessingException {
        String transformMapperName = m_LinkedEntity.concat("~ISO8583.".concat(mtiTypeKey).concat(".".concat(m_Version)));

        FieldsDataTransformMapping fieldsDataTransformMapping =
                m_fieldsDataTransformMappingService.getFieldsDataTransformMappingById(transformMapperName);
        if (fieldsDataTransformMapping != null) {
            formatMap = m_JsonMapper.readValue(fieldsDataTransformMapping.getMappingExpressionScript(), new TypeReference<>() {});
        }

        String iso8583Batch = "";
        String mtiType = mtiTypeKey.startsWith("MTI") ? mtiTypeKey.substring(3) : mtiTypeKey;

        if( objValue instanceof ArrayList || objValue instanceof Object[] )
        {
            Object[] jsonArray = null;
            if( objValue instanceof ArrayList )
            {
                jsonArray = ((ArrayList<?>) objValue).toArray();
            }
            else {
                jsonArray = (Object[]) objValue;
            }

            for ( Object dataMap : jsonArray )
            {
                String iso8583Msg = buildISO8583((LinkedHashMap) dataMap, formatMap, mtiType);
                iso8583Batch = iso8583Batch.concat( iso8583Msg + "\n" );
            }
        }
        else {
            iso8583Batch = buildISO8583((LinkedHashMap) objValue, formatMap, mtiType) + "\n";
        }

        return iso8583Batch;
    }

    /**
     * ISO 8583 message builder
     * @param dataMap Data package on common specs
     * @param formatMap Object containing format, variable, minimum length, maximum length, and other options
     * @param mti_id Message type
     * @return Return byte contains ISO 8583 message
     */
    private String buildISO8583(LinkedHashMap dataMap, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap, String mti_id) throws JsonProcessingException {

        String message = null;
        JSONObject jsonObject = new JSONObject( m_JsonMapper.writeValueAsString( dataMap ) );

        /**
         * ISO8583Structure to get all ISO 8583 fields
         */
        ISO8583Structure iso8583Structure = new ISO8583Structure(mti_id, formatMap);

        try
        {
            try
            {
                Set<?> set =  formatMap.keySet();
                Iterator<?> iter = set.iterator();
                do
                {
                    String key = iter.next().toString();
                    String key_tmp = key.replaceAll("f", "");
                    key_tmp = key_tmp.replaceAll("_", "");
                    if(key_tmp.isEmpty())
                    {
                        key_tmp = "0";
                    }
                    int field = Integer.parseInt(key_tmp);

                    if(formatMap.get(key) != null)
                    {
                        LinkedHashMap<String, String> fieldFormat = formatMap.get(key);
                        String format = fieldFormat.get(ISO8583FieldInfo.DataElementConfig.FORMAT.getText());
                        String variable = fieldFormat.get(ISO8583FieldInfo.DataElementConfig.NAME.getText());
                        variable = variable.trim();
                        int field_length = Integer.parseInt(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.LENGTH.getText()));
                        String options = fieldFormat.get(ISO8583FieldInfo.DataElementConfig.OPTIONS.getText());
                        ISO8583FieldInfo.FormatType field_type = ISO8583FieldInfo.FormatType.valueOf(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.TYPE.getText()));
                        if(!options.isEmpty())
                        {
                            jsonObject = iso8583Structure.applyOption(jsonObject, options);
                        }
                        String data = "";
                        StringBuilder subfield = new StringBuilder();
                        if(!variable.isEmpty())
                        {
                            String fmt = "";
                            if(variable.contains(","))
                            {
                                // split format
                                String[] subformat = iso8583Structure.listSubformat(format);
                                String[] vars = variable.split(",");
                                String subfiedldata = "";
                                int[] sfl = iso8583Structure.getSubfieldLength(format);
                                String subdata = "";
                                for(int i = 0; i<vars.length && i<subformat.length; i++)
                                {
                                    vars[i] = vars[i].trim();
                                    if(jsonObject.get(vars[i]) != null)
                                    {
                                        subdata = jsonObject.get(vars[i]).toString();
                                    }
                                    else
                                    {
                                        subdata = "";
                                    }
                                    if(subformat[i].contains("d"))
                                    {
                                        String tmp = subdata;
                                        tmp = tmp.replaceAll("[^\\d.\\-]", "");
                                        tmp = iso8583Structure.lTrim(tmp, "0");
                                        if(tmp.isEmpty())
                                        {
                                            tmp = "0";
                                        }
                                        long int_data = Long.parseLong(tmp);
                                        subfiedldata = String.format(subformat[i], int_data);
                                        if(subfiedldata.length() > sfl[i])
                                        {
                                            subfiedldata = iso8583Structure.right(subfiedldata, sfl[i]);
                                        }
                                        subfield.append(subfiedldata);
                                    }
                                    else
                                    {
                                        subfiedldata = String.format(subformat[i], subdata);
                                        if(subfiedldata.length() > sfl[i])
                                        {
                                            subfiedldata = iso8583Structure.left(subfiedldata, sfl[i]);
                                        }
                                        subfield.append(subfiedldata);
                                    }
                                }
                                field_length = iso8583Structure.getSubfieldLengthTotal(format);
                                if(field_length > 0)
                                {
                                    fmt = "%-"+field_length+"s";
                                    data = String.format(fmt, subfield.toString());
                                }
                                else
                                {
                                    fmt = "%-s";
                                    data = String.format(fmt, subfield.toString());
                                }
                            }
                            else
                            {
                                variable = variable.trim();
                                if(jsonObject.keySet().contains(variable))
                                {
                                    subfield = new StringBuilder(jsonObject.get(variable).toString());
                                }
                                else
                                {
                                    subfield = new StringBuilder();
                                }
                                ISO8583FieldInfo.Attribute data_type = ISO8583FieldInfo.Attribute.STRING;
                                if(format.contains("d") || field_type == ISO8583FieldInfo.FormatType.NUMERIC)
                                {
                                    subfield = new StringBuilder(subfield.toString().replaceAll("[^\\d.\\-]", ""));
                                    subfield = new StringBuilder(iso8583Structure.lTrim(subfield.toString(), "0"));
                                    if(subfield.isEmpty())
                                    {
                                        subfield = new StringBuilder("0");
                                    }
                                    long val = Long.parseLong(subfield.toString());
                                    subfield = new StringBuilder(String.format(format, val));
                                    data_type = ISO8583FieldInfo.Attribute.NUMERIC;
                                }

                                if(format.contains("f") || field_type == ISO8583FieldInfo.FormatType.FLOAT)
                                {
                                    subfield = new StringBuilder(subfield.toString().replaceAll("[^\\d.\\-]", ""));
                                    subfield = new StringBuilder(iso8583Structure.lTrim(subfield.toString(), "0"));
                                    if(subfield.isEmpty())
                                    {
                                        subfield = new StringBuilder("0.0");
                                    }
                                    double val = Double.parseDouble(subfield.toString());
                                    subfield = new StringBuilder(String.format(format, val));
                                    data_type = ISO8583FieldInfo.Attribute.FLOAT;
                                }

                                field_length = iso8583Structure.getSubfieldLengthTotal(format);
                                if(data_type != ISO8583FieldInfo.Attribute.NUMERIC && data_type != ISO8583FieldInfo.Attribute.FLOAT)
                                {
                                    if(field_length > 0)
                                    {
                                        fmt = "%-"+field_length+"s";
                                        data = String.format(fmt, subfield.toString());

                                    }
                                    else
                                    {
                                        fmt = "%s";
                                        data = String.format(fmt, subfield.toString());
                                    }
                                }
                                else
                                {
                                    subfield = new StringBuilder(subfield.toString().replaceAll("[^\\d.\\-]", ""));
                                    subfield = new StringBuilder(iso8583Structure.lTrim(subfield.toString(), "0"));
                                    if(subfield.isEmpty())
                                    {
                                        subfield = new StringBuilder("0");
                                    }

                                    if ( data_type == ISO8583FieldInfo.Attribute.FLOAT ) {
                                        double val = Double.parseDouble(subfield.toString());
                                        String[] tmp = format.split("\\.");
                                        String decimalPlaces = tmp[1] == null ? ".2f" : ".".concat(tmp[1]);
                                        if (field_length > 0) {
                                            fmt = "%0" + field_length + decimalPlaces;
                                            data = String.format(fmt, val);
                                        } else {
                                            fmt = "%.2f";
                                            data = String.format(fmt, val);
                                        }
                                    }
                                    else {
                                        long val = Long.parseLong(subfield.toString());
                                        if (field_length > 0) {
                                            fmt = "%0" + field_length + "d";
                                            data = String.format(fmt, val);
                                        } else {
                                            fmt = "%d";
                                            data = String.format(fmt, val);
                                        }
                                    }
                                }
                            }
                            if(data.length() < field_length)
                            {
                                data = String.format("%-"+field_length+"s", data);
                            }
                            if(data.length() > field_length)
                            {
                                data = data.substring(0, field_length);
                            }
                            if(field_type == ISO8583FieldInfo.FormatType.AMOUNT)
                            {
                                long data_amount = 0;
                                data = iso8583Structure.lTrim(data, "0");
                                if(data.isEmpty())
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);

                                format = ( format == null || format.isEmpty() ) ? "%012d" : format;
                                data = String.format(format, data_amount);
                                iso8583Structure.addValue(field, data, ISO8583FieldInfo.FormatType.AMOUNT, field_length, format);
                            }
                            else if(field_type == ISO8583FieldInfo.FormatType.CNUMERIC)
                            {
                                long data_amount = 0;
                                data = iso8583Structure.lTrim(data, "0");
                                if(data.isEmpty())
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("C%0" + field_length + "d", data_amount);
                                iso8583Structure.addValue(field, data, ISO8583FieldInfo.FormatType.CNUMERIC, field_length+1, format);
                            }
                            else if(field_type == ISO8583FieldInfo.FormatType.DNUMERIC)
                            {
                                long data_amount = 0;
                                data = iso8583Structure.lTrim(data, "0");
                                if(data.isEmpty())
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("D%0" + field_length + "d", data_amount);
                                iso8583Structure.addValue(field, data, ISO8583FieldInfo.FormatType.DNUMERIC, field_length+1, format);
                            }
                            else if(field_type == ISO8583FieldInfo.FormatType.NUMERIC)
                            {
                                long data_amount = 0;
                                data = iso8583Structure.lTrim(data, "0");
                                data = data.trim();
                                if(data.isEmpty())
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("%0" + field_length + "d", data_amount);
                                iso8583Structure.addValue(field, data, ISO8583FieldInfo.FormatType.NUMERIC, field_length, format);
                            }
                            else if(field_type == ISO8583FieldInfo.FormatType.FLOAT)
                            {
                                double data_amount = 0;
                                data = iso8583Structure.lTrim(data, "0");
                                data = data.trim();
                                if(data.isEmpty())
                                {
                                    data = "0";
                                }
                                data_amount = Double.parseDouble(data);

                                String[] tmp = format.split("\\.");
                                String decimalPlaces = tmp[1] == null ? ".2f" : ".".concat(tmp[1]);
                                data = String.format("%0" + field_length + decimalPlaces, data_amount);
                                iso8583Structure.addValue(field, data, ISO8583FieldInfo.FormatType.FLOAT, field_length, format);
                            }
                            else
                            {
                                if((field_type == ISO8583FieldInfo.FormatType.LVAR || field_type == ISO8583FieldInfo.FormatType.LLVAR || field_type == ISO8583FieldInfo.FormatType.LLLVAR) && !variable.contains(","))
                                {
                                    field_length = data.length();
                                    iso8583Structure.addValue(field, data, field_type, field_length, format);
                                }
                                else
                                {
                                    iso8583Structure.addValue(field, data, field_type, field_length, format);
                                }
                            }
                        }
                    }
                }
                while(iter.hasNext());
                message = iso8583Structure.toString();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return message;
    }

    
}
