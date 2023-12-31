/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.models;

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
public class RuleLogs implements Serializable {
    private String uniqueExchangeId;
    private String ruleId;
    private String previousData;
    private String actionedData;
    private String previousHeaders;
    private String actionedHeaders;
    private String properties;
    private String extensionData;
    private Date createTimeStamp;
}
