/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@ComponentScan(value = "com.ybm", lazyInit = true)
public class BLRuleEngineApplication {
    private static final Logger LOG = LoggerFactory.getLogger(BLRuleEngineApplication.class);

    @Value( "${threadpooltaskexecutor.corePoolSize:5}" )
    private Integer corePoolSize;
    @Value( "${threadpooltaskexecutor.maxPoolSize:100}" )
    private Integer maxPoolSize;
    @Value( "${threadpooltaskexecutor.queueCapacity:500}" )
    private Integer queueCapacity;


    public static void main(String[] args) {
        LOG.info("******************************* BLRuleEngine Loading *******************************");
        SpringApplication.run(BLRuleEngineApplication.class, args);
        //new SpringApplicationBuilder(BLRuleEngineApplication.class).run(args);
        LOG.info("******************************* BLRuleEngine Started *******************************");
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("BLRuleEngine-");
        executor.initialize();
        return executor;
    }

    @PreDestroy
    public void preDestroyGracefulShutdown() {

        LOG.info("******************************* BLRuleEngine graceful shutdown started *******************************");
    }
}

