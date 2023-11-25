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
@Table(name = "BL_RULE_FIELD_LIST")
@IdClass(BLRuleFieldListDbModel.IdClass.class)
public class BLRuleFieldListDbModel implements Serializable {

    @Id
    @Column(name = "RULE_TYPE", length = 25, nullable=false)
    private String ruleType;

    @Id
    @Column(name = "FIELD_NAME", length = 35, nullable=false)
    private String fieldName;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "LABEL_NAME", length = 40, nullable=false)
    private String labelName;

    @Column(name = "DATA_TYPE", length = 25, nullable=false)
    private String dataType;

    @Column(name = "DATA_FORMAT", length = 50, nullable=false)
    private String dataFormat;

    @Column(name = "DEFAULT_VALUE", length = 128)
    private String defaultValue;

    @Column(name = "STATUS", length = 25, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String ruleType;
        private String fieldName;
    }

}

