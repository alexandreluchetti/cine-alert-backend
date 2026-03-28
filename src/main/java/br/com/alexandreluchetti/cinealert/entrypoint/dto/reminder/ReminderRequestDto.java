package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ReminderRequestDto (
    @NotNull(message = "Content ID is required")
    String contentId,

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    LocalDateTime scheduledAt,

    Recurrence recurrence,

    @Size(max = 255, message = "Message must be at most 255 characters")
    String message
) {

    public ReminderRequest toModel() {
        return new ReminderRequest(contentId, scheduledAt, recurrence, message);
    }
}
