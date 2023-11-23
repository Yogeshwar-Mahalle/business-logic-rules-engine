/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleFunctionTemplateRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionTemplateDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunctionTemplate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleFunctionTemplateService {

    @Autowired
    private BLRuleFunctionTemplateRepository blRuleFunctionTemplateRepository;

    public List<BusinessLogicRuleFunctionTemplate> getAllRuleFunction() {
        return blRuleFunctionTemplateRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionTemplateFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleFunctionTemplate getRuleFunction(String functionId) {
        if( functionId == null )
            return null;

        Optional<BLRuleFunctionTemplateDbModel> blRuleFunctionDbModel = blRuleFunctionTemplateRepository.findByFunctionId(functionId);
        return blRuleFunctionDbModel.map(this::mapRuleFunctionTemplateFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleFunctionTemplate saveRuleFunction(BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate) {
        BLRuleFunctionTemplateDbModel blRuleFunctionTemplateDbModel = mapRuleFunctionTemplateToDbModel(businessLogicRuleFunctionTemplate);
        blRuleFunctionTemplateDbModel = blRuleFunctionTemplateRepository.save(blRuleFunctionTemplateDbModel);
        return mapRuleFunctionTemplateFromDbModel(blRuleFunctionTemplateDbModel);
    }

    public List<BusinessLogicRuleFunctionTemplate> removeRuleFunctionById(String functionId) {
        if( functionId == null )
            return null;

        blRuleFunctionTemplateRepository.deleteById(functionId);

        return blRuleFunctionTemplateRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionTemplateFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleFunctionTemplate mapRuleFunctionTemplateFromDbModel(BLRuleFunctionTemplateDbModel blRuleFunctionTemplateDbModel){

        return BusinessLogicRuleFunctionTemplate.builder()
                .functionId(blRuleFunctionTemplateDbModel.getFunctionId())
                .linkedEntity(blRuleFunctionTemplateDbModel.getLinkedEntity())
                .functionName(blRuleFunctionTemplateDbModel.getFunctionName())
                .description(blRuleFunctionTemplateDbModel.getDescription())
                .functionParameters(blRuleFunctionTemplateDbModel.getFunctionParameters())
                .includeFunctionsNameList(blRuleFunctionTemplateDbModel.getIncludeFunctionsNameList())
                .functionLogic(blRuleFunctionTemplateDbModel.getFunctionLogic())
                .status(blRuleFunctionTemplateDbModel.getStatus())
                .createTimeStamp(blRuleFunctionTemplateDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleFunctionTemplateDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleFunctionTemplateDbModel mapRuleFunctionTemplateToDbModel(BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate){

        return BLRuleFunctionTemplateDbModel.builder()
                .functionId( businessLogicRuleFunctionTemplate.getFunctionId() == null ?
                                businessLogicRuleFunctionTemplate.getLinkedEntity() + "~" +
                                businessLogicRuleFunctionTemplate.getFunctionName() :
                        businessLogicRuleFunctionTemplate.getFunctionId() )
                .linkedEntity(businessLogicRuleFunctionTemplate.getLinkedEntity())
                .functionName(businessLogicRuleFunctionTemplate.getFunctionName())
                .description(businessLogicRuleFunctionTemplate.getDescription())
                .functionParameters(businessLogicRuleFunctionTemplate.getFunctionParameters())
                .includeFunctionsNameList(businessLogicRuleFunctionTemplate.getIncludeFunctionsNameList())
                .functionLogic(businessLogicRuleFunctionTemplate.getFunctionLogic())
                .status(businessLogicRuleFunctionTemplate.getStatus())
                .createTimeStamp(businessLogicRuleFunctionTemplate.getCreateTimeStamp() == null ? new Date() : businessLogicRuleFunctionTemplate.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
