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
    String uniqueExchangeId;
    String ruleId;
    String previousData;
    String actionedData;
    String previousHeaders;
    String actionedHeaders;
    String properties;
    String extensionData;
    Date createTimeStamp;
}
