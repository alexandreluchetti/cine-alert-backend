package br.com.alexandreluchetti.cinealert.core.model;

import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reminder {

    private Long id;

    private User user;

    private Content content;

    private LocalDateTime scheduledAt;

    @Builder.Default
    private Recurrence recurrence = Recurrence.ONCE;

    private String message;

    @Builder.Default
    private ReminderStatus status = ReminderStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
}
