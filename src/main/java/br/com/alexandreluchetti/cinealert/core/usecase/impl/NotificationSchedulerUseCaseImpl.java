package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.usecase.FcmUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.NotificationSchedulerUseCase;
import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.repository.ReminderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @Scheduled(fixedDelay = 60000) // every 60 seconds
    public void processarLembretesPendentes() {
        List<Reminder> pendentes = reminderRepository
                .findByStatusAndScheduledAtLessThanEqual(ReminderStatus.PENDING, LocalDateTime.now());

        if (pendentes.isEmpty())
            return;

        log.info("Processing {} pending reminder(s)...", pendentes.size());

        for (Reminder reminder : pendentes) {
            try {
                String title = "🎬 " + reminder.getContent().getTitle();
                String body = reminder.getMessage() != null && !reminder.getMessage().isBlank()
                        ? reminder.getMessage()
                        : "Hora do seu lembrete de cinema!";

                fcmUseCase.sendNotification(
                        reminder.getUser().getFcmToken(),
                        title,
                        body);

                reminder.setStatus(ReminderStatus.SENT);

                if (reminder.getRecurrence() != Recurrence.ONCE) {
                    scheduleNext(reminder);
                }
            } catch (Exception e) {
                log.error("Error processing reminder id={}: {}", reminder.getId(), e.getMessage());
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

        Reminder next = Reminder.builder()
                .user(reminder.getUser())
                .content(reminder.getContent())
                .scheduledAt(nextTime)
                .recurrence(reminder.getRecurrence())
                .message(reminder.getMessage())
                .status(ReminderStatus.PENDING)
                .build();

        reminderRepository.save(next);
        log.info("Scheduled next reminder for '{}' at {}", reminder.getContent().getTitle(), nextTime);
    }
}
