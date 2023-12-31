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
public class BusinessLogicRuleFunctionTemplate implements Serializable {
    private String linkedEntity;
    private String functionId;
    private String functionName;
    private String description;
    private String functionParameters;
    private String includeFunctionsNameList;
    private String functionLogic;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
