package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.ReminderEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ReminderMongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReminderRepositoryImpl implements ReminderRepository {

    private final ReminderMongoRepository reminderMongoRepository;

    public ReminderRepositoryImpl(ReminderMongoRepository reminderMongoRepository) {
        this.reminderMongoRepository = reminderMongoRepository;
    }

    @Override
    public List<Reminder> findByUserIdOrderByScheduledAtAsc(String userId) {
        return reminderMongoRepository.findByUserIdOrderByScheduledAtAsc(userId).stream()
                .map(ReminderEntity::toModel).toList();
    }

    @Override
    public List<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(String userId, ReminderStatus status) {
        return reminderMongoRepository.findByUserIdAndStatusOrderByScheduledAtAsc(userId, status).stream()
                .map(ReminderEntity::toModel).toList();
    }

    @Override
    public Optional<Reminder> findByIdAndUserId(String id, String userId) {
        return reminderMongoRepository.findByIdAndUserId(id, userId).map(ReminderEntity::toModel);
    }

    @Override
    public List<Reminder> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime) {
        return reminderMongoRepository.findByStatusAndScheduledAtLessThanEqual(status, dateTime).stream()
                .map(ReminderEntity::toModel).toList();
    }

    @Override
    public long countByUserIdAndStatus(String userId, ReminderStatus status) {
        return reminderMongoRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public long countByUserId(String userId) {
        return reminderMongoRepository.countByUserId(userId);
    }

    @Override
    public List<Reminder> saveAll(List<Reminder> pendentes) {
        return reminderMongoRepository.saveAll(
                pendentes.stream().map(ReminderEntity::fromModel).toList()
        ).stream().map(ReminderEntity::toModel).toList();
    }

    @Override
    public Reminder save(Reminder next) {
        return reminderMongoRepository.save(ReminderEntity.fromModel(next)).toModel();
    }
}
