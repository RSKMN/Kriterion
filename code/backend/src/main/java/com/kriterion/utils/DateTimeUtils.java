package com.kriterion.utils;

import java.time.LocalDateTime;

public final class DateTimeUtils {
    private DateTimeUtils() {
    }

    public static LocalDateTime nowUtc() {
        return LocalDateTime.now();
    }
}
