/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.interfacesRepo.models;

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
    String interfaceId;
    @Builder.Default
    PropertyType propertyName = PropertyType.FILE_PATH;
    String propertyValue;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}
