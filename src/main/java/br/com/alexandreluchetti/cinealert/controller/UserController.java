package br.com.alexandreluchetti.cinealert.controller;

import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.core.dto.user.UpdateUserRequest;
import br.com.alexandreluchetti.cinealert.core.dto.user.UserResponse;
import br.com.alexandreluchetti.cinealert.core.model.User;
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

    private final UserUseCase userUseCase;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCase.getProfile(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateMe(Authentication auth, @Valid @RequestBody UpdateUserRequest request) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCase.updateProfile(user, request));
    }

    @PutMapping("/me/avatar")
    @Operation(summary = "Update avatar URL")
    public ResponseEntity<UserResponse> updateAvatar(Authentication auth, @RequestBody Map<String, String> body) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(userUseCase.updateAvatar(user, body.get("avatarUrl")));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate account")
    public ResponseEntity<Map<String, String>> deleteMe(Authentication auth) {
        User user = userUseCase.getAuthenticatedUser(auth);
        userUseCase.deleteAccount(user);
        return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
    }
}
