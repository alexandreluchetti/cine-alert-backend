package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.auth.*;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.usecase.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Value("${app.jwt.access-expiration}")
    private long accessExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email already registered");
        }

        User user = User.register(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        if (!user.isActive()) {
            throw AppException.forbidden("Account is deactivated");
        }

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!jwtUtil.isTokenValid(token) || !jwtUtil.isRefreshToken(token)) {
            throw AppException.unauthorized("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.unauthorized("User not found"));

        return buildAuthResponse(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for: {}", request.getEmail());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessExpiration / 1000,
                new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl()));
    }
}
