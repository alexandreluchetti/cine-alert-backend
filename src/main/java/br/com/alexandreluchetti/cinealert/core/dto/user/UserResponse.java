package br.com.alexandreluchetti.cinealert.core.dto.user;

public record UserResponse(
    Long id,
    String name,
    String email,
    String avatarUrl,
    long totalReminders,
    long sentReminders
) {}
