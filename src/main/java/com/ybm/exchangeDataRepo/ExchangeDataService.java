/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.google.common.base.Enums;
import com.ybm.exchangeDataRepo.dbRepository.ExchangeDataRepository;
import com.ybm.exchangeDataRepo.entities.ExchangeDataDbModel;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.exchangeDataRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeDataService {

    @Autowired
    private ExchangeDataRepository exchangeDataRepository;

    public ExchangeData getExchangeDataById(String uniqueExchangeID) {
        Optional<ExchangeDataDbModel> exchangeDataDbModel = exchangeDataRepository.findById(uniqueExchangeID);

        return exchangeDataDbModel.map(this::mapExchangeDataFromDbModel).orElse(null);
    }

    public List<ExchangeData> getExchangeDataByMessageId(String entity, String source, String messageId) {
        List<ExchangeData> exchangeDataDbModel;

        exchangeDataDbModel = exchangeDataRepository.findByLinkedEntityAndSourceAndMessageId(entity, source, messageId).stream()
                .map(
                        this::mapExchangeDataFromDbModel
                )
                .collect(Collectors.toList());

        return exchangeDataDbModel;
    }

    public int getExchangeDataCountByMessageId(String entity, String source, String messageId) {
        return exchangeDataRepository
                .countByLinkedEntityAndSourceAndMessageId(entity, source, messageId);
    }

    public int getExchangeDataCountByMessageIdAndNotExchId(String entity,
                                                           String source,
                                                           String messageId,
                                                           String exchangeId) {
        return exchangeDataRepository
            .countByLinkedEntityAndSourceAndMessageIdAndNotUniqueExchangeId(
                entity,
                source,
                messageId,
                exchangeId
            );
    }

    @Transactional
    public ExchangeData saveExchangeData(ExchangeData exchangeData) {
        ExchangeDataDbModel exchangeDataDbModel = mapExchangeDataToDbModel(exchangeData);
        exchangeDataDbModel = exchangeDataRepository.save(exchangeDataDbModel);

        return mapExchangeDataFromDbModel(exchangeDataDbModel);
    }


    private ExchangeData mapExchangeDataFromDbModel(ExchangeDataDbModel exchangeDataDbModel){

        ContentType originalContentType = Enums.getIfPresent(ContentType.class, exchangeDataDbModel.getOriginalContentType().toUpperCase())
                .or(ContentType.JSON);
        ContentType contentType = Enums.getIfPresent(ContentType.class, exchangeDataDbModel.getContentType().toUpperCase())
                .or(ContentType.JSON);

        return ExchangeData.builder()
                .uniqueExchangeId(exchangeDataDbModel.getUniqueExchangeId())
                .linkedEntity(exchangeDataDbModel.getLinkedEntity())
                .source(exchangeDataDbModel.getSource())
                .target(exchangeDataDbModel.getTarget())
                .messageId(exchangeDataDbModel.getMessageId())
                .workflowMonitor(exchangeDataDbModel.getWorkflowMonitor())
                .originalContentType(originalContentType)
                .originalData(exchangeDataDbModel.getOriginalData())
                .originalHeaders(exchangeDataDbModel.getOriginalHeaders())
                .contentType(contentType)
                .processedData(exchangeDataDbModel.getProcessedData())
                .processedHeaders(exchangeDataDbModel.getProcessedHeaders())
                .properties(exchangeDataDbModel.getProperties())
                .dataExtension(exchangeDataDbModel.getDataExtension())
                .status(StatusType.valueOf(exchangeDataDbModel.getStatus()))
                .createTimeStamp(exchangeDataDbModel.getCreateTimeStamp())
                .updateTimeStamp(exchangeDataDbModel.getUpdateTimeStamp())
                .build();

    }

    private ExchangeDataDbModel mapExchangeDataToDbModel(ExchangeData exchangeData){

        return ExchangeDataDbModel.builder()
                .uniqueExchangeId(exchangeData.getUniqueExchangeId())
                .linkedEntity(exchangeData.getLinkedEntity())
                .source(exchangeData.getSource())
                .target(exchangeData.getTarget())
                .messageId(exchangeData.getMessageId())
                .workflowMonitor(exchangeData.getWorkflowMonitor())
                .originalContentType(exchangeData.getOriginalContentType().name())
                .originalData(exchangeData.getOriginalData())
                .originalHeaders(exchangeData.getOriginalHeaders())
                .contentType(exchangeData.getContentType().name())
                .processedData(exchangeData.getProcessedData())
                .processedHeaders(exchangeData.getProcessedHeaders())
                .properties(exchangeData.getProperties())
                .dataExtension(exchangeData.getDataExtension())
                .status(String.valueOf(exchangeData.getStatus()))
                .createTimeStamp(exchangeData.getCreateTimeStamp() == null ? new Date() : exchangeData.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
