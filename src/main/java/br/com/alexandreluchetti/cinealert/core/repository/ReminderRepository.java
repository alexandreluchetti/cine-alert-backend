package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.dataprovider.entity.ReminderEntity;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository {

    List<ReminderEntity> findByUserIdOrderByScheduledAtAsc(String userId);

    List<ReminderEntity> findByUserIdAndStatusOrderByScheduledAtAsc(String userId, ReminderStatus status);

    Optional<ReminderEntity> findByIdAndUserId(String id, String userId);

    // For the scheduler: find all PENDING reminders whose scheduled time has passed
    List<ReminderEntity> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime);

    long countByUserIdAndStatus(String userId, ReminderStatus status);

    long countByUserId(String userId);

    List<ReminderEntity> saveAll(List<ReminderEntity> pendentes);

    ReminderEntity save(ReminderEntity next);
}
