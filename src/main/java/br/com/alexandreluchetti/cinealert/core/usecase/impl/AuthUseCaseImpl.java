package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtil;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import br.com.alexandreluchetti.cinealert.core.dto.auth.*;
import br.com.alexandreluchetti.cinealert.exception.AppException;
import br.com.alexandreluchetti.cinealert.model.User;
import br.com.alexandreluchetti.cinealert.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw AppException.conflict("Email already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> AppException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        if (!user.isActive()) {
            throw AppException.forbidden("Account is deactivated");
        }

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();

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
        // In a real app, send email with reset link
        // For now, log and return success (don't reveal if email exists)
        log.info("Password reset requested for: {}", request.email());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessExpiration / 1000,
                new AuthResponse.UserInfo(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl()));
    }
}
