package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.auth.*;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.usecase.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthUseCaseImpl implements AuthUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUseCaseImpl.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final long accessExpiration;

    public AuthUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, long accessExpiration) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.accessExpiration = accessExpiration;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        LOGGER.info("Registering new user {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            LOGGER.warn("User with email {} already exists", request.getEmail());
            throw AppException.conflict("Email already registered");
        }

        User user = User.register(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        user = userRepository.save(user);

        LOGGER.info("New user registered {}", user.getId());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        LOGGER.info("Authenticating user {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.unauthorized("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            LOGGER.warn("Invalid password");
            throw AppException.unauthorized("Invalid email or password");
        }

        if (!user.isActive()) {
            LOGGER.warn("User is not active");
            throw AppException.forbidden("Account is deactivated");
        }

        LOGGER.info("Authenticated user {}", request.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        LOGGER.info("Refreshing token");

        String token = request.getRefreshToken();

        if (!jwtUtil.isTokenValid(token) || !jwtUtil.isRefreshToken(token)) {
            LOGGER.warn("Invalid refresh token");
            throw AppException.unauthorized("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.unauthorized("User not found"));

        LOGGER.info("Refreshed user {}", email);
        return buildAuthResponse(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        LOGGER.info("Password reset requested for: {}", request.getEmail());
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
