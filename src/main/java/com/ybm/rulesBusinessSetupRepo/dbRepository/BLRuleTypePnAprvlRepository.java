/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypePnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BLRuleTypePnAprvlRepository extends JpaRepository<BLRuleTypePnAprvlDbModel, String> {
    List<BLRuleTypePnAprvlDbModel> findByRuleType(String ruleType);

    Optional<BLRuleTypePnAprvlDbModel> findByLinkedEntityAndRuleType(String entity, String ruleType);

    List<BLRuleTypePnAprvlDbModel>  findByWorkflowRuleFlag(boolean flag);

    void deleteByLinkedEntityAndRuleType(String entity, String ruleType);

    List<BLRuleTypePnAprvlDbModel> findByLinkedEntityAndWorkflowRuleFlag(String entity, boolean b);
}
