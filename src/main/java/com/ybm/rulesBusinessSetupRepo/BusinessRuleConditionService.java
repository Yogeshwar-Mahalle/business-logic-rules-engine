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

    public List<BusinessLogicRuleCondition> getRuleConditionById(String ruleId) {
        return blRuleConditionRepository.findAllByRuleId(ruleId).stream()
                .map(
                        this::mapRuleConditionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleCondition> saveRuleConditions(List<BusinessLogicRuleCondition> businessLogicRuleConditions) {
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


    private BusinessLogicRuleCondition mapRuleConditionFromDbModel(BLRuleConditionDbModel blRuleConditionDbModel){

        OperandType leftOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionDbModel.getLeftOperandType().toUpperCase())
                .or(OperandType.EXCHANGE);
        OperandType rightOperandType = Enums.getIfPresent(OperandType.class, blRuleConditionDbModel.getRightOperandType().toUpperCase())
                .or(OperandType.EXCHANGE);
        Operator operator = Enums.getIfPresent(Operator.class, blRuleConditionDbModel.getOperator().toUpperCase())
                .or(Operator.EQUAL);
        LogicalOperator logicalOperator = Enums.getIfPresent(LogicalOperator.class, blRuleConditionDbModel.getLogicalOperator().toUpperCase())
                .or(LogicalOperator.AND);

        return BusinessLogicRuleCondition.builder()
                .ruleConditionId(blRuleConditionDbModel.getRuleConditionId())
                .parentRuleConditionId(blRuleConditionDbModel.getParentRuleConditionId())
                .ruleId(blRuleConditionDbModel.getRuleId())
                .sequenceNumber(blRuleConditionDbModel.getSequenceNumber())
                .openConditionScope(blRuleConditionDbModel.getOpenConditionScope())
                .leftOperand(blRuleConditionDbModel.getLeftOperand())
                .leftOperandType(leftOperandType)
                .operator(operator)
                .rightOperand(blRuleConditionDbModel.getRightOperand())
                .rightOperandType(rightOperandType)
                .closeConditionScope(blRuleConditionDbModel.getCloseConditionScope())
                .logicalOperator(logicalOperator)
                .createTimeStamp(blRuleConditionDbModel.getCreateTimeStamp() == null ? new Date() : blRuleConditionDbModel.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

    private BLRuleConditionDbModel mapRuleConditionToDbModel(BusinessLogicRuleCondition businessLogicRuleCondition){

        return BLRuleConditionDbModel.builder()
                .ruleConditionId( businessLogicRuleCondition.getRuleConditionId() == null ?
                        businessLogicRuleCondition.getParentRuleConditionId() == null ? businessLogicRuleCondition.getRuleId() : businessLogicRuleCondition.getParentRuleConditionId()
                                + "~" + businessLogicRuleCondition.getSequenceNumber() :
                        businessLogicRuleCondition.getRuleConditionId() )
                .parentRuleConditionId(businessLogicRuleCondition.getParentRuleConditionId())
                .ruleId(businessLogicRuleCondition.getRuleId())
                .sequenceNumber(businessLogicRuleCondition.getSequenceNumber())
                .openConditionScope(businessLogicRuleCondition.getOpenConditionScope())
                .leftOperand(businessLogicRuleCondition.getLeftOperand())
                .leftOperandType(businessLogicRuleCondition.getLeftOperandType().name())
                .operator(businessLogicRuleCondition.getOperator().name())
                .rightOperand(businessLogicRuleCondition.getRightOperand())
                .rightOperandType(businessLogicRuleCondition.getRightOperandType().name())
                .closeConditionScope(businessLogicRuleCondition.getCloseConditionScope())
                .logicalOperator(businessLogicRuleCondition.getLogicalOperator().name())
                .createTimeStamp(businessLogicRuleCondition.getCreateTimeStamp())
                .updateTimeStamp(businessLogicRuleCondition.getUpdateTimeStamp())
                .build();

    }

}
