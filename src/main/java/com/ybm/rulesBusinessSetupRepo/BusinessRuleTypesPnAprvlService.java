/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleTypePnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypePnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleTypesPnAprvlService {

    @Autowired
    private BLRuleTypePnAprvlRepository blRuleTypePnAprvlRepository;

    public List<BusinessLogicRuleType> getAllPnAprvlRuleTypes() {
        return blRuleTypePnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleType> getAllByRuleType(String ruleType) {
        return blRuleTypePnAprvlRepository.findByRuleType(ruleType).stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleType getRuleTypePnAprvlByEntityAndRuleType(String entity, String ruleType) {
        Optional<BLRuleTypePnAprvlDbModel> blRuleTypesPnAprvlDbModel = blRuleTypePnAprvlRepository.findByLinkedEntityAndRuleType(entity, ruleType);
        return blRuleTypesPnAprvlDbModel.map(this::mapRuleTypesPnAprvlFromDbModel).orElse(null);
    }

    public List<BusinessLogicRuleType> getAllRuleTypePnAprvlByEntityWrkFlowFlag(String entity) {
        return blRuleTypePnAprvlRepository.findByLinkedEntityAndWorkflowRuleFlag(entity, true).stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleType> getAllRuleTypeByWrkFlowFlag() {
        return blRuleTypePnAprvlRepository.findByWorkflowRuleFlag(true).stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public BusinessLogicRuleType saveRuleType(BusinessLogicRuleType businessLogicRuleType) {
        BLRuleTypePnAprvlDbModel blRuleTypePnAprvlDbModel = mapRuleTypesPnAprvlToDbModel(businessLogicRuleType);
        blRuleTypePnAprvlDbModel = blRuleTypePnAprvlRepository.save(blRuleTypePnAprvlDbModel);
        return mapRuleTypesPnAprvlFromDbModel(blRuleTypePnAprvlDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleType> saveRuleTypes(List<BusinessLogicRuleType> businessLogicRuleTypes) {

        List<BLRuleTypePnAprvlDbModel> listBLRuleTypePnAprvlDBModel = businessLogicRuleTypes.stream()
                .map(
                        this::mapRuleTypesPnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleTypePnAprvlRepository.saveAll(listBLRuleTypePnAprvlDBModel).stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public List<BusinessLogicRuleType> removeRuleTypesByEntityAndTypeId(String entity, String ruleType) {

        blRuleTypePnAprvlRepository.deleteByLinkedEntityAndRuleType(entity, ruleType);

        return blRuleTypePnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleTypesPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleType mapRuleTypesPnAprvlFromDbModel(BLRuleTypePnAprvlDbModel blRuleTypeDbModel){

        return BusinessLogicRuleType.builder()
                .ruleType(blRuleTypeDbModel.getRuleType())
                .linkedEntity(blRuleTypeDbModel.getLinkedEntity())
                .description(blRuleTypeDbModel.getDescription())
                .complexRuleFlag(blRuleTypeDbModel.getComplexRuleFlag())
                .workflowRuleFlag(blRuleTypeDbModel.getWorkflowRuleFlag())
                .systemRuleFlag(blRuleTypeDbModel.getSystemRuleFlag())
                .applyAllFlag(blRuleTypeDbModel.getApplyAllFlag())
                .status(StatusType.valueOf(blRuleTypeDbModel.getStatus()))
                .createTimeStamp(blRuleTypeDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleTypeDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleTypePnAprvlDbModel mapRuleTypesPnAprvlToDbModel(BusinessLogicRuleType businessLogicRuleType){

        return BLRuleTypePnAprvlDbModel.builder()
                .ruleType(businessLogicRuleType.getRuleType())
                .linkedEntity(businessLogicRuleType.getLinkedEntity())
                .description(businessLogicRuleType.getDescription())
                .complexRuleFlag(businessLogicRuleType.getComplexRuleFlag())
                .workflowRuleFlag(businessLogicRuleType.getWorkflowRuleFlag())
                .systemRuleFlag(businessLogicRuleType.getSystemRuleFlag())
                .applyAllFlag(businessLogicRuleType.getApplyAllFlag())
                .status(String.valueOf(businessLogicRuleType.getStatus()))
                .createTimeStamp(businessLogicRuleType.getCreateTimeStamp() == null ? new Date() : businessLogicRuleType.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
