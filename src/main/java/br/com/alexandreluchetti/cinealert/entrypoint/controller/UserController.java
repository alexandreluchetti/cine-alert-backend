package br.com.alexandreluchetti.cinealert.entrypoint.controller;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.usecase.UserUseCase;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.user.UpdateUserRequestDto;
import br.com.alexandreluchetti.cinealert.entrypoint.dto.user.UserResponseDto;
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
    public ResponseEntity<UserResponseDto> getMe(Authentication auth) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                UserResponseDto.fromModel(userUseCase.getProfile(user))
        );
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponseDto> updateMe(Authentication auth, @Valid @RequestBody UpdateUserRequestDto request) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                UserResponseDto.fromModel(userUseCase.updateProfile(user, request.toModel()))
        );
    }

    @PutMapping("/me/avatar")
    @Operation(summary = "Update avatar URL")
    public ResponseEntity<UserResponseDto> updateAvatar(Authentication auth, @RequestBody Map<String, String> body) {
        User user = userUseCase.getAuthenticatedUser(auth);
        return ResponseEntity.ok(
                UserResponseDto.fromModel(userUseCase.updateAvatar(user, body.get("avatarUrl")))
        );
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate account")
    public ResponseEntity<Map<String, String>> deleteMe(Authentication auth) {
        User user = userUseCase.getAuthenticatedUser(auth);
        userUseCase.deleteAccount(user);
        return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
    }
}
