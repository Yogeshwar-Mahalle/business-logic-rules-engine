/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.dbRepository;

import com.ybm.interfacesRepo.entities.InterfacePropertyDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfacePropertyRepository extends JpaRepository<InterfacePropertyDbModel, String> {

    List<InterfacePropertyDbModel> findByInterfaceId(@Param("interfaceId") String interfaceId);

    List<InterfacePropertyDbModel> findByInterfaceIdAndStatus(String interfaceId, String status);

    Optional<InterfacePropertyDbModel> findByInterfaceIdAndPropertyName(String interfaceId, String propertyName);

    void deleteByInterfaceIdAndPropertyName(String interfaceId, String propertyName);
}
