/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.interfaces.jms.QueueMessageConsumer;
import com.ybm.interfacesRepo.models.*;
import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InterfaceDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "INTERFACE";
    private static final String SYNCHRONOUS = "SYNCHRONOUS";
    private static final String ASYNCHRONOUS = "ASYNCHRONOUS";


    @Autowired
    private InterfaceProfileService interfaceProfileService;

    @Autowired
    private InterfacePropertyService interfacePropertyService;

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
    }

    @Override
    public Object resolveValue() {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword) {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters) {

        LOG.info("InterfaceDSLResolver.resolveValue parameters : " + Arrays.toString(parameters));

        Object result = null;

        InterfaceProfile interfaceProfile = interfaceProfileService
                .getInterfaceProfileByLinkedEntityAndInterfaceName( parameters[0], parameters[1] );

        List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                .getInterfacePropertiesByInterfaceIdAndStatus( interfaceProfile.getInterfaceId(), StatusType.AC );

        Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                Collectors.toMap(InterfaceProperty::getPropertyName,
                        InterfaceProperty::getPropertyValue)
        );

        ComProtocolType comProtocolType = interfaceProfile.getCommunicationProtocol();

        LOG.info("InterfaceDSLResolver.resolveValue INTERFACE ID : " + interfaceProfile.getInterfaceId());
        LOG.info("InterfaceDSLResolver.resolveValue COMMUNICATION PROTOCOL : " + comProtocolType);
        LOG.info("InterfaceDSLResolver.resolveValue INTERFACE PARAMETERS : " + propertiesMap);

        //Blocking : By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(SYNCHRONOUS)){
            switch ( comProtocolType )
            {
                case FILE -> {

                    if( interfaceProfile.getDirection() == DirectionType.OUTGOING ) {
                        File directoryPath = new File(propertiesMap.get(PropertyType.FILE_PATH));
                        String fileExtension = propertiesMap.get(PropertyType.FILE_EXT) == null ? "text" : propertiesMap.get(PropertyType.FILE_EXT);
                        String fileName = parameters[2] + "-" + new Date().getTime() + "." + fileExtension;
                        File resultFile = new File(directoryPath, fileName);

                        if(!directoryPath.exists())
                            directoryPath.mkdirs();

                        BufferedWriter writer = null;
                        try {
                            writer = new BufferedWriter(new FileWriter(resultFile));
                            writer.write(parameters[3]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return "Result is written to file path : " + resultFile.getAbsoluteFile();
                    }
                    else {
                        return "File reading is not implemented";
                    }
                }
                case API -> {
                    return "API-CALLED";
                }
                case MQ -> {

                    try {

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
                            MessageProducer producer = queueSession.createProducer(destination);
                            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                            TextMessage message = queueSession.createTextMessage(parameters[3]);
                            message.setJMSMessageID(UUID.randomUUID().toString());
                            message.setJMSType(propertiesMap.get(PropertyType.FORMAT_TYPE));
                            message.setJMSTimestamp(new Date().getTime());

                            producer.send(message);

                            // Clean up
                            queueSession.close();
                            queueConnection.close();

                    }
                    catch (Exception e)
                    {
                        LOG.error("InterfaceRunner error : " + e.getMessage());
                        e.printStackTrace();
                    }

                    return "JMS-CALLED";
                }
                case FTP -> {
                    return "SFTP-CALLED";
                }
                default ->  {
                    return "UNKNOWN-PROTOCOL";
                }
            }
        }

        //Non-Blocking : By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(ASYNCHRONOUS)){
            return "ASYNCHRONOUS-CALL";
        }

        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
