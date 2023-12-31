/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldsDataMapping implements Serializable {
    private String linkedEntity;
    private String fieldDataMappingId;
    private String sourceDataType;
    private Integer fieldSequenceNumber;
    private String fieldDataMappingParentId;
    private String uniqueFieldName;
    private String fieldDisplayName;
    private String outputJsonPath;
    private String sourceDataFormat;
    private String sourcePath;
    private Integer startPosition;
    private Integer length;
    private String separator;
    private String startDelimiter;
    private String endDelimiter;
    @Builder.Default
    private DataTrimType dataTrimType = DataTrimType.NONE;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
