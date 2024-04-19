/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.models;

import com.ybm.dataMapping.interfaces.ISO8583FieldInfo;

import java.lang.reflect.Field;

public class ISO8583Field {
    /**
     * Field type
     */
    public ISO8583FieldInfo.FormatType type = ISO8583FieldInfo.FormatType.UNKNOWN;
    /**
     * Field format
     */
    public String format = "%s";
    /**
     * Field data
     */
    public String data = "";
    /**
     * Field length
     */
    public int length = 0;
    /**
     * Field flag whether the data is set or not.
     */
    public boolean isSet = false;
    /**
     * Default constructor
     */
    public ISO8583Field()
    {
    }
    /**
     * Get field
     * @return JSONObject contains ISO 8583 field data and its attributes
     */
    public ISO8583Field getField()
    {
        return this;
    }
    /**
     * Set field
     * @param field JSONObject contains ISO 8583 field data and its attributes
     * @return RoyISO8583Field
     */
    public ISO8583Field setField(ISO8583Field field)
    {
        this.isSet = true;
        this.type = field.type;
        this.length = field.length;
        this.data = field.data;
        return this;
    }
    public ISO8583Field setField(String data, ISO8583FieldInfo.FormatType type, int length)
    {
        this.isSet = true;
        this.type = type;
        this.length = length;
        this.data = data;
        return this;
    }
    /**
     * Overrides <strong>toString</strong> method to convert object to JSON String. This method is useful to debug or show value of each properties of the object.
     */
    public String toString()
    {
        Field[] fields = this.getClass().getDeclaredFields();
        int i, max = fields.length;
        String fieldName = "";
        String fieldType = "";
        String ret = "";
        String value = "";
        boolean skip = false;
        int j = 0;
        for(i = 0; i < max; i++)
        {
            fieldName = fields[i].getName();
            fieldType = fields[i].getType().toString();
            if(i == 0)
            {
                ret += "{";
            }
            if(fieldType.equals("int") || fieldType.equals("long") || fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("boolean"))
            {
                try
                {
                    value = fields[i].get(this).toString();
                }
                catch (Exception e)
                {
                    value = "0";
                }
                skip = false;
            }
            else if(fieldType.contains("String"))
            {
                try
                {
                    value = "\""+ escapeJSON((String) fields[i].get(this))+"\"";
                }
                catch (Exception e)
                {
                    value = "\""+"\"";
                }
                skip = false;
            }
            else
            {
                value = "\""+"\"";
                skip = true;
            }
            if(!skip)
            {
                if(j > 0)
                {
                    ret += ",";
                }
                j++;
                ret += "\r\n\t\""+fieldName+"\":"+value;
            }
            if(i == max-1)
            {
                ret += "\r\n}";
            }
        }
        return ret;
    }

    public static String escapeJSON(String input) {
        String output = "";
        if (input != null) {
            output = input.replaceAll("\"", "\\\"");
            output = output.replaceAll("/", "\\/");
        }
        return output;
    }

}
