/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.models;

import com.ybm.rulesBusinessSetupRepo.LogicalOperator;
import com.ybm.rulesBusinessSetupRepo.OperandType;
import com.ybm.rulesBusinessSetupRepo.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessLogicRuleCondition implements Serializable {
    String ruleConditionId;
    String parentRuleConditionId;
    String ruleId;
    Integer sequenceNumber;
    String openConditionScope;
    String leftOperand;
    OperandType leftOperandType;
    Operator operator;
    String rightOperand;
    OperandType rightOperandType;
    String closeConditionScope;
    LogicalOperator logicalOperator;
    Date createTimeStamp;
    Date updateTimeStamp;
}
