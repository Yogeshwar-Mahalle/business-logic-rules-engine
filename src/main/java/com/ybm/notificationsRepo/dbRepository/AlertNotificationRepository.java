/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.notificationsRepo.dbRepository;

import com.ybm.notificationsRepo.entities.AlertNotificationDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Repository
public interface AlertNotificationRepository extends JpaRepository<AlertNotificationDbModel, String> {
    Collection<AlertNotificationDbModel> findByLinkedEntityAndUserId(String linkedEntity, String userId);


    Collection<AlertNotificationDbModel> findByLinkedEntity(String linkedEntity);

    Collection<AlertNotificationDbModel> findByLinkedEntityAndStatus(String linkedEntity, String status);

    Collection<AlertNotificationDbModel> findByLinkedEntityAndType(String linkedEntity, String type);

    Collection<AlertNotificationDbModel> findByLinkedEntityAndUserIdAndStatus(String linkedEntity, String userId, String status);

    Collection<AlertNotificationDbModel> findByLinkedEntityAndTypeAndUserId(String linkedEntity, String type, String userId);

    Collection<AlertNotificationDbModel> findByLinkedEntityAndTypeAndUserIdAndStatus(String linkedEntity, String type, String userId, String status);

    Optional<AlertNotificationDbModel> findByLinkedEntityAndTypeAndUserIdAndCreateTimeStamp(String linkedEntity, String type, String userId, Date createTimeStamp);

    void deleteByLinkedEntityAndTypeAndUserIdAndCreateTimeStamp(String linkedEntity, String type, String userId, Date createTimeStamp);
}
