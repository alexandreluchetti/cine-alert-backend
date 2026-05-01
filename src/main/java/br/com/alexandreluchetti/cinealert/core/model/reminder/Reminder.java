package br.com.alexandreluchetti.cinealert.core.model.reminder;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;

public class Reminder {

    private String id;
    private String userId;
    private String userFcmToken;
    private String contentId;
    private ContentSnapshot contentSnapshot;
    private LocalDateTime scheduledAt;
    private String zoneId;
    private Recurrence recurrence;
    private String message;
    private ReminderStatus status;
    private LocalDateTime createdAt;

    public Reminder(String id, String userId, String userFcmToken, String contentId, ContentSnapshot contentSnapshot, LocalDateTime scheduledAt, String zoneId, Recurrence recurrence, String message, ReminderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userFcmToken = userFcmToken;
        this.contentId = contentId;
        this.contentSnapshot = contentSnapshot;
        this.scheduledAt = scheduledAt;
        this.zoneId = zoneId;
        this.recurrence = recurrence;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Reminder(String id, String userId, String userFcmToken, String contentId, ContentSnapshot contentSnapshot, LocalDateTime scheduledAt, String zoneId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userFcmToken = userFcmToken;
        this.contentId = contentId;
        this.contentSnapshot = contentSnapshot;
        this.scheduledAt = scheduledAt;
        this.zoneId = zoneId;
        this.recurrence = Recurrence.ONCE;
        this.message = message;
        this.status = ReminderStatus.PENDING;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFcmToken() {
        return userFcmToken;
    }

    public void setUserFcmToken(String userFcmToken) {
        this.userFcmToken = userFcmToken;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public ContentSnapshot getContentSnapshot() {
        return contentSnapshot;
    }

    public void setContentSnapshot(ContentSnapshot contentSnapshot) {
        this.contentSnapshot = contentSnapshot;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
