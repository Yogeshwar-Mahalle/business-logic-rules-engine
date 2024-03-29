/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleValueListPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleValueListPnAprvlDbModel;
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
public class BusinessRuleValueListPnAprvlService {

    @Autowired
    private BLRuleValueListPnAprvlRepository blRuleValueListPnAprvlRepository;

    public List<BusinessLogicRuleValueList> getAllRuleValueList() {
        return blRuleValueListPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleValueListPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleValueList> getRuleValueListByDataType(String dataType) {
        return blRuleValueListPnAprvlRepository.findByDataTypeOrderBySequenceNumber(dataType).stream()
                .map(
                        this::mapRuleValueListPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleValueList getRuleValueByDataTypeAndKeyField(String dataType, String keyField) {

        Optional<BLRuleValueListPnAprvlDbModel> blRuleValueListDbModel = blRuleValueListPnAprvlRepository.findByDataTypeAndKeyField(dataType, keyField);
        return blRuleValueListDbModel.map(this::mapRuleValueListPnAprvlFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleValueList saveRuleValue(BusinessLogicRuleValueList businessLogicRuleValue) {
        BLRuleValueListPnAprvlDbModel blRuleValueListPnAprvlDbModel = mapRuleValueListPnAprvlToDbModel(businessLogicRuleValue);
        blRuleValueListPnAprvlDbModel = blRuleValueListPnAprvlRepository.save(blRuleValueListPnAprvlDbModel);
        return mapRuleValueListPnAprvlFromDbModel(blRuleValueListPnAprvlDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleValueList> saveRuleValuesList(List<BusinessLogicRuleValueList> businessLogicRuleValueLists) {

        List<BLRuleValueListPnAprvlDbModel> listBLRuleValueListDbModel = businessLogicRuleValueLists.stream()
                .map(
                        this::mapRuleValueListPnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleValueListPnAprvlRepository.saveAll(listBLRuleValueListDbModel).stream()
                .map(
                        this::mapRuleValueListPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleValueList> removeRuleValueByFieldId(String dataType, String keyField) {
        blRuleValueListPnAprvlRepository.deleteByDataTypeAndKeyField(dataType, keyField);

        return blRuleValueListPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleValueListPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleValueList mapRuleValueListPnAprvlFromDbModel(BLRuleValueListPnAprvlDbModel blRuleValueListPnAprvlDbModel){

        return BusinessLogicRuleValueList.builder()
                .linkedEntity(blRuleValueListPnAprvlDbModel.getLinkedEntity())
                .listType(blRuleValueListPnAprvlDbModel.getListType())
                .keyField(blRuleValueListPnAprvlDbModel.getKeyField())
                .dataType(blRuleValueListPnAprvlDbModel.getDataType())
                .valueField(blRuleValueListPnAprvlDbModel.getValueField())
                .sequenceNumber(blRuleValueListPnAprvlDbModel.getSequenceNumber())
                .status(StatusType.valueOf(blRuleValueListPnAprvlDbModel.getStatus()))
                .createTimeStamp(blRuleValueListPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleValueListPnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleValueListPnAprvlDbModel mapRuleValueListPnAprvlToDbModel(BusinessLogicRuleValueList businessLogicRuleValueList){

        return BLRuleValueListPnAprvlDbModel.builder()
                .linkedEntity(businessLogicRuleValueList.getLinkedEntity())
                .listType(businessLogicRuleValueList.getListType())
                .keyField(businessLogicRuleValueList.getKeyField())
                .dataType(businessLogicRuleValueList.getDataType())
                .valueField(businessLogicRuleValueList.getValueField())
                .sequenceNumber(businessLogicRuleValueList.getSequenceNumber())
                .status(String.valueOf(businessLogicRuleValueList.getStatus()))
                .createTimeStamp(businessLogicRuleValueList.getCreateTimeStamp() == null ? new Date() : businessLogicRuleValueList.getCreateTimeStamp())
                .updateTimeStamp(businessLogicRuleValueList.getUpdateTimeStamp())
                .build();

    }


}
