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
@Table(name = "FIELDS_DATA_MAPPING")
@IdClass(FieldsDataMappingDbModel.IdClass.class)
public class FieldsDataMappingDbModel implements Serializable {

    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "FIELD_DATA_MAPPING_ID", length = 128, nullable=false)
    private String fieldDataMappingId;

    @Column(name = "SOURCE_DATA_TYPE", length = 25, nullable=false)
    private String sourceDataType;

    @Column(name = "FIELD_SEQUENCE_NUMBER", nullable=false)
    private Integer fieldSequenceNumber;

    @Column(name = "FIELD_DATA_MAPPING_PARENT_ID", length = 128)
    private String fieldDataMappingParentId;

    @Column(name = "UNIQUE_FIELD_NAME", length = 50, nullable=false)
    private String uniqueFieldName;

    @Column(name = "FIELD_DISPLAY_NAME", length = 50, nullable=false)
    private String fieldDisplayName;

    @Column(name = "OUTPUT_JSON_PATH", length = 512, nullable=false)
    private String outputJsonPath;

    @Column(name = "SOURCE_DATA_FORMAT", length = 25, nullable=false)
    private String sourceDataFormat;

    @Column(name = "SOURCE_PATH", length = 512)
    private String sourcePath;

    @Column(name = "START_POSITION")
    private Integer startPosition;

    @Column(name = "LENGTH")
    private Integer length;

    @Column(name = "SEPARATOR")
    private String separator;

    @Column(name = "START_DELIMITER")
    private String startDelimiter;

    @Column(name = "END_DELIMITER")
    private String endDelimiter;

    @Column(name = "DATA_TRIM_TYPE", nullable=false)
    private String dataTrimType;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;


    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String fieldDataMappingId;
    }

}

