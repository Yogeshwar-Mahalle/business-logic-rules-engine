/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleValueListDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BLRuleValueListRepository extends JpaRepository<BLRuleValueListDbModel, String> {

    List<BLRuleValueListDbModel> findByDataTypeOrderBySequenceNumber(@Param("dataType") String dataType);
    Optional<BLRuleValueListDbModel> findByDataTypeAndKeyField(@Param("dataType") String dataType, @Param("keyField") String keyField);
    void deleteByDataTypeAndKeyField(@Param("dataType") String dataType, @Param("keyField") String keyField);
}
