package com.oilcommerce.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long accessExpiration = 900000; // 15 mins
    private final long refreshExpiration = 604800000; // 7 days

    @BeforeEach
    @SuppressWarnings("unused")
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiry", accessExpiration);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiry", refreshExpiration);
        ReflectionTestUtils.setField(jwtTokenProvider, "issuer", "oil-commerce-api");
    }

    @Test
    void testGenerateTokenAndValidate() {
        java.util.UUID userId = java.util.UUID.randomUUID();
        String email = "test@oilcommerce.com";
        String role = "CUSTOMER";
        String token = jwtTokenProvider.generateAccessToken(userId, email, role);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId.toString(), jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
        assertEquals(role, jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    void testGenerateRefreshTokenAndValidate() {
        java.util.UUID userId = java.util.UUID.randomUUID();
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals(userId.toString(), jwtTokenProvider.getUserIdFromToken(refreshToken));
    }
}
