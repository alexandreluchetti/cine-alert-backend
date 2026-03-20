package br.com.alexandreluchetti.cinealert.controller;

import br.com.alexandreluchetti.cinealert.dto.notification.FcmTokenRequest;
import br.com.alexandreluchetti.cinealert.model.User;
import br.com.alexandreluchetti.cinealert.core.usecase.impl.UserUseCaseImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Manage FCM device tokens")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final UserUseCaseImpl userUseCaseImpl;

    @PostMapping("/token")
    @Operation(summary = "Register or update FCM device token")
    public ResponseEntity<Map<String, String>> registerToken(
            Authentication auth,
            @Valid @RequestBody FcmTokenRequest request) {
        User user = userUseCaseImpl.getAuthenticatedUser(auth);
        userUseCaseImpl.updateFcmToken(user, request.fcmToken());
        return ResponseEntity.ok(Map.of("message", "FCM token registered successfully"));
    }
}
