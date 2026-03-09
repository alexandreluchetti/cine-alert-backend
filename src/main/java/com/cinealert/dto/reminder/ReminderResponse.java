package com.cinealert.dto.reminder;

import com.cinealert.dto.content.ContentResponse;
import com.cinealert.model.enums.Recurrence;
import com.cinealert.model.enums.ReminderStatus;

import java.time.LocalDateTime;

public record ReminderResponse(
    Long id,
    ContentResponse content,
    LocalDateTime scheduledAt,
    Recurrence recurrence,
    String message,
    ReminderStatus status,
    LocalDateTime createdAt
) {}
