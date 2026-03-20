package br.com.alexandreluchetti.cinealert.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    @Value("${app.firebase.enabled:false}")
    private boolean firebaseEnabled;

    public void sendNotification(String fcmToken, String title, String body) {
        if (!firebaseEnabled) {
            log.info("[FCM DISABLED] Would send to token={} | title='{}' | body='{}'", fcmToken, title, body);
            return;
        }

        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token is null or blank, skipping notification for '{}'", title);
            return;
        }

        try {
            com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                    .setToken(fcmToken)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body != null && !body.isBlank() ? body : "🎬 Seu lembrete chegou!")
                            .build())
                    .putData("title", title)
                    .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();

            String response = com.google.firebase.messaging.FirebaseMessaging.getInstance().send(message);
            log.info("FCM notification sent successfully: {}", response);
        } catch (Exception e) {
            log.error("Error sending FCM notification to token {}: {}", fcmToken, e.getMessage());
        }
    }
}
