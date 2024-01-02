/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo;

import com.ybm.interfacesRepo.dbRepository.InterfacePropertyRepository;
import com.ybm.interfacesRepo.entities.InterfacePropertyDbModel;
import com.ybm.interfacesRepo.models.InterfaceProperty;
import com.ybm.interfacesRepo.models.PropertyType;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterfacePropertyService {

    @Autowired
    private InterfacePropertyRepository interfacePropertyRepository;

    public List<InterfaceProperty> getAllInterfaceProperties() {
        return interfacePropertyRepository.findAll().stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());
    }
    public InterfaceProperty getInterfacePropertiesByInterfaceIdAndPropertyName(String interfaceId, String propertyName) {

        Optional<InterfacePropertyDbModel> interfacePropertiesDbModel =
                interfacePropertyRepository.findByInterfaceIdAndPropertyName(interfaceId, propertyName);

        return interfacePropertiesDbModel.map(this::mapInterfacePropertiesFromDbModel).orElse(null);
    }

    public List<InterfaceProperty> getInterfacePropertiesByInterfaceId(String interfaceId) {

        return interfacePropertyRepository.findByInterfaceId(interfaceId).stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<InterfaceProperty> getInterfacePropertiesByInterfaceIdAndStatus(String interfaceId, String status) {

        return interfacePropertyRepository.findByInterfaceIdAndStatus(interfaceId, status).stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());

    }

    @Transactional
    public InterfaceProperty saveInterfaceProperty(InterfaceProperty interfaceProperty) {
        InterfacePropertyDbModel interfacePropertyDbModel = mapInterfacePropertiesToDbModel(interfaceProperty);
        interfacePropertyDbModel = interfacePropertyRepository.save(interfacePropertyDbModel);

        return mapInterfacePropertiesFromDbModel(interfacePropertyDbModel);
    }

    @Transactional
    public List<InterfaceProperty> saveInterfacePropertyList(List<InterfaceProperty> interfacePropertyList) {
        List<InterfacePropertyDbModel> listInterfacePropertyDbDBModel = interfacePropertyList.stream()
                .map(
                        this::mapInterfacePropertiesToDbModel
                )
                .collect(Collectors.toList());

        return interfacePropertyRepository.saveAll(listInterfacePropertyDbDBModel).stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InterfaceProperty> removeInterfacePropertiesById(String interfaceId) {
        if( interfaceId == null )
            return null;

        interfacePropertyRepository.deleteById(interfaceId);

        return interfacePropertyRepository.findAll().stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InterfaceProperty> removeInterfacePropertiesByIdAndName(String interfaceId,
                                                                        String propertyName) {
        if( interfaceId == null )
            return null;

        interfacePropertyRepository.deleteByInterfaceIdAndPropertyName(interfaceId, propertyName);

        return interfacePropertyRepository.findAll().stream()
                .map(
                        this::mapInterfacePropertiesFromDbModel
                )
                .collect(Collectors.toList());
    }

    private InterfaceProperty mapInterfacePropertiesFromDbModel(InterfacePropertyDbModel interfacePropertyDbModel){

        return InterfaceProperty.builder()
                .interfaceId(interfacePropertyDbModel.getInterfaceId())
                .propertyName(PropertyType.valueOf(interfacePropertyDbModel.getPropertyName()))
                .propertyValue(interfacePropertyDbModel.getPropertyValue())
                .status(StatusType.valueOf(interfacePropertyDbModel.getStatus()))
                .createTimeStamp(interfacePropertyDbModel.getCreateTimeStamp())
                .updateTimeStamp(interfacePropertyDbModel.getUpdateTimeStamp())
                .build();

    }

    private InterfacePropertyDbModel mapInterfacePropertiesToDbModel(InterfaceProperty interfaceProperty){

        return InterfacePropertyDbModel.builder()
                .interfaceId(interfaceProperty.getInterfaceId())
                .propertyName(String.valueOf(interfaceProperty.getPropertyName()))
                .propertyValue(interfaceProperty.getPropertyValue())
                .status(String.valueOf(interfaceProperty.getStatus()))
                .createTimeStamp(interfaceProperty.getCreateTimeStamp())
                .createTimeStamp(interfaceProperty.getCreateTimeStamp() == null ? new Date() : interfaceProperty.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
