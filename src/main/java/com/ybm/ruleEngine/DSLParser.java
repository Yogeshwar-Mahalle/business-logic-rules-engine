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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DSLParser {

    @Autowired
    private DSLKeywordResolver keywordResolver;
    @Autowired
    private DSLPatternUtil dslPatternUtil;

    public String resolveDomainSpecificKeywords(String expression, Map<String, Object> inputObjects){
        Map<String, Object> dslKeywordToResolverValueMap = executeDSLResolver(expression, inputObjects);
        return replaceKeywordsWithValue(expression, dslKeywordToResolverValueMap);
    }

    private Map<String, Object> executeDSLResolver(String expression, Map<String, Object> inputObjects) {
        List<String> listOfDslKeyword = dslPatternUtil.getListOfDslKeywords(expression);
        Map<String, Object> dslKeywordToResolverValueMap = new HashMap<>();
        listOfDslKeyword
                .forEach(
                        dslKeyword -> {
                            String extractedDslKeyword = dslPatternUtil.extractKeyword(dslKeyword);
                            String keyResolver = dslPatternUtil.getKeywordResolver(extractedDslKeyword);
                            String keywordValue = dslPatternUtil.getKeywordValue(extractedDslKeyword);
                            String keywordValueName = dslPatternUtil.getKeywordValueName(keywordValue);
                            String keywordValueParam = dslPatternUtil.getKeywordValueParam(keywordValue);
                            DSLResolver resolver = keywordResolver.getResolver(keyResolver).orElse(null);
                            assert resolver != null;
                            Object resolveValue = null;
                            if( keywordValueParam == null ) {
                                resolveValue = resolver.resolveValue(keywordValueName);
                            }
                            else {
                                String strKey = dslPatternUtil.getKeywordResolver(keywordValueParam);
                                Object objMap = inputObjects.get(strKey);

                                String strValue = dslPatternUtil.getKeywordValue(keywordValueParam);

                                String strParameter = "";
                                if( objMap != null )
                                {
                                    if( objMap instanceof Map<?,?> )
                                    {
                                        Object objValue = ((Map<?, ?>) objMap).get(strValue);
                                        strParameter = objValue != null ? objValue.toString() : keywordValueParam;
                                    }
                                }
                                else
                                {
                                    strParameter = keywordValueParam;
                                }
                                resolveValue = resolver.resolveValueByParameter(keywordValueName, strParameter);
                            }

                            dslKeywordToResolverValueMap.put(dslKeyword, resolveValue);
                        }
                );
        return dslKeywordToResolverValueMap;
    }

    private String replaceKeywordsWithValue(String expression, Map<String, Object> dslKeywordToResolverValueMap){
        List<String> keyList = dslKeywordToResolverValueMap.keySet().stream().toList();
        for (int index = 0; index < keyList.size(); index++){
            String key = keyList.get(index);
            Object dslResolveValue = dslKeywordToResolverValueMap.get(key);

            String strResolveValue = "null";

            if( dslResolveValue instanceof String ) {
                strResolveValue = "\"" + dslResolveValue.toString() + "\"";
            }
            else if( dslResolveValue instanceof Date) {
                strResolveValue = "\"" + dslResolveValue.toString() + "\"";
            }
            else if( dslResolveValue != null ) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    strResolveValue = objectMapper.writeValueAsString(dslResolveValue);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            expression = expression.replace(key, strResolveValue);
        }
        return expression;
    }
}
