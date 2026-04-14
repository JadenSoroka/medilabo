package com.medilabo.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String SECRET = "TestSecretKeyForUnitTestingPurposesAtLeast256BitsLong!";
    private static final long EXPIRATION_MS = 3_600_000L;

    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_MS);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ── generateToken ─────────────────────────────────────────────────────────

    @Test
    void generateToken_ReturnsNonNullToken() {
        String token = jwtUtil.generateToken("alice");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_TokenContainsExpectedSubject() {
        String token = jwtUtil.generateToken("alice");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void generateToken_TokenIsValid() {
        String token = jwtUtil.generateToken("bob");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    // ── validateToken ─────────────────────────────────────────────────────────

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("user");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("not.a.valid.token")).isFalse();
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("garbage")).isFalse();
    }

    @Test
    void validateToken_TokenSignedWithDifferentKey_ReturnsFalse() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "OtherSecretKeyForTestingPurposesAtLeast256BitsLong!!".getBytes());
        String tokenWithOtherKey = Jwts.builder()
                .subject("user")
                .signWith(otherKey)
                .compact();
        assertThat(jwtUtil.validateToken(tokenWithOtherKey)).isFalse();
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = Jwts.builder()
                .subject("user")
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(secretKey)
                .compact();
        assertThat(jwtUtil.validateToken(expiredToken)).isFalse();
    }

    // ── extractUsername ───────────────────────────────────────────────────────

    @Test
    void extractUsername_ValidToken_ReturnsCorrectSubject() {
        String token = jwtUtil.generateToken("charlie");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("charlie");
    }

    @Test
    void extractUsername_InvalidToken_ThrowsException() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("bad.token"))
                .isInstanceOf(Exception.class);
    }
}
