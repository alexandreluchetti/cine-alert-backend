package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.core.dto.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.dto.user.UserResponse;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;
import br.com.alexandreluchetti.cinealert.model.User;
import br.com.alexandreluchetti.cinealert.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class UserUseCaseImpl implements UserUseCase {

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
        long total = reminderRepository.countByUserId(user.getId());
        long sent = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.SENT);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl(), total, sent);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(User user, UpdateUserRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        userRepository.save(user);
        return getProfile(user);
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(User user, String avatarUrl) {
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return getProfile(user);
    }

    @Override
    @Transactional
    public void updateFcmToken(User user, String fcmToken) {
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAccount(User user) {
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public User getAuthenticatedUser(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }
}
