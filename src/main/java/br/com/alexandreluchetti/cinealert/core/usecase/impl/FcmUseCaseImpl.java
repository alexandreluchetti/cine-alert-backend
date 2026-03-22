package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.usecase.FcmUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FcmUseCaseImpl implements FcmUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FcmUseCaseImpl.class);

    private final boolean firebaseEnabled;

    public FcmUseCaseImpl(boolean firebaseEnabled) {
        this.firebaseEnabled = firebaseEnabled;
    }

    public void sendNotification(String fcmToken, String title, String body) {
        if (!firebaseEnabled) {
            LOGGER.info("[FCM DISABLED] Would send to token={} | title='{}' | body='{}'", fcmToken, title, body);
            return;
        }

        if (fcmToken == null || fcmToken.isBlank()) {
            LOGGER.warn("FCM token is null or blank, skipping notification for '{}'", title);
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
            LOGGER.info("FCM notification sent successfully: {}", response);
        } catch (Exception e) {
            LOGGER.error("Error sending FCM notification to token {}: {}", fcmToken, e.getMessage());
        }
    }
}
