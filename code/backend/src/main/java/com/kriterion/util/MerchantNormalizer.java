package com.kriterion.util;

import lombok.experimental.UtilityClass;
import java.util.regex.Pattern;

@UtilityClass
public class MerchantNormalizer {

    private static final Pattern UPI_CLEANUP = Pattern.compile("(?i)(UPI|SETTLEMENT|REFUND|TRANSFER)/|/\\d+");
    private static final Pattern SYMBOL_CLEANUP = Pattern.compile("[^a-zA-Z0-9\\s]");
    private static final Pattern WHITESPACE_CLEANUP = Pattern.compile("\\s+");

    /**
     * Normalizes a merchant name string for better rule matching.
     * Example: "UPI/SWIGGY LIMITED/12345/BLR" -> "swiggy limited"
     */
    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        // 1. Convert to lowercase
        String normalized = raw.toLowerCase().trim();

        // 2. Remove common UPI/banking prefixes/suffixes
        normalized = UPI_CLEANUP.matcher(normalized).replaceAll("");

        // 3. Remove symbols and special characters
        normalized = SYMBOL_CLEANUP.matcher(normalized).replaceAll(" ");

        // 4. Collapse multiple spaces
        normalized = WHITESPACE_CLEANUP.matcher(normalized).replaceAll(" ").trim();

        return normalized;
    }
}
