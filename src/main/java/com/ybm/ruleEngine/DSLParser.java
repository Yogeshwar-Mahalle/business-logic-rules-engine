/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.ybm.ruleEngine.dslResolver.DSLKeywordResolver;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String resolveDomainSpecificKeywords(String expression){
        Map<String, Object> dslKeywordToResolverValueMap = executeDSLResolver(expression);
        return replaceKeywordsWithValue(expression, dslKeywordToResolverValueMap);
    }

    private Map<String, Object> executeDSLResolver(String expression) {
        List<String> listOfDslKeyword = dslPatternUtil.getListOfDslKeywords(expression);
        Map<String, Object> dslKeywordToResolverValueMap = new HashMap<>();
        listOfDslKeyword
                .forEach(
                        dslKeyword -> {
                            String extractedDslKeyword = dslPatternUtil.extractKeyword(dslKeyword);
                            String keyResolver = dslPatternUtil.getKeywordResolver(extractedDslKeyword);
                            String keywordValue = dslPatternUtil.getKeywordValue(extractedDslKeyword);
                            DSLResolver resolver = keywordResolver.getResolver(keyResolver).get();
                            Object resolveValue = resolver.resolveValue(keywordValue);
                            dslKeywordToResolverValueMap.put(dslKeyword, resolveValue);
                        }
                );
        return dslKeywordToResolverValueMap;
    }

    private String replaceKeywordsWithValue(String expression, Map<String, Object> dslKeywordToResolverValueMap){
        List<String> keyList = dslKeywordToResolverValueMap.keySet().stream().toList();
        for (int index = 0; index < keyList.size(); index++){
            String key = keyList.get(index);
            String dslResolveValue = dslKeywordToResolverValueMap.get(key).toString();
            expression = expression.replace(key, dslResolveValue);
        }
        return expression;
    }
}
