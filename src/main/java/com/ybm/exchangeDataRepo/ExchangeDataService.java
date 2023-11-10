/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.google.common.base.Enums;
import com.ybm.exchangeDataRepo.dbRepository.ExchangeDataRepository;
import com.ybm.exchangeDataRepo.entities.ExchangeDataDbModel;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ExchangeDataService {

    @Autowired
    private ExchangeDataRepository exchangeDataRepository;

    public ExchangeData getExchangeDataById(String uniqueExchangeID) {
        Optional<ExchangeDataDbModel> exchangeDataDbModel = exchangeDataRepository.findById(uniqueExchangeID);

        return exchangeDataDbModel.map(this::mapExchangeDataFromDbModel).orElse(null);

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
                .workflowMonitor(exchangeDataDbModel.getWorkflowMonitor())
                .originalContentType(originalContentType)
                .originalData(exchangeDataDbModel.getOriginalData())
                .originalHeaders(exchangeDataDbModel.getOriginalHeaders())
                .contentType(contentType)
                .processedData(exchangeDataDbModel.getProcessedData())
                .processedHeaders(exchangeDataDbModel.getProcessedHeaders())
                .properties(exchangeDataDbModel.getProperties())
                .status(exchangeDataDbModel.getStatus())
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
                .workflowMonitor(exchangeData.getWorkflowMonitor())
                .originalContentType(exchangeData.getOriginalContentType().name())
                .originalData(exchangeData.getOriginalData())
                .originalHeaders(exchangeData.getOriginalHeaders())
                .contentType(exchangeData.getContentType().name())
                .processedData(exchangeData.getProcessedData())
                .processedHeaders(exchangeData.getProcessedHeaders())
                .properties(exchangeData.getProperties())
                .status(exchangeData.getStatus())
                .createTimeStamp(exchangeData.getCreateTimeStamp() == null ? new Date() : exchangeData.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
