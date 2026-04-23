package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentSnapshotEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.ReminderEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ReminderMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderRepositoryImplTest {

    @Mock
    private ReminderMongoRepository reminderMongoRepository;

    private ReminderRepositoryImpl reminderRepositoryImpl;

    @BeforeEach
    void setUp() {
        reminderRepositoryImpl = new ReminderRepositoryImpl(reminderMongoRepository);
    }

    @Test
    void findByUserIdOrderByScheduledAtAsc_returnsList() {
        ReminderEntity entity = buildReminderEntity();
        when(reminderMongoRepository.findByUserIdOrderByScheduledAtAsc("u-1")).thenReturn(List.of(entity));

        List<Reminder> result = reminderRepositoryImpl.findByUserIdOrderByScheduledAtAsc("u-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("u-1");
    }

    @Test
    void findByUserIdAndStatusOrderByScheduledAtAsc_returnsList() {
        ReminderEntity entity = buildReminderEntity();
        when(reminderMongoRepository.findByUserIdAndStatusOrderByScheduledAtAsc("u-1", ReminderStatus.PENDING))
                .thenReturn(List.of(entity));

        List<Reminder> result = reminderRepositoryImpl.findByUserIdAndStatusOrderByScheduledAtAsc("u-1", ReminderStatus.PENDING);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByIdAndUserId_returnsOptional() {
        ReminderEntity entity = buildReminderEntity();
        when(reminderMongoRepository.findByIdAndUserId("r-1", "u-1")).thenReturn(Optional.of(entity));

        Optional<Reminder> result = reminderRepositoryImpl.findByIdAndUserId("r-1", "u-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("r-1");
    }

    @Test
    void findByStatusAndScheduledAtLessThanEqual_returnsList() {
        ReminderEntity entity = buildReminderEntity();
        LocalDateTime now = LocalDateTime.now();
        when(reminderMongoRepository.findByStatusAndScheduledAtLessThanEqual(ReminderStatus.PENDING, now))
                .thenReturn(List.of(entity));

        List<Reminder> result = reminderRepositoryImpl.findByStatusAndScheduledAtLessThanEqual(ReminderStatus.PENDING, now);

        assertThat(result).hasSize(1);
    }

    @Test
    void countByUserIdAndStatus_returnsCount() {
        when(reminderMongoRepository.countByUserIdAndStatus("u-1", ReminderStatus.PENDING)).thenReturn(5L);

        long result = reminderRepositoryImpl.countByUserIdAndStatus("u-1", ReminderStatus.PENDING);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void countByUserId_returnsCount() {
        when(reminderMongoRepository.countByUserId("u-1")).thenReturn(10L);

        long result = reminderRepositoryImpl.countByUserId("u-1");

        assertThat(result).isEqualTo(10L);
    }

    @Test
    void saveAll_savesAndReturnsList() {
        ReminderEntity entity = buildReminderEntity();
        when(reminderMongoRepository.saveAll(any())).thenReturn(List.of(entity));

        List<Reminder> pendentes = List.of(entity.toModel());
        List<Reminder> result = reminderRepositoryImpl.saveAll(pendentes);

        assertThat(result).hasSize(1);
        verify(reminderMongoRepository).saveAll(any());
    }

    @Test
    void save_savesAndReturnsReminder() {
        ReminderEntity entity = buildReminderEntity();
        when(reminderMongoRepository.save(any(ReminderEntity.class))).thenReturn(entity);

        Reminder reminder = entity.toModel();
        Reminder result = reminderRepositoryImpl.save(reminder);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("r-1");
        verify(reminderMongoRepository).save(any(ReminderEntity.class));
    }

    private ReminderEntity buildReminderEntity() {
        ContentSnapshotEntity snapshot = ContentSnapshotEntity.builder()
                .imdbId("tt123")
                .title("Title")
                .type(ContentType.MOVIE)
                .posterUrl("url")
                .year(2020)
                .build();

        return ReminderEntity.builder()
                .id("r-1")
                .userId("u-1")
                .userFcmToken("fcm")
                .contentId("c-1")
                .contentSnapshot(snapshot)
                .scheduledAt(LocalDateTime.now())
                .recurrence(Recurrence.ONCE)
                .message("Msg")
                .status(ReminderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
