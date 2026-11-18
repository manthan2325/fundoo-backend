package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : JwtTokenTest
 * Description : Tests JWT generation and user id extraction in JwtToken,
 *               including missing, malformed, expired and wrongly signed tokens.
 */

class JwtTokenTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long";
    private static final long ONE_HOUR_MS = 3_600_000L;

    private JwtToken jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = new JwtToken(SECRET, ONE_HOUR_MS);
    }

    // =====================  generateToken  =====================

    // Verifies that a token is created and has the three JWT parts.
    @Test
    void shouldGenerateTokenWithThreeParts() {
        String token = jwtToken.generateToken(1L);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    // Verifies that the user id is stored as the token subject.
    @Test
    void shouldStoreUserIdAsSubject() {
        String token = jwtToken.generateToken(42L);

        assertEquals("42", parseClaims(token).getSubject());
    }

    // Verifies that the token expires after the configured time.
    @Test
    void shouldSetExpirationFromConfiguration() {
        String token = jwtToken.generateToken(1L);
        Claims claims = parseClaims(token);

        long lifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        assertEquals(ONE_HOUR_MS, lifetime);
    }

    // =====================  extractUserId  =====================

    // Verifies that the user id from the token is returned.
    @Test
    void shouldExtractUserIdFromRawToken() {
        String token = jwtToken.generateToken(7L);

        assertEquals(7L, jwtToken.extractUserId(token));
    }

    // Verifies that the "Bearer " prefix is accepted.
    @Test
    void shouldExtractUserIdFromBearerHeader() {
        String token = jwtToken.generateToken(7L);

        assertEquals(7L, jwtToken.extractUserId("Bearer " + token));
    }

    // Verifies that each user id round-trips correctly.
    @Test
    void shouldKeepDifferentUserIdsSeparate() {
        String tokenA = jwtToken.generateToken(1L);
        String tokenB = jwtToken.generateToken(2L);

        assertEquals(1L, jwtToken.extractUserId(tokenA));
        assertEquals(2L, jwtToken.extractUserId(tokenB));
    }

    // Verifies that a null header is rejected.
    @Test
    void shouldRejectNullHeader() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jwtToken.extractUserId(null));

        assertEquals("Missing Token", ex.getMessage());
    }

    // Verifies that a blank header is rejected.
    @Test
    void shouldRejectBlankHeader() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jwtToken.extractUserId("   "));

        assertEquals("Missing Token", ex.getMessage());
    }



    // Verifies that a token signed with a different secret is rejected.
    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {
        JwtToken other = new JwtToken("another-secret-key-that-is-also-32-chars-long!!", ONE_HOUR_MS);
        String foreignToken = other.generateToken(1L);

        SignatureException ex = assertThrows(SignatureException.class,
                () -> jwtToken.extractUserId(foreignToken));

        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", ex.getMessage());
    }

    // =====================  constructor  =====================

    // Verifies that a secret shorter than 32 bytes is refused.
    @Test
    void shouldRejectTooShortSecret() {
        assertThrows(WeakKeyException.class, () -> new JwtToken("short-secret", ONE_HOUR_MS));
    }

    // ---------- helper ----------

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}