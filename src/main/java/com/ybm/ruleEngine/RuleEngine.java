/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.rulesBusinessSetupRepo.BusinessRulesService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleEngine {

    @Autowired
    protected DSLParser dslParser;
    @Autowired
    protected MVELInterpreter mvelInterpreter;
    @Autowired
    private BusinessRulesService businessRulesService;

    private final String INPUT_ORG_MESSAGE = "inOrgMessage";
    private final String INPUT_ORG_PAYLOAD = "inOrgPayload";
    private final String INPUT_PAYLOAD = "inPayload";
    private final String INPUT_HEADERS = "inHeaders";
    private final String INPUT_PROPERTIES = "inProperties";
    private final String OUTPUT_PAYLOAD = "outPayload";
    private final String OUTPUT_HEADERS = "outHeaders";
    private final String OUTPUT_PROPERTIES = "outProperties";

    /**
     * Run BusinessLogicRule engine on set of rules for given data.
     * @param ruleType
     * @param dataExchangeObject
     * @param applyOnlyFirstMatch
     * @return
     */
    public DataExchangeObject run(String ruleType, DataExchangeObject dataExchangeObject, boolean applyOnlyFirstMatch) {
        //TODO: Here for each call, we are fetching all rules from dbRepository. It should be cache.
        List<BusinessLogicRule> listOfBusinessLogicRules = businessRulesService.getAllRulesByType(ruleType);

        if (null == listOfBusinessLogicRules || listOfBusinessLogicRules.isEmpty()){
            return null;
        }

        //STEP 1 (MATCH) : Match the facts and data against the set of rules.
        List<BusinessLogicRule> matchedRulesSet = match(listOfBusinessLogicRules, dataExchangeObject);

        //STEP 2 (SORT) : Sort the matched rules based on priority
        List<BusinessLogicRule> matchedSortedRulesSet = sortMatchedRulesSet(matchedRulesSet);

        if( applyOnlyFirstMatch ) {
            //STEP 3 (FIND FIRST) : Apply only first matched rule using priority from the selected rules.
            BusinessLogicRule resolvedBusinessLogicRule = resolve(matchedSortedRulesSet);
            if (null == resolvedBusinessLogicRule) {
                return null;
            }

            //STEP 4 (EXECUTE) : Run the action of the selected rule on given data and return the output.
            dataExchangeObject = executeRule(resolvedBusinessLogicRule, dataExchangeObject);
        }
        else {

            //STEP 3 (APPLY ALL) : Apply all matched rules sorted on priority from the selected rules.
            for (BusinessLogicRule businessLogicRule : matchedSortedRulesSet ) {

                //STEP 4 (EXECUTE) : Run the action of the selected businessLogicRule on given data and return the output.
                dataExchangeObject = executeRule(businessLogicRule, dataExchangeObject);

            }

        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataMap = dataExchangeObject.getDataObject().getPayload().getDataMap();
        String strPayload = dataMap.toString();
        try {
            strPayload = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        dataExchangeObject.getDataObject().getPayload().setStrMessage(strPayload);

        return dataExchangeObject;
    }

    protected List<BusinessLogicRule> sortMatchedRulesSet(List<BusinessLogicRule> matchedRulesSet){

        return matchedRulesSet.stream()
                .sorted(Comparator.comparingInt(BusinessLogicRule::getPriority))
                .toList();
    }

    /**
     *We can use here any pattern matching algo:
     * 1. Rete
     * 2. Linear
     * 3. Treat
     * 4. Leaps
     *
     * Here we are using Linear matching algorithm for pattern matching.
     * @param listOfBusinessLogicRules
     * @param inputData
     * @return
     */
    protected List<BusinessLogicRule> match(List<BusinessLogicRule> listOfBusinessLogicRules, DataExchangeObject inputData){
        return listOfBusinessLogicRules.stream()
                .filter(
                        businessLogicRule -> {
                            String condition = businessLogicRule.getCondition();
                            return evaluateCondition(condition, inputData);
                        }
                )
                .collect(Collectors.toList());
    }

    /**
     * We can use here any resolving techniques:
     * 1. Lex
     * 2. Recency
     * 3. MEA
     * 4. Refactor
     * 5. Priority wise
     *
     *  Here we are using find first priority rule logic.
     * @param matchedSortedRulesSet
     * @return
     */
    protected BusinessLogicRule resolve(List<BusinessLogicRule> matchedSortedRulesSet){
        Optional<BusinessLogicRule> rule = matchedSortedRulesSet.stream()
                .findFirst();
        return rule.orElse(null);
    }

    /**
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: $(domainName.keyword)
     * Step 2. Evaluate MVEL expression.
     *
     * @param expression
     * @param inputData
     */
    public boolean evaluateCondition(String expression, DataExchangeObject inputData) {

        //Step 1. Resolve domain specific keywords first: $(domainName.keyword)
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(expression);

        //Step 2. Evaluate MVEL expression.
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(INPUT_ORG_MESSAGE, inputData.getOriginalDataObject().getPayload().getStrMessage());
        dataMap.put(INPUT_ORG_PAYLOAD, inputData.getOriginalDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_PAYLOAD, inputData.getDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_HEADERS, inputData.getDataObject().getHeaders());
        dataMap.put(INPUT_PROPERTIES, inputData.getProperties());
        return mvelInterpreter.evaluateMvelExpression(resolvedDslExpression, dataMap);
    }

    /**
     * Execute selected businessLogicRule on input data.
     *
     * Step 1. Resolve domain specific keywords: $(domainName.keyword)
     * Step 2. Evaluate MVEL expression.
     *
     * @param businessLogicRule
     * @param inputData
     * @return
     */
    public DataExchangeObject executeRule(BusinessLogicRule businessLogicRule, DataExchangeObject inputData) {

        DataExchangeObject outputResult = inputData.copy();
        outputResult.getDataObject().getPayload().getDataMap().clear();
        outputResult.getDataObject().getHeaders().clear();
        outputResult.getProperties().clear();

        //Step 1. Resolve domain specific keywords: $(domainName.keyword)
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(businessLogicRule.getAction());

        //Step 2. Evaluate MVEL expression.
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(INPUT_ORG_MESSAGE, inputData.getOriginalDataObject().getPayload().getStrMessage());
        dataMap.put(INPUT_ORG_PAYLOAD, inputData.getOriginalDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_PAYLOAD, inputData.getDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_HEADERS, inputData.getDataObject().getHeaders());
        dataMap.put(INPUT_PROPERTIES, inputData.getProperties());
        dataMap.put(OUTPUT_PAYLOAD, outputResult.getDataObject().getPayload().getDataMap());
        dataMap.put(OUTPUT_HEADERS, outputResult.getDataObject().getHeaders());
        dataMap.put(OUTPUT_PROPERTIES, outputResult.getProperties());
        mvelInterpreter.deriveMvelExpressionAction(resolvedDslExpression, dataMap);

        return outputResult;
    }

}
