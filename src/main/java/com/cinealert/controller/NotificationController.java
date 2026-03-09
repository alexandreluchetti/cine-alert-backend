package com.cinealert.controller;

import com.cinealert.dto.notification.FcmTokenRequest;
import com.cinealert.model.User;
import com.cinealert.service.UserService;
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

    private final UserService userService;

    @PostMapping("/token")
    @Operation(summary = "Register or update FCM device token")
    public ResponseEntity<Map<String, String>> registerToken(
            Authentication auth,
            @Valid @RequestBody FcmTokenRequest request) {
        User user = userService.getAuthenticatedUser(auth);
        userService.updateFcmToken(user, request.fcmToken());
        return ResponseEntity.ok(Map.of("message", "FCM token registered successfully"));
    }
}
