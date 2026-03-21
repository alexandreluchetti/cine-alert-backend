package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.ForgotPasswordRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDto (
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {

    public ForgotPasswordRequest toModel() {
        return new ForgotPasswordRequest(email());
    }
}
