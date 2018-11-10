package io.github.danielwii.wyf.mapper;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class DateMapper {

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null
            ? Timestamp.from(offsetDateTime.toInstant())
            : null;
    }

}
