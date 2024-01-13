/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.sftp;

import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.ruleEngine.dataexchange.DataObject;
import com.ybm.ruleEngine.dataexchange.Payload;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.workflow.WorkflowManager;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Component
public class SFTPFileContentProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(SFTPFileContentProcessor.class);

    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private ExchangeDataService exchangeDataService;

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.info("*************************** SFTP File processing started ***************************");
        File file = exchange.getIn().getBody(File.class);
        String strPayload = Files.readString(file.toPath());
        Map<String, String> headers = new HashMap( exchange.getIn().getHeaders() );

        UUID uuid = UUID.randomUUID();
        LinkedHashMap<String, Object> extData = new LinkedHashMap<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        BusinessLogicRuleEntity businessLogicRuleEntity =
                businessRuleEntityService.getEntityByEntityName( headers.get("entity") );

        String formatType = headers.get("formattype") == null ? "JSON" : headers.get("formattype");
        headers.put("content-type", ContentType.setLabel(formatType).getLabel());

        Payload payload = new Payload(strPayload, ContentType.setLabel( formatType ), null );
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

        exchange.getIn().setBody(result);
        exchange.getIn().getHeaders().putAll(result.getOutDataObject().getHeaders());
        exchange.getProperties().putAll(result.getProperties());
        LOG.info("*************************** SFTP File processing finished ***************************");
    }
}
