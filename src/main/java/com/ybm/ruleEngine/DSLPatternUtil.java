/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DSLPatternUtil {
    //${dsr.keyword(parameter1, ...)} or ${dsr.keyword[index]} or ${dsr.keyword} or ${dsr}
    private static final String expr = "\\$\\{((\\w+\\.\\w+\\(([.,_a-zA-Z0-9\\s\\[\\]]+)\\)([0-9\\s\\[\\]]*))|(\\w+\\.\\w+\\[([0-9\\s]+)\\])|(\\w+\\.\\w+)|(\\w+))\\}";
    private static final Pattern DSL_PATTERN = Pattern.compile(expr);
    private static final String DOT = ".";
    private static final String COMMA = ",";

    public List<String> getListOfDslKeywords(String expression) {
        Matcher matcher = DSL_PATTERN.matcher(expression);
        List<String> listOfDslKeyword = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            listOfDslKeyword.add(group);
        }
        return listOfDslKeyword;
    }

    public String extractKeyword(String keyword) {

        if ( keyword != null && keyword.indexOf('{') != -1 )
        {
            return keyword.substring(keyword.indexOf('{') + 1,  keyword.indexOf('}'));
        }

        return null;
    }

    public String extractParameter(String value) {

        if ( value != null && value.indexOf('(') != -1 )
        {
            return value.substring(value.indexOf('(') + 1, value.indexOf(')'));
        }

        return null;
    }

    public Integer[] extractIndices(String value) {

        if(value == null)
            return null;

        Integer[] indexRange = null;
        if ( value.indexOf('[') != -1 )
        {
            String strIndex = value.substring(value.indexOf('[') + 1, value.indexOf(']'));
            if (strIndex.contains(".."))
            {
                String strStartIndex = strIndex.substring(0, strIndex.indexOf('.'));
                String strEndIndex = strIndex.substring(strIndex.lastIndexOf('.') + 1);

                indexRange = new Integer[]{Integer.parseInt(strStartIndex),Integer.parseInt(strEndIndex)};
            }
            else
            {
                indexRange = new Integer[]{Integer.parseInt(strIndex)};
            }
        }

        return indexRange;
    }

    public String extractKeyName(String value) {

        if ( value != null && value.indexOf('(') != -1 )
        {
            return value.substring(0, value.indexOf('('));
        }
        else if ( value != null && value.indexOf('[') != -1 )
        {
            return value.substring(0, value.indexOf('['));
        }

        return value;
    }

    public String getKeywordResolver(String dslKeyword){
        if ( dslKeyword == null )
            return null;

        ArrayList<String> keywords = Lists.newArrayList(Splitter.on(DOT).omitEmptyStrings().split(dslKeyword));
        return keywords.get(0);
    }

    public String getKeywordValue(String dslKeyword){
//        ArrayList<String> values = Lists.newArrayList(Splitter.on(DOT).omitEmptyStrings().split(dslKeyword));
//        return values.get(1);
        if(dslKeyword == null || !dslKeyword.contains(DOT))
        {
            return null;
        }

        return dslKeyword.substring(dslKeyword.indexOf(DOT) + 1);
    }

    public String getKeywordValueName(String dslKeywordValue){
        return extractKeyName(dslKeywordValue);
    }

    public String[] getKeywordValueParams(String dslKeywordValue){
        String strParameters = extractParameter(dslKeywordValue);
        if( strParameters == null )
        {
            return null;
        }

        ArrayList<String> parameters = Lists.newArrayList(Splitter.on(COMMA).omitEmptyStrings().split(strParameters));
        return parameters.toArray(new String[0]);
    }

    public Integer getKeywordValueIndex(String dslKeywordValue){
        if( dslKeywordValue == null )
            return null;

        String strIndexValue = dslKeywordValue;
        if ( dslKeywordValue.indexOf('(') != -1 )
        {
            strIndexValue = dslKeywordValue.substring(dslKeywordValue.lastIndexOf(')'));

            if(strIndexValue.lastIndexOf('[') != -1)
            {
                strIndexValue = strIndexValue.substring(strIndexValue.lastIndexOf('['));
                return extractIndices(strIndexValue)[0];
            }
        }

        return null;
    }

    public Integer[] getKeywordValueIndices(String dslKeywordValue){
        return extractIndices(dslKeywordValue);
    }

}
