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
        LOG.info("*************************** Message received ***************************");
        LOG.info("Inside On Message...");
        long t1 = System.currentTimeMillis();
        LOG.info("Message consumed at ...."+t1);

        try{
            if(message instanceof TextMessage) {
                LOG.info("String message recieved >> "+((TextMessage) message).getText());

                String messageId = message.getJMSCorrelationID();
                String formatType = message.getJMSType() == null ? getFormatType() : message.getJMSType();

                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                String strPayload = ((TextMessage) message).getText();

                headers.put("content-type", ContentType.setLabel(formatType).getLabel());
                headers.put("entity", getEntity() );
                headers.put("source", getSource() );
                headers.put("formattype", formatType);
                headers.put("message_id", messageId);
                headers.put("messagetype", getMessageType() );
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

                ExchangeData exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, dataExchangeObject);
                exchangeData = exchangeDataService.saveExchangeData(exchangeData);

                DataExchangeObject result = workflowManager.run( dataExchangeObject );

                exchangeData = workflowManager.mapExchangeData(businessLogicRuleEntity, result);
                exchangeData = exchangeDataService.saveExchangeData(exchangeData);

                LOG.info("*************************** Message is processed ***************************");
            }

        }catch(Exception e){
            e.printStackTrace();
            LOG.info("*************************** Message processing failed ***************************");
        }
    }
}
