/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.files;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileWatcherTemplate extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // create a route template with the given name
        routeTemplate("filePollingTemplate")
                // here we define the required input parameters (can have default values)
                .templateParameter("directoryName")
                .templateParameter("sourceName")
                .templateParameter("entityName")
                .templateParameter("formatType")
                .templateParameter("messageType")
                .templateParameter("prefixMessage", "File Content : ")
                .from("file://{{directoryName}}?preMove=inprogress&moveFailed=error&move=backup")
                    .log("*************************** File received ***************************")
                    .log("Exchange information: ${exchange}")
                    .setHeader("source", constant("{{sourceName}}"))
                    .setHeader("entity", constant("{{entityName}}"))
                    .setHeader("formattype", constant("{{formatType}}"))
                    .setHeader("messagetype", constant("{{messageType}}"))
                    .setHeader("messageId", simple("${exchangeId}"))
                    .setHeader("message_id", simple("${exchangeId}"))
                    .setProperty("messageId", header("messageId"))
                    .log("File event with Headers : ${headers} occurred.")
                    .log("File event with messageId property : ${exchangeProperty.messageId} occurred.")
                    .log("{{prefixMessage}} ${body}")
                    .process("fileContentProcessor")
                    .log("Result Out Content :  ${body}")
                    .log("Result Out Headers :  ${headers}")
                    .log("*************************** File is parked after processing ***************************");

    }
}
