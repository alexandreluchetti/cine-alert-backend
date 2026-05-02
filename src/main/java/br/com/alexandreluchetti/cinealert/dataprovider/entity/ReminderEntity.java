package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reminders")
@CompoundIndex(name = "idx_scheduled", def = "{'scheduled_at': 1, 'status': 1}")
public class ReminderEntity {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    /** Denormalized: FCM token at reminder-creation time, for the scheduler. */
    @Field("user_fcm_token")
    private String userFcmToken;

    @Field("content_id")
    private String contentId;

    /** Denormalized snapshot of content data needed for notifications. */
    @Field("content_snapshot")
    private ContentSnapshotEntity contentSnapshot;

    @Field("scheduled_at")
    private LocalDateTime scheduledAt;

    @Field("zone_id")
    private String zoneId;

    @Builder.Default
    @Field("recurrence")
    private Recurrence recurrence = Recurrence.ONCE;

    @Field("message")
    private String message;

    @Builder.Default
    @Field("status")
    private ReminderStatus status = ReminderStatus.PENDING;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    public static ReminderEntity fromModel(Reminder reminder) {
        return new ReminderEntity(
                reminder.getId(),
                reminder.getUserId(),
                reminder.getUserFcmToken(),
                reminder.getContentId(),
                ContentSnapshotEntity.fromModel(reminder.getContentSnapshot()),
                reminder.getScheduledAt(),
                reminder.getZoneId(),
                reminder.getRecurrence(),
                reminder.getMessage(),
                reminder.getStatus(),
                reminder.getCreatedAt()
        );
    }

    public static ReminderEntity recurrenceOneStatusPending(Reminder reminder) {
        return new ReminderEntity(
                reminder.getId(),
                reminder.getUserId(),
                reminder.getUserFcmToken(),
                reminder.getContentId(),
                ContentSnapshotEntity.fromModel(reminder.getContentSnapshot()),
                reminder.getScheduledAt(),
                reminder.getZoneId(),
                Recurrence.ONCE,
                reminder.getMessage(),
                ReminderStatus.PENDING,
                reminder.getCreatedAt()
        );
    }

    public Reminder toModel() {
        return new Reminder(
                id,
                userId,
                userFcmToken,
                contentId,
                contentSnapshot.toModel(),
                scheduledAt,
                zoneId,
                recurrence,
                message,
                status,
                createdAt
        );
    }
}
