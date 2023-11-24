/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleDbModel;
import com.ybm.rulesBusinessSetupRepo.entities.BLRulePnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRulesPnAprvlService {
    @Autowired
    private BLRulesPnAprvlRepository blRulesPnAprvlRepository;

    @Transactional
    public BusinessLogicRule saveRule(BusinessLogicRule businessLogicRule) {

        BLRulePnAprvlDbModel blRulePnAprvlDbModel = mapRuleToPnAprvlDbModel(businessLogicRule);
        blRulePnAprvlDbModel = blRulesPnAprvlRepository.save(blRulePnAprvlDbModel);
        return mapRuleFromPnAprvlDbModel(blRulePnAprvlDbModel);

    }

    @Transactional
    public List<BusinessLogicRule> saveRules(List<BusinessLogicRule> businessLogicRules) {

        List<BLRulePnAprvlDbModel> listBLRulePnAprvlDBModel = businessLogicRules.stream()
                .map(
                        this::mapRuleToPnAprvlDbModel
                )
                .collect(Collectors.toList());

        return blRulesPnAprvlRepository.saveAll(listBLRulePnAprvlDBModel).stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRule> removeRuleById(String ruleId){
        if( ruleId == null )
            return null;

        blRulesPnAprvlRepository.deleteById(ruleId);

        return blRulesPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRule> getAllRules(){
        return blRulesPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());
    }


    public BusinessLogicRule getRuleById(String ruleId){
        if( ruleId == null )
            return null;

        Optional<BLRulePnAprvlDbModel> ruleDbModel = blRulesPnAprvlRepository.findById(ruleId);
        return ruleDbModel.map(this::mapRuleFromPnAprvlDbModel).orElse(null);
    }


    public List<BusinessLogicRule> getAllRulesByType(String ruleType){
        if( ruleType == null )
            return null;

        return blRulesPnAprvlRepository.findByRuleType(ruleType).stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRule> getAllEntityRules(String entity){
        if( entity == null )
            return null;

        return blRulesPnAprvlRepository.findByLinkedEntity(entity).stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());

    }


    public List<BusinessLogicRule> getAllEntityRulesByType(String entity, String ruleType){
        if( entity == null || ruleType == null)
            return null;

        return blRulesPnAprvlRepository.findByLinkedEntityAndRuleType(entity, ruleType).stream()
                .map(
                        this::mapRuleFromPnAprvlDbModel
                )
                .collect(Collectors.toList());

    }

    private BusinessLogicRule mapRuleFromPnAprvlDbModel(BLRulePnAprvlDbModel blRulePnAprvlDbModel){

        return BusinessLogicRule.builder()
                .ruleType(blRulePnAprvlDbModel.getRuleType().toUpperCase())
                .ruleId(blRulePnAprvlDbModel.getRuleId())
                .linkedEntity(blRulePnAprvlDbModel.getLinkedEntity())
                .ruleName(blRulePnAprvlDbModel.getRuleName())
                .condInclFuncNameList(blRulePnAprvlDbModel.getCondInclFuncNameList())
                .condInitTemplate(blRulePnAprvlDbModel.getCondInitTemplate())
                .condition(blRulePnAprvlDbModel.getCondition())
                .actionInclFuncNameList(blRulePnAprvlDbModel.getActionInclFuncNameList())
                .actionInitTemplate(blRulePnAprvlDbModel.getActionInitTemplate())
                .action(blRulePnAprvlDbModel.getAction())
                .actionFinalTemplate(blRulePnAprvlDbModel.getActionFinalTemplate())
                .description(blRulePnAprvlDbModel.getDescription())
                .priority(blRulePnAprvlDbModel.getPriority())
                .status(blRulePnAprvlDbModel.getStatus())
                .createTimeStamp(blRulePnAprvlDbModel.getCreateTimeStamp())
                .effectiveFromDate(blRulePnAprvlDbModel.getEffectiveFromDate())
                .updateTimeStamp(blRulePnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRulePnAprvlDbModel mapRuleToPnAprvlDbModel(BusinessLogicRule businessLogicRule){

        return BLRulePnAprvlDbModel.builder()
                .ruleType(businessLogicRule.getRuleType())
                .ruleId( businessLogicRule.getRuleId() == null ?
                        businessLogicRule.getRuleType() + "~" + businessLogicRule.getLinkedEntity() + "~" + businessLogicRule.getRuleName() :
                        businessLogicRule.getRuleId() )
                .linkedEntity(businessLogicRule.getLinkedEntity())
                .ruleName(businessLogicRule.getRuleName())
                .condInclFuncNameList(businessLogicRule.getCondInclFuncNameList())
                .condInitTemplate(businessLogicRule.getCondInitTemplate())
                .condition(businessLogicRule.getCondition())
                .actionInclFuncNameList(businessLogicRule.getActionInclFuncNameList())
                .actionInitTemplate(businessLogicRule.getActionInitTemplate())
                .action(businessLogicRule.getAction())
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
