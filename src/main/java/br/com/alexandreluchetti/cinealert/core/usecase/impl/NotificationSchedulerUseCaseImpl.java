package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.FcmUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.NotificationSchedulerUseCase;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationSchedulerUseCaseImpl implements NotificationSchedulerUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSchedulerUseCaseImpl.class);

    private final ReminderRepository reminderRepository;
    private final FcmUseCase fcmUseCase;

    public NotificationSchedulerUseCaseImpl(ReminderRepository reminderRepository, FcmUseCase fcmUseCase) {
        this.reminderRepository = reminderRepository;
        this.fcmUseCase = fcmUseCase;
    }

    @Override
    @Scheduled(fixedDelay = 60000) // every 60 seconds
    public void processarLembretesPendentes() {
        List<Reminder> pendentes = reminderRepository
                .findByStatusAndScheduledAtLessThanEqual(ReminderStatus.PENDING, LocalDateTime.now());

        if (pendentes.isEmpty())
            return;

        LOGGER.info("Processing {} pending reminder(s)...", pendentes.size());

        for (Reminder reminder : pendentes) {
            try {
                ContentSnapshot snap = reminder.getContentSnapshot();
                String title = "🎬 " + (snap != null ? snap.getTitle() : "");
                String body = reminder.getMessage() != null && !reminder.getMessage().isBlank()
                        ? reminder.getMessage()
                        : "Hora do seu lembrete de cinema!";

                fcmUseCase.sendNotification(reminder.getUserFcmToken(), title, body);

                reminder.setStatus(ReminderStatus.SENT);

                if (reminder.getRecurrence() != Recurrence.ONCE) {
                    scheduleNext(reminder);
                }
            } catch (Exception e) {
                LOGGER.error("Error processing reminder id={}: {}", reminder.getId(), e.getMessage());
            }
        }

        reminderRepository.saveAll(pendentes);
    }

    private void scheduleNext(Reminder reminder) {
        LocalDateTime nextTime = switch (reminder.getRecurrence()) {
            case DAILY -> reminder.getScheduledAt().plusDays(1);
            case WEEKLY -> reminder.getScheduledAt().plusWeeks(1);
            default -> null;
        };

        if (nextTime == null)
            return;

        Reminder next = new Reminder(
                null,
                reminder.getUserId(),
                reminder.getUserFcmToken(),
                reminder.getContentId(),
                reminder.getContentSnapshot(),
                nextTime,
                reminder.getRecurrence(),
                reminder.getMessage(),
                ReminderStatus.PENDING,
                null
        );

        reminderRepository.save(next);
        ContentSnapshot snap = reminder.getContentSnapshot();
        LOGGER.info("Scheduled next reminder for '{}' at {}", snap != null ? snap.getTitle() : "", nextTime);
    }
}
