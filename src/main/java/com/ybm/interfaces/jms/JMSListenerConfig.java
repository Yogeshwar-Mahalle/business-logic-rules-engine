/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;

import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.interfacesRepo.models.*;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

//import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class JMSListenerConfig implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JMSListenerConfig.class);
    /*@Autowired
    ConnectionFactory connectionFactory;*/

    @Autowired
    private InterfaceProfileService interfaceProfileService;

    @Autowired
    private InterfacePropertyService interfacePropertyService;


    //@PostConstruct
    @Override
    public void run(ApplicationArguments args) {

        try {
            //QueueConnection queueConnection = ((QueueConnectionFactory) connectionFactory).createQueueConnection();

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
                consumer.setMessageListener(new QueueMessageConsumer());

            }


        }
        catch (Exception e)
        {
            LOG.error("JMSListenerConfig error : " + e.getMessage());
            e.printStackTrace();
        }

    }

}
