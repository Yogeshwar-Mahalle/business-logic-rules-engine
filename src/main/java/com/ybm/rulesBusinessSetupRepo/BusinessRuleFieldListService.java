/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleFieldListRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFieldListDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFieldList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleFieldListService {

    @Autowired
    private BLRuleFieldListRepository blRuleFieldListRepository;

    public List<BusinessLogicRuleFieldList> getAllRuleFieldList() {
        return blRuleFieldListRepository.findAll().stream()
                .map(
                        this::mapRuleFieldListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleFieldList> getRuleFieldListByDataType(String ruleType) {
        return blRuleFieldListRepository.findByRuleType(ruleType).stream()
                .map(
                        this::mapRuleFieldListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleFieldList getRuleValueByDataTypeAndKeyField(String ruleType, String fieldName) {

        Optional<BLRuleFieldListDbModel> blRuleFieldListDbModel = blRuleFieldListRepository.findByRuleTypeAndFieldName(ruleType, fieldName);
        return blRuleFieldListDbModel.map(this::mapRuleFieldListFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleFieldList saveRuleValue(BusinessLogicRuleFieldList businessLogicRuleValue) {
        BLRuleFieldListDbModel blRuleFieldListDbModel = mapRuleFieldListToDbModel(businessLogicRuleValue);
        blRuleFieldListDbModel = blRuleFieldListRepository.save(blRuleFieldListDbModel);
        return mapRuleFieldListFromDbModel(blRuleFieldListDbModel);
    }

    @Transactional
    public List<BusinessLogicRuleFieldList> saveRuleValuesList(List<BusinessLogicRuleFieldList> businessLogicRuleFieldLists) {

        List<BLRuleFieldListDbModel> listBLRuleFieldListDbModel = businessLogicRuleFieldLists.stream()
                .map(
                        this::mapRuleFieldListToDbModel
                )
                .collect(Collectors.toList());

        return blRuleFieldListRepository.saveAll(listBLRuleFieldListDbModel).stream()
                .map(
                        this::mapRuleFieldListFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<BusinessLogicRuleFieldList> removeRuleValueByFieldId(String ruleType, String keyField) {
        blRuleFieldListRepository.deleteByRuleTypeAndFieldName(ruleType, keyField);

        return blRuleFieldListRepository.findAll().stream()
                .map(
                        this::mapRuleFieldListFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleFieldList mapRuleFieldListFromDbModel(BLRuleFieldListDbModel blRuleFieldListDbModel){

        return BusinessLogicRuleFieldList.builder()
                .ruleType(blRuleFieldListDbModel.getDataType())
                .fieldName(blRuleFieldListDbModel.getFieldName())
                .description(blRuleFieldListDbModel.getDescription())
                .labelName(blRuleFieldListDbModel.getLabelName())
                .dataType(blRuleFieldListDbModel.getDataType())
                .dataFormat(blRuleFieldListDbModel.getDataFormat())
                .defaultValue(blRuleFieldListDbModel.getDefaultValue())
                .status(blRuleFieldListDbModel.getStatus())
                .createTimeStamp(blRuleFieldListDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleFieldListDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleFieldListDbModel mapRuleFieldListToDbModel(BusinessLogicRuleFieldList businessLogicRuleFieldList){

        return BLRuleFieldListDbModel.builder()
                .ruleType(businessLogicRuleFieldList.getDataType())
                .fieldName(businessLogicRuleFieldList.getFieldName())
                .description(businessLogicRuleFieldList.getDescription())
                .labelName(businessLogicRuleFieldList.getLabelName())
                .dataType(businessLogicRuleFieldList.getDataType())
                .dataFormat(businessLogicRuleFieldList.getDataFormat())
                .defaultValue(businessLogicRuleFieldList.getDefaultValue())
                .status(businessLogicRuleFieldList.getStatus())
                .createTimeStamp(businessLogicRuleFieldList.getCreateTimeStamp() == null ? new Date() : businessLogicRuleFieldList.getCreateTimeStamp())
                .updateTimeStamp(businessLogicRuleFieldList.getUpdateTimeStamp())
                .build();

    }


}
