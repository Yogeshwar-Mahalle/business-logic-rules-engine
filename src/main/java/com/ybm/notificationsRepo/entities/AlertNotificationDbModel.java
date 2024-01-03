/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.notificationsRepo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ALERT_NOTIFICATION")
@IdClass(AlertNotificationDbModel.IdClass.class)
public class AlertNotificationDbModel implements Serializable {
    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "TYPE", length = 20, nullable=false)
    private String type;

    @Id
    @Column(name = "USER_ID", length = 128, nullable=false)
    private String userId;

    @Column(name = "SUBJECT", length = 20, nullable=false)
    private String subject;

    @Column(name = "MESSAGE", length = 100, nullable=false)
    private String message;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Id
    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;


    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String type;
        private String userId;
        private Date createTimeStamp;
    }
}
