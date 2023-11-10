/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleTypesRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypesDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleTypes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleTypesService {

    @Autowired
    private BLRuleTypesRepository blRuleTypesRepository;

    public List<BusinessLogicRuleTypes> getAllRuleTypes() {
        return blRuleTypesRepository.findAll().stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleTypes getRuleType(String ruleType) {
        Optional<BLRuleTypesDbModel> blRuleTypesDbModel = blRuleTypesRepository.findById(ruleType);
        return blRuleTypesDbModel.map(this::mapRuleTypesFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleTypes saveRuleTypes(BusinessLogicRuleTypes businessLogicRuleTypes) {
        BLRuleTypesDbModel blRuleTypesDbModel = mapRuleTypesToDbModel(businessLogicRuleTypes);
        blRuleTypesDbModel = blRuleTypesRepository.save(blRuleTypesDbModel);
        return mapRuleTypesFromDbModel(blRuleTypesDbModel);
    }

    public List<BusinessLogicRuleTypes> removeRuleTypesById(String ruleType) {

        blRuleTypesRepository.deleteById(ruleType);

        return blRuleTypesRepository.findAll().stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleTypes mapRuleTypesFromDbModel(BLRuleTypesDbModel blRuleTypesDbModel){

        return BusinessLogicRuleTypes.builder()
                .ruleType(blRuleTypesDbModel.getRuleType())
                .description(blRuleTypesDbModel.getDescription())
                .complexRuleFlag(blRuleTypesDbModel.isComplexRuleFlag())
                .workflowRuleFlag(blRuleTypesDbModel.isWorkflowRuleFlag())
                .applyAllFlag(blRuleTypesDbModel.isApplyAllFlag())
                .status(blRuleTypesDbModel.getStatus())
                .createTimeStamp(blRuleTypesDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleTypesDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleTypesDbModel mapRuleTypesToDbModel(BusinessLogicRuleTypes businessLogicRuleTypes){

        return BLRuleTypesDbModel.builder()
                .ruleType(businessLogicRuleTypes.getRuleType())
                .description(businessLogicRuleTypes.getDescription())
                .complexRuleFlag(businessLogicRuleTypes.isComplexRuleFlag())
                .workflowRuleFlag(businessLogicRuleTypes.isWorkflowRuleFlag())
                .applyAllFlag(businessLogicRuleTypes.isApplyAllFlag())
                .status(businessLogicRuleTypes.getStatus())
                .createTimeStamp(businessLogicRuleTypes.getCreateTimeStamp() == null ? new Date() : businessLogicRuleTypes.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
