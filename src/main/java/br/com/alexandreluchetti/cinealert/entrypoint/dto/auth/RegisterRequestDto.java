package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.RegisterRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto (
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    String password
) {

    public RegisterRequest toModel() {
        return new RegisterRequest(name(), email(), password());
    }

}
