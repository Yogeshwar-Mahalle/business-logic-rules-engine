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
public class InterfaceProfile implements Serializable {
    String interfaceId;
    String linkedEntity;
    String interfaceName;
    @Builder.Default
    ComProtocolType communicationProtocol = ComProtocolType.FILE;
    @Builder.Default
    DirectionType direction = DirectionType.OUTGOING;
    String status;
    Date createTimeStamp;
    Date updateTimeStamp;
}