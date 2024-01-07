/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.exchangeDataRepo.models.ContentType;
import com.ybm.exchangeDataRepo.models.ExchangeData;
import com.ybm.exchangeDataRepo.models.StatusType;
import com.ybm.ruleEngine.dataexchange.DataExchangeObject;
import com.ybm.ruleEngine.dataexchange.DataObject;
import com.ybm.ruleEngine.dataexchange.Payload;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.rulesBusinessSetupRepo.models.BusinessLogicRuleEntity;
import com.ybm.workflow.WorkflowManager;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

//import javax.jms.*;

@Component
@AllArgsConstructor
@Data
@EnableTransactionManagement
public class QueueMessageConsumer implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueMessageConsumer.class);

    private BusinessRuleEntityService businessRuleEntityService;
    private WorkflowManager workflowManager;
    private ExchangeDataService exchangeDataService;

    private String entity;
    private String source;
    private String formatType;
    private String messageType;


    @Override
    @Transactional
    public void onMessage(Message message) {
        LOG.info("Inside On Message...");
        long t1 = System.currentTimeMillis();
        LOG.info("Message consumed at ...."+t1);

        try{
            if(message instanceof TextMessage) {
                LOG.info("String message recieved >> "+((TextMessage) message).getText());

                String source = getSource();
                String messageId = message.getJMSCorrelationID();
                String formatType = message.getJMSType() == null ? getFormatType() : message.getJMSType();
                String messageType = getMessageType();

                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                String strPayload = ((TextMessage) message).getText();

                headers.put("source", source);
                headers.put("content-type", ContentType.setLabel(formatType).getLabel());
                headers.put("formattype", formatType);
                headers.put("message_id", messageId);
                headers.put("messagetype", messageType);
                headers.put("queue_name", message.getJMSDestination().toString());
                headers.put("delivery_time", String.valueOf(message.getJMSDeliveryTime()));
                //headers.put("properties", message.properties == null ? "" : message.properties);

                UUID uuid = UUID.randomUUID();
                LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
                LinkedHashMap<String, Object> extData = new LinkedHashMap<>();

                BusinessLogicRuleEntity businessLogicRuleEntity =
                        businessRuleEntityService.getEntityByEntityName(getEntity());

                Payload payload = new Payload(strPayload, ContentType.setLabel(formatType), null );
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

            }

        }catch(Exception e){
            e.printStackTrace();
        }
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
            businessLogicRuleEntity = businessRuleEntityService.getEntityByEntityName( "BLRuleEngine" );
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
