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
        StringBuilder ruleConditions = null;
        if(businessLogicRuleConditionList != null) {

            for (BusinessLogicRuleCondition businessLogicRuleCondition : businessLogicRuleConditionList) {

                String leftOperand = null;
                switch (businessLogicRuleCondition.getLeftOperandType()) {
                    case EXCHANGE, PATH -> {
                        leftOperand = businessLogicRuleCondition.getLeftDataObject() != null ?
                                businessLogicRuleCondition.getLeftDataObject() : "inPayload";
                        leftOperand += businessLogicRuleCondition.getLeftOperand() != null ?
                                ".?" + businessLogicRuleCondition.getLeftOperand() : "";

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
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in leftOperand
                    }

                }

                String rightOperand = null;
                switch (businessLogicRuleCondition.getRightOperandType()) {
                    case EXCHANGE, PATH -> {
                        rightOperand = businessLogicRuleCondition.getRightDataObject() != null ?
                                businessLogicRuleCondition.getRightDataObject() : "inPayload";
                        rightOperand += businessLogicRuleCondition.getRightOperand() != null ?
                                ".?" + businessLogicRuleCondition.getRightOperand() : "";
                    }
                    case VARIABLE, FUNCTION -> {
                        rightOperand = businessLogicRuleCondition.getRightOperand();
                    }
                    case CONSTANT -> {
                        rightOperand = businessLogicRuleCondition.getRightOperand();
                        boolean isNumber = Pattern.matches(decimalPattern, rightOperand);
                        if (!isNumber)
                            rightOperand = "\"" + rightOperand + "\"";
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }
                }

                String ruleCondition = businessLogicRuleCondition.getOpenConditionScope() != null ?
                        businessLogicRuleCondition.getOpenConditionScope() : "";

                switch (businessLogicRuleCondition.getOperator()) {
                    case EQUAL -> {
                        ruleCondition += leftOperand + "==" + rightOperand;
                    }
                    case ASSIGN -> {
                        ruleCondition += leftOperand + "=" + rightOperand;
                    }
                    case SET -> {

                        leftOperand = businessLogicRuleCondition.getLeftDataObject() != null ?
                                businessLogicRuleCondition.getLeftDataObject() + ".put( " : "outPayload.put( ";
                        leftOperand += businessLogicRuleCondition.getLeftOperand() != null ?
                                businessLogicRuleCondition.getLeftOperand() : "?";

                        rightOperand = businessLogicRuleCondition.getRightDataObject() != null ?
                                businessLogicRuleCondition.getRightDataObject() : "inPayload";
                        rightOperand += businessLogicRuleCondition.getRightOperand() != null ?
                                "." + businessLogicRuleCondition.getRightOperand() : "";

                        ruleCondition += leftOperand + ", " + rightOperand + " )";
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
                            case NAND -> {
                                ruleConditions = new StringBuilder(" !" + ruleConditions + " && " + ruleCondition );
                            }
                            case NOR -> {
                                ruleConditions = new StringBuilder(" !" + ruleConditions + " || " + ruleCondition );
                            }
                            case NOT -> {
                                ruleConditions.append(" !").append(ruleCondition);
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

                String assigneeScript = null;
                switch (businessLogicRuleAction.getAssigneeType()) {
                    case EXCHANGE, PATH -> {
                        assigneeScript = businessLogicRuleAction.getAssignee() != null ?
                                "outPayload.put( " + businessLogicRuleAction.getAssignee() : "outPayload.";

                    }
                    case VARIABLE, FUNCTION -> {
                        assigneeScript = businessLogicRuleAction.getAssignee();
                    }
                    case CONSTANT -> {
                        assigneeScript = businessLogicRuleAction.getAssignee();
                        boolean isNumber = Pattern.matches(decimalPattern, assigneeScript);
                        if (!isNumber)
                            assigneeScript = "payload." + "\"" + assigneeScript + "\"";
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in leftOperand
                    }

                }

                String assignorScript = null;
                switch (businessLogicRuleAction.getAssignorType()) {
                    case EXCHANGE, PATH -> {
                        assignorScript = ", " + (businessLogicRuleAction.getAssignor() != null ?
                                businessLogicRuleAction.getAssignor() : "inPayload" ) + ")" ;
                    }
                    case VARIABLE, FUNCTION -> {
                        assignorScript = businessLogicRuleAction.getAssignor();
                    }
                    case CONSTANT -> {
                        assignorScript = businessLogicRuleAction.getAssignor();
                        boolean isNumber = Pattern.matches(decimalPattern, assignorScript);
                        if (!isNumber)
                            assignorScript = "payload." + "\"" + assignorScript + "\"";
                    }
                    case BASE_RULE -> {
                        //TODO:Fetch Base Rule details from DB/Cache and set result in rightOperand
                    }
                }

                //TODO:: Business logic on businessLogicRuleAction.getRuleConditionId() is pending
                //TODO:: fetch data mapping rules from RuleCondition table and execute them to change output payload
                //TODO:: handle action interface by using DSL plugin, by writing defaul API plugin to consume APIs based on derived context
                //businessLogicRuleAction.getRuleConditionId();

                if (assigneeScript != null) {
                    ruleAction =  assigneeScript +  assignorScript;

                    ruleActions = ruleActions == null ? ruleAction : ruleActions + "; " + ruleAction;
                }

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
