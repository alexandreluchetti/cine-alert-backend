package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;

public record ReminderStatsResponseDto (
    long total,
    long pending,
    long sent,
    long cancelled
) {

    public static ReminderStatsResponseDto fromModel(ReminderStatsResponse model) {
        return new ReminderStatsResponseDto(
                model.getTotal(),
                model.getPending(),
                model.getSent(),
                model.getCancelled()
        );
    }
}
