package br.com.alexandreluchetti.cinealert.core.model.reminder;

import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;

import java.time.LocalDateTime;

public class ReminderRequest {

    private String contentId;
    private LocalDateTime scheduledAt;
    private Recurrence recurrence;
    private String message;

    public ReminderRequest(String contentId, LocalDateTime scheduledAt, Recurrence recurrence, String message) {
        this.contentId = contentId;
        this.scheduledAt = scheduledAt;
        this.recurrence = recurrence;
        this.message = message;
    }

    public String getContentId() {
        return contentId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public String getMessage() {
        return message;
    }
}
