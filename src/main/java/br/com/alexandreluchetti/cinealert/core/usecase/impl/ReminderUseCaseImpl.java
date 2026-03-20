package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.usecase.ReminderUseCase;
import br.com.alexandreluchetti.cinealert.core.dto.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.dto.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.dto.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.dto.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;
import br.com.alexandreluchetti.cinealert.model.Content;
import br.com.alexandreluchetti.cinealert.model.Reminder;
import br.com.alexandreluchetti.cinealert.model.User;
import br.com.alexandreluchetti.cinealert.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.repository.ReminderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ReminderUseCaseImpl implements ReminderUseCase {

    private final ReminderRepository reminderRepository;
    private final ContentRepository contentRepository;

    public ReminderUseCaseImpl(ReminderRepository reminderRepository, ContentRepository contentRepository) {
        this.reminderRepository = reminderRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    public List<ReminderResponse> getReminders(User user, ReminderStatus status) {
        List<Reminder> reminders = status != null
                ? reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc(user.getId(), status)
                : reminderRepository.findByUserIdOrderByScheduledAtAsc(user.getId());

        return reminders.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ReminderResponse create(User user, ReminderRequest request) {
        Content content = contentRepository.findById(request.contentId())
                .orElseThrow(() -> AppException.notFound("Content not found"));

        Reminder reminder = Reminder.builder()
                .user(user)
                .content(content)
                .scheduledAt(request.scheduledAt())
                .recurrence(request.recurrence() != null ? request.recurrence() : Recurrence.ONCE)
                .message(request.message())
                .status(ReminderStatus.PENDING)
                .build();

        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    public ReminderResponse getById(User user, Long id) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));
        return toResponse(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse update(User user, Long id, ReminderRequest request) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));

        if (reminder.getStatus() == ReminderStatus.SENT) {
            throw AppException.badRequest("Cannot update an already sent reminder");
        }

        if (request.scheduledAt() != null)
            reminder.setScheduledAt(request.scheduledAt());
        if (request.recurrence() != null)
            reminder.setRecurrence(request.recurrence());
        if (request.message() != null)
            reminder.setMessage(request.message());

        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public void cancel(User user, Long id) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));

        if (reminder.getStatus() == ReminderStatus.SENT) {
            throw AppException.badRequest("Cannot cancel an already sent reminder");
        }

        reminder.setStatus(ReminderStatus.CANCELLED);
        reminderRepository.save(reminder);
    }

    @Override
    public ReminderStatsResponse getStats(User user) {
        long total = reminderRepository.countByUserId(user.getId());
        long pending = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.PENDING);
        long sent = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.SENT);
        long cancelled = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.CANCELLED);
        return new ReminderStatsResponse(total, pending, sent, cancelled);
    }

    private ReminderResponse toResponse(Reminder r) {
        Content c = r.getContent();
        ContentResponse contentResp = new ContentResponse(
                c.getId(), c.getImdbId(), c.getTitle(), c.getType(),
                c.getPosterUrl(), c.getYear(), c.getRating(),
                c.getGenre(), c.getSynopsis(), c.getTrailerUrl(), c.getRuntimeMinutes());
        return new ReminderResponse(
                r.getId(), contentResp, r.getScheduledAt(),
                r.getRecurrence(), r.getMessage(), r.getStatus(), r.getCreatedAt());
    }
}
