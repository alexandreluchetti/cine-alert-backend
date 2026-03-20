package br.com.alexandreluchetti.cinealert.core.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}
