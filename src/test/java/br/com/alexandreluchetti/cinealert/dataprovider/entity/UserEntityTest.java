package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void fromModel_toModel_roundTrip() {
        LocalDateTime now = LocalDateTime.now();
        User model = new User("u-1", "João", "joao@example.com", "pass", "url", "fcm", true, now, now);

        UserEntity entity = UserEntity.fromModel(model);

        assertThat(entity.getId()).isEqualTo("u-1");
        assertThat(entity.getEmail()).isEqualTo("joao@example.com");

        User backToModel = entity.toModel();

        assertThat(backToModel.getId()).isEqualTo("u-1");
        assertThat(backToModel.getEmail()).isEqualTo("joao@example.com");
        assertThat(backToModel.getName()).isEqualTo("João");
        assertThat(backToModel.isActive()).isTrue();
    }
}
