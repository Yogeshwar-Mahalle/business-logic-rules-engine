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
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.workflow.WorkflowManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Slf4j
@EnableMethodSecurity
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

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/edo/{entityName}")
    public ResponseEntity<?> postPaymentDetails(@PathVariable("entityName") String entityName,
                                                @RequestHeader LinkedHashMap<String, String> headers,
                                                @RequestBody LinkedHashMap map) {
        String strOrgContentType = headers.get("content-type") != null ? headers.get("content-type") : headers.get("CONTENT-TYPE");
        headers.putIfAbsent("formattype", ContentType.setLabel(strOrgContentType).name());
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

        ExchangeData exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        String ruleType = headers.get("RULE_TYPE") != null ? headers.get("RULE_TYPE") : headers.get("rule_type");
        DataExchangeObject result = ruleEngine.run(ruleType, dataExchangeObject);

        exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/wrkflow/{entityName}")
    public ResponseEntity<?> postToWorkFlow(@PathVariable("entityName") String entityName,
                                            @RequestHeader Map<String, String> headers,
                                            @RequestBody String strPayload) {
        String strOrgContentType = headers.get("content-type") != null ? headers.get("content-type") : headers.get("CONTENT-TYPE");
        headers.putIfAbsent("formattype", ContentType.setLabel(strOrgContentType).name());
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

        ExchangeData exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, dataExchangeObject);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        DataExchangeObject result = workflowManager.run( dataExchangeObject );

        exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, result);
        exchangeData = exchangeDataService.saveExchangeData(exchangeData);

        return ResponseEntity.ok(result);
    }
}
