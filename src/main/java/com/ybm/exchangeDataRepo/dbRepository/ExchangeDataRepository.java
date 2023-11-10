/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.ExchangeDataDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExchangeDataRepository extends JpaRepository<ExchangeDataDbModel, String> {

}
