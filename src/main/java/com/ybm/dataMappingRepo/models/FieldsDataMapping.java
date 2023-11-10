/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMappingRepo.models;

import com.ybm.dataMappingRepo.DataTrimType;
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
    String fieldDataMappingId;
    String sourceDataType;
    Integer fieldSequenceNumber;
    String fieldDataMappingParentId;
    String uniqueFieldName;
    String fieldDisplayName;
    String outputJsonPath;
    String sourceDataFormat;
    String sourcePath;
    Integer startPosition;
    Integer length;
    String separator;
    String startDelimiter;
    String endDelimiter;
    DataTrimType dataTrimType = DataTrimType.NONE;
    Date createTimeStamp;
    Date updateTimeStamp;
}
