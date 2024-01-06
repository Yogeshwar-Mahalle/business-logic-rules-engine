/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.dbRepository;

import com.ybm.interfacesRepo.entities.InterfaceProfileDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfaceProfileRepository extends JpaRepository<InterfaceProfileDbModel, String> {

    Optional<InterfaceProfileDbModel> findByInterfaceId(@Param("interfaceId") String interfaceId);

    Optional<InterfaceProfileDbModel> findByLinkedEntityAndInterfaceName(String linkedEntity, String interfaceId);

    List<InterfaceProfileDbModel> findByLinkedEntity(String linkedEntity);

    List<InterfaceProfileDbModel> findByLinkedEntityAndStatus(String linkedEntity, String status);

    List<InterfaceProfileDbModel> findByDirection(String directionType);

    List<InterfaceProfileDbModel> findByDirectionAndStatus(char directionType, String status);

    List<InterfaceProfileDbModel> findByDirectionAndStatusAndCommunicationProtocol(char directionType, String status, String comProtocolType);
}