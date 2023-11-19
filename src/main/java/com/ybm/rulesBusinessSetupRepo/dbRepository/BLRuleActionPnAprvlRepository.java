/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleActionPnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BLRuleActionPnAprvlRepository extends JpaRepository<BLRuleActionPnAprvlDbModel, String> {

    List<BLRuleActionPnAprvlDbModel> findAllByRuleId(String ruleID);
    List<BLRuleActionPnAprvlDbModel> findAllByRuleActionId(String ruleActionID);
}
