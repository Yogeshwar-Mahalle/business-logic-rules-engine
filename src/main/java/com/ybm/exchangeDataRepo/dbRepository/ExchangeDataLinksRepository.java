/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.ExchangeDataLinksDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExchangeDataLinksRepository extends JpaRepository<ExchangeDataLinksDbModel, String> {
    Collection<ExchangeDataLinksDbModel> findByUniqueExchangeIdOrderByCreateTimeStamp(String uniqueExchangeID);

    Collection<ExchangeDataLinksDbModel> findByExchangeLinkIdOrderByCreateTimeStamp(String exchangeLinkID);
}
