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
@Table(name = "BL_RULE_TYPES_PN_APRVL")
@IdClass(BLRuleTypePnAprvlDbModel.IdClass.class)
public class BLRuleTypePnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "RULE_TYPE", length = 25, nullable=false)
    private String ruleType;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "COMPLEX_RULE_FLAG", nullable=false)
    private Boolean complexRuleFlag;

    @Column(name = "WORKFLOW_RULE_FLAG", nullable=false)
    private Boolean workflowRuleFlag;

    @Column(name = "SYSTEM_RULE_FLAG", nullable=false)
    private Boolean systemRuleFlag;

    @Column(name = "APPLY_ALL_FLAG", nullable=false)
    private Boolean applyAllFlag;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String ruleType;
    }

}

