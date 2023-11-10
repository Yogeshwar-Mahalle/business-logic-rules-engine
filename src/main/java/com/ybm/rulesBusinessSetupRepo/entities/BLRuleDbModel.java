/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BUSINESS_LOGIC_RULES")
@IdClass(BLRuleDbModel.IdClass.class)
public class BLRuleDbModel implements Serializable {

    @Id
    @Column(name = "RULE_ID", length = 128, nullable=false)
    private String ruleId;

    @Column(name = "RULE_TYPE", length = 25, nullable=false)
    private String ruleType;

    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Column(name = "RULE_NAME", length = 75, nullable=false)
    private String ruleName;

    @Column(name = "CONDITION", length = 2097152)
    private String condition;

    @Column(name = "ACTION", length = 2097152)
    private String action;

    @Column(name = "PRIORITY", nullable=false)
    private Integer priority;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Column(name = "STATUS", length = 25, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String ruleId;
    }

}

