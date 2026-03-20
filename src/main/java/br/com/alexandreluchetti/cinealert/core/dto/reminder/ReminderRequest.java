package br.com.alexandreluchetti.cinealert.core.dto.reminder;

import br.com.alexandreluchetti.cinealert.model.enums.Recurrence;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ReminderRequest(
    @NotNull(message = "Content ID is required")
    Long contentId,

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    LocalDateTime scheduledAt,

    Recurrence recurrence,

    @Size(max = 255, message = "Message must be at most 255 characters")
    String message
) {}
