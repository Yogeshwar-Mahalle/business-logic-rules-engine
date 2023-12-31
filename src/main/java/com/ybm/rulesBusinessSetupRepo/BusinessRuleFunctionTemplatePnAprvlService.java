/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleFunctionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionTemplatePnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunctionTemplate;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleFunctionTemplatePnAprvlService {

    @Autowired
    private BLRuleFunctionPnAprvlRepository blRuleFunctionPnAprvlRepository;

    public List<BusinessLogicRuleFunctionTemplate> getAllRuleFunction() {
        return blRuleFunctionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionTemplatePnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleFunctionTemplate getRuleFunction(String functionId) {
        if( functionId == null )
            return null;

        Optional<BLRuleFunctionTemplatePnAprvlDbModel> blRuleFunctionPnAprvlDbModel = blRuleFunctionPnAprvlRepository.findById(functionId);
        return blRuleFunctionPnAprvlDbModel.map(this::mapRuleFunctionTemplatePnAprvlFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleFunctionTemplate saveRuleFunction(BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate) {
        BLRuleFunctionTemplatePnAprvlDbModel blRuleFunctionTemplatePnAprvlDbModel = mapRuleFunctionTemplatePnAprvlToDbModel(businessLogicRuleFunctionTemplate);
        blRuleFunctionTemplatePnAprvlDbModel = blRuleFunctionPnAprvlRepository.save(blRuleFunctionTemplatePnAprvlDbModel);
        return mapRuleFunctionTemplatePnAprvlFromDbModel(blRuleFunctionTemplatePnAprvlDbModel);
    }

    public List<BusinessLogicRuleFunctionTemplate> removeRuleFunctionById(String functionId) {
        if( functionId == null )
            return null;

        blRuleFunctionPnAprvlRepository.deleteById(functionId);

        return blRuleFunctionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionTemplatePnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleFunctionTemplate mapRuleFunctionTemplatePnAprvlFromDbModel(BLRuleFunctionTemplatePnAprvlDbModel blRuleFunctionTemplatePnAprvlDbModel){

        return BusinessLogicRuleFunctionTemplate.builder()
                .functionId(blRuleFunctionTemplatePnAprvlDbModel.getFunctionId())
                .linkedEntity(blRuleFunctionTemplatePnAprvlDbModel.getLinkedEntity())
                .functionName(blRuleFunctionTemplatePnAprvlDbModel.getFunctionName())
                .description(blRuleFunctionTemplatePnAprvlDbModel.getDescription())
                .functionParameters(blRuleFunctionTemplatePnAprvlDbModel.getFunctionParameters())
                .includeFunctionsNameList(blRuleFunctionTemplatePnAprvlDbModel.getIncludeFunctionsNameList())
                .functionLogic(blRuleFunctionTemplatePnAprvlDbModel.getFunctionLogic())
                .status(StatusType.valueOf(blRuleFunctionTemplatePnAprvlDbModel.getStatus()))
                .createTimeStamp(blRuleFunctionTemplatePnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleFunctionTemplatePnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleFunctionTemplatePnAprvlDbModel mapRuleFunctionTemplatePnAprvlToDbModel(BusinessLogicRuleFunctionTemplate businessLogicRuleFunctionTemplate){

        return BLRuleFunctionTemplatePnAprvlDbModel.builder()
                .functionId( businessLogicRuleFunctionTemplate.getFunctionId() == null ?
                        businessLogicRuleFunctionTemplate.getLinkedEntity() + "~" +
                                businessLogicRuleFunctionTemplate.getFunctionName() + "~" +
                                businessLogicRuleFunctionTemplate.getFunctionParameters().hashCode() :
                        businessLogicRuleFunctionTemplate.getFunctionId() )
                .linkedEntity(businessLogicRuleFunctionTemplate.getLinkedEntity())
                .functionName(businessLogicRuleFunctionTemplate.getFunctionName())
                .description(businessLogicRuleFunctionTemplate.getDescription())
                .functionParameters(businessLogicRuleFunctionTemplate.getFunctionParameters())
                .includeFunctionsNameList(businessLogicRuleFunctionTemplate.getIncludeFunctionsNameList())
                .functionLogic(businessLogicRuleFunctionTemplate.getFunctionLogic())
                .status(String.valueOf(businessLogicRuleFunctionTemplate.getStatus()))
                .createTimeStamp(businessLogicRuleFunctionTemplate.getCreateTimeStamp() == null ? new Date() : businessLogicRuleFunctionTemplate.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
