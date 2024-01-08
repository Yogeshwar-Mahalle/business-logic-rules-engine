/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;

import com.ybm.exchangeDataRepo.ExchangeDataService;
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
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
@EnableJms
@EnableTransactionManagement
public class JMSConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JMSConfig.class);

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


    @Bean
    public DefaultJmsListenerContainerFactory containerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setSessionTransacted(true);
        factory.setConnectionFactory( connectionFactory );
        //factory.setMaxMessagesPerTask(1);
        //factory.setConcurrency("1-5");

        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsTopicContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer )
    {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure( factory, connectionFactory );
        factory.setPubSubDomain( true );
        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

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
            LOG.error("JMSConfig error : " + e.getMessage());
            e.printStackTrace();
        }

        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }

}
