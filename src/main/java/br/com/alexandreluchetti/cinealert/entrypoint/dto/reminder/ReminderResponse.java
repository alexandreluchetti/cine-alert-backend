package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponseDto;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;

public record ReminderResponse(
    Long id,
    ContentResponseDto content,
    LocalDateTime scheduledAt,
    Recurrence recurrence,
    String message,
    ReminderStatus status,
    LocalDateTime createdAt
) {}
