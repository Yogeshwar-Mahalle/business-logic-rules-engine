/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.ruleEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.RuleLogsService;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.models.RuleLogs;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleFunctionTemplateService;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleTypesService;
import com.ybm.rulesBusinessSetupRepo.BusinessRulesService;
import com.ybm.rulesBusinessSetupRepo.models.ExchangeObjectType;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunctionTemplate;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RuleEngine {
    private static final Logger LOG = LoggerFactory.getLogger(RuleEngine.class);
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

    @Autowired
    private RuleLogsService ruleLogsService;

    /**
     * Run BusinessLogicRule engine on set of rules for given data.
     * @param ruleType
     * @param dataExchangeObject
     * @return
     */
    public DataExchangeObject run(String entity, String ruleType, DataExchangeObject dataExchangeObject) {
        //TODO: Here for each call, we are fetching all rules from dbRepository. It should be cache.
        BusinessLogicRuleType businessLogicRuleTypes = businessRuleTypesService.getRuleTypeByEntityAndRuleType(entity, ruleType);
        if (businessLogicRuleTypes == null){
            return dataExchangeObject;
        }

        List<BusinessLogicRule> listOfBusinessLogicRules = businessRulesService.getAllRulesByEntityAndType(entity, ruleType);
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

            //Step 5 (Rule Log) : Save the action and data change in the rule log
            RuleLogs RuleLogs = new RuleLogs(
                    dataExchangeObject.getUniqueExchangeId(),
                    resolvedBusinessLogicRule.getRuleId(),
                    dataExchangeObject.getInDataObject().getPayload().toString(),
                    dataExchangeObject.getOutDataObject().getPayload().toString(),
                    dataExchangeObject.getInDataObject().getHeaders().toString(),
                    dataExchangeObject.getOutDataObject().getHeaders().toString(),
                    dataExchangeObject.getProperties().toString(),
                    dataExchangeObject.getDataExtension().toString(),
                    new Date()
            );
            dataExchangeObject.getRuleLogsList().add( ruleLogsService.saveRuleLogs(RuleLogs) );
        }
        else {

            //STEP 3 (APPLY ALL) : Apply all matched rules sorted on priority from the selected rules.
            for (BusinessLogicRule businessLogicRule : matchedSortedRulesSet ) {

                //STEP 4 (EXECUTE) : Run the action of the selected businessLogicRule on given data and return the output.
                dataExchangeObject = executeRule(businessLogicRule, dataExchangeObject);

                //Step 5 (Rule Log) : Save the action and data change in the rule log
                RuleLogs RuleLogs = new RuleLogs(
                        dataExchangeObject.getUniqueExchangeId(),
                        businessLogicRule.getRuleId(),
                        dataExchangeObject.getInDataObject().getPayload().toString(),
                        dataExchangeObject.getOutDataObject().getPayload().toString(),
                        dataExchangeObject.getInDataObject().getHeaders().toString(),
                        dataExchangeObject.getOutDataObject().getHeaders().toString(),
                        dataExchangeObject.getProperties().toString(),
                        dataExchangeObject.getDataExtension().toString(),
                        new Date()
                );
                dataExchangeObject.getRuleLogsList().add( ruleLogsService.saveRuleLogs(RuleLogs) );
            }

        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataMap = dataExchangeObject.getOutDataObject().getPayload().getDataMap();
        if(!dataMap.isEmpty()) {
            String strPayload = dataMap.toString();
            try {
                strPayload = objectMapper.writeValueAsString(dataMap);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            dataExchangeObject.getOutDataObject().getPayload().setContentType(ContentType.JSON);
            dataExchangeObject.getOutDataObject().getPayload().setStrMessage(strPayload);
            //dataExchangeObject.getOutDataObject().getHeaders().put("content-type", ContentType.JSON.label);
        }

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
     * @param templateId
     * @param dataMap
     */
    public String evaluateTemplateFunction(String templateId, LinkedHashMap<String, Object> dataMap) {

        BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(templateId);
        if( businessLogicRuleFunctionTemplate != null ) {
            //Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
            String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(businessLogicRuleFunctionTemplate.getFunctionLogic(), dataMap);

            //Step 2. Evaluate MVEL template.
            return mvelInterpreter.evaluateMvelTemplate(resolvedDslExpression, dataMap);
        }

        return null;
    }

    /**
     * Compile MVEL Template.
     *
     * @param templateId
     * @param template
     */
    public void compileTemplateFunction(String templateId, String template) {
        mvelInterpreter.compileMvelTemplate(templateId, template);
    }

    /*
     * Execute precompiled Template.
     *
     * Step 1. Check if precompiled template already exist in the memory map
     * Step 2. Execute precompiled MVEL template.
     *
     * @param templateName
     * @param inputData
     */
    public String execPreCompiledTemplateFunction(String templateId, LinkedHashMap<String, Object> dataMap) {
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

    private StringBuilder getIncludedFunctionChain(String includedFunctionList)
    {
        StringBuilder includedFuntions = new StringBuilder();

        if ( includedFunctionList != null ) {
            List<String> functionIdList = Stream.of(includedFunctionList.split(","))
                    .map(String::trim)
                    .toList();

            for (String functionId : functionIdList) {

                BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate =
                        businessRuleFunctionTemplateService.getRuleFunction(functionId);

                if( businessLogicRuleFunctionTemplate != null ) {
                    
                    if (businessLogicRuleFunctionTemplate.getIncludeFunctionsNameList() != null )
                    {
                        includedFuntions.append(getIncludedFunctionChain(businessLogicRuleFunctionTemplate.getIncludeFunctionsNameList()));
                    }
                    
                    includedFuntions.append(businessLogicRuleFunctionTemplate.getFunctionName());
                    includedFuntions.append("(");
                    if ( businessLogicRuleFunctionTemplate.getFunctionParameters() != null )
                    {
                        includedFuntions.append(businessLogicRuleFunctionTemplate.getFunctionParameters());
                    }
                    includedFuntions.append(")");
                    includedFuntions.append("{");
                    if ( businessLogicRuleFunctionTemplate.getFunctionLogic() != null )
                    {
                        includedFuntions.append(businessLogicRuleFunctionTemplate.getFunctionLogic());
                    }
                    includedFuntions.append("}");
                }
            }
        }

        return includedFuntions;
    }

    private String getConditionExpressionWithFunction(BusinessLogicRule businessLogicRule)
    {
        StringBuilder resultConditionExpression = getIncludedFunctionChain(businessLogicRule.getCondInclFuncNameList());
        resultConditionExpression.append(businessLogicRule.getCondition());
        return resultConditionExpression.toString();
    }

    private String getActionExpressionWithFunction(BusinessLogicRule businessLogicRule)
    {
        StringBuilder resultActionExpression = getIncludedFunctionChain(businessLogicRule.getActionInclFuncNameList());
        resultActionExpression.append(businessLogicRule.getAction());
        return resultActionExpression.toString();
    }

    /**
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Execute precompiled MVEL template initialization.
     * Step 3. Prefix MVEL template initialization to condition MVEL expression.
     * Step 4. Evaluate MVEL expression.
     *
     * @param businessLogicRule
     * @param inputData
     */
    public boolean evaluateCondition(BusinessLogicRule businessLogicRule, DataExchangeObject inputData) {

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put(ExchangeObjectType.INPUT_EXCHANGE_ID.getLabel(), inputData.getUniqueExchangeId());
        dataMap.put(ExchangeObjectType.INPUT_MESSAGE.getLabel(), inputData.getInDataObject().getPayload().getStrMessage());
        dataMap.put(ExchangeObjectType.INPUT_PAYLOAD.getLabel(), inputData.getInDataObject().getPayload().getDataMap());
        dataMap.put(ExchangeObjectType.INPUT_HEADERS.getLabel(), inputData.getOutDataObject().getHeaders());
        dataMap.put(ExchangeObjectType.INPUT_PROPERTIES.getLabel(), inputData.getProperties());
        dataMap.put(ExchangeObjectType.PROCESSING_PAYLOAD.getLabel(), inputData.getOutDataObject().getPayload().getDataMap());
        dataMap.put(ExchangeObjectType.INPUT_DATA_EXTENSION.getLabel(), inputData.getDataExtension());

        //Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
        String mvelConditionExpression = getConditionExpressionWithFunction(businessLogicRule);
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(mvelConditionExpression, dataMap);

        //Step 2. Execute precompiled MVEL template for initialization.
        //String initializationExpression = execPreCompiledTemplateFunction(businessLogicRule.getCondInitTemplate(), dataMap);
        String initializationExpression = evaluateTemplateFunction(businessLogicRule.getCondInitTemplate(), dataMap);

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
     * Step 1. Resolve domain specific keywords first: ${domainName.keyword} or ${domainName.keyword(parameter)}
     * Step 2. Execute precompiled MVEL template initialization.
     * Step 3. Prefix MVEL template initialization to condition MVEL expression.
     * Step 4. Apply MVEL action expression.
     * Step 5. Execute final precompiled MVEL template.
     * Step 6. Add result of final template result in to extension data block.
     *
     * @param businessLogicRule
     * @param inputData
     * @return
     */
    public DataExchangeObject executeRule(BusinessLogicRule businessLogicRule, DataExchangeObject inputData) {

        System.out.println("Selected and applied rule : " + businessLogicRule.getRuleId());
        DataExchangeObject outputResult = inputData.copy();

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put(ExchangeObjectType.INPUT_EXCHANGE_ID.getLabel(), inputData.getUniqueExchangeId());
        dataMap.put(ExchangeObjectType.INPUT_MESSAGE.getLabel(), inputData.getInDataObject().getPayload().getStrMessage());
        dataMap.put(ExchangeObjectType.INPUT_PAYLOAD.getLabel(), inputData.getInDataObject().getPayload().getDataMap());
        dataMap.put(ExchangeObjectType.INPUT_HEADERS.getLabel(), inputData.getOutDataObject().getHeaders());
        dataMap.put(ExchangeObjectType.INPUT_PROPERTIES.getLabel(), inputData.getProperties());
        dataMap.put(ExchangeObjectType.INPUT_DATA_EXTENSION.getLabel(), inputData.getDataExtension());
        dataMap.put(ExchangeObjectType.PROCESSING_PAYLOAD.getLabel(), outputResult.getOutDataObject().getPayload().getDataMap());
        dataMap.put(ExchangeObjectType.OUTPUT_PAYLOAD.getLabel(), outputResult.getOutDataObject().getPayload().getDataMap());
        dataMap.put(ExchangeObjectType.OUTPUT_HEADERS.getLabel(), outputResult.getOutDataObject().getHeaders());
        dataMap.put(ExchangeObjectType.OUTPUT_PROPERTIES.getLabel(), outputResult.getProperties());
        dataMap.put(ExchangeObjectType.OUTPUT_DATA_EXTENSION.getLabel(), outputResult.getDataExtension());

        //Step 1. Resolve domain specific keywords: ${domainName.keyword} or ${domainName.keyword(parameter)}
        String mvelActionExpression = getActionExpressionWithFunction(businessLogicRule);
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(mvelActionExpression, dataMap);

        //Step 2. Execute precompiled MVEL template for initialization.
        //String initializationExpression = execPreCompiledTemplateFunction(businessLogicRule.getActionInitTemplate(), dataMap);
        String initializationExpression = evaluateTemplateFunction(businessLogicRule.getActionInitTemplate(), dataMap);

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
        //String finalExtensionData = execPreCompiledTemplateFunction(businessLogicRule.getActionFinalTemplate(), dataMap);
        String finalExtensionData = evaluateTemplateFunction(businessLogicRule.getActionFinalTemplate(), dataMap);

        //Step 6. Add result of final template result in to extension data block.
        if( finalExtensionData != null )
            outputResult.getDataExtension().put(businessLogicRule.getActionFinalTemplate(), finalExtensionData);

        return outputResult;
    }

}
