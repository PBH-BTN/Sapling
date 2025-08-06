package com.ghostchu.tracker.sapling.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class TimeUtil {
    public static OffsetDateTime toOffsetDateTime(long time) {
        OffsetDateTime time1 = OffsetDateTime.now();
        long timeStamp1 = time1.getLong(ChronoField.INSTANT_SECONDS);
        LocalDateTime localDateTime1 = Instant.ofEpochSecond(timeStamp1).atOffset(ZoneOffset.UTC).toLocalDateTime();
        return OffsetDateTime.of(localDateTime1, ZoneOffset.UTC);
    }
}
