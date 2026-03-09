package com.cinealert.dto.reminder;

public record ReminderStatsResponse(
    long total,
    long pending,
    long sent,
    long cancelled
) {}
