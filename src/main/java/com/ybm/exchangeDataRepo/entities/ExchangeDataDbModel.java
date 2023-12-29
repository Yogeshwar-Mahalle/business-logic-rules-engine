/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EXCHANGE_DATA")
@IdClass(ExchangeDataDbModel.IdClass.class)
public class ExchangeDataDbModel implements Serializable {

    @Id
    @Column(name = "UNIQUE_EXCHANGE_ID", length = 128, nullable=false)
    private String uniqueExchangeId;

    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Column(name = "SOURCE", length = 25, nullable=false)
    private String source;

    @Column(name = "TARGET", length = 25)
    private String target;

    @Column(name = "MESSAGE_ID", length = 128)
    private String messageId;

    @Column(name = "WORKFLOW_MONITOR", length = 512, nullable=false)
    private String workflowMonitor;

    @Column(name = "ORIGINAL_CONTENT_TYPE", length = 10, nullable=false)
    private String originalContentType;

    @Column(name = "ORIGINAL_DATA", length = 2097152, nullable=false)
    private String originalData;

    @Column(name = "ORIGINAL_HEADERS", length = 524288)
    private String originalHeaders;

    @Column(name = "CONTENT_TYPE", length = 10, nullable=false)
    private String contentType;

    @Column(name = "PROCESSED_DATA", length = 2097152, nullable=false)
    private String processedData;

    @Column(name = "PROCESSED_HEADERS", length = 524288)
    private String processedHeaders;

    @Column(name = "PROPERTIES", length = 524288)
    private String properties;

    @Column(name = "DATA_EXTENSION", length = 2097152)
    private String dataExtension;

    @Column(name = "STATUS", length = 10, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String uniqueExchangeId;
    }

}

