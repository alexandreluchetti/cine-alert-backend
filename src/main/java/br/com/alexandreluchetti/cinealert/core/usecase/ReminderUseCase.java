package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.util.List;

public interface ReminderUseCase {

    List<ReminderResponse> getReminders(User user, ReminderStatus status);

    ReminderResponse create(User user, ReminderRequest request);

    ReminderResponse getById(User user, String id);

    ReminderResponse update(User user, String id, ReminderRequest request);

    void cancel(User user, String id);

    ReminderStatsResponse getStats(User user);
}
