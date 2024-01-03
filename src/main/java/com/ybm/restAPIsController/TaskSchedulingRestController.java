/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.taskRepo.TaskDefinitionService;
import com.ybm.taskRepo.models.TaskDefinition;
import com.ybm.taskscheduler.TaskExecutorService;
import com.ybm.taskscheduler.TaskSchedulingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@EnableMethodSecurity
@RequestMapping(path = "/schedule")
public class TaskSchedulingRestController {
    private static final Logger LOG = LoggerFactory.getLogger(TaskSchedulingRestController.class);
    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private TaskExecutorService taskExecutorService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/taskdef", consumes = "application/json", produces="application/json")
    public void scheduleTask(@RequestBody TaskDefinition taskDefinition) {
        taskDefinitionService.saveTaskDefinition(taskDefinition);

        taskExecutorService.setTaskDefinition(taskDefinition);
        taskSchedulingService.scheduleTask(taskDefinition.getTaskNumber(),
                taskExecutorService,
                taskDefinition.getCronExpression());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/remove/{taskNumber}")
    public void removeJob(@PathVariable Integer taskNumber) {

        taskDefinitionService.removeTaskDefinitionByTaskNumber(taskNumber);
        taskSchedulingService.removeScheduledTask(taskNumber);
    }

}
