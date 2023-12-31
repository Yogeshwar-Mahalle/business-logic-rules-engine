/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleValueListRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleValueListDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleValueList;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleValueListService {

    @Autowired
    private BLRuleValueListRepository blRuleValueListRepository;

    public List<BusinessLogicRuleValueList> getAllRuleValueList() {
        return blRuleValueListRepository.findAll().stream()
                .map(
                        this::mapRuleValueListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleValueList> getRuleValueListByDataType(String dataType) {
        return blRuleValueListRepository.findByDataTypeOrderBySequenceNumber(dataType).stream()
                .map(
                        this::mapRuleValueListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleValueList getRuleValueByDataTypeAndKeyField(String dataType, String keyField) {

        Optional<BLRuleValueListDbModel> blRuleValueListDbModel = blRuleValueListRepository.findByDataTypeAndKeyField(dataType, keyField);
        return blRuleValueListDbModel.map(this::mapRuleValueListFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleValueList saveRuleValue(BusinessLogicRuleValueList businessLogicRuleValue) {
        BLRuleValueListDbModel blRuleValueListDbModel = mapRuleValueListToDbModel(businessLogicRuleValue);
        blRuleValueListDbModel = blRuleValueListRepository.save(blRuleValueListDbModel);
        return mapRuleValueListFromDbModel(blRuleValueListDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleValueList> saveRuleValuesList(List<BusinessLogicRuleValueList> businessLogicRuleValueLists) {

        List<BLRuleValueListDbModel> listBLRuleValueListDbModel = businessLogicRuleValueLists.stream()
                .map(
                        this::mapRuleValueListToDbModel
                )
                .collect(Collectors.toList());

        return blRuleValueListRepository.saveAll(listBLRuleValueListDbModel).stream()
                .map(
                        this::mapRuleValueListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleValueList> removeRuleValueByFieldId(String dataType, String keyField) {
        blRuleValueListRepository.deleteByDataTypeAndKeyField(dataType, keyField);

        return blRuleValueListRepository.findAll().stream()
                .map(
                        this::mapRuleValueListFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleValueList mapRuleValueListFromDbModel(BLRuleValueListDbModel blRuleValueListDbModel){

        return BusinessLogicRuleValueList.builder()
                .linkedEntity(blRuleValueListDbModel.getLinkedEntity())
                .dataType(blRuleValueListDbModel.getDataType())
                .keyField(blRuleValueListDbModel.getKeyField())
                .valueField(blRuleValueListDbModel.getValueField())
                .sequenceNumber(blRuleValueListDbModel.getSequenceNumber())
                .status(StatusType.valueOf(blRuleValueListDbModel.getStatus()))
                .createTimeStamp(blRuleValueListDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleValueListDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleValueListDbModel mapRuleValueListToDbModel(BusinessLogicRuleValueList businessLogicRuleValueList){

        return BLRuleValueListDbModel.builder()
                .linkedEntity(businessLogicRuleValueList.getLinkedEntity())
                .dataType(businessLogicRuleValueList.getDataType())
                .keyField(businessLogicRuleValueList.getKeyField())
                .valueField(businessLogicRuleValueList.getValueField())
                .sequenceNumber(businessLogicRuleValueList.getSequenceNumber())
                .status(String.valueOf(businessLogicRuleValueList.getStatus()))
                .createTimeStamp(businessLogicRuleValueList.getCreateTimeStamp() == null ? new Date() : businessLogicRuleValueList.getCreateTimeStamp())
                .updateTimeStamp(businessLogicRuleValueList.getUpdateTimeStamp())
                .build();

    }


}
