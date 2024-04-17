/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import com.ybm.dataMapping.models.ISO8583Structure;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class ISO8583Message implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ISO8583Message.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private String m_RootNodeName;
    private final String ROOT_NODE_NAME = "ROOT-NODE-NAME";

    /**
     * ISO8583Structure to get all ISO 8583 fields
     */
    private ISO8583Structure iso8583Structure = new ISO8583Structure();

    String configStr = "{\"f41\":{\"format\":\"%-8s\", \"variable\":\"card_acceptor_terminal\", \"options\":\"\", \"field_length\":\"8\", \"type\":\"STRING\"},\"f63\":{\"format\":\"%-32s%-30s%-50s%-18s\", \"variable\":\"locket_code, locket_name, locket_address, locket_phone\", \"options\":\"\", \"field_length\":\"130\", \"type\":\"LLLVAR\"},\"f32\":{\"format\":\"%-11s\", \"variable\":\"acq_institution_code\", \"options\":\"\", \"field_length\":\"11\", \"type\":\"LLVAR\"},\"f42\":{\"format\":\"%15s\", \"variable\":\"acceptor_identification_code\", \"options\":\"\", \"field_length\":\"15\", \"type\":\"STRING\"},\"f121\":{\"format\":\"%-32s\", \"variable\":\"payment_reference\", \"options\":\"\", \"field_length\":\"32\", \"type\":\"LLLVAR\"},\"f12\":{\"format\":\"%-6s\", \"variable\":\"local_time\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"STRING\"},\"f120\":{\"format\":\"%-20s\", \"variable\":\"product_code\", \"options\":\"\", \"field_length\":\"20\", \"type\":\"LLLVAR\"},\"f11\":{\"format\":\"%06d\", \"variable\":\"stan\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"NUMERIC\"},\"f33\":{\"format\":\"%-11s\", \"variable\":\"fwd_institution_code\", \"options\":\"\", \"field_length\":\"11\", \"type\":\"LLVAR\"},\"f13\":{\"format\":\"%-4s\", \"variable\":\"local_date\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"STRING\"},\"f49\":{\"format\":\"%03d\", \"variable\":\"transaction_currency_code\", \"options\":\"\", \"field_length\":\"3\", \"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%-4s\", \"variable\":\"settlement_date\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"STRING\"},\"f37\":{\"format\":\"%012d\", \"variable\":\"reference_number\", \"options\":\"\", \"field_length\":\"12\", \"type\":\"NUMERIC\"},\"f48\":{\"format\":\"%11s%12s%01d\", \"variable\":\"meter_id, customer_id, id_selector\", \"options\":\"\", \"field_length\":\"24\", \"type\":\"LLLVAR\"},\"f2\":{\"format\":\"%-19s\", \"variable\":\"pan\", \"options\":\"\", \"field_length\":\"19\", \"type\":\"LLVAR\"},\"f18\":{\"format\":\"%04d\", \"variable\":\"merchant_type\", \"options\":\"\", \"field_length\":\"4\", \"type\":\"NUMERIC\"},\"f3\":{\"format\":\"%06d\", \"variable\":\"processing_code\", \"options\":\"\", \"field_length\":\"6\", \"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\", \"variable\":\"amount\", \"options\":\"\", \"field_length\":\"12\", \"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\", \"variable\":\"transmission_date_time\", \"options\":\"\", \"field_length\":\"10\", \"type\":\"STRING\"},\"f127\":{\"format\":\"%-20s%-32s\", \"variable\":\"username, password\", \"options\":\"\", \"field_length\":\"52\", \"type\":\"LLLVAR\"}}";

    String iso8583 = "0200F23A400188C180060000000000000182196048200000002731   300000000000020000092513425200007213425209250926602111597        1112345      000000000072DEVALT0120090010080000014514987654321149999999911030E8597E3B2F1646505FDD6E210000090MUP210ZBE957561167FCD8506326E4AHAMDANIE LESTALUHUANI    R1  00000090000000090000000011653600505151106123            0600000000000000000000000000130                                ALTO                          Jalan Anggrek Neli Murni                          02199999          02000500050001         03214987654321                     052tester1             tester1                         ";
    JSONObject config = new JSONObject(configStr);
    String jsonMessage = "";

    public ISO8583Message(String dataName, String textISO8583Message) throws JsonProcessingException {

        textISO8583Message = textISO8583Message == null ? iso8583 : textISO8583Message;

        this.m_OrgMessage = textISO8583Message;
        this.m_RootNodeName = dataName;

        String[] lines = textISO8583Message.split(System.lineSeparator());
        this.m_DataMap = new LinkedHashMap<>();

        for ( String line : lines ) {
            if(jsonMessage.isEmpty())
            {
                jsonMessage = "[ ".concat( parseISO8583(line, config).toString() );
            }
            else
            {
                jsonMessage = jsonMessage.concat( ", " + parseISO8583(line, config).toString() );
            }
        }
        jsonMessage = jsonMessage.concat(" ]");

        jsonMessage = "{ \"" + this.m_RootNodeName + "\": " + jsonMessage + " }";

        ObjectMapper m_JsonMapper = new ObjectMapper();
        this.m_DataMap = m_JsonMapper.readValue(jsonMessage, new TypeReference<LinkedHashMap<String, Object>>() {});

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


    /**
     * Parse ISO 8583 message and convert it into JSONObject using configuration specs
     * @param message Raw ISO 8583 message
     * @param formatMap Object containing format and variable
     * @return Return JSON Object containing common specs that exists on the message
     */
    public JSONObject parseISO8583(String message, JSONObject formatMap)
    {
        JSONObject json = new JSONObject();
        if(!formatMap.equals(new JSONObject()))
        {
            String tmp1 = "", tmp2 = "", key = "", value = "";
            if(message.length() > 36)
            {
                String type = (message.length() > 4)?message.substring(0, 4):"0800";
                this.iso8583Structure = new ISO8583Structure(type, formatMap);
                iso8583Structure.parse(message);
                String[] arrFormat;
                try
                {
                    try
                    {

                        String[] fieldFromTemplate = ISO8583Structure.listStringToArrayString(ISO8583Structure.listFormatMapKey(formatMap));
                        int idx = 2;
                        String subfield = "";
                        String fieldName = "";
                        int maxField = 128;

                        String format, variable;
                        int[] lengths;
                        int j, begin = 0, end = 0;
                        String[] variables;
                        JSONObject fieldFormat;

                        for(idx = 2; idx <= maxField; idx++)
                        {
                            fieldName = "f"+idx;
                            if(iso8583Structure.hasField(idx))
                            {
                                subfield = iso8583Structure.getValue(idx).toString();
                                if(ISO8583Structure.inArray(fieldFromTemplate, fieldName))
                                {
                                    String jostr = formatMap.get(fieldName).toString();
                                    try
                                    {
                                        fieldFormat = new JSONObject(jostr);
                                        format = fieldFormat.get("format").toString();
                                        variable = fieldFormat.get("variable").toString();
                                        variable = variable.replaceAll(" ", "");
                                        variable = variable.replaceAll(",,,", ",");
                                        variable = variable.replaceAll(",,", ",");
                                        variables = variable.split(",");
                                        tmp1 = "";
                                        tmp2 = "";
                                        key = "";
                                        value = "";
                                        if(ISO8583Structure.hasSubfield(format))
                                        {
                                            lengths = ISO8583Structure.getSubfieldLength(format);
                                            arrFormat = null;
                                            if(format.contains("%"))
                                            {
                                                arrFormat = format.split("%");
                                            }
                                            begin = 0; end = 0;
                                            for(j = 0; j < lengths.length; j++)
                                            {
                                                end = begin + lengths[j];
                                                if(subfield.length() >= end && begin >= 0 && begin <= end)
                                                {
                                                    tmp1 = subfield.substring(begin, end);
                                                }
                                                else if(begin >= 0 && subfield.length() >= begin)
                                                {
                                                    tmp1 = subfield.substring(begin);
                                                }
                                                else
                                                {
                                                    tmp1 = "";
                                                }
                                                tmp2 = tmp1;

                                                if(arrFormat[j+1] != null)
                                                {
                                                    if(arrFormat[j+1].contains("s"))
                                                    {
                                                        tmp2 = tmp1;
                                                    }
                                                    if(arrFormat[j+1].contains("d"))
                                                    {
                                                        tmp2 = ISO8583Structure.lTrim(tmp1, "0");
                                                        if(tmp2.equals(""))
                                                        {
                                                            tmp2 = "0";
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    tmp2 = tmp1;
                                                }
                                                key = variables[j].trim();
                                                value = tmp2;
                                                json.put(key, value);
                                                begin = end;
                                            }
                                        }
                                        else
                                        {
                                            key = variables[0].trim();
                                            tmp1 = subfield;
                                            if(format.contains("s"))
                                            {
                                                value = tmp1;
                                            }
                                            if(format.contains("d"))
                                            {
                                                value = ISO8583Structure.lTrim(tmp1, "0");
                                                if(value.equals(""))
                                                {
                                                    value = "0";
                                                }
                                            }
                                            json.put(key, value);
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    value = "";
                                }
                            }
                        }
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
            }
        }
        else
        {
            LOG.error("Unable to parse message because format map is empty. Please check spec.");
        }
        return json;
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
