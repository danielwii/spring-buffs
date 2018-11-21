package io.github.danielwii.buffs.spring.mapper;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class DateFieldMapper {

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null
            ? Timestamp.from(offsetDateTime.toInstant())
            : null;
    }

}
