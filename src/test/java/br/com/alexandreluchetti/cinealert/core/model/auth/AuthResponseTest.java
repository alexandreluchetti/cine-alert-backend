package br.com.alexandreluchetti.cinealert.core.model.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void of_createsAuthResponseWithBearerType() {
        UserInfo user = new UserInfo("id-1", "Name", "test@example.com", "url");

        AuthResponse response = AuthResponse.of("access-token", "refresh-token", 3600, user);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600);
        assertThat(response.getUser()).isEqualTo(user);
    }
}
