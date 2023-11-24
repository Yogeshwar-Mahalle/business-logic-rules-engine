/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo.dbRepository;

import com.ybm.rulesBusinessSetupRepo.entities.BLRuleValueListPnAprvlDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BLRuleValueListPnAprvlRepository extends JpaRepository<BLRuleValueListPnAprvlDbModel, String> {

    List<BLRuleValueListPnAprvlDbModel> findByDataTypeOrderBySequenceNumber(@Param("dataType") String dataType);
    Optional<BLRuleValueListPnAprvlDbModel> findByDataTypeAndKeyField(@Param("dataType") String dataType, @Param("keyField") String keyField);
    void deleteByDataTypeAndKeyField(@Param("dataType") String dataType, @Param("keyField") String keyField);
}
