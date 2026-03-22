package br.com.alexandreluchetti.cinealert.core.usecase;

public interface JwtUtil {

    String generateAccessToken(String email, String userId);

    String generateRefreshToken(String email, String userId);

    String extractEmail(String token);

    String extractUserId(String token);

    boolean isTokenValid(String token);

    boolean isRefreshToken(String token);
}
