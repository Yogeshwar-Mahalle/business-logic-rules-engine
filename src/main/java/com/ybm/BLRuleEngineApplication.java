/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.ybm", lazyInit = true)
public class BLRuleEngineApplication {
    private static final Logger LOG = LoggerFactory.getLogger(BLRuleEngineApplication.class);

    public static void main(String[] args) {
        LOG.info("******************************* BLRuleEngine Loading *******************************");
        new SpringApplicationBuilder(BLRuleEngineApplication.class)
                .run(args);
        LOG.info("******************************* BLRuleEngine Started *******************************");
    }
}

