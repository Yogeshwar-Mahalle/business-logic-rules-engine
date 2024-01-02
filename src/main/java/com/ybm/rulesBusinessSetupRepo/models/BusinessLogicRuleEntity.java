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
import java.util.TimeZone;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessLogicRuleEntity implements Serializable {
    private String entityName;
    private String description;
    private String countryCode;
    private String currencyCode;
    private Date businessDate;
    private TimeZone timeZone;
    private Boolean dataStorageFlag;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
