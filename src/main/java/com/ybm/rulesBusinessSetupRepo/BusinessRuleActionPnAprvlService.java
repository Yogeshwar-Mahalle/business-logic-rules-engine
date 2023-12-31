/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.google.common.base.Enums;
import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleActionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleActionPnAprvlDbModel;
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
public class BusinessRuleActionPnAprvlService {

    @Autowired
    private BLRuleActionPnAprvlRepository blRuleActionPnAprvlRepository;

    public List<BusinessLogicRuleAction> getRuleActionById(String ruleId) {
        if( ruleId == null )
            return null;

        return blRuleActionPnAprvlRepository.findAllByRuleId(ruleId).stream()
                .map(
                        this::mapRuleActionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAction> saveRuleActions(List<BusinessLogicRuleAction> businessLogicRuleActions) {

        List<BLRuleActionPnAprvlDbModel> listBLRuleActionPnAprvlDbModel = businessLogicRuleActions.stream()
                .map(
                        this::mapRuleActionPnAprvlToDbModel
                )
                .collect(Collectors.toList());

        return blRuleActionPnAprvlRepository.saveAll(listBLRuleActionPnAprvlDbModel).stream()
                .map(
                        this::mapRuleActionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAction> removeActionsByRuleId(String ruleId) {
        if( ruleId == null )
            return null;

        blRuleActionPnAprvlRepository.deleteByRuleId(ruleId);

        return blRuleActionPnAprvlRepository.findAll().stream()
                .map(
                        this::mapRuleActionPnAprvlFromDbModel
                )
                .collect(Collectors.toList());
    }

    private BusinessLogicRuleAction mapRuleActionPnAprvlFromDbModel(BLRuleActionPnAprvlDbModel blRuleActionPnAprvlDbModel){

        ExchangeObjectType assigneeDataObject = null;
        if(blRuleActionPnAprvlDbModel.getAssigneeDataObject() != null)
            assigneeDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleActionPnAprvlDbModel.getAssigneeDataObject().toUpperCase())
                    .or(ExchangeObjectType.OUTPUT_PAYLOAD);

        ExchangeObjectType assignorDataObject = null;
        if(blRuleActionPnAprvlDbModel.getAssignorDataObject() != null)
            assignorDataObject = Enums.getIfPresent(ExchangeObjectType.class, blRuleActionPnAprvlDbModel.getAssignorDataObject().toUpperCase())
                    .or(ExchangeObjectType.OUTPUT_PAYLOAD);

        OperandType assigneeType = null;
        if(blRuleActionPnAprvlDbModel.getAssigneeType() != null)
            assigneeType = Enums.getIfPresent(OperandType.class, blRuleActionPnAprvlDbModel.getAssigneeType().toUpperCase())
                    .or(OperandType.EXCHANGE);

        OperandType assignorType = null;
        if(blRuleActionPnAprvlDbModel.getAssignorType() != null)
            assignorType = Enums.getIfPresent(OperandType.class, blRuleActionPnAprvlDbModel.getAssignorType().toUpperCase())
                    .or(OperandType.EXCHANGE);

        return BusinessLogicRuleAction.builder()
                .ruleActionId(blRuleActionPnAprvlDbModel.getRuleActionId())
                .ruleId(blRuleActionPnAprvlDbModel.getRuleId())
                .sequenceNumber(blRuleActionPnAprvlDbModel.getSequenceNumber())
                .assigneeDataObject(assigneeDataObject)
                .assignee(blRuleActionPnAprvlDbModel.getAssignee())
                .assigneeType(assigneeType)
                .assignorDataObject(assignorDataObject)
                .assignor(blRuleActionPnAprvlDbModel.getAssignor())
                .assignorType(assignorType)
                .includeFuncNameList(blRuleActionPnAprvlDbModel.getIncludeFuncNameList())
                .createTimeStamp(blRuleActionPnAprvlDbModel.getCreateTimeStamp())
                .updateTimeStamp(blRuleActionPnAprvlDbModel.getUpdateTimeStamp())
                .build();

    }

    private BLRuleActionPnAprvlDbModel mapRuleActionPnAprvlToDbModel(BusinessLogicRuleAction businessLogicRuleAction){

        return BLRuleActionPnAprvlDbModel.builder()
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
