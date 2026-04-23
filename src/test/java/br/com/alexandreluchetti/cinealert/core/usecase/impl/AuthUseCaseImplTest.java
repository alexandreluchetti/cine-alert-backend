package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.auth.*;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthUseCaseImpl authUseCase;

    private static final long ACCESS_EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCaseImpl(userRepository, passwordEncoder, jwtUtil, ACCESS_EXPIRATION);
    }

    // ─────────────────────────── register ───────────────────────────

    @Test
    void register_success_savesUserAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest("João", "joao@example.com", "pass123");
        when(userRepository.existsByEmail("joao@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        User savedUser = new User("id-1", "João", "joao@example.com", "encodedPass",
                null, null, true, null, null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString())).thenReturn("refresh-token");

        AuthResponse response = authUseCase.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getEmail()).isEqualTo("joao@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsConflict() {
        RegisterRequest request = new RegisterRequest("João", "joao@example.com", "pass123");
        when(userRepository.existsByEmail("joao@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authUseCase.register(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Email already registered");
    }

    // ─────────────────────────── login ───────────────────────────

    @Test
    void login_success_returnsTokens() {
        LoginRequest request = new LoginRequest("joao@example.com", "pass123");
        User user = new User("id-1", "João", "joao@example.com", "encodedPass",
                null, null, true, null, null);

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString())).thenReturn("refresh-token");

        AuthResponse response = authUseCase.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
    }

    @Test
    void login_userNotFound_throwsUnauthorized() {
        LoginRequest request = new LoginRequest("nao@existe.com", "pass");
        when(userRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_invalidPassword_throwsUnauthorized() {
        LoginRequest request = new LoginRequest("joao@example.com", "wrong");
        User user = new User("id-1", "João", "joao@example.com", "encodedPass",
                null, null, true, null, null);

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_inactiveUser_throwsForbidden() {
        LoginRequest request = new LoginRequest("joao@example.com", "pass123");
        User user = new User("id-1", "João", "joao@example.com", "encodedPass",
                null, null, false, null, null);

        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Account is deactivated");
    }

    // ─────────────────────────── refresh ───────────────────────────

    @Test
    void refresh_validRefreshToken_returnsNewTokens() {
        String token = "valid-refresh-token";
        RefreshRequest request = new RefreshRequest(token);

        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("joao@example.com");

        User user = new User("id-1", "João", "joao@example.com", "enc",
                null, null, true, null, null);
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken(anyString(), anyString())).thenReturn("new-refresh");

        AuthResponse response = authUseCase.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void refresh_invalidToken_throwsUnauthorized() {
        RefreshRequest request = new RefreshRequest("bad-token");
        when(jwtUtil.isTokenValid("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.refresh(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    void refresh_accessTokenUsedAsRefresh_throwsUnauthorized() {
        String token = "access-token";
        RefreshRequest request = new RefreshRequest(token);

        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.refresh(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    void refresh_userNotFound_throwsUnauthorized() {
        String token = "valid-refresh";
        RefreshRequest request = new RefreshRequest(token);

        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authUseCase.refresh(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("User not found");
    }

    // ─────────────────────────── forgotPassword ───────────────────────────

    @Test
    void forgotPassword_doesNotThrowAnyException() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("joao@example.com");
        assertThatCode(() -> authUseCase.forgotPassword(request)).doesNotThrowAnyException();
    }
}
