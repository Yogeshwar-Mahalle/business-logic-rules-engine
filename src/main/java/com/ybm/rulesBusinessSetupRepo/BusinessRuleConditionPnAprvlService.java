/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.google.common.base.Enums;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleConditionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleConditionPnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleCondition;
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

        OperandType leftOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionPnAprvlDbModel.getLeftOperandType().toUpperCase())
                .or(OperandType.EXCHANGE);
        OperandType rightOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionPnAprvlDbModel.getRightOperandType().toUpperCase())
                .or(OperandType.EXCHANGE);
        Operator operator = Enums.getIfPresent(Operator.class, blRuleConditionPnAprvlDbModel.getOperator().toUpperCase())
                .or(Operator.EQUAL);
        LogicalOperator logicalOperator = Enums.getIfPresent(LogicalOperator.class, blRuleConditionPnAprvlDbModel.getLogicalOperator().toUpperCase())
                .or(LogicalOperator.AND);

        return BusinessLogicRuleCondition.builder()
                .ruleConditionId(blRuleConditionPnAprvlDbModel.getRuleConditionId())
                .parentRuleConditionId(blRuleConditionPnAprvlDbModel.getParentRuleConditionId())
                .ruleId(blRuleConditionPnAprvlDbModel.getRuleId())
                .sequenceNumber(blRuleConditionPnAprvlDbModel.getSequenceNumber())
                .openConditionScope(blRuleConditionPnAprvlDbModel.getOpenConditionScope())
                .leftDataObject(blRuleConditionPnAprvlDbModel.getLeftDataObject())
                .leftOperand(blRuleConditionPnAprvlDbModel.getLeftOperand())
                .leftOperandType(leftOperandType)
                .operator(operator)
                .rightDataObject(blRuleConditionPnAprvlDbModel.getRightDataObject())
                .rightOperand(blRuleConditionPnAprvlDbModel.getRightOperand())
                .rightOperandType(rightOperandType)
                .closeConditionScope(blRuleConditionPnAprvlDbModel.getCloseConditionScope())
                .logicalOperator(logicalOperator)
                .createTimeStamp(blRuleConditionPnAprvlDbModel.getCreateTimeStamp() == null ? new Date() : blRuleConditionPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

    private BLRuleConditionPnAprvlDbModel mapRuleConditionPnAprvlToDbModel(BusinessLogicRuleCondition businessLogicRuleCondition){

        return BLRuleConditionPnAprvlDbModel.builder()
                .ruleConditionId( businessLogicRuleCondition.getRuleConditionId() == null ?
                        businessLogicRuleCondition.getParentRuleConditionId() == null ? businessLogicRuleCondition.getRuleId() : businessLogicRuleCondition.getParentRuleConditionId()
                                + "~" + businessLogicRuleCondition.getSequenceNumber() :
                        businessLogicRuleCondition.getRuleConditionId() )
                .parentRuleConditionId(businessLogicRuleCondition.getParentRuleConditionId())
                .ruleId(businessLogicRuleCondition.getRuleId())
                .sequenceNumber(businessLogicRuleCondition.getSequenceNumber())
                .openConditionScope(businessLogicRuleCondition.getOpenConditionScope())
                .leftDataObject(businessLogicRuleCondition.getLeftDataObject())
                .leftOperand(businessLogicRuleCondition.getLeftOperand())
                .leftOperandType(businessLogicRuleCondition.getLeftOperandType().name())
                .operator(businessLogicRuleCondition.getOperator().name())
                .rightDataObject(businessLogicRuleCondition.getRightDataObject())
                .rightOperand(businessLogicRuleCondition.getRightOperand())
                .rightOperandType(businessLogicRuleCondition.getRightOperandType().name())
                .closeConditionScope(businessLogicRuleCondition.getCloseConditionScope())
                .logicalOperator(businessLogicRuleCondition.getLogicalOperator().name())
                .createTimeStamp(businessLogicRuleCondition.getCreateTimeStamp())
                .updateTimeStamp(businessLogicRuleCondition.getUpdateTimeStamp())
                .build();

    }

}
