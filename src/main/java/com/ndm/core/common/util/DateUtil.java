package com.ndm.core.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static <T> T now(Class<T> clazz) {
        LocalDateTime now = LocalDateTime.now();

        if (clazz.equals(Date.class)) {
            Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
            return (T) Date.from(instant);
        }
        else if (clazz.equals(String.class)) {
            return (T) now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        return (T) now;
    }
}
