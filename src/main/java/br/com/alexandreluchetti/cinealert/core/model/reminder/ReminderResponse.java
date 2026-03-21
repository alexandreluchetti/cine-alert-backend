package br.com.alexandreluchetti.cinealert.core.model.reminder;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;

public class ReminderResponse {

    private String id;
    private ContentResponse content;
    private LocalDateTime scheduleAt;
    private Recurrence recurrence;
    private String message;
    private ReminderStatus status;
    private LocalDateTime createdAt;

    public ReminderResponse(String id, ContentResponse content, LocalDateTime scheduleAt, Recurrence recurrence, String message, ReminderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.scheduleAt = scheduleAt;
        this.recurrence = recurrence;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public ContentResponse getContent() {
        return content;
    }

    public LocalDateTime getScheduleAt() {
        return scheduleAt;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public String getMessage() {
        return message;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
