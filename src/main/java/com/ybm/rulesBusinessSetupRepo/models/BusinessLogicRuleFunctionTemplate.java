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
    String functionId;
    String linkedEntity;
    String functionName;
    String description;
    String functionParameters;
    String includeFunctionsNameList;
    String functionLogic;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}
