/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces;


import com.ybm.interfaces.files.FilePollingConfig;
import com.ybm.interfaces.jms.*;

import com.ybm.interfaces.sftp.SFTPClientConfig;
import com.ybm.interfaces.websocket.WebsocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
public class InterfaceRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceRunner.class);

    @Autowired
    JMSConfig jmsConfig;
    @Autowired
    FilePollingConfig filePollingConfig;
    @Autowired
    SFTPClientConfig sftpClientConfig;
    @Autowired
    WebsocketConfig webSocketConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        jmsConfig.run(args);
        filePollingConfig.run(args);
        sftpClientConfig.run(args);
        webSocketConfig.run(args);
    }
}
