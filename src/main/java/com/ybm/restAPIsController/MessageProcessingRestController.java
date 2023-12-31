/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.ruleEngine.dataexchange.DataObject;
import com.ybm.ruleEngine.dataexchange.Payload;
import com.ybm.ruleEngine.RuleEngine;
import com.ybm.workflow.WorkflowManager;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    private RuleEngine ruleEngine;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private ExchangeDataService exchangeDataService;

    @PostMapping(value = "/edo/{entity}")
    public ResponseEntity<?> postPaymentDetails(@PathVariable("entity") String entity,
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

        Payload payload = new Payload(strPayload, ContentType.JSON, map );
        DataObject dataObject = new DataObject( headers, payload);
        DataExchangeObject dataExchangeObject = new DataExchangeObject(
                uuid.toString(),
                properties,
                dataObject,
                dataObject,
                extData,
                new LinkedList<>()
        );

        ExchangeData exchangeData = mapExchangeData(entity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        String ruleType = headers.get("RULE_TYPE") != null ? headers.get("RULE_TYPE") : headers.get("rule_type");
        DataExchangeObject result = ruleEngine.run(entity, ruleType, dataExchangeObject);

        exchangeData = mapExchangeData(entity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/wrkflow/{entity}")
    public ResponseEntity<?> postToWorkFlow(@PathVariable("entity") String entity,
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

        Payload payload = new Payload(strPayload, ContentType.setLabel(strOrgContentType), null );
        DataObject dataObject = new DataObject( headers, payload);
        DataExchangeObject dataExchangeObject = new DataExchangeObject(
                uuid.toString(),
                properties,
                dataObject,
                dataObject,
                extData,
                new LinkedList<>()
        );

        ExchangeData exchangeData = mapExchangeData(entity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        DataExchangeObject result = workflowManager.run(entity, dataExchangeObject );

        exchangeData = mapExchangeData(entity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    private static ExchangeData mapExchangeData(String entity, DataExchangeObject dataExchangeObject) {
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
        entity = entity != null ? entity : (headers.get("entity") != null ? headers.get("entity") : headers.get("ENTITY") );
        entity = entity == null ? "BLRuleEngine" : entity;
        String messageId = headers.get("message_id") != null ? headers.get("message_id") : headers.get("MESSAGE_ID");
        messageId = dataExchangeObject.getProperties().get("messageId") != null ? (String) dataExchangeObject.getProperties().get("messageId") : messageId;

        ExchangeData exchangeData = new ExchangeData();
        exchangeData.setUniqueExchangeId(dataExchangeObject.getUniqueExchangeId());
        exchangeData.setLinkedEntity(entity);
        exchangeData.setSource(sourceSys);
        exchangeData.setMessageId(messageId);
        exchangeData.setWorkflowMonitor("{RuleTypesWorkFlow: [\"@@@@@@@@@@-@@@@@@@@@@-@@@@@@@@@@\"]}");//Rules-Interfaces-UserActionOnGUI
        exchangeData.setOriginalContentType(ContentType.setLabel(strOrgContentType));
        exchangeData.setOriginalData(dataExchangeObject.getInDataObject().getPayload().getStrMessage());
        exchangeData.setOriginalHeaders(strOrgHeaders);
        exchangeData.setContentType(ContentType.JSON);
        exchangeData.setProcessedData(dataExchangeObject.getOutDataObject().getPayload().getStrMessage());
        exchangeData.setProcessedHeaders(strProcessedHeaders);
        exchangeData.setProperties(strProperties);
        exchangeData.setDataExtension(strDataExtension);

        if( exchangeData.getStatus() == null )
            exchangeData.setStatus( "RECEIVED" ) ;

        return exchangeData;
    }
}
