package br.com.alexandreluchetti.cinealert.entrypoint.dto.user;

import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto (
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    String password
) {

    public UpdateUserRequest toModel() {
        return new UpdateUserRequest(name, password);
    }
}
