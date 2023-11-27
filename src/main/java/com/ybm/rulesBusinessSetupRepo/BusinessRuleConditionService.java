/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.google.common.base.Enums;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleConditionRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleConditionDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleCondition;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessRuleConditionService {

    @Autowired
    private BLRuleConditionRepository blRuleConditionRepository;

    public List<BusinessLogicRuleCondition> getRuleConditionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        return blRuleConditionRepository.findAllByRuleId(ruleId).stream()
                .map(
                        this::mapRuleConditionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> saveRuleConditions(String ruleId, List<BusinessLogicRuleCondition> businessLogicRuleConditions) {
        if(businessLogicRuleConditions == null)
            return null;

        List<BLRuleConditionDbModel> listBLRuleConditionDbModel = businessLogicRuleConditions.stream()
                .map(
                        blRuleConditionDbModel -> {
                            blRuleConditionDbModel.setRuleId(ruleId);
                            return mapRuleConditionToDbModel(blRuleConditionDbModel);
                        }
                )
                .collect(Collectors.toList());

        return blRuleConditionRepository.saveAll(listBLRuleConditionDbModel).stream()
                .map(
                        this::mapRuleConditionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> saveRuleConditions(List<BusinessLogicRuleCondition> businessLogicRuleConditions) {
        if(businessLogicRuleConditions == null)
            return null;

        List<BLRuleConditionDbModel> listBLRuleConditionDbModel = businessLogicRuleConditions.stream()
                .map(
                        this::mapRuleConditionToDbModel
                )
                .collect(Collectors.toList());

        return blRuleConditionRepository.saveAll(listBLRuleConditionDbModel).stream()
                .map(
                        this::mapRuleConditionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> removeConditionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        blRuleConditionRepository.deleteByRuleId(ruleId);

        return blRuleConditionRepository.findAll().stream()
                .map(
                        this::mapRuleConditionFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleCondition mapRuleConditionFromDbModel(BLRuleConditionDbModel blRuleConditionDbModel){

        OperandType leftOperandType = null;
        if ( blRuleConditionDbModel.getLeftOperandType() != null )
            leftOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionDbModel.getLeftOperandType().toUpperCase())
                .orNull();

        OperandType rightOperandType = null;
        if ( blRuleConditionDbModel.getRightOperandType() != null )
            rightOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionDbModel.getRightOperandType().toUpperCase())
                .orNull();

        Operator operator = null;
        if ( blRuleConditionDbModel.getOperator() != null )
            operator = Enums.getIfPresent(Operator.class, blRuleConditionDbModel.getOperator().toUpperCase())
                .or(Operator.EQUAL);

        LogicalOperator logicalOperator = null;
        if ( blRuleConditionDbModel.getLogicalOperator() != null )
            logicalOperator = Enums.getIfPresent(LogicalOperator.class, blRuleConditionDbModel.getLogicalOperator().toUpperCase())
                    .orNull();

        return BusinessLogicRuleCondition.builder()
                .ruleConditionId(blRuleConditionDbModel.getRuleConditionId())
                .parentRuleConditionId(blRuleConditionDbModel.getParentRuleConditionId())
                .ruleId(blRuleConditionDbModel.getRuleId())
                .sequenceNumber(blRuleConditionDbModel.getSequenceNumber())
                .openConditionScope(blRuleConditionDbModel.getOpenConditionScope())
                .leftDataObject(blRuleConditionDbModel.getLeftDataObject())
                .leftOperand(blRuleConditionDbModel.getLeftOperand())
                .leftOperandType(leftOperandType)
                .operator(operator)
                .rightDataObject(blRuleConditionDbModel.getRightDataObject())
                .rightOperand(blRuleConditionDbModel.getRightOperand())
                .rightOperandType(rightOperandType)
                .closeConditionScope(blRuleConditionDbModel.getCloseConditionScope())
                .logicalOperator(logicalOperator)
                .includeFuncNameList(blRuleConditionDbModel.getIncludeFuncNameList())
                .createTimeStamp(blRuleConditionDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleConditionDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleConditionDbModel mapRuleConditionToDbModel(BusinessLogicRuleCondition businessLogicRuleCondition){

        return BLRuleConditionDbModel.builder()
                .ruleConditionId( businessLogicRuleCondition.getRuleConditionId() == null ?
                        businessLogicRuleCondition.getRuleId() + "~" + businessLogicRuleCondition.getSequenceNumber() :
                        businessLogicRuleCondition.getRuleConditionId() )
                .parentRuleConditionId(businessLogicRuleCondition.getParentRuleConditionId())
                .ruleId(businessLogicRuleCondition.getRuleId())
                .sequenceNumber(businessLogicRuleCondition.getSequenceNumber())
                .openConditionScope(businessLogicRuleCondition.getOpenConditionScope())
                .leftDataObject(businessLogicRuleCondition.getLeftDataObject())
                .leftOperand(businessLogicRuleCondition.getLeftOperand())
                .leftOperandType(businessLogicRuleCondition.getLeftOperandType() != null ?
                        businessLogicRuleCondition.getLeftOperandType().name() : null)
                .operator(businessLogicRuleCondition.getOperator().name())
                .rightDataObject(businessLogicRuleCondition.getRightDataObject())
                .rightOperand(businessLogicRuleCondition.getRightOperand())
                .rightOperandType(businessLogicRuleCondition.getRightOperandType() != null ?
                        businessLogicRuleCondition.getRightOperandType().name() : null)
                .closeConditionScope(businessLogicRuleCondition.getCloseConditionScope())
                .logicalOperator(businessLogicRuleCondition.getLogicalOperator() != null ?
                        businessLogicRuleCondition.getLogicalOperator().name() : null)
                .includeFuncNameList(businessLogicRuleCondition.getIncludeFuncNameList())
                .createTimeStamp(businessLogicRuleCondition.getCreateTimeStamp() == null ? new Date() : businessLogicRuleCondition.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }


}
