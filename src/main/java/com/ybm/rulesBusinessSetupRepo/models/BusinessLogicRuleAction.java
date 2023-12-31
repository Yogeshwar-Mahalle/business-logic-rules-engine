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
public class BusinessLogicRuleAction implements Serializable {
    private String ruleActionId;
    private String ruleId;
    private Integer sequenceNumber;
    private ExchangeObjectType assigneeDataObject;
    private String assignee;
    private OperandType assigneeType;
    private ExchangeObjectType assignorDataObject;
    private String assignor;
    private OperandType assignorType;
    private String includeFuncNameList;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
