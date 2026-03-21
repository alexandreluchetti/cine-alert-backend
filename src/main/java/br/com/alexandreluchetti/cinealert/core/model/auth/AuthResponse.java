package br.com.alexandreluchetti.cinealert.core.model.auth;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static AuthResponse of(String access, String refresh, long expiresIn, UserInfo user) {
        return new AuthResponse(access, refresh, "Bearer", expiresIn, user);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserInfo getUser() {
        return user;
    }
}
