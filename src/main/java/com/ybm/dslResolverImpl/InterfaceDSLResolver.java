/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl;

import com.ybm.interfacesRepo.models.*;
import com.ybm.interfacesRepo.InterfaceProfileService;
import com.ybm.interfacesRepo.InterfacePropertyService;
import com.ybm.ruleEngine.dslResolver.DSLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InterfaceDSLResolver implements DSLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceDSLResolver.class);
    private static final String DSL_RESOLVER_KEYWORD = "INTERFACE";
    private static final String SYNCHRONOUS = "SYNCHRONOUS";
    private static final String ASYNCHRONOUS = "ASYNCHRONOUS";


    @Autowired
    private InterfaceProfileService interfaceProfileService;

    @Autowired
    private InterfacePropertyService interfacePropertyService;

    @Override
    public String getResolverKeyword() {
        return DSL_RESOLVER_KEYWORD;
    }

    @Override
    public Object resolveValue() {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword) {
        //TODO:: Implementation Pending
        return null;
    }

    @Override
    public Object resolveValue(String keyword, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters) {
        Object result = null;

        InterfaceProfile interfaceProfile = interfaceProfileService
                .getInterfaceProfileByLinkedEntityAndInterfaceName( parameters[0], parameters[1] );

        List<InterfaceProperty> interfacePropertyList = interfacePropertyService
                .getInterfacePropertiesByInterfaceIdAndStatus( interfaceProfile.getInterfaceId(), StatusType.AC );

        Map<PropertyType, String> propertiesMap = interfacePropertyList.stream().collect(
                Collectors.toMap(InterfaceProperty::getPropertyName,
                        InterfaceProperty::getPropertyValue)
        );

        ComProtocolType comProtocolType = interfaceProfile.getCommunicationProtocol();



        //Blocking : By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(SYNCHRONOUS)){
            switch ( comProtocolType )
            {
                case FILE -> {

                    if( interfaceProfile.getDirection() == DirectionType.OUTGOING ) {
                        File directoryPath = new File(propertiesMap.get(PropertyType.FILE_PATH));
                        String fileExtension = propertiesMap.get(PropertyType.FILE_EXT) == null ? "text" : propertiesMap.get(PropertyType.FILE_EXT);
                        String fileName = parameters[2] + "-" + new Date().getTime() + "." + fileExtension;
                        File resultFile = new File(directoryPath, fileName);

                        if(!directoryPath.exists())
                            directoryPath.mkdirs();

                        BufferedWriter writer = null;
                        try {
                            writer = new BufferedWriter(new FileWriter(resultFile));
                            writer.write(parameters[3]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return "Result is written to file path : " + resultFile.getAbsoluteFile();
                    }
                    else {
                        return "File reading is not implemented";
                    }
                }
                case API -> {
                    return "API-CALLED";
                }
                case MQ -> {
                    return "JMS-CALLED";
                }
                case FTP -> {
                    return "SFTP-CALLED";
                }
                default ->  {
                    return "UNKNOWN-PROTOCOL";
                }
            }
        }

        //Non-Blocking : By using this keyword external interfaces APIs or DB service can be called
        if (keyword.equalsIgnoreCase(ASYNCHRONOUS)){
            return "ASYNCHRONOUS-CALL";
        }

        return null;
    }

    @Override
    public Object resolveValue(String keyword, String[] parameters, Integer index) {
        //TODO:: By using this keyword external interfaces APIs or DB service can be called
        return null;
    }
}
