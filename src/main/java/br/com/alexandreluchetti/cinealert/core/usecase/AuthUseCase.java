package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.auth.AuthResponse;
import br.com.alexandreluchetti.cinealert.core.model.auth.RegisterRequest;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.auth.*;

public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponseDto login(LoginRequest request);

    AuthResponseDto refresh(RefreshRequest request);

    void forgotPassword(ForgotPasswordRequest request);
}
