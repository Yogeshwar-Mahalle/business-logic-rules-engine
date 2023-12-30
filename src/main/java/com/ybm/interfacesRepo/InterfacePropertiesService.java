/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo;

import com.ybm.interfacesRepo.dbRepository.InterfacePropertiesRepository;
import com.ybm.interfacesRepo.entities.InterfacePropertiesDbModel;
import com.ybm.interfacesRepo.models.InterfaceProperties;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterfacePropertiesService {

    @Autowired
    private InterfacePropertiesRepository interfacePropertiesRepository;

    public InterfaceProperties getInterfacePropertiesByInterfaceIdAndPropertyName(String interfaceId, String propertyName) {

        Optional<InterfacePropertiesDbModel> interfacePropertiesDbModel =
                interfacePropertiesRepository.findByInterfaceIdAndPropertyName(interfaceId, propertyName);

        return interfacePropertiesDbModel.map(this::mapInterfacePropertiesFromDbModel).orElse(null);
    }

    public List<InterfaceProperties> getInterfacePropertiesByInterfaceId(String interfaceId) {

        return interfacePropertiesRepository.findByInterfaceId(interfaceId).stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<InterfaceProperties> getInterfacePropertiesByInterfaceIdAndStatus(String interfaceId, String status) {

        return interfacePropertiesRepository.findByInterfaceIdAndStatus(interfaceId, status).stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public InterfaceProperties saveInterfaceProperties(InterfaceProperties interfaceProperties) {
        InterfacePropertiesDbModel interfacePropertiesDbModel = mapInterfacePropertiesToDbModel(interfaceProperties);
        interfacePropertiesDbModel = interfacePropertiesRepository.save(interfacePropertiesDbModel);

        return mapInterfacePropertiesFromDbModel(interfacePropertiesDbModel);
    }


    private InterfaceProperties mapInterfacePropertiesFromDbModel(InterfacePropertiesDbModel interfacePropertiesDbModel){

        return InterfaceProperties.builder()
                .interfaceId(interfacePropertiesDbModel.getInterfaceId())
                .propertyName(interfacePropertiesDbModel.getPropertyName())
                .propertyValue(interfacePropertiesDbModel.getPropertyValue())
                .status(interfacePropertiesDbModel.getStatus())
                .createTimeStamp(interfacePropertiesDbModel.getCreateTimeStamp())
                .updateTimeStamp(interfacePropertiesDbModel.getUpdateTimeStamp())
                .build();

    }

    private InterfacePropertiesDbModel mapInterfacePropertiesToDbModel(InterfaceProperties interfaceProperties){

        return InterfacePropertiesDbModel.builder()
                .interfaceId(interfaceProperties.getInterfaceId())
                .propertyName(interfaceProperties.getPropertyName())
                .propertyValue(interfaceProperties.getPropertyValue())
                .status(interfaceProperties.getStatus())
                .createTimeStamp(interfaceProperties.getCreateTimeStamp())
                .createTimeStamp(interfaceProperties.getCreateTimeStamp() == null ? new Date() : interfaceProperties.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
