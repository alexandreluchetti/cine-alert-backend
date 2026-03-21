package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.Content;
import br.com.alexandreluchetti.cinealert.core.model.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.ReminderUseCase;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;

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
    public ReminderResponse create(User user, ReminderRequest request) {
        Content content = contentRepository.findById(request.getContentId())
                .orElseThrow(() -> AppException.notFound("Content not found"));

        Reminder.ContentSnapshot snapshot = Reminder.ContentSnapshot.builder()
                .imdbId(content.getImdbId())
                .title(content.getTitle())
                .type(content.getType())
                .posterUrl(content.getPosterUrl())
                .year(content.getYear())
                .build();

        Reminder reminder = Reminder.builder()
                .userId(user.getId())
                .userFcmToken(user.getFcmToken())
                .contentId(content.getId())
                .contentSnapshot(snapshot)
                .scheduledAt(request.getScheduledAt())
                .recurrence(request.getRecurrence() != null ? request.getRecurrence() : Recurrence.ONCE)
                .message(request.getMessage())
                .status(ReminderStatus.PENDING)
                .build();

        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    public ReminderResponse getById(User user, String id) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));
        return toResponse(reminder);
    }

    @Override
    public ReminderResponse update(User user, String id, ReminderRequest request) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));

        if (reminder.getStatus() == ReminderStatus.SENT) {
            throw AppException.badRequest("Cannot update an already sent reminder");
        }

        if (request.getScheduledAt() != null)
            reminder.setScheduledAt(request.getScheduledAt());
        if (request.getRecurrence() != null)
            reminder.setRecurrence(request.getRecurrence());
        if (request.getMessage() != null)
            reminder.setMessage(request.getMessage());

        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    public void cancel(User user, String id) {
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
        Reminder.ContentSnapshot snap = r.getContentSnapshot();
        ContentResponse contentResp = new ContentResponse(
                r.getContentId(), snap != null ? snap.getImdbId() : null,
                snap != null ? snap.getTitle() : null,
                snap != null ? snap.getType() : null,
                snap != null ? snap.getPosterUrl() : null,
                snap != null ? snap.getYear() : null,
                null, java.util.Collections.emptyList(), null, null, null);
        return new ReminderResponse(
                r.getId(), contentResp, r.getScheduledAt(),
                r.getRecurrence(), r.getMessage(), r.getStatus(), r.getCreatedAt());
    }
}
