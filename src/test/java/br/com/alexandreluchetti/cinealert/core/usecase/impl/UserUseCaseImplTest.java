package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private UserUseCaseImpl userUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCaseImpl(userRepository, reminderRepository, passwordEncoder);
        user = new User("user-1", "João", "joao@example.com", "encodedPass",
                "https://avatar.url", "fcm-token", true, null, null);
    }

    // ─────────────────────────── getProfile ───────────────────────────

    @Test
    void getProfile_returnsUserResponseWithCounts() {
        when(reminderRepository.countByUserId("user-1")).thenReturn(5L);
        when(reminderRepository.countByUserIdAndStatus("user-1", ReminderStatus.SENT)).thenReturn(3L);

        UserResponse response = userUseCase.getProfile(user);

        assertThat(response.getId()).isEqualTo("user-1");
        assertThat(response.getName()).isEqualTo("João");
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
        assertThat(response.getTotalReminders()).isEqualTo(5L);
        assertThat(response.getSentReminders()).isEqualTo(3L);
    }

    // ─────────────────────────── updateProfile ───────────────────────────

    @Test
    void updateProfile_withNewName_updatesName() {
        when(reminderRepository.countByUserId(any())).thenReturn(0L);
        when(reminderRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest("Novo Nome", null);
        userUseCase.updateProfile(user, request);

        assertThat(user.getName()).isEqualTo("Novo Nome");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_withNewPassword_encodesAndSaves() {
        when(reminderRepository.countByUserId(any())).thenReturn(0L);
        when(reminderRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest(null, "newPass");
        userUseCase.updateProfile(user, request);

        assertThat(user.getPassword()).isEqualTo("encodedNewPass");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_blankName_doesNotUpdateName() {
        when(reminderRepository.countByUserId(any())).thenReturn(0L);
        when(reminderRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        String originalName = user.getName();
        UpdateUserRequest request = new UpdateUserRequest("  ", null);
        userUseCase.updateProfile(user, request);

        assertThat(user.getName()).isEqualTo(originalName);
    }

    // ─────────────────────────── updateAvatar ───────────────────────────

    @Test
    void updateAvatar_savesNewUrlAndReturnsProfile() {
        when(reminderRepository.countByUserId(any())).thenReturn(0L);
        when(reminderRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userUseCase.updateAvatar(user, "https://new-avatar.url");

        assertThat(user.getAvatarUrl()).isEqualTo("https://new-avatar.url");
        verify(userRepository).save(user);
    }

    // ─────────────────────────── updateFcmToken ───────────────────────────

    @Test
    void updateFcmToken_savesToken() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userUseCase.updateFcmToken(user, "new-fcm-token");

        assertThat(user.getFcmToken()).isEqualTo("new-fcm-token");
        verify(userRepository).save(user);
    }

    // ─────────────────────────── deleteAccount ───────────────────────────

    @Test
    void deleteAccount_setsUserInactive() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userUseCase.deleteAccount(user);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    // ─────────────────────────── getAuthenticatedUser ───────────────────────────

    @Test
    void getAuthenticatedUser_success_returnsUserFromDb() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(user));

        User result = userUseCase.getAuthenticatedUser(authentication);

        assertThat(result.getId()).isEqualTo("user-1");
    }

    @Test
    void getAuthenticatedUser_notFound_throwsNotFound() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.findByEmail("joao@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.getAuthenticatedUser(authentication))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("User not found");
    }
}
