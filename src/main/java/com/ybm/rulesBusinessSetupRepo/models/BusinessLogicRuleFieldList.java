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
public class BusinessLogicRuleFieldList implements Serializable {
    private String linkedEntity;
    private String ruleType;
    private String fieldName;
    private String description;
    private String labelName;
    private String dataType;
    private String dataFormat;
    private String defaultValue;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
