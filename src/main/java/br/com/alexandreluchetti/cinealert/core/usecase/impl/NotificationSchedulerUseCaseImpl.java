package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.ReminderEntity;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.FcmUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.NotificationSchedulerUseCase;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class NotificationSchedulerUseCaseImpl implements NotificationSchedulerUseCase {

    private final ReminderRepository reminderRepository;
    private final FcmUseCase fcmUseCase;

    public NotificationSchedulerUseCaseImpl(ReminderRepository reminderRepository, FcmUseCase fcmUseCase) {
        this.reminderRepository = reminderRepository;
        this.fcmUseCase = fcmUseCase;
    }

    @Override
    @Scheduled(fixedDelay = 60000) // every 60 seconds
    public void processarLembretesPendentes() {
        List<ReminderEntity> pendentes = reminderRepository
                .findByStatusAndScheduledAtLessThanEqual(ReminderStatus.PENDING, LocalDateTime.now());

        if (pendentes.isEmpty())
            return;

        log.info("Processing {} pending reminder(s)...", pendentes.size());

        for (ReminderEntity reminderEntity : pendentes) {
            try {
                ReminderEntity.ContentSnapshot snap = reminderEntity.getContentSnapshot();
                String title = "🎬 " + (snap != null ? snap.getTitle() : "");
                String body = reminderEntity.getMessage() != null && !reminderEntity.getMessage().isBlank()
                        ? reminderEntity.getMessage()
                        : "Hora do seu lembrete de cinema!";

                fcmUseCase.sendNotification(reminderEntity.getUserFcmToken(), title, body);

                reminderEntity.setStatus(ReminderStatus.SENT);

                if (reminderEntity.getRecurrence() != Recurrence.ONCE) {
                    scheduleNext(reminderEntity);
                }
            } catch (Exception e) {
                log.error("Error processing reminder id={}: {}", reminderEntity.getId(), e.getMessage());
            }
        }

        reminderRepository.saveAll(pendentes);
    }

    private void scheduleNext(ReminderEntity reminderEntity) {
        LocalDateTime nextTime = switch (reminderEntity.getRecurrence()) {
            case DAILY -> reminderEntity.getScheduledAt().plusDays(1);
            case WEEKLY -> reminderEntity.getScheduledAt().plusWeeks(1);
            default -> null;
        };

        if (nextTime == null)
            return;

        ReminderEntity next = ReminderEntity.builder()
                .userId(reminderEntity.getUserId())
                .userFcmToken(reminderEntity.getUserFcmToken())
                .contentId(reminderEntity.getContentId())
                .contentSnapshot(reminderEntity.getContentSnapshot())
                .scheduledAt(nextTime)
                .recurrence(reminderEntity.getRecurrence())
                .message(reminderEntity.getMessage())
                .status(ReminderStatus.PENDING)
                .build();

        reminderRepository.save(next);
        ReminderEntity.ContentSnapshot snap = reminderEntity.getContentSnapshot();
        log.info("Scheduled next reminder for '{}' at {}", snap != null ? snap.getTitle() : "", nextTime);
    }
}
