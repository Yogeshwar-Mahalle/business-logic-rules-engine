/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.taskRepo.dbRepository;

import com.ybm.taskRepo.entities.SchedulerLockDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerLockRepository  extends JpaRepository<SchedulerLockDbModel, String>  {
}
