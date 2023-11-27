/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BL_RULE_CONDITIONS_PN_APRVL")
@IdClass(BLRuleConditionPnAprvlDbModel.IdClass.class)
public class BLRuleConditionPnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "RULE_CONDITION_ID", length = 256, nullable=false)
    private String ruleConditionId;

    @Column(name = "PARENT_RULE_CONDITION_ID", length = 128)
    private String parentRuleConditionId;

    @Column(name = "RULE_ID", length = 128, nullable=false)
    private String ruleId;

    @Column(name = "SEQUENCE_NUMBER", nullable=false)
    private Integer sequenceNumber;

    @Builder.Default
    @Column(name = "IS_NOT_INDICATOR", nullable=false, columnDefinition = "boolean default false")
    private Boolean isNotIndicator = false;

    @Column(name = "OPEN_CONDITION_SCOPE", length = 1)
    private String openConditionScope;

    @Column(name = "LEFT_DATA_OBJECT", length = 64)
    private String leftDataObject;

    @Column(name = "LEFT_OPERAND", length = 512)
    private String leftOperand;

    @Column(name = "LEFT_OPERAND_TYPE", length = 25)
    private String leftOperandType;

    @Column(name = "OPERATOR", length = 25)
    private String operator;

    @Column(name = "RIGHT_DATA_OBJECT", length = 64)
    private String rightDataObject;

    @Column(name = "RIGHT_OPERAND", length = 512)
    private String rightOperand;

    @Column(name = "RIGHT_OPERAND_TYPE", length = 25)
    private String rightOperandType;

    @Column(name = "CLOSE_CONDITION_SCOPE", length = 1)
    private String closeConditionScope;

    @Column(name = "LOGICAL_OPERATOR", length = 25)
    private String logicalOperator;

    @Column(name = "INCLUDE_FUNCTIONS_NAME_LIST", length = 1024)
    private String includeFuncNameList;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String ruleConditionId;
    }

}

