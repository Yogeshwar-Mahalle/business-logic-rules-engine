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
@Table(name = "TASK_DEFINITION")
@IdClass(TaskDefinitionDbModel.IdClass.class)
public class TaskDefinitionDbModel {
    @Id
    @Column(name = "LINKED_ENTITY", length = 25, nullable=false)
    private String linkedEntity;

    @Id
    @Column(name = "TASK_NUMBER", nullable=false)
    private Integer taskNumber;

    @Id
    @Column(name = "TASK_TYPE", length = 15, nullable=false)
    private String taskType;

    @Column(name = "CRON_EXPRESSION", length = 25, nullable=false)
    private String cronExpression;

    @Column(name = "TASK_MODULE", length = 100, nullable=false)
    private String taskModule;

    @Column(name = "TASK_ARGUMENT", length = 100)
    private String taskArguments;

    @Column(name = "STATUS", length = 2, nullable=false)
    private String status;

    @Column(name = "CREATE_TIME_STAMP", nullable=false)
    private Date createTimeStamp;

    @Column(name = "UPDATE_TIME_STAMP")
    private Date updateTimeStamp;

    @Data
    static class IdClass implements Serializable {
        private String linkedEntity;
        private String taskNumber;
        private String taskType;
    }
}
