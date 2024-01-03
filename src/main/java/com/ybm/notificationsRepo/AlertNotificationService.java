/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.notificationsRepo;

import com.ybm.notificationsRepo.entities.AlertNotificationDbModel;
import com.ybm.notificationsRepo.models.AlertNotification;
import com.ybm.notificationsRepo.dbRepository.AlertNotificationRepository;
import com.ybm.rulesBusinessSetupRepo.models.StatusType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlertNotificationService {
    @Autowired
    private AlertNotificationRepository alertNotificationRepository;

    public List<AlertNotification> getAllAlertNotification()
    {
        return alertNotificationRepository.findAll().stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<AlertNotification> getAlertNotificationByEntity(String linkedEntity)
    {
        return alertNotificationRepository.findByLinkedEntity(linkedEntity).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<AlertNotification> getAlertNotificationByEntityAndUserId(String linkedEntity, String userId)
    {
        return alertNotificationRepository.findByLinkedEntityAndUserId(linkedEntity, userId).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<AlertNotification> getAlertNotificationByEntityAndType(String linkedEntity, String type)
    {
        return alertNotificationRepository.findByLinkedEntityAndType(linkedEntity, type).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<AlertNotification> getAlertNotificationByEntityAndTypeAndUserId(String linkedEntity,
                                                                       String type,
                                                                       String userId)
    {
        return alertNotificationRepository.findByLinkedEntityAndTypeAndUserId(linkedEntity, type, userId).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<AlertNotification> getAlertNotificationByEntityAndStatus(String linkedEntity, String status)
    {
        return alertNotificationRepository.findByLinkedEntityAndStatus(linkedEntity, status).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());

    }

    public List<AlertNotification> getAlertNotificationByEntityAndUserIdAndStatus(String linkedEntity,
                                                                                  String userId,
                                                                                  String status)
    {
        return alertNotificationRepository.findByLinkedEntityAndUserIdAndStatus(linkedEntity, userId, status).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public List<AlertNotification> getAlertNotificationByEntityAndTypeAndUserIdAndStatus(String linkedEntity,
                                                                                        String type,
                                                                                        String userId,
                                                                                        String status)
    {
        return alertNotificationRepository.findByLinkedEntityAndTypeAndUserIdAndStatus(linkedEntity, type, userId, status).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    public AlertNotification getAlertNotificationByEntityAndTypeAndUserIdAndCreateTimeStamp(String linkedEntity,
                                                                                         String type,
                                                                                         String userId,
                                                                                            Date createTimeStamp)
    {
        Optional<AlertNotificationDbModel> interfaceProfileDbModel =
                alertNotificationRepository.findByLinkedEntityAndTypeAndUserIdAndCreateTimeStamp(linkedEntity,
                                                                                                 type,
                                                                                                 userId,
                                                                                                 createTimeStamp);

        return interfaceProfileDbModel.map(this::mapAlertNotificationFromDbModel).orElse(null);
    }

    @Transactional
    public AlertNotification saveAlertNotification(AlertNotification alertNotification) {
        AlertNotificationDbModel alertNotificationDbModel = mapAlertNotificationToDbModel(alertNotification);
        alertNotificationDbModel = alertNotificationRepository.save(alertNotificationDbModel);

        return mapAlertNotificationFromDbModel(alertNotificationDbModel);
    }

    @Transactional
    public List<AlertNotification> saveAlertNotificationList(List<AlertNotification> alertNotificationList) {

        List<AlertNotificationDbModel> listAlertNotificationDbDBModel = alertNotificationList.stream()
                .map(
                        this::mapAlertNotificationToDbModel
                )
                .collect(Collectors.toList());

        return alertNotificationRepository.saveAll(listAlertNotificationDbDBModel).stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AlertNotification> removeAlertNotificationById(String linkedEntity,
                                                               String type,
                                                               String userId,
                                                               Date createTimeStamp){
        if( linkedEntity == null || type == null || userId == null || createTimeStamp == null )
            return null;

        alertNotificationRepository.deleteByLinkedEntityAndTypeAndUserIdAndCreateTimeStamp(linkedEntity, type, userId, createTimeStamp);

        return alertNotificationRepository.findAll().stream()
                .map(
                        this::mapAlertNotificationFromDbModel
                )
                .collect(Collectors.toList());
    }

    private AlertNotification mapAlertNotificationFromDbModel(AlertNotificationDbModel alertNotificationDbModel){

        return AlertNotification.builder()
                .linkedEntity(alertNotificationDbModel.getLinkedEntity())
                .type(alertNotificationDbModel.getType())
                .userId(alertNotificationDbModel.getUserId())
                .subject(alertNotificationDbModel.getSubject())
                .message(alertNotificationDbModel.getMessage())
                .status(StatusType.valueOf(alertNotificationDbModel.getStatus()))
                .createTimeStamp(alertNotificationDbModel.getCreateTimeStamp())
                .updateTimeStamp(alertNotificationDbModel.getUpdateTimeStamp())
                .build();

    }


    private AlertNotificationDbModel mapAlertNotificationToDbModel(AlertNotification alertNotification){

        return AlertNotificationDbModel.builder()
                .linkedEntity(alertNotification.getLinkedEntity())
                .type(alertNotification.getType())
                .userId(alertNotification.getUserId())
                .subject(alertNotification.getSubject())
                .message(alertNotification.getMessage())
                .status(String.valueOf(alertNotification.getStatus()))
                .createTimeStamp(alertNotification.getCreateTimeStamp())
                .createTimeStamp(alertNotification.getCreateTimeStamp() == null ? new Date() : alertNotification.getCreateTimeStamp())
                .updateTimeStamp(new Date())
                .build();

    }
}
