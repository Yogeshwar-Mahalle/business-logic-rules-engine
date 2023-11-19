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
    String ruleAuditId;
    String changedByUserId;
    String blRuleId;
    Date changeDateTime;
    String changeRemark;
    String approvedByUserId;
    Date approvedDateTime;
}
