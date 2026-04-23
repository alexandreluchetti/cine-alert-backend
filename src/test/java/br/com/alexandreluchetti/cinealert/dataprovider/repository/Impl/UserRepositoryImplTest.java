package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.UserEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.UserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserMongoRepository userMongoRepository;

    private UserRepositoryImpl userRepositoryImpl;

    @BeforeEach
    void setUp() {
        userRepositoryImpl = new UserRepositoryImpl(userMongoRepository);
    }

    @Test
    void findByEmail_returnsOptional() {
        UserEntity entity = buildUserEntity();
        when(userMongoRepository.findByEmail("test@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = userRepositoryImpl.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void existsByEmail_returnsBoolean() {
        when(userMongoRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userRepositoryImpl.existsByEmail("test@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void save_savesAndReturnsUser() {
        UserEntity entity = buildUserEntity();
        when(userMongoRepository.save(any(UserEntity.class))).thenReturn(entity);

        User user = entity.toModel();
        User result = userRepositoryImpl.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("u-1");
        verify(userMongoRepository).save(any(UserEntity.class));
    }

    private UserEntity buildUserEntity() {
        return UserEntity.builder()
                .id("u-1")
                .name("Name")
                .email("test@example.com")
                .password("pass")
                .avatarUrl("url")
                .fcmToken("fcm")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
