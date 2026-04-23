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

import static org.assertj.core.api.Assertions.assertThat;

class ReminderDtosTest {

    @Test
    void reminderRequestDto_toModel() {
        ReminderRequestDto dto = new ReminderRequestDto("c-1", LocalDateTime.now(), Recurrence.ONCE, "Msg");
        ReminderRequest model = dto.toModel();
        assertThat(model.getContentId()).isEqualTo("c-1");
    }

    @Test
    void reminderResponseDto_fromModel() {
        ContentResponse content = new ContentResponse("1", "tt1", "T", ContentType.MOVIE, "u", 20, BigDecimal.ZERO, null, "s", "t", 10);
        ReminderResponse model = new ReminderResponse("r-1", content, LocalDateTime.now(), Recurrence.ONCE, "Msg", ReminderStatus.PENDING, LocalDateTime.now());

        ReminderResponseDto dto = ReminderResponseDto.fromModel(model);
        assertThat(dto.id()).isEqualTo("r-1");
    }

    @Test
    void reminderStatsResponseDto_fromModel() {
        ReminderStatsResponse model = new ReminderStatsResponse(10, 5, 2, 3);
        ReminderStatsResponseDto dto = ReminderStatsResponseDto.fromModel(model);
        assertThat(dto.total()).isEqualTo(10);
    }
}
