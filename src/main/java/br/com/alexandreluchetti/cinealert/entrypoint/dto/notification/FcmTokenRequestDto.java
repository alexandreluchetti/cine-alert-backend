package br.com.alexandreluchetti.cinealert.entrypoint.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequestDto (
    @NotBlank(message = "FCM token is required")
    String fcmToken
) {}
