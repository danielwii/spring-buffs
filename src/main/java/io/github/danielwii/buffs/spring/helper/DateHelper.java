package io.github.danielwii.buffs.spring.helper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public enum DateHelper {
    ;

    public static final ZoneOffset OFFSET_UTC = ZoneOffset.UTC;

    public static OffsetDateTime now() {
        return OffsetDateTime.now(OFFSET_UTC);
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime dateTime) {
        return OffsetDateTime.of(dateTime, OFFSET_UTC);
    }

    public static OffsetDateTime toOffsetDateTime(Date date) {
        return OffsetDateTime.ofInstant(date.toInstant(), OFFSET_UTC);
    }

}
