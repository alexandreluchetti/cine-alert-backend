package br.com.alexandreluchetti.cinealert.entrypoint.dto.user;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequestDto(
    @NotBlank(message = "FCM token cannot be blank")
    String token
) {}
