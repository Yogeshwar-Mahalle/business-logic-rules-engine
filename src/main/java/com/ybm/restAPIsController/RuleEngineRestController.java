/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.rulesBusinessSetupRepo.*;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;

import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleAction;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleCondition;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class RuleEngineRestController {
    @Autowired
    private BusinessRulesService businessRulesService;

    @Autowired
    private BusinessRuleConditionService businessRuleConditionService;

    @Autowired
    private BusinessRuleActionService businessRuleActionService;

    @Autowired
    private BusinessRuleFunctionService businessRuleFunctionService;

    @GetMapping(value = "/get-rule/{ruleId}")
    public ResponseEntity<?> getRuleById(@PathVariable("ruleId") String ruleId) {
        BusinessLogicRule businessLogicRule = businessRulesService.getRuleById(ruleId);
        return ResponseEntity.ok(businessLogicRule);
    }

    @GetMapping(value = "/get-all-rules/{ruleType}")
    public ResponseEntity<?> getAllRulesByType(@PathVariable("ruleType") String ruleType) {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllRulesByType(ruleType);
        return ResponseEntity.ok(allBusinessLogicRules);
    }

    @GetMapping(value = "/get-all-rules")
    public ResponseEntity<?> getAllRules() {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllRules();
        return ResponseEntity.ok(allBusinessLogicRules);
    }

    @GetMapping(value = "/get-entities-rules/{entities}/{ruleType}")
    public ResponseEntity<?> getAllEntityRuleByType(@PathVariable("entities") String entity, @PathVariable("ruleType") String ruleType) {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllEntityRulesByType(entity, ruleType);
        return ResponseEntity.ok(allBusinessLogicRules);
    }

    @GetMapping(value = "/get-entities-rules/{entities}")
    public ResponseEntity<?> getAllEntityRules(@PathVariable("entities") String entity) {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllEntityRules(entity);
        return ResponseEntity.ok(allBusinessLogicRules);
    }

    @PostMapping(value = "/update-rules")
    public ResponseEntity<?> updateRule(@RequestBody BusinessLogicRule businessLogicRule) {

        BusinessLogicRule businessLogicRuleUpdated = businessRulesService.saveRule(businessLogicRule);
        return ResponseEntity.ok(businessLogicRuleUpdated);
    }

    @PostMapping(value = "/update-all-rules")
    public ResponseEntity<?> updateRule(@RequestBody List<BusinessLogicRule> businessLogicRules) {

        List<BusinessLogicRule> rulesUpdated = businessRulesService.saveRules(businessLogicRules);
        return ResponseEntity.ok(rulesUpdated);
    }

    @DeleteMapping(value = "/remove-rule/{ruleId}")
    public ResponseEntity<?> removeRuleById(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRule> allRemainingBusinessLogicRules = businessRulesService.removeRuleById(ruleId);
        return ResponseEntity.ok(allRemainingBusinessLogicRules);
    }

    @GetMapping(value = "/get-rule-conditions/{ruleId}")
    public ResponseEntity<?> getRuleConditionById(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRuleCondition> businessLogicRuleConditions = businessRuleConditionService.getRuleConditionById(ruleId);
        return ResponseEntity.ok(businessLogicRuleConditions);
    }

    @PostMapping(value = "/update-rule-conditions")
    public ResponseEntity<?> updateRuleConditions(@RequestBody List<BusinessLogicRuleCondition> businessLogicRuleConditions) {

        List<BusinessLogicRuleCondition> ruleConditionsUpdated = businessRuleConditionService.saveRuleConditions(businessLogicRuleConditions);
        return ResponseEntity.ok(ruleConditionsUpdated);
    }

    @GetMapping(value = "/get-rule-actions/{ruleId}")
    public ResponseEntity<?> getRuleActionByID(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRuleAction> businessLogicRuleActions = businessRuleActionService.getRuleActionById(ruleId);
        return ResponseEntity.ok(businessLogicRuleActions);
    }

    @PostMapping(value = "/update-rule-actions")
    public ResponseEntity<?> updateRuleActions(@RequestBody List<BusinessLogicRuleAction> businessLogicRuleActions) {

        List<BusinessLogicRuleAction> ruleActionsUpdated = businessRuleActionService.saveRuleActions(businessLogicRuleActions);
        return ResponseEntity.ok(ruleActionsUpdated);
    }

    @GetMapping(value = "/get-all-functions")
    public ResponseEntity<?> getAllRuleFunction() {
        List<BusinessLogicRuleFunction> allBusinessLogicRuleFunction = businessRuleFunctionService.getAllRuleFunction();
        return ResponseEntity.ok(allBusinessLogicRuleFunction);
    }

    @GetMapping(value = "/get-function/{functionId}")
    public ResponseEntity<?> getRuleFunction(@PathVariable("functionId") String functionId) {
        BusinessLogicRuleFunction businessLogicRuleFunction = businessRuleFunctionService.getRuleFunction(functionId);
        return ResponseEntity.ok(businessLogicRuleFunction);
    }

    @PostMapping(value = "/update-rule-function")
    public ResponseEntity<?> updateRuleFunction(@RequestBody BusinessLogicRuleFunction businessLogicRuleFunction) {
        BusinessLogicRuleFunction ruleFunctionUpdated = businessRuleFunctionService.saveRuleFunction(businessLogicRuleFunction);
        return ResponseEntity.ok(ruleFunctionUpdated);
    }

    @DeleteMapping(value = "/remove-rule-function/{functionId}")
    public ResponseEntity<?> removeRuleFunctionById(@PathVariable("functionId") String functionId) {
        List<BusinessLogicRuleFunction> allRemainingRuleFunctions = businessRuleFunctionService.removeRuleFunctionById(functionId);
        return ResponseEntity.ok(allRemainingRuleFunctions);
    }

}
