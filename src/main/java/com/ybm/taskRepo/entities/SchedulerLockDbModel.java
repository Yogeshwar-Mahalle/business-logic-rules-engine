/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskRepo.entities;

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
@Table(name = "SCHEDLOCK")
@IdClass(SchedulerLockDbModel.IdClass.class)
public class SchedulerLockDbModel {
    @Id
    @Column(name = "NAME", length = 64, nullable=false)
    private String name;

    @Column(name = "LOCK_UNTIL", nullable=false)
    private Date lockUntil;

    @Column(name = "LOCKED_AT", nullable=false)
    private Date lockedAt = new Date();

    @Column(name = "LOCKED_BY", length = 255, nullable=false)
    private String lockedBy;

    @Data
    static class IdClass implements Serializable {
        private String name;
    }
}
