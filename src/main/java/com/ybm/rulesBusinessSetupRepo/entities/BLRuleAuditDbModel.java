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
@Table(name = "BL_RULE_AUDIT")
@IdClass(BLRuleAuditDbModel.IdClass.class)
public class BLRuleAuditDbModel implements Serializable {

    @Id
    @Column(name = "RULE_AUDIT_ID", length = 128, nullable=false)
    private String ruleAuditId;

    @Column(name = "CHANGED_BY_USER_ID", length = 128, nullable=false)
    private String changedByUserId;

    @Column(name = "BL_RULE_ID", length = 128, nullable=false)
    private String blRuleId;

    @Column(name = "CHANGE_DATE_TIME", nullable=false)
    private Date changeDateTime;

    @Column(name = "CHANGE_REMARK", length = 512, nullable=false)
    private String changeRemark;

    @Column(name = "APPROVED_BY_USER_ID", length = 128)
    private String approvedByUserId;

    @Column(name = "APPROVED_DATE_TIME")
    private Date approvedDateTime;

    @Data
    static class IdClass implements Serializable {
        private String ruleAuditId;
    }

}

