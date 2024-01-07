/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.LinkedHashMap;
import java.util.Properties;

@Configuration
public class ActiveMQConnectionFactoryConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQConnectionFactoryConfig.class);

    private final LinkedHashMap<String, Connection> connectionMap;

    public ActiveMQConnectionFactoryConfig() {
        this.connectionMap = new LinkedHashMap<>();
    }

    public Connection createConnection(String brokerUrl,
                                     String brokerUserName,
                                     String brokerPassword,
                                     String clientID,
                                     String connectionIDPrefix)
    {

        clientID = clientID  == null ? "BLRuleEngine" : clientID;
        connectionIDPrefix = connectionIDPrefix  == null ? "BLRE" : connectionIDPrefix;

        String connectionKey = brokerUrl + brokerUserName + brokerPassword + clientID + connectionIDPrefix;
        Connection connection = connectionMap.get(connectionKey);

        if( connection == null )
        {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(brokerUrl);
            connectionFactory.setUserName(brokerUserName);
            connectionFactory.setPassword(brokerPassword);
            connectionFactory.setUseCompression(true);
            connectionFactory.setClientID(clientID);
            connectionFactory.setConnectionIDPrefix(connectionIDPrefix);
            connectionFactory.setUseAsyncSend(true);

            Properties properties = null;
            try {
                properties = new Properties();
                properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
                //properties.put(Context.PROVIDER_URL, "tcp://localhost:61616");
                properties.put(Context.PROVIDER_URL, brokerUrl);
                properties.put("userName", brokerUserName);
                properties.put("password", brokerPassword);
                properties.put("clientID", clientID);
                properties.put("connectionIDPrefix", connectionIDPrefix);

                InitialContext context = new InitialContext(properties);
                connectionFactory = (ActiveMQConnectionFactory) context.lookup("ConnectionFactory");

                connection = connectionFactory.createConnection(brokerUserName, brokerPassword);
                connection.start();

                connectionMap.put(connectionKey, connection);

            } catch (Exception e) {
                LOG.info("ActiveMQConnectionFactoryConfig.createConnectionFactory ERROR : " + e.getMessage());
                e.printStackTrace();
            }

        }

        return connection;
    }

    public void stopConnection() {
        connectionMap.forEach( (key,connection) -> {
            try {
                connection.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
