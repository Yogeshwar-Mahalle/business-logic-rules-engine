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
public class InterfaceProfile implements Serializable {
    private String linkedEntity;
    private String interfaceId;
    private String interfaceName;
    @Builder.Default
    private ComProtocolType communicationProtocol = ComProtocolType.FILE;
    @Builder.Default
    private DirectionType direction = DirectionType.OUTGOING;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
