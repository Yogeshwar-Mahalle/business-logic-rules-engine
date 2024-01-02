/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskRepo.models;

import com.ybm.dataMappingRepo.models.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 ┌───────────── second (0-59)
 │ ┌───────────── minute (0 - 59)
 │ │ ┌───────────── hour (0 - 23)
 │ │ │ ┌───────────── day of the month (1 - 31)
 │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
 │ │ │ │ │ ┌───────────── day of the week (0 - 7)
 │ │ │ │ │ │          (or MON-SUN -- 0 or 7 is Sunday)
 │ │ │ │ │ │
 * * * * * *
*/

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskDefinition {
    private String linkedEntity;
    private Integer taskNumber;
    private String taskType;
    private String cronExpression;
    private String taskModule;
    private String taskArguments;
    private StatusType status;
    private Date createTimeStamp;
    private Date updateTimeStamp;
}
