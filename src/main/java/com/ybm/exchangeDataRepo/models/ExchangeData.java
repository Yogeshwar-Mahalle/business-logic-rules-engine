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
public class ExchangeData implements Serializable {
    private String uniqueExchangeId;
    private String linkedEntity;
    private String source;
    private String target;
    private String messageId;
    private String workflowMonitor;
    private ContentType originalContentType;
    private String originalData;
    private String originalHeaders;
    private ContentType contentType;
    private String processedData;
    private String processedHeaders;
    private String properties;
    private String dataExtension;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
