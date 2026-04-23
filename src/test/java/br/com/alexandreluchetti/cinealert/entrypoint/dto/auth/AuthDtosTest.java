package br.com.alexandreluchetti.cinealert.entrypoint.dto.auth;

import br.com.alexandreluchetti.cinealert.core.model.auth.AuthResponse;
import br.com.alexandreluchetti.cinealert.core.model.auth.LoginRequest;
import br.com.alexandreluchetti.cinealert.core.model.auth.RefreshRequest;
import br.com.alexandreluchetti.cinealert.core.model.auth.RegisterRequest;
import br.com.alexandreluchetti.cinealert.core.model.auth.UserInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthDtosTest {

    @Test
    void authResponseDto_fromModel() {
        UserInfo user = new UserInfo("1", "Name", "test@example.com", "url");
        AuthResponse model = AuthResponse.of("access", "refresh", 3600, user);

        AuthResponseDto dto = AuthResponseDto.fromModel(model);

        assertThat(dto.accessToken()).isEqualTo("access");
        assertThat(dto.user().id()).isEqualTo("1");
    }

    @Test
    void loginRequestDto_toModel() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "pass");
        LoginRequest model = dto.toModel();
        assertThat(model.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void refreshRequestDto_toModel() {
        RefreshRequestDto dto = new RefreshRequestDto("refresh");
        RefreshRequest model = dto.toModel();
        assertThat(model.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void registerRequestDto_toModel() {
        RegisterRequestDto dto = new RegisterRequestDto("Name", "test@example.com", "pass");
        RegisterRequest model = dto.toModel();
        assertThat(model.getName()).isEqualTo("Name");
    }
}
