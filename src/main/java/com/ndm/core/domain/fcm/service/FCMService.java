package com.ndm.core.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ndm.core.common.enums.NotificationCode;
import com.ndm.core.entity.User;
import com.ndm.core.model.ErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotificationForUser(NotificationCode notificationCode, User target) {
        if (!StringUtils.hasText(target.getFcmToken())) {
            log.error(ErrorInfo.FCM_TOKEN_NOT_FOUND.getMessage());
            return;
        }

        Notification notification = Notification
                .builder()
                .setTitle(notificationCode.getTitle())
                .setBody(notificationCode.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(target.getFcmToken())
                .setNotification(notification)
                .build();



        try {
            firebaseMessaging.send(message);
        }
        catch(FirebaseMessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
