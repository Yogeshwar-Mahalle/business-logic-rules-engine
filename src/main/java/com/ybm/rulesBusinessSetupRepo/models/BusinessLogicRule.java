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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessLogicRule implements Serializable {
    String ruleId;
    String ruleType;
    String linkedEntity;
    String ruleName;
    String condInclFuncNameList;
    String condInitTemplate;
    String condition;
    String actionInclFuncNameList;
    String actionInitTemplate;
    String action;
    String actionFinalTemplate;
    Integer priority;
    String description;
    String status;
    Date createTimeStamp;
    Date effectiveFromDate;
    Date updateTimeStamp;
}
