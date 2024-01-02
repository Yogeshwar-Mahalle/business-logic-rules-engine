/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.exchangeDataRepo.models.StatusType;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.ruleEngine.dataexchange.DataObject;
import com.ybm.ruleEngine.dataexchange.Payload;
import com.ybm.ruleEngine.RuleEngine;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.workflow.WorkflowManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class MessageProcessingRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessingRestController.class);
    @Autowired
    private RuleEngine ruleEngine;
    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private ExchangeDataService exchangeDataService;

    @PostMapping(value = "/edo/{entityName}")
    public ResponseEntity<?> postPaymentDetails(@PathVariable("entityName") String entityName,
                                                @RequestHeader LinkedHashMap<String, String> headers,
                                                @RequestBody LinkedHashMap map) {
        UUID uuid = UUID.randomUUID();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        LinkedHashMap<String, Object> extData = new LinkedHashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        String strPayload = map.toString();
        try {
            strPayload = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BusinessLogicRuleEntity businessLogicRuleEntity =
                businessRuleEntityService.getEntityByEntityName(entityName);
        Payload payload = new Payload(strPayload, ContentType.JSON, map );
        DataObject dataObject = new DataObject( headers, payload);
        DataExchangeObject dataExchangeObject = new DataExchangeObject(
                uuid.toString(),
                businessLogicRuleEntity,
                properties,
                dataObject,
                dataObject,
                extData,
                new LinkedList<>()
        );

        ExchangeData exchangeData = mapExchangeData(businessLogicRuleEntity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        String ruleType = headers.get("RULE_TYPE") != null ? headers.get("RULE_TYPE") : headers.get("rule_type");
        DataExchangeObject result = ruleEngine.run(ruleType, dataExchangeObject);

        exchangeData = mapExchangeData(businessLogicRuleEntity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/wrkflow/{entityName}")
    public ResponseEntity<?> postToWorkFlow(@PathVariable("entityName") String entityName,
                                            @RequestHeader Map<String, String> headers,
                                            @RequestBody String strPayload) {
        String strOrgContentType = headers.get("content-type") != null ? headers.get("content-type") : headers.get("CONTENT-TYPE");
        UUID uuid = UUID.randomUUID();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        LinkedHashMap<String, Object> extData = new LinkedHashMap<>();

        /*ObjectMapper objectMapper = new ObjectMapper();
        String strPayload = map.toString();
        try {
            strPayload = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/

        BusinessLogicRuleEntity businessLogicRuleEntity =
                businessRuleEntityService.getEntityByEntityName(entityName);

        Payload payload = new Payload(strPayload, ContentType.setLabel(strOrgContentType), null );
        DataObject dataObject = new DataObject( headers, payload);
        DataExchangeObject dataExchangeObject = new DataExchangeObject(
                uuid.toString(),
                businessLogicRuleEntity,
                properties,
                dataObject,
                dataObject,
                extData,
                new LinkedList<>()
        );

        ExchangeData exchangeData = mapExchangeData(businessLogicRuleEntity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        DataExchangeObject result = workflowManager.run( dataExchangeObject );

        exchangeData = mapExchangeData(businessLogicRuleEntity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    private ExchangeData mapExchangeData(BusinessLogicRuleEntity businessLogicRuleEntity, DataExchangeObject dataExchangeObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> headers = dataExchangeObject.getInDataObject().getHeaders();
        String strProperties = dataExchangeObject.getProperties().toString();
        String strOrgHeaders = dataExchangeObject.getInDataObject().getHeaders().toString();
        String strProcessedHeaders = dataExchangeObject.getOutDataObject().getHeaders().toString();
        String strDataExtension = dataExchangeObject.getDataExtension().toString();

        try {
            strProperties = objectMapper.writeValueAsString(dataExchangeObject.getProperties());
            strOrgHeaders = objectMapper.writeValueAsString(dataExchangeObject.getInDataObject().getHeaders());
            strProcessedHeaders = objectMapper.writeValueAsString(dataExchangeObject.getOutDataObject().getHeaders());
            strDataExtension = objectMapper.writeValueAsString(dataExchangeObject.getDataExtension());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String strOrgContentType = headers.get("content-type") != null ? headers.get("content-type") : headers.get("CONTENT-TYPE");
        String sourceSys = headers.get("source") != null ? headers.get("source") : headers.get("SOURCE");
        sourceSys = sourceSys == null ? "BLRuleEngine" : sourceSys;

        if( businessLogicRuleEntity == null )
        {
            String entityName = (headers.get("entity") != null ? headers.get("entity") : headers.get("ENTITY") );
            businessLogicRuleEntity = businessRuleEntityService.getEntityByEntityName(entityName);

            if( businessLogicRuleEntity == null )
            {
                businessLogicRuleEntity = businessRuleEntityService.getEntityByEntityName( "BLRuleEngine" );
            }
        }

        String messageId = headers.get("message_id") != null ? headers.get("message_id") : headers.get("MESSAGE_ID");
        messageId = dataExchangeObject.getProperties().get("messageId") != null ? (String) dataExchangeObject.getProperties().get("messageId") : messageId;
        String messageType = headers.get("messagetype") != null ? headers.get("messagetype") : headers.get("MESSAGETYPE");
        messageType = dataExchangeObject.getProperties().get("messageType") != null ? (String) dataExchangeObject.getProperties().get("messageType") : messageType;

        dataExchangeObject.getProperties().putIfAbsent("entity", businessLogicRuleEntity.getEntityName());
        dataExchangeObject.getProperties().putIfAbsent("source", sourceSys);
        dataExchangeObject.getProperties().putIfAbsent("formatType", ContentType.setLabel(strOrgContentType).name());
        dataExchangeObject.getProperties().putIfAbsent("messageType", messageType);
        dataExchangeObject.getProperties().putIfAbsent("messageId", messageId);


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
            exchangeData.setStatus( StatusType.RECEIVED ) ;

        return exchangeData;
    }
}
