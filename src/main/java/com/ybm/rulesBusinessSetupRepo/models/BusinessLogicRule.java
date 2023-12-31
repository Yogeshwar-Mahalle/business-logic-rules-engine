/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.models;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessLogicRule implements Serializable {
    private String linkedEntity;
    private String ruleId;
    private String ruleType;
    private String ruleName;
    private String condInclFuncNameList;
    private String condInitTemplate;
    private String condition;
    private String actionInclFuncNameList;
    private String actionInitTemplate;
    private String action;
    private String actionFinalTemplate;
    private Integer priority;
    private String description;
    private StatusType status;
    private Date createTimeStamp;
    private Date effectiveFromDate;
    private Date updateTimeStamp;

    List<BusinessLogicRuleCondition> conditionList;
    List<BusinessLogicRuleAction> actionList;

}
