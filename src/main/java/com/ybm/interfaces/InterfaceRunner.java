/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces;

import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.interfaces.jms.QueueMessageConsumer;
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


import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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


    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Start the clock
        long startTime = System.currentTimeMillis();

        //TODO:: Add all incoming interfaces polling mechanism

        try {

            List<InterfaceProfile> interfaceProfileList = interfaceProfileService
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.MQ);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                Properties env = new Properties();
                //env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
                //env.put(Context.PROVIDER_URL, "tcp://localhost:61616");

                env.put(Context.INITIAL_CONTEXT_FACTORY, propertiesMap.get(PropertyType.BROKER_FACTORY));
                env.put(Context.PROVIDER_URL, propertiesMap.get(PropertyType.URL));

                Context ctx = new InitialContext(env);

                QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
                QueueConnection queueConnection =
                        queueConnectionFactory.createQueueConnection(propertiesMap.get(PropertyType.USERID), propertiesMap.get(PropertyType.SECRETE));

                queueConnection.start();

                String queueName = propertiesMap.get(PropertyType.QUEUE_NAME);

                QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = queueSession.createQueue(queueName);
                MessageConsumer consumer = queueSession.createConsumer(destination);
                consumer.setMessageListener(new QueueMessageConsumer(
                        businessRuleEntityService,
                        workflowManager,
                        exchangeDataService,
                        propertiesMap.get(PropertyType.ENTITY),
                        propertiesMap.get(PropertyType.SOURCE),
                        propertiesMap.get(PropertyType.FORMAT_TYPE),
                        propertiesMap.get(PropertyType.MESSAGE_TYPE)
                        )
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
