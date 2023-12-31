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
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "RULE_FIELD_ID", length = 128, nullable=false)
    private String ruleFieldId;

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

    @Column(name = "GET_VALUE_PATH", length = 10240)
    private String getValuePath;

    @Column(name = "SET_VALUE_PATH", length = 10240)
    private String setValuePath;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String ruleFieldId;
        private String ruleType;
        private String fieldName;
    }

}

