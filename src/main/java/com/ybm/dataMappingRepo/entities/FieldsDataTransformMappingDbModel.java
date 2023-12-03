/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FIELDS_DATA_TRANSFORM_MAPPING")
@IdClass(FieldsDataTransformMappingDbModel.IdClass.class)
public class FieldsDataTransformMappingDbModel implements Serializable {

    @Id
    @Column(name = "TRANSFORM_MAPPING_ID", length = 128, nullable=false)
    private String transformMappingId;

    @Id
    @Column(name = "TRANSFORM_MAPPER_NAME", length = 50, nullable=false)
    private String transformMapperName;

    @Id
    @Column(name = "TRANSFORM_MAPPER_VERSION", length = 10, nullable=false)
    private String transformMapperVersion;

    @Column(name = "MAPPING_EXPRESSION_SCRIPT", length = 5120)
    private String mappingExpressionScript;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String transformMappingId;
        private String transformMapperName;
        private String transformMapperVersion;
    }

}

