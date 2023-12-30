/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo;

import com.ybm.interfacesRepo.dbRepository.InterfaceProfileRepository;
import com.ybm.interfacesRepo.entities.InterfaceProfileDbModel;
import com.ybm.interfacesRepo.models.InterfaceProfile;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterfaceProfileService {

    @Autowired
    private InterfaceProfileRepository interfaceProfileRepository;

    public InterfaceProfile getInterfaceProfileByInterfaceId(String interfaceId) {

        Optional<InterfaceProfileDbModel> interfaceProfileDbModel =
                interfaceProfileRepository.findByInterfaceId(interfaceId);

        return interfaceProfileDbModel.map(this::mapInterfaceProfileFromDbModel).orElse(null);
    }

    public List<InterfaceProfile> getInterfaceProfileByLinkedEntity(String linkedEntity) {

        return interfaceProfileRepository.findByLinkedEntity(linkedEntity).stream()
                .map(
                        this::mapInterfaceProfileFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<InterfaceProfile> getInterfaceProfileByLinkedEntityAndStatus(String linkedEntity, String status) {

        return interfaceProfileRepository.findByLinkedEntityAndStatus(linkedEntity, status).stream()
                .map(
                        this::mapInterfaceProfileFromDbModel
                )
                .collect(Collectors.toList());

    }

    public InterfaceProfile getInterfaceProfileByLinkedEntityAndInterfaceName(String linkedEntity, String interfaceName) {

        Optional<InterfaceProfileDbModel> interfaceProfileDbModel =
                interfaceProfileRepository.findByLinkedEntityAndInterfaceName(linkedEntity, interfaceName);

        return interfaceProfileDbModel.map(this::mapInterfaceProfileFromDbModel).orElse(null);
    }

    @Transactional
    public InterfaceProfile saveInterfaceProfile(InterfaceProfile interfaceProfile) {
        InterfaceProfileDbModel interfaceProfileDbModel = mapInterfaceProfileToDbModel(interfaceProfile);
        interfaceProfileDbModel = interfaceProfileRepository.save(interfaceProfileDbModel);

        return mapInterfaceProfileFromDbModel(interfaceProfileDbModel);
    }


    private InterfaceProfile mapInterfaceProfileFromDbModel(InterfaceProfileDbModel interfaceProfileDbModel){

        return InterfaceProfile.builder()
                .interfaceId(interfaceProfileDbModel.getInterfaceId())
                .linkedEntity(interfaceProfileDbModel.getLinkedEntity())
                .interfaceName(interfaceProfileDbModel.getInterfaceName())
                .communicationProtocol(ComProtocolType.valueOf(interfaceProfileDbModel.getCommunicationProtocol()))
                .direction(interfaceProfileDbModel.getDirection())
                .status(interfaceProfileDbModel.getStatus())
                .createTimeStamp(interfaceProfileDbModel.getCreateTimeStamp())
                .updateTimeStamp(interfaceProfileDbModel.getUpdateTimeStamp())
                .build();

    }

    private InterfaceProfileDbModel mapInterfaceProfileToDbModel(InterfaceProfile interfaceProfile){

        return InterfaceProfileDbModel.builder()
                .interfaceId(interfaceProfile.getInterfaceId() == null ?
                        interfaceProfile.getLinkedEntity() + "~" + interfaceProfile.getInterfaceName() :
                        interfaceProfile.getInterfaceId() )
                .linkedEntity(interfaceProfile.getLinkedEntity())
                .interfaceName(interfaceProfile.getInterfaceName())
                .communicationProtocol(String.valueOf(interfaceProfile.getCommunicationProtocol()))
                .direction(interfaceProfile.getDirection())
                .status(interfaceProfile.getStatus())
                .createTimeStamp(interfaceProfile.getCreateTimeStamp())
                .createTimeStamp(interfaceProfile.getCreateTimeStamp() == null ? new Date() : interfaceProfile.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
