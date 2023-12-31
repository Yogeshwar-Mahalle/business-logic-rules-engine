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
public class FieldsDataTransformMapping implements Serializable {
    private String linkedEntity;
    private String transformMappingId;
    private String transformMapperName;
    private String transformMapperVersion;
    private String mappingExpressionScript;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
