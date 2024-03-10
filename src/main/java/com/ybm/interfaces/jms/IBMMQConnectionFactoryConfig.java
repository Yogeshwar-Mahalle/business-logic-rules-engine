/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.*;

@Configuration
public class IBMMQConnectionFactoryConfig {
    private static final Logger LOG = LoggerFactory.getLogger(IBMMQConnectionFactoryConfig.class);

    private final LinkedHashMap<String, Connection> connectionMap;

    public IBMMQConnectionFactoryConfig() {
        this.connectionMap = new LinkedHashMap<>();
    }

    public Connection createConnection(String hostName,
                                       Integer port,
                                       String queueManager,
                                       String channel,
                                       String clientID)
    {
        clientID = clientID  == null ? "EuclidPro" : clientID;
        String connectionKey = hostName + port + queueManager + channel + clientID;
        Connection connection = connectionMap.get(connectionKey);

        if( connection == null )
        {
            try {
                MQConnectionFactory connectionFactory = new MQConnectionFactory();
                connectionFactory.setHostName(hostName);
                connectionFactory.setPort(port);
                connectionFactory.setQueueManager(queueManager);
                connectionFactory.setChannel(channel);
                connectionFactory.setAppName(clientID);
                connectionFactory.setClientID(clientID);
                connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

                Properties properties = new Properties();
                properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
                //properties.put(Context.PROVIDER_URL, "iiop://localhost:2809");
                properties.put("hostName", hostName);
                properties.put("port", port);
                properties.put("queueManager", queueManager);
                properties.put("channel", channel);
                properties.put("appName", clientID);
                properties.put("clientID", clientID);

                InitialContext context = new InitialContext(properties);
                connectionFactory = (MQConnectionFactory) context.lookup("ConnectionFactory");

                connection = (Connection) connectionFactory.createConnection();
                connection.start();

                connectionMap.put(connectionKey, connection);

            } catch (Exception e) {
                LOG.info("IBMMQConnectionFactoryConfig.createConnectionFactory ERROR : " + e.getMessage());
                e.printStackTrace();
            }

        }

        return connection;

    }

    public void stopConnection() {
        connectionMap.forEach((key, connection) -> {
            try {
                connection.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
