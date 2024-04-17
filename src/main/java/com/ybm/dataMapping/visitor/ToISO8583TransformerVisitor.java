/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.visitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import com.ybm.dataMapping.models.ISO8583Structure;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ToISO8583TransformerVisitor implements VisitorInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ToISO8583TransformerVisitor.class);
    private PayloadMessageInterface m_PayloadMessageInterface = null;
    private final ObjectMapper m_JsonMapper = new ObjectMapper();
    /**
     * ISO8583Structure to get all ISO 8583 fields
     */
    public ISO8583Structure iso8583Structure = new ISO8583Structure();

    String configStr = "{\"f41\":{\"format\":\"%-8s\", \"variable\":\"card_acceptor_terminal\", \"options\":\"\", \"field_length\":\"8\", \"type\":\"STRING\"},\"f63\":{\"format\":\"%-32s%-30s%-50s%-18s\", \"variable\":\"locket_code, locket_name, locket_address, locket_phone\", \"options\":\"\", \"field_length\":\"130\", \"type\":\"LLLVAR\"},\"f32\":{\"format\":\"%-11s\", \"variable\":\"acq_institution_code\", \"options\":\"\", \"field_length\":\"11\", \"type\":\"LLVAR\"},\"f42\":{\"format\":\"%15s\", \"variable\":\"acceptor_identification_code\", \"options\":\"\", \"field_length\":\"15\", \"type\":\"STRING\"},\"f121\":{\"format\":\"%-32s\", \"variable\":\"payment_reference\", \"options\":\"\", \"field_length\":\"32\", \"type\":\"LLLVAR\"},\"f12\":{\"format\":\"%-6s\", \"variable\":\"local_time\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"STRING\"},\"f120\":{\"format\":\"%-20s\", \"variable\":\"product_code\", \"options\":\"\", \"field_length\":\"20\", \"type\":\"LLLVAR\"},\"f11\":{\"format\":\"%06d\", \"variable\":\"stan\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"NUMERIC\"},\"f33\":{\"format\":\"%-11s\", \"variable\":\"fwd_institution_code\", \"options\":\"\", \"field_length\":\"11\", \"type\":\"LLVAR\"},\"f13\":{\"format\":\"%-4s\", \"variable\":\"local_date\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"STRING\"},\"f49\":{\"format\":\"%03d\", \"variable\":\"transaction_currency_code\", \"options\":\"\", \"field_length\":\"3\", \"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%-4s\", \"variable\":\"settlement_date\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"STRING\"},\"f37\":{\"format\":\"%012d\", \"variable\":\"reference_number\", \"options\":\"\", \"field_length\":\"12\", \"type\":\"NUMERIC\"},\"f48\":{\"format\":\"%11s%12s%01d\", \"variable\":\"meter_id, customer_id, id_selector\", \"options\":\"\", \"field_length\":\"24\", \"type\":\"LLLVAR\"},\"f2\":{\"format\":\"%-19s\", \"variable\":\"pan\", \"options\":\"\", \"field_length\":\"19\", \"type\":\"LLVAR\"},\"f18\":{\"format\":\"%04d\", \"variable\":\"merchant_type\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"NUMERIC\"},\"f3\":{\"format\":\"%06d\", \"variable\":\"processing_code\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\", \"variable\":\"amount\", \"options\":\"\", \"field_length\":\"12\", \"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\", \"variable\":\"transmission_date_time\", \"options\":\"\", \"field_length\":\"10\", \"type\":\"STRING\"},\"f127\":{\"format\":\"%-20s%-32s\", \"variable\":\"username, password\", \"options\":\"\", \"field_length\":\"52\", \"type\":\"LLLVAR\"}}";
    String mti_id = "0210";
    JSONObject config = new JSONObject(configStr);
    JSONObject jsonObject = new JSONObject();

    @Override
    public void visit(PayloadMessageInterface payloadMessageInterface) {
        this.m_PayloadMessageInterface = payloadMessageInterface;
    }

    @Override
    public String getResult() {

        String iso8583Batch = "";

        try {

            Object objValue = m_PayloadMessageInterface.getDataMap().get( m_PayloadMessageInterface.getRootNode() );

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

                for ( Object json : jsonArray )
                {
                    jsonObject = new JSONObject( m_JsonMapper.writeValueAsString( json ) );
                    String iso8583Msg = new String( buildISO8583(jsonObject, config, mti_id) );
                    iso8583Batch = iso8583Batch.concat( iso8583Msg + "\n" );
                }
            }
            else {
                jsonObject = new JSONObject( m_JsonMapper.writeValueAsString( objValue ) );
                iso8583Batch = new String(  buildISO8583(jsonObject, config, mti_id) );
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return iso8583Batch;
    }


    /**
     * ISO 8583 message builder
     * @param json Data package on common specs
     * @param formatMap Object containing format, variable, minimum length, maximum length, and other options
     * @param mti_id Message type
     * @return Return byte contains ISO 8583 message
     */
    public byte[] buildISO8583(JSONObject json, JSONObject formatMap, String mti_id)
    {
        this.iso8583Structure = new ISO8583Structure(mti_id, formatMap);

        byte[] message = null;
        JSONObject row = new JSONObject();
        String format = "";
        String variable = "";
        int field_length = 0;
        String data = "";
        String subdata = "";
        String fmt = "";
        int field = 0;
        String options = "";
        String key_tmp = "";
        String field_type = "";
        try
        {
            try
            {
                Set<?> s =  formatMap.keySet();
                Iterator<?> iter = s.iterator();
                do
                {
                    String key = iter.next().toString();
                    key_tmp = key.replaceAll("f", "");
                    key_tmp = key_tmp.replaceAll("_", "");
                    if(key_tmp.equals(""))
                    {
                        key_tmp = "0";
                    }
                    field = Integer.parseInt(key_tmp);

                    if(formatMap.get(key) != null)
                    {
                        String keySpecs = formatMap.get(key).toString();
                        row = new JSONObject(keySpecs);
                        format = row.get("format").toString();
                        variable = row.get("variable").toString();
                        variable = variable.trim();
                        field_length = Integer.parseInt(row.get("field_length").toString());
                        options = row.get("options").toString();
                        field_type = row.get("type").toString();
                        if(options.length() > 0)
                        {
                            json = ISO8583Structure.applyOption(json, options);
                        }
                        String subfield = "";
                        if(variable.length() > 0)
                        {
                            if(variable.contains(","))
                            {
                                // split format
                                String subformat[] = ISO8583Structure.listSubformat(format);
                                String vars[] = variable.split(",");
                                String subfiedldata = "";
                                int[] sfl = ISO8583Structure.getSubfieldLength(format);
                                int i;
                                for(i = 0; i<vars.length && i<subformat.length; i++)
                                {
                                    vars[i] = vars[i].trim();
                                    if(json.get(vars[i]) != null)
                                    {
                                        subdata = json.get(vars[i]).toString();
                                    }
                                    else
                                    {
                                        subdata = "";
                                    }
                                    if(subformat[i].contains("d"))
                                    {
                                        String tmp = subdata;
                                        tmp = tmp.replaceAll("[^\\d.\\-]", "");
                                        tmp = ISO8583Structure.lTrim(tmp, "0");
                                        if(tmp.equals(""))
                                        {
                                            tmp = "0";
                                        }
                                        long int_data = Long.parseLong(tmp);
                                        subfiedldata = String.format(subformat[i], int_data);
                                        if(subfiedldata.length() > sfl[i])
                                        {
                                            subfiedldata = ISO8583Structure.right(subfiedldata, sfl[i]);
                                        }
                                        subfield += subfiedldata;
                                    }
                                    else
                                    {
                                        subfiedldata = String.format(subformat[i], subdata);
                                        if(subfiedldata.length() > sfl[i])
                                        {
                                            subfiedldata = ISO8583Structure.left(subfiedldata, sfl[i]);
                                        }
                                        subfield += subfiedldata;
                                    }
                                }
                                field_length = ISO8583Structure.getSubfieldLengthTotal(format);
                                if(field_length > 0)
                                {
                                    fmt = "%-"+field_length+"s";
                                    data = String.format(fmt, subfield);
                                }
                                else
                                {
                                    fmt = "%-s";
                                    data = String.format(fmt, subfield);
                                }
                            }
                            else
                            {
                                variable = variable.trim();
                                if(json.get(variable) != null)
                                {
                                    subfield = json.get(variable).toString();
                                }
                                else
                                {
                                    subfield = "";
                                }
                                String data_type = "string";
                                if(format.contains("d") || field_type.equals("NUMERIC"))
                                {
                                    subfield = subfield.replaceAll("[^\\d.\\-]", "");
                                    subfield = ISO8583Structure.lTrim(subfield, "0");
                                    if(subfield.equals(""))
                                    {
                                        subfield = "0";
                                    }
                                    long val = Long.parseLong(subfield);
                                    subfield = String.format(format, val);
                                    data_type = "numeric";
                                }

                                field_length = ISO8583Structure.getSubfieldLengthTotal(format);
                                if(!data_type.equals("numeric"))
                                {
                                    if(field_length > 0)
                                    {
                                        fmt = "%-"+field_length+"s";
                                        data = String.format(fmt, subfield);

                                    }
                                    else
                                    {
                                        fmt = "%s";
                                        data = String.format(fmt, subfield);
                                    }
                                }
                                else
                                {
                                    subfield = subfield.replaceAll("[^\\d.\\-]", "");
                                    subfield = ISO8583Structure.lTrim(subfield, "0");
                                    if(subfield.equals(""))
                                    {
                                        subfield = "0";
                                    }
                                    long val = Long.parseLong(subfield);
                                    if(field_length > 0)
                                    {
                                        fmt = "%0"+field_length+"d";
                                        data = String.format(fmt, val);
                                    }
                                    else
                                    {
                                        fmt = "%d";
                                        data = String.format(fmt, val);
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
                            if(field_type.equals("AMOUNT"))
                            {
                                long data_amount = 0;
                                data = ISO8583Structure.lTrim(data, "0");
                                if(data.equals(""))
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("%012d", data_amount);
                                iso8583Structure.addValue(field, data, "AMOUNT", 12);
                            }
                            else if(field_type.equals("CNUMERIC"))
                            {
                                long data_amount = 0;
                                data = ISO8583Structure.lTrim(data, "0");
                                if(data.equals(""))
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("C%0"+field_length+"d", data_amount);
                                iso8583Structure.addValue(field, data, "CNUMERIC", field_length+1);
                            }
                            else if(field_type.equals("DNUMERIC"))
                            {
                                long data_amount = 0;
                                data = ISO8583Structure.lTrim(data, "0");
                                if(data.equals(""))
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("D%0"+field_length+"d", data_amount);
                                iso8583Structure.addValue(field, data, "DNUMERIC", field_length+1);
                            }
                            else if(field_type.equals("NUMERIC"))
                            {
                                long data_amount = 0;
                                data = ISO8583Structure.lTrim(data, "0");
                                data = data.trim();
                                if(data.equals(""))
                                {
                                    data = "0";
                                }
                                data_amount = Long.parseLong(data);
                                data = String.format("%0"+field_length+"d", data_amount);
                                iso8583Structure.addValue(field, data, "NUMERIC", field_length);
                            }
                            else
                            {
                                if((field_type.equals("LVAR") || field_type.equals("LLVAR") || field_type.equals("LLLVAR")) && !variable.contains(","))
                                {
                                    field_length = data.length();
                                    iso8583Structure.addValue(field, data, field_type, field_length);
                                }
                                else
                                {
                                    iso8583Structure.addValue(field, data, field_type, field_length);
                                }
                            }
                        }
                    }
                }
                while(iter.hasNext());
                message = iso8583Structure.toString().getBytes();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }

    
}
