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
public class BusinessLogicRuleType implements Serializable {
    private String linkedEntity;
    private String ruleType;
    private String description;
    private boolean complexRuleFlag;
    private boolean workflowRuleFlag;
    private boolean applyAllFlag;
    private boolean systemRuleFlag;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
