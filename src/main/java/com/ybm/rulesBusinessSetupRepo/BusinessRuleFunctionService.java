/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleFunctionRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleFunctionDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleFunction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleFunctionService {

    @Autowired
    private BLRuleFunctionRepository blRuleFunctionRepository;

    public List<BusinessLogicRuleFunction> getAllRuleFunction() {
        return blRuleFunctionRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionFromDbModel
                )
                .collect(Collectors.toList());
    }

    public BusinessLogicRuleFunction getRuleFunction(String functionId) {
        Optional<BLRuleFunctionDbModel> blRuleFunctionDbModel = blRuleFunctionRepository.findById(functionId);
        return blRuleFunctionDbModel.map(this::mapRuleFunctionFromDbModel).orElse(null);
    }

    @Transactional
    public BusinessLogicRuleFunction saveRuleFunction(BusinessLogicRuleFunction businessLogicRuleFunction) {
        BLRuleFunctionDbModel blRuleFunctionDbModel = mapRuleFunctionToDbModel(businessLogicRuleFunction);
        blRuleFunctionDbModel = blRuleFunctionRepository.save(blRuleFunctionDbModel);
        return mapRuleFunctionFromDbModel(blRuleFunctionDbModel);
    }

    public List<BusinessLogicRuleFunction> removeRuleFunctionById(String functionId) {

        blRuleFunctionRepository.deleteById(functionId);

        return blRuleFunctionRepository.findAll().stream()
                .map(
                        this::mapRuleFunctionFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleFunction mapRuleFunctionFromDbModel(BLRuleFunctionDbModel blRuleFunctionDbModel){

        return BusinessLogicRuleFunction.builder()
                .functionId(blRuleFunctionDbModel.getFunctionId())
                .functionName(blRuleFunctionDbModel.getFunctionName())
                .functionParameters(blRuleFunctionDbModel.getFunctionParameters())
                .functionLogic(blRuleFunctionDbModel.getFunctionLogic())
                .description(blRuleFunctionDbModel.getDescription())
                .status(blRuleFunctionDbModel.getStatus())
                .createTimeStamp(blRuleFunctionDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleFunctionDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleFunctionDbModel mapRuleFunctionToDbModel(BusinessLogicRuleFunction businessLogicRuleFunction){

        return BLRuleFunctionDbModel.builder()
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
