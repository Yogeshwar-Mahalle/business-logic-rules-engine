/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.sftp;

import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.interfacesRepo.models.*;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.TemplatedRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SFTPClientConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SFTPClientConfig.class);

    @Autowired
    private InterfaceProfileService interfaceProfileService;
    @Autowired
    private InterfacePropertyService interfacePropertyService;

    @Autowired
    CamelContext camelContext;

    public void run(ApplicationArguments args) throws Exception {

        // Start the clock
        long startTime = System.currentTimeMillis();

        //TODO:: Add all incoming interfaces polling mechanism

        try {

            List<InterfaceProfile> interfaceProfileList = interfaceProfileService
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.SFTP);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                String hostname = propertiesMap.get(PropertyType.HOSTNAME) == null ? "localhost" : propertiesMap.get(PropertyType.HOSTNAME);
                String port = propertiesMap.get(PropertyType.PORT) == null ? "22" : propertiesMap.get(PropertyType.PORT);
                String username = propertiesMap.get(PropertyType.USERID) == null ? "anonymous" : propertiesMap.get(PropertyType.USERID);
                String directoryPath = propertiesMap.get(PropertyType.FILE_PATH) == null ? "./FEED/" : propertiesMap.get(PropertyType.FILE_PATH);
                String options = propertiesMap.get(PropertyType.OPTIONS) == null ? "preMove=inprogress&moveFailed=error&move=backup&binary=false" : propertiesMap.get(PropertyType.OPTIONS);
                String entityName = interfaceProfile.getLinkedEntity() == null ? "BLRuleEngine" : interfaceProfile.getLinkedEntity();
                String sourceName = propertiesMap.get(PropertyType.SOURCE) == null ? "FEED" : propertiesMap.get(PropertyType.SOURCE);
                String formatType = propertiesMap.get(PropertyType.FORMAT_TYPE) == null ? "JSON" : propertiesMap.get(PropertyType.FORMAT_TYPE);
                String messageType = propertiesMap.get(PropertyType.MESSAGE_TYPE) == null ? "pacs.008" : propertiesMap.get(PropertyType.MESSAGE_TYPE);

                TemplatedRouteBuilder.builder(camelContext, "sftpClientPollingTemplate")
                        .routeId(interfaceProfile.getInterfaceId())
                        .parameter("hostname", hostname)
                        .parameter("port", port)
                        .parameter("username", username)
                        .parameter("directoryName", directoryPath)
                        .parameter("options", options)
                        .parameter("entityName", entityName)
                        .parameter("sourceName", sourceName)
                        .parameter("formatType", formatType)
                        .parameter("messageType", messageType)
                        .parameter("prefixMessage", "File Content : ")
                        .add();

            }

        }
        catch (Exception e)
        {
            LOG.error("SFTPClientConfig error : " + e.getMessage());
            e.printStackTrace();
        }

        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }
}
