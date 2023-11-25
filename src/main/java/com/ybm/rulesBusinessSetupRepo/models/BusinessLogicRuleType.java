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
    String ruleType;
    String linkedEntity;
    String description;
    boolean complexRuleFlag;
    boolean workflowRuleFlag;
    boolean applyAllFlag;
    boolean systemRuleFlag;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}
