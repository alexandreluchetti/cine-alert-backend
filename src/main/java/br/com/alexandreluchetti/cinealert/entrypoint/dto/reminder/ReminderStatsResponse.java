package br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder;

public record ReminderStatsResponse(
    long total,
    long pending,
    long sent,
    long cancelled
) {}
