/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleDbModel;
//import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesRepositoryDummy;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleAction;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleCondition;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BusinessRulesService {
    @Autowired
    private BLRulesRepository blRulesRepository;
    //private BLRulesRepositoryDummy blRulesRepository;
    @Autowired
    private BusinessRuleConditionService businessRuleConditionService;
    @Autowired
    private BusinessRuleActionService businessRuleActionService;

    @Transactional
    public BusinessLogicRule saveRule(BusinessLogicRule businessLogicRule) {

        BLRuleDbModel blRuleDbModel = mapRuleToDbModel(businessLogicRule);
        blRuleDbModel = blRulesRepository.save(blRuleDbModel);

        BusinessLogicRule updatedBusinessLogicRule = mapRuleFromDbModel(blRuleDbModel);

        List<BusinessLogicRuleCondition> conditionList =
                businessRuleConditionService.saveRuleConditions(updatedBusinessLogicRule.getRuleId(), businessLogicRule.getConditionList());
        updatedBusinessLogicRule.setConditionList(conditionList);

        List<BusinessLogicRuleAction> actionList =
                businessRuleActionService.saveRuleActions(updatedBusinessLogicRule.getRuleId(), businessLogicRule.getActionList());
        updatedBusinessLogicRule.setActionList(actionList);

        return updatedBusinessLogicRule;

    }

    @Transactional
    public List<BusinessLogicRule> saveRules(List<BusinessLogicRule> businessLogicRules) {

        List<BLRuleDbModel> listBLRuleDBModel = businessLogicRules.stream()
                .map(
                        this::mapRuleToDbModel
                )
                .collect(Collectors.toList());

        List<BusinessLogicRule> updatedBusinessLogicRules =
                blRulesRepository.saveAll(listBLRuleDBModel).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule businessLogicRule : businessLogicRules )
        {
            for( BusinessLogicRule updatedBusinessLogicRule : updatedBusinessLogicRules )
            {
                if( businessLogicRule.getRuleId().equals(updatedBusinessLogicRule.getRuleId()) )
                {
                    List<BusinessLogicRuleCondition> conditionList =
                            businessRuleConditionService.saveRuleConditions(updatedBusinessLogicRule.getRuleId(), businessLogicRule.getConditionList());
                    updatedBusinessLogicRule.setConditionList(conditionList);

                    List<BusinessLogicRuleAction> actionList =
                            businessRuleActionService.saveRuleActions(updatedBusinessLogicRule.getRuleId(), businessLogicRule.getActionList());
                    updatedBusinessLogicRule.setActionList(actionList);
                }
            }
        }

        return updatedBusinessLogicRules;
    }

    public List<BusinessLogicRule> removeRuleById(String ruleId){
        if( ruleId == null )
            return null;

        List<BusinessLogicRuleCondition> updatedConditionList =
                businessRuleConditionService.removeConditionsByRuleId(ruleId);
        List<BusinessLogicRuleAction> updatedActionList =
                businessRuleActionService.removeActionsByRuleId(ruleId);

        blRulesRepository.deleteById(ruleId);

        List<BusinessLogicRule> updatedBusinessLogicRules =
                blRulesRepository.findAll().stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule updatedBusinessLogicRule : updatedBusinessLogicRules )
        {
            List<BusinessLogicRuleCondition> conditionList = new ArrayList<>();
            for( BusinessLogicRuleCondition businessLogicRuleCondition : updatedConditionList )
            {
                if( businessLogicRuleCondition.getRuleId().equals(updatedBusinessLogicRule.getRuleId()) )
                    conditionList.add(businessLogicRuleCondition);
            }

            List<BusinessLogicRuleAction> actionList = new ArrayList<>();
            for( BusinessLogicRuleAction businessLogicRuleAction : updatedActionList )
            {
                if( businessLogicRuleAction.getRuleId().equals(updatedBusinessLogicRule.getRuleId()) )
                    actionList.add(businessLogicRuleAction);
            }

            updatedBusinessLogicRule.setConditionList(conditionList);
            updatedBusinessLogicRule.setActionList(actionList);
        }

        return updatedBusinessLogicRules;
    }

    public List<BusinessLogicRule> getAllRules(){
        List<BusinessLogicRule> ruleList = blRulesRepository.findAll().stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule businessLogicRule : ruleList )
        {
            List<BusinessLogicRuleCondition> conditionList =
                    businessRuleConditionService.getRuleConditionsByRuleId(businessLogicRule.getRuleId());

            List<BusinessLogicRuleAction> actionList =
                    businessRuleActionService.getRuleActionsByRuleId(businessLogicRule.getRuleId());

            businessLogicRule.setConditionList(conditionList);
            businessLogicRule.setActionList(actionList);
        }

        return ruleList;
    }


    public BusinessLogicRule getRuleById(String ruleId){
        if( ruleId == null )
            return null;

        Optional<BLRuleDbModel> ruleDbModel = blRulesRepository.findByRuleId(ruleId);
        BusinessLogicRule businessLogicRule =
                ruleDbModel.map(this::mapRuleFromDbModel).orElse(null);

        List<BusinessLogicRuleCondition> conditionList =
                businessRuleConditionService.getRuleConditionsByRuleId(businessLogicRule.getRuleId());

        List<BusinessLogicRuleAction> actionList =
                businessRuleActionService.getRuleActionsByRuleId(businessLogicRule.getRuleId());

        businessLogicRule.setConditionList(conditionList);
        businessLogicRule.setActionList(actionList);

        return businessLogicRule;
    }


    public List<BusinessLogicRule> getAllRulesByType(String ruleType){
        if( ruleType == null )
            return null;

        List<BusinessLogicRule> ruleList = blRulesRepository.findByRuleType(ruleType).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule businessLogicRule : ruleList )
        {
            List<BusinessLogicRuleCondition> conditionList =
                    businessRuleConditionService.getRuleConditionsByRuleId(businessLogicRule.getRuleId());

            List<BusinessLogicRuleAction> actionList =
                    businessRuleActionService.getRuleActionsByRuleId(businessLogicRule.getRuleId());

            businessLogicRule.setConditionList(conditionList);
            businessLogicRule.setActionList(actionList);
        }

        return ruleList;
    }

    public List<BusinessLogicRule> getAllEntityRules(String entity){
        if( entity == null )
            return null;

        List<BusinessLogicRule> ruleList = blRulesRepository.findByLinkedEntity(entity).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule businessLogicRule : ruleList )
        {
            List<BusinessLogicRuleCondition> conditionList =
                    businessRuleConditionService.getRuleConditionsByRuleId(businessLogicRule.getRuleId());

            List<BusinessLogicRuleAction> actionList =
                    businessRuleActionService.getRuleActionsByRuleId(businessLogicRule.getRuleId());

            businessLogicRule.setConditionList(conditionList);
            businessLogicRule.setActionList(actionList);
        }

        return ruleList;
    }


    public List<BusinessLogicRule> getAllEntityRulesByType(String entity, String ruleType){
        if( entity == null || ruleType == null)
            return null;

        List<BusinessLogicRule> ruleList = blRulesRepository.findByLinkedEntityAndRuleType(entity, ruleType).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

        for ( BusinessLogicRule businessLogicRule : ruleList )
        {
            List<BusinessLogicRuleCondition> conditionList =
                    businessRuleConditionService.getRuleConditionsByRuleId(businessLogicRule.getRuleId());

            List<BusinessLogicRuleAction> actionList =
                    businessRuleActionService.getRuleActionsByRuleId(businessLogicRule.getRuleId());

            businessLogicRule.setConditionList(conditionList);
            businessLogicRule.setActionList(actionList);
        }

        return ruleList;
    }

    private BusinessLogicRule mapRuleFromDbModel(BLRuleDbModel blRuleDbModel)
    {
        return BusinessLogicRule.builder()
                .ruleType(blRuleDbModel.getRuleType().toUpperCase())
                .ruleId(blRuleDbModel.getRuleId())
                .linkedEntity(blRuleDbModel.getLinkedEntity())
                .ruleName(blRuleDbModel.getRuleName())
                .condInclFuncNameList(blRuleDbModel.getCondInclFuncNameList())
                .condInitTemplate(blRuleDbModel.getCondInitTemplate())
                .condition(blRuleDbModel.getCondition())
                .actionInclFuncNameList(blRuleDbModel.getActionInclFuncNameList())
                .actionInitTemplate(blRuleDbModel.getActionInitTemplate())
                .action(blRuleDbModel.getAction())
                .actionFinalTemplate(blRuleDbModel.getActionFinalTemplate())
                .description(blRuleDbModel.getDescription())
                .priority(blRuleDbModel.getPriority())
                .status(blRuleDbModel.getStatus())
                .createTimeStamp(blRuleDbModel.getCreateTimeStamp())
                .effectiveFromDate(blRuleDbModel.getEffectiveFromDate())
                .updateTimeStamp(blRuleDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleDbModel mapRuleToDbModel(BusinessLogicRule businessLogicRule)
    {
        List<BusinessLogicRuleCondition> businessLogicRuleConditionList = businessLogicRule.getConditionList();
        List<BusinessLogicRuleAction> businessLogicRuleActionList = businessLogicRule.getActionList();

        String decimalPattern = "([-+]*)([0-9]*)|([-+]*)([0-9]*)\\.([0-9]*)";

        String condIncludeFunctionNameList = null;
        StringBuilder ruleConditions = null;
        if(businessLogicRuleConditionList != null) {

            for (BusinessLogicRuleCondition businessLogicRuleCondition : businessLogicRuleConditionList) {

                String leftOperand = null;
                switch (businessLogicRuleCondition.getLeftOperandType()) {
                    case EXCHANGE -> {
                        leftOperand = businessLogicRuleCondition.getLeftDataObject() != null ?
                                businessLogicRuleCondition.getLeftDataObject().getLabel() : ExchangeObjectType.INPUT_PAYLOAD.getLabel();
                        leftOperand += businessLogicRuleCondition.getLeftOperand() != null ?
                                ".?" + businessLogicRuleCondition.getLeftOperand() : "";
                    }
                    case VARIABLE -> {
                        leftOperand = businessLogicRuleCondition.getLeftOperand() != null ?
                                businessLogicRuleCondition.getLeftOperand() : "";
                    }
                    case CONSTANT -> {
                        leftOperand = businessLogicRuleCondition.getLeftOperand();
                        if( leftOperand != null ) {
                            boolean isNumber = Pattern.matches(decimalPattern, leftOperand);
                            if (!isNumber)
                                leftOperand = "\"" + leftOperand + "\"";
                        }
                    }
                    case FUNCTION -> {
                        //TODO:: Logic to call function by passing input parameter
                    }
                    case PATH -> {
                        //TODO:: Logic to fetch data from xml string by using xpath or from json by using jpath
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in leftOperand
                    }

                }

                String rightOperand = null;
                switch (businessLogicRuleCondition.getRightOperandType()) {
                    case EXCHANGE -> {
                        rightOperand = businessLogicRuleCondition.getRightDataObject() != null ?
                                businessLogicRuleCondition.getRightDataObject().getLabel() : ExchangeObjectType.INPUT_PAYLOAD.getLabel();
                        rightOperand += businessLogicRuleCondition.getRightOperand() != null ?
                                ".?" + businessLogicRuleCondition.getRightOperand() : "";
                    }
                    case VARIABLE -> {
                        rightOperand = businessLogicRuleCondition.getRightOperand() != null ?
                                businessLogicRuleCondition.getRightOperand() : "";
                    }
                    case CONSTANT -> {
                        rightOperand = businessLogicRuleCondition.getRightOperand();
                        if( rightOperand != null ) {
                            boolean isNumber = Pattern.matches(decimalPattern, rightOperand);
                            if (!isNumber)
                                rightOperand = "\"" + rightOperand + "\"";
                        }
                    }
                    case FUNCTION -> {
                        //TODO:: Logic to call function by passing input parameter
                    }
                    case PATH -> {
                        //TODO:: Logic to fetch data from xml string by using xpath or from json by using jpath
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }
                }

                String ruleCondition = businessLogicRuleCondition.getIsNotIndicator() != null &&
                        businessLogicRuleCondition.getIsNotIndicator() ? "!" : "";
                ruleCondition += businessLogicRuleCondition.getOpenConditionScope() != null ?
                        businessLogicRuleCondition.getOpenConditionScope() : "";

                switch (businessLogicRuleCondition.getOperator()) {
                    case EQUAL -> {
                        ruleCondition += leftOperand + "==" + rightOperand;
                    }
                    case ASSIGN -> {
                        ruleCondition += leftOperand + "=" + rightOperand;
                    }
                    case NOT_EQUAL -> {
                        ruleCondition += leftOperand + "!=" + rightOperand;
                    }
                    case LESS_THAN -> {
                        ruleCondition += leftOperand + "<" + rightOperand;
                    }
                    case GREATER_THAN -> {
                        ruleCondition += leftOperand + ">" + rightOperand;
                    }
                    case LESS_THAN_EQUAL -> {
                        ruleCondition += leftOperand + "<=" + rightOperand;
                    }
                    case GREATER_THAN_EQUAL -> {
                        ruleCondition += leftOperand + ">=" + rightOperand;
                    }
                    case EQUAL_IGNORECASE -> {
                        ruleCondition += leftOperand + ".equalsIgnoreCase(" + rightOperand + ")";
                    }
                    case NOT_EQUAL_IGNORECASE -> {
                        ruleCondition += "!" + leftOperand + ".equalsIgnoreCase(" + rightOperand + ")";
                    }
                    case CONTAIN -> {
                        ruleCondition += leftOperand + ".contains(" + rightOperand + ")";
                    }
                    case NOT_CONTAIN -> {
                        ruleCondition += "!" + leftOperand + ".contains(" + rightOperand + ")";
                    }
                    case TRIM_EQUAL -> {
                        ruleCondition += leftOperand + "==" + rightOperand + ".trim()";
                    }
                    case TRIM_NOT_EQUAL -> {
                        ruleCondition += "!" + leftOperand + "==" + rightOperand + ".trim()";
                    }
                    case TRIM_EQUAL_IGNORECASE -> {
                        ruleCondition += leftOperand + ".equalsIgnoreCase( " + rightOperand + ".trim() )";
                    }
                    case TRIM_NOT_EQUAL_IGNORECASE -> {
                        ruleCondition += "!" + leftOperand + ".equalsIgnoreCase( " + rightOperand + ".trim() )";
                    }
                    case TRIM_LESS_THAN -> {
                        ruleCondition += leftOperand + "<" + rightOperand + ".trim()";
                    }
                    case TRIM_LESS_THAN_EQUAL -> {
                        ruleCondition += leftOperand + "<=" + rightOperand + ".trim()";
                    }
                    case TRIM_GREATER_THAN -> {
                        ruleCondition += leftOperand + ">" + rightOperand + ".trim()";
                    }
                    case TRIM_GREATER_THAN_EQUAL -> {
                        ruleCondition += leftOperand + ">=" + rightOperand + ".trim()";
                    }
                }

                ruleCondition += businessLogicRuleCondition.getCloseConditionScope() != null ?
                        businessLogicRuleCondition.getCloseConditionScope() : "";

                if (ruleConditions != null) {
                    if (businessLogicRuleCondition.getLogicalOperator() != null) {
                        switch (businessLogicRuleCondition.getLogicalOperator()) {
                            case AND -> {
                                ruleConditions.append(" && ").append(ruleCondition);
                            }
                            case OR -> {
                                ruleConditions.append(" || ").append(ruleCondition);
                            }
                        }
                    }
                }
                else {
                    ruleConditions = new StringBuilder(ruleCondition);
                }

                if (businessLogicRuleCondition.getIncludeFuncNameList() != null &&
                        !businessLogicRuleCondition.getIncludeFuncNameList().trim().isEmpty())
                    condIncludeFunctionNameList = condIncludeFunctionNameList == null ?
                            businessLogicRuleCondition.getIncludeFuncNameList() :
                            condIncludeFunctionNameList + ", " + businessLogicRuleCondition.getIncludeFuncNameList();
            }
        }

        String actionIncludeFunctionNameList = null;
        String ruleActions = null;
        if(businessLogicRuleActionList != null) {
            for (BusinessLogicRuleAction businessLogicRuleAction : businessLogicRuleActionList) {
                String ruleAction = null;

                switch (businessLogicRuleAction.getAssigneeType()) {
                    case EXCHANGE -> {
                        ruleAction = businessLogicRuleAction.getAssigneeDataObject() != null ?
                                businessLogicRuleAction.getAssigneeDataObject().getLabel() : ExchangeObjectType.OUTPUT_PAYLOAD.getLabel();
                        ruleAction += ".put(";
                        ruleAction += businessLogicRuleAction.getAssignee() != null ?
                                businessLogicRuleAction.getAssignee() : "?";
                        ruleAction += ", ";
                    }
                    case VARIABLE -> {
                        ruleAction = businessLogicRuleAction.getAssignee();
                        ruleAction += "=";
                    }
                    case CONSTANT -> {
                        //TODO:: Assignee can not be constant, need to handle this scenario later
                        ruleAction = businessLogicRuleAction.getAssignee();
                        if( ruleAction != null) {
                            boolean isNumber = Pattern.matches(decimalPattern, ruleAction);
                            if (!isNumber)
                                ruleAction = "\"" + ruleAction + "\"";
                        }
                    }
                    case DSL -> {
                        ruleAction = "${" ;
                        ruleAction += businessLogicRuleAction.getAssignee();
                        ruleAction += "}==";
                    }
                    case FUNCTION -> {
                        ruleAction = businessLogicRuleAction.getAssignee();
                        ruleAction += "==";
                    }
                    case PATH -> {
                        //TODO:: Logic to fetch data from xml string by using xpath or from json by using jpath
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }

                }

                switch (businessLogicRuleAction.getAssignorType()) {
                    case EXCHANGE -> {
                        ruleAction += businessLogicRuleAction.getAssignorDataObject() != null ?
                                businessLogicRuleAction.getAssignorDataObject().getLabel() : ExchangeObjectType.INPUT_PAYLOAD.getLabel();
                        ruleAction += businessLogicRuleAction.getAssignor() != null ?
                                "." + businessLogicRuleAction.getAssignor() : "";
                    }
                    case VARIABLE -> {
                        ruleAction += businessLogicRuleAction.getAssignor();
                    }
                    case CONSTANT -> {
                        String assignor = businessLogicRuleAction.getAssignor();
                        if( assignor != null) {
                            boolean isNumber = Pattern.matches(decimalPattern, assignor);
                            if (!isNumber)
                                assignor = "\"" + assignor + "\"";
                        }
                        ruleAction += assignor + "); ";
                    }
                    case DSL -> {
                        ruleAction += "${" ;
                        ruleAction += businessLogicRuleAction.getAssignor();
                        ruleAction += "}";
                    }
                    case FUNCTION -> {
                        ruleAction += businessLogicRuleAction.getAssignor();
                    }
                    case PATH -> {
                        //TODO:: Logic to fetch data from xml string by using xpath or from json by using jpath
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }
                }

                //Closing expression string
                switch (businessLogicRuleAction.getAssigneeType()) {
                    case EXCHANGE -> {
                        ruleAction += "); ";
                    }
                    case VARIABLE, CONSTANT, DSL, FUNCTION -> {
                        ruleAction += "; ";
                    }
                    case PATH -> {
                        //TODO:: Logic to fetch data from xml string by using xpath or from json by using jpath
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }

                }

                ruleActions = ruleActions == null ? ruleAction : ruleActions + ruleAction;

                if (businessLogicRuleAction.getIncludeFuncNameList() != null &&
                        !businessLogicRuleAction.getIncludeFuncNameList().trim().isEmpty())
                    actionIncludeFunctionNameList = actionIncludeFunctionNameList == null ?
                            businessLogicRuleAction.getIncludeFuncNameList() :
                            actionIncludeFunctionNameList + "," + businessLogicRuleAction.getIncludeFuncNameList();
            }
        }

        return BLRuleDbModel.builder()
                .ruleType(businessLogicRule.getRuleType())
                .ruleId( businessLogicRule.getRuleId() == null ?
                        businessLogicRule.getRuleType() + "~" + businessLogicRule.getLinkedEntity() + "~" + businessLogicRule.getRuleName() :
                        businessLogicRule.getRuleId() )
                .linkedEntity(businessLogicRule.getLinkedEntity())
                .ruleName(businessLogicRule.getRuleName())
                .condInclFuncNameList(condIncludeFunctionNameList)
                .condInitTemplate(businessLogicRule.getCondInitTemplate())
                .condition(ruleConditions != null ? ruleConditions.toString() : businessLogicRule.getCondition())
                .actionInclFuncNameList(actionIncludeFunctionNameList)
                .actionInitTemplate(businessLogicRule.getActionInitTemplate())
                .action(ruleActions != null ? ruleActions : businessLogicRule.getAction())
                .actionFinalTemplate(businessLogicRule.getActionFinalTemplate())
                .description(businessLogicRule.getDescription())
                .priority(businessLogicRule.getPriority())
                .status(businessLogicRule.getStatus())
                .createTimeStamp(businessLogicRule.getCreateTimeStamp() == null ? new Date() : businessLogicRule.getCreateTimeStamp())
                .effectiveFromDate(businessLogicRule.getEffectiveFromDate())
                .updateTimeStamp(new Date())
                .build();

    }
}
