/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces;

import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.interfaces.jms.ActiveMQConnectionFactoryConfig;
import com.ybm.interfaces.jms.IBMMQConnectionFactoryConfig;
import com.ybm.interfaces.jms.QueueMessageConsumer;
import com.ybm.interfaces.jms.RabbitMQConnectionFactoryConfig;
import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.interfacesRepo.models.*;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.workflow.WorkflowManager;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class InterfaceRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceRunner.class);

    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private ExchangeDataService exchangeDataService;
    @Autowired
    private InterfaceProfileService interfaceProfileService;
    @Autowired
    private InterfacePropertyService interfacePropertyService;
    @Autowired
    ActiveMQConnectionFactoryConfig activeMQConnectionFactoryConfig;
    @Autowired
    IBMMQConnectionFactoryConfig ibmMQConnectionFactoryConfig;
    @Autowired
    RabbitMQConnectionFactoryConfig rabbitMQConnectionFactoryConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Start the clock
        long startTime = System.currentTimeMillis();

        //TODO:: Add all incoming interfaces polling mechanism

        try {

            //ActiveMQ
            List<InterfaceProfile> interfaceProfileList = interfaceProfileService
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.AMQ);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                Connection connection =
                        activeMQConnectionFactoryConfig.createConnection(propertiesMap.get(PropertyType.URL),
                                                                        propertiesMap.get(PropertyType.USERID),
                                                                        propertiesMap.get(PropertyType.SECRETE),
                                                                        propertiesMap.get(PropertyType.CLIENT_ID),
                                                                        propertiesMap.get(PropertyType.CONNECTION_ID));

                String queueName = propertiesMap.get(PropertyType.QUEUE_NAME);

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(queueName);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new QueueMessageConsumer(
                        businessRuleEntityService,
                        workflowManager,
                        exchangeDataService,
                        propertiesMap.get(PropertyType.ENTITY),
                        propertiesMap.get(PropertyType.SOURCE),
                        propertiesMap.get(PropertyType.FORMAT_TYPE),
                        propertiesMap.get(PropertyType.MESSAGE_TYPE))
                );
            }


            //IBM WebsphereMQ
            interfaceProfileList = interfaceProfileService
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.WMQ);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                Connection connection =
                        ibmMQConnectionFactoryConfig.createConnection(propertiesMap.get(PropertyType.HOSTNAME),
                                                                    Integer.valueOf(propertiesMap.get(PropertyType.PORT)),
                                                                    propertiesMap.get(PropertyType.QUEUE_MANAGER),
                                                                    propertiesMap.get(PropertyType.CHANNEL),
                                                                    propertiesMap.get(PropertyType.CLIENT_ID));

                String queueName = propertiesMap.get(PropertyType.QUEUE_NAME);

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(queueName);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new QueueMessageConsumer(
                        businessRuleEntityService,
                        workflowManager,
                        exchangeDataService,
                        propertiesMap.get(PropertyType.ENTITY),
                        propertiesMap.get(PropertyType.SOURCE),
                        propertiesMap.get(PropertyType.FORMAT_TYPE),
                        propertiesMap.get(PropertyType.MESSAGE_TYPE))
                );
            }


            //RabbitMQ
            interfaceProfileList = interfaceProfileService
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.RMQ);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                Connection connection =
                        rabbitMQConnectionFactoryConfig.createConnection(propertiesMap.get(PropertyType.HOSTNAME),
                                                                        Integer.valueOf(propertiesMap.get(PropertyType.PORT)),
                                                                        propertiesMap.get(PropertyType.USERID),
                                                                        propertiesMap.get(PropertyType.SECRETE),
                                                                        propertiesMap.get(PropertyType.VIRTUAL_HOST),
                                                                        propertiesMap.get(PropertyType.CLIENT_ID));

                String queueName = propertiesMap.get(PropertyType.QUEUE_NAME);

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(queueName);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new QueueMessageConsumer(
                        businessRuleEntityService,
                        workflowManager,
                        exchangeDataService,
                        propertiesMap.get(PropertyType.ENTITY),
                        propertiesMap.get(PropertyType.SOURCE),
                        propertiesMap.get(PropertyType.FORMAT_TYPE),
                        propertiesMap.get(PropertyType.MESSAGE_TYPE))
                );
            }
        }
        catch (Exception e)
        {
            LOG.error("InterfaceRunner error : " + e.getMessage());
            e.printStackTrace();
        }

        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }
}
