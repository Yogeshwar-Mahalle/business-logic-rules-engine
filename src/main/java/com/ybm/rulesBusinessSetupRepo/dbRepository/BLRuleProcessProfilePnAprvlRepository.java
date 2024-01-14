/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleProcessProfilePnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BLRuleProcessProfilePnAprvlRepository extends JpaRepository<BLRuleProcessProfilePnAprvlDbModel, String> {

    Optional<BLRuleProcessProfilePnAprvlDbModel> findByLinkedEntityAndProfileName(String linkedEntity, String profileName);

    void deleteByLinkedEntityAndProfileName(String linkedEntity, String profileName);

}
