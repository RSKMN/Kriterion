package com.kriterion.security.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for password security utilities.
 */
public class PasswordSecurityUtilTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Test
    void verifyPasswordSucceedsWithCorrectPassword() {
        String rawPassword = "SecurePass123";
        String encodedPassword = encoder.encode(rawPassword);

        boolean result = PasswordSecurityUtil.verifyPassword(rawPassword, encodedPassword, encoder);
        assertTrue(result);
    }

    @Test
    void verifyPasswordFailsWithWrongPassword() {
        String rawPassword = "SecurePass123";
        String wrongPassword = "WrongPass456";
        String encodedPassword = encoder.encode(rawPassword);

        boolean result = PasswordSecurityUtil.verifyPassword(wrongPassword, encodedPassword, encoder);
        assertFalse(result);
    }

    @Test
    void verifyPasswordFailsWithNullRawPassword() {
        String encodedPassword = encoder.encode("SomePassword123");

        boolean result = PasswordSecurityUtil.verifyPassword(null, encodedPassword, encoder);
        assertFalse(result);
    }

    @Test
    void isPasswordStrongValidatesLength() {
        assertFalse(PasswordSecurityUtil.isPasswordStrong("Short1")); // too short
        assertTrue(PasswordSecurityUtil.isPasswordStrong("SecurePass123")); // sufficient
    }

    @Test
    void isPasswordStrongValidatesRequiredCharacterTypes() {
        assertFalse(PasswordSecurityUtil.isPasswordStrong("password123")); // no uppercase
        assertFalse(PasswordSecurityUtil.isPasswordStrong("PASSWORD123")); // no lowercase
        assertFalse(PasswordSecurityUtil.isPasswordStrong("PasswordAbc")); // no digit
        assertTrue(PasswordSecurityUtil.isPasswordStrong("SecurePass123")); // all required
    }

    @Test
    void getGenericAuthErrorReturnsConsistentMessage() {
        String error = PasswordSecurityUtil.getGenericAuthError();
        assertNotNull(error);
        assertFalse(error.contains("email"));
        assertFalse(error.contains("password"));
    }

    @Test
    void bcryptStrength12IsUsed() {
        // BCrypt with strength 12 produces hashes starting with $2a$12$
        String encoded = encoder.encode("TestPassword123");
        assertTrue(encoded.startsWith("$2a$12$"), "BCrypt strength 12 should be used");
    }
}
