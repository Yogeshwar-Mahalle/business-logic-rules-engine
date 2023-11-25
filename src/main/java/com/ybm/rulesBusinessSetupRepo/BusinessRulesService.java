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
                businessRuleConditionService.saveRuleConditions(updatedBusinessLogicRule.getRuleId(), updatedBusinessLogicRule.getConditionList());
        updatedBusinessLogicRule.setConditionList(conditionList);

        List<BusinessLogicRuleAction> actionList =
                businessRuleActionService.saveRuleActions(updatedBusinessLogicRule.getRuleId(), updatedBusinessLogicRule.getActionList());
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

        for ( BusinessLogicRule updatedBusinessLogicRule : updatedBusinessLogicRules )
        {
            List<BusinessLogicRuleCondition> conditionList =
                    businessRuleConditionService.saveRuleConditions(updatedBusinessLogicRule.getRuleId(), updatedBusinessLogicRule.getConditionList());
            updatedBusinessLogicRule.setConditionList(conditionList);

            List<BusinessLogicRuleAction> actionList =
                    businessRuleActionService.saveRuleActions(updatedBusinessLogicRule.getRuleId(), updatedBusinessLogicRule.getActionList());
            updatedBusinessLogicRule.setActionList(actionList);
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

        Optional<BLRuleDbModel> ruleDbModel = blRulesRepository.findById(ruleId);
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
        String ruleConditions = null;
        if(businessLogicRuleConditionList != null) {
            for (BusinessLogicRuleCondition businessLogicRuleCondition : businessLogicRuleConditionList) {
                String ruleCondition = businessLogicRuleCondition.getOpenConditionScope() != null ?
                        businessLogicRuleCondition.getOpenConditionScope() : "";

                String leftOperand = null;
                switch (businessLogicRuleCondition.getLeftOperandType()) {
                    case EXCHANGE, PATH -> {
                        leftOperand = businessLogicRuleCondition.getLeftDataObject() != null ?
                                businessLogicRuleCondition.getLeftDataObject() : "inPayload";
                        leftOperand += businessLogicRuleCondition.getLeftOperand() != null ?
                                "." + businessLogicRuleCondition.getLeftOperand() : "";

                    }
                    case VARIABLE, FUNCTION -> {
                        leftOperand = businessLogicRuleCondition.getLeftOperand();
                    }
                    case CONSTANT -> {
                        leftOperand = businessLogicRuleCondition.getLeftOperand();
                        boolean isNumber = Pattern.matches(decimalPattern, leftOperand);
                        if (!isNumber)
                            leftOperand = "\"" + leftOperand + "\"";
                    }

                }

                String rightOperand = null;
                switch (businessLogicRuleCondition.getRightOperandType()) {
                    case EXCHANGE, PATH -> {
                        rightOperand = businessLogicRuleCondition.getRightDataObject() != null ?
                                businessLogicRuleCondition.getRightDataObject() : "inPayload";
                        rightOperand += businessLogicRuleCondition.getRightOperand() != null ?
                                "." + businessLogicRuleCondition.getRightOperand() : "";
                    }
                    case VARIABLE, FUNCTION -> {
                        rightOperand = businessLogicRuleCondition.getRightOperand();
                    }
                    case CONSTANT -> {
                        rightOperand = businessLogicRuleCondition.getLeftOperand();
                        boolean isNumber = Pattern.matches(decimalPattern, rightOperand);
                        if (!isNumber)
                            rightOperand = "\"" + rightOperand + "\"";
                    }
                }

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
                        ruleCondition += leftOperand + ".equalIgnoreCase(" + rightOperand + ")";
                    }
                    case NOT_EQUAL_IGNORECASE -> {
                        ruleCondition += "!" + leftOperand + ".equalIgnoreCase(" + rightOperand + ")";
                    }
                    case CONTAIN -> {
                        ruleCondition += leftOperand + ".contain(" + rightOperand + ")";
                    }
                    case NOT_CONTAIN -> {
                        ruleCondition += "!" + leftOperand + ".contain(" + rightOperand + ")";
                    }
                    case TRIM_EQUAL -> {
                        ruleCondition += leftOperand + "==" + rightOperand + ".trim()";
                    }
                    case TRIM_NOT_EQUAL -> {
                        ruleCondition += "!" + leftOperand + "==" + rightOperand + ".trim()";
                    }
                    case TRIM_EQUAL_IGNORECASE -> {
                        ruleCondition += leftOperand + ".equalIgnoreCase( " + rightOperand + ".trim() )";
                    }
                    case TRIM_NOT_EQUAL_IGNORECASE -> {
                        ruleCondition += "!" + leftOperand + ".equalIgnoreCase( " + rightOperand + ".trim() )";
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

                ruleCondition += businessLogicRuleCondition.getLogicalOperator() != null ?
                        businessLogicRuleCondition.getLogicalOperator() : "";

                ruleCondition += businessLogicRuleCondition.getOpenConditionScope() != null ?
                        businessLogicRuleCondition.getOpenConditionScope() : "";

                ruleConditions = ruleConditions == null ? ruleCondition : ruleConditions + ruleCondition;

                if (businessLogicRuleCondition.getIncludeFuncNameList() != null)
                    condIncludeFunctionNameList = condIncludeFunctionNameList == null ?
                            businessLogicRuleCondition.getIncludeFuncNameList() :
                            condIncludeFunctionNameList + businessLogicRuleCondition.getIncludeFuncNameList();
            }
        }

        String actionIncludeFunctionNameList = null;
        String ruleActions = null;
        if(businessLogicRuleActionList != null) {
            for (BusinessLogicRuleAction businessLogicRuleAction : businessLogicRuleActionList) {
                String ruleAction = null;
                String assignee = businessLogicRuleAction.getAssignee();
                String assignor = businessLogicRuleAction.getAssignor();

                if (assignee != null) {
                    ruleAction = "outPayload.put( " + assignee + ", " + assignor + " );";

                    ruleActions = ruleActions == null ? ruleAction : ruleActions + ruleAction;
                }

                if (businessLogicRuleAction.getIncludeFuncNameList() != null)
                    actionIncludeFunctionNameList = actionIncludeFunctionNameList == null ?
                            businessLogicRuleAction.getIncludeFuncNameList() :
                            actionIncludeFunctionNameList + businessLogicRuleAction.getIncludeFuncNameList();
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
                .condition(ruleConditions != null ? ruleConditions : businessLogicRule.getCondition())
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
