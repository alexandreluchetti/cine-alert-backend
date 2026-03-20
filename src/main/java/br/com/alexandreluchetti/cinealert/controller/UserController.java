package br.com.alexandreluchetti.cinealert.controller;

import br.com.alexandreluchetti.cinealert.dto.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.dto.user.UserResponse;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserUseCaseImpl userUseCaseImpl;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        User user = userUseCaseImpl.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCaseImpl.getProfile(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateMe(Authentication auth, @Valid @RequestBody UpdateUserRequest request) {
        User user = userUseCaseImpl.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCaseImpl.updateProfile(user, request));
    }

    @PutMapping("/me/avatar")
    @Operation(summary = "Update avatar URL")
    public ResponseEntity<UserResponse> updateAvatar(Authentication auth, @RequestBody Map<String, String> body) {
        User user = userUseCaseImpl.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCaseImpl.updateAvatar(user, body.get("avatarUrl")));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate account")
    public ResponseEntity<Map<String, String>> deleteMe(Authentication auth) {
        User user = userUseCaseImpl.getAuthenticatedUser(auth);
        userUseCaseImpl.deleteAccount(user);
        return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
    }
}
