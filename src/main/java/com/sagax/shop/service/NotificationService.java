package com.sagax.shop.service;

public interface NotificationService {

    void sendEmail(String to, String subject, String body);

    void sendSms(String phoneNumber, String message);

    void sendPushNotification(String userId, String title, String message);

    void sendSlackMessage(String channel, String message);
}
