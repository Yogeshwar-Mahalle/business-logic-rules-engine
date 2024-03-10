/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm;

import com.ybm.interfaces.jms.ActiveMQConnectionFactoryConfig;
import com.ybm.interfaces.jms.IBMMQConnectionFactoryConfig;
import com.ybm.interfaces.jms.RabbitMQConnectionFactoryConfig;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(value = "com.ybm", lazyInit = true)
@EnableTransactionManagement
public class EuclidProApplication {
    private static final Logger LOG = LoggerFactory.getLogger(EuclidProApplication.class);

    public static void main(String[] args) {
        LOG.info("******************************* EuclidPro Loading *******************************");
        SpringApplication.run(EuclidProApplication.class, args);
        //new SpringApplicationBuilder(EuclidProApplication.class).run(args);
        LOG.info("******************************* EuclidPro Started *******************************");
    }


    @Autowired
    ActiveMQConnectionFactoryConfig activeMQConnectionFactoryConfig;
    @Autowired
    IBMMQConnectionFactoryConfig ibmMQConnectionFactoryConfig;
    @Autowired
    RabbitMQConnectionFactoryConfig rabbitMQConnectionFactoryConfig;

    @PreDestroy
    public void preDestroyGracefulShutdown() {
        LOG.info("******************************* EuclidPro graceful shutdown started *******************************");

        activeMQConnectionFactoryConfig.stopConnection();
        ibmMQConnectionFactoryConfig.stopConnection();
        rabbitMQConnectionFactoryConfig.stopConnection();
    }
}

