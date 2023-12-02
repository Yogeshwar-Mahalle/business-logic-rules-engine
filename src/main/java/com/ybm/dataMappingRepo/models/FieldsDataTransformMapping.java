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
    String transformMappingId;
    String transformMapperName;
    String mappingExpressionScript;
    Date createTimeStamp;
    Date updateTimeStamp;
}
