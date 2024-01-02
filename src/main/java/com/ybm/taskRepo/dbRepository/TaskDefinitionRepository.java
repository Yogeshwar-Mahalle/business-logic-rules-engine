/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskRepo.dbRepository;

import com.ybm.taskRepo.entities.TaskDefinitionDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinitionDbModel, String> {

    Collection<TaskDefinitionDbModel> findByLinkedEntity(String entityName);

    Optional<TaskDefinitionDbModel> findByTaskNumber(Integer taskNumber);

    void deleteByTaskNumber(Integer taskNumber);
}
