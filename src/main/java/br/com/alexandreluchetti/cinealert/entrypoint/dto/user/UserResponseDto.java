package br.com.alexandreluchetti.cinealert.entrypoint.dto.user;

import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;

public record UserResponseDto (
    String id,
    String name,
    String email,
    String avatarUrl,
    long totalReminders,
    long sentReminders
) {

    public static UserResponseDto fromModel(UserResponse model) {
        return new UserResponseDto(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getAvatarUrl(),
                model.getTotalReminders(),
                model.getSentReminders()
        );
    }
}
