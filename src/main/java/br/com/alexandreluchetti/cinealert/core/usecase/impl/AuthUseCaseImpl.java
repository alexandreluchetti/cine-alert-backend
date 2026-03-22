package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtil;
import br.com.alexandreluchetti.cinealert.core.model.auth.*;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.UserEntity;
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

        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userEntity = userRepository.save(userEntity);

        return buildAuthResponse(userEntity);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        if (!userEntity.isActive()) {
            throw AppException.forbidden("Account is deactivated");
        }

        return buildAuthResponse(userEntity);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!jwtUtil.isTokenValid(token) || !jwtUtil.isRefreshToken(token)) {
            throw AppException.unauthorized("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(token);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.unauthorized("User not found"));

        return buildAuthResponse(userEntity);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for: {}", request.getEmail());
    }

    private AuthResponse buildAuthResponse(UserEntity userEntity) {
        String accessToken = jwtUtil.generateAccessToken(userEntity.getEmail(), userEntity.getId());
        String refreshToken = jwtUtil.generateRefreshToken(userEntity.getEmail(), userEntity.getId());

        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessExpiration / 1000,
                new UserInfo(userEntity.getId(), userEntity.getName(), userEntity.getEmail(), userEntity.getAvatarUrl()));
    }
}
