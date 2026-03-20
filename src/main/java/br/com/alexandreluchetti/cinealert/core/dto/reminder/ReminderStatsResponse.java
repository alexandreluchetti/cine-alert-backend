package br.com.alexandreluchetti.cinealert.core.dto.reminder;

public record ReminderStatsResponse(
    long total,
    long pending,
    long sent,
    long cancelled
) {}
