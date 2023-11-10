/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BLRulesRepository extends JpaRepository<BLRuleDbModel, String> {
    List<BLRuleDbModel> findByRuleType(String ruleType);
    List<BLRuleDbModel> findByLinkedEntityAndRuleType(String linkedEntity, String ruleType);
    List<BLRuleDbModel> findByLinkedEntity(String linkedEntity);

}
