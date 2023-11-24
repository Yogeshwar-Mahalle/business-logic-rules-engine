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
@Table(name = "BL_RULES_PN_APRVL")
@IdClass(BLRulePnAprvlDbModel.IdClass.class)
public class BLRulePnAprvlDbModel implements Serializable {

    @Id
    @Column(name = "RULE_ID", length = 128, nullable=false)
    private String ruleId;

    @Id
    @Column(name = "RULE_TYPE", length = 25, nullable=false)
    private String ruleType;

    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "RULE_NAME", length = 75, nullable=false)
    private String ruleName;

    @Column(name = "CONDITION_INCLUDE_FUNCTIONS_NAME_LIST", length = 5120)
    private String condInclFuncNameList;

    @Column(name = "CONDITION_INIT_TEMPLATE", length = 128)
    String condInitTemplate;

    @Column(name = "CONDITION", length = 2097152)
    private String condition;

    @Column(name = "ACTION_INCLUDE_FUNCTIONS_NAME_LIST", length = 5120)
    private String actionInclFuncNameList;

    @Column(name = "ACTION_INIT_TEMPLATE", length = 128)
    String actionInitTemplate;

    @Column(name = "ACTION", length = 2097152)
    private String action;

    @Column(name = "ACTION_FINAL_TEMPLATE", length = 128)
    String actionFinalTemplate;

    @Column(name = "PRIORITY", nullable=false)
    private Integer priority;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "STATUS", length = 25, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "EFFECTIVE_FROM_DATE", nullable=false)
    private Date effectiveFromDate;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;


    @Data
    static class IdClass implements Serializable {
        private String ruleId;
        private String ruleType;
        private String linkedEntity;
        private String ruleName;
    }

}

