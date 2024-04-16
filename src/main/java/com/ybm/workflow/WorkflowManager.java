/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.ruleEngine.RuleEngine;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleTypesService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class WorkflowManager {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowManager.class);

    private final String WORKFLOW_RULE_TYPE_LIST = "workflowruletypelist";
    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private BusinessRuleTypesService businessRuleTypesService;

    @Autowired
    private RuleEngine ruleEngine;


    public DataExchangeObject run(DataExchangeObject dataExchangeObject) {

        DataExchangeObject wrkflwDataExchangeObject = dataExchangeObject.copy();
        BusinessLogicRuleEntity businessLogicRuleEntity = dataExchangeObject.getBusinessLogicRuleEntity();

        List<BusinessLogicRuleType> listBusinessLogicRuleTypes =
                businessRuleTypesService.getAllRuleTypeByEntityWrkFlowFlag(businessLogicRuleEntity.getEntityName());
        if (null == listBusinessLogicRuleTypes || listBusinessLogicRuleTypes.isEmpty()){
            return dataExchangeObject;
        }

        for ( BusinessLogicRuleType businessLogicRuleType : listBusinessLogicRuleTypes ) {

            LOG.info( "CHECK WORKFLOWS RULES FOR WORKFLOW RULE TYPE : " + businessLogicRuleType.getRuleType() );

            DataExchangeObject wrkFlowRulesXchangeObj = ruleEngine.run( businessLogicRuleType.getRuleType(), wrkflwDataExchangeObject );
            wrkflwDataExchangeObject.setProperties(wrkFlowRulesXchangeObj.getProperties());
            wrkflwDataExchangeObject.setRuleLogsList(wrkFlowRulesXchangeObj.getRuleLogsList());

            String strRuleTypeList = (String) wrkFlowRulesXchangeObj.getOutDataObject().getPayload().getDataMap().get(WORKFLOW_RULE_TYPE_LIST);
            if( strRuleTypeList != null )
            {
                List<String> ruleTypeList = Stream.of(strRuleTypeList.split(","))
                        .map(String::trim)
                        .toList();
                for ( String ruleType : ruleTypeList ) {

                    wrkflwDataExchangeObject = ruleEngine.run(ruleType, wrkflwDataExchangeObject);

                    //Set output payload of previous rule to input payload of next rule in the workflow
                    wrkflwDataExchangeObject = new DataExchangeObject(
                            dataExchangeObject.getUniqueExchangeId(),
                            wrkflwDataExchangeObject.getBusinessLogicRuleEntity(),
                            wrkflwDataExchangeObject.getProperties(),
                            wrkflwDataExchangeObject.getOutDataObject(),
                            wrkflwDataExchangeObject.getOutDataObject(),
                            wrkflwDataExchangeObject.getDataExtension(),
                            wrkflwDataExchangeObject.getRuleLogsList()
                    );
                }
            }
        }

        //Set output payload of last rule to input exchange object without modification in the input payload
        dataExchangeObject.setOutDataObject(wrkflwDataExchangeObject.getOutDataObject());
        dataExchangeObject.setProperties(wrkflwDataExchangeObject.getProperties());
        dataExchangeObject.setDataExtension(wrkflwDataExchangeObject.getDataExtension());
        dataExchangeObject.setRuleLogsList(wrkflwDataExchangeObject.getRuleLogsList());

        return dataExchangeObject;
    }

    public ExchangeData mapExchangeData(BusinessLogicRuleEntity businessLogicRuleEntity, DataExchangeObject dataExchangeObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> headers = dataExchangeObject.getInDataObject().getHeaders();

        String strOrgContentType = headers.get("content-type") != null ? headers.get("content-type") : headers.get("CONTENT-TYPE");
        dataExchangeObject.getInDataObject().getHeaders().putIfAbsent("formattype", ContentType.setLabel(strOrgContentType).name());
        String sourceSys = headers.get("source") != null ? headers.get("source") : headers.get("SOURCE");
        sourceSys = sourceSys == null ? "EuclidPro" : sourceSys;

        if( businessLogicRuleEntity == null )
        {
            businessLogicRuleEntity = businessRuleEntityService.getEntityByEntityName( "EuclidPro" );
        }

        String messageId = headers.get("message_id") != null ? headers.get("message_id") : headers.get("MESSAGE_ID");
        messageId = dataExchangeObject.getProperties().get("messageId") != null ? (String) dataExchangeObject.getProperties().get("messageId") : messageId;
        String messageType = headers.get("messagetype") != null ? headers.get("messagetype") : headers.get("MESSAGETYPE");
        messageType = dataExchangeObject.getProperties().get("messageType") != null ? (String) dataExchangeObject.getProperties().get("messageType") : messageType;

        dataExchangeObject.getProperties().putIfAbsent("entity", businessLogicRuleEntity.getEntityName());
        dataExchangeObject.getProperties().putIfAbsent("source", sourceSys);
        dataExchangeObject.getProperties().putIfAbsent("formatType", ContentType.setLabel(strOrgContentType).name());
        dataExchangeObject.getProperties().putIfAbsent("messageType", messageType == null ? "MESSAGE" : messageType);
        dataExchangeObject.getProperties().putIfAbsent("messageId", messageId == null ? dataExchangeObject.getUniqueExchangeId() : messageId);


        String strProperties = dataExchangeObject.getProperties().toString();
        String strOrgHeaders = dataExchangeObject.getInDataObject().getHeaders().toString();
        String strProcessedHeaders = dataExchangeObject.getOutDataObject().getHeaders().toString();
        String strDataExtension = dataExchangeObject.getDataExtension().toString();

        try {
            strProperties = objectMapper.writeValueAsString(dataExchangeObject.getProperties() );
            strOrgHeaders = objectMapper.writeValueAsString(dataExchangeObject.getInDataObject().getHeaders());
            strProcessedHeaders = objectMapper.writeValueAsString(dataExchangeObject.getOutDataObject().getHeaders());
            strDataExtension = objectMapper.writeValueAsString(dataExchangeObject.getDataExtension());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ExchangeData exchangeData = new ExchangeData();
        exchangeData.setUniqueExchangeId(dataExchangeObject.getUniqueExchangeId());
        exchangeData.setLinkedEntity(businessLogicRuleEntity.getEntityName());
        exchangeData.setSource(sourceSys);
        exchangeData.setMessageId(messageId);
        exchangeData.setWorkflowMonitor("{RuleTypesWorkFlow: [\"@@@@@@@@@@@@@@@@@@@@-@@@@@@@@@@@@@@@-@@@@@@@@@@@@@@@\"]}");//Rules-Interfaces-UserActionOnGUI
        exchangeData.setOriginalContentType(ContentType.setLabel(strOrgContentType));
        exchangeData.setOriginalData(dataExchangeObject.getInDataObject().getPayload().getStrMessage());
        exchangeData.setOriginalHeaders(strOrgHeaders);
        exchangeData.setContentType(ContentType.JSON);
        exchangeData.setProcessedData(dataExchangeObject.getOutDataObject().getPayload().getStrMessage());
        exchangeData.setProcessedHeaders(strProcessedHeaders);
        exchangeData.setProperties(strProperties);
        exchangeData.setDataExtension(strDataExtension);

        if( exchangeData.getStatus() == null )
            exchangeData.setStatus( com.ybm.exchangeDataRepo.models.StatusType.RECEIVED ) ;

        return exchangeData;
    }

}
