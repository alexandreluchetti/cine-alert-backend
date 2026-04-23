package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.user.UserResponse;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private JwtUtilImpl jwtUtilImpl;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getMe_returns200() throws Exception {
        UserResponse response = new UserResponse("u-1", "João", "joao@example.com", "url", 10, 5);

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(userUseCase.getProfile(any())).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u-1"));
    }

    @Test
    void updateMe_returns200() throws Exception {
        UserResponse response = new UserResponse("u-1", "João Atualizado", "joao@example.com", "url", 10, 5);

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(userUseCase.updateProfile(any(), any())).thenReturn(response);

        String body = """
                {
                    "name": "João Atualizado"
                }
                """;

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Atualizado"));
    }

    @Test
    void updateAvatar_returns200() throws Exception {
        UserResponse response = new UserResponse("u-1", "João", "joao@example.com", "new-url", 10, 5);

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(userUseCase.updateAvatar(any(), eq("new-url"))).thenReturn(response);

        String body = """
                {
                    "avatarUrl": "new-url"
                }
                """;

        mockMvc.perform(put("/api/users/me/avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("new-url"));
    }

    @Test
    void deleteMe_returns200() throws Exception {
        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        doNothing().when(userUseCase).deleteAccount(any());

        mockMvc.perform(delete("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateFcmToken_returns200() throws Exception {
        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        doNothing().when(userUseCase).updateFcmToken(any(), eq("new-token"));

        String body = """
                {
                    "token": "new-token"
                }
                """;

        mockMvc.perform(put("/api/users/me/fcm-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
