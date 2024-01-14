/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleEntityPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleEntityPnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleEntityPnAprvlService {

    @Autowired
    private BLRuleEntityPnAprvlRepository blRuleEntityPnAprvlRepository;

    public List<BusinessLogicRuleEntity> getAllPnAprvlEntities() {
        return blRuleEntityPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleEntityPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }
    public BusinessLogicRuleEntity getEntityPnAprvlByEntityName(String entityName) {
        Optional<BLRuleEntityPnAprvlDbModel> blRuleEntityPnAprvlDbModel = blRuleEntityPnAprvlRepository.findByEntityName(entityName);
        return blRuleEntityPnAprvlDbModel.map(this::mapRuleEntityPnAprvlFromDbModel).orElse(null);
    }


    @Transactional
    public BusinessLogicRuleEntity saveRuleEntityPnAprvl(BusinessLogicRuleEntity businessLogicRuleEntity) {
        BLRuleEntityPnAprvlDbModel blRuleEntityPnAprvlDbModel = mapRuleEntityPnAprvlToDbModel(businessLogicRuleEntity);
        blRuleEntityPnAprvlDbModel = blRuleEntityPnAprvlRepository.save(blRuleEntityPnAprvlDbModel);
        return mapRuleEntityPnAprvlFromDbModel(blRuleEntityPnAprvlDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleEntity> saveRuleEntitiesPnAprvl(List<BusinessLogicRuleEntity> businessLogicRuleEntities) {

        List<BLRuleEntityPnAprvlDbModel> listBLRuleEntityPnAprvlDBModel = businessLogicRuleEntities.stream()
                .map(
                        this::mapRuleEntityPnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleEntityPnAprvlRepository.saveAll(listBLRuleEntityPnAprvlDBModel).stream()
                .map(
                        this::mapRuleEntityPnAprvlFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public List<BusinessLogicRuleEntity> removeRuleEntityPnAprvlByEntityName(String entityName) {

        blRuleEntityPnAprvlRepository.deleteByEntityName(entityName);

        return blRuleEntityPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleEntityPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleEntity mapRuleEntityPnAprvlFromDbModel(BLRuleEntityPnAprvlDbModel blRuleEntityPnAprvlDbModel){

        return BusinessLogicRuleEntity.builder()
                .entityName(blRuleEntityPnAprvlDbModel.getEntityName())
                .description(blRuleEntityPnAprvlDbModel.getDescription())
                .countryCode(blRuleEntityPnAprvlDbModel.getCountryCode())
                .currencyCode(blRuleEntityPnAprvlDbModel.getCurrencyCode())
                .businessDate(blRuleEntityPnAprvlDbModel.getBusinessDate())
                .timeZone(blRuleEntityPnAprvlDbModel.getTimeZone())
                .dataStorageFlag(blRuleEntityPnAprvlDbModel.getDataStorageFlag())
                .status(StatusType.valueOf(blRuleEntityPnAprvlDbModel.getStatus()))
                .createTimeStamp(blRuleEntityPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleEntityPnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleEntityPnAprvlDbModel mapRuleEntityPnAprvlToDbModel(BusinessLogicRuleEntity businessLogicRuleEntity){

        return BLRuleEntityPnAprvlDbModel.builder()
                .entityName(businessLogicRuleEntity.getEntityName())
                .description(businessLogicRuleEntity.getDescription())
                .countryCode(businessLogicRuleEntity.getCountryCode())
                .currencyCode(businessLogicRuleEntity.getCurrencyCode())
                .businessDate(businessLogicRuleEntity.getBusinessDate() == null ? new Date() : businessLogicRuleEntity.getBusinessDate())
                .timeZone(businessLogicRuleEntity.getTimeZone())
                .dataStorageFlag(businessLogicRuleEntity.getDataStorageFlag())
                .status(String.valueOf(businessLogicRuleEntity.getStatus()))
                .createTimeStamp(businessLogicRuleEntity.getCreateTimeStamp() == null ? new Date() : businessLogicRuleEntity.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
