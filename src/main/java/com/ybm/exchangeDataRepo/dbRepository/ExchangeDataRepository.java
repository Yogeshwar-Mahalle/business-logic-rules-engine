/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.ExchangeDataDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ExchangeDataRepository extends JpaRepository<ExchangeDataDbModel, String> {

    List<ExchangeDataDbModel> findByLinkedEntityAndSourceAndMessageId(String entity, String source, String messageId);

    int countByLinkedEntityAndSourceAndMessageId(String entity, String source, String messageId);
}
