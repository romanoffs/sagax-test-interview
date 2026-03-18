package com.sagax.shop.service;

// CASE 33: Interface Segregation Principle violation — fat interface.
// EmailNotificationService will throw UnsupportedOperationException for SMS/Push/Slack.
// Should be split into separate interfaces: EmailSender, SmsSender, PushSender, SlackSender.
public interface NotificationService {

    void sendEmail(String to, String subject, String body);

    void sendSms(String phoneNumber, String message);

    void sendPushNotification(String userId, String title, String message);

    void sendSlackMessage(String channel, String message);
}
