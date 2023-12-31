/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.models;

import com.ybm.rulesBusinessSetupRepo.models.StatusType;
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
public class InterfaceProperty implements Serializable {
    private String interfaceId;
    @Builder.Default
    private PropertyType propertyName = PropertyType.FILE_PATH;
    private String propertyValue;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
