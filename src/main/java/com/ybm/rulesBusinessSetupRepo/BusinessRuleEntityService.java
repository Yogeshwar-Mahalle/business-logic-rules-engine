/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleEntityRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleEntityDbModel;
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
public class BusinessRuleEntityService {

    @Autowired
    private BLRuleEntityRepository blRuleEntityRepository;

    public List<BusinessLogicRuleEntity> getAllEntities() {
        return blRuleEntityRepository.findAll().stream()
                .map(
                        this::mapRuleEntityFromDbModel
                )
                .collect(Collectors.toList());
    }
    public BusinessLogicRuleEntity getEntityByEntityName(String entityName) {
        Optional<BLRuleEntityDbModel> blRuleEntityDbModel = blRuleEntityRepository.findByEntityName(entityName);
        return blRuleEntityDbModel.map(this::mapRuleEntityFromDbModel).orElse(null);
    }


    @Transactional
    public BusinessLogicRuleEntity saveRuleEntity(BusinessLogicRuleEntity businessLogicRuleEntity) {
        BLRuleEntityDbModel blRuleTypeDbModel = mapRuleEntityToDbModel(businessLogicRuleEntity);
        blRuleTypeDbModel = blRuleEntityRepository.save(blRuleTypeDbModel);
        return mapRuleEntityFromDbModel(blRuleTypeDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleEntity> saveRuleEntities(List<BusinessLogicRuleEntity> BusinessLogicRuleEntities) {

        List<BLRuleEntityDbModel> listBLRuleEntityDBModel = BusinessLogicRuleEntities.stream()
                .map(
                        this::mapRuleEntityToDbModel
                )
                .collect(Collectors.toList());

        return blRuleEntityRepository.saveAll(listBLRuleEntityDBModel).stream()
                .map(
                        this::mapRuleEntityFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<BusinessLogicRuleEntity> removeRuleEntityByEntityName(String entityName) {

        blRuleEntityRepository.deleteByEntityName(entityName);

        return blRuleEntityRepository.findAll().stream()
                .map(
                        this::mapRuleEntityFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleEntity mapRuleEntityFromDbModel(BLRuleEntityDbModel blRuleEntityDbModel){

        return BusinessLogicRuleEntity.builder()
                .entityName(blRuleEntityDbModel.getEntityName())
                .description(blRuleEntityDbModel.getDescription())
                .status(StatusType.valueOf(blRuleEntityDbModel.getStatus()))
                .createTimeStamp(blRuleEntityDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleEntityDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleEntityDbModel mapRuleEntityToDbModel(BusinessLogicRuleEntity businessLogicRuleEntity){

        return BLRuleEntityDbModel.builder()
                .entityName(businessLogicRuleEntity.getEntityName())
                .description(businessLogicRuleEntity.getDescription())
                .status(String.valueOf(businessLogicRuleEntity.getStatus()))
                .createTimeStamp(businessLogicRuleEntity.getCreateTimeStamp() == null ? new Date() : businessLogicRuleEntity.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
