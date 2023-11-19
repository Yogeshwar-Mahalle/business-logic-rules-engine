/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleFunctionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionPnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleFunctionPnAprvlService {

    @Autowired
    private BLRuleFunctionPnAprvlRepository blRuleFunctionPnAprvlRepository;

    public List<BusinessLogicRuleFunction> getAllRuleFunction() {
        return blRuleFunctionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleFunction getRuleFunction(String functionId) {
        Optional<BLRuleFunctionPnAprvlDbModel> blRuleFunctionPnAprvlDbModel = blRuleFunctionPnAprvlRepository.findById(functionId);
        return blRuleFunctionPnAprvlDbModel.map(this::mapRuleFunctionPnAprvlFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleFunction saveRuleFunction(BusinessLogicRuleFunction businessLogicRuleFunction) {
        BLRuleFunctionPnAprvlDbModel blRuleFunctionPnAprvlDbModel = mapRuleFunctionPnAprvlToDbModel(businessLogicRuleFunction);
        blRuleFunctionPnAprvlDbModel = blRuleFunctionPnAprvlRepository.save(blRuleFunctionPnAprvlDbModel);
        return mapRuleFunctionPnAprvlFromDbModel(blRuleFunctionPnAprvlDbModel);
    }

    public List<BusinessLogicRuleFunction> removeRuleFunctionById(String functionId) {

        blRuleFunctionPnAprvlRepository.deleteById(functionId);

        return blRuleFunctionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleFunction mapRuleFunctionPnAprvlFromDbModel(BLRuleFunctionPnAprvlDbModel blRuleFunctionPnAprvlDbModel){

        return BusinessLogicRuleFunction.builder()
                .functionId(blRuleFunctionPnAprvlDbModel.getFunctionId())
                .functionName(blRuleFunctionPnAprvlDbModel.getFunctionName())
                .functionParameters(blRuleFunctionPnAprvlDbModel.getFunctionParameters())
                .functionLogic(blRuleFunctionPnAprvlDbModel.getFunctionLogic())
                .description(blRuleFunctionPnAprvlDbModel.getDescription())
                .status(blRuleFunctionPnAprvlDbModel.getStatus())
                .createTimeStamp(blRuleFunctionPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleFunctionPnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleFunctionPnAprvlDbModel mapRuleFunctionPnAprvlToDbModel(BusinessLogicRuleFunction businessLogicRuleFunction){

        return BLRuleFunctionPnAprvlDbModel.builder()
                .functionId( businessLogicRuleFunction.getFunctionId() == null ?
                        businessLogicRuleFunction.getFunctionName() + "~" + businessLogicRuleFunction.getFunctionParameters().hashCode() :
                        businessLogicRuleFunction.getFunctionId() )
                .functionName(businessLogicRuleFunction.getFunctionName())
                .functionParameters(businessLogicRuleFunction.getFunctionParameters())
                .functionLogic(businessLogicRuleFunction.getFunctionLogic())
                .description(businessLogicRuleFunction.getDescription())
                .status(businessLogicRuleFunction.getStatus())
                .createTimeStamp(businessLogicRuleFunction.getCreateTimeStamp() == null ? new Date() : businessLogicRuleFunction.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
