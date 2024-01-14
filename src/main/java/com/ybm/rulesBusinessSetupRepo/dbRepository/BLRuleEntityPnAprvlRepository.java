/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleEntityPnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BLRuleEntityPnAprvlRepository extends JpaRepository<BLRuleEntityPnAprvlDbModel, String> {

    Optional<BLRuleEntityPnAprvlDbModel> findByEntityName(String entityName);

    void deleteByEntityName(String entityName);

}
