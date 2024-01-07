/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.ybm.exchangeDataRepo.dbRepository.ExchangeDataLinksRepository;
import com.ybm.exchangeDataRepo.entities.ExchangeDataLinksDbModel;
import com.ybm.exchangeDataRepo.models.ExchangeDataLinks;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeDataLinksService {

    @Autowired
    private ExchangeDataLinksRepository exchangeDataLinksRepository;

    public List<ExchangeDataLinks> getExchangeLinkByExchangeId(String uniqueExchangeID) {

        return exchangeDataLinksRepository.findByUniqueExchangeIdOrderByCreateTimeStamp(uniqueExchangeID).stream()
                .map(
                        this::mapExchangeDataLinksFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<ExchangeDataLinks> getExchangeLinksByExchangeLinkId(String exchangeLinkID) {

        return exchangeDataLinksRepository.findByExchangeLinkIdOrderByCreateTimeStamp(exchangeLinkID).stream()
                .map(
                        this::mapExchangeDataLinksFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public ExchangeDataLinks saveExchangeDataLinks(ExchangeDataLinks exchangeDataLinks) {
        ExchangeDataLinksDbModel exchangeDataLinksDbModel = mapExchangeDataLinksToDbModel(exchangeDataLinks);
        exchangeDataLinksDbModel = exchangeDataLinksRepository.save(exchangeDataLinksDbModel);

        return mapExchangeDataLinksFromDbModel(exchangeDataLinksDbModel);
    }


    private ExchangeDataLinks mapExchangeDataLinksFromDbModel(ExchangeDataLinksDbModel exchangeDataLinksDbModel){

        return ExchangeDataLinks.builder()
                .exchangeLinkId(exchangeDataLinksDbModel.getExchangeLinkId())
                .uniqueExchangeId(exchangeDataLinksDbModel.getUniqueExchangeId())
                .createTimeStamp(exchangeDataLinksDbModel.getCreateTimeStamp())
                .build();

    }

    private ExchangeDataLinksDbModel mapExchangeDataLinksToDbModel(ExchangeDataLinks exchangeDataLinks){

        return ExchangeDataLinksDbModel.builder()
                .exchangeLinkId(exchangeDataLinks.getExchangeLinkId())
                .uniqueExchangeId(exchangeDataLinks.getUniqueExchangeId())
                .createTimeStamp(exchangeDataLinks.getCreateTimeStamp() == null ? new Date() : exchangeDataLinks.getCreateTimeStamp())
                .build();

    }

}
