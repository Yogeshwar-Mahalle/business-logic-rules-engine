/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.ruleEngine.RuleEngine;
import com.ybm.rulesBusinessSetupRepo.*;
import com.ybm.rulesBusinessSetupRepo.models.*;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@EnableMethodSecurity
@RestController
public class BLRuleEngineRestController {
    private static final Logger LOG = LoggerFactory.getLogger(BLRuleEngineRestController.class);
    @Autowired
    private RuleEngine ruleEngine;
    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private BusinessRuleTypesService businessRuleTypesService;
    @Autowired
    private BusinessRulesService businessRulesService;
    @Autowired
    private BusinessRuleConditionService businessRuleConditionService;
    @Autowired
    private BusinessRuleActionService businessRuleActionService;
    @Autowired
    private BusinessRuleFunctionTemplateService businessRuleFunctionTemplateService;
    @Autowired
    private BusinessRuleFieldListService businessRuleFieldListService;
    @Autowired
    private BusinessRuleValueListService businessRuleValueListService;

    @Autowired
    private BusinessRuleProcessProfileService businessRuleProcessProfileService;

    @GetMapping(value = "/get-all-rule-entity")
    public ResponseEntity<?> getAllRuleEntities() {
        List<BusinessLogicRuleEntity> allBusinessLogicRuleEntity = businessRuleEntityService.getAllEntities();
        return ResponseEntity.ok(allBusinessLogicRuleEntity);
    }

    @GetMapping(value = "/get-rule-entity/{entity}")
    public ResponseEntity<?> getRuleEntityByEntityName(@PathVariable("entity") String entityName) {
        BusinessLogicRuleEntity businessLogicRuleEntity = businessRuleEntityService.getEntityByEntityName(entityName);
        return ResponseEntity.ok(businessLogicRuleEntity);
    }

    @PostMapping(value = "/update-rule-entity")
    public ResponseEntity<?> updateRuleEntity(@RequestBody BusinessLogicRuleEntity businessLogicRuleEntity) {

        BusinessLogicRuleEntity businessLogicRuleEntityUpdated = businessRuleEntityService.saveRuleEntity(businessLogicRuleEntity);
        return ResponseEntity.ok(businessLogicRuleEntityUpdated);
    }

