package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
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
    public List<ReminderEntity> findByUserIdOrderByScheduledAtAsc(String userId) {
        return reminderMongoRepository.findByUserIdOrderByScheduledAtAsc(userId);
    }

    @Override
    public List<ReminderEntity> findByUserIdAndStatusOrderByScheduledAtAsc(String userId, ReminderStatus status) {
        return reminderMongoRepository.findByUserIdAndStatusOrderByScheduledAtAsc(userId, status);
    }

    @Override
    public Optional<ReminderEntity> findByIdAndUserId(String id, String userId) {
        return reminderMongoRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<ReminderEntity> findByStatusAndScheduledAtLessThanEqual(ReminderStatus status, LocalDateTime dateTime) {
        return reminderMongoRepository.findByStatusAndScheduledAtLessThanEqual(status, dateTime);
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
    public List<ReminderEntity> saveAll(List<ReminderEntity> pendentes) {
        return reminderMongoRepository.saveAll(pendentes);
    }

    @Override
    public ReminderEntity save(ReminderEntity next) {
        return reminderMongoRepository.save(next);
    }
}
