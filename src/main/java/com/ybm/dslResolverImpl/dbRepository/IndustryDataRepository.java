/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl.dbRepository;

import com.ybm.dslResolverImpl.entities.IndustryDataDbModel;
import com.ybm.exchangeDataRepo.entities.RuleLogsDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IndustryDataRepository extends JpaRepository<IndustryDataDbModel, String> {

    List<IndustryDataDbModel> findByDataTypeOrderBySequenceNumber(@Param("dataType") String dataType);
    IndustryDataDbModel findByDataTypeAndKeyField(@Param("dataType") String dataType, @Param("keyField") String keyField);

}
