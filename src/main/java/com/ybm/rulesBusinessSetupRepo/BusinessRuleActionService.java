/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.google.common.base.Enums;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleActionRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleActionDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleAction;
import com.ybm.rulesBusinessSetupRepo.models.ExchangeObjectType;
import com.ybm.rulesBusinessSetupRepo.models.OperandType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessRuleActionService {

    @Autowired
    private BLRuleActionRepository blRuleActionRepository;

    public List<BusinessLogicRuleAction> getRuleActionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        return blRuleActionRepository.findAllByRuleId(ruleId).stream()
                .map(
                        this::mapRuleActionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAction> saveRuleActions(String ruleId, List<BusinessLogicRuleAction> businessLogicRuleActions) {
        if(businessLogicRuleActions == null)
            return null;

        List<BLRuleActionDbModel> listBLRuleActionDbModel = businessLogicRuleActions.stream()
                .map(
                        blRuleActionDbModel -> {
                            blRuleActionDbModel.setRuleId(ruleId);
                            return mapRuleActionToDbModel(blRuleActionDbModel);
                        }
                )
                .collect(Collectors.toList());

        return blRuleActionRepository.saveAll(listBLRuleActionDbModel).stream()
                .map(
                        this::mapRuleActionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAction> saveRuleActions(List<BusinessLogicRuleAction> businessLogicRuleActions) {
        if(businessLogicRuleActions == null)
            return null;

        List<BLRuleActionDbModel> listBLRuleActionDbModel = businessLogicRuleActions.stream()
                .map(
                        this::mapRuleActionToDbModel
                )
                .collect(Collectors.toList());

        return blRuleActionRepository.saveAll(listBLRuleActionDbModel).stream()
                .map(
                        this::mapRuleActionFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAction> removeActionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        blRuleActionRepository.deleteByRuleId(ruleId);

        return blRuleActionRepository.findAll().stream()
                .map(
                        this::mapRuleActionFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleAction mapRuleActionFromDbModel(BLRuleActionDbModel blRuleActionDbModel){

        ExchangeObjectType assigneeDataObject = null;
        if(blRuleActionDbModel.getAssigneeDataObject() != null)
            assigneeDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleActionDbModel.getAssigneeDataObject().toUpperCase())
                    .or(ExchangeObjectType.OUTPUT_PAYLOAD);

        ExchangeObjectType assignorDataObject = null;
        if(blRuleActionDbModel.getAssignorDataObject() != null)
            assignorDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleActionDbModel.getAssignorDataObject().toUpperCase())
                    .or(ExchangeObjectType.OUTPUT_PAYLOAD);

        OperandType assigneeType = null;
        if(blRuleActionDbModel.getAssigneeType() != null)
            assigneeType = Enums.getIfPresent(OperandType.class, blRuleActionDbModel.getAssigneeType().toUpperCase())
                .or(OperandType.EXCHANGE);

        OperandType assignorType = null;
        if(blRuleActionDbModel.getAssignorType() != null)
            assignorType = Enums.getIfPresent(OperandType.class, blRuleActionDbModel.getAssignorType().toUpperCase())
                .or(OperandType.EXCHANGE);

        return BusinessLogicRuleAction.builder()
                .ruleActionId(blRuleActionDbModel.getRuleActionId())
                .ruleId(blRuleActionDbModel.getRuleId())
                .sequenceNumber(blRuleActionDbModel.getSequenceNumber())
                .assigneeDataObject(assigneeDataObject)
                .assignee(blRuleActionDbModel.getAssignee())
                .assigneeType(assigneeType)
                .assignorDataObject(assignorDataObject)
                .assignor(blRuleActionDbModel.getAssignor())
                .assignorType(assignorType)
                .includeFuncNameList(blRuleActionDbModel.getIncludeFuncNameList())
                .createTimeStamp(blRuleActionDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleActionDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleActionDbModel mapRuleActionToDbModel(BusinessLogicRuleAction businessLogicRuleAction){

        return BLRuleActionDbModel.builder()
                .ruleActionId( businessLogicRuleAction.getRuleActionId() == null ?
                        businessLogicRuleAction.getRuleId() + "~" + businessLogicRuleAction.getSequenceNumber() :
                        businessLogicRuleAction.getRuleActionId() )
                .ruleId(businessLogicRuleAction.getRuleId())
                .sequenceNumber(businessLogicRuleAction.getSequenceNumber())
                .assigneeDataObject(businessLogicRuleAction.getAssigneeDataObject() != null ?
                        businessLogicRuleAction.getAssigneeDataObject().name() : null)
                .assignee(businessLogicRuleAction.getAssignee())
                .assigneeType(businessLogicRuleAction.getAssigneeType().name())
                .assignorDataObject(businessLogicRuleAction.getAssignorDataObject() != null ?
                        businessLogicRuleAction.getAssignorDataObject().name() : null)
                .assignor(businessLogicRuleAction.getAssignor())
                .assignorType(businessLogicRuleAction.getAssignorType().name())
                .includeFuncNameList(businessLogicRuleAction.getIncludeFuncNameList())
                .createTimeStamp(businessLogicRuleAction.getCreateTimeStamp() == null ? new Date() : businessLogicRuleAction.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
