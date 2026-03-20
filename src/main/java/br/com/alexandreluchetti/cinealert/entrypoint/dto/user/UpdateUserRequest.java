package br.com.alexandreluchetti.cinealert.entrypoint.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    String password
) {}
