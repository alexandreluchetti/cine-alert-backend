package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.RefreshRequest;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto (
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {

    public RefreshRequest toModel() {
        return new RefreshRequest(refreshToken());
    }
}
