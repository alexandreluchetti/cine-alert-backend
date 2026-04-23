package br.com.alexandreluchetti.cinealert.configuration.shared;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilImplTest {

    private JwtUtilImpl jwtUtil;
    private final String secret = "1234567890123456789012345678901234567890123456789012345678901234";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtilImpl();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtil, "accessExpiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 86400000L); // 24 hours
    }

    @Test
    void generateAccessToken_isValidAndReturnsCorrectClaims() {
        String token = jwtUtil.generateAccessToken("test@example.com", "user-1");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@example.com");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo("user-1");
        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
    }

    @Test
    void generateRefreshToken_isRefreshToken() {
        String token = jwtUtil.generateRefreshToken("test@example.com", "user-1");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isTrue();
    }

    @Test
    void isTokenValid_tamperedToken_returnsFalse() {
        String token = jwtUtil.generateAccessToken("test@example.com", "user-1");
        String tampered = token + "a";

        assertThat(jwtUtil.isTokenValid(tampered)).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Create an expired token manually
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();

        assertThat(jwtUtil.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void isRefreshToken_missingTypeClaim_returnsFalse() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        String token = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact(); // No "type" claim

        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
    }

    @Test
    void extractUserId_missingUserIdClaim_returnsNull() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        String token = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();

        assertThat(jwtUtil.extractUserId(token)).isNull();
    }
}
