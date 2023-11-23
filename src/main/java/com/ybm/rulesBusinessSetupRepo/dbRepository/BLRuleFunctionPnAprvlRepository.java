/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionTemplatePnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BLRuleFunctionPnAprvlRepository extends JpaRepository<BLRuleFunctionTemplatePnAprvlDbModel, String> {

}
