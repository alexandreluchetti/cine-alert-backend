package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.ContentEntity;
import br.com.alexandreluchetti.cinealert.core.model.ReminderEntity;
import br.com.alexandreluchetti.cinealert.core.model.UserEntity;
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
    public List<ReminderResponse> getReminders(UserEntity userEntity, ReminderStatus status) {
        List<ReminderEntity> reminderEntities = status != null
                ? reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc(userEntity.getId(), status)
                : reminderRepository.findByUserIdOrderByScheduledAtAsc(userEntity.getId());

        return reminderEntities.stream().map(this::toResponse).toList();
    }

    @Override
    public ReminderResponse create(UserEntity userEntity, ReminderRequest request) {
        ContentEntity contentEntity = contentRepository.findById(request.getContentId())
                .orElseThrow(() -> AppException.notFound("Content not found"));

        ReminderEntity.ContentSnapshot snapshot = ReminderEntity.ContentSnapshot.builder()
                .imdbId(contentEntity.getImdbId())
                .title(contentEntity.getTitle())
                .type(contentEntity.getType())
                .posterUrl(contentEntity.getPosterUrl())
                .year(contentEntity.getYear())
                .build();

        ReminderEntity reminderEntity = ReminderEntity.builder()
                .userId(userEntity.getId())
                .userFcmToken(userEntity.getFcmToken())
                .contentId(contentEntity.getId())
                .contentSnapshot(snapshot)
                .scheduledAt(request.getScheduledAt())
                .recurrence(request.getRecurrence() != null ? request.getRecurrence() : Recurrence.ONCE)
                .message(request.getMessage())
                .status(ReminderStatus.PENDING)
                .build();

        return toResponse(reminderRepository.save(reminderEntity));
    }

    @Override
    public ReminderResponse getById(UserEntity userEntity, String id) {
        ReminderEntity reminderEntity = reminderRepository.findByIdAndUserId(id, userEntity.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));
        return toResponse(reminderEntity);
    }

    @Override
    public ReminderResponse update(UserEntity userEntity, String id, ReminderRequest request) {
        ReminderEntity reminderEntity = reminderRepository.findByIdAndUserId(id, userEntity.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));

        if (reminderEntity.getStatus() == ReminderStatus.SENT) {
            throw AppException.badRequest("Cannot update an already sent reminder");
        }

        if (request.getScheduledAt() != null)
            reminderEntity.setScheduledAt(request.getScheduledAt());
        if (request.getRecurrence() != null)
            reminderEntity.setRecurrence(request.getRecurrence());
        if (request.getMessage() != null)
            reminderEntity.setMessage(request.getMessage());

        return toResponse(reminderRepository.save(reminderEntity));
    }

    @Override
    public void cancel(UserEntity userEntity, String id) {
        ReminderEntity reminderEntity = reminderRepository.findByIdAndUserId(id, userEntity.getId())
                .orElseThrow(() -> AppException.notFound("Reminder not found"));

        if (reminderEntity.getStatus() == ReminderStatus.SENT) {
            throw AppException.badRequest("Cannot cancel an already sent reminder");
        }

        reminderEntity.setStatus(ReminderStatus.CANCELLED);
        reminderRepository.save(reminderEntity);
    }

    @Override
    public ReminderStatsResponse getStats(UserEntity userEntity) {
        long total = reminderRepository.countByUserId(userEntity.getId());
        long pending = reminderRepository.countByUserIdAndStatus(userEntity.getId(), ReminderStatus.PENDING);
        long sent = reminderRepository.countByUserIdAndStatus(userEntity.getId(), ReminderStatus.SENT);
        long cancelled = reminderRepository.countByUserIdAndStatus(userEntity.getId(), ReminderStatus.CANCELLED);
        return new ReminderStatsResponse(total, pending, sent, cancelled);
    }

    private ReminderResponse toResponse(ReminderEntity r) {
        ReminderEntity.ContentSnapshot snap = r.getContentSnapshot();
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
