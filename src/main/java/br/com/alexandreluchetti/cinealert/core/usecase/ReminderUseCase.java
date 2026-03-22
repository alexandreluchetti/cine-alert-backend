package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.UserEntity;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderRequest;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;

import java.util.List;

public interface ReminderUseCase {

    List<ReminderResponse> getReminders(UserEntity userEntity, ReminderStatus status);

    ReminderResponse create(UserEntity userEntity, ReminderRequest request);

    ReminderResponse getById(UserEntity userEntity, String id);

    ReminderResponse update(UserEntity userEntity, String id, ReminderRequest request);

    void cancel(UserEntity userEntity, String id);

    ReminderStatsResponse getStats(UserEntity userEntity);
}
