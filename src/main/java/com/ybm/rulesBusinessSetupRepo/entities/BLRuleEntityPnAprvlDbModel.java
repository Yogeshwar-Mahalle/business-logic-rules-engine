/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BL_RULE_ENTITY_PN_APRVL")
@IdClass(BLRuleEntityPnAprvlDbModel.IdClass.class)
public class BLRuleEntityPnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "ENTITY_NAME", length = 25, nullable=false)
    private String entityName;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "COUNTRY_CODE", length = 2, nullable=false)
    private String countryCode;

    @Column(name = "CURRENCY_CODE", length = 3, nullable=false)
    private String currencyCode;

    @Column(name = "BUSINESS_DATE", nullable=false)
    private Date businessDate;

    @Column(name = "TIME_ZONE", nullable=false)
    private TimeZone timeZone;

    @Column(name = "DATA_STORAGE_FLAG", nullable=false)
    private Boolean dataStorageFlag;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String entityName;
    }

}

