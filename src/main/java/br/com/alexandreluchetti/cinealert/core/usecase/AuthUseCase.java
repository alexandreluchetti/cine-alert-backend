package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.dto.auth.*;

public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void forgotPassword(ForgotPasswordRequest request);
}
