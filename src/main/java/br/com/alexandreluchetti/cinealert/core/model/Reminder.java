package br.com.alexandreluchetti.cinealert.core.model;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "reminders")
@CompoundIndex(name = "idx_scheduled", def = "{'scheduled_at': 1, 'status': 1}")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reminder {

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
    private ContentSnapshot contentSnapshot;

    @Field("scheduled_at")
    private LocalDateTime scheduledAt;

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

    // ──────────────────────────────────────────────────────────────────────
    // Embedded content snapshot (avoids extra DB lookups in the scheduler)
    // ──────────────────────────────────────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ContentSnapshot {
        private String imdbId;
        private String title;
        private ContentType type;
        private String posterUrl;
        private Integer year;
    }
}
