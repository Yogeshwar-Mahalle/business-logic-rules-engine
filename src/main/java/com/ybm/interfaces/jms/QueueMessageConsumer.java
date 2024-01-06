/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.jms;


import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//import javax.jms.*;



@Component
public class QueueMessageConsumer implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(QueueMessageConsumer.class);


    @Override
    public void onMessage(Message message) {
        LOG.info("Inside On Message...");
        long t1 = System.currentTimeMillis();
        LOG.info("Message consumed at ...."+t1);

        try{
            if(message instanceof TextMessage) {
                LOG.info("String message recieved >> "+((TextMessage) message).getText());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
