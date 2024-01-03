/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.notificationsRepo.models;

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
public class AlertNotification implements Serializable {
    private String linkedEntity;
    private String type;
    private String userId;
    private String subject;
    private String message;
    @Builder.Default
    private StatusType status = StatusType.PN;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
