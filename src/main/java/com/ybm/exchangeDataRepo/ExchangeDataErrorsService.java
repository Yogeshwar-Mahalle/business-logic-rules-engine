/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.ybm.exchangeDataRepo.dbRepository.ExchangeDataErrorsRepository;
import com.ybm.exchangeDataRepo.entities.ExchangeDataErrorsDbModel;
import com.ybm.exchangeDataRepo.models.ExchangeDataErrors;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeDataErrorsService {

    @Autowired
    private ExchangeDataErrorsRepository exchangeDataErrorsRepository;

    public List<ExchangeDataErrors> getExchangeErrorsByExchangeId(String uniqueExchangeID) {

        return exchangeDataErrorsRepository.findByUniqueExchangeIdOrderByCreateTimeStamp(uniqueExchangeID).stream()
                .map(
                        this::mapExchangeDataErrorsFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public ExchangeDataErrors saveRuleLogs(ExchangeDataErrors exchangeDataErrors) {
        ExchangeDataErrorsDbModel exchangeDataErrorsDbModel = mapExchangeDataErrorsToDbModel(exchangeDataErrors);
        exchangeDataErrorsDbModel = exchangeDataErrorsRepository.save(exchangeDataErrorsDbModel);

        return mapExchangeDataErrorsFromDbModel(exchangeDataErrorsDbModel);
    }


    private ExchangeDataErrors mapExchangeDataErrorsFromDbModel(ExchangeDataErrorsDbModel exchangeDataErrorsDbModel){

        return ExchangeDataErrors.builder()
                .uniqueExchangeId(exchangeDataErrorsDbModel.getUniqueExchangeId())
                .errorMessage(exchangeDataErrorsDbModel.getErrorMessage())
                .createTimeStamp(exchangeDataErrorsDbModel.getCreateTimeStamp())
                .build();

    }

    private ExchangeDataErrorsDbModel mapExchangeDataErrorsToDbModel(ExchangeDataErrors exchangeDataErrors){

        return ExchangeDataErrorsDbModel.builder()
                .uniqueExchangeId(exchangeDataErrors.getUniqueExchangeId())
                .errorMessage(exchangeDataErrors.getErrorMessage())
                .createTimeStamp(exchangeDataErrors.getCreateTimeStamp() == null ? new Date() : exchangeDataErrors.getCreateTimeStamp())
                .build();

    }

}
