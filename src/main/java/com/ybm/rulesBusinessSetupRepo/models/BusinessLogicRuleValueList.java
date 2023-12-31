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
public class BusinessLogicRuleValueList implements Serializable {
    private String linkedEntity;
    private String dataType;
    private String keyField;
    private String valueField;
    private Integer sequenceNumber;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
