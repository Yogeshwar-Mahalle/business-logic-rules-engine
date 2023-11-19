/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleActionPnAprvlRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleActionPnAprvlDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleAction;
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


    private BusinessLogicRuleAction mapRuleActionPnAprvlFromDbModel(BLRuleActionPnAprvlDbModel blRuleActionPnAprvlDbModel){

        return BusinessLogicRuleAction.builder()
                .ruleActionId(blRuleActionPnAprvlDbModel.getRuleActionId())
                .ruleId(blRuleActionPnAprvlDbModel.getRuleId())
                .sequenceNumber(blRuleActionPnAprvlDbModel.getSequenceNumber())
                .ruleConditionId(blRuleActionPnAprvlDbModel.getRuleConditionId())
                .assignee(blRuleActionPnAprvlDbModel.getAssignee())
                .assignor(blRuleActionPnAprvlDbModel.getAssignor())
                .otherAssignor(blRuleActionPnAprvlDbModel.getOtherAssignor())
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
                .ruleConditionId(businessLogicRuleAction.getRuleConditionId())
                .assignee(businessLogicRuleAction.getAssignee())
                .assignor(businessLogicRuleAction.getAssignor())
                .otherAssignor(businessLogicRuleAction.getOtherAssignor())
                .createTimeStamp(businessLogicRuleAction.getCreateTimeStamp() == null ? new Date() : businessLogicRuleAction.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }

}
