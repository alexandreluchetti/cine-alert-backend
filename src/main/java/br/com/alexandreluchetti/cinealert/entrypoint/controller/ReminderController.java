package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.usecase.ReminderUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderRequestDto;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderResponse;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderStatsResponse;
import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.core.model.enums.ReminderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Create and manage movie/series reminders")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {

    private final ReminderUseCase reminderUseCase;
    private final UserUseCase userUseCase;

    @GetMapping
    @Operation(summary = "List all reminders for the authenticated user")
    public ResponseEntity<List<ReminderResponse>> getAll(
            Authentication auth,
            @RequestParam(required = false) ReminderStatus status) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(reminderUseCase.getReminders(user, status));
    }

    @PostMapping
    @Operation(summary = "Create a new reminder")
    public ResponseEntity<ReminderResponse> create(
            Authentication auth,
            @Valid @RequestBody ReminderRequestDto request) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(reminderUseCase.create(user, request.toModel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific reminder")
    public ResponseEntity<ReminderResponse> getById(Authentication auth, @PathVariable Long id) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(reminderUseCase.getById(user, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reminder")
    public ResponseEntity<ReminderResponse> update(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody ReminderRequestDto request) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(reminderUseCase.update(user, id, request.toModel()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reminder")
    public ResponseEntity<Map<String, String>> delete(Authentication auth, @PathVariable Long id) {
        User user = userUseCase.getAuthenticatedUser(auth);
        reminderUseCase.cancel(user, id);
        return ResponseEntity.ok(Map.of("message", "Reminder cancelled successfully"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get reminder statistics for the authenticated user")
    public ResponseEntity<ReminderStatsResponse> getStats(Authentication auth) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(reminderUseCase.getStats(user));
    }
}
