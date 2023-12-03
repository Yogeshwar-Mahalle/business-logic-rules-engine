/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.ruleEngine.dslResolver.DSLKeywordResolver;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
@Service
public class DSLParser {

    @Autowired
    private DSLKeywordResolver keywordResolver;
    @Autowired
    private DSLPatternUtil dslPatternUtil;

    public String resolveDomainSpecificKeywords(String expression, Map<String, Object> inputObjects){
        if( expression == null || inputObjects == null)
            return expression;

        Map<String, Object> dslKeywordToResolverValueMap = executeDSLResolver(expression, inputObjects);
        return replaceKeywordsWithValue(expression, dslKeywordToResolverValueMap);
    }

    private Map<String, Object> executeDSLResolver(String expression, Map<String, Object> inputObjects) {
        if( expression == null )
            return null;

        List<String> listOfDslKeyword = dslPatternUtil.getListOfDslKeywords(expression);
        Map<String, Object> dslKeywordToResolverValueMap = new HashMap<>();
        listOfDslKeyword
                .forEach(
                        dslKeyword -> {
                            String extractedDslKeyword = dslPatternUtil.extractKeyword(dslKeyword);
                            String keyResolver = dslPatternUtil.getKeywordResolver(extractedDslKeyword);
                            String keywordValue = dslPatternUtil.getKeywordValue(extractedDslKeyword);
                            String keywordValueName = dslPatternUtil.getKeywordValueName(keywordValue);
                            String[] keywordValueParams = dslPatternUtil.getKeywordValueParams(keywordValue);
                            Integer index = dslPatternUtil.getKeywordValueIndex(keywordValue);
                            DSLResolver resolver = keywordResolver.getResolver(keyResolver).orElse(null);
                            assert resolver != null;
                            Object resolveValue = null;
                            if( keywordValue == null ) {
                                resolveValue = resolver.resolveValue();
                            }
                            else if( keywordValueParams == null && index == null ) {
                                resolveValue = resolver.resolveValue(keywordValueName);
                            }
                            else if( keywordValueParams == null ) {
                                resolveValue = resolver.resolveValue(keywordValueName, index);
                            }
                            else if( inputObjects != null ) {
                                String[] strParameters = new String[keywordValueParams.length];
                                int i = 0;
                                for (String parameter : keywordValueParams) {
                                    parameter = parameter.trim();
                                    String strKey = dslPatternUtil.getKeywordResolver(parameter);
                                    Object objMap = strKey == null ? null : inputObjects.get(strKey.trim());

                                    String strValue = dslPatternUtil.getKeywordValue(parameter);
                                    String valueKeyName = strValue == null ? null : dslPatternUtil.getKeywordValueName(strValue.trim());

                                    if (objMap != null) {
                                        Integer[] indices = dslPatternUtil.getKeywordValueIndices(strValue);
                                        if (objMap instanceof Map<?, ?>) {
                                            Object objValue = ((Map<?, ?>) objMap).get(valueKeyName);
                                            if(objValue == null)
                                            {
                                                ObjectMapper objectMapper = new ObjectMapper();
                                                String jsonParameter = "";
                                                try {
                                                    jsonParameter = objectMapper.writeValueAsString(objMap);
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                strParameters[i++] = jsonParameter;
                                            }
                                            else if( indices != null && (objValue instanceof ArrayList || objValue.getClass().isArray() ) )
                                            {
                                                Object [] objArray = null;

                                                // Cast to Object array from array Object
                                                if (objValue.getClass().isArray()) {
                                                    List<Object> objArrayList = new ArrayList<>();
                                                    for (int k = 0; k < Array.getLength(objValue); i++) {
                                                        objArrayList.add(Array.get(objValue, k));
                                                    }
                                                    objArray = objArrayList.toArray();
                                                }
                                                else {
                                                    objArray = (Object []) ((ArrayList<?>) objValue).toArray();
                                                }

                                                if(indices.length > 1)
                                                {
                                                    Object[] objArrayInRange = Arrays.copyOfRange(objArray,
                                                            indices[0],
                                                            indices[1] + 1);

                                                    strParameters[i++] = Arrays.toString(objArrayInRange);
                                                }
                                                else {
                                                    strParameters[i++] = objArray[indices[0]].toString();
                                                }
                                            }
                                            else
                                            {
                                                strParameters[i++] = objValue.toString();
                                            }
                                        }
                                        else if (objMap instanceof String)
                                        {
                                            strParameters[i++] = (String) objMap;
                                        }
                                        else
                                        {
                                            strParameters[i++] = objMap.toString();
                                        }
                                    } else {
                                        strParameters[i++] = parameter;
                                    }
                                }

                                if(index == null)
                                {
                                    resolveValue = resolver.resolveValue(keywordValueName, strParameters);
                                }
                                else
                                {
                                    resolveValue = resolver.resolveValue(keywordValueName, strParameters, index);
                                }
                            }
                            else {
                                if(index == null)
                                {
                                    resolveValue = resolver.resolveValue(keywordValueName, keywordValueParams);
                                }
                                else
                                {
                                    resolveValue = resolver.resolveValue(keywordValueName, keywordValueParams, index);
                                }
                            }

                            dslKeywordToResolverValueMap.put(dslKeyword, resolveValue);
                        }
                );
        return dslKeywordToResolverValueMap;
    }

    private String replaceKeywordsWithValue(String expression, Map<String, Object> dslKeywordToResolverValueMap){
        if( expression == null || dslKeywordToResolverValueMap == null)
            return expression;

        List<String> keyList = dslKeywordToResolverValueMap.keySet().stream().toList();
        for (int index = 0; index < keyList.size(); index++){
            String key = keyList.get(index);
            Object dslResolveValue = dslKeywordToResolverValueMap.get(key);

            String strResolveValue = "null";

            if( dslResolveValue != null ) {
                if (dslResolveValue instanceof Class) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        strResolveValue = objectMapper.writeValueAsString(dslResolveValue);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    strResolveValue = dslResolveValue.toString();
                }
            }

            expression = expression.replace(key, strResolveValue);
        }
        return expression;
    }
}
