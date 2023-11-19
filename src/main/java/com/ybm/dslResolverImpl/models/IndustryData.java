/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dslResolverImpl.models;

import com.ybm.dataMappingRepo.DataTrimType;
import jakarta.persistence.Column;
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
public class IndustryData implements Serializable {
    private String dataType;
    private String keyField;
    private String valueField;
    private Integer sequenceNumber;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
