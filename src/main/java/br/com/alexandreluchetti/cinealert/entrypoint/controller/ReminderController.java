package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.usecase.ReminderUseCase;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderRequestDto;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderResponseDto;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.reminder.ReminderStatsResponseDto;
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
    public ResponseEntity<List<ReminderResponseDto>> getAll(
            Authentication auth,
            @RequestParam(required = false) ReminderStatus status) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                reminderUseCase.getReminders(userEntity, status).stream().map(ReminderResponseDto::fromModel).toList()
        );
    }

    @PostMapping
    @Operation(summary = "Create a new reminder")
    public ResponseEntity<ReminderResponseDto> create(
            Authentication auth,
            @Valid @RequestBody ReminderRequestDto request) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ReminderResponseDto.fromModel(reminderUseCase.create(userEntity, request.toModel()))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific reminder")
    public ResponseEntity<ReminderResponseDto> getById(Authentication auth, @PathVariable String id) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                ReminderResponseDto.fromModel(reminderUseCase.getById(userEntity, id))
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reminder")
    public ResponseEntity<ReminderResponseDto> update(
            Authentication auth,
            @PathVariable String id,
            @Valid @RequestBody ReminderRequestDto request) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                ReminderResponseDto.fromModel(reminderUseCase.update(userEntity, id, request.toModel()))
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reminder")
    public ResponseEntity<Map<String, String>> delete(Authentication auth, @PathVariable String id) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        reminderUseCase.cancel(userEntity, id);
        return ResponseEntity.ok(Map.of("message", "Reminder cancelled successfully"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get reminder statistics for the authenticated user")
    public ResponseEntity<ReminderStatsResponseDto> getStats(Authentication auth) {
        User userEntity = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                ReminderStatsResponseDto.fromModel(reminderUseCase.getStats(userEntity))
        );
    }
}
