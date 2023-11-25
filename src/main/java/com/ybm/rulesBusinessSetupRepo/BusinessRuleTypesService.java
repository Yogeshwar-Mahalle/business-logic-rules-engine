/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleTypeRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypeDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
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
    private BLRuleTypeRepository blRuleTypeRepository;

    public List<BusinessLogicRuleType> getAllRuleTypes() {
        return blRuleTypeRepository.findAll().stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleType getRuleType(String ruleType) {
        Optional<BLRuleTypeDbModel> blRuleTypesDbModel = blRuleTypeRepository.findByRuleType(ruleType);
        return blRuleTypesDbModel.map(this::mapRuleTypesFromDbModel).orElse(null);
    }

    public List<BusinessLogicRuleType> getAllRuleTypeByWrkFlowFlag() {
        return blRuleTypeRepository.findByWorkflowRuleFlag(true).stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public BusinessLogicRuleType saveRuleType(BusinessLogicRuleType businessLogicRuleType) {
        BLRuleTypeDbModel blRuleTypeDbModel = mapRuleTypesToDbModel(businessLogicRuleType);
        blRuleTypeDbModel = blRuleTypeRepository.save(blRuleTypeDbModel);
        return mapRuleTypesFromDbModel(blRuleTypeDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleType> saveRuleTypes(List<BusinessLogicRuleType> businessLogicRuleTypes) {

        List<BLRuleTypeDbModel> listBLRuleTypeDBModel = businessLogicRuleTypes.stream()
                .map(
                        this::mapRuleTypesToDbModel
                )
                .collect(Collectors.toList());

        return blRuleTypeRepository.saveAll(listBLRuleTypeDBModel).stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRuleType> removeRuleTypesById(String ruleType) {

        blRuleTypeRepository.deleteById(ruleType);

        return blRuleTypeRepository.findAll().stream()
                .map(
                        this::mapRuleTypesFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleType mapRuleTypesFromDbModel(BLRuleTypeDbModel blRuleTypeDbModel){

        return BusinessLogicRuleType.builder()
                .ruleType(blRuleTypeDbModel.getRuleType())
                .linkedEntity(blRuleTypeDbModel.getLinkedEntity())
                .description(blRuleTypeDbModel.getDescription())
                .complexRuleFlag(blRuleTypeDbModel.isComplexRuleFlag())
                .workflowRuleFlag(blRuleTypeDbModel.isWorkflowRuleFlag())
                .systemRuleFlag(blRuleTypeDbModel.isSystemRuleFlag())
                .applyAllFlag(blRuleTypeDbModel.isApplyAllFlag())
                .status(blRuleTypeDbModel.getStatus())
                .createTimeStamp(blRuleTypeDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleTypeDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleTypeDbModel mapRuleTypesToDbModel(BusinessLogicRuleType businessLogicRuleType){

        return BLRuleTypeDbModel.builder()
                .ruleType(businessLogicRuleType.getRuleType())
                .linkedEntity(businessLogicRuleType.getLinkedEntity())
                .description(businessLogicRuleType.getDescription())
                .complexRuleFlag(businessLogicRuleType.isComplexRuleFlag())
                .workflowRuleFlag(businessLogicRuleType.isWorkflowRuleFlag())
                .systemRuleFlag(businessLogicRuleType.isSystemRuleFlag())
                .applyAllFlag(businessLogicRuleType.isApplyAllFlag())
                .status(businessLogicRuleType.getStatus())
                .createTimeStamp(businessLogicRuleType.getCreateTimeStamp() == null ? new Date() : businessLogicRuleType.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
