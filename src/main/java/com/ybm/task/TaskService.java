/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

public class TaskService implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);
    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long startTime = System.currentTimeMillis();

        //TODO:: Add all task running mechanism


        // End the clock
        long endTime = System.currentTimeMillis();
        LOG.info("Elapsed time: " + (endTime - startTime));
    }
}
