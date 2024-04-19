/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.payloadtypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.ISO8583FieldInfo;
import com.ybm.dataMapping.interfaces.PayloadMessageInterface;
import com.ybm.dataMapping.interfaces.ProcessingInterface;
import com.ybm.dataMapping.interfaces.VisitorInterface;
import com.ybm.dataMapping.models.ISO8583Structure;
import com.ybm.dataMappingRepo.FieldsDataTransformMappingService;
import com.ybm.dataMappingRepo.models.FieldsDataTransformMapping;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Set;

public class ISO8583Message implements PayloadMessageInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ISO8583Message.class);
    private final String m_OrgMessage;
    private LinkedHashMap<String, Object> m_DataMap = null;
    private String m_RootNodeName;
    private final String ROOT_NODE_NAME = "ROOT-NODE-NAME";

    String configStr = "{\"f41\":{\"format\":\"%-8s\", \"name\":\"card_acceptor_terminal\", \"options\":\"\", \"length\":\"8\", \"type\":\"STRING\"},\"f63\":{\"format\":\"%-32s%-30s%-50s%-18s\", \"name\":\"locket_code, locket_name, locket_address, locket_phone\", \"options\":\"\", \"length\":\"130\", \"type\":\"LLLVAR\"},\"f32\":{\"format\":\"%-11s\", \"name\":\"acq_institution_code\", \"options\":\"\", \"length\":\"11\", \"type\":\"LLVAR\"},\"f42\":{\"format\":\"%15s\", \"name\":\"acceptor_identification_code\", \"options\":\"\", \"length\":\"15\", \"type\":\"STRING\"},\"f121\":{\"format\":\"%-32s\", \"name\":\"payment_reference\", \"options\":\"\", \"length\":\"32\", \"type\":\"LLLVAR\"},\"f12\":{\"format\":\"%-6s\", \"name\":\"local_time\", \"options\":\"\", \"length\":\"6\", \"type\":\"STRING\"},\"f120\":{\"format\":\"%-20s\", \"name\":\"product_code\", \"options\":\"\", \"length\":\"20\", \"type\":\"LLLVAR\"},\"f11\":{\"format\":\"%06d\", \"name\":\"stan\", \"options\":\"\", \"length\":\"6\", \"type\":\"NUMERIC\"},\"f33\":{\"format\":\"%-11s\", \"name\":\"fwd_institution_code\", \"options\":\"\", \"length\":\"11\", \"type\":\"LLVAR\"},\"f13\":{\"format\":\"%-4s\", \"name\":\"local_date\", \"options\":\"\", \"length\":\"4\", \"type\":\"STRING\"},\"f49\":{\"format\":\"%03d\", \"name\":\"transaction_currency_code\", \"options\":\"\", \"length\":\"3\", \"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%-4s\", \"name\":\"settlement_date\", \"options\":\"\", \"length\":\"4\", \"type\":\"STRING\"},\"f37\":{\"format\":\"%012d\", \"name\":\"reference_number\", \"options\":\"\", \"length\":\"12\", \"type\":\"NUMERIC\"},\"f48\":{\"format\":\"%11s%12s%01d\", \"name\":\"meter_id, customer_id, id_selector\", \"options\":\"\", \"length\":\"24\", \"type\":\"LLLVAR\"},\"f2\":{\"format\":\"%-19s\", \"name\":\"pan\", \"options\":\"\", \"length\":\"19\", \"type\":\"LLVAR\"},\"f18\":{\"format\":\"%04d\", \"name\":\"merchant_type\", \"options\":\"\", \"length\":\"4\", \"type\":\"NUMERIC\"},\"f3\":{\"format\":\"%06d\", \"name\":\"processing_code\", \"options\":\"\", \"length\":\"6\", \"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\", \"name\":\"amount\", \"options\":\"\", \"length\":\"12\", \"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\", \"name\":\"transmission_date_time\", \"options\":\"\", \"length\":\"10\", \"type\":\"STRING\"},\"f127\":{\"format\":\"%-20s%-32s\", \"name\":\"username, password\", \"options\":\"\", \"length\":\"52\", \"type\":\"LLLVAR\"}}";
    String iso8583 = "0200F23A400188C180060000000000000182196048200000002731   300000000000020000092513425200007213425209250926602111597        1112345      000000000072DEVALT0120090010080000014514987654321149999999911030E8597E3B2F1646505FDD6E210000090MUP210ZBE957561167FCD8506326E4AHAMDANIE LESTALUHUANI    R1  00000090000000090000000011653600505151106123            0600000000000000000000000000130                                ALTO                          Jalan Anggrek Neli Murni                          02199999          02000500050001         03214987654321                     052tester1             tester1                         ";

    public ISO8583Message(String dataName, String textISO8583Message, String linkedEntity, String version, FieldsDataTransformMappingService fieldsDataTransformMappingService) throws JsonProcessingException {

        textISO8583Message = textISO8583Message == null ? iso8583 : textISO8583Message;

        this.m_OrgMessage = textISO8583Message;
        this.m_RootNodeName = dataName;
        this.m_DataMap = new LinkedHashMap<>();
        ObjectMapper jsonMapper = new ObjectMapper();
        LinkedHashMap<String, LinkedHashMap<String, String>> formatMapStd = jsonMapper.readValue(configStr, new TypeReference<>() {});

        String jsonMessage = "";
        String[] lines = textISO8583Message.split(System.lineSeparator());
        for ( String line : lines ) {
            // MTI 4 characters + Primary BIT MAP 16 Hexadecimal Characters = 20
            if ( line.length() > 20 ) {
                LinkedHashMap<String, LinkedHashMap<String, String>> formatMap = formatMapStd;
                String mti = line.substring(0, 4);
                String mtiKeyTag = "MTI".concat(mti);
                String transformMapperName = linkedEntity.concat("~ISO8583.".concat(mtiKeyTag).concat(".".concat(version)));

                FieldsDataTransformMapping fieldsDataTransformMapping =
                        fieldsDataTransformMappingService.getFieldsDataTransformMappingById(transformMapperName);
                if( fieldsDataTransformMapping != null ) {
                    formatMap = jsonMapper.readValue(fieldsDataTransformMapping.getMappingExpressionScript(), new TypeReference<>() {});
                }

                if (jsonMessage.isEmpty()) {
                    jsonMessage = "[ ".concat(parseISO8583(line, formatMap).toString());
                } else {
                    jsonMessage = jsonMessage.concat(", " + parseISO8583(line, formatMap).toString());
                }
            }
        }
        jsonMessage = jsonMessage.concat(" ]");
        jsonMessage = "{ \"" + this.m_RootNodeName + "\": " + jsonMessage + " }";

        this.m_DataMap = jsonMapper.readValue(jsonMessage, new TypeReference<LinkedHashMap<String, Object>>() {});

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
    private JSONObject parseISO8583(String message, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap)
    {
        JSONObject json = new JSONObject();
        if(!formatMap.equals(new JSONObject()))
        {
            if(message.length() > 20)
            {
                String mtiType = message.substring(0, 4);
                String mtiKeyTag = "MTI".concat(mtiType);
                /**
                 * ISO8583Structure to get all ISO 8583 fields
                 */
                ISO8583Structure iso8583Structure = new ISO8583Structure(mtiType, formatMap);
                iso8583Structure.parse(message);
                String[] arrFormat;
                try
                {
                    try
                    {
                        Set<String> fieldsFromTemplate = iso8583Structure.formatMapKeySet();

                        for(int idx = 2; idx <= iso8583Structure.getMaxField(); idx++)
                        {
                            String key = "", value = "";
                            String fieldName = "f"+idx;
                            if(iso8583Structure.hasField(idx))
                            {
                                String subfield = iso8583Structure.getValue(idx);
                                if(iso8583Structure.isInArray(fieldsFromTemplate, fieldName))
                                {
                                    LinkedHashMap<String, String> fieldFormat = formatMap.get(fieldName);
                                    try
                                    {
                                        String format = fieldFormat.get(ISO8583FieldInfo.DataElementConfig.FORMAT.getText());
                                        String variable = fieldFormat.get(ISO8583FieldInfo.DataElementConfig.NAME.getText());
                                        variable = variable.replaceAll(" ", "");
                                        variable = variable.replaceAll(",,,", ",");
                                        variable = variable.replaceAll(",,", ",");
                                        String[] variables = variable.split(",");

                                        String tmp1 = "", tmp2 = "";
                                        if(iso8583Structure.hasSubfield(format))
                                        {
                                            int[] lengths = iso8583Structure.getSubfieldLength(format);
                                            arrFormat = null;
                                            if(format.contains("%"))
                                            {
                                                arrFormat = format.split("%");
                                            }
                                            int begin = 0, end = 0;
                                            for(int j = 0; j < lengths.length; j++)
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
                                                    if(arrFormat[j+1].contains("d") || arrFormat[j+1].contains("f"))
                                                    {
                                                        tmp2 = iso8583Structure.lTrim(tmp1, "0");
                                                        if(tmp2.isEmpty())
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
                                            if(format.contains("d") || format.contains("f"))
                                            {
                                                value = iso8583Structure.lTrim(tmp1, "0");
                                                if(value.isEmpty())
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
                                        LOG.error(e.getMessage());
                                    }
                                }
                                else
                                {
                                    value = "";
                                }
                            }
                        }
                        JSONObject jsonRoot = new JSONObject();
                        json = jsonRoot.put(mtiKeyTag, json);
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
