package br.com.alexandreluchetti.cinealert.dataprovider.repository;

import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepositoryImpl extends MongoRepository<Reminder, String> {

    List<Reminder> findByUserIdOrderByScheduledAtAsc(String userId);

    List<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(String userId, ReminderStatus status);

    Optional<Reminder> findByIdAndUserId(String id, String userId);

    // For the scheduler: find all PENDING reminders whose scheduled time has passed
    List<Reminder> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime);

    long countByUserIdAndStatus(String userId, ReminderStatus status);

    long countByUserId(String userId);
}
