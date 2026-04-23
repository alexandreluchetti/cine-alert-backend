package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.content.Content;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderUseCaseImplTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private ContentRepository contentRepository;

    private ReminderUseCaseImpl reminderUseCase;

    private User user;
    private Content content;
    private ContentSnapshot snapshot;

    @BeforeEach
    void setUp() {
        reminderUseCase = new ReminderUseCaseImpl(reminderRepository, contentRepository);

        user = new User("user-1", "João", "joao@example.com", "enc",
                null, "fcm-token", true, null, null);

        content = new Content("content-1", "tt1234567", "Inception",
                ContentType.MOVIE, "https://poster.url", 2010,
                new BigDecimal("8.8"), "ACTION", "Dream heist", null, 148);

        snapshot = new ContentSnapshot("tt1234567", "Inception",
                ContentType.MOVIE, "https://poster.url", 2010);
    }

    // ─────────────────────────── getReminders ───────────────────────────

    @Test
    void getReminders_noFilter_returnsAllReminders() {
        Reminder r1 = buildReminder("r-1", ReminderStatus.PENDING);
        Reminder r2 = buildReminder("r-2", ReminderStatus.SENT);

        when(reminderRepository.findByUserIdOrderByScheduledAtAsc("user-1"))
                .thenReturn(List.of(r1, r2));

        List<ReminderResponse> result = reminderUseCase.getReminders(user, null);

        assertThat(result).hasSize(2);
        verify(reminderRepository).findByUserIdOrderByScheduledAtAsc("user-1");
    }

    @Test
    void getReminders_withStatusFilter_returnsFilteredReminders() {
        Reminder r = buildReminder("r-1", ReminderStatus.PENDING);
        when(reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc("user-1", ReminderStatus.PENDING))
                .thenReturn(List.of(r));

        List<ReminderResponse> result = reminderUseCase.getReminders(user, ReminderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReminderStatus.PENDING);
    }

    // ─────────────────────────── create ───────────────────────────

    @Test
    void create_success_returnsReminderResponse() {
        ReminderRequest request = new ReminderRequest(
                "content-1", LocalDateTime.now().plusDays(1), Recurrence.ONCE, "Watch this!");

        when(contentRepository.findById("content-1")).thenReturn(Optional.of(content));

        Reminder saved = buildReminder("r-new", ReminderStatus.PENDING);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(saved);

        ReminderResponse response = reminderUseCase.create(user, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("r-new");
    }

    @Test
    void create_contentNotFound_throwsNotFound() {
        ReminderRequest request = new ReminderRequest(
                "invalid-id", LocalDateTime.now().plusDays(1), null, null);

        when(contentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderUseCase.create(user, request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Content not found");
    }

    @Test
    void create_nullRecurrence_defaultsToOnce() {
        ReminderRequest request = new ReminderRequest(
                "content-1", LocalDateTime.now().plusDays(1), null, null);

        when(contentRepository.findById("content-1")).thenReturn(Optional.of(content));

        Reminder saved = buildReminder("r-new", ReminderStatus.PENDING);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(saved);

        reminderUseCase.create(user, request);

        verify(reminderRepository).save(argThat(r -> r.getRecurrence() == Recurrence.ONCE));
    }

    // ─────────────────────────── getById ───────────────────────────

    @Test
    void getById_found_returnsReminderResponse() {
        Reminder r = buildReminder("r-1", ReminderStatus.PENDING);
        when(reminderRepository.findByIdAndUserId("r-1", "user-1")).thenReturn(Optional.of(r));

        ReminderResponse response = reminderUseCase.getById(user, "r-1");

        assertThat(response.getId()).isEqualTo("r-1");
    }

    @Test
    void getById_notFound_throwsNotFound() {
        when(reminderRepository.findByIdAndUserId("ghost", "user-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderUseCase.getById(user, "ghost"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Reminder not found");
    }

    // ─────────────────────────── update ───────────────────────────

    @Test
    void update_success_updatesFields() {
        Reminder r = buildReminder("r-1", ReminderStatus.PENDING);
        when(reminderRepository.findByIdAndUserId("r-1", "user-1")).thenReturn(Optional.of(r));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(r);

        LocalDateTime newTime = LocalDateTime.now().plusDays(5);
        ReminderRequest request = new ReminderRequest("content-1", newTime, Recurrence.DAILY, "Updated!");

        ReminderResponse response = reminderUseCase.update(user, "r-1", request);

        assertThat(response).isNotNull();
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void update_reminderAlreadySent_throwsBadRequest() {
        Reminder sent = buildReminder("r-1", ReminderStatus.SENT);
        when(reminderRepository.findByIdAndUserId("r-1", "user-1")).thenReturn(Optional.of(sent));

        ReminderRequest request = new ReminderRequest("content-1", LocalDateTime.now().plusDays(1), null, null);

        assertThatThrownBy(() -> reminderUseCase.update(user, "r-1", request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Cannot cancel/update an already sent reminder");
    }

    // ─────────────────────────── cancel ───────────────────────────

    @Test
    void cancel_success_setsStatusCancelled() {
        Reminder r = buildReminder("r-1", ReminderStatus.PENDING);
        when(reminderRepository.findByIdAndUserId("r-1", "user-1")).thenReturn(Optional.of(r));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(r);

        reminderUseCase.cancel(user, "r-1");

        verify(reminderRepository).save(argThat(rem -> rem.getStatus() == ReminderStatus.CANCELLED));
    }

    @Test
    void cancel_reminderAlreadySent_throwsBadRequest() {
        Reminder sent = buildReminder("r-1", ReminderStatus.SENT);
        when(reminderRepository.findByIdAndUserId("r-1", "user-1")).thenReturn(Optional.of(sent));

        assertThatThrownBy(() -> reminderUseCase.cancel(user, "r-1"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Cannot cancel/update an already sent reminder");
    }

    @Test
    void cancel_reminderNotFound_throwsNotFound() {
        when(reminderRepository.findByIdAndUserId("x", "user-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderUseCase.cancel(user, "x"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Reminder not found");
    }

    // ─────────────────────────── getStats ───────────────────────────

    @Test
    void getStats_returnsCorrectCounts() {
        when(reminderRepository.countByUserId("user-1")).thenReturn(10L);
        when(reminderRepository.countByUserIdAndStatus("user-1", ReminderStatus.PENDING)).thenReturn(4L);
        when(reminderRepository.countByUserIdAndStatus("user-1", ReminderStatus.SENT)).thenReturn(5L);
        when(reminderRepository.countByUserIdAndStatus("user-1", ReminderStatus.CANCELLED)).thenReturn(1L);

        ReminderStatsResponse stats = reminderUseCase.getStats(user);

        assertThat(stats.getTotal()).isEqualTo(10L);
        assertThat(stats.getPending()).isEqualTo(4L);
        assertThat(stats.getSent()).isEqualTo(5L);
        assertThat(stats.getCancelled()).isEqualTo(1L);
    }

    // ─────────────────────────── helpers ───────────────────────────

    private Reminder buildReminder(String id, ReminderStatus status) {
        return new Reminder(id, "user-1", "fcm-token", "content-1",
                snapshot, LocalDateTime.now().plusDays(1),
                Recurrence.ONCE, "Watch this!", status, LocalDateTime.now());
    }
}
