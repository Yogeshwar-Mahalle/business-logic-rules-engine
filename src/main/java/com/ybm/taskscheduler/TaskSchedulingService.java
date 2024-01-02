/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskscheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

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

@Service
public class TaskSchedulingService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskSchedulingService.class);
    @Autowired
    private TaskScheduler taskScheduler;

    Map<Integer, ScheduledFuture<?>> jobsMap = new LinkedHashMap<>();

    public void scheduleTask(Integer taskNumber, Runnable tasklet, String cronExpression) {
        LOG.info("Scheduling task number : " + taskNumber + " and cron expression : " + cronExpression);
        ScheduledFuture<?> scheduledTask =
                taskScheduler.schedule(tasklet, new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(taskNumber, scheduledTask);
    }

    public void removeScheduledTask(Integer taskNumber) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(taskNumber);
        if(scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(taskNumber, null);
        }
    }

}
