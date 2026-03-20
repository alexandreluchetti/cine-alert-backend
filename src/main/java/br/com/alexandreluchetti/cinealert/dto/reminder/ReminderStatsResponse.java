package br.com.alexandreluchetti.cinealert.dto.reminder;

public record ReminderStatsResponse(
    long total,
    long pending,
    long sent,
    long cancelled
) {}
