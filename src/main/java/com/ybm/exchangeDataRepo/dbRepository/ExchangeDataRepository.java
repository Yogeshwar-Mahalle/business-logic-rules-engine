/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo.dbRepository;

import com.ybm.exchangeDataRepo.entities.ExchangeDataDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExchangeDataRepository extends JpaRepository<ExchangeDataDbModel, String> {

    List<ExchangeDataDbModel> findByLinkedEntityAndSourceAndMessageId(String entity, String source, String messageId);

    int countByLinkedEntityAndSourceAndMessageId(String entity, String source, String messageId);

    @Query("SELECT count(exchangeDataDbModel) FROM ExchangeDataDbModel exchangeDataDbModel WHERE " +
            "exchangeDataDbModel.linkedEntity = :entity and " +
            "exchangeDataDbModel.source = :source and " +
            "exchangeDataDbModel.messageId = :messageId and " +
            "exchangeDataDbModel.uniqueExchangeId <> :exchangeId")
    int countByLinkedEntityAndSourceAndMessageIdAndNotUniqueExchangeId(@Param("entity")String entity,
                                                                       @Param("source")String source,
                                                                       @Param("messageId")String messageId,
                                                                       @Param("exchangeId")String exchangeId);
}
