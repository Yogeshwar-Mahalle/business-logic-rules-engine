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
@Table(name = "BL_RULE_ACTIONS")
@IdClass(BLRuleActionDbModel.IdClass.class)
public class BLRuleActionDbModel implements Serializable {

    @Id
    @Column(name = "RULE_ACTION_ID", length = 128, nullable=false)
    private String ruleActionId;

    @Column(name = "RULE_ID", length = 128, nullable=false)
    private String ruleId;

    @Column(name = "SEQUENCE_NUMBER", nullable=false)
    private Integer sequenceNumber;

    @Column(name = "ASSIGNEE", length = 128, nullable=false)
    private String assignee;

    @Column(name = "ASSIGNEE_TYPE", length = 25)
    private String assigneeType;

    @Column(name = "RULE_CONDITION_ID", length = 128)
    private String ruleConditionId;

    @Column(name = "ASSIGNOR", length = 5120, nullable=false)
    private String assignor;

    @Column(name = "ASSIGNOR_TYPE", length = 25)
    private String assignorType;

    @Column(name = "INCLUDE_FUNCTIONS_NAME_LIST", length = 2048)
    private String includeFuncNameList;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String ruleActionId;
    }

}

