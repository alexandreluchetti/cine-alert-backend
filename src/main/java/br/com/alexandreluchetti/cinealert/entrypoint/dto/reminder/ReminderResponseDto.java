package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponseDto;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public record ReminderResponseDto (
    String id,
    ContentResponseDto content,
    ZonedDateTime scheduledAt,
    Recurrence recurrence,
    String message,
    ReminderStatus status,
    ZonedDateTime createdAt
) {

    public static ReminderResponseDto fromModel(ReminderResponse reminderResponse) {
        ZoneId zone = resolveZone(reminderResponse.getZoneId());
        return new ReminderResponseDto(
                reminderResponse.getId(),
                ContentResponseDto.fromModel(reminderResponse.getContent()),
                toZoned(reminderResponse.getScheduleAt(), zone),
                reminderResponse.getRecurrence(),
                reminderResponse.getMessage(),
                reminderResponse.getStatus(),
                toZoned(reminderResponse.getCreatedAt(), zone)
        );
    }

    private static ZoneId resolveZone(String storedZoneId) {
        if (storedZoneId != null && !storedZoneId.isBlank()) {
            try {
                return ZoneId.of(storedZoneId);
            } catch (Exception ignored) {}
        }
        return ZoneOffset.UTC;
    }

    private static ZonedDateTime toZoned(LocalDateTime utc, ZoneId targetZone) {
        if (utc == null) return null;
        return utc.atZone(ZoneOffset.UTC).withZoneSameInstant(targetZone);
    }
}
