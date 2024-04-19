/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.models;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.dataMapping.interfaces.ISO8583FieldInfo;
import com.ybm.dataMapping.interfaces.ISO8583MTI;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISO8583Structure {

    private static final Logger LOG = LoggerFactory.getLogger(ISO8583Structure.class);


    /**
     * Message Type Indicator (default : Version 1987, Financial messages, Request, Acquirer )
     * -- GETTER --
     *  Get Message Type Indicator
     *
     *
     * -- SETTER --
     *  Set Message Type Indicator
     *
     @return Message Type Indicator
      * @param type Message Type Indicator

     */
    @Setter
    @Getter
    //private String mtiType = "0200";
    private byte[] mtiType = new byte[ISO8583MTI.MTI_LEN];

    /**
     * Primary bitmap. Java long takes 8 bytes hence 8 bytes * 8 bits = 64 bits bitmap.
     */
    private long primaryBitMap = 0;
    /**
     * Secondary bitmap. Java long takes 8 bytes hence 8 bytes * 8 bits = 64 bits bitmap.
     */
    private long secondaryBitMap = 0;
    /**
     * Tertiary bitmap. Java long takes 8 bytes hence 8 bytes * 8 bits = 64 bits bitmap.
     */
    private long tertiaryBitMap = 0;

    /**
     * ISO8583 configuration
     * -- GETTER --
     *  Get configuration of the specs
     *
     *
     * -- SETTER --
     *  Set configuration of the specs
     *
     @return JSONObject contians configuration of the specs
      * @param config JSONObject contians configuration of the specs

     */
    @Setter
    @Getter
    private LinkedHashMap<String, LinkedHashMap<String, String>> formatMap = new LinkedHashMap<>();

    /**
     * General ISO8583 configuration. When server receive ISO8583 string message and contains any field that is not configured in specs,
     * it will be parsed using general config of ISO8583.
     */
    private static LinkedHashMap<String, LinkedHashMap<String, String>> generalFormatMapConfig = null;

    /**
     * JSONObject of ISO8583 field
     */
    private JSONObject jsonItem = new JSONObject();

    /**
     * Array of ISO8583 field name
     */
    public String[] fields = null;

    /**
     * ISO8583 field name
     */
    private String fieldStr = "";

    /**
     * The greatest field number
     * -- GETTER --
     *  Get maximum field
     *
     * @return Maximum field

     */
    @Getter
    public int maxField = 1;

    private static char hexNdx[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static Pattern patHex = Pattern.compile("[0-9A-Z]*");

    /**
     * Default constructor
     */
    public ISO8583Structure()
    {
        mtiType[ISO8583MTI.MTI_VER_NO] = ISO8583MTI.VER_1987;
        mtiType[ISO8583MTI.MTI_MSG_CLASS] = ISO8583MTI.MSG_CLASS_NETWORK_MANAGEMENT;
        mtiType[ISO8583MTI.MTI_MSG_FUNC] = ISO8583MTI.MSG_FUNC_NOTIFICATION;
        mtiType[ISO8583MTI.MTI_TXN_ORIG] = ISO8583MTI.TXN_ORIG_OTHER;

        if(ISO8583Structure.generalFormatMapConfig == null)
        {
            ISO8583Structure.parseGeneralConfig();
        }
    }
    /**
     * Constructor with Message Type Indicator
     * @param mti_id Message Type Indicator
     */
    public ISO8583Structure(String mti_id)
    {
        this.setMtiType(mti_id.getBytes());
        if(ISO8583Structure.generalFormatMapConfig == null)
        {
            ISO8583Structure.parseGeneralConfig();
        }
    }
    /**
     * Constructor with Message Type Indicator and configuration of the specs
     * @param mti_id Message Type Indicator
     * @param formatMap Configuration of the specs
     */
    public ISO8583Structure(String mti_id, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap)
    {
        this.setMtiType(mti_id.getBytes());
        this.setFormatMap(formatMap);
    }

    /**
     * Constructor with Message Type Indicator, configuration of the specs and message to be parse
     * @param mti_id Message Type Indicator
     * @param formatMap Configuration of the specs
     * @param message Message to be parse
     */
    public ISO8583Structure(String mti_id, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap, String message)
    {
        this.setMtiType(mti_id.getBytes());
        this.setFormatMap(formatMap);
        this.parse(message);
    }

    public int getVersionNumer() {
        return mtiType[ISO8583MTI.MTI_VER_NO];
    }

    public int getMessageClass() {
        return mtiType[ISO8583MTI.MTI_MSG_CLASS];
    }

    public int getMessageFunction() {
        return mtiType[ISO8583MTI.MTI_MSG_FUNC];
    }

    public int getTransactionOriginator() {
        return mtiType[ISO8583MTI.MTI_TXN_ORIG];
    }

    public void setVersionNumer(int i) {
        if (i < 0 || i > 9) {
            LOG.error(" Expecting values between 0 & 9");
            throw new IllegalArgumentException(" Expecting values between 0 & 9");
        }
        mtiType[ISO8583MTI.MTI_VER_NO] = (byte) i;
    }

    public void setMessageClass(int i) {
        if (i < 0 || i > 9) {
            LOG.error(" Expecting values between 0 & 9");
            throw new IllegalArgumentException(" Expecting values between 0 & 9");
        }
        mtiType[ISO8583MTI.MTI_MSG_CLASS] = (byte) i;
    }

    public void setMessageFunction(int i) {
        if (i < 0 || i > 9) {
            LOG.error(" Expecting values between 0 & 9");
            throw new IllegalArgumentException(" Expecting values between 0 & 9");
        }
        mtiType[ISO8583MTI.MTI_MSG_FUNC] = (byte) i;
    }

    public void setTransactionOriginator(int i) {
        if (i < 0 || i > 9) {
            LOG.error(" Expecting values between 0 & 9");
            throw new IllegalArgumentException(" Expecting values between 0 & 9");
        }
        mtiType[ISO8583MTI.MTI_TXN_ORIG] = (byte) i;
    }

    /**
     * Set field
     * @param field ISO8583 field number
     * @param iso8583Field ISO8583 field data
     */
    public void setField(int field, ISO8583Field iso8583Field)
    {
        this.addBit(field);
        this.setValue(field, iso8583Field.data, iso8583Field.type, iso8583Field.length);
    }
    /**
     * Get ISO8583 field
     * @param field ISO8583 field number
     * @return ISO8583StructureField contains field object
     */
    public ISO8583Field getField(int field)
    {
        ISO8583Field fieldData = new ISO8583Field();
        JSONObject data = new JSONObject();
        try
        {
            data = (JSONObject) this.jsonItem.get("F"+field);
            fieldData.setField( data.optString(ISO8583FieldInfo.DataElementConfig.DATA.getText(), ""),
                    ISO8583FieldInfo.Format.valueOf(data.optString(ISO8583FieldInfo.DataElementConfig.TYPE.getText(), ISO8583FieldInfo.Format.STRING.name())),
                    Integer.parseInt(data.optString(ISO8583FieldInfo.DataElementConfig.LENGTH.getText(), "1")) );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return fieldData;
    }

    /**
     * Check if the field is present or not
     * @param field ISO8583 field number
     * @return true if field is present and false if field is not present
     */
    public boolean hasField(int field)
    {
        int i;
        for(i = 0; i<this.fields.length; i++)
        {
            if(this.fields[i].equals(field+""))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * Parse ISO 8583 Message
     * @param message String contains ISO 8583 message
     */
    public void parse(String message)
    {
        this.fields = null;

        String ln = "";

        String primaryBitMapStr = "";
        long primaryBitMapInt = 0;

        String secondaryBitMapStr = "";
        long secondaryBitMapInt = 0;

        String tertiaryBitMapStr = "";
        long tertiaryBitMapInt = 0;

        this.mtiType = message.substring(0, 4).getBytes();
        primaryBitMapStr = message.substring(4, 20).replaceAll("[^A-Fa-f0-9]", "");
        if(primaryBitMapStr.isEmpty())
        {
            primaryBitMapStr = "0";
        }

        primaryBitMapInt = Long.parseUnsignedLong(primaryBitMapStr, 16);
        this.primaryBitMap = primaryBitMapInt;

        if((this.primaryBitMap & 0x8000000000000000L) == 0x8000000000000000L)
        {
            secondaryBitMapStr = message.substring(20, 36).replaceAll("[^A-Fa-f0-9]", "");
            if(secondaryBitMapStr.isEmpty())
            {
                secondaryBitMapStr = "0";
            }

            secondaryBitMapInt = Long.parseUnsignedLong(secondaryBitMapStr, 16);
            this.secondaryBitMap = secondaryBitMapInt;
        }
        if((this.secondaryBitMap & 0x8000000000000000L) == 0x8000000000000000L)
        {
            tertiaryBitMapStr = message.substring(36, 44).replaceAll("[^A-Fa-f0-9]", "");
            if(tertiaryBitMapStr.equals(""))
            {
                tertiaryBitMapStr = "0";
            }

            tertiaryBitMapInt = Long.parseUnsignedLong(tertiaryBitMapStr, 16);
            this.tertiaryBitMap = tertiaryBitMapInt;
        }

        // get field list from bitmap
        int i = 0;
        long k = 0;

        k = this.primaryBitMap;
        for(i = 1; i <= 64; i++)
        {
            if((k & 0x8000000000000000L) == 0x8000000000000000L && i > 1)
            {
                this.addBit(i);
            }
            k = k - 0x8000000000000000L;
            k = k << 1;
        }

        int bitmapLength = 16;
        if((this.primaryBitMap & 0x8000000000000000L) == 0x8000000000000000L)
        {
            bitmapLength = 32;
            k = this.secondaryBitMap;
            for(i = 65; i <= 128; i++)
            {
                if((k & 0x8000000000000000L) == 0x8000000000000000L)
                {
                    this.addBit(i);
                }
                k = k - 0x8000000000000000L;
                k = k << 1;
            }

            if((this.secondaryBitMap & 0x8000000000000000L) == 0x8000000000000000L)
            {
                bitmapLength = 48;
                k = this.tertiaryBitMap;
                for(i = 129; i <= 192; i++)
                {
                    if((k & 0x8000000000000000L) == 0x8000000000000000L)
                    {
                        this.addBit(i);
                    }
                    k = k - 0x8000000000000000L;
                    k = k << 1;
                }
            }
        }

        ISO8583FieldInfo.Format dataType = ISO8583FieldInfo.Format.UNKNOWN;
        int dataLength = 0;
        int realLength = 0;
        int fieldLength = 0;
        String rawData = "";
        String shiftedData = "";
        int field = 0;

        boolean validMessage = true;
        for (String strField : this.fields) {
            field = Integer.parseInt(strField);
            // get config
            LinkedHashMap<String, String> fieldFormat = this.formatMap.get("f" + field);
            if (fieldFormat == null) {

                if (ISO8583Structure.generalFormatMapConfig != null) {
                    fieldFormat = ISO8583Structure.generalFormatMapConfig.get("f" + field);
                    if (fieldFormat == null) {
                        validMessage = false;
                    }
                } else {
                    validMessage = false;
                }
            }
        }
        if(validMessage)
        {
            if(message.length() > ( bitmapLength + ISO8583MTI.MTI_LEN ) )
            {
                shiftedData = message.substring( bitmapLength + ISO8583MTI.MTI_LEN );
                for (String strField : this.fields) {
                    field = Integer.parseInt(strField);
                    LinkedHashMap<String, String> fieldFormat = this.formatMap.get("f" + field);
                    if (fieldFormat == null) {
                        if (ISO8583Structure.generalFormatMapConfig != null) {
                            fieldFormat = ISO8583Structure.generalFormatMapConfig.get("f" + field);
                        }
                    }
                    if (fieldFormat != null) {
                        if (fieldFormat.get(ISO8583FieldInfo.DataElementConfig.TYPE.getText()) != null) {
                            dataType = ISO8583FieldInfo.Format.valueOf(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.TYPE.getText()));
                            fieldLength = Integer.parseInt(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.LENGTH.getText()));
                            dataLength = fieldLength;
                            realLength = dataLength;
                            rawData = "";
                            if (dataType == ISO8583FieldInfo.Format.LLLVAR) {
                                if (shiftedData.length() >= 3) {
                                    rawData = shiftedData.substring(0, 3);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if (ln.isEmpty()) {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(3);
                                    if (shiftedData.length() >= realLength) {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    } else {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.LLVAR) {
                                if (shiftedData.length() >= 2) {
                                    rawData = shiftedData.substring(0, 2);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if (ln.isEmpty()) {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(2);
                                    if (shiftedData.length() >= realLength) {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    } else {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.LVAR) {
                                if (!shiftedData.isEmpty()) {
                                    rawData = shiftedData.substring(0, 1);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if (ln.isEmpty()) {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(1);
                                    if (shiftedData.length() >= realLength) {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    } else {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.AMOUNT) {
                                if (shiftedData.length() >= 12) {
                                    rawData = shiftedData.substring(0, 12);
                                    rawData = rawData.replaceAll("[^\\d]", "");
                                    rawData = lTrim(rawData, "0");
                                    if (rawData.isEmpty()) {
                                        rawData = "0";
                                    }
                                    long numVal = Long.parseLong(rawData);
                                    rawData = String.format("%012d", numVal);
                                    shiftedData = shiftedData.substring(12);
                                    realLength = 12;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.YYMM) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.YYMMDD) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.DDMMYY) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.MMDDhhmmss) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.YYMMDDhhmmss) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.STRING) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else if (dataType == ISO8583FieldInfo.Format.FIXED) {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            } else {
                                if (shiftedData.length() >= dataLength) {
                                    rawData = shiftedData.substring(0, dataLength);
                                    shiftedData = shiftedData.substring(dataLength);
                                    realLength = dataLength;
                                }
                            }
                            this.setValue(field, rawData, dataType, realLength);
                        }
                    }
                }
            }
        }
    }
    public String showAsList()
    {
        int i;
        StringBuilder ret = new StringBuilder();
        for(i = 2; i<=this.maxField; i++)
        {
            if(this.hasField(i))
            {
                ret.append(String.format("%3d %s\r\n", i, this.getValue(i)));
            }
        }
        return ret.toString();
    }

    /**
     * Apply function to data
     * @param value Value of data
     * @param option Function
     * @return String result of the function
     */
    private static String applyFunction(String value, String option)
    {
        /**
         * substring(start)
         * substring(start, end)
         * left(length)
         * right(length)
         * lower()
         * upper()
         * reverse()
         * times(times)
         * division(div)
         * add(add)
         * subtract(sub)
         * inverse(n)
         * before(string)
         * after(string)
         */
        option = option.trim();
        String opt = "";
        String val = value;

        int iargs0, iargs1;
        double dargs0, dval;

        if(option.contains("substring("))
        {
            // TODO: substring
            opt = option.substring("substring".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                if(opt.contains(","))
                {
                    String[] args = opt.split(",");
                    args[0] = args[0].trim();
                    args[1] = args[1].trim();
                    if(args[0].equals(""))
                    {
                        args[0] = "0";
                    }
                    if(args[1].equals(""))
                    {
                        args[1] = "0";
                    }
                    iargs0 = Integer.parseInt(args[0]);
                    iargs1 = Integer.parseInt(args[1]);
                    if(iargs0 < 0) iargs0 = 0;
                    if(iargs1 < 0) iargs1 = 0;
                    if(iargs1 > iargs0)
                    {
                        iargs1 = iargs0;
                    }
                    if(val.length() == 0)
                    {

                    }
                    else if(value.length() < iargs0)
                    {
                        val = "";
                    }
                    else if(value.length() < iargs1)
                    {
                        val = val.substring(iargs0);
                    }
                    else if(value.length() >= iargs1)
                    {
                        val = val.substring(iargs0, iargs1);
                    }
                    else
                    {
                        // do nothing
                    }
                }
                else
                {
                    iargs0 = Integer.parseInt(opt);
                    if(iargs0 < 0) iargs0 = 0;
                    if(value.length() < iargs0)
                    {
                        val = "";
                    }
                    else
                    {
                        val = val.substring(iargs0);
                    }
                }
            }
        }
        else if(option.contains("left("))
        {
            // TODO: left
            opt = option.substring("left".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                iargs0 = Integer.parseInt(opt);
                if(iargs0 < 0) iargs0 = 0;
                if(value.length() < iargs0)
                {

                }
                else
                {
                    val = val.substring(0, iargs0);
                }
            }
        }
        else if(option.contains("right("))
        {
            // TODO: right
            opt = option.substring("right".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                iargs0 = Integer.parseInt(opt);

                if(value.length() < iargs0)
                {
                }
                else
                {
                    int length = value.length();
                    int start = length - iargs0;
                    int end = length;
                    if(start < 0)
                    {
                        start = 0;
                    }
                    if(end < 0)
                    {
                        end = 0;
                    }
                    if(end < start)
                    {
                        end = start;
                    }
                    val = val.substring(start, end);
                }
            }
        }
        else if(option.contains("upper("))
        {
            val = val.toUpperCase();
        }
        else if(option.contains("lower("))
        {
            val = val.toLowerCase();
        }
        else if(option.contains("reverse("))
        {
            // TODO: reverse
            try
            {
                val = new StringBuilder(val).reverse().toString();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
        else if(option.contains("times("))
        {
            // TODO: times
            opt = option.substring("times".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                opt = opt.replaceAll("[^\\d.\\-]", "");
                if(opt.isEmpty())
                {
                    opt = "0";
                }
                dargs0 = Double.parseDouble(opt);
                val = val.replaceAll("[^\\d.\\-]", "");
                if(val.isEmpty())
                {
                    val = "0";
                }
                dval = Double.parseDouble(val);
                dval = dval * dargs0;
                val = dval + "";
            }
        }
        else if(option.contains("division("))
        {
            // TODO: division
            opt = option.substring("division".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                opt = opt.replaceAll("[^\\d.\\-]", "");
                if(opt.isEmpty())
                {
                    opt = "0";
                }
                double args0 = Double.parseDouble(opt);
                val = val.replaceAll("[^\\d.\\-]", "");
                if(val.isEmpty())
                {
                    val = "0";
                }
                dval = Double.parseDouble(val);
                dval = dval / args0;
                val = dval + "";
            }
        }
        else if(option.contains("add("))
        {
            // TODO: add
            opt = option.substring("add".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                opt = opt.replaceAll("[^\\d.\\-]", "");
                if(opt.isEmpty())
                {
                    opt = "0";
                }
                double args0 = Double.parseDouble(opt);
                val = val.replaceAll("[^\\d.\\-]", "");
                if(val.isEmpty())
                {
                    val = "0";
                }
                dval = Double.parseDouble(val);
                dval = dval + args0;
                val = dval + "";
            }
        }
        else if(option.contains("subtract("))
        {
            // TODO: subtract
            opt = option.substring("subtract".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();

            if(!opt.isEmpty())
            {
                opt = opt.replaceAll("[^\\d.\\-]", "");
                if(opt.isEmpty())
                {
                    opt = "0";
                }
                double args0 = Double.parseDouble(opt);
                val = val.replaceAll("[^\\d.\\-]", "");
                if(val.isEmpty())
                {
                    val = "0";
                }
                dval = Double.parseDouble(val);
                dval = dval - args0;
                val = dval + "";
            }
        }
        else if(option.contains("inverse("))
        {
            // TODO: inverse
            opt = option.substring("inverse".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();
            opt = opt.replaceAll("[^\\d.\\-]", "");
            if(opt.isEmpty())
            {
                opt = "1";
            }
            double args0 = Double.parseDouble(opt);

            val = val.replaceAll("[^\\d.\\-]", "");
            if(val.isEmpty())
            {
                val = "0";
            }
            dval = Double.parseDouble(val);
            if(dval == 0)
            {
                val = dval + "";
            }
            else
            {
                dval = args0 / dval;
                val = dval + "";
            }

        }
        else if(option.contains("before("))
        {
            // TODO: before
            opt = option.substring("before".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();
            opt = urlDecode(opt);

            if(!opt.isEmpty())
            {
                val = opt + val;
            }
        }
        else if(option.contains("after("))
        {
            // TODO: after
            opt = option.substring("after".length());
            opt = lTrim(opt, "\\(");
            opt = rTrim(opt, "\\)");
            opt = opt.trim();
            opt = urlDecode(opt);
            if(!opt.isEmpty())
            {
                val = val + opt;
            }
        }
        return val;
    }

    /**
     * Apply options to transaction data. It will modify data
     * @param json JSONObject contains transaction data
     * @param options String containing the function
     * @return JSONObject after the operation
     */
    public static JSONObject applyOption(JSONObject json, String options)
    {
        if(!options.isEmpty())
        {
            options = options.replaceAll("substring \\(", "substring\\(");
            options = options.replaceAll("left \\(", "left\\(");
            options = options.replaceAll("right \\(", "right\\(");
            options = options.replaceAll("lower \\(", "lower\\(");
            options = options.replaceAll("upper \\(", "upper\\(");
            options = options.replaceAll("reverse \\(", "reverse\\(");
            options = options.replaceAll("times \\(", "times\\(");
            options = options.replaceAll("division \\(", "division\\(");
            options = options.replaceAll("add \\(", "add\\(");
            options = options.replaceAll("subtract \\(", "subtract\\(");
            options = options.replaceAll("inverse \\(", "inverse\\(");
            options = options.replaceAll("before \\(", "before\\(");
            options = options.replaceAll("after \\(", "after\\(");

            options = options.replaceAll("substring \\(", "substring\\(");
            options = options.replaceAll("left \\(", "left\\(");
            options = options.replaceAll("right \\(", "right\\(");
            options = options.replaceAll("lower \\(", "lower\\(");
            options = options.replaceAll("upper \\(", "upper\\(");
            options = options.replaceAll("reverse \\(", "reverse\\(");
            options = options.replaceAll("times \\(", "times\\(");
            options = options.replaceAll("division \\(", "division\\(");
            options = options.replaceAll("add \\(", "add\\(");
            options = options.replaceAll("subtract \\(", "subtract\\(");
            options = options.replaceAll("inverse \\(", "inverse\\(");
            options = options.replaceAll("before \\(", "before\\(");
            options = options.replaceAll("after \\(", "after\\(");

            /**
             * TODO : Function list provided bellow
             * substring(start)
             * substring(start, end)
             * left(length)
             * right(length)
             * lower()
             * upper()
             * reverse()
             * times(times)
             * division(div)
             * add(add)
             * subtract(sub)
             * inverse(n)
             * before(string)
             * after(string)
             */
            if(options.isEmpty())
            {
            }
            else
            {
                String[] options_array;
                String key = "";
                String opt = "";
                String option = "";
                String value = "";
                String[] args;
                if(options.contains("&"))
                {
                    options_array = options.split("&");
                }
                else
                {
                    options_array = new String[1];
                    options_array[0] = options;
                }
                int count, i, j;
                count = options_array.length;

                for(i = 0; i<count; i++)
                {
                    opt = options_array[i];
                    if(opt.contains("="))
                    {
                        args = opt.split("=", 2);
                        key = args[0].trim();
                        option = args[1];
                        try
                        {
                            if(json.has(key))
                            {
                                value = json.optString(key, "");
                                if(!options.isEmpty())
                                {
                                    value = applyFunction(value, option);
                                }
                                json.put(key, value);
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            LOG.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return json;
    }

    /**
     * Decode URL
     * @param input Encoded string
     * @return Decoded string
     */
    private static String urlDecode(String input)
    {
        String result = "";
        result = java.net.URLDecoder.decode(input, StandardCharsets.UTF_8);
        return result;
    }
    /**
     * Encode URL
     * @param input String to be encoded
     * @return Encoded string
     */
    private String urlEncode(String input)
    {
        String result = "";
        result = java.net.URLEncoder.encode(input, StandardCharsets.UTF_8);
        return result;
    }

    /**
     * List formatMap into string array
     * @param format Data format
     * @return String array containing single format
     */
    public static String[] listSubformat(String format)
    {
        Pattern p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(format);
        String fmt = "";
        int i = 0;
        while(m.find())
        {
            i++;
        }
        String[] subformats = new String[i];
        p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        m = p.matcher(format);
        i = 0;
        while(m.find())
        {
            fmt = m.group(0);
            fmt = fmt.trim();
            if(fmt.isEmpty())
            {
                fmt = "0";
            }
            subformats[i] = fmt;
            i++;
        }
        return subformats;
    }

    /**
     * Parse ISO 8583 Message
     * @param message String contains ISO 8583 message
     * @param formatMap LinkedHashMap contains specs
     */
    public void parse(String message, LinkedHashMap<String, LinkedHashMap<String, String>> formatMap)
    {
        this.setFormatMap(formatMap);
        this.parse(message);
    }
    /**
     * Generate ISO 8583 message contains Message Type Indicator, bitmap, and all fields
     */
    public String toString()
    {
        String message = "";
        int i;
        int[] fieldsInt;
        if(this.fields.length > 0)
        {
            fieldsInt = new int[this.fields.length];
            for(i = 0; i < this.fields.length; i++)
            {
                fieldsInt[i] = Integer.parseInt(this.fields[i]);
            }
            Arrays.sort(fieldsInt);
            for(i = 0; i < this.fields.length; i++)
            {
                this.fields[i] = fieldsInt[i]+"";
            }
            this.fieldStr = String.join(",", this.fields);
            message = new String(this.mtiType) + this.getBitmap() + this.getBody();
        }
        return message;
    }
    /**
     * Padding string on right with specified character
     * @param s Padding string
     * @param n Length expected
     * @return Padded string
     */
    private String padRight(String s, int n)
    {
        return String.format("%1$-" + n + "s", s);
    }
    /**
     * Padding string on left with specified character
     * @param s Padding string
     * @param n Length expected
     * @return Padded string
     */
    private String padLeft(String s, int n)
    {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * Strip characters from the beginning of a string
     * @param input String to be stripped
     * @param mask Character mask to strip string
     * @return Stripped string
     */
    public static String lTrim(String input, String mask)
    {
        int lastLen = input.length();
        int curLen = lastLen;
        do
        {
            lastLen = input.length();
            input = input.replaceAll("^"+mask, "");
            curLen = input.length();
        }
        while (curLen < lastLen);
        return input;
    }
    /**
     * Strip characters from the end of a string
     * @param input String to be stripped
     * @param mask Character mask to strip string
     * @return Stripped string
     */
    private static String rTrim(String input, String mask)
    {
        int lastLen = input.length();
        int curLen = lastLen;
        do
        {
            lastLen = input.length();
            input = input.replaceAll(mask+"$", "");
            curLen = input.length();
        }
        while (curLen < lastLen);
        return input;
    }

    /**
     * Get N right string
     * @param input Input string
     * @param length Expected length
     * @return N right string
     */
    public static String right(String input, int length)
    {
        if(length >= input.length())
        {
            return input;
        }
        else
        {
            return input.substring(input.length() - length, input.length()-1);
        }
    }
    /**
     * Get N left string
     * @param input Input string
     * @param length Expected length
     * @return N left string
     */
    public static String left(String input, int length)
    {
        if(length >= input.length())
        {
            return input;
        }
        else
        {
            return input.substring(0, length);
        }
    }

    /**
     * Get ISO 8583 bitmap.
     * @return String contains ISO 8583 bitmap
     */
    public String getBitmap()
    {
        String bitmap = "";
        int maxField = 0;
        String fieldStr = "";
        int fieldInt = 0;
        int i = 0;
        for(i = 0; i<fields.length; i++)
        {
            fieldStr = this.fields[i];
            if(fieldStr.isEmpty())
            {
                fieldStr = "0";
            }
            fieldInt = Integer.parseInt(fieldStr);
            if(fieldInt > maxField)
            {
                maxField = fieldInt;
            }
            if(fieldInt >= 1 && fieldInt <= ISO8583MTI.BIT_MAP_BIN_LEN)
            {
                primaryBitMap = (primaryBitMap | (1L << (ISO8583MTI.BIT_MAP_BIN_LEN - fieldInt)));
            }

            if(fieldInt >= 65 && fieldInt <= (ISO8583MTI.BIT_MAP_BIN_LEN + ISO8583MTI.BIT_MAP_BIN_LEN ))
            {
                secondaryBitMap = (secondaryBitMap | (1L << (ISO8583MTI.BIT_MAP_BIN_LEN - fieldInt)));
            }

            if(fieldInt >= 129 && fieldInt <= (ISO8583MTI.BIT_MAP_BIN_LEN + ISO8583MTI.BIT_MAP_BIN_LEN + ISO8583MTI.BIT_MAP_BIN_LEN))
            {
                tertiaryBitMap = (tertiaryBitMap | (1L << (ISO8583MTI.BIT_MAP_BIN_LEN - fieldInt)));
            }
        }
        String h1 = "", h2 = "", h3 = "";
        if(maxField > 128)
        {
            primaryBitMap = (primaryBitMap | (1L << 63));
            secondaryBitMap = (secondaryBitMap | (1L << 63));
            h1 = String.format("%016x", primaryBitMap);
            h2 = String.format("%016x", secondaryBitMap);
            h3 = String.format("%016x", tertiaryBitMap);
            bitmap = h1+h2+h3;
        }
        else if(maxField > 64)
        {
            primaryBitMap = (primaryBitMap | (1L << 63));
            h1 = String.format("%016x", primaryBitMap);
            h2 = String.format("%016x", secondaryBitMap);
            bitmap = h1+h2;
        }
        else
        {
            h1 = String.format("%016x", primaryBitMap);
            bitmap = h1;
        }
        return bitmap.toUpperCase();
    }
    /**
     * Get message body.
     * This method will pack all field to a string. The length of each field is set when the field is added/modified
     * @return String contains ISO 8583 message body
     */
    public String getBody()
    {
        String fieldStr = "";
        JSONObject jo = new JSONObject();
        StringBuilder body = new StringBuilder();
        String data = "";
        String finalItemData = "";
        ISO8583FieldInfo.Format dataType = ISO8583FieldInfo.Format.UNKNOWN;
        int dataLength = 0;
        int iter = 0;

        long val = 0;
        String len = "";

        for(iter = 0; iter < this.fields.length; iter++)
        {
            fieldStr = this.fields[iter];
            jo = (JSONObject) this.jsonItem.get("F"+fieldStr.trim());
            try
            {
                data = jo.get(ISO8583FieldInfo.DataElementConfig.DATA.getText()).toString();
                finalItemData = data;
                dataType = ISO8583FieldInfo.Format.valueOf(jo.get(ISO8583FieldInfo.DataElementConfig.TYPE.getText()).toString());
                dataLength = Integer.parseInt(jo.get(ISO8583FieldInfo.DataElementConfig.LENGTH.getText()).toString());
                if(dataType == ISO8583FieldInfo.Format.AMOUNT)
                {
                    dataLength = 12;
                    data = data.trim();
                    if(data.isEmpty())
                    {
                        data = "0";
                    }
                    val = Long.parseLong(data);
                    finalItemData = String.format("%012d", val);
                }
                else if(dataType == ISO8583FieldInfo.Format.LLLVAR)
                {
                    len = String.format("%03d", data.length());
                    finalItemData = len + data;
                }
                else if(dataType == ISO8583FieldInfo.Format.LLVAR)
                {
                    len = String.format("%02d", data.length());
                    finalItemData = len + data;
                }
                else if(dataType == ISO8583FieldInfo.Format.LVAR)
                {
                    len = String.format("%1d", data.length());
                    finalItemData = len + data;
                }
                else if(dataType == ISO8583FieldInfo.Format.NUMERIC)
                {
                    data = data.trim();
                    if(data.isEmpty())
                    {
                        data = "0";
                    }
                    val = Long.parseLong(data);
                    finalItemData = String.format("%0"+dataLength+"d", val);
                }
                else
                {
                    finalItemData = String.format("%-"+dataLength+"s", data);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
            body.append(finalItemData);
        }
        return body.toString();
    }
    /**
     * Add bit to ISO 8583 message. It will affect to the bitmap.
     * @param field ISO 8583 field number
     */
    public void addBit(int field)
    {
        if(field > this.maxField)
        {
            this.maxField = field;
        }
        if(this.fields != null)
        {
            this.fieldStr = String.join(",", this.fields);
            int i;
            boolean duplicated = false;
            for(i = 0; i < this.fields.length; i++)
            {
                if(this.fields[i].trim().equals(field+""))
                {
                    duplicated = true;
                }
            }
            if(!duplicated)
            {
                this.fieldStr += ","+field;
            }
        }
        else
        {
            this.fieldStr = field+"";
        }
        this.fields = this.fieldStr.split(",");
    }
    /**
     * Add field value. It seem with the setValue method unless invoke addBit first before set the field value
     * @param field ISO 8583 field number
     * @param data String data
     * @param dataType field Type
     * @param dataLength Data length
     */
    public void addValue(int field, String data, ISO8583FieldInfo.Format dataType, int dataLength)
    {
        this.addBit(field);
        this.setValue(field, data, dataType, dataLength);
    }
    /**
     * Set field value
     * @param field ISO 8583 field number
     * @param data String data
     * @param dataType field Type
     * @param dataLength Data length
     */
    public void setValue(int field, String data, ISO8583FieldInfo.Format dataType, int dataLength)
    {
        JSONObject jo = new JSONObject();

        if(dataType == ISO8583FieldInfo.Format.AMOUNT)
        {
            dataLength = 12;
        }

        if( dataType == ISO8583FieldInfo.Format.LVAR ||
                dataType == ISO8583FieldInfo.Format.LLVAR ||
                dataType == ISO8583FieldInfo.Format.LLLVAR )
        {
            dataLength = data.length();
        }

        if(dataLength == 0)
        {
            String fieldName = String.format("f", field);
            LinkedHashMap<String, String> fieldFormat = this.formatMap.get(fieldName);
            if(fieldFormat != null)
            {
                int c_field_length = Integer.parseInt(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.LENGTH.getText()));
                ISO8583FieldInfo.Format c_type = ISO8583FieldInfo.Format.valueOf(fieldFormat.get(ISO8583FieldInfo.DataElementConfig.TYPE.getText()));
                if(dataLength < c_field_length)
                {
                    dataLength = c_field_length;
                }

                if(dataType == ISO8583FieldInfo.Format.UNKNOWN)
                {
                    dataType = c_type;
                }
            }
        }
        jo.put(ISO8583FieldInfo.DataElementConfig.DATA.getText(), data);
        jo.put(ISO8583FieldInfo.DataElementConfig.TYPE.getText(), dataType);
        jo.put(ISO8583FieldInfo.DataElementConfig.LENGTH.getText(), dataLength);
        this.jsonItem.put("F"+field, jo);
    }
    /**
     * Get value of field
     * @param field ISO 8583 field number
     * @return String data of the field
     */
    public String getValue(int field)
    {
        JSONObject jo = new JSONObject();
        try
        {
            jo = (JSONObject) this.jsonItem.get("F"+field);
            if(jo != null)
            {
                return jo.optString(ISO8583FieldInfo.DataElementConfig.DATA.getText(), "");
            }
            else
            {
                return "";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return "";
        }
    }
    /**
     * Get value
     * @param field Specified field
     * @param defaultValue Default value
     * @return String value of field
     */
    public String getValue(int field, String defaultValue)
    {
        JSONObject jo = new JSONObject();
        try
        {
            jo = (JSONObject) this.jsonItem.get("F"+field);
            if(jo != null)
            {
                return jo.optString(ISO8583FieldInfo.DataElementConfig.DATA.getText(), defaultValue);
            }
            else
            {
                return defaultValue;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Create general configuration of ISO 8583
     */
    private static void parseGeneralConfig()
    {
        try
        {
            String configStr = "{\"f94\":{\"format\":\"%-7s\",\"name\":\"F94\",\"options\":\"\",\"length\":7,\"type\":\"STRING\"},\"f93\":{\"format\":\"%05d\",\"name\":\"F93\",\"options\":\"\",\"length\":5,\"type\":\"NUMERIC\"},\"f96\":{\"format\":\"%-8s\",\"name\":\"F96\",\"options\":\"\",\"length\":8,\"type\":\"STRING\"},\"f95\":{\"format\":\"%-42s\",\"name\":\"F95\",\"options\":\"\",\"length\":42,\"type\":\"STRING\"},\"f10\":{\"format\":\"%08d\",\"name\":\"F10\",\"options\":\"\",\"length\":8,\"type\":\"NUMERIC\"},\"f98\":{\"format\":\"%-25s\",\"name\":\"F98\",\"options\":\"\",\"length\":25,\"type\":\"STRING\"},\"f97\":{\"format\":\"%-17s\",\"name\":\"F97\",\"options\":\"\",\"length\":17,\"type\":\"STRING\"},\"f12\":{\"format\":\"%06d\",\"name\":\"F12\",\"options\":\"\",\"length\":6,\"type\":\"NUMERIC\"},\"f11\":{\"format\":\"%06d\",\"name\":\"F11\",\"options\":\"\",\"length\":6,\"type\":\"NUMERIC\"},\"f99\":{\"format\":\"%011d\",\"name\":\"F99\",\"options\":\"\",\"length\":11,\"type\":\"LLVAR\"},\"f14\":{\"format\":\"%04d\",\"name\":\"F14\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f13\":{\"format\":\"%04d\",\"name\":\"F13\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f16\":{\"format\":\"%04d\",\"name\":\"F16\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%04d\",\"name\":\"F15\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f18\":{\"format\":\"%04d\",\"name\":\"F18\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f17\":{\"format\":\"%04d\",\"name\":\"F17\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f19\":{\"format\":\"%03d\",\"name\":\"F19\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f126\":{\"format\":\"%-6s\",\"name\":\"F126\",\"options\":\"\",\"length\":6,\"type\":\"LVAR\"},\"f125\":{\"format\":\"%-50s\",\"name\":\"F125\",\"options\":\"\",\"length\":50,\"type\":\"LLVAR\"},\"f124\":{\"format\":\"%-255s\",\"name\":\"F124\",\"options\":\"\",\"length\":255,\"type\":\"LLLVAR\"},\"f123\":{\"format\":\"%-999s\",\"name\":\"F123\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f21\":{\"format\":\"%03d\",\"name\":\"F21\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f122\":{\"format\":\"%-999s\",\"name\":\"F122\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f20\":{\"format\":\"%03d\",\"name\":\"F20\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f121\":{\"format\":\"%-999s\",\"name\":\"F121\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f23\":{\"format\":\"%03d\",\"name\":\"F23\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f120\":{\"format\":\"%-999s\",\"name\":\"F120\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f22\":{\"format\":\"%03d\",\"name\":\"F22\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f25\":{\"format\":\"%02d\",\"name\":\"F25\",\"options\":\"\",\"length\":2,\"type\":\"NUMERIC\"},\"f24\":{\"format\":\"%03d\",\"name\":\"F24\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f27\":{\"format\":\"%01d\",\"name\":\"F27\",\"options\":\"\",\"length\":1,\"type\":\"NUMERIC\"},\"f26\":{\"format\":\"%02d\",\"name\":\"F26\",\"options\":\"\",\"length\":2,\"type\":\"NUMERIC\"},\"f29\":{\"format\":\"%-9s\",\"name\":\"F29\",\"options\":\"\",\"length\":9,\"type\":\"STRING\"},\"f28\":{\"format\":\"%08d\",\"name\":\"F28\",\"options\":\"\",\"length\":8,\"type\":\"NUMERIC\"},\"f127\":{\"format\":\"%-999s\",\"name\":\"F127\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f30\":{\"format\":\"%08d\",\"name\":\"F30\",\"options\":\"\",\"length\":8,\"type\":\"NUMERIC\"},\"f32\":{\"format\":\"%011d\",\"name\":\"F32\",\"options\":\"\",\"length\":11,\"type\":\"LLVAR\"},\"f31\":{\"format\":\"%-9s\",\"name\":\"F31\",\"options\":\"\",\"length\":9,\"type\":\"STRING\"},\"f34\":{\"format\":\"%-28s\",\"name\":\"F34\",\"options\":\"\",\"length\":28,\"type\":\"LLVAR\"},\"f33\":{\"format\":\"%011d\",\"name\":\"F33\",\"options\":\"\",\"length\":11,\"type\":\"LLVAR\"},\"f36\":{\"format\":\"%0104d\",\"name\":\"F36\",\"options\":\"\",\"length\":104,\"type\":\"LLLVAR\"},\"f35\":{\"format\":\"%-28s\",\"name\":\"F35\",\"options\":\"\",\"length\":37,\"type\":\"LLVAR\"},\"f38\":{\"format\":\"%-6s\",\"name\":\"F38\",\"options\":\"\",\"length\":6,\"type\":\"STRING\"},\"f37\":{\"format\":\"%-12s\",\"name\":\"F37\",\"options\":\"\",\"length\":12,\"type\":\"STRING\"},\"f39\":{\"format\":\"%-2s\",\"name\":\"F39\",\"options\":\"\",\"length\":2,\"type\":\"STRING\"},\"f41\":{\"format\":\"%-8s\",\"name\":\"F41\",\"options\":\"\",\"length\":8,\"type\":\"STRING\"},\"f40\":{\"format\":\"%-3s\",\"name\":\"F40\",\"options\":\"\",\"length\":3,\"type\":\"STRING\"},\"f43\":{\"format\":\"%-40s\",\"name\":\"F43\",\"options\":\"\",\"length\":40,\"type\":\"STRING\"},\"f42\":{\"format\":\"%-15s\",\"name\":\"F42\",\"options\":\"\",\"length\":15,\"type\":\"STRING\"},\"f45\":{\"format\":\"%-76s\",\"name\":\"F45\",\"options\":\"\",\"length\":76,\"type\":\"LLVAR\"},\"f44\":{\"format\":\"%-25s\",\"name\":\"F44\",\"options\":\"\",\"length\":25,\"type\":\"LLVAR\"},\"f47\":{\"format\":\"%-999s\",\"name\":\"F47\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f46\":{\"format\":\"%-999s\",\"name\":\"F46\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f49\":{\"format\":\"%-3s\",\"name\":\"F49\",\"options\":\"\",\"length\":3,\"type\":\"STRING\"},\"f48\":{\"format\":\"%-119s\",\"name\":\"F48\",\"options\":\"\",\"length\":119,\"type\":\"LLLVAR\"},\"f50\":{\"format\":\"%-3s\",\"name\":\"F50\",\"options\":\"\",\"length\":3,\"type\":\"STRING\"},\"f52\":{\"format\":\"%-16s\",\"name\":\"F52\",\"options\":\"\",\"length\":16,\"type\":\"STRING\"},\"f51\":{\"format\":\"%-3s\",\"name\":\"F51\",\"options\":\"\",\"length\":3,\"type\":\"STRING\"},\"f54\":{\"format\":\"%-120s\",\"name\":\"F54\",\"options\":\"\",\"length\":120,\"type\":\"STRING\"},\"f53\":{\"format\":\"%-18s\",\"name\":\"F53\",\"options\":\"\",\"length\":18,\"type\":\"STRING\"},\"f56\":{\"format\":\"%-999s\",\"name\":\"F56\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f55\":{\"format\":\"%-999s\",\"name\":\"F55\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f58\":{\"format\":\"%-999s\",\"name\":\"F58\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f57\":{\"format\":\"%-999s\",\"name\":\"F57\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f59\":{\"format\":\"%-99s\",\"name\":\"F59\",\"options\":\"\",\"length\":99,\"type\":\"LLVAR\"},\"f2\":{\"format\":\"%-19s\",\"name\":\"F2\",\"options\":\"\",\"length\":19,\"type\":\"LLVAR\"},\"f3\":{\"format\":\"%06d\",\"name\":\"F3\",\"options\":\"\",\"length\":6,\"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\",\"name\":\"F4\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f5\":{\"format\":\"%012d\",\"name\":\"F5\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f6\":{\"format\":\"%012d\",\"name\":\"F6\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\",\"name\":\"F7\",\"options\":\"\",\"length\":10,\"type\":\"STRING\"},\"f8\":{\"format\":\"%08d\",\"name\":\"F8\",\"options\":\"\",\"length\":8,\"type\":\"NUMERIC\"},\"f9\":{\"format\":\"%08d\",\"name\":\"F9\",\"options\":\"\",\"length\":8,\"type\":\"NUMERIC\"},\"f61\":{\"format\":\"%-99s\",\"name\":\"F61\",\"options\":\"\",\"length\":99,\"type\":\"LLVAR\"},\"f60\":{\"format\":\"%-60s\",\"name\":\"F60\",\"options\":\"\",\"length\":60,\"type\":\"LLVAR\"},\"f63\":{\"format\":\"%-999s\",\"name\":\"F63\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f62\":{\"format\":\"%-999s\",\"name\":\"F62\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f67\":{\"format\":\"%02d\",\"name\":\"F67\",\"options\":\"\",\"length\":2,\"type\":\"NUMERIC\"},\"f66\":{\"format\":\"%01d\",\"name\":\"F66\",\"options\":\"\",\"length\":1,\"type\":\"NUMERIC\"},\"f69\":{\"format\":\"%03d\",\"name\":\"F69\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f68\":{\"format\":\"%03d\",\"name\":\"F68\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f70\":{\"format\":\"%03d\",\"name\":\"F70\",\"options\":\"\",\"length\":3,\"type\":\"NUMERIC\"},\"f72\":{\"format\":\"%-999s\",\"name\":\"F72\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f115\":{\"format\":\"%-999s\",\"name\":\"F115\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f71\":{\"format\":\"%04d\",\"name\":\"F71\",\"options\":\"\",\"length\":4,\"type\":\"NUMERIC\"},\"f114\":{\"format\":\"%-999s\",\"name\":\"F114\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f74\":{\"format\":\"%010d\",\"name\":\"F74\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f113\":{\"format\":\"%011d\",\"name\":\"F113\",\"options\":\"\",\"length\":11,\"type\":\"LLVAR\"},\"f73\":{\"format\":\"%06d\",\"name\":\"F73\",\"options\":\"\",\"length\":6,\"type\":\"NUMERIC\"},\"f112\":{\"format\":\"%-999s\",\"name\":\"F112\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f76\":{\"format\":\"%010d\",\"name\":\"F76\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f111\":{\"format\":\"%-999s\",\"name\":\"F111\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f75\":{\"format\":\"%010d\",\"name\":\"F75\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f110\":{\"format\":\"%-999s\",\"name\":\"F110\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f78\":{\"format\":\"%010d\",\"name\":\"F78\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f77\":{\"format\":\"%010d\",\"name\":\"F77\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f79\":{\"format\":\"%010d\",\"name\":\"F79\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f119\":{\"format\":\"%-999s\",\"name\":\"F119\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f118\":{\"format\":\"%-999s\",\"name\":\"F118\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f81\":{\"format\":\"%010d\",\"name\":\"F81\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f117\":{\"format\":\"%-999s\",\"name\":\"F117\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f80\":{\"format\":\"%010d\",\"name\":\"F80\",\"options\":\"\",\"length\":10,\"type\":\"NUMERIC\"},\"f116\":{\"format\":\"%-999s\",\"name\":\"F116\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f83\":{\"format\":\"%012d\",\"name\":\"F83\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f104\":{\"format\":\"%-100s\",\"name\":\"F104\",\"options\":\"\",\"length\":100,\"type\":\"LLLVAR\"},\"f82\":{\"format\":\"%012d\",\"name\":\"F82\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f103\":{\"format\":\"%-28s\",\"name\":\"F103\",\"options\":\"\",\"length\":28,\"type\":\"LLVAR\"},\"f85\":{\"format\":\"%012d\",\"name\":\"F85\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f102\":{\"format\":\"%-28s\",\"name\":\"F102\",\"options\":\"\",\"length\":28,\"type\":\"LLVAR\"},\"f84\":{\"format\":\"%012d\",\"name\":\"F84\",\"options\":\"\",\"length\":12,\"type\":\"NUMERIC\"},\"f101\":{\"format\":\"%-17s\",\"name\":\"F101\",\"options\":\"\",\"length\":17,\"type\":\"STRING\"},\"f87\":{\"format\":\"%-16s\",\"name\":\"F87\",\"options\":\"\",\"length\":16,\"type\":\"STRING\"},\"f100\":{\"format\":\"%011d\",\"name\":\"F100\",\"options\":\"\",\"length\":11,\"type\":\"LLVAR\"},\"f86\":{\"format\":\"%015d\",\"name\":\"F86\",\"options\":\"\",\"length\":15,\"type\":\"NUMERIC\"},\"f89\":{\"format\":\"%016d\",\"name\":\"F89\",\"options\":\"\",\"length\":16,\"type\":\"NUMERIC\"},\"f88\":{\"format\":\"%016d\",\"name\":\"F88\",\"options\":\"\",\"length\":16,\"type\":\"NUMERIC\"},\"f109\":{\"format\":\"%-999s\",\"name\":\"F109\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f90\":{\"format\":\"%-42s\",\"name\":\"F90\",\"options\":\"\",\"length\":42,\"type\":\"STRING\"},\"f108\":{\"format\":\"%-999s\",\"name\":\"F108\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f107\":{\"format\":\"%-999s\",\"name\":\"F107\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f92\":{\"format\":\"%02d\",\"name\":\"F92\",\"options\":\"\",\"length\":2,\"type\":\"NUMERIC\"},\"f106\":{\"format\":\"%-999s\",\"name\":\"F106\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"},\"f91\":{\"format\":\"%-1s\",\"name\":\"F91\",\"options\":\"\",\"length\":1,\"type\":\"STRING\"},\"f105\":{\"format\":\"%-999s\",\"name\":\"F105\",\"options\":\"\",\"length\":999,\"type\":\"LLLVAR\"}}";

            ObjectMapper jsonMapper = new ObjectMapper();
            ISO8583Structure.generalFormatMapConfig = jsonMapper.readValue(configStr, new TypeReference<>() {});
        }
        catch ( JsonProcessingException e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    /**
     * List the all format to string set
     * @return String set of the format fields
     */
    public Set<String> formatMapKeySet()
    {
        return formatMap.keySet();
    }

    /**
     * Get length of all subfields
     * @param format Format of the field
     * @return Array containing each of subfield length
     */
    public static int[] getSubfieldLength(String format)
    {
        Pattern p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(format);
        String fmt = "";
        int i = 0;
        while(m.find())
        {
            i++;
        }

        int[] lengths = new int[i];
        p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        m = p.matcher(format);
        i = 0;
        while(m.find())
        {
            fmt = m.group(0);
            fmt = fmt.replaceAll("-", "");
            fmt = lTrim(fmt, "%");
            fmt = lTrim(fmt, "+");
            fmt = lTrim(fmt, "-");
            fmt = lTrim(fmt, ",");
            fmt = rTrim(fmt, "s");
            fmt = rTrim(fmt, "d");
            if(fmt.contains("."))
            {
                String[] tmp = fmt.split(".");
                fmt = tmp[0];
            }
            fmt = lTrim(fmt, "0");
            fmt = fmt.trim();
            if(fmt.isEmpty())
            {
                fmt = "0";
            }
            lengths[i] = Integer.parseInt(fmt);
            i++;
        }
        return lengths;
    }
    /**
     * Calculate all subfields length
     * @param format Format of the field
     * @return Total length of the subfields
     */
    public static int getSubfieldLengthTotal(String format)
    {
        Pattern p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(format);
        String fmt = "";
        int length = 0;
        while(m.find())
        {
            fmt = m.group(0);
            fmt = fmt.replaceAll("-", "");
            fmt = lTrim(fmt, "%");
            fmt = lTrim(fmt, "+");
            fmt = lTrim(fmt, "-");
            fmt = lTrim(fmt, ",");
            fmt = rTrim(fmt, "s");
            fmt = rTrim(fmt, "d");
            if(fmt.contains("."))
            {
                String[] tmp = fmt.split(".");
                fmt = tmp[0];
            }
            fmt = lTrim(fmt, "0");
            fmt = fmt.trim();
            if(fmt.isEmpty())
            {
                fmt = "0";
            }
            length += Integer.parseInt(fmt);
        }
        return length;
    }

    /**
     * Because substring in Java not supported multi line string, so use this function instead
     * @param input Input string
     * @param start Start offset
     * @param end End offset
     * @return Return substring from start to end
     */
    private String substringOf(String input, int start, int end)
    {
        byte[] inputByte = input.getBytes();
        String tmp2;
        byte[] tmp3 = new byte[1];
        int i;
        StringBuilder output = new StringBuilder();
        try
        {
            for(i = start; i<end; i++)
            {
                tmp3[0] = inputByte[i];
                tmp2 = new String(tmp3);
                output.append(tmp2);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return input;
        }
        return output.toString();
    }

    /**
     * Because substring in Java not supported multi line string, so use this function instead
     * @param input Input string
     * @param start Start offset
     * @return Return substring from start
     */
    private String substringOf(String input, int start)
    {
        byte[] inputByte = input.getBytes();
        int end = input.length();
        String tmp2;
        byte[] tmp3 = new byte[1];
        int i;
        StringBuilder output = new StringBuilder();
        try
        {
            for(i = start; i<end; i++)
            {
                tmp3[0] = inputByte[i];
                tmp2 = new String(tmp3);
                output.append(tmp2);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return input;
        }
        return output.toString();
    }


    /**
     * Check if format more than one
     * @param format Format to be checked
     * @return true if more than one format. Otherwise return false
     */
    public static boolean hasSubfield(String format)
    {
        Pattern p = Pattern.compile("\\%([0-9\\+\\-\\,\\.]*)[sdf]", Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(format);
        int i = 0;
        while(m.find())
        {
            i++;
        }
        if(i > 1)
        {
            return true;
        }
        return false;
    }

    /**
     * Check whether the word is exists on the string array
     * @param haystack String array from where the word to be search
     * @param needle Word to be search
     * @param strict Flag for case sensitive or insensitive
     * @return true if word is in array. Otherwise return false
     */
    private boolean isInArray(Set<String> haystack, String needle, boolean strict)
    {
        if(strict)
        {
            return haystack.contains(needle);
        }
        else
        {
            return haystack.stream().anyMatch(needle::equalsIgnoreCase);
        }
    }

    /**
     * Check whether the word is exists on the string array and ignore case
     * @param haystack String array from where the word to be search
     * @param needle Word to be search
     * @return true if word is in array. Otherwise return false
     */
    public boolean isInArray(Set<String> haystack, String needle)
    {
        return isInArray(haystack, needle, false);
    }

    /**
     * Converts a hex String to array of bytes
     * @param hex string
     * @return binary array, is half the length of the input string
     * @throws IllegalArgumentException
     */
    public static byte[] hex2bin(String hex) throws IllegalArgumentException {
        byte[] ret = null;
        if (hex == null || "".equals(hex)) {
            ret = new byte[] {0};
            return ret;
        }

        String uhex = hex.toUpperCase();
        if (!patHex.matcher(uhex).matches()) {
            throw new IllegalArgumentException("Expecting Hex String got [" + hex + "]");
        }

        if ((uhex.length() % 2) != 0) {
            uhex = "0" + uhex;
        }
        ret = new byte[uhex.length() / 2];
        for (int i = 0, j = 0; i < ret.length; i++, j += 2) {
            char l = uhex.charAt(j + 1);
            char m = uhex.charAt(j);
            byte lsb, msb;
            lsb = (l >= '0' && l <= '9') ? (byte) (l - '0') : (byte) ((l - 'A') + 10);
            msb = (m >= '0' && m <= '9') ? (byte) (m - '0') : (byte) ((m - 'A') + 10);
            ret[i] = 0;
            ret[i] = (byte) (lsb & (byte) 0xf);
            ret[i] |= (byte) ((msb << 4) & 0xf0);
        }
        return ret;
    }
    /**
     * Converts a binary array into a String of Hex
     * @param bin
     * @return Hex String, twice the size of input byte array
     */
    public static String bin2hex(byte[] bin) {
        return bin2hex(bin,0,bin.length);
    }
    public static String bin2hex(byte[] bin, int curOffset, int len) {
        if (bin == null || curOffset >= len || curOffset < 0 || bin.length < (len-curOffset)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bin.length * 2);
        for (int i = curOffset; i < len; i++) {
            int ndx = bin[i] & 0xff;
            sb.append(hexNdx[(ndx >>> 4)]);
            sb.append(hexNdx[(ndx & 0xf)]);
        }
        return sb.toString();
    }

}
