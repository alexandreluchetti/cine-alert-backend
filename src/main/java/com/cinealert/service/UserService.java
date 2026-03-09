package com.cinealert.service;

import com.cinealert.dto.user.*;
import com.cinealert.exception.AppException;
import com.cinealert.model.User;
import com.cinealert.model.enums.ReminderStatus;
import com.cinealert.repository.ReminderRepository;
import com.cinealert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(User user) {
        long total = reminderRepository.countByUserId(user.getId());
        long sent = reminderRepository.countByUserIdAndStatus(user.getId(), ReminderStatus.SENT);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl(), total, sent);
    }

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

    @Transactional
    public UserResponse updateAvatar(User user, String avatarUrl) {
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return getProfile(user);
    }

    @Transactional
    public void updateFcmToken(User user, String fcmToken) {
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(User user) {
        user.setActive(false);
        userRepository.save(user);
    }

    public User getAuthenticatedUser(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }
}
