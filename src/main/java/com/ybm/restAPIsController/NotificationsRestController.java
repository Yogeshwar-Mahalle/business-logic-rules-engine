/*
 * Copyright (c) 2024. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.restAPIsController;

import com.ybm.notificationsRepo.AlertNotificationService;
import com.ybm.notificationsRepo.models.AlertNotification;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/notifications")
public class NotificationsRestController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsRestController.class);
    @Autowired
    AlertNotificationService alertNotificationService;

    @GetMapping(value = "/get-all-alert-notification")
    public ResponseEntity<?> getAllAlertNotification() {
        List<AlertNotification> allAlertNotification =
                    alertNotificationService.getAllAlertNotification();
        return ResponseEntity.ok(allAlertNotification);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}")
    public ResponseEntity<?> getAllEntityAlerts(@PathVariable("linkedEntity") String linkedEntity) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntity(linkedEntity);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{userId}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndUser(@PathVariable("linkedEntity") String linkedEntity,
                                                           @PathVariable("userId") String userId) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndUserId(linkedEntity, userId);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{type}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndType(@PathVariable("linkedEntity") String linkedEntity,
                                                           @PathVariable("type") String type) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndType(linkedEntity, type);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{status}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndStatus(@PathVariable("linkedEntity") String linkedEntity,
                                                               @PathVariable("status") String status) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndStatus(linkedEntity, status);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{type}/{userId}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndTypeAndUser(@PathVariable("linkedEntity") String linkedEntity,
                                                                 @PathVariable("type") String type,
                                                                 @PathVariable("userId") String userId) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndTypeAndUserId(linkedEntity, type, userId);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{userId}/{status}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndUserAndStatus(@PathVariable("linkedEntity") String linkedEntity,
                                                                       @PathVariable("userId") String userId,
                                                                       @PathVariable("status") String status) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndUserIdAndStatus(linkedEntity, userId, status);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{type}/{userId}/{status}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndTypeAndUserAndStatus(@PathVariable("linkedEntity") String linkedEntity,
                                                                 @PathVariable("type") String type,
                                                                 @PathVariable("userId") String userId,
                                                                 @PathVariable("status") String status) {
        List<AlertNotification> alertNotificationList =
                alertNotificationService.getAlertNotificationByEntityAndTypeAndUserIdAndStatus(linkedEntity, type, userId, status);
        return ResponseEntity.ok(alertNotificationList);
    }

    @GetMapping(value = "/get-all-alert-notification/{linkedEntity}/{type}/{userId}/{createTimeStamp}")
    public ResponseEntity<?> getAllEntityAlertsByEntityAndTypeAndUserAndCreateTime(@PathVariable("linkedEntity") String linkedEntity,
                                                                 @PathVariable("type") String type,
                                                                 @PathVariable("userId") String userId,
                                                                 @PathVariable("createTimeStamp") Date createTimeStamp) {
        AlertNotification alertNotification =
                alertNotificationService.getAlertNotificationByEntityAndTypeAndUserIdAndCreateTimeStamp(linkedEntity,
                        type,
                        userId,
                        createTimeStamp);
        return ResponseEntity.ok(alertNotification);
    }

    @PostMapping(value = "/update-alert-notification")
    public ResponseEntity<?> updateAlertNotification(@RequestBody AlertNotification alertNotification) {
        AlertNotification alertNotificationUpdated =
                alertNotificationService.saveAlertNotification(alertNotification);
        return ResponseEntity.ok(alertNotificationUpdated);
    }

    @PostMapping(value = "/update-all-alert-notifications")
    public ResponseEntity<?> updateAlertNotificationList(@RequestBody List<AlertNotification> alertNotificationList) {
        List<AlertNotification> alertNotificationsUpdated =
                alertNotificationService.saveAlertNotificationList(alertNotificationList);
        return ResponseEntity.ok(alertNotificationsUpdated);
    }

    @DeleteMapping(value = "/remove-alert-notification/{linkedEntity}/{type}/{userId}/{createTimeStamp}")
    public ResponseEntity<?> removeAlertNotificationById(@PathVariable("linkedEntity") String linkedEntity,
                                                         @PathVariable("type") String type,
                                                         @PathVariable("userId") String userId,
                                                         @PathVariable("createTimeStamp") Date createTimeStamp) {
        List<AlertNotification> allAlertNotifications =
                alertNotificationService.removeAlertNotificationById(linkedEntity, type, userId, createTimeStamp);
        return ResponseEntity.ok(allAlertNotifications);
    }

}
