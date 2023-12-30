/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.dbRepository;

import com.ybm.interfacesRepo.entities.InterfacePropertiesDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterfacePropertiesRepository extends JpaRepository<InterfacePropertiesDbModel, String> {

    List<InterfacePropertiesDbModel> findByInterfaceId(@Param("interfaceId") String interfaceId);

    List<InterfacePropertiesDbModel> findByInterfaceIdAndStatus( String interfaceId, String status);

    Optional<InterfacePropertiesDbModel> findByInterfaceIdAndPropertyName(String interfaceId, String propertyName);

}
