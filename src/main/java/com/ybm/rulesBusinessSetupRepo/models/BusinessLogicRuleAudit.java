/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusinessLogicRuleAudit implements Serializable {
    private String ruleAuditId;
    private String changedByUserId;
    private String blRuleId;
    private Date changeDateTime;
    private String changeRemark;
    private String approvedByUserId;
    private Date approvedDateTime;
}
