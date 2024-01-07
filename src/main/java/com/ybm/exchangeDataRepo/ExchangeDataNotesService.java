/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.ybm.exchangeDataRepo.dbRepository.ExchangeDataNotesRepository;
import com.ybm.exchangeDataRepo.entities.ExchangeDataNotesDbModel;
import com.ybm.exchangeDataRepo.models.ExchangeDataNotes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeDataNotesService {

    @Autowired
    private ExchangeDataNotesRepository exchangeDataNotesRepository;

    public List<ExchangeDataNotes> getExchangeNotesByExchangeId(String uniqueExchangeID) {

        return exchangeDataNotesRepository.findByUniqueExchangeIdOrderByCreateTimeStamp(uniqueExchangeID).stream()
                .map(
                        this::mapExchangeDataNotesFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public ExchangeDataNotes saveExchangeDataNotes(ExchangeDataNotes exchangeDataNotes) {
        ExchangeDataNotesDbModel exchangeDataNotesDbModel = mapExchangeDataNotesToDbModel(exchangeDataNotes);
        exchangeDataNotesDbModel = exchangeDataNotesRepository.save(exchangeDataNotesDbModel);

        return mapExchangeDataNotesFromDbModel(exchangeDataNotesDbModel);
    }


    private ExchangeDataNotes mapExchangeDataNotesFromDbModel(ExchangeDataNotesDbModel exchangeDataNotesDbModel){

        return ExchangeDataNotes.builder()
                .uniqueExchangeId(exchangeDataNotesDbModel.getUniqueExchangeId())
                .userNotes(exchangeDataNotesDbModel.getUserNotes())
                .createTimeStamp(exchangeDataNotesDbModel.getCreateTimeStamp())
                .build();

    }

    private ExchangeDataNotesDbModel mapExchangeDataNotesToDbModel(ExchangeDataNotes exchangeDataNotes){

        return ExchangeDataNotesDbModel.builder()
                .uniqueExchangeId(exchangeDataNotes.getUniqueExchangeId())
                .userNotes(exchangeDataNotes.getUserNotes())
                .createTimeStamp(exchangeDataNotes.getCreateTimeStamp() == null ? new Date() : exchangeDataNotes.getCreateTimeStamp())
                .build();

    }

}
