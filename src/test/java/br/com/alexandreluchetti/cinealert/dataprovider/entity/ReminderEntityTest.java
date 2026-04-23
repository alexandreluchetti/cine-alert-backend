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

    @Test
    void fromModel_toModel_roundTrip() {
        LocalDateTime now = LocalDateTime.now();
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Title", ContentType.MOVIE, "url", 2020);
        Reminder model = new Reminder("r-1", "u-1", "fcm", "c-1", snapshot,
                now.plusDays(1), Recurrence.DAILY, "Msg", ReminderStatus.SENT, now);

        ReminderEntity entity = ReminderEntity.fromModel(model);

        assertThat(entity.getId()).isEqualTo("r-1");
        assertThat(entity.getRecurrence()).isEqualTo(Recurrence.DAILY);

        Reminder backToModel = entity.toModel();

        assertThat(backToModel.getId()).isEqualTo("r-1");
        assertThat(backToModel.getRecurrence()).isEqualTo(Recurrence.DAILY);
        assertThat(backToModel.getStatus()).isEqualTo(ReminderStatus.SENT);
        assertThat(backToModel.getContentSnapshot().getImdbId()).isEqualTo("tt123");
    }

    @Test
    void recurrenceOneStatusPending_overridesValues() {
        LocalDateTime now = LocalDateTime.now();
        ContentSnapshot snapshot = new ContentSnapshot("tt123", "Title", ContentType.MOVIE, "url", 2020);
        Reminder model = new Reminder("r-1", "u-1", "fcm", "c-1", snapshot,
                now.plusDays(1), Recurrence.DAILY, "Msg", ReminderStatus.SENT, now);

        ReminderEntity entity = ReminderEntity.recurrenceOneStatusPending(model);

        assertThat(entity.getRecurrence()).isEqualTo(Recurrence.ONCE);
        assertThat(entity.getStatus()).isEqualTo(ReminderStatus.PENDING);
    }
}
