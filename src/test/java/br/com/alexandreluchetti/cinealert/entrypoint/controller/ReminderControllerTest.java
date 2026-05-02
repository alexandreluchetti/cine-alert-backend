package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.model.enums.Recurrence;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.core.model.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.usecase.ReminderUseCase;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReminderController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReminderUseCase reminderUseCase;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private JwtUtilImpl jwtUtilImpl;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getAll_returns200() throws Exception {
        ReminderResponse rr = new ReminderResponse("r-1", buildContentResponse(), LocalDateTime.now(),
                "America/Sao_Paulo", Recurrence.ONCE, "Msg", ReminderStatus.PENDING, LocalDateTime.now());

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(reminderUseCase.getReminders(any(), any())).thenReturn(List.of(rr));

        mockMvc.perform(get("/api/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("r-1"));
    }

    @Test
    void create_returns201() throws Exception {
        ReminderResponse rr = new ReminderResponse("r-1", buildContentResponse(), LocalDateTime.now(),
                "America/Sao_Paulo", Recurrence.ONCE, "Msg", ReminderStatus.PENDING, LocalDateTime.now());

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(reminderUseCase.create(any(), any())).thenReturn(rr);

        String body = """
                {
                    "contentId": "c-1",
                    "scheduledAt": "2030-01-01T12:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("r-1"));
    }

    @Test
    void getById_returns200() throws Exception {
        ReminderResponse rr = new ReminderResponse("r-1", buildContentResponse(), LocalDateTime.now(),
                "America/Sao_Paulo", Recurrence.ONCE, "Msg", ReminderStatus.PENDING, LocalDateTime.now());

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(reminderUseCase.getById(any(), eq("r-1"))).thenReturn(rr);

        mockMvc.perform(get("/api/reminders/r-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r-1"));
    }

    @Test
    void update_returns200() throws Exception {
        ReminderResponse rr = new ReminderResponse("r-1", buildContentResponse(), LocalDateTime.now(),
                "America/Sao_Paulo", Recurrence.ONCE, "Updated Msg", ReminderStatus.PENDING, LocalDateTime.now());

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(reminderUseCase.update(any(), eq("r-1"), any())).thenReturn(rr);

        String body = """
                {
                    "contentId": "c-1",
                    "scheduledAt": "2030-01-01T12:00:00Z",
                    "message": "Updated Msg"
                }
                """;

        mockMvc.perform(put("/api/reminders/r-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated Msg"));
    }

    @Test
    void delete_returns200() throws Exception {
        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        doNothing().when(reminderUseCase).cancel(any(), eq("r-1"));

        mockMvc.perform(delete("/api/reminders/r-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getStats_returns200() throws Exception {
        ReminderStatsResponse stats = new ReminderStatsResponse(10, 5, 4, 1);

        when(userUseCase.getAuthenticatedUser(any())).thenReturn(null);
        when(reminderUseCase.getStats(any())).thenReturn(stats);

        mockMvc.perform(get("/api/reminders/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.pending").value(5));
    }

    private ContentResponse buildContentResponse() {
        return new ContentResponse("c-1", "tt123", "Title", ContentType.MOVIE, "url",
                2020, new BigDecimal("8.0"), Collections.emptyList(), "Syn", "Trailer", 120);
    }
}
