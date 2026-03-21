package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;

public interface UserUseCase {

    UserResponse getProfile(User user);

    UserResponse updateProfile(User user, UpdateUserRequest request);

    UserResponse updateAvatar(User user, String avatarUrl);

    void updateFcmToken(User user, String fcmToken);

    void deleteAccount(User user);

    User getAuthenticatedUser(org.springframework.security.core.Authentication authentication);
}
