package br.com.alexandreluchetti.cinealert.core.usecase.impl;

import br.com.alexandreluchetti.cinealert.core.model.UserEntity;
import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;
import br.com.alexandreluchetti.cinealert.core.repository.ReminderRepository;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.configuration.exception.AppException;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public UserResponse getProfile(UserEntity userEntity) {
        long total = reminderRepository.countByUserId(userEntity.getId());
        long sent = reminderRepository.countByUserIdAndStatus(userEntity.getId(), ReminderStatus.SENT);
        return new UserResponse(userEntity.getId(), userEntity.getName(), userEntity.getEmail(), userEntity.getAvatarUrl(), total, sent);
    }

    @Override
    public UserResponse updateProfile(UserEntity userEntity, UpdateUserRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            userEntity.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(userEntity);
        return getProfile(userEntity);
    }

    @Override
    public UserResponse updateAvatar(UserEntity userEntity, String avatarUrl) {
        userEntity.setAvatarUrl(avatarUrl);
        userRepository.save(userEntity);
        return getProfile(userEntity);
    }

    @Override
    public void updateFcmToken(UserEntity userEntity, String fcmToken) {
        userEntity.setFcmToken(fcmToken);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteAccount(UserEntity userEntity) {
        userEntity.setActive(false);
        userRepository.save(userEntity);
    }

    @Override
    public UserEntity getAuthenticatedUser(org.springframework.security.core.Authentication authentication) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String email = userEntity.getEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }
}
