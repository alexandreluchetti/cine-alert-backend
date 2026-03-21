package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponseDto;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;

public record ReminderResponseDto (
    Long id,
    ContentResponseDto content,
    LocalDateTime scheduledAt,
    Recurrence recurrence,
    String message,
    ReminderStatus status,
    LocalDateTime createdAt
) {

    public static ReminderResponseDto fromModel(ReminderResponse reminderResponse) {
        return new ReminderResponseDto(
                reminderResponse.getId(),
                ContentResponseDto.fromModel(reminderResponse.getContent()),
                reminderResponse.getScheduleAt(),
                reminderResponse.getRecurrence(),
                reminderResponse.getMessage(),
                reminderResponse.getStatus(),
                reminderResponse.getCreatedAt()
        );
    }
}
