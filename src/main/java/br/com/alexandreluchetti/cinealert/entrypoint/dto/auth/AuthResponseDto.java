package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.AuthResponse;

public record AuthResponseDto (
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserInfoDto user
) {

    public static AuthResponseDto of(String access, String refresh, long expiresIn, UserInfoDto user) {
        return new AuthResponseDto(access, refresh, "Bearer", expiresIn, user);
    }

    public static AuthResponseDto fromModel(AuthResponse authResponse) {
        return new AuthResponseDto(
                authResponse.getAccessToken(),
                authResponse.getRefreshToken(),
                authResponse.getTokenType(),
                authResponse.getExpiresIn(),
                new UserInfoDto(
                        authResponse.getUser().getId(),
                        authResponse.getUser().getName(),
                        authResponse.getUser().getEmail(),
                        authResponse.getUser().getAvatarUrl()
                )
        );
    }
}
