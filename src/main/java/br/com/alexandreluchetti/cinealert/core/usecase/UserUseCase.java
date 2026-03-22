package br.com.alexandreluchetti.cinealert.core.usecase;

import br.com.alexandreluchetti.cinealert.core.model.UserEntity;
import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;

public interface UserUseCase {

    UserResponse getProfile(UserEntity userEntity);

    UserResponse updateProfile(UserEntity userEntity, UpdateUserRequest request);

    UserResponse updateAvatar(UserEntity userEntity, String avatarUrl);

    void updateFcmToken(UserEntity userEntity, String fcmToken);

    void deleteAccount(UserEntity userEntity);

    UserEntity getAuthenticatedUser(org.springframework.security.core.Authentication authentication);
}
