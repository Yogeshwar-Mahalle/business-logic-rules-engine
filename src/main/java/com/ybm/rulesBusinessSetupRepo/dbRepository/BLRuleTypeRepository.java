/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleTypeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BLRuleTypeRepository extends JpaRepository<BLRuleTypeDbModel, String> {
    Optional<BLRuleTypeDbModel> findByRuleType(String ruleType);

}
