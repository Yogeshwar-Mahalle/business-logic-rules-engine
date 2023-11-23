/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.rulesBusinessSetupRepo;

import com.ybm.rulesBusinessSetupRepo.dbRepository.BLRuleAuditRepository;
import com.ybm.rulesBusinessSetupRepo.entities.BLRuleAuditDbModel;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleAudit;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessRuleAuditService {

    @Autowired
    private BLRuleAuditRepository blRuleAuditRepository;

    public List<BusinessLogicRuleAudit> getRuleAuditById(String ruleAuditId) {
        if( ruleAuditId == null )
            return null;

        return blRuleAuditRepository.findAllByRuleAuditId(ruleAuditId).stream()
                .map(
                        this::mapRuleAuditFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BusinessLogicRuleAudit> saveRuleAudits(List<BusinessLogicRuleAudit> businessLogicRuleAudits) {

        List<BLRuleAuditDbModel> listBLRuleAuditDbModel = businessLogicRuleAudits.stream()
                .map(
                        this::mapRuleAuditToDbModel
                )
                .collect(Collectors.toList());

        return blRuleAuditRepository.saveAll(listBLRuleAuditDbModel).stream()
                .map(
                        this::mapRuleAuditFromDbModel
                )
                .collect(Collectors.toList());
    }


    private BusinessLogicRuleAudit mapRuleAuditFromDbModel(BLRuleAuditDbModel blRuleAuditDbModel){

        return BusinessLogicRuleAudit.builder()
                .ruleAuditId(blRuleAuditDbModel.getRuleAuditId())
                .changedByUserId(blRuleAuditDbModel.getChangedByUserId())
                .blRuleId(blRuleAuditDbModel.getBlRuleId())
                .changeDateTime(blRuleAuditDbModel.getChangeDateTime())
                .changeRemark(blRuleAuditDbModel.getChangeRemark())
                .approvedByUserId(blRuleAuditDbModel.getApprovedByUserId())
                .approvedDateTime(blRuleAuditDbModel.getApprovedDateTime())
                .build();

    }

    private BLRuleAuditDbModel mapRuleAuditToDbModel(BusinessLogicRuleAudit businessLogicRuleAudit){

        return BLRuleAuditDbModel.builder()
                .ruleAuditId(businessLogicRuleAudit.getRuleAuditId())
                .changedByUserId(businessLogicRuleAudit.getChangedByUserId())
                .blRuleId(businessLogicRuleAudit.getBlRuleId())
                .changeDateTime(businessLogicRuleAudit.getChangeDateTime() == null ? new Date() : businessLogicRuleAudit.getChangeDateTime())
                .changeRemark(businessLogicRuleAudit.getChangeRemark())
                .approvedByUserId(businessLogicRuleAudit.getApprovedByUserId())
                .approvedDateTime(businessLogicRuleAudit.getApprovedDateTime())
                .build();

    }

}
