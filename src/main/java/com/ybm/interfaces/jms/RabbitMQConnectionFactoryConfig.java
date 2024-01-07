/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;

import com.rabbitmq.client.ConnectionFactory;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.LinkedHashMap;
import java.util.Properties;

@Configuration
public class RabbitMQConnectionFactoryConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConnectionFactoryConfig.class);

    private final LinkedHashMap<String, Connection> connectionMap;

    public RabbitMQConnectionFactoryConfig() {
        this.connectionMap = new LinkedHashMap<>();
    }

    public Connection createConnection(String hostName,
                                       Integer port,
                                       String userName,
                                       String password,
                                       String virtualHost,
                                       String clientID)
    {
        clientID = clientID  == null ? "BLRuleEngine" : clientID;
        String connectionKey = hostName + port + userName + password + virtualHost + clientID;
        Connection connection = connectionMap.get(connectionKey);

        if( connection == null )
        {
            try {

                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setHost(hostName);
                connectionFactory.setPort(port);
                connectionFactory.setUsername(userName);
                connectionFactory.setPassword(password);
                connectionFactory.setVirtualHost(virtualHost);

                Properties properties = new Properties();
                properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.rabbitmq.jms.admin.RMQObjectFactory");
                //properties.put(Context.PROVIDER_URL, "iiop://localhost:2809");
                properties.put("hostName", hostName);
                properties.put("port", port);
                properties.put("userName", userName);
                properties.put("password", password);
                properties.put("virtualHost", virtualHost);
                properties.put("clientID", clientID);

                InitialContext context = new InitialContext(properties);
                connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");

                connection = (Connection) connectionFactory.newConnection();
                connection.start();

                connectionMap.put(connectionKey, connection);

            } catch (Exception e) {
                LOG.info("RabbitMQConnectionFactoryConfig.createConnectionFactory ERROR : " + e.getMessage());
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
