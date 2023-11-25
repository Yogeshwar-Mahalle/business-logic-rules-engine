/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFieldListDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BLRuleFieldListRepository extends JpaRepository<BLRuleFieldListDbModel, String> {

    List<BLRuleFieldListDbModel> findByRuleType(@Param("ruleType") String ruleType);
    Optional<BLRuleFieldListDbModel> findByRuleTypeAndFieldName(@Param("ruleType") String ruleType, @Param("fieldName") String fieldName);
    void deleteByRuleTypeAndFieldName(@Param("ruleType") String ruleType, @Param("fieldName") String fieldName);
}
