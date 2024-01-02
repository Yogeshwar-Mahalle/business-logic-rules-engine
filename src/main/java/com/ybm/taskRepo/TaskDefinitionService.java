/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskRepo;

import com.ybm.dataMappingRepo.models.StatusType;
import com.ybm.taskRepo.dbRepository.TaskDefinitionRepository;
import com.ybm.taskRepo.entities.TaskDefinitionDbModel;
import com.ybm.taskRepo.models.TaskDefinition;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class TaskDefinitionService {
    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;

    public List<TaskDefinition> getTaskDefinitionsByEntity(String entityName) {

        return taskDefinitionRepository.findByLinkedEntity(entityName).stream()
                .map(
                        this::mapTaskDefinitionFromDbModel
                )
                .collect(Collectors.toList());

    }

    public TaskDefinition getTaskDefinitionByTaskNumber(Integer taskNumber) {

        Optional<TaskDefinitionDbModel> taskDefinitionDbModel = taskDefinitionRepository.findByTaskNumber(taskNumber);

        return taskDefinitionDbModel.map(this::mapTaskDefinitionFromDbModel).orElse(null);

    }

    @Transactional
    public TaskDefinition saveTaskDefinition(TaskDefinition taskDefinition) {
        TaskDefinitionDbModel taskDefinitionDbModel = mapTaskDefinitionToDbModel(taskDefinition);
        taskDefinitionDbModel = taskDefinitionRepository.save(taskDefinitionDbModel);

        return mapTaskDefinitionFromDbModel(taskDefinitionDbModel);
    }

    @Transactional
    public List<TaskDefinition> saveTaskDefinitionList(List<TaskDefinition> taskDefinitionList) {
        if( taskDefinitionList == null )
            return null;

        List<TaskDefinitionDbModel> listTaskDefinitionDbModel = taskDefinitionList
                .stream()
                .map(
                        this::mapTaskDefinitionToDbModel
                )
                .collect(Collectors.toList());

        return taskDefinitionRepository.saveAll(listTaskDefinitionDbModel)
                .stream()
                .map(
                        this::mapTaskDefinitionFromDbModel
                )
                .toList();
    }

    @Transactional
    public List<TaskDefinition> removeTaskDefinitionByTaskNumber(Integer taskNumber) {
        if( taskNumber == null )
            return null;

        taskDefinitionRepository.deleteByTaskNumber(taskNumber);

        return taskDefinitionRepository.findAll().stream()
                .map(
                        this::mapTaskDefinitionFromDbModel
                )
                .collect(Collectors.toList());
    }


    private TaskDefinition mapTaskDefinitionFromDbModel(TaskDefinitionDbModel taskDefinitionDbModel){

        return TaskDefinition.builder()
                .linkedEntity(taskDefinitionDbModel.getLinkedEntity())
                .taskNumber(taskDefinitionDbModel.getTaskNumber())
                .taskType(taskDefinitionDbModel.getTaskType())
                .cronExpression(taskDefinitionDbModel.getCronExpression())
                .taskModule(taskDefinitionDbModel.getTaskModule())
                .taskArguments(taskDefinitionDbModel.getTaskArguments())
                .status(StatusType.valueOf(taskDefinitionDbModel.getStatus()))
                .createTimeStamp(taskDefinitionDbModel.getCreateTimeStamp())
                .updateTimeStamp(taskDefinitionDbModel.getUpdateTimeStamp())
                .build();

    }

    private TaskDefinitionDbModel mapTaskDefinitionToDbModel(TaskDefinition taskDefinition){

        return TaskDefinitionDbModel.builder()
                .linkedEntity(taskDefinition.getLinkedEntity())
                .taskNumber(taskDefinition.getTaskNumber())
                .taskType(taskDefinition.getTaskType())
                .cronExpression(taskDefinition.getCronExpression())
                .taskModule(taskDefinition.getTaskModule())
                .taskArguments(taskDefinition.getTaskArguments())
                .status(String.valueOf(taskDefinition.getStatus()))
                .createTimeStamp(taskDefinition.getCreateTimeStamp() == null ? new Date() : taskDefinition.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
