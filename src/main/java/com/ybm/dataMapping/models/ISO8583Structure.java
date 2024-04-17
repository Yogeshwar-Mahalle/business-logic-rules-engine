/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.models;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
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
    private String type = "0200";

    /**
     * Primary bitmap. Java int takes 4 bytes for int hence for 2 int it takes 8 bytes.
     * 8 bytes * 8 bits = 64 bits bitmap
     */
    private int[] primaryBitMap = {0, 0};
    /**
     * Secondary bitmap. Java int takes 4 bytes for int hence for 2 int it takes 8 bytes.
     * 8 bytes * 8 bits = 64 bits bitmap
     */
    private int[] secondaryBitMap = {0, 0};
    /**
     * Tertiary bitmap. Java int takes 4 bytes for int hence for 2 int it takes 8 bytes.
     * 8 bytes * 8 bits = 64 bits bitmap
     */
    private int[] tertiaryBitMap = {0, 0};

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
    private JSONObject config = new JSONObject();

    /**
     * General ISO8583 configuration. When server receive ISO8583 string message and contains any field that is not configured in specs,
     * it will be parsed using general config of ISO8583.
     */
    private static JSONObject generalConfig = null;

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
        if(ISO8583Structure.generalConfig == null)
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
        this.setType(mti_id);
        if(ISO8583Structure.generalConfig == null)
        {
            ISO8583Structure.parseGeneralConfig();
        }
    }
    /**
     * Constructor with Message Type Indicator and configuration of the specs
     * @param mti_id Message Type Indicator
     * @param config Configuration of the specs
     */
    public ISO8583Structure(String mti_id, JSONObject config)
    {
        this.setType(mti_id);
        this.setConfig(config);
    }

    /**
     * Constructor with Message Type Indicator, configuration of the specs and message to be parse
     * @param mti_id Message Type Indicator
     * @param config Configuration of the specs
     * @param message Message to be parse
     */
    public ISO8583Structure(String mti_id, JSONObject config, String message)
    {
        this.setType(mti_id);
        this.setConfig(config);
        this.parse(message);
    }

    /**
     * Set field
     * @param field ISO8583 field number
     * @param data ISO8583 field data
     */
    public void setField(int field, ISO8583Field data)
    {
        this.addBit(field);
        this.setValue(field, data.data, data.type, data.length);
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
            fieldData.setField( data.optString("data", ""),
                    data.optString("type", "STRING"),
                    Integer.parseInt(data.optString("length", "1")) );
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

        String primaryBitMap0Str = "";
        String primaryBitMap1Str = "";
        int primaryBitMap0Int = 0;
        int primaryBitMap1Int = 0;

        String secondaryBitMap0Str = "";
        String secondaryBitMap1Str = "";
        int secondaryBitMap0Int = 0;
        int secondaryBitMap1Int = 0;

        String tertiaryBitMap0Str = "";
        String tertiaryBitMap1Str = "";
        int tertiaryBitMap0Int = 0;
        int tertiaryBitMap1Int = 0;

        this.type = message.substring(0, 4);
        primaryBitMap0Str = message.substring(4, 12).replaceAll("[^A-Fa-f0-9]", "");
        primaryBitMap1Str = message.substring(12, 20).replaceAll("[^A-Fa-f0-9]", "");
        if(primaryBitMap0Str.isEmpty())
        {
            primaryBitMap0Str = "0";
        }
        if(primaryBitMap1Str.isEmpty())
        {
            primaryBitMap1Str = "0";
        }
        primaryBitMap0Int = (int)Long.parseLong(primaryBitMap0Str, 16);
        primaryBitMap1Int = (int)Long.parseLong(primaryBitMap1Str, 16);
        this.primaryBitMap[0] = primaryBitMap0Int;
        this.primaryBitMap[1] = primaryBitMap1Int;
        if((this.primaryBitMap[0] & 0x80000000) == 0x80000000)
        {
            secondaryBitMap0Str = message.substring(20, 28).replaceAll("[^A-Fa-f0-9]", "");
            secondaryBitMap1Str = message.substring(28, 36).replaceAll("[^A-Fa-f0-9]", "");
            if(secondaryBitMap0Str.isEmpty())
            {
                secondaryBitMap0Str = "0";
            }
            if(secondaryBitMap1Str.isEmpty())
            {
                secondaryBitMap1Str = "0";
            }
            secondaryBitMap0Int = (int)Long.parseLong(secondaryBitMap0Str, 16);
            secondaryBitMap1Int = (int)Long.parseLong(secondaryBitMap1Str, 16);
            this.secondaryBitMap[0] = secondaryBitMap0Int;
            this.secondaryBitMap[1] = secondaryBitMap1Int;
        }
        if((this.secondaryBitMap[0] & 0x80000000) == 0x80000000)
        {
            tertiaryBitMap0Str = message.substring(36, 44).replaceAll("[^A-Fa-f0-9]", "");
            tertiaryBitMap1Str = message.substring(44, 52).replaceAll("[^A-Fa-f0-9]", "");
            if(tertiaryBitMap0Str.equals(""))
            {
                tertiaryBitMap0Str = "0";
            }
            if(tertiaryBitMap1Str.equals(""))
            {
                tertiaryBitMap1Str = "0";
            }
            tertiaryBitMap0Int = (int)Long.parseLong(tertiaryBitMap0Str, 16);
            tertiaryBitMap1Int = (int)Long.parseLong(tertiaryBitMap1Str, 16);
            this.tertiaryBitMap[0] = tertiaryBitMap0Int;
            this.tertiaryBitMap[1] = tertiaryBitMap1Int;
        }
        int iter = 0;
        // get field list from bitmap
        int i = 0;
        int k = 0;

        k = this.primaryBitMap[0];
        for(i = 1; i <= 32; i++)
        {
            if((k & 0x80000000) == 0x80000000 && i > 1)
            {
                this.addBit(i);
            }
            k = k - 0x80000000;
            k = k << 1;
        }
        k = this.primaryBitMap[1];
        for(i = 33; i <= 64; i++)
        {
            if((k & 0x80000000) == 0x80000000)
            {
                this.addBit(i);
            }
            k = k - 0x80000000;
            k = k << 1;
        }
        int bitmapLength = 16;
        if((this.primaryBitMap[0] & 0x80000000) == 0x80000000)
        {
            bitmapLength = 32;
            k = this.secondaryBitMap[0];
            for(i = 65; i <= 96; i++)
            {
                if((k & 0x80000000) == 0x80000000)
                {
                    this.addBit(i);
                }
                k = k - 0x80000000;
                k = k << 1;
            }
            k = this.secondaryBitMap[1];
            for(i = 97; i <= 128; i++)
            {
                if((k & 0x80000000) == 0x80000000)
                {
                    this.addBit(i);
                }
                k = k - 0x80000000;
                k = k << 1;
            }
            if((this.secondaryBitMap[0] & 0x80000000) == 0x80000000)
            {
                bitmapLength = 48;
                k = this.tertiaryBitMap[0];
                for(i = 129; i <= 160; i++)
                {
                    if((k & 0x80000000) == 0x80000000)
                    {
                        this.addBit(i);
                    }
                    k = k - 0x80000000;
                    k = k << 1;
                }
                k = this.tertiaryBitMap[1];
                for(i = 161; i <= 192; i++)
                {
                    if((k & 0x80000000) == 0x80000000)
                    {
                        this.addBit(i);
                    }
                    k = k - 0x80000000;
                    k = k << 1;
                }
            }
        }
        JSONObject jo = new JSONObject();
        String dataType = "";
        int dataLength = 0;
        int realLength = 0;
        int fieldLength = 0;
        String rawData = "";
        String shiftedData = "";
        int field = 0;

        boolean validMessage = true;
        for(iter = 0; iter<this.fields.length; iter++)
        {
            field = Integer.parseInt(this.fields[iter]);
            // get config
            jo = this.config.optJSONObject("f"+field);
            if(jo == null)
            {

                if(ISO8583Structure.generalConfig != null)
                {
                    jo = (JSONObject) ISO8583Structure.generalConfig.get("f"+field);
                    if(jo == null)
                    {
                        validMessage = false;
                    }
                }
                else
                {
                    validMessage = false;
                }
            }
        }
        if(validMessage)
        {
            if(message.length() > bitmapLength+4)
            {
                shiftedData = message.substring(bitmapLength+4);
                for(iter = 0; iter<this.fields.length; iter++)
                {
                    field = Integer.parseInt(this.fields[iter]);
                    jo = this.config.optJSONObject("f"+field);
                    if(jo == null)
                    {
                        if(ISO8583Structure.generalConfig != null)
                        {
                            jo = (JSONObject) ISO8583Structure.generalConfig.get("f"+field);
                        }
                    }
                    if(jo != null)
                    {
                        if(jo.get("type") != null)
                        {
                            dataType = jo.get("type").toString();
                            fieldLength = Integer.parseInt(jo.get("field_length").toString());
                            dataLength = fieldLength;
                            realLength = dataLength;
                            rawData = "";
                            if(dataType.equals("LLLVAR"))
                            {
                                if(shiftedData.length() >= 3)
                                {
                                    rawData = shiftedData.substring(0, 3);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if(ln.isEmpty())
                                    {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(3);
                                    if(shiftedData.length() >= realLength)
                                    {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    }
                                    else
                                    {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            }
                            else if(dataType.equals("LLVAR"))
                            {
                                if(shiftedData.length() >= 2)
                                {
                                    rawData = shiftedData.substring(0, 2);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if(ln.isEmpty())
                                    {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(2);
                                    if(shiftedData.length() >= realLength)
                                    {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    }
                                    else
                                    {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            }
                            else if(dataType.equals("LVAR"))
                            {
                                if(!shiftedData.isEmpty())
                                {
                                    rawData = shiftedData.substring(0, 1);
                                    ln = rawData.replaceAll("[^\\d.]", "");
                                    ln = lTrim(ln, "0");
                                    if(ln.isEmpty())
                                    {
                                        ln = "0";
                                    }
                                    realLength = Integer.parseInt(ln);
                                    shiftedData = shiftedData.substring(1);
                                    if(shiftedData.length() >= realLength)
                                    {
                                        rawData = shiftedData.substring(0, realLength);
                                        shiftedData = shiftedData.substring(realLength);
                                    }
                                    else
                                    {
                                        // insufficient data
                                        rawData = "";
                                    }
                                }
                            }
                            else if(dataType.equals("AMOUNT"))
                            {
                                if(shiftedData.length() >= 12)
                                {
                                    rawData = shiftedData.substring(0, 12);
                                    rawData = rawData.replaceAll("[^\\d]", "");
                                    rawData = lTrim(rawData, "0");
                                    if(rawData.isEmpty())
                                    {
                                        rawData = "0";
                                    }
                                    long numVal = Long.parseLong(rawData);
                                    rawData = String.format("%012d", numVal);
                                    shiftedData = shiftedData.substring(12);
                                    realLength = 12;
                                }
                            }
                            else
                            {
                                if(shiftedData.length() >= dataLength)
                                {
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
     * @param config JSONObject contains specs
     */
    public void parse(String message, JSONObject config)
    {
        this.setConfig(config);
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
            message = this.type+this.getBitmap()+this.getBody();
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
            if(fieldInt >= 1 && fieldInt <= 32)
            {
                primaryBitMap[0] = (primaryBitMap[0] | (1 << (32-fieldInt)));
            }
            if(fieldInt >= 33 && fieldInt <= 64)
            {
                primaryBitMap[1] = (primaryBitMap[1] | (1 << (32-fieldInt)));
            }
            if(fieldInt >= 65 && fieldInt <= 96)
            {
                secondaryBitMap[0] = (secondaryBitMap[0] | (1 << (32-fieldInt)));
            }
            if(fieldInt >= 97 && fieldInt <= 128)
            {
                secondaryBitMap[1] = (secondaryBitMap[1] | (1 << (32-fieldInt)));
            }
            if(fieldInt >= 129 && fieldInt <= 160)
            {
                tertiaryBitMap[0] = (tertiaryBitMap[0] | (1 << (32-fieldInt)));
            }
            if(fieldInt >= 161 && fieldInt <= 192)
            {
                tertiaryBitMap[1] = (tertiaryBitMap[1] | (1 << (32-fieldInt)));
            }
        }
        String h1 = "", h2 = "", h3 = "";
        if(maxField > 128)
        {
            primaryBitMap[0] = (primaryBitMap[0] | (1 << 31));
            secondaryBitMap[0] = (secondaryBitMap[0] | (1 << 31));
            h1 = String.format("%08x%08x", primaryBitMap[0], primaryBitMap[1]);
            h2 = String.format("%08x%08x", secondaryBitMap[0], secondaryBitMap[1]);
            h3 = String.format("%08x%08x", tertiaryBitMap[0], tertiaryBitMap[1]);
            bitmap = h1+h2+h3;
        }
        else if(maxField > 64)
        {
            primaryBitMap[0] = (primaryBitMap[0] | (1 << 31));
            h1 = String.format("%08x%08x", primaryBitMap[0], primaryBitMap[1]);
            h2 = String.format("%08x%08x", secondaryBitMap[0], secondaryBitMap[1]);
            bitmap = h1+h2;
        }
        else
        {
            h1 = String.format("%08x%08x", primaryBitMap[0], primaryBitMap[1]);
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
        String dataType = "";
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
                data = jo.get("data").toString();
                finalItemData = data;
                dataType = jo.get("type").toString();
                dataLength = Integer.parseInt(jo.get("length").toString());
                if(dataType.equals("AMOUNT"))
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
                else if(dataType.equals("LLLVAR"))
                {
                    len = String.format("%03d", data.length());
                    finalItemData = len+data;
                }
                else if(dataType.equals("LLVAR"))
                {
                    len = String.format("%02d", data.length());
                    finalItemData = len+data;
                }
                else if(dataType.equals("LVAR"))
                {
                    len = String.format("%1d", data.length());
                    finalItemData = len+data;
                }
                else if(dataType.equals("NUMERIC"))
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
    public void addValue(int field, String data, String dataType, int dataLength)
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
    public void setValue(int field, String data, String dataType, int dataLength)
    {
        JSONObject jo = new JSONObject();
        JSONObject j = new JSONObject();

        if(dataType.equals("AMOUNT"))
        {
            dataLength = 12;
        }
        if(dataType.equals("LVAR") || dataType.equals("LLVAR") || dataType.equals("LLLVAR"))
        {
            dataLength = data.length();
        }
        if(dataLength == 0)
        {
            String f = String.format("f", field);
            j = this.config.optJSONObject(f);
            if(j != null)
            {
                int c_field_length = Integer.parseInt(j.get("field_length").toString());
                String c_type = j.get("type").toString();
                if(dataLength < c_field_length)
                {
                    dataLength = c_field_length;
                }
                if(dataType.isEmpty())
                {
                    dataType = c_type;
                }
            }
        }
        jo.put("data", data);
        jo.put("type", dataType);
        jo.put("length", dataLength);
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
                return jo.optString("data", "");
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
                return jo.optString("data", defaultValue);
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
            ISO8583Structure.generalConfig = new JSONObject("{\"f94\":{\"format\":\"%-7s\",\"variable\":\"F94\",\"options\":\"\",\"field_length\":7,\"type\":\"STRING\"},\"f93\":{\"format\":\"%05d\",\"variable\":\"F93\",\"options\":\"\",\"field_length\":5,\"type\":\"NUMERIC\"},\"f96\":{\"format\":\"%-8s\",\"variable\":\"F96\",\"options\":\"\",\"field_length\":8,\"type\":\"STRING\"},\"f95\":{\"format\":\"%-42s\",\"variable\":\"F95\",\"options\":\"\",\"field_length\":42,\"type\":\"STRING\"},\"f10\":{\"format\":\"%08d\",\"variable\":\"F10\",\"options\":\"\",\"field_length\":8,\"type\":\"NUMERIC\"},\"f98\":{\"format\":\"%-25s\",\"variable\":\"F98\",\"options\":\"\",\"field_length\":25,\"type\":\"STRING\"},\"f97\":{\"format\":\"%-17s\",\"variable\":\"F97\",\"options\":\"\",\"field_length\":17,\"type\":\"STRING\"},\"f12\":{\"format\":\"%06d\",\"variable\":\"F12\",\"options\":\"\",\"field_length\":6,\"type\":\"NUMERIC\"},\"f11\":{\"format\":\"%06d\",\"variable\":\"F11\",\"options\":\"\",\"field_length\":6,\"type\":\"NUMERIC\"},\"f99\":{\"format\":\"%011d\",\"variable\":\"F99\",\"options\":\"\",\"field_length\":11,\"type\":\"LLVAR\"},\"f14\":{\"format\":\"%04d\",\"variable\":\"F14\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f13\":{\"format\":\"%04d\",\"variable\":\"F13\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f16\":{\"format\":\"%04d\",\"variable\":\"F16\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f15\":{\"format\":\"%04d\",\"variable\":\"F15\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f18\":{\"format\":\"%04d\",\"variable\":\"F18\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f17\":{\"format\":\"%04d\",\"variable\":\"F17\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f19\":{\"format\":\"%03d\",\"variable\":\"F19\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f126\":{\"format\":\"%-6s\",\"variable\":\"F126\",\"options\":\"\",\"field_length\":6,\"type\":\"LVAR\"},\"f125\":{\"format\":\"%-50s\",\"variable\":\"F125\",\"options\":\"\",\"field_length\":50,\"type\":\"LLVAR\"},\"f124\":{\"format\":\"%-255s\",\"variable\":\"F124\",\"options\":\"\",\"field_length\":255,\"type\":\"LLLVAR\"},\"f123\":{\"format\":\"%-999s\",\"variable\":\"F123\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f21\":{\"format\":\"%03d\",\"variable\":\"F21\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f122\":{\"format\":\"%-999s\",\"variable\":\"F122\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f20\":{\"format\":\"%03d\",\"variable\":\"F20\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f121\":{\"format\":\"%-999s\",\"variable\":\"F121\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f23\":{\"format\":\"%03d\",\"variable\":\"F23\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f120\":{\"format\":\"%-999s\",\"variable\":\"F120\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f22\":{\"format\":\"%03d\",\"variable\":\"F22\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f25\":{\"format\":\"%02d\",\"variable\":\"F25\",\"options\":\"\",\"field_length\":2,\"type\":\"NUMERIC\"},\"f24\":{\"format\":\"%03d\",\"variable\":\"F24\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f27\":{\"format\":\"%01d\",\"variable\":\"F27\",\"options\":\"\",\"field_length\":1,\"type\":\"NUMERIC\"},\"f26\":{\"format\":\"%02d\",\"variable\":\"F26\",\"options\":\"\",\"field_length\":2,\"type\":\"NUMERIC\"},\"f29\":{\"format\":\"%-9s\",\"variable\":\"F29\",\"options\":\"\",\"field_length\":9,\"type\":\"STRING\"},\"f28\":{\"format\":\"%08d\",\"variable\":\"F28\",\"options\":\"\",\"field_length\":8,\"type\":\"NUMERIC\"},\"f127\":{\"format\":\"%-999s\",\"variable\":\"F127\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f30\":{\"format\":\"%08d\",\"variable\":\"F30\",\"options\":\"\",\"field_length\":8,\"type\":\"NUMERIC\"},\"f32\":{\"format\":\"%011d\",\"variable\":\"F32\",\"options\":\"\",\"field_length\":11,\"type\":\"LLVAR\"},\"f31\":{\"format\":\"%-9s\",\"variable\":\"F31\",\"options\":\"\",\"field_length\":9,\"type\":\"STRING\"},\"f34\":{\"format\":\"%-28s\",\"variable\":\"F34\",\"options\":\"\",\"field_length\":28,\"type\":\"LLVAR\"},\"f33\":{\"format\":\"%011d\",\"variable\":\"F33\",\"options\":\"\",\"field_length\":11,\"type\":\"LLVAR\"},\"f36\":{\"format\":\"%0104d\",\"variable\":\"F36\",\"options\":\"\",\"field_length\":104,\"type\":\"LLLVAR\"},\"f35\":{\"format\":\"%-28s\",\"variable\":\"F35\",\"options\":\"\",\"field_length\":37,\"type\":\"LLVAR\"},\"f38\":{\"format\":\"%-6s\",\"variable\":\"F38\",\"options\":\"\",\"field_length\":6,\"type\":\"STRING\"},\"f37\":{\"format\":\"%-12s\",\"variable\":\"F37\",\"options\":\"\",\"field_length\":12,\"type\":\"STRING\"},\"f39\":{\"format\":\"%-2s\",\"variable\":\"F39\",\"options\":\"\",\"field_length\":2,\"type\":\"STRING\"},\"f41\":{\"format\":\"%-8s\",\"variable\":\"F41\",\"options\":\"\",\"field_length\":8,\"type\":\"STRING\"},\"f40\":{\"format\":\"%-3s\",\"variable\":\"F40\",\"options\":\"\",\"field_length\":3,\"type\":\"STRING\"},\"f43\":{\"format\":\"%-40s\",\"variable\":\"F43\",\"options\":\"\",\"field_length\":40,\"type\":\"STRING\"},\"f42\":{\"format\":\"%-15s\",\"variable\":\"F42\",\"options\":\"\",\"field_length\":15,\"type\":\"STRING\"},\"f45\":{\"format\":\"%-76s\",\"variable\":\"F45\",\"options\":\"\",\"field_length\":76,\"type\":\"LLVAR\"},\"f44\":{\"format\":\"%-25s\",\"variable\":\"F44\",\"options\":\"\",\"field_length\":25,\"type\":\"LLVAR\"},\"f47\":{\"format\":\"%-999s\",\"variable\":\"F47\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f46\":{\"format\":\"%-999s\",\"variable\":\"F46\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f49\":{\"format\":\"%-3s\",\"variable\":\"F49\",\"options\":\"\",\"field_length\":3,\"type\":\"STRING\"},\"f48\":{\"format\":\"%-119s\",\"variable\":\"F48\",\"options\":\"\",\"field_length\":119,\"type\":\"LLLVAR\"},\"f50\":{\"format\":\"%-3s\",\"variable\":\"F50\",\"options\":\"\",\"field_length\":3,\"type\":\"STRING\"},\"f52\":{\"format\":\"%-16s\",\"variable\":\"F52\",\"options\":\"\",\"field_length\":16,\"type\":\"STRING\"},\"f51\":{\"format\":\"%-3s\",\"variable\":\"F51\",\"options\":\"\",\"field_length\":3,\"type\":\"STRING\"},\"f54\":{\"format\":\"%-120s\",\"variable\":\"F54\",\"options\":\"\",\"field_length\":120,\"type\":\"STRING\"},\"f53\":{\"format\":\"%-18s\",\"variable\":\"F53\",\"options\":\"\",\"field_length\":18,\"type\":\"STRING\"},\"f56\":{\"format\":\"%-999s\",\"variable\":\"F56\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f55\":{\"format\":\"%-999s\",\"variable\":\"F55\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f58\":{\"format\":\"%-999s\",\"variable\":\"F58\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f57\":{\"format\":\"%-999s\",\"variable\":\"F57\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f59\":{\"format\":\"%-99s\",\"variable\":\"F59\",\"options\":\"\",\"field_length\":99,\"type\":\"LLVAR\"},\"f2\":{\"format\":\"%-19s\",\"variable\":\"F2\",\"options\":\"\",\"field_length\":19,\"type\":\"LLVAR\"},\"f3\":{\"format\":\"%06d\",\"variable\":\"F3\",\"options\":\"\",\"field_length\":6,\"type\":\"NUMERIC\"},\"f4\":{\"format\":\"%012d\",\"variable\":\"F4\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f5\":{\"format\":\"%012d\",\"variable\":\"F5\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f6\":{\"format\":\"%012d\",\"variable\":\"F6\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f7\":{\"format\":\"%-10s\",\"variable\":\"F7\",\"options\":\"\",\"field_length\":10,\"type\":\"STRING\"},\"f8\":{\"format\":\"%08d\",\"variable\":\"F8\",\"options\":\"\",\"field_length\":8,\"type\":\"NUMERIC\"},\"f9\":{\"format\":\"%08d\",\"variable\":\"F9\",\"options\":\"\",\"field_length\":8,\"type\":\"NUMERIC\"},\"f61\":{\"format\":\"%-99s\",\"variable\":\"F61\",\"options\":\"\",\"field_length\":99,\"type\":\"LLVAR\"},\"f60\":{\"format\":\"%-60s\",\"variable\":\"F60\",\"options\":\"\",\"field_length\":60,\"type\":\"LLVAR\"},\"f63\":{\"format\":\"%-999s\",\"variable\":\"F63\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f62\":{\"format\":\"%-999s\",\"variable\":\"F62\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f67\":{\"format\":\"%02d\",\"variable\":\"F67\",\"options\":\"\",\"field_length\":2,\"type\":\"NUMERIC\"},\"f66\":{\"format\":\"%01d\",\"variable\":\"F66\",\"options\":\"\",\"field_length\":1,\"type\":\"NUMERIC\"},\"f69\":{\"format\":\"%03d\",\"variable\":\"F69\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f68\":{\"format\":\"%03d\",\"variable\":\"F68\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f70\":{\"format\":\"%03d\",\"variable\":\"F70\",\"options\":\"\",\"field_length\":3,\"type\":\"NUMERIC\"},\"f72\":{\"format\":\"%-999s\",\"variable\":\"F72\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f115\":{\"format\":\"%-999s\",\"variable\":\"F115\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f71\":{\"format\":\"%04d\",\"variable\":\"F71\",\"options\":\"\",\"field_length\":4,\"type\":\"NUMERIC\"},\"f114\":{\"format\":\"%-999s\",\"variable\":\"F114\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f74\":{\"format\":\"%010d\",\"variable\":\"F74\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f113\":{\"format\":\"%011d\",\"variable\":\"F113\",\"options\":\"\",\"field_length\":11,\"type\":\"LLVAR\"},\"f73\":{\"format\":\"%06d\",\"variable\":\"F73\",\"options\":\"\",\"field_length\":6,\"type\":\"NUMERIC\"},\"f112\":{\"format\":\"%-999s\",\"variable\":\"F112\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f76\":{\"format\":\"%010d\",\"variable\":\"F76\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f111\":{\"format\":\"%-999s\",\"variable\":\"F111\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f75\":{\"format\":\"%010d\",\"variable\":\"F75\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f110\":{\"format\":\"%-999s\",\"variable\":\"F110\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f78\":{\"format\":\"%010d\",\"variable\":\"F78\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f77\":{\"format\":\"%010d\",\"variable\":\"F77\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f79\":{\"format\":\"%010d\",\"variable\":\"F79\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f119\":{\"format\":\"%-999s\",\"variable\":\"F119\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f118\":{\"format\":\"%-999s\",\"variable\":\"F118\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f81\":{\"format\":\"%010d\",\"variable\":\"F81\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f117\":{\"format\":\"%-999s\",\"variable\":\"F117\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f80\":{\"format\":\"%010d\",\"variable\":\"F80\",\"options\":\"\",\"field_length\":10,\"type\":\"NUMERIC\"},\"f116\":{\"format\":\"%-999s\",\"variable\":\"F116\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f83\":{\"format\":\"%012d\",\"variable\":\"F83\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f104\":{\"format\":\"%-100s\",\"variable\":\"F104\",\"options\":\"\",\"field_length\":100,\"type\":\"LLLVAR\"},\"f82\":{\"format\":\"%012d\",\"variable\":\"F82\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f103\":{\"format\":\"%-28s\",\"variable\":\"F103\",\"options\":\"\",\"field_length\":28,\"type\":\"LLVAR\"},\"f85\":{\"format\":\"%012d\",\"variable\":\"F85\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f102\":{\"format\":\"%-28s\",\"variable\":\"F102\",\"options\":\"\",\"field_length\":28,\"type\":\"LLVAR\"},\"f84\":{\"format\":\"%012d\",\"variable\":\"F84\",\"options\":\"\",\"field_length\":12,\"type\":\"NUMERIC\"},\"f101\":{\"format\":\"%-17s\",\"variable\":\"F101\",\"options\":\"\",\"field_length\":17,\"type\":\"STRING\"},\"f87\":{\"format\":\"%-16s\",\"variable\":\"F87\",\"options\":\"\",\"field_length\":16,\"type\":\"STRING\"},\"f100\":{\"format\":\"%011d\",\"variable\":\"F100\",\"options\":\"\",\"field_length\":11,\"type\":\"LLVAR\"},\"f86\":{\"format\":\"%015d\",\"variable\":\"F86\",\"options\":\"\",\"field_length\":15,\"type\":\"NUMERIC\"},\"f89\":{\"format\":\"%016d\",\"variable\":\"F89\",\"options\":\"\",\"field_length\":16,\"type\":\"NUMERIC\"},\"f88\":{\"format\":\"%016d\",\"variable\":\"F88\",\"options\":\"\",\"field_length\":16,\"type\":\"NUMERIC\"},\"f109\":{\"format\":\"%-999s\",\"variable\":\"F109\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f90\":{\"format\":\"%-42s\",\"variable\":\"F90\",\"options\":\"\",\"field_length\":42,\"type\":\"STRING\"},\"f108\":{\"format\":\"%-999s\",\"variable\":\"F108\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f107\":{\"format\":\"%-999s\",\"variable\":\"F107\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f92\":{\"format\":\"%02d\",\"variable\":\"F92\",\"options\":\"\",\"field_length\":2,\"type\":\"NUMERIC\"},\"f106\":{\"format\":\"%-999s\",\"variable\":\"F106\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"},\"f91\":{\"format\":\"%-1s\",\"variable\":\"F91\",\"options\":\"\",\"field_length\":1,\"type\":\"STRING\"},\"f105\":{\"format\":\"%-999s\",\"variable\":\"F105\",\"options\":\"\",\"field_length\":999,\"type\":\"LLLVAR\"}}");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    /**
     * List the all format to string list
     * @param formatMap JSON containing the format
     * @return String list of the format
     */
    public static List<String> listFormatMapKey(JSONObject formatMap)
    {
        Set<?> s =  formatMap.keySet();
        Iterator<?> iter = s.iterator();
        List<String> keys = new ArrayList<>();
        String key;
        do
        {
            key = iter.next().toString();
            keys.add(key);
        }
        while(iter.hasNext());
        return keys;
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

        int lengths[] = new int[i];
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
     * Convert string list to string array
     * @param stringList String list to be converted
     * @return String array converted from form string list
     */
    public static String[] listStringToArrayString(List<String> stringList)
    {
        String[] strarray = new String[stringList.size()];
        stringList.toArray(strarray);
        return strarray;
    }
    /**
     * Check whether the word is exists on the string array
     * @param haystack String array from where the word to be search
     * @param needle Word to be search
     * @param strict Flag for case sensitive or insensitive
     * @return true if word is in array. Otherwise return false
     */
    private static boolean inArray(String[] haystack, String needle, boolean strict)
    {
        int i;
        String t1, t2;
        for(i = 0; i<haystack.length; i++)
        {
            if(strict)
            {
                t1 = haystack[i];
                t2 = needle;
                if(t1.equals(t2))
                {
                    return true;
                }
            }
            else
            {
                t1 = haystack[i];
                t1 = t1.toLowerCase();
                t2 = needle;
                t2 = t2.toLowerCase();
                if(t1.equals(t2))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the word is exists on the string array and ignore case
     * @param haystack String array from where the word to be search
     * @param needle Word to be search
     * @return true if word is in array. Otherwise return false
     */
    public static boolean inArray(String[] haystack, String needle)
    {
        return inArray(haystack, needle, false);
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
