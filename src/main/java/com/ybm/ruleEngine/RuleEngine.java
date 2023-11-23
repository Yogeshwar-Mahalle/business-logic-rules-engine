/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleFunctionTemplateService;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleTypesService;
import com.ybm.rulesBusinessSetupRepo.BusinessRulesService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunctionTemplate;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
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
    private BusinessRuleTypesService businessRuleTypesService;
    @Autowired
    private BusinessRulesService businessRulesService;

    @Autowired
    private BusinessRuleFunctionTemplateService businessRuleFunctionTemplateService;

    private final String INPUT_MESSAGE = "inMessage";
    private final String INPUT_PAYLOAD = "inPayload";
    private final String INPUT_HEADERS = "inHeaders";
    private final String INPUT_PROPERTIES = "inProperties";
    private final String PROCESSING_PAYLOAD = "payload";
    private final String OUTPUT_PAYLOAD = "outPayload";
    private final String OUTPUT_HEADERS = "outHeaders";
    private final String OUTPUT_PROPERTIES = "outProperties";
    private final String INPUT_EXTENSION_DATA = "inExtData";
    private final String OUTPUT_EXTENSION_DATA = "outExtData";

    private final String CUSTOM_FIELDS = "customFields";

    /**
     * Run BusinessLogicRule engine on set of rules for given data.
     * @param ruleType
     * @param dataExchangeObject
     * @return
     */
    public DataExchangeObject run(String ruleType, DataExchangeObject dataExchangeObject) {
        //TODO: Here for each call, we are fetching all rules from dbRepository. It should be cache.
        BusinessLogicRuleType businessLogicRuleTypes = businessRuleTypesService.getRuleType(ruleType);
        if (businessLogicRuleTypes == null){
            return dataExchangeObject;
        }

        List<BusinessLogicRule> listOfBusinessLogicRules = businessRulesService.getAllRulesByType(ruleType);
        if (null == listOfBusinessLogicRules || listOfBusinessLogicRules.isEmpty()){
            return dataExchangeObject;
        }

        //STEP 1 (MATCH) : Match the facts and data against the set of rules.
        List<BusinessLogicRule> matchedRulesSet = match(listOfBusinessLogicRules, dataExchangeObject);

        //STEP 2 (SORT) : Sort the matched rules based on priority
        List<BusinessLogicRule> matchedSortedRulesSet = sortMatchedRulesSet(matchedRulesSet);

        if( !businessLogicRuleTypes.isApplyAllFlag() ) {
            //STEP 3 (FIND FIRST) : Apply only first matched rule using priority from the selected rules.
            BusinessLogicRule resolvedBusinessLogicRule = resolve(matchedSortedRulesSet);
            if (null == resolvedBusinessLogicRule) {
                return dataExchangeObject;
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
        Map<String, Object> dataMap = dataExchangeObject.getOutDataObject().getPayload().getDataMap();
        String strPayload = dataMap.toString();
        try {
            strPayload = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        dataExchangeObject.getOutDataObject().getPayload().setStrMessage(strPayload);

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
                            return evaluateCondition(businessLogicRule, inputData);
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
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Evaluate MVEL expression.
     *
     * @param template
     * @param inputData
     */
    public String evaluateTemplateFunction(String template, DataExchangeObject inputData) {

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(INPUT_MESSAGE, inputData.getInDataObject().getPayload().getStrMessage());
        dataMap.put(INPUT_PAYLOAD, inputData.getInDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_HEADERS, inputData.getOutDataObject().getHeaders());
        dataMap.put(INPUT_PROPERTIES, inputData.getProperties());
        dataMap.put(PROCESSING_PAYLOAD, inputData.getOutDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_EXTENSION_DATA, inputData.getExtData());

        //Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(template, dataMap);

        //Step 2. Evaluate MVEL template.
        return mvelInterpreter.evaluateMvelTemplate(resolvedDslExpression, dataMap);
    }

    /**
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Evaluate MVEL expression.
     *
     * @param templateId
     * @param template
     */
    public void compileTemplateFunction(String templateId, String template) {
        mvelInterpreter.compileMvelTemplate(templateId, template);
    }

    /*
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Evaluate MVEL expression.
     *
     * @param templateName
     * @param inputData
     */
    public String execPreCompiledTemplateFunction(String templateId, Map<String, Object> dataMap) {
        if ( templateId == null )
            return null;

        //Step 1. Check if MVEL precompiled template exist in memory map, else compile and store it.
        boolean bPreCompileFuncTemplateExist = mvelInterpreter.isPreCompiledFuncTemplateExist(templateId);
        if( !bPreCompileFuncTemplateExist )
        {
            BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(templateId);
            if( businessLogicRuleFunctionTemplate != null )
                compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
        }

        //Step 2. Execute MVEL precompiled template.
        return mvelInterpreter.execPreCompiledMvelTemplate(templateId, dataMap);
    }

    /**
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Evaluate MVEL expression.
     *
     * @param businessLogicRule
     * @param inputData
     */
    public boolean evaluateCondition(BusinessLogicRule businessLogicRule, DataExchangeObject inputData) {

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(INPUT_MESSAGE, inputData.getInDataObject().getPayload().getStrMessage());
        dataMap.put(INPUT_PAYLOAD, inputData.getInDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_HEADERS, inputData.getOutDataObject().getHeaders());
        dataMap.put(INPUT_PROPERTIES, inputData.getProperties());
        dataMap.put(PROCESSING_PAYLOAD, inputData.getOutDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_EXTENSION_DATA, inputData.getExtData());

        //Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(businessLogicRule.getCondition(), dataMap);

        //Step 2. Execute precompiled MVEL template for initialization.
        String initializationExpression = execPreCompiledTemplateFunction(businessLogicRule.getCondInitTemplate(), dataMap);

        //Step 3. Prefix Initialization expression to condition expression
        String finalExpression = null;
        if( initializationExpression != null )
        {
            finalExpression = initializationExpression;
        }

        if( resolvedDslExpression != null )
        {
            finalExpression = finalExpression == null ? resolvedDslExpression : finalExpression + resolvedDslExpression;
        }

        //Step 4. Evaluate MVEL expression.
        return mvelInterpreter.evaluateMvelExpression(finalExpression, dataMap);
    }

    /**
     * Execute selected businessLogicRule on input data.
     *
     * Step 1. Resolve domain specific keywords: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Evaluate MVEL expression.
     *
     * @param businessLogicRule
     * @param inputData
     * @return
     */
    public DataExchangeObject executeRule(BusinessLogicRule businessLogicRule, DataExchangeObject inputData) {

        DataExchangeObject outputResult = inputData.copy();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(INPUT_MESSAGE, inputData.getInDataObject().getPayload().getStrMessage());
        dataMap.put(INPUT_PAYLOAD, inputData.getInDataObject().getPayload().getDataMap());
        dataMap.put(INPUT_HEADERS, inputData.getOutDataObject().getHeaders());
        dataMap.put(INPUT_PROPERTIES, inputData.getProperties());
        dataMap.put(INPUT_EXTENSION_DATA, inputData.getExtData());
        dataMap.put(PROCESSING_PAYLOAD, outputResult.getOutDataObject().getPayload().getDataMap());
        dataMap.put(OUTPUT_PAYLOAD, outputResult.getOutDataObject().getPayload().getDataMap());
        dataMap.put(OUTPUT_HEADERS, outputResult.getOutDataObject().getHeaders());
        dataMap.put(OUTPUT_PROPERTIES, outputResult.getProperties());
        dataMap.put(OUTPUT_EXTENSION_DATA, outputResult.getExtData());

        //Step 1. Resolve domain specific keywords: ${domainName.keyword} or ${domainName.keyword(parameter)}
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(businessLogicRule.getAction(), dataMap);

        //Step 2. Execute precompiled MVEL template for initialization.
        String initializationExpression = execPreCompiledTemplateFunction(businessLogicRule.getActionInitTemplate(), dataMap);

        //Step 3. Prefix Initialization expression to condition expression
        String finalExpression = null;
        if( initializationExpression != null )
        {
            finalExpression = initializationExpression;
        }

        if( resolvedDslExpression != null )
        {
            finalExpression = finalExpression == null ? resolvedDslExpression : finalExpression + resolvedDslExpression;
        }

        //Step 4. Apply MVEL expression action.
        mvelInterpreter.applyMvelExpressionAction(finalExpression, dataMap);

        //Step 5. Execute final precompiled MVEL template for extension.
        String finalExtensionData = execPreCompiledTemplateFunction(businessLogicRule.getActionFinalTemplate(), dataMap);

        Map<String, Object> customFieldsMap = new HashMap<>();
        customFieldsMap.put("fullNameUpperCase", finalExtensionData);

        //Step 6. Prefix Initialization expression to condition expression
        if( finalExtensionData != null )
            outputResult.getExtData().put(CUSTOM_FIELDS, customFieldsMap);

        return outputResult;
    }

}
