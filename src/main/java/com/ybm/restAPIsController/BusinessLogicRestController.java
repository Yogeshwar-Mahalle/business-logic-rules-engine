/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.ContentType;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class BusinessLogicRestController {

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private WorkflowManager workflowManager;


    @Autowired
    private ExchangeDataService exchangeDataService;

    @PostMapping(value = "/edo")
    public ResponseEntity<?> postPaymentDetails(@RequestHeader Map<String, String> headers, @RequestBody Map map) {
        UUID uuid = UUID.randomUUID();
        Map<String, Object> properties = new HashMap<>();

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
                dataObject
        );

        ExchangeData exchangeData = mapExchangeData(dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        String ruleType = headers.get("RULE_TYPE") != null ? headers.get("RULE_TYPE") : headers.get("rule_type");
        DataExchangeObject result = ruleEngine.run(ruleType, dataExchangeObject);

        exchangeData = mapExchangeData(result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/wrkflow")
    public ResponseEntity<?> postToWorkFlow(@RequestHeader Map<String, String> headers, @RequestBody Map map) {
        UUID uuid = UUID.randomUUID();
        Map<String, Object> properties = new HashMap<>();

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
                dataObject
        );

        ExchangeData exchangeData = mapExchangeData(dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        DataExchangeObject result = workflowManager.run( dataExchangeObject );

        exchangeData = mapExchangeData(result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    private static ExchangeData mapExchangeData(DataExchangeObject dataExchangeObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        String strProperties = dataExchangeObject.getProperties().toString();
        String strOrgHeaders = dataExchangeObject.getOriginalDataObject().getHeaders().toString();
        String strProcessedHeaders = dataExchangeObject.getDataObject().getHeaders().toString();

        try {
            strProperties = objectMapper.writeValueAsString(dataExchangeObject.getProperties());
            strOrgHeaders = objectMapper.writeValueAsString(dataExchangeObject.getOriginalDataObject().getHeaders());
            strProcessedHeaders = objectMapper.writeValueAsString(dataExchangeObject.getDataObject().getHeaders());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ExchangeData exchangeData = new ExchangeData();
        exchangeData.setUniqueExchangeId(dataExchangeObject.getUniqueExchangeId());
        exchangeData.setLinkedEntity("IN");
        exchangeData.setSource("IN-GW");
        exchangeData.setWorkflowMonitor("{RuleTypesWorkFlow: [\"BOOK\"]}");
        exchangeData.setOriginalContentType(ContentType.JSON);
        exchangeData.setOriginalData(dataExchangeObject.getOriginalDataObject().getPayload().getStrMessage());
        exchangeData.setOriginalHeaders(strOrgHeaders);
        exchangeData.setContentType(ContentType.JSON);
        exchangeData.setProcessedData(dataExchangeObject.getDataObject().getPayload().getStrMessage());
        exchangeData.setProcessedHeaders(strProcessedHeaders);
        exchangeData.setProperties(strProperties);
        exchangeData.setStatus("AC");
        return exchangeData;
    }
}
