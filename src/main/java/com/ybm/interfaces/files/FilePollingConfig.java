/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfaces.files;

import com.ybm.exchangeDataRepo.ExchangeDataService;
import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.interfacesRepo.models.*;
import com.ybm.rulesBusinessSetupRepo.BusinessRuleEntityService;
import com.ybm.workflow.WorkflowManager;

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
public class FilePollingConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FilePollingConfig.class);

    @Autowired
    private BusinessRuleEntityService businessRuleEntityService;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private ExchangeDataService exchangeDataService;
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
                    .getInterfaceProfileByDirectionAndStatusAndComProto(DirectionType.INCOMING, StatusType.AC, ComProtocolType.FILE);

            for (InterfaceProfile interfaceProfile : interfaceProfileList) {
                List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                        .getInterfacePropertiesByInterfaceIdAndStatus(interfaceProfile.getInterfaceId(), StatusType.AC);

                Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                        Collectors.toMap(InterfaceProperty::getPropertyName,
                                InterfaceProperty::getPropertyValue)
                );

                TemplatedRouteBuilder.builder(camelContext, "filePollingTemplate")
                        .routeId(interfaceProfile.getInterfaceId())
                        .parameter("directoryName", propertiesMap.get(PropertyType.FILE_PATH))
                        .parameter("sourceName", "FEED")
                        .parameter("entityName", interfaceProfile.getLinkedEntity())
                        .parameter("formatType", "json")
                        .parameter("messageType", "pacs.008")
                        .parameter("prefixMessage", "File Content : ")
                        .add();

                /*new QueueMessageConsumer(
                        businessRuleEntityService,
                        workflowManager,
                        exchangeDataService,
                        propertiesMap.get(PropertyType.ENTITY),
                        propertiesMap.get(PropertyType.SOURCE),
                        propertiesMap.get(PropertyType.FORMAT_TYPE),
                        propertiesMap.get(PropertyType.MESSAGE_TYPE))*/

            }

        }
        catch (Exception e)
        {
            LOG.error("FilePollingConfig error : " + e.getMessage());
            e.printStackTrace();
        }

        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }
}
