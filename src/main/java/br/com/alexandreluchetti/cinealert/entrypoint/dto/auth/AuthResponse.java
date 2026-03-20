package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserInfo user
) {
    public record UserInfo(Long id, String name, String email, String avatarUrl) {}

    public static AuthResponse of(String access, String refresh, long expiresIn, UserInfo user) {
        return new AuthResponse(access, refresh, "Bearer", expiresIn, user);
    }
}
