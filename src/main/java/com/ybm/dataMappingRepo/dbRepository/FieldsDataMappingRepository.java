/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo.dbRepository;

import com.ybm.dataMappingRepo.entities.FieldsDataMappingDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FieldsDataMappingRepository extends JpaRepository<FieldsDataMappingDbModel, String> {

}
