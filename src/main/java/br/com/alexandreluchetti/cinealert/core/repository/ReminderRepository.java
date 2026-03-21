package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository {

    Optional<Reminder> findById(Long id);

    Reminder save(Reminder reminder);

    List<Reminder> findByUserIdOrderByScheduledAtAsc(Long userId);

    List<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(Long userId, ReminderStatus status);

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    // For the scheduler: find all PENDING reminders whose scheduled time has passed
    List<Reminder> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime);

    long countByUserIdAndStatus(Long userId, ReminderStatus status);

    long countByUserId(Long userId);
}
