package com.medilabo.diabetesassessment.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "TestSecretKeyForUnitTestingPurposesAtLeast256BitsLong!";

    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .signWith(secretKey)
                .compact();
    }

    // ── validateToken ─────────────────────────────────────────────────────────

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = generateToken("testUser");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void validateToken_TokenSignedWithDifferentKey_ReturnsFalse() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "DifferentSecretKeyForTestingPurposesAtLeast256BitsLong!".getBytes());
        String tokenWithOtherKey = Jwts.builder().subject("user").signWith(otherKey).compact();
        assertThat(jwtUtil.validateToken(tokenWithOtherKey)).isFalse();
    }

    // ── extractUsername ───────────────────────────────────────────────────────

    @Test
    void extractUsername_ValidToken_ReturnsCorrectUsername() {
        String token = generateToken("alice");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void extractUsername_InvalidToken_ReturnsNull() {
        assertThat(jwtUtil.extractUsername("invalid.token.value")).isNull();
    }

    @Test
    void extractUsername_MalformedToken_ReturnsNull() {
        assertThat(jwtUtil.extractUsername("not-a-jwt")).isNull();
    }
}
