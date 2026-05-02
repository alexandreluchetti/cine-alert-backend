package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.FcmUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerUseCaseImplTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private FcmUseCase fcmUseCase;

    private NotificationSchedulerUseCaseImpl scheduler;

    private static final String ZONE = "America/Sao_Paulo";

    @BeforeEach
    void setUp() {
        scheduler = new NotificationSchedulerUseCaseImpl(reminderRepository, fcmUseCase);
    }

    @Test
    void processarLembretesPendentes_noPendentes_doesNothing() {
        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(Collections.emptyList());

        scheduler.processarLembretesPendentes();

        verify(fcmUseCase, never()).sendNotification(anyString(), anyString(), anyString());
        verify(reminderRepository, never()).saveAll(any());
    }

    @Test
    void processarLembretesPendentes_withPendentes_sendsNotificationAndSetsSentStatus() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Inception", ContentType.MOVIE, "url", 2010);
        Reminder r = new Reminder("r-1", "u-1", "fcm-token", "c-1", snapshot,
                LocalDateTime.now().minusMinutes(5), ZONE, Recurrence.ONCE, "Time to watch!", ReminderStatus.PENDING, LocalDateTime.now());

        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(List.of(r));

        scheduler.processarLembretesPendentes();

        verify(fcmUseCase).sendNotification(eq("fcm-token"), eq("🎬 Inception"), eq("Time to watch!"));
        assertThat(r.getStatus()).isEqualTo(ReminderStatus.SENT);
        verify(reminderRepository).saveAll(List.of(r));
    }

    @Test
    void processarLembretesPendentes_daily_schedulesNextWithSameZone() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Series", ContentType.SERIES, "url", 2021);
        LocalDateTime scheduledTime = LocalDateTime.now().minusMinutes(5);
        Reminder r = new Reminder("r-1", "u-1", "fcm-token", "c-1", snapshot,
                scheduledTime, ZONE, Recurrence.DAILY, null, ReminderStatus.PENDING, LocalDateTime.now());

        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(List.of(r));

        scheduler.processarLembretesPendentes();

        verify(fcmUseCase).sendNotification(eq("fcm-token"), eq("🎬 Series"), eq("Hora do seu lembrete de cinema!"));
        assertThat(r.getStatus()).isEqualTo(ReminderStatus.SENT);

        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(captor.capture());

        Reminder next = captor.getValue();
        assertThat(next.getRecurrence()).isEqualTo(Recurrence.DAILY);
        assertThat(next.getScheduledAt()).isEqualTo(scheduledTime.plusDays(1));
        assertThat(next.getStatus()).isEqualTo(ReminderStatus.PENDING);
        assertThat(next.getZoneId()).isEqualTo(ZONE);
    }

    @Test
    void processarLembretesPendentes_weekly_schedulesNextWithSameZone() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Series", ContentType.SERIES, "url", 2021);
        LocalDateTime scheduledTime = LocalDateTime.now().minusMinutes(5);
        Reminder r = new Reminder("r-1", "u-1", "fcm-token", "c-1", snapshot,
                scheduledTime, ZONE, Recurrence.WEEKLY, null, ReminderStatus.PENDING, LocalDateTime.now());

        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(List.of(r));

        scheduler.processarLembretesPendentes();

        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(captor.capture());

        Reminder next = captor.getValue();
        assertThat(next.getRecurrence()).isEqualTo(Recurrence.WEEKLY);
        assertThat(next.getScheduledAt()).isEqualTo(scheduledTime.plusWeeks(1));
        assertThat(next.getZoneId()).isEqualTo(ZONE);
    }

    @Test
    void processarLembretesPendentes_fcmError_statusRemainsAndContinues() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Movie", ContentType.MOVIE, "url", 2021);
        Reminder r1 = new Reminder("r-1", "u-1", "bad-token", "c-1", snapshot,
                LocalDateTime.now().minusMinutes(5), ZONE, Recurrence.ONCE, null, ReminderStatus.PENDING, LocalDateTime.now());

        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(List.of(r1));
        doThrow(new RuntimeException("FCM down")).when(fcmUseCase)
                .sendNotification(eq("bad-token"), anyString(), anyString());

        scheduler.processarLembretesPendentes();

        assertThat(r1.getStatus()).isEqualTo(ReminderStatus.PENDING);
        verify(reminderRepository).saveAll(List.of(r1));
    }

    @Test
    void processarLembretesPendentes_nullZoneId_doesNotThrow() {
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Movie", ContentType.MOVIE, "url", 2021);
        LocalDateTime scheduledTime = LocalDateTime.now().minusMinutes(5);
        Reminder r = new Reminder("r-1", "u-1", "fcm-token", "c-1", snapshot,
                scheduledTime, null, Recurrence.DAILY, null, ReminderStatus.PENDING, LocalDateTime.now());

        when(reminderRepository.findByStatusAndScheduledAtLessThanEqual(eq(ReminderStatus.PENDING), any()))
                .thenReturn(List.of(r));

        scheduler.processarLembretesPendentes();

        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(captor.capture());
        assertThat(captor.getValue().getZoneId()).isNull();
    }
}
