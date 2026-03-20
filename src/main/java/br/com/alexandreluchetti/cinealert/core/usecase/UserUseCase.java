package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.dto.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.dto.user.UserResponse;
import br.com.alexandreluchetti.cinealert.model.User;

public interface UserUseCase {

    UserResponse getProfile(User user);

    UserResponse updateProfile(User user, UpdateUserRequest request);

    UserResponse updateAvatar(User user, String avatarUrl);

    void updateFcmToken(User user, String fcmToken);

    void deleteAccount(User user);

    User getAuthenticatedUser(org.springframework.security.core.Authentication authentication);
}