    @PostMapping(value = "/update-all-rule-entity")
    public ResponseEntity<?> updateRuleEntities(@RequestBody List<BusinessLogicRuleEntity> businessLogicRuleEntities) {

        List<BusinessLogicRuleEntity> ruleEntitiesUpdated = businessRuleEntityService.saveRuleEntities(businessLogicRuleEntities);
        return ResponseEntity.ok(ruleEntitiesUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-entity/{entity}")
    public ResponseEntity<?> removeRuleEntityByEntityName(@PathVariable("entity") String entityName) {
        List<BusinessLogicRuleEntity> allRemainingBusinessLogicRuleEntities =
                businessRuleEntityService.removeRuleEntityByEntityName(entityName);
        return ResponseEntity.ok(allRemainingBusinessLogicRuleEntities);
    }

    @GetMapping(value = "/get-all-rule-type")
    public ResponseEntity<?> getAllRuleType() {
        List<BusinessLogicRuleType> allBusinessLogicRuleType = businessRuleTypesService.getAllRuleTypes();
        return ResponseEntity.ok(allBusinessLogicRuleType);
    }

    @GetMapping(value = "/get-rule-type/{ruleTypeId}")
    public ResponseEntity<?> getRuleTypeByTypeId(@PathVariable("ruleTypeId") String ruleTypeId) {
        List<BusinessLogicRuleType> businessLogicRuleTypeList = businessRuleTypesService.getAllByRuleType(ruleTypeId);
        return ResponseEntity.ok(businessLogicRuleTypeList);
    }

    @GetMapping(value = "/get-rule-type/{entity}/{ruleTypeId}")
    public ResponseEntity<?> getRuleTypeByTypeId(@PathVariable("entity") String entity,
                                                 @PathVariable("ruleTypeId") String ruleTypeId) {
        BusinessLogicRuleType businessLogicRuleType = businessRuleTypesService.getRuleTypeByEntityAndRuleType(entity, ruleTypeId);
        return ResponseEntity.ok(businessLogicRuleType);
    }

    @PostMapping(value = "/update-rule-type")
    public ResponseEntity<?> updateRuleType(@RequestBody BusinessLogicRuleType businessLogicRuleType) {

        BusinessLogicRuleType businessLogicRuleTypeUpdated = businessRuleTypesService.saveRuleType(businessLogicRuleType);
        return ResponseEntity.ok(businessLogicRuleTypeUpdated);
    }

    @PostMapping(value = "/update-all-rule-type")
    public ResponseEntity<?> updateRuleType(@RequestBody List<BusinessLogicRuleType> businessLogicRuleTypes) {

        List<BusinessLogicRuleType> rulesUpdated = businessRuleTypesService.saveRuleTypes(businessLogicRuleTypes);
        return ResponseEntity.ok(rulesUpdated);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-type/{entity}/{ruleTypeId}")
    public ResponseEntity<?> removeRuleTypeByTypeId(@PathVariable("entity") String entity,
                                                    @PathVariable("ruleTypeId") String ruleTypeId) {
        List<BusinessLogicRuleType> allRemainingBusinessLogicRuleTypes = businessRuleTypesService.removeRuleTypesByEntityAndTypeId(entity, ruleTypeId);
        return ResponseEntity.ok(allRemainingBusinessLogicRuleTypes);
    }

    @GetMapping(value = "/get-rule/{ruleId}")
    public ResponseEntity<?> getRuleById(@PathVariable("ruleId") String ruleId) {
        BusinessLogicRule businessLogicRule = businessRulesService.getRuleById(ruleId);
        return ResponseEntity.ok(businessLogicRule);
    }

    @GetMapping(value = "/get-all-rules/{entity}/{ruleType}")
    public ResponseEntity<?> getAllRulesByEntityAndType(@PathVariable("entity") String entity,
                                                        @PathVariable("ruleType") String ruleType) {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllRulesByEntityAndType(entity, ruleType);
        return ResponseEntity.ok(allBusinessLogicRules);
    }

    @GetMapping(value = "/get-all-rules/{entity}")
    public ResponseEntity<?> getAllEntityRules(@PathVariable("entity") String entity) {
        List<BusinessLogicRule> allBusinessLogicRules = businessRulesService.getAllRulesByEntity(entity);
        return ResponseEntity.ok(allBusinessLogicRules);
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

    @PostMapping(value = "/update-rule")
    public ResponseEntity<?> updateRule(@RequestBody BusinessLogicRule businessLogicRule) {
        BusinessLogicRule businessLogicRuleUpdated = businessRulesService.saveRule(businessLogicRule);

        /*String condInitTemplateFunId = businessLogicRule.getCondInitTemplate();
        if( condInitTemplateFunId != null ) {
            BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(condInitTemplateFunId);
            if( businessLogicRuleFunctionTemplate != null )
                ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
        }

        String actionInitTemplateFunId = businessLogicRule.getActionInitTemplate();
        if( actionInitTemplateFunId != null ) {
            BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(actionInitTemplateFunId);
            if( businessLogicRuleFunctionTemplate != null )
                ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
        }

        String actionFinalTemplateFunId = businessLogicRule.getActionFinalTemplate();
        if( actionFinalTemplateFunId != null ) {
            BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(actionFinalTemplateFunId);
            if( businessLogicRuleFunctionTemplate != null )
                ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
        }*/

        return ResponseEntity.ok(businessLogicRuleUpdated);
    }

    @PostMapping(value = "/update-all-rules")
    public ResponseEntity<?> updateRule(@RequestBody List<BusinessLogicRule> businessLogicRules) {

        List<BusinessLogicRule> rulesUpdated = businessRulesService.saveRules(businessLogicRules);

        /*for ( BusinessLogicRule businessLogicRule : businessLogicRules ) {
            String condInitTemplateFunId = businessLogicRule.getCondInitTemplate();
            if( condInitTemplateFunId != null ) {
                BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(condInitTemplateFunId);
                if( businessLogicRuleFunctionTemplate != null )
                    ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
            }

            String actionInitTemplateFunId = businessLogicRule.getActionInitTemplate();
            if( actionInitTemplateFunId != null ) {
                BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(actionInitTemplateFunId);
                if( businessLogicRuleFunctionTemplate != null )
                    ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
            }

            String actionFinalTemplateFunId = businessLogicRule.getActionFinalTemplate();
            if( actionFinalTemplateFunId != null ) {
                BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(actionFinalTemplateFunId);
                if( businessLogicRuleFunctionTemplate != null )
                    ruleEngine.compileTemplateFunction(businessLogicRuleFunctionTemplate.getFunctionId(), businessLogicRuleFunctionTemplate.getFunctionLogic());
            }
        }*/

        return ResponseEntity.ok(rulesUpdated);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule/{ruleId}")
    public ResponseEntity<?> removeRuleById(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRule> allRemainingBusinessLogicRules = businessRulesService.removeRuleById(ruleId);
        return ResponseEntity.ok(allRemainingBusinessLogicRules);
    }

    @GetMapping(value = "/get-rule-conditions/{ruleId}")
    public ResponseEntity<?> getRuleConditionById(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRuleCondition> businessLogicRuleConditions = businessRuleConditionService.getRuleConditionsByRuleId(ruleId);
        return ResponseEntity.ok(businessLogicRuleConditions);
    }

    @PostMapping(value = "/update-rule-conditions")
    public ResponseEntity<?> updateRuleConditions(@RequestBody List<BusinessLogicRuleCondition> businessLogicRuleConditions) {
        List<BusinessLogicRuleCondition> ruleConditionsUpdated = businessRuleConditionService.saveRuleConditions(businessLogicRuleConditions);
        return ResponseEntity.ok(ruleConditionsUpdated);
    }

    @GetMapping(value = "/get-rule-actions/{ruleId}")
    public ResponseEntity<?> getRuleActionByID(@PathVariable("ruleId") String ruleId) {
        List<BusinessLogicRuleAction> businessLogicRuleActions = businessRuleActionService.getRuleActionsByRuleId(ruleId);
        return ResponseEntity.ok(businessLogicRuleActions);
    }

    @PostMapping(value = "/update-rule-actions")
    public ResponseEntity<?> updateRuleActions(@RequestBody List<BusinessLogicRuleAction> businessLogicRuleActions) {
        List<BusinessLogicRuleAction> ruleActionsUpdated = businessRuleActionService.saveRuleActions(businessLogicRuleActions);
        return ResponseEntity.ok(ruleActionsUpdated);
    }

    @GetMapping(value = "/get-all-functions")
    public ResponseEntity<?> getAllRuleFunction() {
        List<BusinessLogicRuleFunctionTemplate> allBusinessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getAllRuleFunction();
        return ResponseEntity.ok(allBusinessLogicRuleFunctionTemplate);
    }

    @GetMapping(value = "/get-function/{functionId}")
    public ResponseEntity<?> getRuleFunction(@PathVariable("functionId") String functionId) {
        BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate = businessRuleFunctionTemplateService.getRuleFunction(functionId);
        return ResponseEntity.ok(businessLogicRuleFunctionTemplate);
    }

    @PostMapping(value = "/update-rule-function")
    public ResponseEntity<?> updateRuleFunction(@RequestBody BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate) {
        BusinessLogicRuleFunctionTemplate ruleFunctionUpdated = businessRuleFunctionTemplateService.saveRuleFunction(businessLogicRuleFunctionTemplate);

        if( ruleFunctionUpdated != null && ruleFunctionUpdated.getFunctionParameters() == null )
            ruleEngine.compileTemplateFunction(ruleFunctionUpdated.getFunctionId(), ruleFunctionUpdated.getFunctionLogic());

        return ResponseEntity.ok(ruleFunctionUpdated);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-function/{functionId}")
    public ResponseEntity<?> removeRuleFunctionById(@PathVariable("functionId") String functionId) {
        List<BusinessLogicRuleFunctionTemplate> allRemainingRuleFunctions = businessRuleFunctionTemplateService.removeRuleFunctionById(functionId);
        return ResponseEntity.ok(allRemainingRuleFunctions);
    }

    @GetMapping(value = "/get-all-field-list")
    public ResponseEntity<?> getAllRuleFieldList() {
        List<BusinessLogicRuleFieldList> allBusinessLogicRuleFieldList = businessRuleFieldListService.getAllRuleFieldList();
        return ResponseEntity.ok(allBusinessLogicRuleFieldList);
    }

    @GetMapping(value = "/get-rule-fields/{ruleType}")
    public ResponseEntity<?> getRuleFieldsByRuleType(@PathVariable("ruleType") String ruleType) {
        List<BusinessLogicRuleFieldList> businessLogicRuleFieldListByRuleType = businessRuleFieldListService.getRuleFieldListByRuleType(ruleType);
        return ResponseEntity.ok(businessLogicRuleFieldListByRuleType);
    }

    @GetMapping(value = "/get-rule-fields/{ruleType}/{fieldName}")
    public ResponseEntity<?> getRuleFieldsByFieldName(@PathVariable("ruleType") String ruleType, @PathVariable("fieldName") String fieldName) {
        BusinessLogicRuleFieldList businessLogicRuleFieldList = businessRuleFieldListService.getRuleFieldByRuleTypeAndFieldName(ruleType, fieldName);
        return ResponseEntity.ok(businessLogicRuleFieldList);
    }

    @PostMapping(value = "/update-field-list")
    public ResponseEntity<?> updateRuleField(@RequestBody BusinessLogicRuleFieldList businessLogicRuleField) {
        BusinessLogicRuleFieldList businessLogicRuleFieldListUpdated = businessRuleFieldListService.saveRuleField(businessLogicRuleField);
        return ResponseEntity.ok(businessLogicRuleFieldListUpdated);
    }

    @PostMapping(value = "/update-all-field-list")
    public ResponseEntity<?> updateRuleFields(@RequestBody List<BusinessLogicRuleFieldList> businessLogicRuleFieldList) {
        List<BusinessLogicRuleFieldList> ruleFieldListUpdated = businessRuleFieldListService.saveRuleFieldList(businessLogicRuleFieldList);
        return ResponseEntity.ok(ruleFieldListUpdated);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-field-list/{ruleType}/{fieldName}")
    public ResponseEntity<?> removeRuleFieldByFieldId(@PathVariable("ruleType") String ruleType, @PathVariable("fieldName") String fieldName) {
        List<BusinessLogicRuleFieldList> allRemainingBusinessLogicRuleFieldList = businessRuleFieldListService.removeRuleFieldByFieldId(ruleType, fieldName);
        return ResponseEntity.ok(allRemainingBusinessLogicRuleFieldList);
    }


    @GetMapping(value = "/get-all-rule-values")
    public ResponseEntity<?> getAllRuleValueList() {
        List<BusinessLogicRuleValueList> allBusinessLogicRuleValueList = businessRuleValueListService.getAllRuleValueList();
        return ResponseEntity.ok(allBusinessLogicRuleValueList);
    }

    @GetMapping(value = "/get-rule-values/{dataType}")
    public ResponseEntity<?> getRuleValuesByDataType(@PathVariable("dataType") String dataType) {
        List<BusinessLogicRuleValueList> businessLogicRuleValueListByDataType = businessRuleValueListService.getRuleValueListByDataType(dataType);
        return ResponseEntity.ok(businessLogicRuleValueListByDataType);
    }

    @GetMapping(value = "/get-rule-values/{dataType}/{keyField}")
    public ResponseEntity<?> getRuleValueByFieldId(@PathVariable("dataType") String dataType, @PathVariable("keyField") String keyField) {
        BusinessLogicRuleValueList businessLogicRuleValueList = businessRuleValueListService.getRuleValueByDataTypeAndKeyField(dataType, keyField);
        return ResponseEntity.ok(businessLogicRuleValueList);
    }

    @PostMapping(value = "/update-rule-value")
    public ResponseEntity<?> updateRuleValue(@RequestBody BusinessLogicRuleValueList businessLogicRuleValue) {
        BusinessLogicRuleValueList businessLogicRuleValueListUpdated = businessRuleValueListService.saveRuleValue(businessLogicRuleValue);
        return ResponseEntity.ok(businessLogicRuleValueListUpdated);
    }

    @PostMapping(value = "/update-all-rule-values")
    public ResponseEntity<?> updateRuleValues(@RequestBody List<BusinessLogicRuleValueList> businessLogicRuleValueList) {
        List<BusinessLogicRuleValueList> ruleValueListUpdated = businessRuleValueListService.saveRuleValuesList(businessLogicRuleValueList);
        return ResponseEntity.ok(ruleValueListUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-value-list/{dataType}/{keyField}")
    public ResponseEntity<?> removeRuleValueByFieldId(@PathVariable("dataType") String dataType, @PathVariable("keyField") String keyField) {
        List<BusinessLogicRuleValueList> allRemainingBusinessLogicRuleValueList = businessRuleValueListService.removeRuleValueByFieldId(dataType, keyField);
        return ResponseEntity.ok(allRemainingBusinessLogicRuleValueList);
    }

    @GetMapping(value = "/get-all-rule-process-profiles")
    public ResponseEntity<?> getAllRuleProcessProfile() {
        List<BusinessLogicRuleProcessProfile> allBusinessLogicRuleProcessProfiles = businessRuleProcessProfileService.getAllProcessProfiles();
        return ResponseEntity.ok(allBusinessLogicRuleProcessProfiles);
    }

    @GetMapping(value = "/get-rule-process-profile/{entityName}/{profileName}")
    public ResponseEntity<?> getRuleProcessProfileByProfileName(@PathVariable("entityName") String entityName,
                                                                @PathVariable("profileName") String profileName) {
        BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile =
                businessRuleProcessProfileService.getProcessProfileByEntityAndProfileName(entityName, profileName);
        return ResponseEntity.ok(businessLogicRuleProcessProfile);
    }

    @PostMapping(value = "/update-rule-process-profile")
    public ResponseEntity<?> updateRuleProcessProfile(@RequestBody BusinessLogicRuleProcessProfile businessLogicRuleProcessProfile) {

        BusinessLogicRuleProcessProfile businessLogicRuleProcessProfileUpdated =
                businessRuleProcessProfileService.saveRuleProcessProfile(businessLogicRuleProcessProfile);
        return ResponseEntity.ok(businessLogicRuleProcessProfileUpdated);
    }

    @PostMapping(value = "/update-all-rule-process-profiles")
    public ResponseEntity<?> updateRuleProcessProfiles(@RequestBody List<BusinessLogicRuleProcessProfile> businessLogicRuleProcessProfiles) {

        List<BusinessLogicRuleProcessProfile> ruleProcessProfilesUpdated =
                businessRuleProcessProfileService.saveRuleProcessProfiles(businessLogicRuleProcessProfiles);
        return ResponseEntity.ok(ruleProcessProfilesUpdated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/remove-rule-process-profile/{entityName}/{profileName}")
    public ResponseEntity<?> removeRuleProcessProfileByEntityName(@PathVariable("entityName") String entityName,
                                                                  @PathVariable("profileName") String profileName) {
        List<BusinessLogicRuleProcessProfile> allRemainingBusinessLogicRuleProcessProfiles =
                businessRuleProcessProfileService.removeRuleProcessProfileByEntityAndProfileName(entityName, profileName);
        return ResponseEntity.ok(allRemainingBusinessLogicRuleProcessProfiles);
    }

}
