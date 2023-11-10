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
public class BusinessLogicRuleFunction implements Serializable {
    String functionId;
    String functionName;
    String functionParameters;
    String functionLogic;
    String description;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}
