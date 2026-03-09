package com.cinealert.service;

import com.cinealert.dto.content.ContentResponse;
import com.cinealert.dto.reminder.*;
import com.cinealert.exception.AppException;
import com.cinealert.model.Content;
import com.cinealert.model.Reminder;
import com.cinealert.model.User;
import com.cinealert.model.enums.Recurrence;
import com.cinealert.model.enums.ReminderStatus;
import com.cinealert.repository.ContentRepository;
import com.cinealert.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ContentRepository contentRepository;

    public List<ReminderResponse> getReminders(User user, ReminderStatus status) {
        List<Reminder> reminders = status != null
                ? reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc(user.getId(), status)
                : reminderRepository.findByUserIdOrderByScheduledAtAsc(user.getId());

        return reminders.stream().map(this::toResponse).toList();
    }

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

    public ReminderResponse getById(User user, Long id) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));
        return toResponse(reminder);
    }

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
