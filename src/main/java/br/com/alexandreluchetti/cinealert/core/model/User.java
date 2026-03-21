package br.com.alexandreluchetti.cinealert.core.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    private Long id;

    private String name;

    private String email;

    private String password;

    private String avatarUrl;

    private String fcmToken;

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
