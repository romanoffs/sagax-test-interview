package com.sagax.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

// CASE 10: Prototype scope — but when injected into a Singleton (OrderService)
// via constructor injection, it's created only ONCE, defeating the purpose.
@Service
@Scope("prototype")
@Slf4j
public class EmailNotificationService implements NotificationService {

    private int emailsSentCount = 0;

    @Override
    public void sendEmail(String to, String subject, String body) {
        emailsSentCount++;
        log.info("Sending email to: {}, subject: {}, total sent: {}", to, subject, emailsSentCount);
    }

    // CASE 33: ISP violation — these methods throw UnsupportedOperationException
    @Override
    public void sendSms(String phoneNumber, String message) {
        throw new UnsupportedOperationException("SMS not supported by EmailNotificationService");
    }

    @Override
    public void sendPushNotification(String userId, String title, String message) {
        throw new UnsupportedOperationException("Push notifications not supported by EmailNotificationService");
    }

    @Override
    public void sendSlackMessage(String channel, String message) {
        throw new UnsupportedOperationException("Slack not supported by EmailNotificationService");
    }

    public int getEmailsSentCount() {
        return emailsSentCount;
    }
}
