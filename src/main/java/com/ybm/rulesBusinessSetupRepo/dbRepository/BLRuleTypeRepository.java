/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BLRuleTypeRepository extends JpaRepository<BLRuleTypeDbModel, String> {
    List<BLRuleTypeDbModel> findByRuleType(String ruleType);

    Optional<BLRuleTypeDbModel> findByLinkedEntityAndRuleType(String entity, String ruleType);

    List<BLRuleTypeDbModel>  findByWorkflowRuleFlag(boolean flag);

    void deleteByLinkedEntityAndRuleType(String entity, String ruleType);

    List<BLRuleTypeDbModel> findByLinkedEntityAndWorkflowRuleFlag(String entity, boolean b);
}
