package com.kriterion.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility for extracting authenticated user information from SecurityContext.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {

    /**
     * Extract the authenticated user ID (principal) from SecurityContext.
     * Returns the principal as a string which is the userId from JWT.
     */
    public static String getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            return auth.getPrincipal().toString();
        }
        return null;
    }

    /**
     * Check if current user is authenticated.
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * Get authenticated user ID as Long.
     */
    public static Long getAuthenticatedUserIdAsLong() {
        String userId = getAuthenticatedUserId();
        try {
            return userId != null ? Long.parseLong(userId) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
