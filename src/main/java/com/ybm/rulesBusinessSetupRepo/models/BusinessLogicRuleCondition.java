/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.models;

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
    private String ruleConditionId;
    private String parentRuleConditionId;
    private String ruleId;
    private Integer sequenceNumber;
    @Builder.Default
    private Boolean isNotIndicator = false;
    private String openConditionScope;
    private ExchangeObjectType leftDataObject;
    private String leftOperand;
    private OperandType leftOperandType;
    private Operator operator;
    private ExchangeObjectType rightDataObject;
    private String rightOperand;
    private OperandType rightOperandType;
    private String closeConditionScope;
    private LogicalOperator logicalOperator;
    private String includeFuncNameList;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
