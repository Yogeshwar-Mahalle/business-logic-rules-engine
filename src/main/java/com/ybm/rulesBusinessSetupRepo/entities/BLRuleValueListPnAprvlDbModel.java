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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BL_RULE_VALUE_LIST_PN_APRVL")
@IdClass(BLRuleValueListPnAprvlDbModel.IdClass.class)
public class BLRuleValueListPnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "DATA_TYPE", length = 25, nullable=false)
    private String dataType;

    @Id
    @Column(name = "KEY_FIELD", length = 128, nullable=false)
    private String keyField;

    @Column(name = "VALUE_FIELD", length = 256)
    private String valueField;

    @Column(name = "SEQUENCE_NUMBER", nullable=false)
    private Integer sequenceNumber;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String dataType;
        private String keyField;
    }

}

