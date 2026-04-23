package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private JwtUtilImpl jwtUtilImpl;

    @MockBean
    private UserRepository userRepository;

    @Test
    void registerToken_returns200() throws Exception {
        User user = new User("u-1", "Name", "email@test.com", "pass", "url", "old", true, null, null);
        when(userUseCase.getAuthenticatedUser(any())).thenReturn(user);
        doNothing().when(userUseCase).updateFcmToken(any(), eq("new-token"));

        String body = """
                {
                    "fcmToken": "new-token"
                }
                """;

        mockMvc.perform(post("/api/notifications/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("FCM token registered successfully"));
    }
}
