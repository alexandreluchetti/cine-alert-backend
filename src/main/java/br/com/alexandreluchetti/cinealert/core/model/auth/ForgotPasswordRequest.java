package br.com.alexandreluchetti.cinealert.core.model.auth;

public class ForgotPasswordRequest {

    private String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
