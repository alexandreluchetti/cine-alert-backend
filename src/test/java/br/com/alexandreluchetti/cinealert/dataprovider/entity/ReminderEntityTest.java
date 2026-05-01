package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderEntityTest {

    private static final String ZONE = "America/Sao_Paulo";

    @Test
    void fromModel_toModel_roundTrip() {
        LocalDateTime now = LocalDateTime.now();
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Title", ContentType.MOVIE, "url", 2020);
        Reminder model = new Reminder("r-1", "u-1", "fcm", "c-1", snapshot,
                now.plusDays(1), ZONE, Recurrence.DAILY, "Msg", ReminderStatus.SENT, now);

        ReminderEntity entity = ReminderEntity.fromModel(model);

        assertThat(entity.getId()).isEqualTo("r-1");
        assertThat(entity.getZoneId()).isEqualTo(ZONE);
        assertThat(entity.getRecurrence()).isEqualTo(Recurrence.DAILY);

        Reminder backToModel = entity.toModel();

        assertThat(backToModel.getId()).isEqualTo("r-1");
        assertThat(backToModel.getZoneId()).isEqualTo(ZONE);
        assertThat(backToModel.getRecurrence()).isEqualTo(Recurrence.DAILY);
        assertThat(backToModel.getStatus()).isEqualTo(ReminderStatus.SENT);
        assertThat(backToModel.getContentSnapshot().getImdbId()).isEqualTo("tt123");
    }

    @Test
    void recurrenceOneStatusPending_overridesValues() {
        LocalDateTime now = LocalDateTime.now();
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Title", ContentType.MOVIE, "url", 2020);
        Reminder model = new Reminder("r-1", "u-1", "fcm", "c-1", snapshot,
                now.plusDays(1), ZONE, Recurrence.DAILY, "Msg", ReminderStatus.SENT, now);

        ReminderEntity entity = ReminderEntity.recurrenceOneStatusPending(model);

        assertThat(entity.getRecurrence()).isEqualTo(Recurrence.ONCE);
        assertThat(entity.getStatus()).isEqualTo(ReminderStatus.PENDING);
        assertThat(entity.getZoneId()).isEqualTo(ZONE);
    }

    @Test
    void fromModel_nullZoneId_preservedAsNull() {
        LocalDateTime now = LocalDateTime.now();
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Title", ContentType.MOVIE, "url", 2020);
        Reminder model = new Reminder("r-1", "u-1", "fcm", "c-1", snapshot,
                now.plusDays(1), null, Recurrence.ONCE, "Msg", ReminderStatus.PENDING, now);

        ReminderEntity entity = ReminderEntity.fromModel(model);
        Reminder backToModel = entity.toModel();

        assertThat(entity.getZoneId()).isNull();
        assertThat(backToModel.getZoneId()).isNull();
    }
}
