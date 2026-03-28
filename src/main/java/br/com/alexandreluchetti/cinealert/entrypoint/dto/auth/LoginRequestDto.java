package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.LoginRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto (
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {

    public LoginRequest toModel() {
        return new LoginRequest(email, password);
    }
}
