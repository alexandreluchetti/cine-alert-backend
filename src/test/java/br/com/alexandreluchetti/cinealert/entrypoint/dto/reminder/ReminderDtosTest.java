package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderDtosTest {

    private static final ZoneId ZONE_BRT = ZoneId.of("America/Sao_Paulo");

    // ─────────────────────── ReminderRequestDto ───────────────────────

    @Test
    void reminderRequestDto_toModel_convertsToUtcAndCapturesZone() {
        ZonedDateTime localTime = ZonedDateTime.of(2025, 12, 1, 17, 0, 0, 0, ZONE_BRT);
        ReminderRequestDto dto = new ReminderRequestDto("c-1", localTime, Recurrence.ONCE, "Msg");

        ReminderRequest model = dto.toModel();

        assertThat(model.getContentId()).isEqualTo("c-1");
        assertThat(model.getZoneId()).isEqualTo("America/Sao_Paulo");
        // 17:00 BRT (UTC-3) → 20:00 UTC
        assertThat(model.getScheduledAt()).isEqualTo(LocalDateTime.of(2025, 12, 1, 20, 0, 0));
    }

    @Test
    void reminderRequestDto_toModel_withOffsetZone_capturesOffsetId() {
        ZonedDateTime withOffset = ZonedDateTime.of(2025, 12, 1, 17, 0, 0, 0, ZoneOffset.of("-05:00"));
        ReminderRequestDto dto = new ReminderRequestDto("c-1", withOffset, Recurrence.DAILY, null);

        ReminderRequest model = dto.toModel();

        assertThat(model.getZoneId()).isEqualTo("-05:00");
        assertThat(model.getScheduledAt()).isEqualTo(LocalDateTime.of(2025, 12, 1, 22, 0, 0));
    }

    // ─────────────────────── ReminderResponseDto ───────────────────────

    @Test
    void reminderResponseDto_fromModel_convertsUtcToStoredZone() {
        LocalDateTime utcScheduled = LocalDateTime.of(2025, 12, 1, 20, 0, 0);
        LocalDateTime utcCreated   = LocalDateTime.of(2025, 12, 1, 10, 0, 0);
        ContentResponse content = new ContentResponse("1", "tt1", "T", ContentType.MOVIE, "u", 2020,
                BigDecimal.ZERO, null, "s", "t", 10);
        ReminderResponse model = new ReminderResponse(
                "r-1", content, utcScheduled, "America/Sao_Paulo",
                Recurrence.ONCE, "Msg", ReminderStatus.PENDING, utcCreated);

        ReminderResponseDto dto = ReminderResponseDto.fromModel(model);

        assertThat(dto.id()).isEqualTo("r-1");
        // 20:00 UTC → 17:00 BRT (UTC-3)
        assertThat(dto.scheduledAt().getHour()).isEqualTo(17);
        assertThat(dto.scheduledAt().getZone()).isEqualTo(ZONE_BRT);
        // 10:00 UTC → 07:00 BRT
        assertThat(dto.createdAt().getHour()).isEqualTo(7);
    }

    @Test
    void reminderResponseDto_fromModel_nullZoneId_fallsBackToUtc() {
        LocalDateTime utcScheduled = LocalDateTime.of(2025, 12, 1, 20, 0, 0);
        ContentResponse content = new ContentResponse("1", "tt1", "T", ContentType.MOVIE, "u", 2020,
                BigDecimal.ZERO, null, "s", "t", 10);
        ReminderResponse model = new ReminderResponse(
                "r-1", content, utcScheduled, null,
                Recurrence.ONCE, "Msg", ReminderStatus.PENDING, null);

        ReminderResponseDto dto = ReminderResponseDto.fromModel(model);

        assertThat(dto.scheduledAt().getZone()).isEqualTo(ZoneOffset.UTC);
        assertThat(dto.scheduledAt().getHour()).isEqualTo(20);
        assertThat(dto.createdAt()).isNull();
    }

    @Test
    void reminderResponseDto_fromModel_invalidZoneId_fallsBackToUtc() {
        LocalDateTime utcScheduled = LocalDateTime.of(2025, 12, 1, 20, 0, 0);
        ContentResponse content = new ContentResponse("1", "tt1", "T", ContentType.MOVIE, "u", 2020,
                BigDecimal.ZERO, null, "s", "t", 10);
        ReminderResponse model = new ReminderResponse(
                "r-1", content, utcScheduled, "Not/AZone",
                Recurrence.ONCE, "Msg", ReminderStatus.PENDING, null);

        ReminderResponseDto dto = ReminderResponseDto.fromModel(model);

        assertThat(dto.scheduledAt().getZone()).isEqualTo(ZoneOffset.UTC);
    }

    // ─────────────────────── ReminderStatsResponseDto ───────────────────────

    @Test
    void reminderStatsResponseDto_fromModel() {
        ReminderStatsResponse model = new ReminderStatsResponse(10, 5, 2, 3);
        ReminderStatsResponseDto dto = ReminderStatsResponseDto.fromModel(model);
        assertThat(dto.total()).isEqualTo(10);
        assertThat(dto.pending()).isEqualTo(5);
        assertThat(dto.sent()).isEqualTo(2);
        assertThat(dto.cancelled()).isEqualTo(3);
    }
}
