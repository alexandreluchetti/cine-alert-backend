package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.UserInfo;

public record UserInfoDto(
        Long id,
        String name,
        String email,
        String avatarUrl
) {

    public static UserInfoDto from(UserInfo userInfo) {
        return new UserInfoDto(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getAvatarUrl()
        );
    }
}
