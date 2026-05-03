package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.reminder.Reminder;
import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.core.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class UserUseCaseImpl implements UserUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserUseCaseImpl.class);

    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUseCaseImpl(
            UserRepository userRepository,
            ReminderRepository reminderRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse getProfile(User user) {
        LOGGER.info("Fetching profile for {}", user.getEmail());

        long total = reminderRepository.countByUserId(user.getId());
        long sent = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.SENT);
        LOGGER.info("Total reminders for {} is {}", user.getId(), total);

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl(), total, sent);
    }

    @Override
    public UserResponse updateProfile(User user, UpdateUserRequest request) {
        LOGGER.info("Updating profile for {}", user.getEmail());

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        LOGGER.info("Updated profile for {}", user.getEmail());
        return getProfile(user);
    }

    @Override
    public UserResponse updateAvatar(User user, String avatarUrl) {
        LOGGER.info("Updating avatar for {}", user.getEmail());
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        LOGGER.info("Updated avatar for {}", user.getEmail());
        return getProfile(user);
    }

    @Override
    public void updateFcmToken(User user, String fcmToken) {
        LOGGER.info("Updating FCM token for {}", user.getEmail());
        user.setFcmToken(fcmToken);
        userRepository.save(user);

        updateFcmTokenForPendingReminders(user, fcmToken);
        LOGGER.info("Updated FCM token for {}", user.getEmail());
    }

    private void updateFcmTokenForPendingReminders(User user, String fcmToken) {
        List<Reminder> pending = reminderRepository
                .findByUserIdAndStatusOrderByScheduledAtAsc(user.getId(), ReminderStatus.PENDING);

        if (!pending.isEmpty()) {
            pending.forEach(r -> r.setUserFcmToken(fcmToken));
            reminderRepository.saveAll(pending);
            LOGGER.info("Updated FCM token on {} pending reminder(s) for {}", pending.size(), user.getEmail());
        }
    }

    @Override
    public void deleteAccount(User user) {
        LOGGER.info("Deleting account for {}", user.getEmail());
        user.setActive(false);
        userRepository.save(user);
        LOGGER.info("Deleted account for {}", user.getEmail());
    }

    @Override
    public User getAuthenticatedUser(Authentication authentication) {
        LOGGER.info("Fetching authenticated user for {}", authentication.getPrincipal());
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        User userFound = userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
        LOGGER.info("Fetched authenticated user for {}", email);
        return userFound;
    }
}
