/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.exchangeDataRepo;

import com.ybm.exchangeDataRepo.dbRepository.RuleLogsRepository;
import com.ybm.exchangeDataRepo.entities.RuleLogsDbModel;
import com.ybm.exchangeDataRepo.models.RuleLogs;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleLogsService {

    @Autowired
    private RuleLogsRepository ruleLogsRepository;

    public List<RuleLogs> getRuleLogsByExchangeId(String uniqueExchangeID) {

        return ruleLogsRepository.findByUniqueExchangeIdOrderByCreateTimeStamp(uniqueExchangeID).stream()
                .map(
                        this::mapRuleLogsFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public RuleLogs saveRuleLogs(RuleLogs ruleLogs) {
        RuleLogsDbModel ruleLogsDbModel = mapRuleLogsToDbModel(ruleLogs);
        ruleLogsDbModel = ruleLogsRepository.save(ruleLogsDbModel);

        return mapRuleLogsFromDbModel(ruleLogsDbModel);
    }


    private RuleLogs mapRuleLogsFromDbModel(RuleLogsDbModel ruleLogsDbModel){

        return RuleLogs.builder()
                .uniqueExchangeId(ruleLogsDbModel.getUniqueExchangeId())
                .ruleId(ruleLogsDbModel.getRuleId())
                .previousData(ruleLogsDbModel.getPreviousData())
                .actionedData(ruleLogsDbModel.getActionedData())
                .previousHeaders(ruleLogsDbModel.getPreviousHeaders())
                .actionedHeaders(ruleLogsDbModel.getActionedHeaders())
                .properties(ruleLogsDbModel.getProperties())
                .extensionData(ruleLogsDbModel.getExtensionData())
                .createTimeStamp(ruleLogsDbModel.getCreateTimeStamp())
                .build();

    }

    private RuleLogsDbModel mapRuleLogsToDbModel(RuleLogs ruleLogs){

        return RuleLogsDbModel.builder()
                .uniqueExchangeId(ruleLogs.getUniqueExchangeId())
                .ruleId(ruleLogs.getRuleId())
                .previousData(ruleLogs.getPreviousData())
                .actionedData(ruleLogs.getActionedData())
                .previousHeaders(ruleLogs.getPreviousHeaders())
                .actionedHeaders(ruleLogs.getActionedHeaders())
                .properties(ruleLogs.getProperties())
                .extensionData(ruleLogs.getExtensionData())
                .createTimeStamp(ruleLogs.getCreateTimeStamp() == null ? new Date() : ruleLogs.getCreateTimeStamp())
                .build();

    }

}
