package com.kriterion.security.util;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password security utilities for hardened authentication.
 * Prevents common password attack patterns.
 */
public class PasswordSecurityUtil {

    private PasswordSecurityUtil() {
    }

    /**
     * Perform timing-safe password verification.
     * Always performs full verification even if password is obviously wrong,
     * to prevent timing attacks that could leak password length/patterns.
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword, PasswordEncoder encoder) {
        if (rawPassword == null || encodedPassword == null) {
            // Still perform dummy encode to consume same time
            encoder.encode("dummy-password-for-timing");
            return false;
        }

        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Check if password meets minimum requirements.
     * Validates length and character diversity.
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Generic error message to avoid leaking auth details.
     */
    public static String getGenericAuthError() {
        return "Invalid email or password";
    }
}
