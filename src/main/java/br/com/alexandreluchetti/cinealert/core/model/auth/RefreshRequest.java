package br.com.alexandreluchetti.cinealert.core.model.auth;

public class RefreshRequest {

    private String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
