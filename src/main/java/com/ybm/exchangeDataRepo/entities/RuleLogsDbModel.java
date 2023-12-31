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
@Table(name = "RULE_LOGS")
@IdClass(RuleLogsDbModel.IdClass.class)
public class RuleLogsDbModel implements Serializable {

    @Id
    @Column(name = "UNIQUE_EXCHANGE_ID", length = 128, nullable=false)
    private String uniqueExchangeId;

    @Id
    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Id
    @Column(name = "RULE_ID", length = 128, nullable=false)
    private String ruleId;

    @Column(name = "PREVIOUS_DATA", length = 2097152, nullable=false)
    private String previousData;

    @Column(name = "ACTIONED_DATA", length = 2097152, nullable=false)
    private String actionedData;

    @Column(name = "PREVIOUS_HEADERS", length = 524288)
    private String previousHeaders;

    @Column(name = "ACTIONED_HEADERS", length = 524288)
    private String actionedHeaders;

    @Column(name = "PROPERTIES", length = 524288)
    private String properties;

    @Column(name = "EXTENSION_DATA", length = 524288)
    private String extensionData;


    @Data
    static class IdClass implements Serializable {
        private String uniqueExchangeId;
        private Date createTimeStamp;
        private String ruleId;
    }

}

