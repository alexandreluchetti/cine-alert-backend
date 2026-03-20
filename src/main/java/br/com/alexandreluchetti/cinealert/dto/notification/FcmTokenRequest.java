package br.com.alexandreluchetti.cinealert.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
    @NotBlank(message = "FCM token is required")
    String fcmToken
) {}
