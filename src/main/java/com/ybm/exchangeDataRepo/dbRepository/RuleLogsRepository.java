/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.RuleLogsDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleLogsRepository extends JpaRepository<RuleLogsDbModel, String> {

    //@Query("SELECT ruleLogsDbModel FROM RuleLogsDbModel ruleLogsDbModel WHERE ruleLogsDbModel.UNIQUE_EXCHANGE_ID = :uniqueExchangeId ORDER BY ruleLogsDbModel.CREATE_TIME_STAMP")
    List<RuleLogsDbModel> findByUniqueExchangeIdOrderByCreateTimeStamp(@Param("uniqueExchangeId") String uniqueExchangeId);
}
