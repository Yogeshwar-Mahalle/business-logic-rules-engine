/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.models;

import com.ybm.exchangeDataRepo.ContentType;
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
public class ExchangeData implements Serializable {
    String uniqueExchangeId;
    String linkedEntity;
    String source;
    String target;
    String workflowMonitor;
    ContentType originalContentType;
    String originalData;
    String originalHeaders;
    ContentType contentType;
    String processedData;
    String processedHeaders;
    String properties;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}
