/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskscheduler;

import com.ybm.taskRepo.models.TaskDefinition;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Setter
@Getter
@Service
public class TaskExecutorService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutorService.class);

    private TaskDefinition taskDefinition;

    @Override
    public void run() {
        // Start the clock
        long startTime = System.currentTimeMillis();

        LOG.info("Running task of Type : " + taskDefinition.getTaskType()
                + ", Number : " + taskDefinition.getTaskNumber()
                + " linked to " + taskDefinition.getLinkedEntity() );

        //TODO:: Add all taskscheduler running mechanism


        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }

}
