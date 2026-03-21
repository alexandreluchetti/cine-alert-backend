package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.auth.AuthResponse;
import br.com.alexandreluchetti.cinealert.core.model.auth.LoginRequest;
import br.com.alexandreluchetti.cinealert.core.model.auth.RefreshRequest;
import br.com.alexandreluchetti.cinealert.core.model.auth.RegisterRequest;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.auth.*;

public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void forgotPassword(ForgotPasswordRequest request);
}
