/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleAuditDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BLRuleAuditRepository extends JpaRepository<BLRuleAuditDbModel, String> {

    List<BLRuleAuditDbModel> findAllByRuleAuditId(String ruleAuditId);
}
