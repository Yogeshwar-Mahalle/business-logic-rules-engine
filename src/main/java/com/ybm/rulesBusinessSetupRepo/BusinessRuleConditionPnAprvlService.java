/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.google.common.base.Enums;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleConditionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleConditionPnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessRuleConditionPnAprvlService {

    @Autowired
    private BLRuleConditionPnAprvlRepository blRuleConditionPnAprvlRepository;

    public List<BusinessLogicRuleCondition> getRuleConditionById(String ruleCondId) {
        if( ruleCondId == null )
            return null;

        return blRuleConditionPnAprvlRepository.findAllByRuleId(ruleCondId).stream()
                .map(
                        this::mapRuleConditionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> saveRuleConditions(List<BusinessLogicRuleCondition> businessLogicRuleConditions) {
        if(businessLogicRuleConditions == null)
            return null;

        List<BLRuleConditionPnAprvlDbModel> listBLRuleConditionPnAprvlDbModel = businessLogicRuleConditions.stream()
                .map(
                        this::mapRuleConditionPnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleConditionPnAprvlRepository.saveAll(listBLRuleConditionPnAprvlDbModel).stream()
                .map(
                        this::mapRuleConditionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> removeConditionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        blRuleConditionPnAprvlRepository.deleteByRuleId(ruleId);

        return blRuleConditionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleConditionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleCondition mapRuleConditionPnAprvlFromDbModel(BLRuleConditionPnAprvlDbModel blRuleConditionPnAprvlDbModel){

        ExchangeObjectType leftDataObject = null;
        if ( blRuleConditionPnAprvlDbModel.getLeftDataObject() != null )
            leftDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleConditionPnAprvlDbModel.getLeftDataObject().toUpperCase())
                    .orNull();

        ExchangeObjectType rightDataObject = null;
        if ( blRuleConditionPnAprvlDbModel.getRightDataObject() != null )
            rightDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleConditionPnAprvlDbModel.getRightDataObject().toUpperCase())
                    .orNull();

        OperandType leftOperandType = null;
        if ( blRuleConditionPnAprvlDbModel.getLeftOperandType() != null )
            leftOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionPnAprvlDbModel.getLeftOperandType().toUpperCase())
                    .orNull();

        OperandType rightOperandType = null;
        if ( blRuleConditionPnAprvlDbModel.getRightOperandType() != null )
            rightOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionPnAprvlDbModel.getRightOperandType().toUpperCase())
                    .orNull();

        Operator operator = null;
        if ( blRuleConditionPnAprvlDbModel.getOperator() != null )
            operator = Enums.getIfPresent(Operator.class, blRuleConditionPnAprvlDbModel.getOperator().toUpperCase())
                    .or(Operator.EQUAL);

        LogicalOperator logicalOperator = null;
        if ( blRuleConditionPnAprvlDbModel.getLogicalOperator() != null )
            logicalOperator = Enums.getIfPresent(LogicalOperator.class, blRuleConditionPnAprvlDbModel.getLogicalOperator().toUpperCase())
                    .orNull();

        return BusinessLogicRuleCondition.builder()
                .ruleConditionId(blRuleConditionPnAprvlDbModel.getRuleConditionId())
                .parentRuleConditionId(blRuleConditionPnAprvlDbModel.getParentRuleConditionId())
                .ruleId(blRuleConditionPnAprvlDbModel.getRuleId())
                .sequenceNumber(blRuleConditionPnAprvlDbModel.getSequenceNumber())
                .isNotIndicator(blRuleConditionPnAprvlDbModel.getIsNotIndicator())
                .openConditionScope(blRuleConditionPnAprvlDbModel.getOpenConditionScope())
                .leftDataObject(leftDataObject)
                .leftOperand(blRuleConditionPnAprvlDbModel.getLeftOperand())
                .leftOperandType(leftOperandType)
                .operator(operator)
                .rightDataObject(rightDataObject)
                .rightOperand(blRuleConditionPnAprvlDbModel.getRightOperand())
                .rightOperandType(rightOperandType)
                .closeConditionScope(blRuleConditionPnAprvlDbModel.getCloseConditionScope())
                .logicalOperator(logicalOperator)
                .includeFuncNameList(blRuleConditionPnAprvlDbModel.getIncludeFuncNameList())
                .createTimeStamp(blRuleConditionPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleConditionPnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleConditionPnAprvlDbModel mapRuleConditionPnAprvlToDbModel(BusinessLogicRuleCondition businessLogicRuleCondition){

        return BLRuleConditionPnAprvlDbModel.builder()
                .ruleConditionId( businessLogicRuleCondition.getRuleConditionId() == null ?
                        businessLogicRuleCondition.getRuleId() + "~" + businessLogicRuleCondition.getSequenceNumber() :
                        businessLogicRuleCondition.getRuleConditionId() )
                .parentRuleConditionId(businessLogicRuleCondition.getParentRuleConditionId())
                .ruleId(businessLogicRuleCondition.getRuleId())
                .sequenceNumber(businessLogicRuleCondition.getSequenceNumber())
                .isNotIndicator(businessLogicRuleCondition.getIsNotIndicator())
                .openConditionScope(businessLogicRuleCondition.getOpenConditionScope())
                .leftDataObject(businessLogicRuleCondition.getLeftDataObject() != null ?
                        businessLogicRuleCondition.getLeftDataObject().name() : null)
                .leftOperand(businessLogicRuleCondition.getLeftOperand())
                .leftOperandType(businessLogicRuleCondition.getLeftOperandType() != null ?
                        businessLogicRuleCondition.getLeftOperandType().name() : null)
                .operator(businessLogicRuleCondition.getOperator().name())
                .rightDataObject(businessLogicRuleCondition.getRightDataObject() != null ?
                        businessLogicRuleCondition.getRightDataObject().name() : null)
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
