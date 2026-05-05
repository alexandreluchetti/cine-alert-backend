package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.InOrder;

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
    void updateFcmToken_noPendingReminders_onlySavesUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(Collections.emptyList());

        userUseCase.updateFcmToken(user, "new-fcm-token");

        assertThat(user.getFcmToken()).isEqualTo("new-fcm-token");
        verify(userRepository).save(user);
        verify(reminderRepository, never()).saveAll(any());
    }

    @Test
    void updateFcmToken_withPendingReminders_updatesTokenOnAll() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Inception", ContentType.MOVIE, "url", 2010);
        Reminder r1 = new Reminder("r-1", "user-1", "old-token", "c-1", snapshot,
                LocalDateTime.now().plusDays(1), "America/Sao_Paulo", Recurrence.ONCE, null, ReminderStatus.PENDING, null);
        Reminder r2 = new Reminder("r-2", "user-1", "old-token", "c-2", snapshot,
                LocalDateTime.now().plusDays(2), "America/Sao_Paulo", Recurrence.DAILY, null, ReminderStatus.PENDING, null);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(List.of(r1, r2));

        userUseCase.updateFcmToken(user, "new-fcm-token");

        assertThat(user.getFcmToken()).isEqualTo("new-fcm-token");
        assertThat(r1.getUserFcmToken()).isEqualTo("new-fcm-token");
        assertThat(r2.getUserFcmToken()).isEqualTo("new-fcm-token");
        verify(reminderRepository).saveAll(List.of(r1, r2));
    }

    @Test
    void updateFcmToken_withSinglePendingReminder_updatesToken() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Inception", ContentType.MOVIE, "url", 2010);
        Reminder r = new Reminder("r-1", "user-1", "old-token", "c-1", snapshot,
                LocalDateTime.now().plusDays(1), "America/Sao_Paulo", Recurrence.ONCE, null, ReminderStatus.PENDING, null);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(List.of(r));

        userUseCase.updateFcmToken(user, "new-fcm-token");

        assertThat(r.getUserFcmToken()).isEqualTo("new-fcm-token");
        verify(reminderRepository).saveAll(List.of(r));
    }

    @Test
    void updateFcmToken_savesUserBeforeUpdatingReminders() {
        // Garante que o token do usuário é persistido ANTES de propagar para os reminders
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Inception", ContentType.MOVIE, "url", 2010);
        Reminder r = new Reminder("r-1", "user-1", "old-token", "c-1", snapshot,
                LocalDateTime.now().plusDays(1), "America/Sao_Paulo", Recurrence.ONCE, null, ReminderStatus.PENDING, null);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(List.of(r));

        userUseCase.updateFcmToken(user, "new-fcm-token");

        InOrder inOrder = inOrder(userRepository, reminderRepository);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(reminderRepository).saveAll(any());
    }

    @Test
    void updateFcmToken_onlyQueriesPendingStatus() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(Collections.emptyList());

        userUseCase.updateFcmToken(user, "new-fcm-token");

        // Garante que somente PENDING é consultado — SENT e CANCELLED não são tocados
        verify(reminderRepository).findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING);
        verify(reminderRepository, never()).findByUserIdAndStatusOrderByScheduledAtAsc(any(), eq(ReminderStatus.SENT));
        verify(reminderRepository, never()).findByUserIdAndStatusOrderByScheduledAtAsc(any(), eq(ReminderStatus.CANCELLED));
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
