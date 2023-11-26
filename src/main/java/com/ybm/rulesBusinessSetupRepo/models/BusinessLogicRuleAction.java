/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.models;

import com.ybm.rulesBusinessSetupRepo.OperandType;
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
public class BusinessLogicRuleAction implements Serializable {
    String ruleActionId;
    String ruleId;
    Integer sequenceNumber;
    String assignee;
    OperandType assigneeType;
    String ruleConditionId;
    String assignor;
    OperandType assignorType;
    String includeFuncNameList;
    Date createTimeStamp;
    Date updateTimeStamp;
}
