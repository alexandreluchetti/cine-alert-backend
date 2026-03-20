package br.com.alexandreluchetti.cinealert.repository;

import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserIdOrderByScheduledAtAsc(Long userId);

    List<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(Long userId, ReminderStatus status);

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    // For the scheduler: find all PENDING reminders whose scheduled time has passed
    List<Reminder> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime);

    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.user.id = :userId AND r.status = :status")
    long countByUserIdAndStatus(Long userId, ReminderStatus status);

    long countByUserId(Long userId);
}
