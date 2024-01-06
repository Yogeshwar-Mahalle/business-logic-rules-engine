/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.ExchangeDataNotesDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExchangeDataNotesRepository extends JpaRepository<ExchangeDataNotesDbModel, String> {

    Collection<ExchangeDataNotesDbModel> findByUniqueExchangeIdOrderByCreateTimeStamp(String uniqueExchangeID);
}
