/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRulePnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BLRulesPnAprvlRepository extends JpaRepository<BLRulePnAprvlDbModel, String> {
    List<BLRulePnAprvlDbModel> findByRuleType(String ruleType);
    List<BLRulePnAprvlDbModel> findByLinkedEntityAndRuleType(String linkedEntity, String ruleType);
    List<BLRulePnAprvlDbModel> findByLinkedEntity(String linkedEntity);

}
