/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.workflow;

import com.ybm.ruleEngine.RuleEngine;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleTypesService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class WorkflowManager {

    private final String WORKFLOW_RULE_TYPE_LIST = "workflowruletypelist";
    @Autowired
    private BusinessRuleTypesService businessRuleTypesService;

    @Autowired
    private RuleEngine ruleEngine;


    public DataExchangeObject run(DataExchangeObject dataExchangeObject) {

        List<BusinessLogicRuleType> listBusinessLogicRuleTypes = businessRuleTypesService.getAllRuleTypeByWrkFlowFlag();
        if (null == listBusinessLogicRuleTypes || listBusinessLogicRuleTypes.isEmpty()){
            return dataExchangeObject;
        }

        for ( BusinessLogicRuleType businessLogicRuleType : listBusinessLogicRuleTypes ) {

            DataExchangeObject wrkFlowRules = ruleEngine.run(businessLogicRuleType.getRuleType(), dataExchangeObject);

            String strRuleTypeList = (String) wrkFlowRules.getDataObject().getPayload().getDataMap().get(WORKFLOW_RULE_TYPE_LIST);
            if( strRuleTypeList != null )
            {
                List<String> ruleTypeList = Stream.of(strRuleTypeList.split(","))
                        .map(String::trim)
                        .toList();
                for ( String ruleType : ruleTypeList ) {

                    //TODO: Implementation of WorkFlow RuleTypes sequence flow pending by using java stream or chain function.
                    dataExchangeObject = ruleEngine.run(ruleType, dataExchangeObject);

                }
            }
        }

        return dataExchangeObject;
    }
}
