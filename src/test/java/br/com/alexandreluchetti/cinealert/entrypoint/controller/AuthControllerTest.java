package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.auth.AuthResponse;
import br.com.alexandreluchetti.cinealert.core.model.auth.UserInfo;
import br.com.alexandreluchetti.cinealert.core.usecase.AuthUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alexandreluchetti.cinealert.configuration.shared.JwtUtilImpl;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller tests
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUseCase authUseCase;

    @MockBean
    private JwtUtilImpl jwtUtilImpl;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_success_returns201() throws Exception {
        AuthResponse response = new AuthResponse("access", "refresh", "Bearer", 3600,
                new UserInfo("1", "Test", "test@example.com", null));

        when(authUseCase.register(any())).thenReturn(response);

        String body = """
                {
                    "name": "Test",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void register_invalidRequest_returns400() throws Exception {
        String body = """
                {
                    "name": "",
                    "email": "invalid-email",
                    "password": "pass"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_success_returns200() throws Exception {
        AuthResponse response = new AuthResponse("access", "refresh", "Bearer", 3600,
                new UserInfo("1", "Test", "test@example.com", null));

        when(authUseCase.login(any())).thenReturn(response);

        String body = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"));
    }

    @Test
    void refresh_success_returns200() throws Exception {
        AuthResponse response = new AuthResponse("new-access", "new-refresh", "Bearer", 3600,
                new UserInfo("1", "Test", "test@example.com", null));

        when(authUseCase.refresh(any())).thenReturn(response);

        String body = """
                {
                    "refreshToken": "valid-refresh-token"
                }
                """;

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    void logout_success_returns200() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void forgotPassword_success_returns200() throws Exception {
        doNothing().when(authUseCase).forgotPassword(any());

        String body = """
                {
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
