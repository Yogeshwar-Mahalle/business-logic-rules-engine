/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleDbModel;
//import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRulesRepositoryDummy;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRulesService {
    @Autowired
    private BLRulesRepository blRulesRepository;
    //private BLRulesRepositoryDummy blRulesRepository;

    @Transactional
    public BusinessLogicRule saveRule(BusinessLogicRule businessLogicRule) {

        BLRuleDbModel blRuleDbModel = mapRuleToDbModel(businessLogicRule);
        blRuleDbModel = blRulesRepository.save(blRuleDbModel);
        return mapRuleFromDbModel(blRuleDbModel);

    }

    @Transactional
    public List<BusinessLogicRule> saveRules(List<BusinessLogicRule> businessLogicRules) {

        List<BLRuleDbModel> listBLRuleDBModel = businessLogicRules.stream()
                .map(
                        this::mapRuleToDbModel
                )
                .collect(Collectors.toList());

        return blRulesRepository.saveAll(listBLRuleDBModel).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRule> removeRuleById(String ruleId){
        if( ruleId == null )
            return null;

        blRulesRepository.deleteById(ruleId);

        return blRulesRepository.findAll().stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRule> getAllRules(){
        return blRulesRepository.findAll().stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());
    }


    public BusinessLogicRule getRuleById(String ruleId){
        if( ruleId == null )
            return null;

        Optional<BLRuleDbModel> ruleDbModel = blRulesRepository.findById(ruleId);
        return ruleDbModel.map(this::mapRuleFromDbModel).orElse(null);
    }


    public List<BusinessLogicRule> getAllRulesByType(String ruleType){
        if( ruleType == null )
            return null;

        return blRulesRepository.findByRuleType(ruleType).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRule> getAllEntityRules(String entity){
        if( entity == null )
            return null;

        return blRulesRepository.findByLinkedEntity(entity).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

    }


    public List<BusinessLogicRule> getAllEntityRulesByType(String entity, String ruleType){
        if( entity == null || ruleType == null)
            return null;

        return blRulesRepository.findByLinkedEntityAndRuleType(entity, ruleType).stream()
                .map(
                        this::mapRuleFromDbModel
                )
                .collect(Collectors.toList());

    }

    private BusinessLogicRule mapRuleFromDbModel(BLRuleDbModel blRuleDbModel){

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

    private BLRuleDbModel mapRuleToDbModel(BusinessLogicRule businessLogicRule){

        return BLRuleDbModel.builder()
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
