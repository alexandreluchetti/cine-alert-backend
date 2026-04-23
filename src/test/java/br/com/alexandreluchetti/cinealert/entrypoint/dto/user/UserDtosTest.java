package br.com.alexandreluchetti.cinealert.entrypoint.dto.user;

import br.com.alexandreluchetti.cinealert.core.model.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtosTest {

    @Test
    void updateUserRequestDto_toModel() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto("Name", "pass");
        UpdateUserRequest model = dto.toModel();
        assertThat(model.getName()).isEqualTo("Name");
    }

    @Test
    void userResponseDto_fromModel() {
        UserResponse model = new UserResponse("u-1", "Name", "e", "url", 10, 5);
        UserResponseDto dto = UserResponseDto.fromModel(model);
        assertThat(dto.id()).isEqualTo("u-1");
    }
}
