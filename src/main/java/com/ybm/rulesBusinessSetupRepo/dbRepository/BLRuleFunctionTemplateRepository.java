/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionTemplateDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BLRuleFunctionTemplateRepository extends JpaRepository<BLRuleFunctionTemplateDbModel, String> {
    Optional<BLRuleFunctionTemplateDbModel> findByFunctionId(String ruleType);
}
