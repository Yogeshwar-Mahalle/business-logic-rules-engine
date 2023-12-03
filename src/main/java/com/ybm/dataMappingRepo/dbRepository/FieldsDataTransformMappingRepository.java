/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo.dbRepository;

import com.ybm.dataMappingRepo.entities.FieldsDataTransformMappingDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FieldsDataTransformMappingRepository extends JpaRepository<FieldsDataTransformMappingDbModel, String> {

    Optional<FieldsDataTransformMappingDbModel> findByTransformMapperNameAndTransformMapperVersion(@Param("transformMapperName") String transformMapperName,
                                                                                                   @Param("transformMapperVersion") String transformMapperVersion);

    Optional<FieldsDataTransformMappingDbModel> findByTransformMappingId(String transformMappingId);
}
