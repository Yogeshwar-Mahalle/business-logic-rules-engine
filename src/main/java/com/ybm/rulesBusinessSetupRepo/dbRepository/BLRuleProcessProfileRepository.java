/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleProcessProfileDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BLRuleProcessProfileRepository extends JpaRepository<BLRuleProcessProfileDbModel, String> {

    Optional<BLRuleProcessProfileDbModel> findByLinkedEntityAndProfileName(String linkedEntity, String profileName);

    void deleteByLinkedEntityAndProfileName(String linkedEntity, String profileName);

}
